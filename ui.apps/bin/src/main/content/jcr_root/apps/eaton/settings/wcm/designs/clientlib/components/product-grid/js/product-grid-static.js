/**
 *
 *
 *
 * - THIS IS AN AUTOGENERATED FILE. DO NOT EDIT THIS FILE DIRECTLY -
 * - Generated by Gulp (gulp-babel).
 *
 *
 *
 *
 */


//-----------------------------------
// Product Gird Component
//-----------------------------------
'use strict';

var App = window.App || {};
App.productGrid = function () {

  var resultListCSSClass = '.results-list';
  var $componentEl = $('.product-grid').find(resultListCSSClass);
  var i18nStrings = {};

  /**
  * Initialize
  */
  var init = function init() {
    i18nStrings = App.global.utils.loadI18NStrings($componentEl);
    addEventListeners();
  };

  /**
   * Bind All Event Listeners
   */
  var addEventListeners = function addEventListeners() {
    $componentEl.find('[data-load-more]').on('click', loadMoreResults);
  };

  /**
  * Configure AJAX Request
  * @param { Object } options - description
  * @return { Promise }
  */
  var fetchData = function fetchData(url, nextPage) {
    var requestOptions = {
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
  var getItemTemplate = function getItemTemplate(item, i18n, gridType) {
    if (item.contentType === 'product-card' && gridType === 'product-card-sku') {
      return App.global.templates.productGridSKU(item, i18n);
    } else if (item.contentType === 'product-card' && gridType === 'product-card-subcategory') {
      return App.global.templates.productGridSubcategory(item, i18n);
    }
  };

  /**
  * Fetch the next page of results and add them to the DOM
  * @param { Object } event - Click Event Object
  */
  var loadMoreResults = function loadMoreResults(event) {

    event.preventDefault();
    var $loadMoreBtn = $(event.currentTarget);
    var $currentComponent = $loadMoreBtn.closest(resultListCSSClass);
    var requestURL = $currentComponent.attr('data-results-url');
    var requestNextPage = $currentComponent.attr('data-results-next-page');
    var gridType = $currentComponent.attr('data-results-type');

    $loadMoreBtn.blur(); // EATON-801 FIX - Button will stay in "Hover" state when clicked

    // If the Request URL doesn't exists Remove the Load More Button and don't proceed
    if (!requestURL) {
      $currentComponent.find('[data-load-more]').remove();
      return;
    }

    // This object is used as a helper to map the AJAX Response Object
    // to the appropiate productGridType (sku / subcategory)
    var resultsObjectMap = {
      'product-card-sku': 'sku',
      'product-card-subcategory': 'subcategory'
    };

    // Else Fetch New Results
    fetchData(requestURL, requestNextPage).done(function (data) {

      // Loop over all result items
      var newResults = data[resultsObjectMap[gridType]].resultsList.reduce(function (items, currentItem) {

        // Get the HTML Template for the current Result Item
        return items += getItemTemplate(currentItem, i18nStrings, gridType);
      }, '');

      // Append the new Result Elements to the DOM
      var $newResults = $(newResults).hide();
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
}();