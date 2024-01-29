(function () {
  const SUBMITTAL_CLASS = 'submittal-builder';
  const FLOATING_BUTTONS_CLASS = SUBMITTAL_CLASS + '__floating-button__container';
  const FLOATING_BUTTON_CLASS = SUBMITTAL_CLASS + '__floating-button';
  const DOWNLOAD_BUTTON_CLASS = SUBMITTAL_CLASS + '__download-button';
  const PREVIEW_BUTTON_CLASS = SUBMITTAL_CLASS + '__preview-button';
  const BACK_TO_TOP_BUTTON_CLASS = SUBMITTAL_CLASS + '__back-to-top';
  const BACK_TO_TOP_BUTTON_SELECTOR = '.' + BACK_TO_TOP_BUTTON_CLASS;
  const SUBMITTAL_SELECTOR = '.' + SUBMITTAL_CLASS;
  const INTRO_SELECTOR = SUBMITTAL_SELECTOR + '__intro';
  const FILTERS_SELECTOR = SUBMITTAL_SELECTOR + '__filters';
  const RESULTS_SELECTOR = SUBMITTAL_SELECTOR + '__results';
  const DOWNLOAD_SELECTOR = SUBMITTAL_SELECTOR + '__download';
  const DOWNLOAD_BUTTON_SELECTOR = '.' + DOWNLOAD_BUTTON_CLASS;
  const PREVIEW_BUTTON_SELECTOR = '.' + PREVIEW_BUTTON_CLASS;
  const WELCOME_MESSAGE_CLASS = SUBMITTAL_CLASS + '__welcome-message';
  const WELCOME_MESSAGE_SELECTOR = '.' + WELCOME_MESSAGE_CLASS;

  let App = window.App || {};
  App.SubmittalBuilder = class SubmittalBuilder {
    static markup() {
      /* This UI component has no template, it expects the submittal intro, filters,
       * package, results,  and download UI components to exist under the container. */
      return '';
    }

    constructor(container) {
      // The constructor should only contain the boiler plate code for finding or creating the reference.
      if (typeof container.dataset.ref === 'undefined') {
        this.ref = Math.random();
        App.SubmittalBuilder.refs[this.ref] = this;
        container.dataset.ref = this.ref;
        this.init(container);
      } else {
        // If this element has already been instantiated, use the existing reference.
        return App.SubmittalBuilder.refs[container.dataset.ref];
      }
    }

    init(container) {
      this.container = container;
      const dataset = this.container.dataset;
      this.resultServletUrl = dataset.resultServletUrl;
      this.downloadServletUrl = dataset.downloadServletUrl;
      this.emailServletUrl = dataset.emailServletUrl;
      this.totalCount = parseInt(dataset.totalCount);
      this.documentsPluralText = dataset.documentsPluralText;
      this.documentsSingularText = dataset.documentsSingularText;
      this.resultsPluralText = dataset.resultsPluralText;
      this.resultsSingularText = dataset.resultsSingularText;
      this.intro = new App.SubmittalIntro(this.container.querySelector(INTRO_SELECTOR));

      this.filters = new App.Filters(this.container.querySelector(FILTERS_SELECTOR));

      this.results = new App.SubmittalResults(this.container.querySelector(RESULTS_SELECTOR));
      this.download = new App.SubmittalDownload(this.container.querySelector(DOWNLOAD_SELECTOR));

      this.filters.resultCount = this.totalCount;
      this.intro.resultCount = this.totalCount;
      this.intro.resultsText = this.resultsText;
      this.intro.documentsText = this.documentsText;

      this.intro.maximumPackageFileSizeValue = this.download.formatPackageSize(this.results.zipSizeLimit, 0);
      this.results.documentsText = this.documentsText;
      this.results.resultsText = this.resultsText;
      this.results.resultCount = this.totalCount;
      this.results.hideResultsList = true;

      this.submittalScope = (dataset.submittalScope ? JSON.parse(dataset.submittalScope) : {}).families;
      if (!this.submittalScope) {this.submittalScope = [];}

      this.submittalAttributes = (dataset.submittalAttributes ? JSON.parse(dataset.submittalAttributes) : {}).attributes;
      if (!this.submittalAttributes) {this.submittalAttributes = [];}

      this.results.propertyList = this.submittalAttributes;
      this.filters.propertyList = this.submittalAttributes;

      if (typeof this.container.dataset.filters !== 'undefined') {
        this.filters.filterList = JSON.parse(this.container.dataset.filters);
      }

      if (typeof this.container.dataset.results !== 'undefined') {
        this.results.resultList = JSON.parse(this.container.dataset.results);
      }

      this.intro.activeFilters = this.filters.activeFilterList;

      this.servletReady = fetch('/libs/granite/csrf/token.json', {
        credentials: 'same-origin'
      })
      .then(response => response.json())
      .then(json => json.token);

      this.render();
    }

    /**
     * @function openDownloadDialog Closes the package and download components
     * and opens the filters component.
    */
    openFilters() {
      if (!this.filters.isOpen) {
        this.results.showAllResults();
        this.download.close();
        this.filters.open();
      }
    }

    /**
     * @function openPackage Closes the results, filters and download components
     * and opens the package component.
    */
    openPackage() {
      if (!this.results.showingOnlyPackage) {
        this.updateWelcomeMessageVisibility();
        this.results.showOnlyPackage();
        this.filters.close();
        this.download.close();
        this.intro.showOnlyPackage();
      }
    }

    /**
     * @function closePackage Closes the download and package components and opens
     * the results and filters components.
    */
    closePackage() {
      if (this.results.showingOnlyPackage) {
        this.results.showAllResults();
        this.download.close();
        this.intro.hideOnlyPackage();

        /**
         * Only open filters for non-mobile devices
         */
        if (!window.matchMedia(App.global.constants.MEDIA_QUERIES.MOBILE).matches) {
          this.filters.open();
        }
      }
    }

    /**
     * @function openDownloadDialog Closes the filters and package components
     * and opens the download component.
    */
    openDownloadDialog() {
      if (!this.download.isOpen) {
        // Should the filters only be closed on mobile?
        // this.filters.close();
        // this.results.showAllResults();
        this.download.open();
      }
    }

    updateResultCount() {
      if (this.results.size === 0) {
        this.downloadButton.setAttribute('disabled', true);
      } else {
        this.downloadButton.removeAttribute('disabled');
      }
      this.documentCountElement.innerHTML = this.results.files.length;
      this.intro.packageSize = this.results.files.length;

      this.intro.packageFileSize = this.download.formatPackageSize(this.results.estimatedZipSize);

      this.intro.resultsText = this.resultsText;
      this.intro.documentsText = this.documentsText;
      this.results.documentsText = this.documentsText;
      this.results.resultsText = this.resultsText;
      this.download.packageSize = this.results.estimatedZipSize;

      // update datalayer
      this.updateDataLayer();
      this.updateFloatingPreviewButtonDisabledState();
    }

    get resultsText() {
      return typeof this.results.resultCountValue === 'undefined' || this.results.resultCountValue > 1 ? this.resultsPluralText : this.resultsSingularText;
    }

    get documentsText() {
      return this.results.files.length === 1 && this.filters.activeFilterList.length > 0 ? this.documentsSingularText : this.documentsPluralText;
    }

    /**
     * @function downloadPackage If the 'option' parameter is 'download' then
     * will initiate the package download based on the package component. Otherwise
     * it will send a request to the server to send an email with the package.
    */
    downloadPackage(option) {
      const fileName = this.download.fileName;
      const mergeAssetsFileName = this.download.mergeAssetsFileName;
      if (option === 'email') {
        let emailToRecipients = [document.getElementById('download-email').value];
        let requestParams = {
          fileName: fileName,
          mergeAssetsFileName: mergeAssetsFileName,
          emailToRecipients: emailToRecipients,
          assetPaths: this.results.packageList
        };
        $.ajax({
          type: 'POST',
          url: this.emailServletUrl,
          data: JSON.stringify(requestParams),
          headers: {'Content-Type': 'application/json'},
          success: function(resultData) {

          }
        });
      }
      if (option === 'download') {
        let requestParams = {
          fileName: fileName,
          mergeAssetsFileName: mergeAssetsFileName,
          option: option,
          assetPaths: this.results.packageList
        };
        $.ajax({
          type: 'POST',
          url: this.downloadServletUrl,
          data: JSON.stringify(requestParams),
          headers: {'Content-Type': 'application/json'},
          success: function(resultData) {
            if (resultData) {
              let byteCharacters = atob(resultData);
              let byteNumbers = new Array(byteCharacters.length);
              for (let i = 0; i < byteCharacters.length; i++) {
                byteNumbers[i] = byteCharacters.charCodeAt(i);
              }
              let byteArray = new Uint8Array(byteNumbers);
              let blob;
              let fileType = 'application/zip';
              let fileNameExt = fileName.concat('.zip');
              if (window.navigator.msSaveBlob) {
                blob = new Blob([byteArray], { type: fileType });
                window.navigator.msSaveBlob(blob, fileNameExt);
              } else {
                let blob = new Blob([byteArray], { type: fileType });
                let link = document.createElement('a');
                link.href = window.URL.createObjectURL(blob);
                link.download = fileNameExt;
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
              }
            }
          }
        });
      }
    }

    loadResults(startingRecord) {
      const scope = this.submittalScope.map(family => family.id);
      const facets = this.submittalAttributes.map(attribute => attribute.name);
      const activeIds = this.filters.activeFilterList.map(filter => filter.id);
      const url = `${ this.resultServletUrl }?startingRecord=${ encodeURIComponent(startingRecord) }&activeFilters=${ encodeURIComponent(JSON.stringify(activeIds)) }&scope=${ encodeURIComponent(JSON.stringify(scope)) }&facets=${ encodeURIComponent(JSON.stringify(facets)) }&sortOrder=${ encodeURIComponent(JSON.stringify(this.results.sortOrder)) }`;

      return fetch(url, { credentials: 'same-origin' })
      .then(response => response.json());
    }

    loadMore(startingRecord) {
      this.loadResults(startingRecord)
      .then(({results}) => this.results.resultList = this.results.resultList.concat(results));
    }

    updateResults() {
      this.loadResults(0)
      .then(({filters, results, totalCount}) => {
        this.totalCount = totalCount;
        this.filters.resultCount = this.totalCount;
        this.intro.resultCount = this.totalCount;
        this.results.resultCount = this.totalCount;
        this.results.resultsText = this.resultsText;
        this.results.resultList = results;
        this.filters.filterList = filters;
        this.intro.resultsText = this.resultsText;
        this.intro.documentsText = this.documentsText;
        this.intro.activeFilters = this.filters.activeFilterList;
        this.updateResultCount();
      });
    }
    /**
     * @function getFileExtensionFromUrl
     * @param {string} url
     * @returns {string} returns the file extension
    */
    getFileExtensionFromUrl(url) {
      return url.split('.').pop();
    }
    /**
     * @function getFileNameFromUrl
     * @param {object} result an object that contains all available data on the file
     * @returns {string} returns the result.title if it has a value, otherwise returns the filename from the URL
    */
    getFileNameFromUrl(result) {
      const titleKeyValue = typeof result.title !== 'undefined' ? result.title : '';
      const titleFromUrl = result.url.split('/').pop().split('.').shift();
      return titleKeyValue !== '' ? titleKeyValue : titleFromUrl;
    }
    /**
     * @function updateDataLayer creates an array of results with a simplified data structure for use by the dataLayer
    */
    updateDataLayer() {
      const simplifiedFileInfo = this.results.files.map(result => {
        return {
          'data-analytics-assetId': result.url,
          'data-analytics-assetTitle': this.getFileNameFromUrl(result),
          'data-analytics-assetType': this.getFileExtensionFromUrl(result.url)
        };
      });
      /**
       * Temporarily disabling eslint to access the global dataLayer object defined in
       * ui.apps/src/main/content/jcr_root/apps/eaton/components/structure/eaton-edit-template-page/datalayer.html
       */
      /* eslint-disable no-undef */
      dataLayer.submittalBuilderPackage = simplifiedFileInfo;
      /* eslint-enable */
    }
    /**
     * @function updateWelcomeMessageVisibility
     * hide the welcome message if a filter has been chosen
     * show the welcome message when no filter has been chosen
    */
    updateWelcomeMessageVisibility() {
      if (this.filters.activeFilterList.length > 0 || this.results.showingOnlyPackage || this.results.size > 0) {
        this.welcomeMessage.classList.add('hidden');
        this.results.hideResultsList = false;
      } else {
        this.welcomeMessage.classList.remove('hidden');
        this.results.hideResultsList = true;
      }
    }
    /**
     * @function updateFloatingPreviewButtonDisabledState
     * enable or disable the floating preview button's disabled state to match the tab version
    */
    updateFloatingPreviewButtonDisabledState() {
      this.container.querySelector(PREVIEW_BUTTON_SELECTOR).disabled = this.results.files.length === 0 ? 'disabled' : '';
    }

    clearAllFilters() {
      this.filters.activeFilterList.map((filter) => this.filters.deactiveFilter(filter.name));
      this.filters.selectedFilters = [];
      this.updateResults();
    }

    clearFilter(filter) {
      let removeFilterIds = filter.activeFilterValues.map(filterValue => filterValue.id);
      let newActiveFilters = this.filters.selectedFilters;
      // remove all active filter values from the given filter and updated selectedFilter list
      Object.keys(this.filters.selectedFilters).map(key => removeFilterIds.indexOf(newActiveFilters[key].id) > -1 && delete newActiveFilters[key]);
      this.filters.selectedFilters = newActiveFilters;
      this.updateResults();
    }

    render() {
      const floatingButtons = document.createElement('div');

      floatingButtons.classList.add(FLOATING_BUTTONS_CLASS);
      floatingButtons.innerHTML = `

          <button data-analytics-name="submittal-builder-floating-buttons-open-download-modal"
            ${ this.results.size === 0 ? 'disabled' : '' } class="button--reset ${ FLOATING_BUTTON_CLASS } ${ DOWNLOAD_BUTTON_CLASS }"  style="display:none">
            <i class="icon icon-download ${ FLOATING_BUTTON_CLASS }__icon" aria-hidden="true"></i>
          </button>
          <button data-analytics-name="submittal-builder-floating-buttons-view-package"
            class="button--reset ${ FLOATING_BUTTON_CLASS } ${ PREVIEW_BUTTON_CLASS }"
            ${ this.filters.activeFilterList.length === 0 ? 'disabled' : '' } style="display:none">
            <span class="${ PREVIEW_BUTTON_CLASS }__document-count">${ this.results.size }</span>
            <i class="icon icon-folder ${ FLOATING_BUTTON_CLASS }__icon" aria-hidden="true"></i>
          </button>
          <button class="back-to-top button--reset sumb" data-scroll-to="body" >
            <span class="sr-only">Back to top of the page</span>
            <i class="icon icon-chevron-up ${ FLOATING_BUTTON_CLASS }__icon" aria-hidden="true"></i>
          </button>


        `;
      this.container.appendChild(floatingButtons);

      this.documentCountElement = floatingButtons.querySelector(`.${ PREVIEW_BUTTON_CLASS }__document-count`);
      this.documentCountElement = floatingButtons.querySelector(`.${ PREVIEW_BUTTON_CLASS }__document-count`);
      this.downloadButton = this.container.querySelector(DOWNLOAD_BUTTON_SELECTOR);
      this.previewButton = this.container.querySelector(PREVIEW_BUTTON_SELECTOR);
      this.backToTopButton = this.container.querySelector(BACK_TO_TOP_BUTTON_SELECTOR);
      this.welcomeMessage = this.container.querySelector(WELCOME_MESSAGE_SELECTOR);

      this.addEventListeners();
    }

    addEventListeners() {
      this.intro.addEventListener('filterButtonClicked', () => this.openFilters());
      this.intro.addEventListener('packageButtonClicked', () => {
        if (this.results.showingOnlyPackage) {
          this.closePackage();
        } else {
          this.openPackage();
        }
      });

      this.intro.addEventListener('closePackageViewClicked', () => {
        this.closePackage();
        this.intro.hideEditPackageMode();
      });

      this.intro.addEventListener('editSubmittalPackageButtonClicked', () => {
        this.results.showEditPackageMode();
        this.intro.showEditPackageMode();
      });

      this.intro.addEventListener('finishEditsButtonClicked', () => {
        this.results.hideEditPackageMode();
        this.intro.hideEditPackageMode();
      });

      this.intro.addEventListener('filterRemoved', ({detail: {name}}) => {
        this.filters.deactiveFilter(name);
        this.updateResults();
      });

      this.intro.addEventListener('downloadButtonClicked', () => this.openDownloadDialog());
      this.downloadButton.addEventListener('click', () => this.openDownloadDialog());
      this.previewButton.addEventListener('click', () => this.openPackage());
      this.download.addEventListener('packageDownloaded', () => this.downloadPackage('download'));
      this.download.addEventListener('packageEmailed', () => this.downloadPackage('email'));
      this.results.addEventListener('resultSelected', () => this.updateResultCount());
      this.results.addEventListener('resultDeselected', () => this.updateResultCount());
      this.results.addEventListener('allResultsDeselected', () => this.updateResultCount());
      this.results.addEventListener('allResultsSelected', () => this.updateResultCount());
      this.results.addEventListener('orderChanged', () => this.updateResults());
      this.results.addEventListener('loadMore', ({detail: { startingRecord }}) => this.loadMore(startingRecord));
      this.intro.addEventListener('clearAllFilters', () => this.clearAllFilters());

      // this.backToTopButton.addEventListener('click', () => {
      //   window.scroll({
      //     top: 0,
      //     behavior: 'smooth'
      //   });
      // });

      /** Fire event when filters are updated so filter chips in the intro component updates with new active filters updates */
      this.filters.addEventListener('filterListUpdated', () => {
        this.intro.activeFilters = this.filters.activeFilterList;
        this.updateWelcomeMessageVisibility();
      });


      this.filters.addEventListener('filterRemoved', () =>
        this.updateResults());
      this.filters.addEventListener('clearSelection', (e) => this.clearFilter(e.detail.component));
      this.filters.addEventListener('filterSelected', () => this.updateResults());
      this.filters.addEventListener('clearAllFilters', () => this.clearAllFilters());
    }
  };

  App.SubmittalBuilder.refs = { };
})();
