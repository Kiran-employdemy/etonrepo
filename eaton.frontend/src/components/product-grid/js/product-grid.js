//-----------------------------------
// Product Gird Component
//-----------------------------------
'use strict';

let App = window.App || {};
App.productGrid = function () {

  const resultListCSSClass = '.results-list';
  let $componentEl = '';
  if ($('.product-grid').find(resultListCSSClass).length > 0) {
    $componentEl = $('.product-grid').find(resultListCSSClass);
  } else {
    $componentEl = $('.compatible-product-tool').find(resultListCSSClass);
  }

  let i18nStrings = {};

  /**
   * Initialize
   */
  const init = () => {

    /* addEventListeners(); added in first line to fix issue for "load more" button in IE10*/
    addEventListeners();
    i18nStrings = App.global.utils.loadI18NStrings($componentEl);
  };

  /**
   * Bind All Event Listeners
   */
  const addEventListeners = () => {
    document.addEventListener('DOMContentLoaded', function (event) {

      setTimeout(function() {
        let outerSelectedFilterDiv = document.createElement('div');
        outerSelectedFilterDiv.classList.add('all-filter-selected-value');
        let path = window.location.pathname;
        if (path.includes('searchTerm')) {
          let pathArray = window.location.pathname.split('.');
          for (let i = 0; i < pathArray.length; i++) {
            if (pathArray[i].includes('searchTerm')) {
              let searchTerm = pathArray[i].replace('searchTerm$', '');
              let searchTermUrlDiv = document.createElement('div');
              searchTermUrlDiv.classList.add('searchTerm-url-value');
              searchTermUrlDiv.innerText = searchTerm;
              outerSelectedFilterDiv.appendChild(searchTermUrlDiv);
            }
          }
        }
        const nodeList = document.querySelectorAll('.faceted-navigation__facet-value a input');
        for (let i = 0; i < nodeList.length; i++) {
          if (nodeList[i].checked) {
            let title = nodeList[i].getAttribute('data-title');
            let value = nodeList[i].value;
            let filterDiv = document.createElement('div');
            filterDiv.classList.add('filter-selected-value');
            filterDiv.innerHTML = '<button class=close-selected-filter data-value=' + value + '>X</button>' + title;
            outerSelectedFilterDiv.appendChild(filterDiv);
          }
        }

        const selectedFilterHeader = document.querySelectorAll('.compatible-product-tool .product-grid-results .faceted-navigation-header');
        for (let i = 0; i < selectedFilterHeader.length; i++) {
          selectedFilterHeader[i].appendChild(outerSelectedFilterDiv);
        }
        const clearIndividualSelectedFilter = document.querySelectorAll('.close-selected-filter');
        for (let j = 0;j < clearIndividualSelectedFilter.length;j++) {
          clearIndividualSelectedFilter[j].addEventListener('click', function(event) {
            let selectedElement;
            let path = window.location.pathname;
            selectedElement = '$' + event.target.dataset.value;
            path = path.replace(selectedElement, '');
            window.location = path;

          });
        }
      }, 1000);


      let activeFiltersContainer = document.querySelector('.faceted-navigation__active-filters');
      if (activeFiltersContainer) {
        let activeFiltersComponent = new window.App.ActiveFilters(activeFiltersContainer);
        activeFiltersComponent.addEventListener('filterRemoved', (e) => window.location = e.detail.value);

        activeFiltersComponent.addEventListener('clearAllFilters', (e) => {
          window.location = window.location.pathname.split('.models.')[0] + '.models.html';
        });
      }

      let filtersContainer = document.querySelector('#product-grid__filters') || {};
      if (filtersContainer) {
        let filtersComponent = new window.App.Filters(filtersContainer);

        filtersComponent.addEventListener('clearSelection', (e) => {
          if (e.detail.component.activeFilterValues.length > 0) {
            if (e.detail.component.filterValues.singleFacetEnabled) {
              window.location = e.detail.component.activeFilterValues[0].id;
            } else {
              let selectedElement;
              let path = window.location.pathname;
              e.detail.component.activeFilterValues.forEach(function (element) {
                // This is to handle  space in the text and encode it for correct replacement
                if (element.name === 'Secure Only') {
                  element.name = encodeURIComponent(element.name);
                }
                selectedElement = '$' + element.name;
                path = path.replace(selectedElement, '');
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

      if ($('#product-grid__filters a').length) {
        $('#product-grid__filters a').attr('onclick', "if(selectedSku.length > 0) { sessionStorage.setItem('selectedProdx', selectedSku) }");
      }

    });

    $componentEl.find('[data-load-more]').on('click', loadMoreResults);
  };

  /**
   * Configure AJAX Request
   * @param { Object } options - description
   * @return { Promise }
   */
  const fetchData = (url, nextPage) => {
    /* eslint-disable no-unused-vars*/
    const requestOptions = {
      page: nextPage,
      format: 'json'
    };

    return $.ajax({
      type: 'GET',
      url: '/eaton/content/loadmore.json',
      data: 'url=' + encodeURIComponent(url) + '&count=' + nextPage,
      success: function (result) {


      }
    });



  };

  /**
   * It Returns the HTML Markup for the given Result Item
   * @param { Object } item
   * @param { Object } i18n
   * @param { String } gridType
   * @return { String } String with HTML code
   */
  const getItemTemplate = (item, i18n, gridType) => {
    if (gridType === 'product-card-sku') {
      return App.global.templates.productGridSKU(item, i18n);
    } else if (gridType === 'product-card-subcategory') {
      return App.global.templates.productGridSubcategory(item, i18n);
    } else if (gridType === 'compatible-product-tool_dimmerView') {
      return App.global.templates.productCompatibilityDimmerView(item, i18n);
    } else if (gridType === 'compatible-product-tool_ledView') {
      return App.global.templates.productCompatibilityledView(item, i18n);
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
    fetchData(requestURL, requestNextPage).done(function (data) {

      let json = JSON.parse(data);


      // Loop over all result items
      let newResults = json.resultsList.reduce(function (items, currentItem) {

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
/* eslint-enable no-unused-vars*/
