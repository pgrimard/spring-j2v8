/**
 * Server side render function that merges a template with a model.  Caution should be taken when replacing template
 * variables with model data to avoid script injection attacks.  Consider using serialize-javascript to avoid these
 * attacks {@link https://www.npmjs.com/package/serialize-javascript}.
 *
 * @param template The template that will be merged.
 * @param model The model data to merge with the template.
 * @returns {string} The merged template.
 */
window.render = function render(template, model) {
  return template
    .replace('SERVER_RENDERED_HTML', JSON.stringify(model.req))
    .replace('SERVER_RENDERED_STATE', JSON.stringify(model.initialState));
};