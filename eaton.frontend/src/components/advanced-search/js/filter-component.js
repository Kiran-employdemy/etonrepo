/* eslint-disable no-undef */
// noinspection JSConstantReassignment

if (typeof require !== 'undefined') {
  const globalConstants = require('../../../global/js/etn-new-global-constants');
  constants = require('./advanced-search-constants');
  displayStyles = globalConstants.displayStyles;
  eventListeners = globalConstants.eventListeners;
  customEvents = constants.customEvents;
}

class FilterComponent {

  constructor(clickHandler, filterRemoved, filterAdded) {
    this.clickHandler = clickHandler;
    this.filterRemoved = filterRemoved;
    this.filterAdded = filterAdded;
    this.registerDateClicks();
  }

  renderFilters(searchResult) {
    let filterTemplate = document.getElementById(elementIdOf.mustacheTemplate.forFilter);
    let filterdisplay = document.querySelector(querySelectorFor.filterDisplay);
    if (filterdisplay !== null && filterTemplate) {
      let template = filterTemplate.innerHTML;
      this.fillSearchI18nKeysToFacetsList(searchResult);
      filterdisplay.innerHTML = window.Mustache.render(template, searchResult.facets);
      this.closeMobileAccordions();
      if (filterdisplay.innerHTML.indexOf(elementIdOf.secureAttributes) > -1) {
        let innerspan = document.getElementById(elementIdOf.secureAttributes).querySelector(querySelectorFor.filterInnerSpan) || {};
        let securelock = this.getSecurelock();
        innerspan.insertBefore(securelock, innerspan.lastChild);
      }
      this.displayViewMoreAndLessAndInitializeEvents(searchResult);
      this.addClickListenerToMobileFilter();
      this.registerFacetsEventListener();
    }
  }

  fillSearchI18nKeysToFacetsList(searchResult) {
    let searchResultsFilter = document.getElementById(elementIdOf.searchResultsFilter);
    let searchResultsFilterDataset = searchResultsFilter.dataset;
    let searchLabel = searchResultsFilterDataset.searchLabel;
    let searchPlaceHolder = searchResultsFilterDataset.facetSearchPlaceholder;
    if (searchResult.facets && searchResult.facets.facetGroupList && searchResult.facets.facetGroupList.length > 0) {
      searchResult.facets.facetGroupList.forEach(groupList => {
        groupList.searchLabel = searchLabel;
        groupList.searchPlaceHolder = searchPlaceHolder;
      });
    }
  }

  addClickListenerToMobileFilter() {
    let self = this;
    document.getElementById(elementIdOf.mobileFilterButton).onclick = (event) => {
      event.stopPropagation();
      document.body.classList.add(elementClasses.facetsOpen);
      self.closeMobileAccordions();
    };
    document.querySelector(querySelectorFor.mobileFilterCloseIcon).onclick = (event) => {
      event.stopPropagation();
      document.body.classList.remove(elementClasses.facetsOpen);
    };
  }

  closeMobileAccordions() {
    let isMobile = (window.innerWidth < window.App.global.constants.GRID.MD) ? literals.TRUE : literals.FALSE;
    document.querySelectorAll(querySelectorFor.mobileCollapsable).forEach(mobileCollapsable => {
      mobileCollapsable.getAttribute(elementAttributes.ariaExpanded) === isMobile && mobileCollapsable.click();
    });
  }

  displayViewMoreAndLessAndInitializeEvents(searchResult) {
    let defaultShow = searchResult.facetsViewMoreOffset;
    let filterGroups = document.querySelectorAll(querySelectorFor.filterGroups);
    // eslint-disable-next-line no-eq-null, eqeqeq

    filterGroups.forEach((filterGroup) => {
      let filters = filterGroup.querySelectorAll(querySelectorFor.listItem);
      if (filters.length > defaultShow) {
        filterGroup.querySelector(querySelectorFor.filterViewMore).style.display = displayStyles.block;
        filterGroup.querySelector(querySelectorFor.filterViewLess).style.display = displayStyles.none;
        let viewMoreCount = filters.length - defaultShow;
        filterGroup.querySelector(querySelectorFor.filterViewMore + ' ' + htmlTags.bdi).innerHTML = '(' + viewMoreCount + ')';
      } else {
        filterGroup.querySelector(querySelectorFor.filterViewMore).style.display = displayStyles.none;
        filterGroup.querySelector(querySelectorFor.filterViewLess).style.display = displayStyles.none;
      }
      for (let j = 0; j < filters.length; j++) {
        if (filters.length > defaultShow && j >= defaultShow) {
          filters[j].classList.add(elementClasses.hide);
          filters[j].classList.add(elementClasses.more);
        }
      }
    });
    this.attachEventListenersToViewMoreLess();
    this.initializeAutoSuggestionLogic();
  }

  getSecurelock() {
    let seculeLockIcon = document.createElement(htmlTags.icon);
    seculeLockIcon.className = elementClasses.secureIcon;
    return seculeLockIcon;
  }

