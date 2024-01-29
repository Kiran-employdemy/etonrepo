(function () {
  const escapeAttr = window.App.global.utils.escapeAttr;
  const unescapeAttr = window.App.global.utils.unescapeAttr;

  let App = window.App || {};
  App.SubmittalHeader = class SubmittalHeader {
    /**
     * Initialize the SubmittalHeader class
     * @function init
     * @param {object} container - A reference to the SubmittalHeader container's DOM element so data attributes may be referenced
    */
    init(container) {
      this.container = container;
      this.title = this.container.dataset.title;
      this.resultsText = this.container.dataset.resultsText;
      this.resultCountValue = this.container.dataset.resultCount;
      this.activeFilterValues = this.container.dataset.activeFilterValues ? unescapeAttr(this.container.dataset.activeFilterValues) : [];
      this.showingOnlyPackage = this.container.dataset.showingOnlyPackage === 'true' ? true : false;
      this.itemsInPackage = this.container.dataset.itemsInPackage;
      this.packageFileSizeValue = this.container.dataset.packageFileSizeValue;
      this.maximumPackageFileSizeValue = this.container.dataset.maximumPackageFileSizeValue;
      this.documentsText = this.container.dataset.documentsText;
      this.ofText = this.container.dataset.ofText;
      this.clearAllFiltersText = this.container.dataset.clearAllFiltersText;
      this.clearFiltersText = this.container.dataset.clearFiltersText;
      this.submittalBuilderHelpLinkText = this.container.dataset.submittalBuilderHelpLinkText;
      this.submittalBuilderHelpLink = this.container.dataset.submittalBuilderHelpLink;
      this.render();
    }

    /**
     * @function markup
     * The current html representation of the component based upon the current properties.
     */
    markup() {
      return `
        <h3 class="faceted-navigation-header__results-count submittal-builder__results-count submittal-builder__results-count--results ${ this.showingOnlyPackage ? 'hidden' : '' }">
          ${ !this.showingOnlyPackage ? `${ this.resultCount } ${ this.resultsText }` : '' }
        </h3>
        <h3 class="faceted-navigation-header__results-count submittal-builder__results-count submittal-builder__results-count--documents ${ this.showingOnlyPackage ? '' : 'hidden' }">
          ${ this.itemsInPackage } ${ this.documentsText }
          <span class="submittal-builder__results-count__subtitle"><bdi>(${ this.packageFileSizeValue } ${ this.ofText } ${ this.maximumPackageFileSizeValue })</bdi></span>
        </h3>
        <a href="#" role="button" class="faceted-navigation-header__action-link faceted-navigation-header__action-link--clear-filters faceted-navigation-header__action-link--clear-filters--desktop ${ this.activeFilters.length > 0 ? '' : 'hidden' }">
            ${ this.clearAllFiltersText }
        </a>
        <a href="${ this.submittalBuilderHelpLink }" target="_blank" class="faceted-navigation-header__results-count submittal-builder__results-count submittal-builder__results-count--documents helpbutton pull-right">
        ${ this.submittalBuilderHelpLinkText }
    </a>
        <hr class="submittal-builder__hr">
        ${ this.activeFilters.length > 0 ? `
          <div class="faceted-navigation-header__active-filters" data-active-filter-values="${ escapeAttr(this.activeFilters) }"/>
        ` : '' }
      `;
    }

    /**
     * @function
     * Renders the component based upon the current properties.
     */
    render() {
      this.container.innerHTML = this.markup();
      this.resultsTitleElement = this.container.querySelector('.submittal-builder__results-count--results');
      this.documentsTitleElement = this.container.querySelector('.submittal-builder__results-count--documents');
      this.clearAllFiltersButtons = this.container.querySelectorAll('.faceted-navigation-header__action-link--clear-filters');
      this.resultCount = this.resultCountValue;

      this.activeFiltersContainer = this.container.querySelector('.faceted-navigation-header__active-filters');
      if (this.activeFiltersContainer) {
        this.activeFiltersComponent = new App.ActiveFilters(this.activeFiltersContainer);
      }

      this.addEventListeners();
    }

    /**
     * @function addEventListeners - Adds event listeners for the internal implementation
     * of this component.
     */
    addEventListeners() {
      this.clearAllFiltersButtons.forEach((clearAllFiltersButton) =>
        clearAllFiltersButton.addEventListener('click', (e) => {
          e.preventDefault();
          this.clearAllFilters();
        }));

      if (this.activeFiltersComponent) {
        this.activeFiltersComponent.addEventListener('clearAllFilters', () => this.clearAllFilters());

        this.activeFiltersComponent.addEventListener('filterRemoved', (e) =>
            this.container.dispatchEvent(new CustomEvent('filterRemoved', { detail: e.detail })));
      }
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
     * @function set resultCount
     * Sets resultCountValue to zero if no filters have been selected
     * Updates the titleElement.innerHTML
    */
    set resultCount(count) {
      this.resultCountValue = this.activeFilters.length > 0 ? count : 0;
      this.resultsTitleElement.innerHTML = `${ this.resultCount } ${ this.resultsText }`;
      this.documentsTitleElement.innerHTML = `${ this.itemsInPackage } ${ this.documentsText }
        <span class="submittal-builder__results-count__subtitle">(<bdi>${ this.packageFileSizeValue } ${ this.ofText } ${ this.maximumPackageFileSizeValue }</bdi>)&lrm;</span>`;
    }

    /**
     * @function get resultCount - Gets the current count of results as an integer.
     */
    get resultCount() {
      return this.resultCountValue ? this.resultCountValue : 0;
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
     * The active filter chips section of the submittal builder
     * @constructor
     * @param {object} container - A reference to the SubmittalHeader container's DOM element
    */
    constructor(container) {
      // The constructor should only contain the boiler plate code for finding or creating the reference.
      if (typeof container.dataset.ref === 'undefined') {
        this.ref = Math.random();
        App.SubmittalHeader.refs[this.ref] = this;
        container.dataset.ref = this.ref;
        this.init(container);
      } else {
        // If this element has already been instantiated, use the existing reference.
        return App.SubmittalHeader.refs[container.dataset.ref];
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

  App.SubmittalHeader.refs = { };
})();

