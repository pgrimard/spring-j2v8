package com.patrickgrimard.examples;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8Value;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Created on 2016-12-03
 *
 * @author Patrick
 */
public class V8ScriptTemplateView extends AbstractUrlBasedView {

    private static final String DEFAULT_CONTENT_TYPE = "text/html";

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private static final String DEFAULT_RESOURCE_LOADER_PATH = "classpath:";

    private static final String DEFAULT_RENDER_FUNCTION = "render";

    private String[] scripts;

    private String renderFunction;

    private Charset charset;

    private String[] resourceLoaderPaths;

    private ResourceLoader resourceLoader;

    public V8ScriptTemplateView() {
        setContentType(null);
    }

    public V8ScriptTemplateView(String url) {
        super(url);
        setContentType(null);
    }

    public void setScripts(String... scripts) {
        this.scripts = scripts;
    }

    public void setRenderFunction(String renderFunction) {
        this.renderFunction = renderFunction;
    }

    @Override
    public void setContentType(String contentType) {
        super.setContentType(contentType);
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public void setResourceLoaderPath(String resourceLoaderPath) {
        String[] paths = StringUtils.commaDelimitedListToStringArray(resourceLoaderPath);
        this.resourceLoaderPaths = new String[paths.length + 1];
        this.resourceLoaderPaths[0] = "";
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            if (!path.endsWith("/") && !path.endsWith(":")) {
                path = path + "/";
            }
            this.resourceLoaderPaths[i + 1] = path;
        }
    }

    @Override
    protected void initApplicationContext(ApplicationContext context) {
        super.initApplicationContext(context);

        V8ScriptTemplateConfig viewConfig = autodetectViewConfig();

        if (this.scripts == null && viewConfig.getScripts() != null) {
            this.scripts = viewConfig.getScripts();
        }
        if(this.renderFunction == null) {
            this.renderFunction = (viewConfig.getRenderFunction() != null ? viewConfig.getRenderFunction() : DEFAULT_RENDER_FUNCTION);
        }
        if (this.getContentType() == null) {
            setContentType(viewConfig.getContentType() != null ? viewConfig.getContentType() : DEFAULT_CONTENT_TYPE);
        }
        if (this.charset == null) {
            this.charset = (viewConfig.getCharset() != null ? viewConfig.getCharset() : DEFAULT_CHARSET);
        }
        if (this.resourceLoaderPaths == null) {
            String resourceLoaderPath = viewConfig.getResourceLoaderPath();
            setResourceLoaderPath(resourceLoaderPath == null ? DEFAULT_RESOURCE_LOADER_PATH : resourceLoaderPath);
        }
        if (this.resourceLoader == null) {
            this.resourceLoader = getApplicationContext();
        }
    }

    @Override
    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        super.prepareResponse(request, response);

        setResponseContentType(request, response);
        response.setCharacterEncoding(this.charset.name());
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        V8 v8 = V8.createV8Runtime("window");

        List<V8Value> scriptResults = Arrays.stream(this.scripts)
                .map(script -> {
                    try {
                        return v8.executeObjectScript(getResourceAsString(script));
                    } catch (IOException e) {
                        throw new IllegalStateException(String.format("Failed to execute script %s", script), e);
                    }
                })
                .collect(toList());

        String template = getResourceAsString(getUrl());
        V8Object modelAttributes = new V8Object(v8);
        model.forEach((k, v) -> modelAttributes.add(k, (String) v)); //TODO Values may not always be String values

        Object html = v8.executeJSFunction("render", template, modelAttributes);
        response.getWriter().write(String.valueOf(html));

        modelAttributes.release();
        scriptResults.forEach(V8Value::release);
        v8.release();
    }

    protected Resource getResource(String location) {
        for (String path : this.resourceLoaderPaths) {
            Resource resource = this.resourceLoader.getResource(path + location);
            if (resource.exists()) return resource;
        }

        throw new IllegalStateException(String.format("Resource %s not found", location));
    }

    protected String getResourceAsString(String path) throws IOException {
        Resource resource = getResource(path);

        if (resource != null && resource.exists()) {
            InputStreamReader reader = new InputStreamReader(resource.getInputStream(), this.charset);
            return FileCopyUtils.copyToString(reader);
        } else {
            throw new IllegalStateException(String.format("Resource %s not found", path));
        }
    }

    protected V8ScriptTemplateConfig autodetectViewConfig() throws BeansException {
        try {
            return BeanFactoryUtils.beanOfTypeIncludingAncestors(
                    getApplicationContext(), V8ScriptTemplateConfig.class, true, false);
        } catch (NoSuchBeanDefinitionException ex) {
            throw new ApplicationContextException("Expected a single V8ScriptTemplateConfig bean in the current " +
                    "Servlet web application context or the parent root context: V8ScriptTemplateConfigurer is " +
                    "the usual implementation. This bean may have any name.", ex);
        }
    }
}
