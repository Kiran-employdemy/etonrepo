//-----------------------------------
// Product Gird Component
//-----------------------------------
'use strict';

let App = window.App || {};
App.productGrid = (function() {

  const resultListCSSClass = '.results-list';
  const $componentEl = $('.product-grid').find(resultListCSSClass);
  let i18nStrings = {};


  /**
  * Initialize
  */
  const init = () => {
    i18nStrings = App.global.utils.loadI18NStrings($componentEl);
    addEventListeners();
  };


  /**
   * Bind All Event Listeners
   */
  const addEventListeners = function () {
    $componentEl.find('[data-load-more]').on('click', loadMoreResults);
  };


  /**
  * Configure AJAX Request
  * @param { Object } options - description
  * @return { Promise }
  */
  const fetchData = (url, nextPage) => {
    const requestOptions = {
      page: nextPage,
      format: 'json'
    };

    return $.getJSON(url, requestOptions);
  };


  /**
  * It Returns the HTML Markup for the given Result Item
  * @param { Object } item
  * @param { Object } i18n
  * @param { String } gridType
  * @return { String } String with HTML code
  */
  const getItemTemplate = (item, i18n, gridType) => {
    if (item.contentType === 'product-card' && gridType === 'product-card-sku') {
      return App.global.templates.productGridSKU(item, i18n);
    }

    else if (item.contentType === 'product-card' && gridType === 'product-card-subcategory') {
      return App.global.templates.productGridSubcategory(item, i18n);
    }
  };


  /**
  * Fetch the next page of results and add them to the DOM
  * @param { Object } event - Click Event Object
  */
  const loadMoreResults = (event) => {

    event.preventDefault();
    const $loadMoreBtn = $(event.currentTarget);
    const $currentComponent = $loadMoreBtn.closest(resultListCSSClass);
    const requestURL = $currentComponent.attr('data-results-url');
    const requestNextPage = $currentComponent.attr('data-results-next-page');
    const gridType = $currentComponent.attr('data-results-type');

    $loadMoreBtn.blur(); // EATON-801 FIX - Button will stay in "Hover" state when clicked

    // If the Request URL doesn't exists Remove the Load More Button and don't proceed
    if (!requestURL) {
      $currentComponent.find('[data-load-more]').remove();
      return;
    }

    // This object is used as a helper to map the AJAX Response Object
    // to the appropiate productGridType (sku / subcategory)
    const resultsObjectMap = {
      'product-card-sku': 'sku',
      'product-card-subcategory': 'subcategory'
    };

    // Else Fetch New Results
    fetchData(requestURL, requestNextPage)
      .done( data => {

        // Loop over all result items
        const newResults = data[resultsObjectMap[gridType]].resultsList.reduce((items, currentItem) => {

          // Get the HTML Template for the current Result Item
          return items += getItemTemplate(currentItem, i18nStrings, gridType);

        }, '');

        // Append the new Result Elements to the DOM
        const $newResults = $(newResults).hide();
        $currentComponent.find('.results-list__content').append($newResults);
        $newResults.fadeIn(300);

        // if the next page of results is "null" or empty, remove "Load More" button
        if (!data.search.ajaxRequestNextPage) {
          $currentComponent.find('[data-load-more]').remove();
        }

        // Update Fetch URL for the next set of items / next AJAX Request
        $currentComponent.attr('data-results-url', data.search.ajaxRequestUrl);
        $currentComponent.attr('data-results-next-page', data.search.ajaxRequestNextPage);

        // Request the image rendition module to update the images in the elements loaded with AJAX
        App.renditions.updateImagesSrc();

      })

      // Callback for Failed Request
      .fail((data) => {
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
