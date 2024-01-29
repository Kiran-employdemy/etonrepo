(function () {
  const BODY_ELEMENT = document.getElementsByTagName('body')[0];
  const TABS_FIXED_CLASS = 'submittal-builder__tabs--fixed';
  const COMPONENT_CLASS = 'submittal-builder';
  const INTRO_CLASS = COMPONENT_CLASS + '__intro';
  const FILTER_BUTTON_CLASS = COMPONENT_CLASS + '__button--filters-button';
  const PACKAGE_BUTTON_CLASS = COMPONENT_CLASS + '__package-button';
  const FILTER_BUTTON_SELECTOR = '.' + FILTER_BUTTON_CLASS;
  const PACKAGE_BUTTON_SELECTOR = '.' + PACKAGE_BUTTON_CLASS;
  const PACKAGE_SIZE_CLASS = INTRO_CLASS + '-package-size';
  const PACKAGE_SIZE_SELECTOR = '.' + PACKAGE_SIZE_CLASS;
  const VIEW_SUBMITTAL_BUILDER_BUTTON_CLASS = COMPONENT_CLASS + '__button--view-submittal-builder-button';
  const VIEW_SUBMITTAL_BUILDER_BUTTON_SELECTOR = '.' + VIEW_SUBMITTAL_BUILDER_BUTTON_CLASS;
  const DOWNLOAD_BUTTON_INTRO_CLASS = COMPONENT_CLASS + '__button--download-button-intro';
  const DOWNLOAD_BUTTON_INTRO_SELECTOR = '.' + DOWNLOAD_BUTTON_INTRO_CLASS;
  const DOWNLOAD_BUTTON_VIEWING_PACKAGE_CLASS = COMPONENT_CLASS + '__button--download-button-viewing-package';
  const DOWNLOAD_BUTTON_VIEWING_PACKAGE_SELECTOR = '.' + DOWNLOAD_BUTTON_VIEWING_PACKAGE_CLASS;
  const ACTIVE_FILTERS_CONTAINER_CLASS = 'faceted-navigation__active-filters__container';
  const ACTIVE_FILTERS_CONTAINER_SELECTOR = '.' + ACTIVE_FILTERS_CONTAINER_CLASS;
  const escapeAttr = window.App.global.utils.escapeAttr;
  let SUBMITTAL_BUILDER_INTRO_ELEMENT = null;
  let TABS_OFFSET = 0;
  let TABS_HEIGHT = 0;

  let App = window.App || {};
  App.SubmittalIntro = class SubmittalIntro {
    static markup({title, description, instructionsTitle, instructions, filtersButtonText, packageButtonText, downloadButtonText, resultCount, resultsText, ofText, activeFilters, showingOnlyPackage, showingEditPackageMode, finishEditsButtonText, viewSubmittalBuilderButtonText, editSubmittalBuilderButtonText, packageSizeValue, packageFileSizeValue, allResultsText, maximumPackageFileSizeValue, documentsText, clearAllFiltersText, clearFiltersText, activeFilterCount, submittalBuilderHelpLinkText, submittalBuilderHelpLink}) {
      let activeFilterElement = `<div class="faceted-navigation-filters ${ ACTIVE_FILTERS_CONTAINER_CLASS }"
          data-active-filter-values="${ escapeAttr(activeFilters) }"
          data-result-count="${ resultCount }"
          data-results-text="${ resultsText }"
          data-of-text="${ ofText }"
          data-showing-only-package="${ showingOnlyPackage }"
          data-items-in-package="${ packageSizeValue }"
          data-package-file-size-value="${ packageFileSizeValue }"
          data-maximum-package-file-size-value="${ maximumPackageFileSizeValue }"
          data-documents-text="${ documentsText }"
          data-clear-all-filters-text="${ clearAllFiltersText }"
          data-clear-filters-text="${ clearFiltersText }"
          data-submittal-builder-help-link-text="${ submittalBuilderHelpLinkText }"
          data-submittal-builder-help-link="${ submittalBuilderHelpLink }">       
          </div>`;

      return `
        <div class="${ INTRO_CLASS } ${ showingOnlyPackage ? INTRO_CLASS + '--package-only' : '' } ${ showingEditPackageMode ? INTRO_CLASS + '--package-only--edit-mode' : '' }">
          <div class="container">
            <div class="${ INTRO_CLASS }__container">
              <span class="icon icon-pages ${ INTRO_CLASS }__icon"></span>
              <div class="${ INTRO_CLASS }__title">
                <h1 class="${ INTRO_CLASS }__header">${ title }</h1>
                <p class="${ COMPONENT_CLASS }__body-text ${ INTRO_CLASS }__title__body-text">${ description }</p>
              </div>
              <div class="${ INTRO_CLASS }__instructions">
                <h2 class="${ INTRO_CLASS }__instructions__title">${ instructionsTitle }</h2>
                <p class="${ COMPONENT_CLASS }__body-text ${ INTRO_CLASS }__instructions__body-text">${ instructions }</p>
              </div>
            </div>
          </div>

          <div class="module-tabs ${ COMPONENT_CLASS }__tabs">
            <div class="container ${ COMPONENT_CLASS }__tabs__container">
              <ul class="module-tabs__list ${ COMPONENT_CLASS }__tabs__list">
                <li class="module-tabs__list-item ${ COMPONENT_CLASS }__tabs__list-item ${ !showingOnlyPackage ? 'module-tabs__list-item--active' : '' }">
                    ${ !showingOnlyPackage ? `
                    <button disabled class="button--reset module-tabs__list-link ${ COMPONENT_CLASS }__tabs__list-link ${ VIEW_SUBMITTAL_BUILDER_BUTTON_CLASS }">
                      ${ allResultsText }
                    </button>
                    ` : `
                    <button class="button--reset module-tabs__list-link ${ COMPONENT_CLASS }__tabs__list-link ${ VIEW_SUBMITTAL_BUILDER_BUTTON_CLASS }"
                      data-analytics-name="submittal-builder-intro-view-submittal-builder">
                      ${ allResultsText }
                    </button>
                  ` }
                </li>
                <li class="module-tabs__list-item ${ COMPONENT_CLASS }__tabs__list-item ${ showingOnlyPackage ? 'module-tabs__list-item--active' : '' }">
                ${ packageSizeValue === 0 || typeof packageSizeValue === 'undefined' || showingOnlyPackage ? `
                  <button disabled class="button--reset module-tabs__list-link ${ COMPONENT_CLASS }__tabs__list-link ${ PACKAGE_BUTTON_CLASS }">
                    ${ packageButtonText } <span class="${ PACKAGE_SIZE_CLASS }">(${ packageSizeValue })&lrm;</span>
                  </button>
                  ` : `
                    <button class="button--reset module-tabs__list-link ${ COMPONENT_CLASS }__tabs__list-link ${ PACKAGE_BUTTON_CLASS }"
                      data-analytics-name="submittal-builder-intro-view-package">
                      ${ packageButtonText } <span class="${ PACKAGE_SIZE_CLASS }">(${ packageSizeValue })&lrm;</span>
                    </button>
                ` }
                </li>
                <li class="module-tabs__list-item ${ COMPONENT_CLASS }__tabs__list-item">
                ${ packageSizeValue === 0 || typeof packageSizeValue === 'undefined' ? `
                  <button disabled class="button--reset module-tabs__list-link ${ COMPONENT_CLASS }__tabs__list-link ${ DOWNLOAD_BUTTON_INTRO_CLASS }">
                    <i class="icon icon-download icon-download--desktop" aria-hidden="true"></i> ${ downloadButtonText } <i class="icon icon-download icon-download--mobile" aria-hidden="true"></i> <div>(${ packageFileSizeValue })</div>
                  </button>
                  ` : `
                  <button class="button--reset module-tabs__list-link ${ COMPONENT_CLASS }__tabs__list-link ${ DOWNLOAD_BUTTON_INTRO_CLASS }"
                    data-analytics-name="submittal-builder-intro-open-download-modal">
                    <i class="icon icon-download icon-download--desktop" aria-hidden="true"></i> ${ downloadButtonText } <i class="icon icon-download icon-download--mobile" aria-hidden="true"></i> <div>(${ packageFileSizeValue })</div>
                  </button>
                ` }
                </li>
              </ul>
            </div>
          </div>

          <div class="container">
            <div class="row">
              <div class="col-md-12">
                ${ activeFilterElement }
              </div>
              <div class="col-md-6 ${ INTRO_CLASS }__buttons__container ${ showingOnlyPackage ? 'hidden' : '' }">
                <button
                  data-analytics-name="submittal-builder-intro-view-filters"
                  class="b-button b-button__secondary b-button__secondary--light submittal-builder__button ${ FILTER_BUTTON_CLASS }">
                  ${ filtersButtonText }
                  (${ activeFilterCount })
                </button>
              </div>
            </div>
          </div>
        </div>
      `;
    }

    /**
     * The intro section of the submittal builder
     * @constructor
     * @param {object} container - A reference to the SubmittalIntro container's DOM element
    */
    constructor(container) {
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
    get activeFilterCount() {
      return this.activeFilters.length;
    }

    /**
     * Initialize the SubmittalIntro class
     * @function init
     * @param {object} container - A reference to the SubmittalIntro container's DOM element so data attributes may be referenced
    */
    init(container) {
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
    set activeFilters(activeFilterValues) {
      this.activeFilterValues = activeFilterValues;

      this.render();
    }

    get activeFilters() {
      return this.activeFilterValues ? this.activeFilterValues : [];
    }

    /**
     * @function packageSize Updates the UI to show the package size as an integer.
    */
    set packageSize(size) {
      this.packageSizeValue = size;

      this.render();
    }

    /**
     * @function packageFileSize Updates the UI to show the package file size as a string.
    */
    set packageFileSize(fileSizeValue) {
      this.packageFileSizeValue = fileSizeValue;

      this.render();
    }

    /**
     * @function set resultCount - Updates the SubmittalHeader component
     * with the updated result count.
    */
    set resultCount(count) {
      this.resultCountValue = count;
      this.submittalHeaderComponent.resultCount = count;
    }

    get resultCount() {
      return this.resultCountValue;
    }

    showOnlyPackage() {
      this.showingOnlyPackage = true;

      this.render();
    }

    hideOnlyPackage() {
      this.showingOnlyPackage = false;

      this.render();
    }

    showEditPackageMode() {
      this.showingEditPackageMode = true;

      this.render();
    }

    hideEditPackageMode() {
      this.showingEditPackageMode = false;

      this.render();
    }

    /**
    * @function handleScroll - controls sticking and unsticking the .submittal-builder__tabs element
    * @param {object} event
    */
    handleScroll (event) {
      const scrollTop = typeof window.scrollY === 'undefined' ? window.pageYOffset : window.scrollY; // IE11 doesn't have window.scrollY property

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
    getTabsOffset () {
      TABS_HEIGHT = TABS_HEIGHT === 0 ? document.querySelector('.' + COMPONENT_CLASS + '__tabs').offsetHeight : TABS_HEIGHT;
      TABS_OFFSET = TABS_OFFSET === 0 ? document.querySelector('.' + COMPONENT_CLASS + '__tabs').offsetTop + (TABS_HEIGHT * .5) : TABS_OFFSET;
    }

    render() {
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

        const lazyScroll = App.global.utils.throttle(this.handleScroll, 15);

        window.onscroll = lazyScroll;
      }

      this.filterButton.addEventListener('click', () =>
        this.container.dispatchEvent(new CustomEvent('filterButtonClicked')));

      this.viewSubmittalBuilderButton.addEventListener('click', () =>
        this.container.dispatchEvent(new CustomEvent('closePackageViewClicked')));

      this.packageButton.addEventListener('click', () =>
        this.container.dispatchEvent(new CustomEvent('packageButtonClicked')));

      this.downloadButtonIntro.addEventListener('click', () =>
        this.container.dispatchEvent(new CustomEvent('downloadButtonClicked')));

      this.submittalHeaderComponent = new App.SubmittalHeader(this.container.querySelector(ACTIVE_FILTERS_CONTAINER_SELECTOR));

      this.submittalHeaderComponent.addEventListener('filterRemoved', ({detail}) =>
        this.container.dispatchEvent(new CustomEvent('filterRemoved', { detail })));

      this.container.querySelector(ACTIVE_FILTERS_CONTAINER_SELECTOR).addEventListener('clearAllFilters', () => {
        this.activeFilters = [];
        this.container.dispatchEvent(new CustomEvent('clearAllFilters'));
      });
    }

    /**
     * @function addEventListener - A method to allow clients to add event listeners
     *  to this component. Calls the addEventListener method of this components containing element.
     */
    addEventListener() {
      return this.container.addEventListener.apply(this.container, arguments);
    }

    /**
     * @function removeEventListener - A method to allow clients to remove event listeners
     *  from this component. Calls the removeEventListener method of this components containing element.
     */
    removeEventListener() {
      return this.container.removeEventListener.apply(this.container, arguments);
    }
  };

  App.SubmittalIntro.refs = { };
})();
