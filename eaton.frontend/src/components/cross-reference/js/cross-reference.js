(function(App) {
  App.XRef = App.XRef || {};
  App.XRef.currentPage = 0;

  let container = document.querySelector('#cross-reference');
  if (container) {

    let $resultAccordion = $(container);
    let toggleLink = container.querySelector('.cross-reference__accordion-toggle__link');
    let mobileFiltersCount = container.querySelector('.cross-reference__mobile-filters-count');
    let expandText = toggleLink.dataset.activeText;
    let collapseText = toggleLink.dataset.inactiveText;
    let isExpandAll = false;

    App.XRef.sortBy = document.getElementById('default--option').dataset.defaultSortingTitle;
    App.XRef.sortByText = document.getElementById('default--option').dataset.defaultSortingTitleText;
    let eatonText = container.dataset.eatonText;
    let filtersComponent = new App.Filters(document.getElementById('cross-reference__filters'));
    let activeFiltersComponent = new App.ActiveFilters(document.getElementById('cross-reference__active-filters'));

    App.XRef.createResult = function(result, baseSkuPath) {
      let resultElement = document.getElementById('cross-reference-template').cloneNode(true);
      resultElement.classList.remove('hidden');
      resultElement.removeAttribute('id');

      let crossedBrand = result.crossedPartSubBrand && result.crossedPartSubBrand !== 'None' ? result.crossedPartSubBrand : result.crossedPartBrand;
      let brand = result.subBrand && result.subBrand !== 'None' ? result.subBrand : eatonText;

      resultElement.querySelector('.cross-reference__compare--competitor__brand').innerText = crossedBrand;
      resultElement.querySelector('.cross-reference__compare--competitor__number').innerText = result.crossedPartNumber;
      resultElement.querySelector('.cross-reference__type').innerText = result.partType;
      resultElement.querySelector('.cross-reference__compare--eaton__brand').innerText = brand;
      resultElement.querySelector('.cross-reference__compare--eaton__number').innerText = result.partNumber;
      resultElement.querySelector('.cross-reference__compare--eaton__link').href = `${ baseSkuPath }.${ result.encodedPartNumber }.html`;

      // Accordion data population
      resultElement.querySelector('.cross-reference__card__text__sku--number').innerText = result.partNumber;
      resultElement.querySelector('.cross-reference__card__text__sku--link').href = `${ baseSkuPath }.${ result.encodedPartNumber }.html`;
      resultElement.querySelector('.cross-reference__card__image__img').src = result.image;
      resultElement.querySelector('.cross-reference__card__text__description').innerText = result.description;

      if (result.status && result.status === 'Discontinued') {
        resultElement.querySelector('.cross-reference__compare--competitor__discontinued').classList.remove('hidden');
      } else {
        resultElement.querySelector('.cross-reference__compare--competitor__discontinued').classList.add('hidden');
      }

      if (result.comments) {
        resultElement.querySelector('.cross-reference__card__text__comment__copy').innerText = result.comments;
        resultElement.querySelector('.cross-reference__card__text__comment').classList.remove('hidden');
      } else {
        resultElement.querySelector('.cross-reference__card__text__comment').classList.add('hidden');
      }

      if ((result.upsellSkus && result.upsellSkus.length > 0) || (result.replPartNumbers && result.replPartNumbers.length > 0)) {
        resultElement.querySelector('.cross-reference__card__text__alternative a').href = `${ baseSkuPath }.${ result.encodedPartNumber }.html#upsell-products`;
        resultElement.querySelector('.cross-reference__card__text__alternative').classList.remove('hidden');
      } else {
        resultElement.querySelector('.cross-reference__card__text__alternative').classList.add('hidden');
      }

      resultElement.querySelector('.accordion').id = 'accordionEx' + result.resultID;
      resultElement.querySelector('.card-header').id = 'heading' + result.resultID;

      let icon = resultElement.querySelector('.cross-reference__collapse-icon') || {};
      if (!isExpandAll) {
        icon.classList.add('collapsed');
      }
      icon.attributes.getNamedItem('data-parent').nodeValue = '#accordion' + result.resultID;
      icon.attributes.getNamedItem('href').nodeValue = '#collapse' + result.resultID;
      icon.attributes.getNamedItem('aria-controls').nodeValue = '#collapse' + result.resultID;

      let body = resultElement.querySelector('.collapse') || {};
      body.attributes.getNamedItem('aria-labelledby').nodeValue = 'heading' + result.resultID;
      body.attributes.getNamedItem('data-parent').nodeValue = '#accordion' + result.resultID;
      body.id = 'collapse' + result.resultID;
      body.classList.add('card-collapse');
      if (isExpandAll) {
        body.classList.add('in');
      }
      App.XRef.setExpandCollapseText();
      return resultElement;
    };

    App.XRef.updateUrl = function(search, activeFilters, sortBy) {
      let segments = window.location.pathname.split('.');
      let path = segments[0];
      path += '.searchTerm$' + App.global.utils.encodeSelector(search);
      if (activeFilters && activeFilters.length > 0) {
        path += '.facets$' + activeFilters.join('$');
      }
      if (sortBy) {
        path += '.sort$' + sortBy;
      }
      path += '.html' + window.location.search;

      window.history.pushState({ }, '', path);
    };

    App.XRef.updateResults = function(page, activeFilters, { preventHistory = false, clearResults = true } = { }) {
      page = page || 0;
      let sortBy = App.XRef.sortBy || document.getElementById('default--option').dataset.defaultSortingTitle;
      let crossReferenceContainer = document.getElementById('cross-reference');
      let bestResultsList = document.querySelector('.cross-reference__results--best') || {};
      let partialResultsList = document.querySelector('.cross-reference__results--partial') || {};
      let search = document.getElementById('xref-search-box').value.trim();
      let xrefForm = document.getElementById('xref-search-form')[0];

      if (!search && xrefForm) {
        xrefForm.reportValidity();
      } else {
        if (!preventHistory) {
          App.XRef.updateUrl(search, activeFilters, sortBy);
        }

        let params = {
          search: search,
          page: page,
          sortBy: sortBy
        };

        if (activeFilters) {
          params.facets = activeFilters.join(',');
        }

        if (activeFilters && activeFilters.length > 0) {
          mobileFiltersCount.innerText = `(${ activeFilters.length })`;
        } else {
          mobileFiltersCount.innerText = '';
        }

        fetch(crossReferenceContainer.dataset.servletUrl + '?' + Object.keys(params).map(key => key + '=' + params[key]).join('&'))
        .then(response => response.json())
        .then(json => {
          if (json.error) {
            console.error(json.error);
            return;
          }

          if (clearResults) {
            bestResultsList.innerHTML = '';
            partialResultsList.innerHTML = '';
          }

          if (json.bestMatchResults.length > 0) {
            document.getElementById('best_matchHeader').classList.remove('hidden');
            document.querySelector('.cross-reference__results--best').classList.remove('hidden');
            document.getElementById('best_matchSearch').innerHTML = search;
          } else {
            document.getElementById('best_matchHeader').classList.add('hidden');
            document.querySelector('.cross-reference__results--best').classList.add('hidden');
          }

          if (json.partialMatchResults.length > 0) {
            document.getElementById('partial_matchHeader').classList.remove('hidden');
            document.querySelector('.cross-reference__results--partial').classList.remove('hidden');
            document.getElementById('partial_matchSearch').innerHTML = search;
          } else {
            document.getElementById('partial_matchHeader').classList.add('hidden');
            document.querySelector('.cross-reference__results--partial').classList.add('hidden');
          }
          let baseSkuPath = crossReferenceContainer.dataset.baseSkuPath;
          if (!baseSkuPath) {
            baseSkuPath = json.baseSKUPath;
          }

          json.partialMatchResults.forEach(result => partialResultsList.appendChild(App.XRef.createResult(result, baseSkuPath)));
          json.bestMatchResults.forEach(result => bestResultsList.appendChild(App.XRef.createResult(result, baseSkuPath)));

          filtersComponent.filterList = json.facets;

          let clearFiltersText = document.querySelector('.xref__clear-all-filters') || {};

          if (filtersComponent.activeFilterList.length > 0) {
            clearFiltersText.style.display = 'block';
          } else {
            clearFiltersText.style.display = 'none';
          }

          activeFiltersComponent.activeFilterCount = filtersComponent.activeFilterList.length;
          activeFiltersComponent.activeFilters = filtersComponent.activeFilterList;

          let resultCountElement = document.querySelector('.faceted-navigation-header__results-count') || {};
          if (Number(json.totalCount) > 0) {
            resultCountElement.innerText = `${ json.totalCount } ${ resultCountElement.dataset.resultsText }`;
            filtersComponent.resultCount = parseInt(json.totalCount);
          } else {
            resultCountElement.innerText = `0 ${ resultCountElement.dataset.resultsText }`;
            filtersComponent.resultCount = 0;
          }

          document.querySelector('.cross-reference__landing-message').classList.add('hidden');
          if (json.totalCount > 0) {
            container.classList.remove('hidden');
            document.querySelector('.cross-reference__no-results-message').classList.add('hidden');
          } else {
            container.classList.add('hidden');
            document.querySelector('.cross-reference__no-results-message').classList.remove('hidden');
          }

          if (parseInt(json.totalCount) > (bestResultsList.childElementCount + partialResultsList.childElementCount)) {
            document.querySelector('.cross-reference-load-more').classList.remove('hidden');
          } else {
            document.querySelector('.cross-reference-load-more').classList.add('hidden');
          }

          App.XRef.attachBackToSearchListeners();

          $resultAccordion.find('.card-collapse').on('hidden.bs.collapse', () => App.XRef.setExpandCollapseText());
          $resultAccordion.find('.card-collapse').on('shown.bs.collapse', () => App.XRef.setExpandCollapseText());
        });
      }
    };

    App.XRef.clearFilters = function() {
      filtersComponent.clearFilters();
    };

    App.XRef.resetResults = function(filters, preventHistory) {
      isExpandAll = false;
      App.XRef.currentPage = 0;
      App.XRef.updateResults(App.XRef.currentPage, filters, { preventHistory });
    };



    App.XRef.attachBackToSearchListeners = function() {
      [...document.querySelectorAll('.cross-reference__compare--eaton__link'),
        ...document.querySelectorAll('.cross-reference__card__text__sku--link'),
        ...document.querySelectorAll('.cross-reference__card__text__alternative a')
      ].forEach(resultLink => resultLink.addEventListener('click', () => {
        sessionStorage.setItem('backToSearch_show', 'true');
        sessionStorage.setItem('backToSearch_url', document.URL);
        sessionStorage.setItem('backToSearched_url', resultLink.href);
      }));
    };

    document.addEventListener('DOMContentLoaded', () => {
      let loadMore = document.querySelector('.cross-reference-load-more') || {};
      let clearAllFilters = document.querySelector('.xref__clear-all-filters') || {};
      let openFacets = document.querySelector('.open-facets-cross-reference-mobile') || {};

      $resultAccordion.find('.card-collapse').on('hidden.bs.collapse', () => App.XRef.setExpandCollapseText());
      $resultAccordion.find('.card-collapse').on('shown.bs.collapse', () => App.XRef.setExpandCollapseText());

      App.XRef.attachBackToSearchListeners();

      container.querySelector('.cross-reference__accordion-toggle__link').addEventListener('click', e => {
        e.stopImmediatePropagation();
        toggleResults();
      });

      function allClosed() {
        return container.querySelectorAll('.card-collapse.in').length === 0;
      }

      App.XRef.setExpandCollapseText = function() {
        toggleLink.innerText = allClosed() ? expandText : collapseText;
        if (allClosed()) {
          isExpandAll = false;
        }
      };

      function toggleResults() {
        toggleLink.classList.toggle('active');
        App.XRef.setExpandCollapseText();

        allClosed() ? openAllResults() : closeAllResults();
      }

      function openAllResults() {
        isExpandAll = true;
        container.querySelectorAll('.cross-reference__collapse-icon').forEach(icon => icon.classList.remove('collapsed'));
        $resultAccordion.find('.card-collapse:not(".in")').collapse('show');
      }

      function closeAllResults() {
        isExpandAll = false;
        container.querySelectorAll('.cross-reference__collapse-icon').forEach(icon => icon.classList.add('collapsed'));
        $resultAccordion.find('.card-collapse').collapse('hide');
      }

      document.querySelector('.xref-search').addEventListener('click', (event) => {
        App.XRef.clearFilters();
        App.XRef.resetResults();
        event.preventDefault();
      });

      document.querySelector('#xref-search-box').addEventListener('keydown', (event) => {

        if (event.key === 'Enter') {
          App.XRef.clearFilters();
          App.XRef.resetResults();
          event.preventDefault();
        }

      });

      filtersComponent.addEventListener('filterSelected', e => {
        isExpandAll = false;
        App.XRef.currentPage = 0;
        App.XRef.updateResults(App.XRef.currentPage, filtersComponent.activeFilterList.map(filter => filter.id));
        App.XRef.scrollToSearchBoxOffset();
      });

      filtersComponent.addEventListener('filterRemoved', e => {
        App.XRef.resetResults(filtersComponent.activeFilterList.map(filter => filter.id));
        App.XRef.scrollToSearchBoxOffset();
      });

      filtersComponent.addEventListener('clearAllFilters', () => {
        App.XRef.clearFilters();
        App.XRef.resetResults();
      });

      activeFiltersComponent.addEventListener('clearAllFilters', () => {
        App.XRef.clearFilters();
        App.XRef.resetResults();
      });

      filtersComponent.addEventListener('clearSelection', function (e) {
        if (e.detail) {
          isExpandAll = false;
          let filter = e.detail.component;
          let removeFilterIds = filter.activeFilterValues.map(filterValue => filterValue.id);
          let newActiveFilters = filtersComponent.selectedFilters;
            // remove all active filter values from the given filter and updated selectedFilter list
          Object.keys(filtersComponent.selectedFilters).map(function (key) {
            return removeFilterIds.indexOf(newActiveFilters[key].id) > -1 && delete newActiveFilters[key];
          });
          filtersComponent.selectedFilters = newActiveFilters;
          App.XRef.updateResults(App.XRef.currentPage, filtersComponent.activeFilterList.map(filter => filter.id));
          App.XRef.scrollToSearchBoxOffset();
        }
      });

      activeFiltersComponent.addEventListener('filterRemoved', (e) => {
        if (e.detail) {
          isExpandAll = false;
          App.XRef.currentPage = 0;
          let name = e.detail.name;
          let newActiveFilters = filtersComponent.selectedFilters;
          Object.keys(filtersComponent.selectedFilters).map(function (key) {
            return newActiveFilters[name] && delete newActiveFilters[name];
          });
          App.XRef.updateResults(App.XRef.currentPage, filtersComponent.activeFilterList.map(filter => filter.id));
        }
      });

      [...document.getElementsByClassName('sortByItems')].forEach(item => {
        item.addEventListener('click', () => {
          App.XRef.currentPage = 0;
          App.XRef.sortBy = item.dataset.sortbyoption;
          App.XRef.sortByText = item.dataset.sortbyoptiontext;
          App.XRef.updateResults(App.XRef.currentPage, filtersComponent.activeFilterList.map(filter => filter.id));

          [...document.getElementsByClassName('sortByItems')]
            .forEach(deselectItem => deselectItem.classList.remove('faceted-navigation-header__sort-options--selected'));
          item.classList.add('faceted-navigation-header__sort-options--selected');
          document.getElementById('default--option').innerText = App.XRef.sortByText;
        });
      });

      clearAllFilters.addEventListener('click', e => {
        e.preventDefault();
        App.XRef.clearFilters();
        App.XRef.resetResults();
      });

      loadMore.addEventListener('click', () => {
        App.XRef.currentPage += 1;
        App.XRef.updateResults(App.XRef.currentPage, filtersComponent.activeFilterList.map(filter => filter.id), { clearResults: false });
      });

      openFacets.addEventListener('click', e => {
        e.preventDefault();
        filtersComponent.open();
      });

      window.addEventListener('popstate', event => {
        if (container) {
          let searchSelector = window.location.pathname.split('.').find(str => str.indexOf('searchTerm') === 0);
          let searchTerm;

          if (searchSelector) {
            searchTerm = App.global.utils.decodeSelector(searchSelector.split('$')[1]);
          }

          let sortBySelector = window.location.pathname.split('.').find(str => str.indexOf('sort') === 0);
          if (sortBySelector) {
            App.XRef.sortBy = sortBySelector.split('$')[1];

            [...document.getElementsByClassName('sortByItems')].forEach(option => {
              option.classList.remove('faceted-navigation-header__sort-options--selected');

              if (option.dataset.sortbyoption === App.XRef.sortBy) {
                option.classList.add('faceted-navigation-header__sort-options--selected');
                App.XRef.sortByText = option.dataset.sortbyoptiontext;
              }
            });
          }

          let facetSelector = window.location.pathname.split('.').find(str => str.indexOf('facets') === 0);
          let facets = [];

          if (facetSelector) {
            facets = facetSelector.split('$');
            facets.shift();
          }

          if (searchTerm) {
            let newActiveFilters = filtersComponent.selectedFilters;
            Object.keys(filtersComponent.selectedFilters).forEach(key => facets.indexOf(key) === -1 && delete newActiveFilters[key]);
            filtersComponent.selectedFilters = newActiveFilters;

            App.XRef.resetResults(facets, true);
          }
        }
      });
    });
  }
})(window.App);
