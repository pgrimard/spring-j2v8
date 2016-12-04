# J2V8 View Rendering

This project demonstrates using [J2V8](https://github.com/eclipsesource/J2V8) to render views with JavaScript on the server.

## Configuration
In its simplest form, configuration is as simple as defining a `V8ScriptTemplateViewResolver` and a
`V8ScriptTemplateConfigurer` while passing it the scripts required to render views.  The `V8ScriptTemplateConfigurer`
also provides a fluent API for setting additional config properties.

```java
@SpringBootApplication
public class SpringJ2V8Application {

    @Bean
    public V8ScriptTemplateViewResolver viewResolver() {
        return new V8ScriptTemplateViewResolver("/static/", ".html");
    }

    @Bean
    public V8ScriptTemplateConfigurer scriptTemplateConfigurer() {
        return new V8ScriptTemplateConfigurer("static/server.js");
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringJ2V8Application.class, args);
    }
}
```

## The Server Side JavaScript Render Function
When rendering the view, `V8ScriptTemplateView` passes 2 arguments to the render function, the `template` and the `model`.
The render function should return a string which will be written to the `HttpServletResponse` body.  Model attribute types
can be all the types supported by J2V8 as well as Java arrays, `Map` and `Iterable` which will be converted to `V8Array`
or `V8Object` where appropriate.  Nested arrays, `Map` and `Iterable` instances will also be converted.

```javascript
window.render = function render(template, model) {
  // merge the template with the model and return a string
  return template.replace('SOME_PLACEHOLDER', JSON.stringify(model.someValue));
};
```