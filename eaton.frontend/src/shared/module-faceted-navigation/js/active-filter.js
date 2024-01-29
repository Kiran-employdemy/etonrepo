(function () {
  const unescapeAttr = window.App.global.utils.unescapeAttr;

  let App = window.App || {};
  App.ActiveFilter = class ActiveFilter {
    /**
     * Initialize the ActiveFilter class
     * @function init
     * @param {object} container - A reference to the ActiveFilter container's DOM element so data attributes may be referenced
    */
    init(container) {
      this.container = container;
      this.name = unescapeAttr(this.container.dataset.name);
      this.value = unescapeAttr(this.container.dataset.value);
      this.title = unescapeAttr(this.container.dataset.title);

      this.render();
    }

    /**
     * @function markup
     * The current html representation of the component based upon the current properties.
     */
    markup() {
      return `
        <button class="faceted-navigation-header__filter-link"
          aria-label="Remove ${ this.title } filter"
          data-filter-name="${ this.name }"
          data-filter-value="${ this.value }">
          <span class="faceted-navigation-header__filter-label"><bdi>${ this.title }</bdi></span>
          <i class="icon icon-close" aria-hidden="true"></i>
        </button>
      `;
    }

    /**
     * @function
     * Renders the component based upon the current properties.
     */
    render() {
      this.container.innerHTML = this.markup();
      this.filterLink = this.container.querySelector('.faceted-navigation-header__filter-link');

      this.addEventListeners();
    }

    /**
     * @function addEventListeners - Adds event listeners for the internal implementation
     * of this component.
     */
    addEventListeners() {
      this.filterLink.addEventListener('click', () =>
        this.container.dispatchEvent(new CustomEvent('removed', { detail: { name: this.name, value: this.value }})));
    }

    /**
     * The active filter chip
     * @constructor
     * @param {object} container - A reference to the ActiveFilter container's DOM element
    */
    constructor(container) {
      // The constructor should only contain the boiler plate code for finding or creating the reference.
      if (typeof container.dataset.ref === 'undefined') {
        this.ref = Math.random();
        App.ActiveFilter.refs[this.ref] = this;
        container.dataset.ref = this.ref;
        this.init(container);
      } else {
        // If this element has already been instantiated, use the existing reference.
        return App.ActiveFilter.refs[container.dataset.ref];
      }
    }

    /**
     * @function addEventListener - A method to allow clients to add event listeners
     *  to this component. Calls the addEventListener method of this components containing element.
     */
    addEventListener() {
      return this.container.addEventListener.apply(this.container, arguments);
    }

    /**
     * @function removeEventListener - A method to allow clients to remove event listeners
     *  from this component. Calls the removeEventListener method of this components containing element.
     */
    removeEventListener() {
      return this.container.removeEventListener.apply(this.container, arguments);
    }
  };

  App.ActiveFilter.refs = { };
})();
