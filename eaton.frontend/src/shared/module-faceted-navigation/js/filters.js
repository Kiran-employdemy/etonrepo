(function () {
  const BODY_ELEMENT = document.querySelector('body');
  const escapeAttr = window.App.global.utils.escapeAttr;
  const unescapeAttr = window.App.global.utils.unescapeAttr;
  let App = window.App || {};
  App.Filters = class Filters {
    /**
     * Initialize the Filters class
     * @function init
     * @param {object} container - A reference to the Filters container's DOM element so data attributes may be referenced
    */
    init(container) {
      this.container = container;
      this.title = this.container.dataset.title;
      this.preventScrollTop = this.container.dataset.preventScrollTop === 'true';
      this.mobileDialogTitle = this.container.dataset.mobileDialogTitle;
      this.facetSearchDisabled = this.container.dataset.facetSearchDisabled === 'true';
      this.facetSearchLabel = this.container.dataset.facetSearchLabel;
      this.facetSearchPlaceholder = this.container.dataset.facetSearchPlaceholder;
      this.inText = this.container.dataset.inText;
      this.noSuggestionsText = this.container.dataset.noSuggestionsText;
      this.facetGroupSearchLabel = this.container.dataset.facetGroupSearchLabel;
      this.facetGroupSearchPlaceholder = this.container.dataset.facetGroupSearchPlaceholder;
      this.facetGroupSearchNoSuggestionsText = this.container.dataset.facetGroupSearchNoSuggestionsText;
      this.closeText = this.container.dataset.closeText;
      this.viewMoreText = this.container.dataset.viewMoreText;
      this.viewLessText = this.container.dataset.viewLessText;
      this.resultsPluralText = this.container.dataset.resultsPluralText;
      this.resultsSingularText = this.container.dataset.resultsSingularText;
      this.ofText = this.container.dataset.ofText;
      this.clearAllFiltersText = this.container.dataset.clearAllFiltersText;
      this.clearFiltersText = this.container.dataset.clearFiltersText;
      this.clearSelectionText = this.container.dataset.clearSelectionText;
      this.facetValueCount = this.container.dataset.facetValueCount;

      if (this.container.dataset.hideFromActive) {
        this.hideFromActive = unescapeAttr(this.container.dataset.hideFromActive);
      }

      if (this.container.dataset.filterList) {
        this.filterListValues = unescapeAttr(this.container.dataset.filterList);
      }

      if (this.container.dataset.propertyList) {
        this.propertyListValues = unescapeAttr(this.container.dataset.propertyList);
      }

      this.selectedFilters = {};
      if (this.filterListValues) {
        this.filterListValues.forEach(filter => {
          filter.values.forEach(value => {
            if (value.active) {
              this.selectedFilters[value.name] = value;
            }
          });
        });
      }

      this.render();
    }

    /**
     * @function markup
     * The current html representation of the component based upon the current properties.
     */
    markup() {
      return `
        <div class="eaton-form faceted-navigation faceted-navigation__filters">
          <h3 class="faceted-navigation__title">${ this.title }</h3>
          ${ !this.facetSearchDisabled ? `
            <div class="global-filter-search__container"
              data-global-facet-search-label="${ this.facetSearchLabel }"
              data-global-facet-search-placeholder="${ this.facetSearchPlaceholder }"
              data-global-facet-search-in-text="${ this.inText }"
              data-global-facet-search-no-suggestions-text="${ this.noSuggestionsText }"
              data-global-facet-search-filter-components="${ escapeAttr(this.filterComponents) }">
            </div>
          ` : '' }
          <div class="mobile-header">
            <span>${ this.mobileDialogTitle }</span>
            <button data-toggle-modal-facet
              aria-label="${ this.closeText }"
              class="close-facets-mobile button--reset">
              <span class="sr-only">${ this.closeText } ${ this.title }</span>
              <i class="icon icon-close" aria-hidden="true"></i>
            </button>
          </div>
          <hr class="faceted-navigation__hr">
          <div class="cross-reference__active-filters bullseye-map-active__filters faceted-navigation__active-filters__container faceted-navigation__active-filters__container--mobile ${ this.activeFilterList ? '' : 'hidden' }"
            data-active-filter-values="${ escapeAttr(this.activeFilterList) }"
            data-result-count="${ this.resultCount }"
            data-results-text="${ this.resultsText }"
            data-of-text="${ this.ofText }"
            data-clear-all-filters-text="${ this.clearAllFiltersText }"
            data-clear-filters-text="${ this.clearFiltersText }"
            data-clear-selection-text="${ this.clearSelectionText }">
          </div>
          ${ this.filterList.map(filter => `
            <div class="faceted-navigation__filter-container"
              data-values="${ escapeAttr(filter) }"
              data-clear-selection-text="${ this.clearSelectionText }"
              data-view-more-text="${ this.viewMoreText }"
              data-facet-value-count="${ this.facetValueCount }"
              data-view-less-text="${ this.viewLessText }"
              data-facet-group-search-label="${ this.facetGroupSearchLabel }"
              data-facet-group-search-placeholder="${ this.facetGroupSearchPlaceholder }"
              data-facet-group-search-no-suggestions-text="${ this.facetGroupSearchNoSuggestionsText }"></div>
          `).join('') }
        </div>
      `;
    }
    /**
     * @function filterPageScroll
     * scrolls the page on load to top of results header if filter is selected
     */
    filterPageScroll(filterContainerRef) {
      if (document.querySelector('.product-tabs-v2') !== null) {
        return;
      }
      const headerElem = document.querySelector('.header-primary-nav');
      const HEADER_HEIGHT_STICKY = headerElem ? headerElem.offsetHeight : 0;
      const FILTER_MAIN_CONTAINER = filterContainerRef.closest('.container').parentElement.parentElement;
      const PSEUDO_DIVIDER_HEIGHT = 4;
      let resultPositionTop = FILTER_MAIN_CONTAINER.offsetTop;
      let NON_STICKY_HEADER_MOBILE = false;
      let HEADER_ACTUAL_HEIGHT;
      // check device as mobile/tab dont have sticky header
      if (window.innerWidth <= 990) {
        NON_STICKY_HEADER_MOBILE = true;
      }
      HEADER_ACTUAL_HEIGHT = NON_STICKY_HEADER_MOBILE ? 0 : HEADER_HEIGHT_STICKY - PSEUDO_DIVIDER_HEIGHT;
      // element structure contains extra intro container inside the main filter container for submittal builder
      if (FILTER_MAIN_CONTAINER.querySelector('.submittal-builder__intro__container') !== null) {
        resultPositionTop += FILTER_MAIN_CONTAINER.querySelector('.submittal-builder__intro__container').closest('.container').offsetHeight;
      }
      if (document.getElementsByClassName('otCenterRounded').length > 0) {
        const COOKIES_ACCEPTANCE_HEIGHT = document.getElementsByClassName('otCenterRounded')[0].offsetHeight;
        // remove cookie height and header height
        resultPositionTop -= (HEADER_ACTUAL_HEIGHT + COOKIES_ACCEPTANCE_HEIGHT);
      } else {
        // Remove Header height from the scroll position
        resultPositionTop -= HEADER_ACTUAL_HEIGHT;
      }
      if (!this.preventScrollTop) {
        setTimeout(() => {
          // scroll if any of the filter is selected
          if ((document.querySelectorAll('.faceted-navigation__filter-container input[type="checkbox"]:checked').length > 0) || (document.querySelectorAll('.faceted-navigation__filter-container input[type="radio"]:checked').length > 0) || (document.querySelectorAll('.faceted-navigation-header__active-filter').length > 0)) {
            window.scrollTo(0,resultPositionTop);
          }
        },700);
      }
    }
    /**
     * @function
     * Renders the component based upon the current properties.
     */
    render() {
      this.container.innerHTML = this.markup();
      this.closeButton = this.container.querySelector('.close-facets-mobile');

      /** Initialize filter group if filter component exists on the page  */
      this.filterComponents = [];
      if (this.container.querySelectorAll('.faceted-navigation__filter-container').length > 0) {
        [...this.container.querySelectorAll('.faceted-navigation__filter-container')].forEach(filterContainer => {
          let filterComponent = new App.Filter(filterContainer);

          if (this.hideFromActive && this.hideFromActive.indexOf(filterComponent.filterValues.name) === -1) {
            filterComponent.activeFilterValues.forEach(value => {
              this.selectedFilters[value.name] = { title: value.title, id: value.id, value: value.value };
            });
          }

          filterComponent.addEventListener('clearSelection', ({detail: { component }}) =>
            this.container.dispatchEvent(new CustomEvent('clearSelection', { detail: { component }})));

          filterComponent.addEventListener('selected', ({detail: { name, value, title, id, removeFilterIds }}) => {
            Object.keys(this.selectedFilters).map(key => removeFilterIds.indexOf(this.selectedFilters[key].id) > -1 && delete this.selectedFilters[key]);
            this.selectedFilters[name] = { value, title, id };
            this.container.dispatchEvent(new CustomEvent('filterSelected', { detail: { selectedId: id, value: value, activeFilters: this.activeFilterValues, removeFilterIds: removeFilterIds }}));
          });

          filterComponent.addEventListener('filterRemoved', ({detail: { name, value}}) => this.deactiveFilter(name, value));

          this.filterComponents.push(filterComponent);
        });
        // scroll function if filter component exists on the page
        this.filterPageScroll(this.container);
      }

      this.globalFacetSearchContainer = this.container.querySelector('.global-filter-search__container');
      if (this.globalFacetSearchContainer) {
        this.globalFacetSearch = new App.GlobalFacetSearch(this.globalFacetSearchContainer);
      }

      this.activeFiltersContainer = this.container.querySelector('.faceted-navigation__active-filters__container');
      this.activeFiltersComponent = new App.ActiveFilters(this.activeFiltersContainer);

      this.activeFiltersComponent.activeFilters = this.activeFilterList;

      if (this.activeFilterList > 0) {
        this.activeFiltersContainer.classList.remove('hidden');
      }

      this.addEventListeners();
    }

    /**
     * @function addEventListeners - Adds event listeners for the internal implementation
     * of this component.
     */
    addEventListeners() {
      this.closeButton.addEventListener('click', () => this.close());

      this.activeFiltersComponent.addEventListener('filterRemoved', ({detail: {name, value}}) => this.deactiveFilter(name, value));

      this.activeFiltersComponent.addEventListener('clearAllFilters', () => this.clearAllFilters());

      if (this.globalFacetSearch) {
        this.globalFacetSearch.addEventListener('suggestionSelected', ({detail: { category, value }}) => {
          this.filterComponents.forEach(filterComponent => {
            if (filterComponent.filterValues.title === category) {
              filterComponent.applyFilterValueByTitle(value);
            }
          });
        });

        this.globalFacetSearch.addEventListener('searchTermChanged', ({detail}) => {
          let suggestions = this.filterComponents.reduce((suggestions, filterComponent) => suggestions.concat(filterComponent.suggestedSearchTerms(detail)), []);
          this.globalFacetSearch.updateFacetSearchSuggestion(suggestions);
        });
      }
    }

    /**
     * @function resultsText
     * @returns the result text based upon whether there is currently a single result or multiple results.
     */
    get resultsText() {
      return this.resultCount > 1 ? this.resultsPluralText : this.resultsSingularText;
    }

    /**
     * @function get isOpen - check if Filters is open (mobile only) and return
     * @returns {boolean}
    */
    get isOpen() {
      return !this.container.classList.contains('hidden') &&
             !this.container.classList.contains('hidden-xs');
    }

    /**
     * @function get filterList
     * @returns {array} of filter values or an empty array
     */
    get filterList() {
      return this.filterListValues ? this.filterListValues.map(filter => {
        filter.values.forEach(valueProp => valueProp.active = typeof this.activeFilterValues[valueProp.name] !== 'undefined');
        filter.showAsGrid = this.propertyMap[filter.name] ? this.propertyMap[filter.name].showAsGrid : false;
        filter.facetSearchEnabled = this.propertyMap[filter.name] ? this.propertyMap[filter.name].facetSearchEnabled : false;
        filter.singleFacetEnabled = this.propertyMap[filter.name] ? this.propertyMap[filter.name].singleFacetEnabled : false;
        if (filter.name === 'eaton-secure_attributes') {
          filter.secure = true;
        }
        else {
          filter.secure = false;
        }
        return filter;
      }).sort((firstFilter, secondFilter) => (this.ordering.indexOf(firstFilter.name) > this.ordering.indexOf(secondFilter.name)) === true ? 1 : -1) : [];
    }

    /**
     * @function set propertyList the property list as an array. This will drive the behavior of the corresponding
     * filters and the order of the filters. This expects an array of objects each with a 'name' as a string,
     * a 'showAsGrid' as a boolean and a 'facetSearchEnabled' as a boolean.
     */
    set propertyList(props) {
      this.propertyListValues = props;
    }

    /**
     * @function get propertyList as defined in the JS doc for the setter.
     */
    get propertyList() {
      return this.propertyListValues ? this.propertyListValues : [];
    }

    /**
     * @function get propertyMap provides the same information as propertyList but provides it as a map with the name
     * as the key and the other properties contained with an object associated to that name.
     */
    get propertyMap() {
      return this.propertyList.reduce((map, prop) => {
        map[prop.name] = prop;
        return map;
      }, {});
    }

    /**
     * Returns an array of property names in the order in which they should be displayed.
     */
    get ordering() {
      return this.propertyListValues ? this.propertyListValues.map(prop => prop.name) : [];
    }

    /**
     * @function set filterList
     * @param {array} filterListValues - an array of filter list values
     * Re-renders component after filterListValues has been set
     * Dispatches filterListUpdated event
    */
    set filterList(filterListValues) {
      this.filterListValues = filterListValues;
      // call scroll function for submittal on selection/deselection
      this.filterPageScroll(this.container);
      this.container.dispatchEvent(new CustomEvent('filterListUpdated'));

      this.render();
    }

    /**
     * @function get activeFilterValues - Returns the list of active filters as
     * key value pairs, where each key is a filter category and each value is an
     * actual filter value.
    */
    get activeFilterValues() {
      return this.selectedFilters ? this.selectedFilters : {};
    }

    /**
     * @funtion clearFilters
     * Clears all filters and renders the component.
     */
    clearFilters() {
      this.selectedFilters = {};
      this.render();
    }
    /**
     * @function get activeFilterValues - Returns the list of active filters as
     * an array of objects where each object has a name, value and title, where
     * the name is the filter category, each value is the active value in that
     * category, and the title is the displayable title for the active value.
    */
    get activeFilterList() {
      let list = [];
      Object.keys(this.activeFilterValues).forEach(name => {
        list.push({
          name: name,
          value: this.activeFilterValues[name].value,
          title: this.activeFilterValues[name].title,
          id: this.activeFilterValues[name].id
        });
      });
      return list;
    }

    /**
     * @function deactiveFilter
     * Deactivates the filter with the provided name.
     */
    deactiveFilter(name, value) {
      delete this.activeFilterValues[name];
      this.container.dispatchEvent(new CustomEvent('filterRemoved', { detail: { name, value }}));
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
     * @function open - displays Filters
    */
    open() {
      // It begins hidden on XS, but after that it is either hidden or shown at every breakpoint.
      let App = window.App || {};
      this.container.classList.remove('hidden-xs');
      this.container.classList.remove('hidden');
      BODY_ELEMENT.classList.add('facets-open');
      if ( window.innerWidth < App.global.constants.GRID.MD) {
        for (let i = 0; i < $('button.faceted-navigation__header.button--reset').length; i++) {
          let objButton = $('button.faceted-navigation__header.button--reset')[i];
          $(objButton).attr('aria-expanded') === 'true' && $(objButton).trigger('click');
        }
      } else {
        for (let i = 0; i < $('button.faceted-navigation__header.button--reset').length; i++) {
          let objButton = $('button.faceted-navigation__header.button--reset')[i];
          window.innerWidth > App.global.constants.GRID.MD && $(objButton).attr('aria-expanded') === 'false' && $(objButton).trigger('click');
        }
      }
    }

    /**
     * @function close - hides Filters
    */
    close() {
      // Do not add the hidden XS class here, we want it hidden at every breakpoint.
      this.container.classList.add('hidden');
      BODY_ELEMENT.classList.remove('facets-open');
    }

    /**
     * @function get resultCount - Get's the current number of results as an integer.
     */
    get resultCount() {
      return this.resultCountValue;
    }

    /**
     * @function set resultCount - Updates the ActiveFilters component
     * with the updated result count.
    */
    set resultCount(count) {
      this.resultCountValue = count;
      this.activeFiltersComponent.resultCount = count;
    }

    /**
     * The filters/facets section of the submittal builder
     * @constructor
     * @param {object} container - A reference to the Filters container's DOM element
    */
    constructor(container) {
      // The constructor should only contain the boiler plate code for finding or creating the reference.
      if (typeof container.dataset.ref === 'undefined') {
        this.ref = Math.random();
        App.Filters.refs[this.ref] = this;
        container.dataset.ref = this.ref;
        this.init(container);
      } else {
        // If this element has already been instantiated, use the existing reference.
        return App.Filters.refs[container.dataset.ref];
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

  App.Filters.refs = { };
})();
