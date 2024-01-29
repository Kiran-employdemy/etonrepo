(function () {
  const FACET_SEARCH_NUMBER_OF_SUGGESTIONS = 2;
  const FACET_SEARCH_MIN_LENGTH = 2;

  let App = window.App || {};
  App.GlobalFacetSearch = class GlobalFacetSearch {
    /**
     * Initialize the SubmittalFilters class
     * @function init
     * @param {object} container - A reference to the SubmittalFilters container's DOM element so data attributes may be referenced
    */
    init(container) {
      this.container = container;
      this.facetSearchLabel = this.container.dataset.globalFacetSearchLabel;
      this.facetSearchPlaceholder = this.container.dataset.globalFacetSearchPlaceholder;
      this.inText = this.container.dataset.globalFacetSearchInText;
      this.noSuggestionsText = this.container.dataset.globalFacetSearchNoSuggestionsText;

      this.render();
    }

    /**
     * @function markup
     * The current html representation of the component based upon the current properties.
     */
    markup() {
      return `
        <label class="global-filter-search__label">${ this.facetSearchLabel }</label>
        <input type="text" class="global-filter-search__input" placeholder="${ this.facetSearchPlaceholder }">
        <ul class="global-filter-search__suggestions"></ul>
        <div class="global-filter-search__suggestions no-suggestions-container hidden">
          <div class="global-filter-search__suggestion global-filter-search__suggestion__no-suggestions__message">
            ${ this.noSuggestionsText }
            <span class="global-filter-search__suggestion__suggested-term">
              ${ this.facetSearchTerm }
            </span>
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
      this.input = this.container.querySelector('.global-filter-search__input');
      this.suggestions = this.container.querySelector('.global-filter-search__suggestions');
      this.noSuggestions = this.container.querySelector('.no-suggestions-container');
      this.noSuggestionsTerm = this.container.querySelector('.global-filter-search__suggestion__suggested-term');

      this.addEventListeners();
    }

    /**
     * @function addEventListeners - Adds event listeners for the internal implementation
     * of this component.
     */
    addEventListeners() {
      this.input.addEventListener('input', () =>
        this.container.dispatchEvent(new CustomEvent('searchTermChanged', { detail: this.facetSearchTerm })));

      let closeSuggestions = e => {
        if (!this.container.contains(e.target)) {
          this.suggestions.classList.add('hidden');
          this.noSuggestions.classList.add('hidden');
        }
      };

      document.removeEventListener('click', closeSuggestions);
      document.addEventListener('click', closeSuggestions);
    }

    /**
     * @function get facetSearchTerm
     * @returns {string} the value in the input field
     */
    get facetSearchTerm() {
      return this.input ? this.input.value : '';
    }

    /**
     * @function createSuggestions
     * @returns {array} returns an array of list items of suggestions
     */
    createSuggestions(suggestions) {
      return suggestions
      .filter(suggestedFilter => suggestedFilter.value.toLowerCase().indexOf(this.facetSearchTerm.toLowerCase()) !== -1)
      .slice(0, FACET_SEARCH_NUMBER_OF_SUGGESTIONS)
      .map(({value, category}) => `
        <li class="global-filter-search__suggestion">
          <a href="#" data-search-value="${ value }" data-search-category="${ category }" class="global-filter-search__suggestion__link">
            <span class="global-filter-search__suggestion__suggested-term">${ value }</span>
            <span class="global-filter-search__suggestion__conjunction">${ this.inText }</span>
            <span class="global-filter-search__suggestion__suggested-facetgroup">${ category }</span>
          </a>
        </li>
      `);
    }

    /**
     * @function updateFacetSearchSuggestion
     * displays suggestions if FACET_SEARCH_MIN_LENGTH is met, hides it suggestions otherwise
     ** if there are matching suggestions, noSuggestions is hidden, suggestions is displayed, and click events are attached to each search suggestion
     ** if there are no matching suggestions, suggestions is hidden and noSuggestions is displayed
     */
    updateFacetSearchSuggestion(suggestions) {
      if (this.facetSearchTerm.length >= FACET_SEARCH_MIN_LENGTH) {
        let suggestionsMarkup = this.createSuggestions(suggestions);
        if (suggestionsMarkup.length > 0) {
          this.suggestions.innerHTML = suggestionsMarkup.join('');
          this.noSuggestions.classList.add('hidden');
          this.suggestions.classList.remove('hidden');
        } else {
          this.noSuggestions.classList.remove('hidden');
          this.suggestions.classList.add('hidden');
          this.noSuggestionsTerm.innerText = this.facetSearchTerm;
        }
      } else {
        this.noSuggestions.classList.add('hidden');
        this.suggestions.classList.add('hidden');
      }

      this.container.querySelectorAll('.global-filter-search__suggestion__link').forEach(suggestion =>
        suggestion.addEventListener('click', e => {
          e.preventDefault();
          this.container.dispatchEvent(
            new CustomEvent('suggestionSelected', { detail: { category: suggestion.dataset.searchCategory, value: suggestion.dataset.searchValue }}));
        })
      );
    }

    /**
     * The global facet search component
     * @constructor
     * @param {object} container - A reference to the GlobalFacetSearch container's DOM element
    */
    constructor(container) {
      // The constructor should only contain the boiler plate code for finding or creating the reference.
      if (typeof container.dataset.ref === 'undefined') {
        this.ref = Math.random();
        App.GlobalFacetSearch.refs[this.ref] = this;
        container.dataset.ref = this.ref;
        this.init(container);
      } else {
        // If this element has already been instantiated, use the existing reference.
        return App.GlobalFacetSearch.refs[container.dataset.ref];
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

  App.GlobalFacetSearch.refs = { };
})();
