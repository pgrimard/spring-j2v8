# J2V8 View Rendering

This project demonstrates using [J2V8](https://github.com/eclipsesource/J2V8) to render views with JavaScript on the server.

## Configuration
Configuration is as simple as defining a `V8ScriptTemplateViewResolver` and a `V8ScriptTemplateConfigurer`.

```java
@SpringBootApplication
public class SpringJ2V8Application {

    @Bean
    public V8ScriptTemplateViewResolver viewResolver() {
        return new V8ScriptTemplateViewResolver("/static/", ".html");
    }

    @Bean
    public V8ScriptTemplateConfigurer v8ScriptTemplateConfigurer() {
        return new V8ScriptTemplateConfigurer("static/server.js");
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringJ2V8Application.class, args);
    }
}
```

## Writing the JavaScript Render Function
When rendering the view, `V8ScriptTemplateView` passes 2 arguments to the render function, the `template` and the `model`.
The render function should return a string which will be written to the `HttpServletResponse`.

```javascript
window.render = function render(template, model) {
  return template
    .replace('SERVER_RENDERED_HTML', JSON.stringify(model.req))
    .replace('SERVER_RENDERED_STATE', JSON.stringify(model.initialState));
};
```