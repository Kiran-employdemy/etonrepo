//-----------------------------------
// Search Results Component
//-----------------------------------
'use strict';

let App = window.App || {};
App.searchResults = (function () {
  let resultListCSSClass = '.results-list';
  let $componentEl = $('.search-results').find(resultListCSSClass);
  let i18nStrings = {};

  /**
  * Initialize
  */
  const init = () => {
    addEventListeners();
    i18nStrings = App.global.utils.loadI18NStrings($componentEl);
  };

  /**
   * Bind All Event Listeners
   */
  const addEventListeners = () => {
    document.addEventListener('DOMContentLoaded', function(event) {
      let activeFiltersContainer = document.querySelector('.faceted-navigation__active-filters');
      if (activeFiltersContainer) {
        let activeFiltersComponent = new window.App.ActiveFilters(activeFiltersContainer);
        activeFiltersComponent.addEventListener('filterRemoved', (e) => window.location = e.detail.value);

        activeFiltersComponent.addEventListener('clearAllFilters', (e) => {
          window.location = window.location.pathname.split('.html')[0] + '.html';
        });
      }
      let filtersContainer = document.querySelector('#search-results__filters');
      if (filtersContainer) {
        let filtersComponent = new window.App.Filters(filtersContainer);
        filtersComponent.addEventListener('clearSelection', (e) => {
          if (e.detail.component.activeFilterValues.length > 0) {
            if (e.detail.component.filterValues.singleFacetEnabled || e.detail.component.filterValues.name === 'eaton-secure_attributes') {
              window.location = e.detail.component.activeFilterValues[0].id;
            } else {
              let selectedElement;
              let path = window.location.pathname;
              e.detail.component.activeFilterValues.forEach(function(element) {
                selectedElement = '$' + element.name;
                path = path.replace(selectedElement,'');
                window.location = path;
              });
            }
          }
        });

        filtersComponent.addEventListener('filterRemoved', (e) => window.location = e.detail.value);
        if (document.querySelector('.open-facets-mobile') !== null) {
          document.querySelector('.open-facets-mobile').addEventListener('click', () => filtersComponent.open());
        }
      }
    });

    $componentEl.find('[data-load-more]').on('click', loadMoreResults);
    i18nStrings = App.global.utils.loadI18NStrings($componentEl);
  };

  /**
  * Configure AJAX Request
  * @param { Object } options - description
  * @return { Promise }
  */
  let fetchData = (url, nextPage) => {
    /* eslint-disable no-unused-vars*/
    let requestOptions = {
      page: nextPage,
      format: 'json'
    };

    return $.ajax({
      type: 'GET',
      url: '/eaton/content/search/loadmore.json',
      data: 'url=' + url + '&count=' + nextPage,
      success: function(result) {


      }
    });
  };

  /**
  * It Returns the HTML Markup for the given Result Item
  * @param { Object } item
  * @param { Object } i18n
  * @return { String } String with HTML code
  */
  let getItemTemplate = (item, i18n) => {
    if (item.contentType === 'family' || item.contentType === 'sku') {
      return App.global.templates.searchResultsProductFamily(item, i18n);
    } else if (item.contentType === 'article' || item.contentType === 'news-and-insights') {
      return App.global.templates.searchResultsArticle(item, i18n);
    } else if (item.contentType === 'resources') {
      return App.global.templates.searchResultsResource(item, i18n);
    } else if (item.contentType === 'others') {
      return App.global.templates.searchResultsArticle(item, i18n);
    }
  };

  /**
  * Fetch the next page of results and add them to the DOM
  * @param { Object } event - Click Event Object
  */
  let loadMoreResults = (event) => {

    event.preventDefault();
    let $loadMoreBtn = $(event.currentTarget);
    let $currentComponent = $(event.currentTarget).closest(resultListCSSClass);
    let requestURL = $currentComponent.attr('data-results-url');
    let requestNextPage = $currentComponent.attr('data-results-next-page');
    $loadMoreBtn.blur(); // EATON-801 FIX - Button will stay in "Hover" state when clicked
    // If the Request URL doesn't exists Remove the Load More Button and don't proceed
    if (!requestURL) {
      $currentComponent.find('[data-load-more]').remove();
      return;
    }

    // Else Fetch New Results
    fetchData(requestURL, requestNextPage).done(function (_ref) {
      let search = _ref.search;

      let json = $.parseJSON(_ref);


      // Loop over all result items
      let newResults = json.resultsList.reduce(function (items, currentItem) {

        // Get the HTML Template for the current Result Item
        return items += getItemTemplate(currentItem, i18nStrings);
      }, '');

      // Append the new Result Elements to the DOM
      let $newResults = $(newResults).hide();
      $currentComponent.find('.results-list__content').append($newResults);
      $newResults.fadeIn(300);

      // if the next page of results is "null" or empty, remove "Load More" button
      if (json.buttonStatus === 'inActive') {
        $currentComponent.find('[data-load-more]').remove();
      }

      // Update Fetch URL for the next set of items / next AJAX Request
      $currentComponent.attr('data-results-url', requestURL);
      $currentComponent.attr('data-results-next-page', json.loadmoreButtonCount);

      // Request the image rendition module to update the images in the elements loaded with AJAX
      App.renditions.updateImagesSrc();
    })

    // Callback for Failed Request
    .fail(function (data) {
      console.error('[Request-Error]', data);
    });
  };

  /**
  * If containing DOM element is found, Initialize and Expose public methods
  */
  if ($componentEl.length > 0) {
    init();
    // Public methods
    // return { }
  }
}());
/* eslint-enable no-unused-vars*/