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

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

/* eslint-disable no-undef */
var MenuComponent = function () {
  function MenuComponent(clickHandler) {
    _classCallCheck(this, MenuComponent);

    this.clearFiltersLink = document.querySelector(querySelectorFor.clearFilters);
    this.clickHandler = clickHandler;
    this.initializeClicks();
  }

  _createClass(MenuComponent, [{
    key: 'addSelectedFilter',
    value: function addSelectedFilter(filter) {
      var htmlString = '\n      <button class="faceted-navigation-header__filter-link"\n        aria-label="Remove ' + filter.title + ' filter"\n        data-filter-name="' + filter.name + '"\n data-filter-value="' + filter.value + '" \n data-filter-check="' + filter.value + '"   >\n        <span class="faceted-navigation-header__filter-label"><bdi>' + filter.dataset.title + '</bdi></span>\n        <i class="icon close icon-close icon-close-adv"  aria-hidden="true" id="' + filter.value + '"></i>\n      </button>\n    ';
      document.getElementById(elementIdOf.selectedFilterContainer).innerHTML += htmlString;
    }
  }, {
    key: 'removeFilterFromSelectedFilters',
    value: function removeFilterFromSelectedFilters(filter, single) {
      var filterNameToReturn = null;
      if (single) {
        var filterName = filter.getAttribute('name');
        document.querySelectorAll(querySelectorFor.selectedFilterButtons).forEach(function (button) {
          if (button.dataset.filterName === filterName) {
            button.remove();
            filterNameToReturn = button.dataset.filterValue;
          }
        });
      } else {
        var filterValue = filter.value;
        document.querySelectorAll(querySelectorFor.selectedFilterButtons).forEach(function (button) {
          if (button.dataset.filterValue === filterValue) {
            button.remove();
          }
        });
      }
      return filterNameToReturn;
    }
  }, {
    key: 'unhideClearFilters',
    value: function unhideClearFilters() {
      this.clearFiltersLink.classList.remove(elementClasses.hideClearFilters);
    }
  }, {
    key: 'hideClearFilters',
    value: function hideClearFilters() {
      this.clearFiltersLink.classList.add(elementClasses.hideClearFilters);
    }
  }, {
    key: 'initializeClicks',
    value: function initializeClicks() {
      var self = this;
      this.clearFiltersLink.onclick = function () {
        document.querySelectorAll(querySelectorFor.selectedFilterButtons).forEach(function (filter) {
          filter.remove();
        });
        self.clickHandler.dispatchEvent(customEvents.CLEAR_FILTERS_CLICKED);
      };

      document.getElementById(elementIdOf.selectedFilterContainer).onclick = function (event) {
        var id = event.target.id;
        if (!id || id === elementIdOf.selectedFilterContainer) {
          return;
        }
        document.getElementById(id).parentElement.remove();
        var facetForIdInFacetList = document.getElementById('facet-' + id);
        if (facetForIdInFacetList) {
          facetForIdInFacetList.checked = false;
        }
        self.clickHandler.dispatchEvent(customEvents.filterDeselected(id));
      };

      document.querySelectorAll(querySelectorFor.sortOptions).forEach(function (sortOption) {
        sortOption.onclick = function () {
          document.querySelector(querySelectorFor.selectedSortOption).classList.remove(elementClasses.selectedSortOption);
          sortOption.classList.add(elementClasses.selectedSortOption);
          document.getElementById(elementIdOf.sortByDropdownButton).querySelector(querySelectorFor.sortByDropdownLabel).innerHTML = sortOption.id;
          self.clickHandler.dispatchEvent(customEvents.sortOptionSelected(sortOption.dataset.value));
        };
      });
    }
  }, {
    key: 'selectedFilterCount',
    value: function selectedFilterCount() {
      return document.querySelectorAll(querySelectorFor.selectedFilterButtons).length;
    }
  }, {
    key: 'getSelectedSortBy',
    value: function getSelectedSortBy() {
      return document.querySelector(querySelectorFor.selectedSortOption).dataset.value;
    }
  }]);

  return MenuComponent;
}();

var createMenuComponent = function createMenuComponent(clickHandler) {
  return new MenuComponent(clickHandler);
};

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  module.exports = { createMenuComponent: createMenuComponent };
}