/* eslint-disable no-undef */
class MenuComponent {

  constructor(clickHandler) {
    this.clearFiltersLink = document.querySelector(querySelectorFor.clearFilters);
    this.clickHandler = clickHandler;
    this.initializeClicks();
  }

  addSelectedFilter(filter) {
    let htmlString = '\n      <button class="faceted-navigation-header__filter-link"\n        aria-label="Remove ' + filter.title + ' filter"\n        data-filter-name="' + filter.name + '"\n data-filter-value="' + filter.value + '" \n data-filter-check="' + filter.value + '"   >\n        <span class="faceted-navigation-header__filter-label"><bdi>' + filter.dataset.title + '</bdi></span>\n        <i class="icon close icon-close icon-close-adv"  aria-hidden="true" id="' + filter.value + '"></i>\n      </button>\n    ';
    document.getElementById(elementIdOf.selectedFilterContainer).innerHTML += htmlString;
  }

  removeFilterFromSelectedFilters(filter, single) {
    let filterNameToReturn = null;
    if (single) {
      let filterName = filter.getAttribute('name');
      document.querySelectorAll(querySelectorFor.selectedFilterButtons).forEach(button => {
        if (button.dataset.filterName === filterName) {
          button.remove();
          filterNameToReturn = button.dataset.filterValue;
        }
      });
    } else {
      let filterValue = filter.value;
      document.querySelectorAll(querySelectorFor.selectedFilterButtons).forEach(button => {
        if (button.dataset.filterValue === filterValue) {
          button.remove();
        }
      });
    }
    return filterNameToReturn;
  }

  unhideClearFilters() {
    this.clearFiltersLink.classList.remove(elementClasses.hideClearFilters);
  }

  hideClearFilters() {
    this.clearFiltersLink.classList.add(elementClasses.hideClearFilters);
  }

  initializeClicks() {
    let self = this;
    this.clearFiltersLink.onclick = () => {
      document.querySelectorAll(querySelectorFor.selectedFilterButtons).forEach(filter => {
        filter.remove();
      });
      self.clickHandler.dispatchEvent(customEvents.CLEAR_FILTERS_CLICKED);
    };

    document.getElementById(elementIdOf.selectedFilterContainer).onclick = (event) => {
      let id = event.target.id;
      if (!id || id === elementIdOf.selectedFilterContainer) {
        return;
      }
      document.getElementById(id).parentElement.remove();
      let facetForIdInFacetList = document.getElementById('facet-' + id);
      if (facetForIdInFacetList) {
        facetForIdInFacetList.checked = false;
      }
      self.clickHandler.dispatchEvent(customEvents.filterDeselected(id));
    };

    document.querySelectorAll(querySelectorFor.sortOptions).forEach(sortOption => {
      sortOption.onclick = () => {
        document.querySelector(querySelectorFor.selectedSortOption).classList.remove(elementClasses.selectedSortOption);
        sortOption.classList.add(elementClasses.selectedSortOption);
        document.getElementById(elementIdOf.sortByDropdownButton).querySelector(querySelectorFor.sortByDropdownLabel).innerHTML = sortOption.id;
        self.clickHandler.dispatchEvent(customEvents.sortOptionSelected(sortOption.dataset.value));
      };
    });
  }

  selectedFilterCount() {
    return document.querySelectorAll(querySelectorFor.selectedFilterButtons).length;
  }

  getSelectedSortBy() {
    return document.querySelector(querySelectorFor.selectedSortOption).dataset.value;
  }
}

const createMenuComponent = (clickHandler) => {
  return new MenuComponent(clickHandler);
};

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  module.exports = {createMenuComponent};
}
