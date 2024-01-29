'use strict';

let App = window.App || {};
App.template = function () {

    // renditionImage

  let renditionImage = (image) => {
    return '\n      <div class="rendition">\n        <img\n          class="rendition__image img-responsive"\n          data-src="' + image.mobile + '"\n          data-mobile-rendition="' + image.mobile + '"\n          data-tablet-rendition="' + image.tablet + '"\n          data-desktop-rendition="' + image.desktop + '"\n        />\n      </div>\n    ';
  };

    // SKU VG Template
  let productGridVG = (data, i18n) => {
    return '\n      <div class="product-card-sku">\n\n        <div class="product-card-sku__image-wrapper b-body-copy-small">\n          <a href="' + data.contentItem.link.url + '"\n            class="product-card-sku__image-link"\n            data-analytics-event="model-result"\n            target="' + data.contentItem.link.target + '"\n          >\n            ' + renditionImage(data.contentItem.image) + '\n          </a>\n        </div>\n\n        <div class="product-card-sku__header">\n\n          <div class="product-card-sku__title-wrapper">\n            <h3 class="product-card-sku__name">\n              <a href="' + data.contentItem.link.url + '"\n                target="' + data.contentItem.link.target + '"\n                class="product-card-sku__url-link"\n              >\n                <span class="name-label">' + data.contentItem.name + '</span>\n                <i class="icon icon-chevron-right" aria-hidden="true"></i>\n              </a>\n            </h3>\n                      </div>\n\n               </div>\n\n        <div class="product-card-sku__content">\n          <div class="product-card-sku__attrs-list-vg">\n\n            ' + data.contentItem.productAttributes.map(function (attribute) {
      return '\n                <div class="product-card-sku__attrs-list-item-vg">\n                  <div class="product-card-sku__attr-label b-eyebrow-small text-uppercase">' + attribute.productAttributeLabel + '</div>\n                  <div class="product-card-sku__attr-value b-body-copy">' + attribute.productAttributeValue + '</div>\n                </div>';
    }).join('') + '\n\n          </div>\n          <div class="product-card-sku__description">' + data.contentItem.description + '</div>\n        </div>\n\n      </div>';
  };

  return {
    renditionImage: renditionImage,
    productGridVG: productGridVG

  };

}();
App.productGrid = function () {
  let resultListCSSClass = '.results-list';
  let $componentEl = $('.product-selector-results').find(resultListCSSClass);
  let i18nStrings = {};

  /**
  * Initialize
  */
  let init = () => {
    /* addEventListeners(); added in first line to fix issue for "load more" button in IE10*/
    addEventListeners();
    i18nStrings = App.global.utils.loadI18NStrings($componentEl);
  };

  /**
   * Bind All Event Listeners
   */
  let addEventListeners = () => {
    document.addEventListener('DOMContentLoaded', function(event) {
      let activeFiltersContainer = document.querySelector('.faceted-navigation__active-filters');
      if (activeFiltersContainer) {
        let activeFiltersComponent = new window.App.ActiveFilters(activeFiltersContainer);
        activeFiltersComponent.addEventListener('filterRemoved', (e) => window.location = e.detail.value);

        activeFiltersComponent.addEventListener('clearAllFilters', (e) => {
          window.location = window.location.pathname.split('.models.')[0] + '.models.html';
        });
      }

      let filtersContainer = document.querySelector('#vg-selector__filters');
      if (filtersContainer) {
        let filtersComponent = new window.App.Filters(filtersContainer);

        filtersComponent.addEventListener('clearSelection', (e) => {
          if (e.detail.component.activeFilterValues.length > 0) {
            window.location = e.detail.component.activeFilterValues[0].id;
          }
        });

        filtersComponent.addEventListener('filterRemoved', (e) => window.location = e.detail.value);
        if (document.querySelector('.open-facets-mobile') !== null) {
          document.querySelector('.open-facets-mobile').addEventListener('click', () => filtersComponent.open());
        }
      }
    });

    $componentEl.find('[data-load-more]').on('click', loadMoreResults);
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
		// alert("click");
    return $.ajax({
      type: 'GET',
      url: '/eaton/vgproductselector/loadmore.json',
      data: 'url=' + url + '&count=' + nextPage,
      success: function success(result) {}
    });
  };

  /**
  * It Returns the HTML Markup for the given Result Item
  * @param { Object } item
  * @param { Object } i18n
  * @param { String } gridType
  * @return { String } String with HTML code
  */
  let getItemTemplate = (item, i18n, gridType) => {

    if (gridType === 'product-card-sku') {
      return App.global.templates.productGridSKU(item, i18n);
    } else if (gridType === 'product-card-vg') {
      return App.template.productGridVG(item, i18n);
    }
  };

  /**
  * Fetch the next page of results and add them to the DOM
  * @param { Object } event - Click Event Object
  */
  let loadMoreResults = (event) => {

    event.preventDefault();
    let $loadMoreBtn = $(event.currentTarget);
    let $currentComponent = $loadMoreBtn.closest(resultListCSSClass);
    let requestURL = $currentComponent.attr('data-results-url');
    let requestNextPage = $currentComponent.attr('data-results-next-page');
    let gridType = $currentComponent.attr('data-results-type');
    $loadMoreBtn.blur(); // EATON-801 FIX - Button will stay in "Hover" state when clicked
    // If the Request URL doesn't exists Remove the Load More Button and don't proceed
    if (!requestURL) {
      $currentComponent.find('[data-load-more]').remove();
      return;
    }

    // This object is used as a helper to map the AJAX Response Object
    // to the appropiate productGridType (sku / subcategory)
    let resultsObjectMap = {
      'product-card-sku': 'sku',
      'product-card-vg': 'sku'

    };

    // Else Fetch New Results
    fetchData(requestURL, requestNextPage).done(function (data) {

      let json = JSON.parse(data);

      // Loop over all result items
      let newResults = json.resultsList.reduce( (items, currentItem) => {

        // Get the HTML Template for the current Result Item
        return items += getItemTemplate(currentItem, i18nStrings, gridType);
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
/* eslint-enable no-unused-vars*/
