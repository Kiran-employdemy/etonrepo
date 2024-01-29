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


'use strict';

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

function _toConsumableArray(arr) { if (Array.isArray(arr)) { for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) { arr2[i] = arr[i]; } return arr2; } else { return Array.from(arr); } }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

(function () {
  var BODY_ELEMENT = document.querySelector('body');
  var escapeAttr = window.App.global.utils.escapeAttr;
  var unescapeAttr = window.App.global.utils.unescapeAttr;
  var App = window.App || {};
  App.Filters = function () {
    _createClass(Filters, [{
      key: 'init',

      /**
       * Initialize the Filters class
       * @function init
       * @param {object} container - A reference to the Filters container's DOM element so data attributes may be referenced
      */
      value: function init(container) {
        var _this = this;

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
          this.filterListValues.forEach(function (filter) {
            filter.values.forEach(function (value) {
              if (value.active) {
                _this.selectedFilters[value.name] = value;
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

    }, {
      key: 'markup',
      value: function markup() {
        var _this2 = this;

        return '\n        <div class="eaton-form faceted-navigation faceted-navigation__filters">\n          <h3 class="faceted-navigation__title">' + this.title + '</h3>\n          ' + (!this.facetSearchDisabled ? '\n            <div class="global-filter-search__container"\n              data-global-facet-search-label="' + this.facetSearchLabel + '"\n              data-global-facet-search-placeholder="' + this.facetSearchPlaceholder + '"\n              data-global-facet-search-in-text="' + this.inText + '"\n              data-global-facet-search-no-suggestions-text="' + this.noSuggestionsText + '"\n              data-global-facet-search-filter-components="' + escapeAttr(this.filterComponents) + '">\n            </div>\n          ' : '') + '\n          <div class="mobile-header">\n            <span>' + this.mobileDialogTitle + '</span>\n            <button data-toggle-modal-facet\n              aria-label="' + this.closeText + '"\n              class="close-facets-mobile button--reset">\n              <span class="sr-only">' + this.closeText + ' ' + this.title + '</span>\n              <i class="icon icon-close" aria-hidden="true"></i>\n            </button>\n          </div>\n          <hr class="faceted-navigation__hr">\n          <div class="cross-reference__active-filters bullseye-map-active__filters faceted-navigation__active-filters__container faceted-navigation__active-filters__container--mobile ' + (this.activeFilterList ? '' : 'hidden') + '"\n            data-active-filter-values="' + escapeAttr(this.activeFilterList) + '"\n            data-result-count="' + this.resultCount + '"\n            data-results-text="' + this.resultsText + '"\n            data-of-text="' + this.ofText + '"\n            data-clear-all-filters-text="' + this.clearAllFiltersText + '"\n            data-clear-filters-text="' + this.clearFiltersText + '"\n            data-clear-selection-text="' + this.clearSelectionText + '">\n          </div>\n          ' + this.filterList.map(function (filter) {
          return '\n            <div class="faceted-navigation__filter-container"\n              data-values="' + escapeAttr(filter) + '"\n              data-clear-selection-text="' + _this2.clearSelectionText + '"\n              data-view-more-text="' + _this2.viewMoreText + '"\n              data-facet-value-count="' + _this2.facetValueCount + '"\n              data-view-less-text="' + _this2.viewLessText + '"\n              data-facet-group-search-label="' + _this2.facetGroupSearchLabel + '"\n              data-facet-group-search-placeholder="' + _this2.facetGroupSearchPlaceholder + '"\n              data-facet-group-search-no-suggestions-text="' + _this2.facetGroupSearchNoSuggestionsText + '"></div>\n          ';
        }).join('') + '\n        </div>\n      ';
      }
      /**
       * @function filterPageScroll
       * scrolls the page on load to top of results header if filter is selected
       */

    }, {
      key: 'filterPageScroll',
      value: function filterPageScroll(filterContainerRef) {
        if (document.querySelector('.product-tabs-v2') !== null) {
          return;
        }
        var headerElem = document.querySelector('.header-primary-nav');
        var HEADER_HEIGHT_STICKY = headerElem ? headerElem.offsetHeight : 0;
        var FILTER_MAIN_CONTAINER = filterContainerRef.closest('.container').parentElement.parentElement;
        var PSEUDO_DIVIDER_HEIGHT = 4;
        var resultPositionTop = FILTER_MAIN_CONTAINER.offsetTop;
        var NON_STICKY_HEADER_MOBILE = false;
        var HEADER_ACTUAL_HEIGHT = void 0;
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
          var COOKIES_ACCEPTANCE_HEIGHT = document.getElementsByClassName('otCenterRounded')[0].offsetHeight;
          // remove cookie height and header height
          resultPositionTop -= HEADER_ACTUAL_HEIGHT + COOKIES_ACCEPTANCE_HEIGHT;
        } else {
          // Remove Header height from the scroll position
          resultPositionTop -= HEADER_ACTUAL_HEIGHT;
        }
        if (!this.preventScrollTop) {
          setTimeout(function () {
            // scroll if any of the filter is selected
            if (document.querySelectorAll('.faceted-navigation__filter-container input[type="checkbox"]:checked').length > 0 || document.querySelectorAll('.faceted-navigation__filter-container input[type="radio"]:checked').length > 0 || document.querySelectorAll('.faceted-navigation-header__active-filter').length > 0) {
              window.scrollTo(0, resultPositionTop);
            }
          }, 700);
        }
      }
      /**
       * @function
       * Renders the component based upon the current properties.
       */

    }, {
      key: 'render',
      value: function render() {
        var _this3 = this;

        this.container.innerHTML = this.markup();
        this.closeButton = this.container.querySelector('.close-facets-mobile');

        /** Initialize filter group if filter component exists on the page  */
        this.filterComponents = [];
        if (this.container.querySelectorAll('.faceted-navigation__filter-container').length > 0) {
          [].concat(_toConsumableArray(this.container.querySelectorAll('.faceted-navigation__filter-container'))).forEach(function (filterContainer) {
            var filterComponent = new App.Filter(filterContainer);

            if (_this3.hideFromActive && _this3.hideFromActive.indexOf(filterComponent.filterValues.name) === -1) {
              filterComponent.activeFilterValues.forEach(function (value) {
                _this3.selectedFilters[value.name] = { title: value.title, id: value.id, value: value.value };
              });
            }

            filterComponent.addEventListener('clearSelection', function (_ref) {
              var component = _ref.detail.component;
              return _this3.container.dispatchEvent(new CustomEvent('clearSelection', { detail: { component: component } }));
            });

            filterComponent.addEventListener('selected', function (_ref2) {
              var _ref2$detail = _ref2.detail,
                  name = _ref2$detail.name,
                  value = _ref2$detail.value,
                  title = _ref2$detail.title,
                  id = _ref2$detail.id,
                  removeFilterIds = _ref2$detail.removeFilterIds;

              Object.keys(_this3.selectedFilters).map(function (key) {
                return removeFilterIds.indexOf(_this3.selectedFilters[key].id) > -1 && delete _this3.selectedFilters[key];
              });
              _this3.selectedFilters[name] = { value: value, title: title, id: id };
              _this3.container.dispatchEvent(new CustomEvent('filterSelected', { detail: { selectedId: id, value: value, activeFilters: _this3.activeFilterValues, removeFilterIds: removeFilterIds } }));
            });

            filterComponent.addEventListener('filterRemoved', function (_ref3) {
              var _ref3$detail = _ref3.detail,
                  name = _ref3$detail.name,
                  value = _ref3$detail.value;
              return _this3.deactiveFilter(name, value);
            });

            _this3.filterComponents.push(filterComponent);
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

    }, {
      key: 'addEventListeners',
      value: function addEventListeners() {
        var _this4 = this;

        this.closeButton.addEventListener('click', function () {
          return _this4.close();
        });

        this.activeFiltersComponent.addEventListener('filterRemoved', function (_ref4) {
          var _ref4$detail = _ref4.detail,
              name = _ref4$detail.name,
              value = _ref4$detail.value;
          return _this4.deactiveFilter(name, value);
        });

        this.activeFiltersComponent.addEventListener('clearAllFilters', function () {
          return _this4.clearAllFilters();
        });

        if (this.globalFacetSearch) {
          this.globalFacetSearch.addEventListener('suggestionSelected', function (_ref5) {
            var _ref5$detail = _ref5.detail,
                category = _ref5$detail.category,
                value = _ref5$detail.value;

            _this4.filterComponents.forEach(function (filterComponent) {
              if (filterComponent.filterValues.title === category) {
                filterComponent.applyFilterValueByTitle(value);
              }
            });
          });

          this.globalFacetSearch.addEventListener('searchTermChanged', function (_ref6) {
            var detail = _ref6.detail;

            var suggestions = _this4.filterComponents.reduce(function (suggestions, filterComponent) {
              return suggestions.concat(filterComponent.suggestedSearchTerms(detail));
            }, []);
            _this4.globalFacetSearch.updateFacetSearchSuggestion(suggestions);
          });
        }
      }

      /**
       * @function resultsText
       * @returns the result text based upon whether there is currently a single result or multiple results.
       */

    }, {
      key: 'clearFilters',


      /**
       * @funtion clearFilters
       * Clears all filters and renders the component.
       */
      value: function clearFilters() {
        this.selectedFilters = {};
        this.render();
      }
      /**
       * @function get activeFilterValues - Returns the list of active filters as
       * an array of objects where each object has a name, value and title, where
       * the name is the filter category, each value is the active value in that
       * category, and the title is the displayable title for the active value.
      */

    }, {
      key: 'deactiveFilter',


      /**
       * @function deactiveFilter
       * Deactivates the filter with the provided name.
       */
      value: function deactiveFilter(name, value) {
        delete this.activeFilterValues[name];
        this.container.dispatchEvent(new CustomEvent('filterRemoved', { detail: { name: name, value: value } }));
      }

      /**
       * @function clearAllFilters
       * This only dispatches an event it does not actually do the clearing of filters.
       * This is meant to be used as a way to tell client code that all the filters should be cleared.
       */

    }, {
      key: 'clearAllFilters',
      value: function clearAllFilters() {
        this.container.dispatchEvent(new CustomEvent('clearAllFilters'));
      }

      /**
       * @function open - displays Filters
      */

    }, {
      key: 'open',
      value: function open() {
        // It begins hidden on XS, but after that it is either hidden or shown at every breakpoint.
        var App = window.App || {};
        this.container.classList.remove('hidden-xs');
        this.container.classList.remove('hidden');
        BODY_ELEMENT.classList.add('facets-open');
        if (window.innerWidth < App.global.constants.GRID.MD) {
          for (var i = 0; i < $('button.faceted-navigation__header.button--reset').length; i++) {
            var objButton = $('button.faceted-navigation__header.button--reset')[i];
            $(objButton).attr('aria-expanded') === 'true' && $(objButton).trigger('click');
          }
        } else {
          for (var _i = 0; _i < $('button.faceted-navigation__header.button--reset').length; _i++) {
            var _objButton = $('button.faceted-navigation__header.button--reset')[_i];
            window.innerWidth > App.global.constants.GRID.MD && $(_objButton).attr('aria-expanded') === 'false' && $(_objButton).trigger('click');
          }
        }
      }

      /**
       * @function close - hides Filters
      */

    }, {
      key: 'close',
      value: function close() {
        // Do not add the hidden XS class here, we want it hidden at every breakpoint.
        this.container.classList.add('hidden');
        BODY_ELEMENT.classList.remove('facets-open');
      }

      /**
       * @function get resultCount - Get's the current number of results as an integer.
       */

    }, {
      key: 'resultsText',
      get: function get() {
        return this.resultCount > 1 ? this.resultsPluralText : this.resultsSingularText;
      }

      /**
       * @function get isOpen - check if Filters is open (mobile only) and return
       * @returns {boolean}
      */

    }, {
      key: 'isOpen',
      get: function get() {
        return !this.container.classList.contains('hidden') && !this.container.classList.contains('hidden-xs');
      }

      /**
       * @function get filterList
       * @returns {array} of filter values or an empty array
       */

    }, {
      key: 'filterList',
      get: function get() {
        var _this5 = this;

        return this.filterListValues ? this.filterListValues.map(function (filter) {
          filter.values.forEach(function (valueProp) {
            return valueProp.active = typeof _this5.activeFilterValues[valueProp.name] !== 'undefined';
          });
          filter.showAsGrid = _this5.propertyMap[filter.name] ? _this5.propertyMap[filter.name].showAsGrid : false;
          filter.facetSearchEnabled = _this5.propertyMap[filter.name] ? _this5.propertyMap[filter.name].facetSearchEnabled : false;
          filter.singleFacetEnabled = _this5.propertyMap[filter.name] ? _this5.propertyMap[filter.name].singleFacetEnabled : false;
          if (filter.name === 'eaton-secure_attributes') {
            filter.secure = true;
          } else {
            filter.secure = false;
          }
          return filter;
        }).sort(function (firstFilter, secondFilter) {
          return _this5.ordering.indexOf(firstFilter.name) > _this5.ordering.indexOf(secondFilter.name) === true ? 1 : -1;
        }) : [];
      }

      /**
       * @function set propertyList the property list as an array. This will drive the behavior of the corresponding
       * filters and the order of the filters. This expects an array of objects each with a 'name' as a string,
       * a 'showAsGrid' as a boolean and a 'facetSearchEnabled' as a boolean.
       */
      ,


      /**
       * @function set filterList
       * @param {array} filterListValues - an array of filter list values
       * Re-renders component after filterListValues has been set
       * Dispatches filterListUpdated event
      */
      set: function set(filterListValues) {
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

    }, {
      key: 'propertyList',
      set: function set(props) {
        this.propertyListValues = props;
      }

      /**
       * @function get propertyList as defined in the JS doc for the setter.
       */
      ,
      get: function get() {
        return this.propertyListValues ? this.propertyListValues : [];
      }

      /**
       * @function get propertyMap provides the same information as propertyList but provides it as a map with the name
       * as the key and the other properties contained with an object associated to that name.
       */

    }, {
      key: 'propertyMap',
      get: function get() {
        return this.propertyList.reduce(function (map, prop) {
          map[prop.name] = prop;
          return map;
        }, {});
      }

      /**
       * Returns an array of property names in the order in which they should be displayed.
       */

    }, {
      key: 'ordering',
      get: function get() {
        return this.propertyListValues ? this.propertyListValues.map(function (prop) {
          return prop.name;
        }) : [];
      }
    }, {
      key: 'activeFilterValues',
      get: function get() {
        return this.selectedFilters ? this.selectedFilters : {};
      }
    }, {
      key: 'activeFilterList',
      get: function get() {
        var _this6 = this;

        var list = [];
        Object.keys(this.activeFilterValues).forEach(function (name) {
          list.push({
            name: name,
            value: _this6.activeFilterValues[name].value,
            title: _this6.activeFilterValues[name].title,
            id: _this6.activeFilterValues[name].id
          });
        });
        return list;
      }
    }, {
      key: 'resultCount',
      get: function get() {
        return this.resultCountValue;
      }

      /**
       * @function set resultCount - Updates the ActiveFilters component
       * with the updated result count.
      */
      ,
      set: function set(count) {
        this.resultCountValue = count;
        this.activeFiltersComponent.resultCount = count;
      }

      /**
       * The filters/facets section of the submittal builder
       * @constructor
       * @param {object} container - A reference to the Filters container's DOM element
      */

    }]);

    function Filters(container) {
      _classCallCheck(this, Filters);

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


    _createClass(Filters, [{
      key: 'addEventListener',
      value: function addEventListener() {
        return this.container.addEventListener.apply(this.container, arguments);
      }

      /**
       * @function removeEventListener - A method to allow clients to remove event listeners
       *  from this component. Calls the removeEventListener method of this components containing element.
       */

    }, {
      key: 'removeEventListener',
      value: function removeEventListener() {
        return this.container.removeEventListener.apply(this.container, arguments);
      }
    }]);

    return Filters;
  }();

  App.Filters.refs = {};
})();