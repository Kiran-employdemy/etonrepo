(function () {
  const escapeAttr = window.App.global.utils.escapeAttr;
  const unescapeAttr = window.App.global.utils.unescapeAttr;

  let App = window.App || {};
  App.ActiveFilters = class ActiveFilters {
    /**
     * Initialize the ActiveFilters class
     * @function init
     * @param {object} container - A reference to the ActiveFilters container's DOM element so data attributes may be referenced
    */
    init(container) {
      const facetedNavigationHeader = document.querySelector('.faceted-navigation-header');

      this.container = container;
      this.activeFilterValues = this.container.dataset.activeFilterValues ? unescapeAttr(this.container.dataset.activeFilterValues) : [];
      this.clearFiltersText = this.container.dataset.clearFiltersText;
      /** if facetedNavigationHeader exists, use the activeFacetCount data attribute. If not, use the activeFilterValues length (used in the context of submittal builder)  */
      this.activeFilterCount = facetedNavigationHeader && typeof facetedNavigationHeader.dataset.activeFacetCount !== 'undefined'
        ? facetedNavigationHeader.dataset.activeFacetCount
        : this.activeFilterValues.length;
      this.redirectUrl = facetedNavigationHeader ? facetedNavigationHeader.dataset.redirectLink : null;

      this.render();
    }

    /**
     * @function markup
     * The current html representation of the component based upon the current properties.
     */
    markup() {
      return `
      <div class="${ this.activeFilterCount > 0 ? '' : 'hidden' }">
        <div class="${ this.activeFilterCount > 0 && !this.showingOnlyPackage ? '' : 'hidden' }">
          ${ this.activeFilters.map(({name, value, title}) => `
            <div class="faceted-navigation-header__active-filter"
              data-name="${ escapeAttr(name) }"
              data-value="${ escapeAttr(value) }"
              data-title="${ escapeAttr(title) }"></div>
          `).join('') }
        </div>

        <a href="#"
          rel="nofollow"
          class="faceted-navigation__action-link faceted-navigation__action-link--clear-filters faceted-navigation__action-link--clear-filters--mobile ${ this.activeFilters.length > 0 ? '' : 'hidden' }">
            ${ this.clearFiltersText }
        </a>
      </div>
      `;
    }

    /**
     * @function
     * Renders the component based upon the current properties.
     */
    render() {
      this.container.innerHTML = this.markup();
      this.clearAllFiltersButton = this.container.querySelector('.faceted-navigation__action-link--clear-filters');
      this.container.dispatchEvent(new CustomEvent('active-filters-rendered'));

      this.addEventListeners();
    }

    /**
     * @function addEventListeners - Adds event listeners for the internal implementation
     * of this component.
     */
    addEventListeners() {
      [...this.container.querySelectorAll('.faceted-navigation-header__active-filter')]
      .forEach(activeFilterContainer =>
        new App.ActiveFilter(activeFilterContainer)
          .addEventListener('removed', ({detail: { name, value }}) => this.container.dispatchEvent(new CustomEvent('filterRemoved', { detail: { name: name, value: value }}))));


      this.clearAllFiltersButton.addEventListener('click', (e) => {
        e.preventDefault();
        /** if a redirect URL is defined, send the user along (this is used in non-submittal builder situations) */
        if (this.redirectUrl) {
          window.location = this.redirectUrl;
        }
        this.clearAllFilters();
      });
    }

    /**
     * @function activeFilters
     * The currently active filters provided in an array. This will never return null
     * and will return an empty array if there are no active filters.
     */
    get activeFilters() {
      return this.activeFilterValues ? this.activeFilterValues : [];
    }

    /**
     * @function activeFilters
     * This will set the currently active filters.
     */
    set activeFilters(activeFilters) {
      this.activeFilterValues = activeFilters;

      this.render();
    }

    /**
     * @function clearAllFilters
     * This only dispatches an event it does not actually do the clearing of filters.
     * This is meant to be used as a way to tell client code that all the filters should be cleared.
     */
    clearAllFilters() {
      this.container.dispatchEvent(new CustomEvent('clearAllFilters'));
    }

    /**
     * The active filter chips section
     * @constructor
     * @param {object} container - A reference to the ActiveFilters container's DOM element
    */
    constructor(container) {
      // The constructor should only contain the boiler plate code for finding or creating the reference.
      if (typeof container.dataset.ref === 'undefined') {
        this.ref = Math.random();
        App.ActiveFilters.refs[this.ref] = this;
        container.dataset.ref = this.ref;
        this.init(container);
      } else {
        // If this element has already been instantiated, use the existing reference.
        return App.ActiveFilters.refs[container.dataset.ref];
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

  App.ActiveFilters.refs = { };
})();