  attachEventListenersToViewMoreLess() {
    document.querySelectorAll(querySelectorFor.filterViewMore).forEach(viewMore => {
      viewMore.onclick = () => {
        let idViewmore = viewMore.parentElement.parentElement.id;
        let viewLess = document.getElementById(idViewmore).querySelector(querySelectorFor.filterViewLess);
        viewLess.classList.remove(elementClasses.hide);
        viewMore.classList.add(elementClasses.hide);
        viewLess.style.display = displayStyles.block;
        document.getElementById(idViewmore).querySelectorAll(querySelectorFor.more).forEach(filterValue => {
          filterValue.classList.remove(elementClasses.hide);
          filterValue.style.display = displayStyles.block;
        });
      };
    });

    document.querySelectorAll(querySelectorFor.filterViewLess).forEach(viewLess => {
      viewLess.onclick = () => {
        let idViewLess = viewLess.parentElement.parentElement.id;
        let viewMore = document.getElementById(idViewLess).querySelector(querySelectorFor.filterViewMore);
        viewMore.classList.remove(elementClasses.hide);
        viewLess.classList.remove(elementClasses.hide);
        viewLess.style.display = displayStyles.none;
        document.getElementById(idViewLess).querySelectorAll(querySelectorFor.more).forEach(filterValue => {
          filterValue.classList.add(elementClasses.hide);
        });
      };
    });
  }

  initializeAutoSuggestionLogic() {
    document.body.onclick = () => {
      document.querySelectorAll(querySelectorFor.autoSuggestionItems).forEach(autoSuggestion => {
        autoSuggestion.classList.add(elementClasses.hide);
        autoSuggestion.classList.remove(elementClasses.show);
      });
    };
    document.querySelectorAll(querySelectorFor.autoSuggestionInput).forEach(input => {
      input.addEventListener(eventListeners.KEY_UP, () => {
        let inputString = input.value.toLowerCase();
        let filterRange = input.dataset.searchboxFor;
        Array.from(document.querySelectorAll(`#custom-${filterRange} .li-div`)).forEach(filter => {
          let filterTitle = filter.dataset.filterTitle.toLowerCase();
          let classList = filter.classList;
          classList.remove(elementClasses.show);
          classList.remove(elementClasses.hide);
          if (filterTitle.indexOf(inputString) !== -1) {
            classList.add(elementClasses.show);
            classList.remove(elementClasses.hide);
          } else {
            classList.remove(elementClasses.show);
            classList.add(elementClasses.hide);
          }
          filter.onclick = () => {
            document.querySelectorAll(querySelectorFor.autoSuggestionItems).forEach(item => {
              item.classList.add(elementClasses.hide);
              item.classList.remove(elementClasses.show);
            });
          };
        });
      });
    });
  }

  registerFacetsEventListener() {
    let self = this;
    document.querySelectorAll(querySelectorFor.facetFilters).forEach(filter => {
      filter.onclick = () => {
        if (filter.checked) {
          filter.parentElement.classList.add(elementClasses.checked);
          let filterNameToRemove = '';
          if (filter.type === 'radio') {
            filterNameToRemove = self.filterRemoved(filter, true);
          }
          self.clickHandler.dispatchEvent(customEvents.filterSelected(filter, filterNameToRemove));
          self.filterAdded(filter);
        } else {
          filter.parentElement.classList.remove(elementClasses.checked);
          self.clickHandler.dispatchEvent(customEvents.filterDeselected(filter.value));
          self.filterRemoved(filter, false);
        }
      };
    });
  }

  registerDateClicks() {
    let self = this;
    let applyDate = document.querySelector(querySelectorFor.applyDate);
    let resetDate = document.querySelector(querySelectorFor.resetDate);
    if (!applyDate || !resetDate) {
      return;
    }
    applyDate.onclick = () => {
      let startDate = self.getStartDate();
      let endDate = self.getEndDate();
      if (startDate === null || startDate === undefined || startDate === '') {
        self.showDateError();
      } else {
        self.hideDateError();
        self.clickHandler.dispatchEvent(customEvents.applyDateClicked(startDate, endDate));
      }


    };

    resetDate.onclick = () => {
      self.clearDateFields();
      document.getElementById(elementIdOf.dateError).classList.add(elementClasses.hide);
      self.hideDateError();
      self.clickHandler.dispatchEvent(customEvents.RESET_DATES_CLICKED);
    };
  }

  getEndDate() {
    let endDate = document.querySelector(querySelectorFor.endDate);
    if (!endDate) {
      return '';
    }
    return endDate.value;
  }

  getStartDate() {
    let startDate = document.querySelector(querySelectorFor.startDate);
    if (!startDate) {
      return '';
    }
    return startDate.value;
  }

  clearDateFields() {
    document.querySelectorAll(querySelectorFor.dateInput).forEach(dateInput => {
      dateInput.value = '';
    });
  }

  hideDateError() {
    let dateError = document.getElementById(elementIdOf.dateError);
    if (dateError) {
      dateError.classList.add(elementClasses.hide);
    }
  }

  showDateError() {
    let dateError = document.getElementById(elementIdOf.dateError);
    if (dateError) {
      dateError.style.display = displayStyles.block;
      dateError.classList.remove(elementClasses.hide);

    }
  }

  hideFilters() {
    let facetFilterContainer = document.querySelector(this.getStartDate() === '' ? querySelectorFor.filterContainer : querySelectorFor.filterContainerWithoutDates);
    facetFilterContainer.style.display = displayStyles.none;
  }

  showFilters() {
    let facetFilterContainer = document.querySelector(querySelectorFor.filterContainer);
    let facetFilterContainerWithoutDates = document.querySelector(querySelectorFor.filterContainerWithoutDates);
    facetFilterContainer.style.display = '';
    facetFilterContainerWithoutDates.style.display = '';
  }
}

const createFilterComponent = (clickHandler, filterRemoved, filterAdded) => {
  return new FilterComponent(clickHandler, filterRemoved, filterAdded);
};

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  module.exports = {createFilterComponent};
}
