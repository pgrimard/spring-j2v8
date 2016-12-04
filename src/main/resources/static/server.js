window.render = function render(template, model) {
  return template.replace('SERVER_RENDERED_HTML', JSON.stringify(model));
};