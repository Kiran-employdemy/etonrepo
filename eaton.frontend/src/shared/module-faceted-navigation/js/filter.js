(function () {
  const unescapeAttr = window.App.global.utils.unescapeAttr;

  let App = window.App || {};
  App.Filter = class Filter {
    /**
     * Initialize the Filter class
     * @function init
     * @param {object} container - A reference to the Filter container's DOM element so data attributes may be referenced
    */
    init(container) {
      this.container = container;
      this.filterValues = unescapeAttr(this.container.dataset.values);
      this.title = this.container.dataset.title;
      this.name = this.container.dataset.name;
      this.defaultVisibleFacets = this.showAsGrid && window.matchMedia && window.matchMedia(App.global.constants.MEDIA_QUERIES.DESKTOP).matches ? 21 : 7;
      this.viewMoreText = this.container.dataset.viewMoreText;
      this.viewLessText = this.container.dataset.viewLessText;
      this.clearSelectionText = this.container.dataset.clearSelectionText;
      this.facetGroupSearchLabel = this.container.dataset.facetGroupSearchLabel;
      this.facetGroupSearchPlaceholder = this.container.dataset.facetGroupSearchPlaceholder;
      this.facetGroupSearchNoSuggestionsText = this.container.dataset.facetGroupSearchNoSuggestionsText;
      this.facetValueCount = this.container.dataset.facetValueCount;
      this.render();
    }

    /**
     * @function markup
     * The current html representation of the component based upon the current properties.
     */
    markup() {
      let cnt = 0;
      let upperCnt = this.facetValueCount;
      let viewMoreCnt = this.filterValues.values.length - this.facetValueCount;
      let showHideStr = viewMoreCnt > 0 ? 'show' : 'hidden';
      let str1 = '';
      let str2 = '';


      return `
        <div class="faceted-navigation__facet-group ${ this.showAsGrid ? 'faceted-navigation__facet-group--grid' : '' } ${ this.filterValues.values.length > 0 ? '' : 'hidden' }">
          <button class="faceted-navigation__header button--reset"
            data-toggle="collapse"
            data-target="#${ this.filterValues.name }"
            aria-expanded="true"
            aria-controls="${ this.filterValues.name }">
              ${ this.filterValues.title }
              <i class="icon icon-sign-minus" aria-hidden="true"></i>
              <i class="icon icon-sign-plus faceted-navigation__icon-sign-plus" aria-hidden="true"></i>
          </button>
          <div id="${ this.filterValues.name }" class="collapse in">

            <div class="faceted-navigation__list__search__container ${ this.facetSearchEnabled && !this.showAsGrid ? '' : 'hidden' }">
              <label class="faceted-navigation__list__search__label">${ this.facetGroupSearchLabel }</label>
              <input type="text" class="faceted-navigation__list__search__input" placeholder="${ this.facetGroupSearchPlaceholder }">
              <div class="faceted-navigation__list__search__suggestions faceted-navigation__list__search__no-suggestions__container hidden">
                <div class="faceted-navigation__list__search__suggestion faceted-navigation__list__search__suggestion__no-suggestions__message">
                  ${ this.facetGroupSearchNoSuggestionsText }
                  <span class="faceted-navigation__list__search__suggestion__suggested-term">
                    ${ this.facetGroupSearchTerm }
                  </span>
                </div>
              </div>
            </div>

            <a href="#"
              rel="nofollow"
              role="button"
              class="faceted-navigation-header__action-link faceted-navigation-header__action-link--clear-filters faceted-navigation-header__action-link--clear-filters--sidebar ${ this.activeFilterValues.length > 0 && !this.filterValues.hideClearLink ? '' : 'hidden' }">
              ${ this.clearSelectionText }
            </a>
            <fieldset>
              <ul class="faceted-navigation__list faceted-navigation__list--unfiltered ${ this.showAsGrid && this.filterValues.singleFacetEnabled && this.filterValues.values.find(filterValue => filterValue.active === true) !== undefined ? 'faceted-navigation__facet-group--grid__has-checked-facet' : '' }">
              ${ this.filterValues.values.map(filterValue => {
        /* eslint-disable */
        let urlFromFilterValue = filterValue.url;
        let url = urlFromFilterValue ?  urlFromFilterValue.replace('(#.*)','') + (window.location.hash?window.location.hash:'') : (urlFromFilterValue === undefined ? "#" : window.location);
        if (cnt < upperCnt || upperCnt === 0) {
          /* eslint-enable */
          cnt++;
          str1 = `<li class="faceted-navigation__list-item ${ filterValue.isHidden ? 'hidden' : '' } ${ this.showAsGrid && filterValue.active ? 'faceted-navigation__facet-group--grid__facet--checked' : '' }">
                    <div class="faceted-navigation__facet-value">
                      <label class="faceted-navigation__facet-value-label">
                        <a href="${ url ? url : '#' }"
                           rel="nofollow"
                           role="${ url ? 'link' : 'button' }"
                           class="${ url ? 'has-url' : '' }">
                          <input
                            data-analytics-event="submittal-builder-facet"
                            data-analytics-name="checkbox : ${ filterValue.name }"
                            data-analytics-state="${ filterValue.active ? 'on' : 'off' }"
                            data-single-facet-enabled="${ this.filterValues.singleFacetEnabled }"
                            type="${ this.filterValues.singleFacetEnabled ? 'radio' : 'checkbox' }"
                            class="input input--small"
                            value="${ filterValue.name }"
                            name="${ this.filterValues.name }"
                            data-title="${ filterValue.title }"
                            data-id="${ filterValue.id }"
                            data-is-secure="${ filterValue.secure }"
                            ${ filterValue.active ? 'checked' : '' }>
                          <span class="inner">`;
          str2 = `<bdi>${ filterValue.title }</bdi></span>
                        </a>
                      </label>
                    </div>
                    </li>`;
          if (this.filterValues.secure) {
            return str1 + '\n <i class="icon icon-secure-lock" aria-hidden="true"></i> \n' + str2;
          } else {
            return str1 + str2;
          }
        } else {
          str1 = `<li class="faceted-navigation__list-item-more ${ filterValue.isHidden ? 'hidden' : '' } ${ this.showAsGrid && filterValue.active ? 'faceted-navigation__facet-group--grid__facet--checked' : '' }">
                    <div class="faceted-navigation__facet-value">
                      <label class="faceted-navigation__facet-value-label">
                        <a href="${ url ? url : '#' }"
                           rel="nofollow"
                           role="${ url ? 'link' : 'button' }"
                           class="${ url ? 'has-url' : '' }">
                          <input
                            data-analytics-event="submittal-builder-facet"
                            data-analytics-name="checkbox : ${ filterValue.name }"
                            data-analytics-state="${ filterValue.active ? 'on' : 'off' }"
                            data-single-facet-enabled="${ this.filterValues.singleFacetEnabled }"
                            type="${ this.filterValues.singleFacetEnabled ? 'radio' : 'checkbox' }"
                            class="input input--small"
                            value="${ filterValue.name }"
                            name="${ this.filterValues.name }"
                            data-title="${ filterValue.title }"
                            data-id="${ filterValue.id }"
                            ${ filterValue.active ? 'checked' : '' }>
                          <span class="inner">`;
          str2 = `<bdi>${ filterValue.title }</bdi></span>
                        </a>
                      </label>
                    </div>
                  </li>`;
          if (this.filterValues.secure) {
            return str1 + '\n <i class="icon icon-secure-lock" aria-hidden="true"></i> \n' + str2;
          } else {
            return str1 + str2;
          }
        }
              }
      ).join('') }
                 <li class="faceted-navigation__view-more-less-container ${ showHideStr }">
                 <button class="button--reset faceted-navigation__view-more-values">${ this.viewMoreText } <span class="faceted-navigation__view-more-values__count"><bdi>(${ viewMoreCnt })</bdi></span></button>
                  <button class="button--reset faceted-navigation__view-less-values">${ this.viewLessText }</button>
                </li>
              </ul>
            </fieldset>
          </div>
        </div>
      `;
    }

    /**
     * @function
     * Renders the component based upon the current properties.
     */
    render() {
      this.container.innerHTML = this.markup();

      this.viewMoreLessValuesContainer = this.container.querySelector('.faceted-navigation__view-more-less-container');
      this.viewMoreValuesButton = this.container.querySelector('.faceted-navigation__view-more-values');
      this.viewLessValuesButton = this.container.querySelector('.faceted-navigation__view-less-values');
      this.viewMoreValuesCount = this.container.querySelector('.faceted-navigation__view-more-values__count');
      this.noSuggestionsMessageContainer = this.container.querySelector('.faceted-navigation__list__search__no-suggestions__container');
      this.noSuggestionsMessage = this.container.querySelector('.faceted-navigation__list__search__suggestion__no-suggestions__message');
      this.clearSelectionButton = this.container.querySelector('.faceted-navigation-header__action-link--clear-filters');
      this.facetList = this.container.querySelector('.faceted-navigation__list');
      this.facetListItems = this.container.querySelectorAll('.faceted-navigation__list-item');

      this.input = this.container.querySelector('.faceted-navigation__list__search__input');
      this.noSuggestions = this.container.querySelector('.faceted-navigation__list__search__no-suggestions__container');
      this.noSuggestionsTerm = this.container.querySelector('.faceted-navigation__list__search__suggested-term');

      this.addEventListeners();
    }

    /**
     * @function addEventListeners - Adds event listeners for the internal implementation
     * of this component.
     */
    addEventListeners() {
      this.viewMoreValuesButton.addEventListener('click', () => this.showMoreValues());
      this.viewLessValuesButton.addEventListener('click', () => this.showLessValues());

      // If a filter value is selected pass its dataset into each registered callback.
      this.container.querySelectorAll('.faceted-navigation__facet-value-label a').forEach(filterLink => {
        filterLink.addEventListener('click', (e) => {
          if (filterLink.getAttribute('href') === '#') {
            e.preventDefault();
            this.applyFilterValue(filterLink.querySelector('input'));
          }
        });
      });

      this.clearSelectionButton.addEventListener('click', (e) => {
        e.preventDefault();
        this.clearSelection();
      });

      this.input.addEventListener('input', () => this.filterFacets());
    }

    get showAsGrid() {
      return this.filterValues.showAsGrid;
    }

    get facetSearchEnabled() {
      return this.filterValues.facetSearchEnabled;
    }

    /**
     * @function suggestedSearchTerms
     * @param searchTerm The search term to use as a basis.
     * @returns The filter titles that match this provided search term.
     */
    suggestedSearchTerms(searchTerm) {
      return this.filterValues.values.map(filterValue => ({ value: filterValue.title, category: this.filterValues.title }));
    }

    /**
     * @function applyFilterValueByTitle
     * Provided a filter value title this will find the dom element filter with that title and apply it.
     */
    applyFilterValueByTitle(filterTitle) {
      let matchingFilters = this.filterValues.values.filter(filterValue => filterValue.title === filterTitle);

      if (matchingFilters.length > 0) {
        let filterName = matchingFilters[0].name;
        let filterElement = this.container.querySelector('input[value="' + filterName + '"]');
        if (filterElement !== null) {
          filterElement.checked = true;
          if (filterElement.parentElement.classList.contains('has-url')) {
            filterElement.parentElement.click();
          }
          this.applyFilterValue(filterElement);
        }
      }
    }

    /**
     * @function get facetGroupSearchTerm
     * @returns {string} the value in the input field
     */
    get facetGroupSearchTerm() {
      return this.input ? this.input.value : '';
    }

    /**
     * @function showMoreValues - 5 filters are displayed initially.
     * If there are 6 or more filters available in the group, the rest are shown when this method is called
    */
    showMoreValues() {
      this.container.classList.add('faceted-navigation--show-all-values');
    }

    /**
     * @function showLessValues - Hide all but the first 5 filters
    */
    showLessValues() {
      this.container.classList.remove('faceted-navigation--show-all-values');
    }

    /**
     * @function activeFilterValues
     * The list of active filters.
     */
    get activeFilterValues() {
      return this.filterValues.values.filter(value => value.active);
    }

    /**
     * @function clearSelection
     * Clears the filter selection from this filter group.
     */
    clearSelection() {
      this.container.dispatchEvent(new CustomEvent('clearSelection', { detail: { component: this } }));
    }

    /**
     * @function applyFilterValue
     * Applies the given filer value. This should be the dom element filter value
     */
    applyFilterValue(filterValue) {
      let matchingValues = this.filterValues.values.filter(value => value.id === filterValue.dataset.id);
      let wasNotActive = matchingValues.filter(value => value.active).length === 0;
      if (wasNotActive && matchingValues.length > 0) {
        let matchingValue = matchingValues[0];
        let removeFilterIds = this.filterValues.singleFacetEnabled
          ? this.activeFilterValues.map(filterValue => filterValue.id)
          : [];

        this.container.dispatchEvent(new CustomEvent('selected', {
          detail: {
            name: matchingValue.name,
            value: matchingValue.value,
            title: matchingValue.title,
            id: matchingValue.id,
            removeFilterIds: removeFilterIds,
            component: this
          }
        }));
      } else if (!wasNotActive && matchingValues.length > 0) {
        this.container.dispatchEvent(new CustomEvent('filterRemoved', {
          detail: {
            name: matchingValues[0].name,
            value: matchingValues[0].value,
            component: this
          }
        }));
      }
    }

    /**
     * @function setViewMoreButtonVisibility
     * If there are more matching filtered facets than the default amount of visible facets, the view more button should be visible
     */
    setViewMoreButtonVisibility() {
      if (this.matchingFilteredFacets > this.defaultVisibleFacets) {
        this.viewMoreLessValuesContainer.classList.remove('hidden');
      } else {
        this.viewMoreLessValuesContainer.classList.add('hidden');
      }

      this.viewMoreValuesCount.innerHTML = `(${ this.matchingFilteredFacets })`;
    }
    /**
     * @function setNoSuggestionsVisibility
     * Display a message if there are no matching filtered facets
     */
    setNoSuggestionsVisibility() {
      if (this.matchingFilteredFacets === 0) {
        this.noSuggestionsMessage.innerHTML = `${ this.facetGroupSearchNoSuggestionsText }
          <span class="faceted-navigation__list__search__suggestion__suggested-term">
            ${ this.facetGroupSearchTerm }
          </span>`;
        this.noSuggestionsMessageContainer.classList.remove('hidden');
      } else {
        this.noSuggestionsMessageContainer.classList.add('hidden');
      }
    }

    /**
     * @function setFacetListFilteredState
     * Checks if there are no matchingFilteredFacets or if all facets match
     * (using startsWith() with an empty string returns a match, meaning all facets match)
    */
    setFacetListFilteredState() {
      if (this.matchingFilteredFacets === 0 || this.matchingFilteredFacets === this.filterValues.values.length) {
        this.facetList.classList.remove('faceted-navigation__list--filtered');
        this.facetList.classList.add('faceted-navigation__list--unfiltered');
      } else {
        this.facetList.classList.remove('faceted-navigation__list--unfiltered');
        this.facetList.classList.add('faceted-navigation__list--filtered');
      }
    }

    /**
     * @function filterFacets
     * Compares the facetGroupSearchTerm with the facet titles
     * Hides facets that do not contain the facetGroupSearchTerm
     * Sets matchingFilteredFacets to keep track of how many facets contain the facetGroupSearchTerm
     * Calls setFacetListFilteredState, setViewMoreButtonVisibility, and setNoSuggestionsVisibility after comparison is complete
     */
    filterFacets() {
      this.matchingFilteredFacets = 0;

      // there may be a cleaner way to do this
      this.filterValues.values = this.filterValues.values.map((filter, index) => {
        if (filter.title.toLowerCase().startsWith(this.facetGroupSearchTerm.toLowerCase()) === false) {
          this.facetListItems[index].classList.add('hidden');
          this.facetListItems[index].classList.remove('faceted-navigation__list-item__filtered-match');
        } else {
          this.facetListItems[index].classList.remove('hidden');
          this.facetListItems[index].classList.add('faceted-navigation__list-item__filtered-match');
          this.matchingFilteredFacets++;
        }

        return filter;
      });

      this.setFacetListFilteredState();
      this.setViewMoreButtonVisibility();
      this.setNoSuggestionsVisibility();
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

    /**
     * The filter/facet group
     * @constructor
     * @param {object} container - A reference to the Filter container's DOM element
    */
    constructor(container) {
      // The constructor should only contain the boiler plate code for finding or creating the reference.
      if (typeof container.dataset.ref === 'undefined') {
        this.ref = Math.random();
        App.Filter.refs[this.ref] = this;
        container.dataset.ref = this.ref;
        this.init(container);
      } else {
        // If this element has already been instantiated, use the existing reference.
        return App.Filter.refs[container.dataset.ref];
      }
    }
  };

  App.Filter.refs = {};
})();
