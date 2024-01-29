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

(function () {
  var BODY_ELEMENT = document.getElementsByTagName('body')[0];
  var TABS_FIXED_CLASS = 'submittal-builder__tabs--fixed';
  var COMPONENT_CLASS = 'submittal-builder';
  var INTRO_CLASS = COMPONENT_CLASS + '__intro';
  var FILTER_BUTTON_CLASS = COMPONENT_CLASS + '__button--filters-button';
  var PACKAGE_BUTTON_CLASS = COMPONENT_CLASS + '__package-button';
  var FILTER_BUTTON_SELECTOR = '.' + FILTER_BUTTON_CLASS;
  var PACKAGE_BUTTON_SELECTOR = '.' + PACKAGE_BUTTON_CLASS;
  var PACKAGE_SIZE_CLASS = INTRO_CLASS + '-package-size';
  var PACKAGE_SIZE_SELECTOR = '.' + PACKAGE_SIZE_CLASS;
  var VIEW_SUBMITTAL_BUILDER_BUTTON_CLASS = COMPONENT_CLASS + '__button--view-submittal-builder-button';
  var VIEW_SUBMITTAL_BUILDER_BUTTON_SELECTOR = '.' + VIEW_SUBMITTAL_BUILDER_BUTTON_CLASS;
  var DOWNLOAD_BUTTON_INTRO_CLASS = COMPONENT_CLASS + '__button--download-button-intro';
  var DOWNLOAD_BUTTON_INTRO_SELECTOR = '.' + DOWNLOAD_BUTTON_INTRO_CLASS;
  var DOWNLOAD_BUTTON_VIEWING_PACKAGE_CLASS = COMPONENT_CLASS + '__button--download-button-viewing-package';
  var DOWNLOAD_BUTTON_VIEWING_PACKAGE_SELECTOR = '.' + DOWNLOAD_BUTTON_VIEWING_PACKAGE_CLASS;
  var ACTIVE_FILTERS_CONTAINER_CLASS = 'faceted-navigation__active-filters__container';
  var ACTIVE_FILTERS_CONTAINER_SELECTOR = '.' + ACTIVE_FILTERS_CONTAINER_CLASS;
  var escapeAttr = window.App.global.utils.escapeAttr;
  var SUBMITTAL_BUILDER_INTRO_ELEMENT = null;
  var TABS_OFFSET = 0;
  var TABS_HEIGHT = 0;

  var App = window.App || {};
  App.SubmittalIntro = function () {
    _createClass(SubmittalIntro, null, [{
      key: 'markup',
      value: function markup(_ref) {
        var title = _ref.title,
            description = _ref.description,
            instructionsTitle = _ref.instructionsTitle,
            instructions = _ref.instructions,
            filtersButtonText = _ref.filtersButtonText,
            packageButtonText = _ref.packageButtonText,
            downloadButtonText = _ref.downloadButtonText,
            resultCount = _ref.resultCount,
            resultsText = _ref.resultsText,
            ofText = _ref.ofText,
            activeFilters = _ref.activeFilters,
            showingOnlyPackage = _ref.showingOnlyPackage,
            showingEditPackageMode = _ref.showingEditPackageMode,
            finishEditsButtonText = _ref.finishEditsButtonText,
            viewSubmittalBuilderButtonText = _ref.viewSubmittalBuilderButtonText,
            editSubmittalBuilderButtonText = _ref.editSubmittalBuilderButtonText,
            packageSizeValue = _ref.packageSizeValue,
            packageFileSizeValue = _ref.packageFileSizeValue,
            allResultsText = _ref.allResultsText,
            maximumPackageFileSizeValue = _ref.maximumPackageFileSizeValue,
            documentsText = _ref.documentsText,
            clearAllFiltersText = _ref.clearAllFiltersText,
            clearFiltersText = _ref.clearFiltersText,
            activeFilterCount = _ref.activeFilterCount,
            submittalBuilderHelpLinkText = _ref.submittalBuilderHelpLinkText,
            submittalBuilderHelpLink = _ref.submittalBuilderHelpLink;

        var activeFilterElement = '<div class="faceted-navigation-filters ' + ACTIVE_FILTERS_CONTAINER_CLASS + '"\n          data-active-filter-values="' + escapeAttr(activeFilters) + '"\n          data-result-count="' + resultCount + '"\n          data-results-text="' + resultsText + '"\n          data-of-text="' + ofText + '"\n          data-showing-only-package="' + showingOnlyPackage + '"\n          data-items-in-package="' + packageSizeValue + '"\n          data-package-file-size-value="' + packageFileSizeValue + '"\n          data-maximum-package-file-size-value="' + maximumPackageFileSizeValue + '"\n          data-documents-text="' + documentsText + '"\n          data-clear-all-filters-text="' + clearAllFiltersText + '"\n          data-clear-filters-text="' + clearFiltersText + '"\n          data-submittal-builder-help-link-text="' + submittalBuilderHelpLinkText + '"\n          data-submittal-builder-help-link="' + submittalBuilderHelpLink + '">       \n          </div>';

        return '\n        <div class="' + INTRO_CLASS + ' ' + (showingOnlyPackage ? INTRO_CLASS + '--package-only' : '') + ' ' + (showingEditPackageMode ? INTRO_CLASS + '--package-only--edit-mode' : '') + '">\n          <div class="container">\n            <div class="' + INTRO_CLASS + '__container">\n              <span class="icon icon-pages ' + INTRO_CLASS + '__icon"></span>\n              <div class="' + INTRO_CLASS + '__title">\n                <h1 class="' + INTRO_CLASS + '__header">' + title + '</h1>\n                <p class="' + COMPONENT_CLASS + '__body-text ' + INTRO_CLASS + '__title__body-text">' + description + '</p>\n              </div>\n              <div class="' + INTRO_CLASS + '__instructions">\n                <h2 class="' + INTRO_CLASS + '__instructions__title">' + instructionsTitle + '</h2>\n                <p class="' + COMPONENT_CLASS + '__body-text ' + INTRO_CLASS + '__instructions__body-text">' + instructions + '</p>\n              </div>\n            </div>\n          </div>\n\n          <div class="module-tabs ' + COMPONENT_CLASS + '__tabs">\n            <div class="container ' + COMPONENT_CLASS + '__tabs__container">\n              <ul class="module-tabs__list ' + COMPONENT_CLASS + '__tabs__list">\n                <li class="module-tabs__list-item ' + COMPONENT_CLASS + '__tabs__list-item ' + (!showingOnlyPackage ? 'module-tabs__list-item--active' : '') + '">\n                    ' + (!showingOnlyPackage ? '\n                    <button disabled class="button--reset module-tabs__list-link ' + COMPONENT_CLASS + '__tabs__list-link ' + VIEW_SUBMITTAL_BUILDER_BUTTON_CLASS + '">\n                      ' + allResultsText + '\n                    </button>\n                    ' : '\n                    <button class="button--reset module-tabs__list-link ' + COMPONENT_CLASS + '__tabs__list-link ' + VIEW_SUBMITTAL_BUILDER_BUTTON_CLASS + '"\n                      data-analytics-name="submittal-builder-intro-view-submittal-builder">\n                      ' + allResultsText + '\n                    </button>\n                  ') + '\n                </li>\n                <li class="module-tabs__list-item ' + COMPONENT_CLASS + '__tabs__list-item ' + (showingOnlyPackage ? 'module-tabs__list-item--active' : '') + '">\n                ' + (packageSizeValue === 0 || typeof packageSizeValue === 'undefined' || showingOnlyPackage ? '\n                  <button disabled class="button--reset module-tabs__list-link ' + COMPONENT_CLASS + '__tabs__list-link ' + PACKAGE_BUTTON_CLASS + '">\n                    ' + packageButtonText + ' <span class="' + PACKAGE_SIZE_CLASS + '">(' + packageSizeValue + ')&lrm;</span>\n                  </button>\n                  ' : '\n                    <button class="button--reset module-tabs__list-link ' + COMPONENT_CLASS + '__tabs__list-link ' + PACKAGE_BUTTON_CLASS + '"\n                      data-analytics-name="submittal-builder-intro-view-package">\n                      ' + packageButtonText + ' <span class="' + PACKAGE_SIZE_CLASS + '">(' + packageSizeValue + ')&lrm;</span>\n                    </button>\n                ') + '\n                </li>\n                <li class="module-tabs__list-item ' + COMPONENT_CLASS + '__tabs__list-item">\n                ' + (packageSizeValue === 0 || typeof packageSizeValue === 'undefined' ? '\n                  <button disabled class="button--reset module-tabs__list-link ' + COMPONENT_CLASS + '__tabs__list-link ' + DOWNLOAD_BUTTON_INTRO_CLASS + '">\n                    <i class="icon icon-download icon-download--desktop" aria-hidden="true"></i> ' + downloadButtonText + ' <i class="icon icon-download icon-download--mobile" aria-hidden="true"></i> <div>(' + packageFileSizeValue + ')</div>\n                  </button>\n                  ' : '\n                  <button class="button--reset module-tabs__list-link ' + COMPONENT_CLASS + '__tabs__list-link ' + DOWNLOAD_BUTTON_INTRO_CLASS + '"\n                    data-analytics-name="submittal-builder-intro-open-download-modal">\n                    <i class="icon icon-download icon-download--desktop" aria-hidden="true"></i> ' + downloadButtonText + ' <i class="icon icon-download icon-download--mobile" aria-hidden="true"></i> <div>(' + packageFileSizeValue + ')</div>\n                  </button>\n                ') + '\n                </li>\n              </ul>\n            </div>\n          </div>\n\n          <div class="container">\n            <div class="row">\n              <div class="col-md-12">\n                ' + activeFilterElement + '\n              </div>\n              <div class="col-md-6 ' + INTRO_CLASS + '__buttons__container ' + (showingOnlyPackage ? 'hidden' : '') + '">\n                <button\n                  data-analytics-name="submittal-builder-intro-view-filters"\n                  class="b-button b-button__secondary b-button__secondary--light submittal-builder__button ' + FILTER_BUTTON_CLASS + '">\n                  ' + filtersButtonText + '\n                  (' + activeFilterCount + ')\n                </button>\n              </div>\n            </div>\n          </div>\n        </div>\n      ';
      }

      /**
       * The intro section of the submittal builder
       * @constructor
       * @param {object} container - A reference to the SubmittalIntro container's DOM element
      */

    }]);

    function SubmittalIntro(container) {
      _classCallCheck(this, SubmittalIntro);

      // The constructor should only contain the boiler plate code for finding or creating the reference.
      if (typeof container.dataset.ref === 'undefined') {
        this.ref = Math.random();
        App.SubmittalIntro.refs[this.ref] = this;
        container.dataset.ref = this.ref;
        this.init(container);
      } else {
        // If this element has already been instantiated, use the existing reference.
        return App.SubmittalIntro.refs[container.dataset.ref];
      }
    }

    /**
     * @function get activeFilterCount
     * This is the value displayed on the mobile filters button.
     */


    _createClass(SubmittalIntro, [{
      key: 'init',


      /**
       * Initialize the SubmittalIntro class
       * @function init
       * @param {object} container - A reference to the SubmittalIntro container's DOM element so data attributes may be referenced
      */
      value: function init(container) {
        this.container = container;
        this.title = this.container.dataset.title;
        this.description = this.container.dataset.description;
        this.instructionsTitle = this.container.dataset.instructionsTitle;
        this.instructions = this.container.dataset.instructions;
        this.filtersButtonText = this.container.dataset.filtersButtonText;
        this.packageButtonText = this.container.dataset.packageButtonText;
        this.downloadButtonText = this.container.dataset.downloadButtonText;
        this.documentsText = this.container.dataset.documentsText;
        this.viewSubmittalBuilderButtonText = this.container.dataset.viewSubmittalBuilderButtonText;
        this.editSubmittalBuilderButtonText = this.container.dataset.editSubmittalBuilderButtonText;
        this.allResultsText = this.container.dataset.allResultsText;
        this.ofText = this.container.dataset.ofText;
        this.clearAllFiltersText = this.container.dataset.clearAllFiltersText;
        this.clearFiltersText = this.container.dataset.clearFiltersText;
        this.activeFilterValues = [];
        this.showingOnlyPackage = false;
        this.showingEditPackageMode = false;
        this.packageSizeValue = 0;
        this.packageFileSizeValue = '0 mb';
        this.maximumPackageFileSizeValue = 0;
        this.tabsOffset = 0;
        this.submittalBuilderHelpLinkText = this.container.dataset.submittalBuilderHelpLinkText;
        this.submittalBuilderHelpLink = this.container.dataset.submittalBuilderHelpLink;

        this.render();
      }

      /**
       * @function set activeFilters Sets the active filters to be rendered in the desktop view.
       */

    }, {
      key: 'showOnlyPackage',
      value: function showOnlyPackage() {
        this.showingOnlyPackage = true;

        this.render();
      }
    }, {
      key: 'hideOnlyPackage',
      value: function hideOnlyPackage() {
        this.showingOnlyPackage = false;

        this.render();
      }
    }, {
      key: 'showEditPackageMode',
      value: function showEditPackageMode() {
        this.showingEditPackageMode = true;

        this.render();
      }
    }, {
      key: 'hideEditPackageMode',
      value: function hideEditPackageMode() {
        this.showingEditPackageMode = false;

        this.render();
      }

      /**
      * @function handleScroll - controls sticking and unsticking the .submittal-builder__tabs element
      * @param {object} event
      */

    }, {
      key: 'handleScroll',
      value: function handleScroll(event) {
        var scrollTop = typeof window.scrollY === 'undefined' ? window.pageYOffset : window.scrollY; // IE11 doesn't have window.scrollY property

        if (scrollTop > TABS_OFFSET) {
          BODY_ELEMENT.classList.add(TABS_FIXED_CLASS);
          // add TABS_HEIGHT as bottom margin to .submittal-builder__intro to prevent jumping when tabs become sticky
          SUBMITTAL_BUILDER_INTRO_ELEMENT.style.marginBottom = TABS_HEIGHT + 'px';
        } else {
          BODY_ELEMENT.classList.remove(TABS_FIXED_CLASS);
          SUBMITTAL_BUILDER_INTRO_ELEMENT.style.marginBottom = '';
        }
      }

      /**
      * @function getTabsOffset - get the .submittal-builder__tabs element height and offset, but only set it once
      */

    }, {
      key: 'getTabsOffset',
      value: function getTabsOffset() {
        TABS_HEIGHT = TABS_HEIGHT === 0 ? document.querySelector('.' + COMPONENT_CLASS + '__tabs').offsetHeight : TABS_HEIGHT;
        TABS_OFFSET = TABS_OFFSET === 0 ? document.querySelector('.' + COMPONENT_CLASS + '__tabs').offsetTop + TABS_HEIGHT * .5 : TABS_OFFSET;
      }
    }, {
      key: 'render',
      value: function render() {
        var _this = this;

        this.container.innerHTML = App.SubmittalIntro.markup(this);

        this.filterButton = this.container.querySelector(FILTER_BUTTON_SELECTOR);
        this.packageButton = this.container.querySelector(PACKAGE_BUTTON_SELECTOR);
        this.packageSizeElement = this.container.querySelector(PACKAGE_SIZE_SELECTOR);
        this.viewSubmittalBuilderButton = this.container.querySelector(VIEW_SUBMITTAL_BUILDER_BUTTON_SELECTOR);
        this.downloadButtonIntro = this.container.querySelector(DOWNLOAD_BUTTON_INTRO_SELECTOR);
        this.downloadButtonViewingPackage = this.container.querySelector(DOWNLOAD_BUTTON_VIEWING_PACKAGE_SELECTOR);

        SUBMITTAL_BUILDER_INTRO_ELEMENT = this.container.querySelector('.submittal-builder__intro');

        // Only apply sticky tabs behaviors on mobile
        if (window.matchMedia(App.global.constants.MEDIA_QUERIES.MOBILE).matches) {
          this.getTabsOffset();

          var lazyScroll = App.global.utils.throttle(this.handleScroll, 15);

          window.onscroll = lazyScroll;
        }

        this.filterButton.addEventListener('click', function () {
          return _this.container.dispatchEvent(new CustomEvent('filterButtonClicked'));
        });

        this.viewSubmittalBuilderButton.addEventListener('click', function () {
          return _this.container.dispatchEvent(new CustomEvent('closePackageViewClicked'));
        });

        this.packageButton.addEventListener('click', function () {
          return _this.container.dispatchEvent(new CustomEvent('packageButtonClicked'));
        });

        this.downloadButtonIntro.addEventListener('click', function () {
          return _this.container.dispatchEvent(new CustomEvent('downloadButtonClicked'));
        });

        this.submittalHeaderComponent = new App.SubmittalHeader(this.container.querySelector(ACTIVE_FILTERS_CONTAINER_SELECTOR));

        this.submittalHeaderComponent.addEventListener('filterRemoved', function (_ref2) {
          var detail = _ref2.detail;
          return _this.container.dispatchEvent(new CustomEvent('filterRemoved', { detail: detail }));
        });

        this.container.querySelector(ACTIVE_FILTERS_CONTAINER_SELECTOR).addEventListener('clearAllFilters', function () {
          _this.activeFilters = [];
          _this.container.dispatchEvent(new CustomEvent('clearAllFilters'));
        });
      }

      /**
       * @function addEventListener - A method to allow clients to add event listeners
       *  to this component. Calls the addEventListener method of this components containing element.
       */

    }, {
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
    }, {
      key: 'activeFilterCount',
      get: function get() {
        return this.activeFilters.length;
      }
    }, {
      key: 'activeFilters',
      set: function set(activeFilterValues) {
        this.activeFilterValues = activeFilterValues;

        this.render();
      },
      get: function get() {
        return this.activeFilterValues ? this.activeFilterValues : [];
      }

      /**
       * @function packageSize Updates the UI to show the package size as an integer.
      */

    }, {
      key: 'packageSize',
      set: function set(size) {
        this.packageSizeValue = size;

        this.render();
      }

      /**
       * @function packageFileSize Updates the UI to show the package file size as a string.
      */

    }, {
      key: 'packageFileSize',
      set: function set(fileSizeValue) {
        this.packageFileSizeValue = fileSizeValue;

        this.render();
      }

      /**
       * @function set resultCount - Updates the SubmittalHeader component
       * with the updated result count.
      */

    }, {
      key: 'resultCount',
      set: function set(count) {
        this.resultCountValue = count;
        this.submittalHeaderComponent.resultCount = count;
      },
      get: function get() {
        return this.resultCountValue;
      }
    }]);

    return SubmittalIntro;
  }();

  App.SubmittalIntro.refs = {};
})();