(function () {
  const BODY_ELEMENT = document.getElementsByTagName('body')[0];
  const COMPONENT_CLASS = 'submittal-builder';
  const HIDDEN_CLASS = 'hidden';
  const RESULTS_CLASS = COMPONENT_CLASS + '__results';
  const ADD_ALL_BUTTON_CLASS = RESULTS_CLASS + '__add-all-button';
  const ADD_ALL_BUTTON_SELECTOR = '.' + ADD_ALL_BUTTON_CLASS;
  const REMOVE_ALL_BUTTON_CLASS = RESULTS_CLASS + '__remove-all-button';
  const REMOVE_ALL_BUTTON_SELECTOR = '.' + REMOVE_ALL_BUTTON_CLASS;
  const CELL_HEADING_BUTTON_CLASS = RESULTS_CLASS + '__cellheading__button';
  const CELL_HEADING_BUTTON_SELECTOR = '.' + CELL_HEADING_BUTTON_CLASS;
  const SORTABLE_RESULTS_CLASS = RESULTS_CLASS + '__wrapper';
  const SORTABLE_RESULTS_SELECTOR = '.' + SORTABLE_RESULTS_CLASS;
  const HIDE_SCROLLER_RIGHT_GRADIENT_CLASS = SORTABLE_RESULTS_CLASS + '--no-right-border';
  const MOBILE_SORT_SELECT_CLASS = RESULTS_CLASS + '__native-select';
  const MOBILE_SORT_SELECT_SELECTOR = '.' + MOBILE_SORT_SELECT_CLASS;
  const RESULT_CONTAINER_CLASS = COMPONENT_CLASS + '__result';
  const RESULT_CONTAINER_SELECTOR = '.' + RESULT_CONTAINER_CLASS;
  const PACKAGE_ONLY_DISPLAYED_CLASS = RESULTS_CLASS + '--package-only';
  const PACKAGE_ONLY_EDIT_MODE_CLASS = PACKAGE_ONLY_DISPLAYED_CLASS + '--edit-mode';
  const DEFAULT_ZIP_FILE_SIZE = 100000000;
  const LOAD_MORE_BUTTON_SELECTOR = '#submittalLoadMore';
  const DESKTOP_HEADER_ROW_CLASS = RESULTS_CLASS + '__row--head';
  const DESKTOP_HEADER_ROW_SELECTOR = '.' + DESKTOP_HEADER_ROW_CLASS;
  const MODAL_OPEN_CLASS = COMPONENT_CLASS + '__modal--open';
  const MODAL_CLASS = COMPONENT_CLASS + '__modal';
  const RESULTS_MODAL_CLASS = RESULTS_CLASS + '__modal';
  const RESULTS_MODAL_SELECTOR = '.' + RESULTS_MODAL_CLASS;
  const CLOSE_MODAL_CLASS = MODAL_CLASS + '__close';
  const INNER_CONTENT_MODAL_CLASS = MODAL_CLASS + '__inner-content';
  const CLOSE_MODAL_SELECTOR = '.' + CLOSE_MODAL_CLASS;
  const CONFIRM_REMOVE_ALL_ITEMS_FROM_PACKAGE_BUTTON_CLASS = RESULTS_CLASS + '__confirm-remove-all-items-from-package';
  const CONFIRM_REMOVE_ALL_ITEMS_FROM_PACKAGE_BUTTON_SELECTOR = '.' + CONFIRM_REMOVE_ALL_ITEMS_FROM_PACKAGE_BUTTON_CLASS;
  const DECLINE_REMOVE_ALL_ITEMS_FROM_PACKAGE_BUTTON_CLASS = RESULTS_CLASS + '__decline-remove-all-items-from-package';
  const DECLINE_REMOVE_ALL_ITEMS_FROM_PACKAGE_BUTTON_SELECTOR = '.' + DECLINE_REMOVE_ALL_ITEMS_FROM_PACKAGE_BUTTON_CLASS;
  const STICKY_CLASS = RESULTS_CLASS + '-cellheading--sticky';
  const STICKY_CLASS_SELECTOR = '.' + STICKY_CLASS;
  const UNSTICKY_CLASS = RESULTS_CLASS + '-cellheading--unsticky';
  const SECONDARY_HEADER_CLASS = RESULTS_CLASS + '__secondary-header';
  const SECONDARY_HEADER_SELECTOR = '.' + SECONDARY_HEADER_CLASS;
  const SCROLLING_CONTAINER_CLASS = RESULTS_CLASS + '__scroller';
  const SCROLLING_CONTAINER_SELECTOR = '.' + SCROLLING_CONTAINER_CLASS;
  const PAGINATION_CLASS = RESULTS_CLASS + '__pagination';
  const PAGINATION_CONTAINER_CLASS = PAGINATION_CLASS + '__container';
  const PAGINATION_CONTAINER_SELECTOR = '.' + PAGINATION_CONTAINER_CLASS;
  const PAGINATION_BUTTON_CLASS = PAGINATION_CLASS + '__button';
  const PAGINATION_NEXT_CLASS = PAGINATION_BUTTON_CLASS + '--next';
  const PAGINATION_NEXT_SELECTOR = '.' + PAGINATION_NEXT_CLASS;
  const PAGINATION_PREVIOUS_CLASS = PAGINATION_BUTTON_CLASS + '--previous';
  const PAGINATION_PREVIOUS_SELECTOR = '.' + PAGINATION_PREVIOUS_CLASS;
  const PAGINATION_COLUMNS_TO_SCROLL = 3;
  const escapeAttr = window.App.global.utils.escapeAttr;
  const TYPE = 'Type';
  const MATERIAL = 'Material';
  const SERIES = 'Series';
  const NEMA_LOAD = 'NEMA load';
  const SIDE_RAIL = 'Side-rail height';
  const DESCRIPTION = 'Description';

  let App = window.App || {};
  App.SubmittalResults = class SubmittalResults {
    static markup({files, showingOnlyPackage, displayedResults, sortByText, addAllText, removeAllText, previewText, fileSizeText, propertyList, isFileSizeLimitReached, estimatedZipSize, zipSizeLimit, closeText, cannotAddMoreFilesMessage, showingEditPackageMode, loadMoreLabel, pageSize, resultCount, showLoadMore, resultsText, documentsTextValue, itemHasBeenRemovedText, allItemsRemovedText, fileDeletionConfirmationTitleText, fileDeletionConfirmationText, yesButtonText, noButtonText, nextButtonText, previousButtonText, hideResultsList, canScrollBackward, canScrollForward }) {
      let resultElements = [];
      const mobileSortElements = [];
      const headerElements = [];

      const fileDeletionModal = `
        <div class="submittal-builder__modal overlay ${ RESULTS_MODAL_CLASS } ${ HIDDEN_CLASS }">
          <div class="${ INNER_CONTENT_MODAL_CLASS }">
            <h3 class="${ MODAL_CLASS }__header">${ fileDeletionConfirmationTitleText }</h3>
            <button aria-label="${ closeText }"
              class="button--reset ${ CLOSE_MODAL_CLASS }">
              <span class="sr-only">[close text] ${ fileDeletionConfirmationText }</span>
              <i class="icon icon-close" aria-hidden="true"></i>
            </button>
            <div>
              <p class="${ RESULTS_MODAL_CLASS }__confirmation-text">${ fileDeletionConfirmationText }</p>
              <div class="${ RESULTS_MODAL_CLASS }__button-group" role="group">
                <button
                  data-analytics-name="submittal-builder-decline-remove-all-items-from-package"
                  class="b-button b-button__primary b-button__primary--light ${ DECLINE_REMOVE_ALL_ITEMS_FROM_PACKAGE_BUTTON_CLASS }">
                    ${ noButtonText }
                </button>
                <button
                  data-analytics-name="submittal-builder-confirm-remove-all-items-from-package"
                  class="b-button b-button__primary b-button__primary--light ${ CONFIRM_REMOVE_ALL_ITEMS_FROM_PACKAGE_BUTTON_CLASS }">
                    ${ yesButtonText }
                </button>
              </div>
            </div>
          </div>
        </div>
      `;

      function getFlagforFieldes(responseObject , valueKey) {
        let flag = true;
        for (let index = 0 ; index < responseObject.length; index++)
        {
          if (responseObject[index][valueKey])
          {
            flag = false;
            break;
          }
        }
        return flag;
      }

      App.SubmittalResultsFlag = {};
      App.SubmittalResultsFlag.type = getFlagforFieldes(displayedResults,'b-line-submittal-builder_type');
      App.SubmittalResultsFlag.material = getFlagforFieldes(displayedResults,'b-line-submittal-builder_material');
      App.SubmittalResultsFlag.series = getFlagforFieldes(displayedResults,'b-line-submittal-builder_series');
      App.SubmittalResultsFlag.nemaLoad = getFlagforFieldes(displayedResults,'b-line-submittal-builder_nema-load');
      App.SubmittalResultsFlag.sideRail = getFlagforFieldes(displayedResults,'b-line-submittal-builder_side-rail-height');
      App.SubmittalResultsFlag.description = getFlagforFieldes(displayedResults,'b-line-submittal-builder_description');

      if (typeof displayedResults !== 'undefined') {
        resultElements = displayedResults.map(result => `
            <div class="${ RESULT_CONTAINER_CLASS }"
                 data-result="${ escapeAttr(result) }"
                 data-property-list="${ escapeAttr(propertyList) }"
                 data-preview-text="${ previewText }"
                 data-close-text="${ closeText }"
                 data-package-size="${ estimatedZipSize }"
                 data-size-limit="${ zipSizeLimit }"
                 data-cannot-add-more-files-message="${ cannotAddMoreFilesMessage }"
                 data-selected="${ files.map(file => file.url).indexOf(result.url) >= 0 }"
                 data-showing-edit-package-mode="${ showingEditPackageMode }"
                 data-item-has-been-removed-text="${ itemHasBeenRemovedText }">
            </div>
          `
        );
      }

      if (propertyList.length > 0) {
        if (showingOnlyPackage) {
          headerElements.push(`<div class="${ RESULTS_CLASS }-cellheading ${ RESULTS_CLASS }-cellheading--narrow ${ CELL_HEADING_BUTTON_CLASS }"></div>`);
        }

        propertyList.forEach((prop, index) => {
          headerElements.push(`
            <button
              data-analytics-event="submittal-builder-column-sort"
              data-analytics-name="submittal-builder-column-header : ${ prop.name }"
              data-analytics-state=${ prop.order === 'ASC' ? 'ASC' : 'DESC' }
              class="button--reset ${ RESULTS_CLASS }-cellheading ${ CELL_HEADING_BUTTON_CLASS } ${ index === 0 ? STICKY_CLASS : '' } ${ index === 1 ? UNSTICKY_CLASS : '' } ${ prop.title === NEMA_LOAD && App.SubmittalResultsFlag.nemaLoad ? HIDDEN_CLASS : '' } ${ prop.title === SIDE_RAIL && App.SubmittalResultsFlag.sideRail ? HIDDEN_CLASS : '' } ${ prop.title === TYPE && App.SubmittalResultsFlag.type ? HIDDEN_CLASS : '' } ${ prop.title === MATERIAL && App.SubmittalResultsFlag.material ? HIDDEN_CLASS : '' } ${ prop.title === SERIES && App.SubmittalResultsFlag.series ? HIDDEN_CLASS : '' } ${ prop.title === DESCRIPTION && App.SubmittalResultsFlag.description ? HIDDEN_CLASS : '' }"
              data-name="${ prop.name }"
              ${ index !== 0 ? 'data-waypoint="' + index + '"' : '' }>
              <span class="submittal-builder-column-header__text">${ prop.title }</span>
              <i class="icon ${ prop.order === 'ASC' ? 'icon-chevron-up' : 'icon-chevron-down' }" aria-hidden="true"></i>
            </button>
          `);

          mobileSortElements.push(`
            <option value="${ prop.name }">${ prop.title }</option>
          `);
        });

        headerElements.push(`
          <div class="${ RESULTS_CLASS }-cellheading" data-waypoint>
            ${ previewText } (${ fileSizeText })
          </div>
        `);
      }

      return `
      ${ fileDeletionModal }
      <div class="${ hideResultsList ? HIDDEN_CLASS : '' }">
        <div class="submittal-builder__results-count__container--mobile">
          <h3 class="faceted-navigation-header__results-count submittal-builder__results-count">
            ${ showingOnlyPackage || showingEditPackageMode ? `${ files.length } ${ documentsTextValue } ` : '' }

            ${ resultCount > 0 && !showingOnlyPackage ? `${ resultCount } ${ resultsText }` : '' }
          </h3>

          <hr class="${ COMPONENT_CLASS }__hr">
        </div>

        <div class="${ RESULTS_CLASS }__all-items-removed ${ resultElements.length === 0 && showingEditPackageMode === true ? '' : 'hidden' }">${ allItemsRemovedText }</div>

        <div class="${ RESULTS_CLASS }__wrapper--outer" ${ resultElements.length === 0 && showingEditPackageMode === true ? 'hidden' : '' }>
          <div class="${ RESULTS_CLASS }__scroller">
            <div class="${ RESULTS_CLASS }__container">
              <div class="${ RESULTS_CLASS }__desktop-filter">
                <div class="${ RESULTS_CLASS }__row ${ RESULTS_CLASS }__row--head">
                  ${ headerElements.join('') }
                </div>
              </div>

              <div class="${ SECONDARY_HEADER_CLASS }">
                <button
                  data-analytics-name="submittal-builder-add-all-results"
                  class="btn button--reset ${ ADD_ALL_BUTTON_CLASS } ${ ADD_ALL_BUTTON_CLASS }--desktop ${ isFileSizeLimitReached ? 'disabled-btn' : '' }  ">
                    ${ addAllText }
                </button>
                <button
                  data-analytics-name="submittal-builder-remove-all-results"
                  class="button--reset ${ REMOVE_ALL_BUTTON_CLASS } ${ REMOVE_ALL_BUTTON_CLASS }--desktop ${ files < 1 ? 'hidden' : '' }">
                    ${ removeAllText }
                </button>
                <div class="${ PAGINATION_CONTAINER_CLASS } ${ canScrollForward || canScrollBackward ? '' : HIDDEN_CLASS }">
                  <button class="button--reset ${ PAGINATION_BUTTON_CLASS } ${ PAGINATION_PREVIOUS_CLASS }"
                    aria-label="${ previousButtonText }"
                    data-analytics-name="submittal-builder-pagination-previous-column"
                    ${ !canScrollBackward ? 'disabled' : '' }>
                    <span class="icon icon-chevron-left" aria-hidden="true"></span>
                  </button>
                  <button class="button--reset ${ PAGINATION_BUTTON_CLASS } ${ PAGINATION_NEXT_CLASS }"
                    aria-label="${ nextButtonText }"
                    data-analytics-name="submittal-builder-pagination-next-column"
                    ${ !canScrollForward ? 'disabled' : '' }>
                    <span class="icon icon-chevron-right" aria-hidden="true"></span>
                  </button>
                </div>
              </div>

            <div class="${ RESULTS_CLASS }__mobile_filter">
              <button class="button--reset ${ ADD_ALL_BUTTON_CLASS } ${ isFileSizeLimitReached ? 'disabled-btn' : '' } ">${ addAllText }</button>
              <button class="button--reset ${ REMOVE_ALL_BUTTON_CLASS } ${ files < 1 ? 'hidden' : '' }">${ removeAllText }</button>
              <div class="faceted-navigation-header__sort-options ${ RESULTS_CLASS }__sort-options">
                <div class="dropdown">
                  <button id="dSortFacets" class="" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    ${ sortByText }
                    <i class="icon icon-chevron-down" aria-hidden="true"></i>
                  </button>
                  </div>
                  <select class="native-select ${ MOBILE_SORT_SELECT_CLASS }">
                    ${ mobileSortElements.join('') }
                  </select>
                </div>
              </div>

              <div class="${ SORTABLE_RESULTS_CLASS }" id="accordion_div">
                ${ resultElements.join('') }
              </div>
            </div>
          </div>
        </div>
        <div id="submittalLoadMore" class="text-center ${ showLoadMore ? '' : 'hidden' }">
          <button  class="b-button b-button__primary b-button__primary--light" role="button" data-load-more>${ loadMoreLabel }</button>
        </div>
      </div>
      `;
    }

    constructor(container) {
      // The constructor should only contain the boiler plate code for finding or creating the reference.
      if (typeof container.dataset.ref === 'undefined') {
        this.ref = Math.random();
        App.SubmittalResults.refs[this.ref] = this;
        container.dataset.ref = this.ref;
        this.init(container);
      } else {
        // If this element has already been instantiated, use the existing reference.
        return App.SubmittalResults.refs[container.dataset.ref];
      }
    }

    init(container) {
      this.container = container;
      this.hideResultsList = false;
      this.pageSize = parseInt(this.container.dataset.pageSize);
      this.cannotAddMoreFilesMessage = this.container.dataset.cannotAddMoreFilesMessage;
      this.description = this.container.dataset.description;
      this.loadMoreLabel = this.container.dataset.loadMoreLabel;
      this.resultListValues = this.container.dataset.resultList;
      this.addAllText = this.container.dataset.addAllText;
      this.removeAllText = this.container.dataset.removeAllText;
      this.previewText = this.container.dataset.previewText;
      this.fileSizeText = this.container.dataset.fileSizeText;
      this.sortByText = this.container.dataset.sortByText;
      this.closeText = this.container.dataset.closeText;
      this.showingEditPackageMode = false;
      this.resultComponents = [];
      this.files = [];
      this.zipSizeLimit = this.container.dataset.zipSizeLimit;
      this.showingOnlyPackage = false;
      this.resultsText = this.container.dataset.resultsText;
      this.documentsTextValue = 0;
      this.itemHasBeenRemovedText = this.container.dataset.itemHasBeenRemovedText;
      this.allItemsRemovedText = this.container.dataset.allItemsRemovedText;
      this.fileDeletionConfirmationText = this.container.dataset.fileDeletionConfirmationText;
      this.fileDeletionConfirmationTitleText = this.container.dataset.fileDeletionConfirmationTitleText;
      this.yesButtonText = this.container.dataset.yesButtonText;
      this.noButtonText = this.container.dataset.noButtonText;
      this.nextButtonText = this.container.dataset.nextButtonText;
      this.previousButtonText = this.container.dataset.previousButtonText;
      this.activeFilterValues = [];
      this.columns = [];

      this.itemsInPackage = this.container.dataset.itemsInPackage;

      this.render();
    }

    static renderKeywords(resultL) {
      return `${ resultL.attributes.map(resultDetail => `<li>${ resultDetail.name }</li>`) }`;
    }

    get showLoadMore() {
      return !this.showingOnlyPackage && this.resultList.length < this.resultCount;
    }

    get sortOrder() {
      return this.sortOrderValue ? this.sortOrderValue : [{ name: '', order: 'ASC' }];
    }

    set sortOrder(order) {
      this.sortOrderValue = order;
    }

    set resultCount(count) {
      this.resultCountValue = count;
    }

    get resultCount() {
      return this.resultCountValue;
    }

    set documentsText (documentsTextValue) {
      this.documentsTextValue = documentsTextValue;

      this.render();
    }

    sort(name) {
      // Determine the order for this column based on its current order, defaulting to ascending.
      let orderIsAscending = false;
      this.sortOrder.forEach(property => {
        if (property.name === name) {
          orderIsAscending = property.order === 'ASC';
        }
      });
      const order = orderIsAscending ? 'DESC' : 'ASC';

      // Remove the property with the given name from the list.
      this.sortOrder = this.sortOrder.filter(property => property.name !== name);

      this.sortOrder.unshift({name, order});

      // trigger a sort on this.files too so it is in sync across tabs
      this.reorderFilesInPackage();

      this.container.dispatchEvent(new CustomEvent('orderChanged'));
    }

    get resultList() {
      return this.resultListValues ? this.resultListValues : [];
    }

    set resultList(resultListValues) {
      this.resultListValues = resultListValues;

      this.render();
    }

    get files() {
      return this.filesValue ? this.filesValue : [];
    }

    set files(files) {
      this.filesValue = files;
      this.resultComponents.forEach(resultComponent => resultComponent.packageSize = this.estimatedZipSize);
      this.reorderFiles();
    }

    get packageList() {
      return this.files.map(file => file.url);
    }

    reorderFiles() {
      let fileOrder = [...this.container
        .querySelectorAll(SORTABLE_RESULTS_SELECTOR + ' .submittal-builder__results__panel')]
        .map(element => element.dataset.url);

      this.files.sort((file1, file2) => fileOrder.indexOf(file1.url) > fileOrder.indexOf(file2.url) === true ? 1 : -1);
    }

    /**
    * @function reorderFilesInPackage - reorders the the list of files in the package based on the sortOrder property
    */
    reorderFilesInPackage() {
      /** create a reference to the sortOrder */
      let sortOrder = this.sortOrder[0];

      /** sort this.files in ascending order */
      this.files.sort((file1, file2) => {
        /** check if each parameter is defined
         * if true, convert to uppercase for a case insensitive comparison
         * if false, set to 'ZZZZZ' to ensure it is at the end of the package
         */
        let file1Value = typeof file1[sortOrder.name] !== 'undefined' ? file1[sortOrder.name].toUpperCase() : 'ZZZZZZ';
        let file2Value = typeof file2[sortOrder.name] !== 'undefined' ? file2[sortOrder.name].toUpperCase() : 'ZZZZZZ';

        return (file1Value < file2Value) ? -1 : (file1Value > file2Value) ? 1 : 0;
      });

      /** reverse the file list if sortOrder.order is descending */
      if (sortOrder.order === 'DESC') {
        this.files.reverse();
      }

      this.render();
    }

    get displayedResults() {
      return this.showingOnlyPackage ? this.files : this.resultList;
    }

    get size() {
      return this.files.length;
    }

    get isFileSizeLimitReached() {
      return this.estimatedZipSize >= parseInt(this.zipSizeLimit !== '0' ? this.zipSizeLimit : DEFAULT_ZIP_FILE_SIZE);
    }

    get estimatedZipSize() {
      return this.files.reduce((totalSize, file) => Number(totalSize) + Number(file.size), 0);
    }

    removeFile(resultComponent) {
      this.files = this.files.filter(file => file.url !== resultComponent.propertyValues.url);

      resultComponent.deselect()
      .then(() => {
        this.container.dispatchEvent(new CustomEvent('resultDeselected', { detail: { resultComponent: resultComponent }}));
        this.render();
      });
    }

    addFile(resultComponent) {
      this.files.push(resultComponent.propertyValues);

      resultComponent.select()
      .then(() =>
        this.container.dispatchEvent(new CustomEvent('resultSelected', { detail: { resultComponent: resultComponent }})));
    }

    /**
     * Add all files in the resultList to package
     * @function addAllFiles
    */
    addAllFiles() {
      this.resultList.slice(0,this.resultComponents.length).forEach(file => {
        if (this.fileIsEligible(file)) {this.files.push(file);}
      });

      this.container.dispatchEvent(new CustomEvent('allResultsSelected'));
      this.render();
    }

    /**
     * @function openRemoveAllFilesConfirmationModal - shows removeAllFilesConfirmationModal
    */
    openFileDeletionConfirmationModal() {
      this.fileDeletionConfirmationModal.classList.remove(HIDDEN_CLASS);
      BODY_ELEMENT.classList.add(MODAL_OPEN_CLASS);
    }

    /**
     * @function closeRemoveAllFilesConfirmationModal - hides removeAllFilesConfirmationModal
    */
    closeFileDeletionConfirmationModal() {
      this.fileDeletionConfirmationModal.classList.add(HIDDEN_CLASS);
      BODY_ELEMENT.classList.remove(MODAL_OPEN_CLASS);
    }

    /**
     * Show a modal to confirm that all files should be removed from the package
     * @function showRemoveAllFilesConfirmationModal
    */
    showFileDeletionConfirmationModal() {
      this.openFileDeletionConfirmationModal();
    }

    /**
     * Remove all files from the package
     * @function removeAllFiles
    */
    removeAllFiles() {
      this.files = [];
      this.container.dispatchEvent(new CustomEvent('allResultsDeselected'));
      this.closeFileDeletionConfirmationModal();

      this.render();
    }

    /**
     * Determines if the file is eligible by checking if it has already been added
     * and if it is small enough to be added without making the package too large.
     * @function fileIsEligible
     * @param {array} file - A file to be added.
     * @returns {array} whether the file is eligible to be added.
    */
    fileIsEligible(file) {
      return this.files.map(file => file.url).indexOf(file.url) === -1 &&
             this.estimatedZipSize + parseInt(file.size) < parseInt(this.zipSizeLimit);
    }

    /**
     * Only display the files that have been added to the package
     * @function showOnlyPackage
    */
    showOnlyPackage() {
      this.showingOnlyPackage = true;
      this.container.classList.add(PACKAGE_ONLY_DISPLAYED_CLASS);
      // The current requirements is that any time the package view is used the
      // edit mode is also turned on. However this component is flexible enough
      // to show the package with edit mode left off or show the package with edit
      // mode turned on.
      this.showEditPackageMode();

      this.render();
    }

    /**
     * Make the package contents editable
     * @function showEditPackageMode
    */
    showEditPackageMode() {
      this.showingEditPackageMode = true;
      this.render();
      this.container.classList.add(PACKAGE_ONLY_EDIT_MODE_CLASS);
    }

    /**
     * Make the package contents read only
     * @function hideEditPackageMode
    */
    hideEditPackageMode() {
      this.showingEditPackageMode = false;
      this.container.classList.remove(PACKAGE_ONLY_EDIT_MODE_CLASS);

      this.render();
    }

    /**
     * Show all results
     * @function showAllResults
    */
    showAllResults() {
      this.showingOnlyPackage = false;
      this.container.classList.remove(PACKAGE_ONLY_DISPLAYED_CLASS);
      this.hideEditPackageMode();

      this.render();
    }

    /**
     * @function properties - Sets an array of property names that should be displayed in the table.
     */
    set propertyList(properties) {
      this.properties = properties;
    }

    get propertyList() {
      let propMap = {};
      if (this.properties) {
        propMap = this.properties.reduce((map, prop) => {
          map[prop.name] = prop;
          return map;
        }, {});

        this.sortOrder.forEach(orderProp => {
          if (propMap[orderProp.name]) {
            propMap[orderProp.name].order = orderProp.order;
          }
        });
      }

      return Object.keys(propMap).map(propName => propMap[propName]);
    }

    /**
     * Get the height of the header
     * @function headerHeight
     * @returns {string} header's scrollHeight in pixels
    */
    get headerHeight() {
      return this.headerRow.scrollHeight + 'px';
    }

    /**
     * Get the greater height of the secondary header's child elements (add/remove all link and pagination arrows) if paginationContainer exists in the DOM
     * @function secondaryHeaderHeight
     * @returns {string} secondary header's tallest child element's scrollHeight in pixels
    */
    get secondaryHeaderHeight() {
      if (this.showingOnlyPackage && this.paginationContainer !== null) {
        return this.removeAllButtonDesktop.scrollHeight > this.paginationContainer.scrollHeight ? this.removeAllButtonDesktop.scrollHeight + 'px' : this.paginationContainer.scrollHeight + 'px';
      } else if (this.paginationContainer !== null) {
        return this.addAllButtonDesktop.scrollHeight > this.paginationContainer.scrollHeight ? this.addAllButtonDesktop.scrollHeight + 'px' : this.paginationContainer.scrollHeight + 'px';
      }
    }

    /**
     * Set the height of the sticky column contents to match its siblings in the row if fixedColumnHeader exists in the DOM
     * Heights must be manually set on absolutely positioned items in order for the background color heights to match up
     * @function updateFixedColumnHeaderHeight
    */
    updateFixedColumnHeaderHeight() {
      if (this.fixedColumnHeader !== null) {
        this.fixedColumnHeader.style.height = this.headerHeight;
      }
    }

    /**
     * Set the height of the secondary header to prevent its child elements from vertically overflowing
     * @function updateSecondaryHeaderHeight
    */
    updateSecondaryHeaderHeight() {
      this.secondaryHeader.style.height = this.secondaryHeaderHeight;
    }

    get numberOfColumnsInView() {
      return this.unstickyViewportWidth / this.unstickyColumnWidth;
    }

    get unstickyViewportWidth() {
      // This assumes a constant width of the unsticky columns
      return this.container.querySelector('.submittal-builder__results__container') !== null ? this.container.querySelector('.submittal-builder__results__container').offsetWidth : 0;
    }

    get unstickyColumnWidth() {
      return this.container.querySelector('.submittal-builder__results-cellheading--unsticky') !== null ? this.container.querySelector('.submittal-builder__results-cellheading--unsticky').offsetWidth : 0;
    }

    get unstickyCellHeadings() {
      // This assumes that the only columns that are being paginated are the ones with headers that don't have the sticky or narrow classes.
      return this.container.querySelectorAll('.submittal-builder__results-cellheading:not(.submittal-builder__results-cellheading--sticky):not(.submittal-builder__results-cellheading--narrow)');
    }

    get cellsToMove() {
      return this.resultComponents
        .map(resultComponent => resultComponent.unstickyCells())
        .reduce((allCells, resultCells) => allCells.concat(resultCells), []);
    }

    get canScrollForward() {
      let maxColumns = this.unstickyCellHeadings.length;
      let currentColumns = this.columns.length + this.numberOfColumnsInView;
      return currentColumns < maxColumns;
    }

    get canScrollBackward() {
      return this.columns.length > 0;
    }

    paginationNext() {
      let scrolled = false;

      for (let i = 0; i < PAGINATION_COLUMNS_TO_SCROLL; i++) {
        if (this.canScrollForward) {
          scrolled = true;
          this.columns.push(this.unstickyColumnWidth);
        }
      }

      if (scrolled) {
        this.updatePagination();
      }
    }

    paginationPrevious() {
      let scrolled = false;

      for (let i = 0; i < PAGINATION_COLUMNS_TO_SCROLL; i++) {
        if (this.canScrollBackward) {
          scrolled = true;
          this.columns.pop();
        }
      }

      if (scrolled) {
        this.updatePagination();
      }
    }

    get maxTranslation() {
      return (this.unstickyCellHeadings.length * this.unstickyColumnWidth) - this.unstickyViewportWidth;
    }

    get totalTranslation() {
      let totalTranslation = this.columns.reduce((columnOne, columnTwo) => columnOne + columnTwo, 0);

      // Make the last columns stick to the end of the horizontal space.
      totalTranslation = totalTranslation > this.maxTranslation ? this.maxTranslation : totalTranslation;

      // Accounts for the padding between the blue bar and the first column.
      totalTranslation = totalTranslation - window.getComputedStyle(this.unstickyCellHeadings[0]).paddingLeft.replace('px', '');

      // Make sure the translation never goes below 0.
      totalTranslation = totalTranslation < 0 ? 0 : totalTranslation;

      return totalTranslation;
    }

    updatePagination() {
      let translation = `translate(-${ this.totalTranslation }px)`;
      this.unstickyCellHeadings.forEach(moveHeader => moveHeader.style.transform = translation);
      this.cellsToMove.forEach(moveCell => moveCell.style.transform = translation);

      if (!this.canScrollForward && !this.canScrollBackward) {
        this.paginationContainer.classList.add(HIDDEN_CLASS);
      } else {
        this.paginationContainer.classList.remove(HIDDEN_CLASS);
      }

      if (!this.canScrollForward) {
        this.container.querySelector(SORTABLE_RESULTS_SELECTOR).classList.add(HIDE_SCROLLER_RIGHT_GRADIENT_CLASS);
      } else {
        this.container.querySelector(SORTABLE_RESULTS_SELECTOR).classList.remove(HIDE_SCROLLER_RIGHT_GRADIENT_CLASS);
      }

      this.paginationNextButton.disabled = !this.canScrollForward;
      this.paginationPreviousButton.disabled = !this.canScrollBackward;
    }

    open() {
      this.container.classList.remove(HIDDEN_CLASS);
    }

    close() {
      this.container.classList.add(HIDDEN_CLASS);
    }

    render() {
      this.container.innerHTML = App.SubmittalResults.markup(this);
      this.fileDeletionConfirmationModal = this.container.querySelector(RESULTS_MODAL_SELECTOR);
      this.confirmRemoveAllItemsFromPackageButton = this.container.querySelector(CONFIRM_REMOVE_ALL_ITEMS_FROM_PACKAGE_BUTTON_SELECTOR);
      this.declineRemoveAllItemsFromPackageButton = this.container.querySelector(DECLINE_REMOVE_ALL_ITEMS_FROM_PACKAGE_BUTTON_SELECTOR);
      this.addAllButtonDesktop = this.container.querySelector(ADD_ALL_BUTTON_SELECTOR + '--desktop');
      this.addAllButtons = this.container.querySelectorAll(ADD_ALL_BUTTON_SELECTOR);
      this.removeAllButtons = this.container.querySelectorAll(REMOVE_ALL_BUTTON_SELECTOR);
      this.removeAllButtonDesktop = this.container.querySelector(REMOVE_ALL_BUTTON_SELECTOR + '--desktop');
      this.closeModalButton = this.container.querySelector(CLOSE_MODAL_SELECTOR);
      this.mobileSortSelector = this.container.querySelector(MOBILE_SORT_SELECT_SELECTOR);
      this.loadMoreButton = this.container.querySelector(LOAD_MORE_BUTTON_SELECTOR);
      this.headerRow = this.container.querySelector(DESKTOP_HEADER_ROW_SELECTOR);
      this.fixedColumnHeader = this.container.querySelector(STICKY_CLASS_SELECTOR);
      this.secondaryHeader = this.container.querySelector(SECONDARY_HEADER_SELECTOR);
      this.paginationContainer = this.container.querySelector(PAGINATION_CONTAINER_SELECTOR);
      this.paginationNextButton = this.container.querySelector(PAGINATION_NEXT_SELECTOR);
      this.paginationPreviousButton = this.container.querySelector(PAGINATION_PREVIOUS_SELECTOR);
      this.scrollingContainer = this.container.querySelector(SCROLLING_CONTAINER_SELECTOR);
      this.paginationWaypoints = this.container.querySelectorAll('[data-waypoint]');
      this.paginationNextWaypoint = null;
      this.paginationPreviousWaypoint = null;
      this.resultComponents = [];

      this.updateFixedColumnHeaderHeight();
      this.updateSecondaryHeaderHeight();

      if (this.container.querySelectorAll(RESULT_CONTAINER_SELECTOR).length > 0) {
        [...this.container.querySelectorAll(RESULT_CONTAINER_SELECTOR)]
        .forEach(resultContainer => {
          this.resultComponents.push(new App.SubmittalResult(resultContainer));
        });

        this.updatePagination();
      }

      this.addEventListeners();
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

    /**
     * @function addEventListeners - Adds event listeners for the internal implementation
     * of this component.
     */
    addEventListeners() {
      this.resultComponents.forEach(resultComponent =>
        resultComponent.addEventListener('added', () => {
          if (!this.isFileSizeLimitReached) {
            this.addFile(resultComponent);
          }
        })
      );

      this.resultComponents.forEach(resultComponent =>
        resultComponent.addEventListener('removed', () => {
          this.removeFile(resultComponent);
        })
      );

      if (this.showingEditPackageMode) {
        this.sortableResults = window.Sortable.create(this.container.querySelector(SORTABLE_RESULTS_SELECTOR), {
          animation: 100,
          dragClass: '.submittal-builder__results__row',
          handle: '.sortable-handle',
          onEnd: () => this.reorderFiles()
        });
      }

      this.container.querySelectorAll(CELL_HEADING_BUTTON_SELECTOR).forEach(cellHeading =>
        cellHeading.addEventListener('click', () => {
          this.sort(cellHeading.dataset.name);
        })
      );

      this.addAllButtons.forEach((button) => button.addEventListener('click', () => this.addAllFiles()));
      this.removeAllButtons.forEach((button) => button.addEventListener('click', () => this.showFileDeletionConfirmationModal()));

      this.mobileSortSelector.addEventListener('change', (e) => {
        this.sort(e.target.value);
      });

      this.loadMoreButton.addEventListener('click', () => {
        this.container.dispatchEvent(new CustomEvent('loadMore', { detail: { startingRecord: this.resultList.length }}));});

      this.closeModalButton.addEventListener('click', () => this.closeFileDeletionConfirmationModal());

      this.confirmRemoveAllItemsFromPackageButton.addEventListener('click', () => this.removeAllFiles());
      this.declineRemoveAllItemsFromPackageButton.addEventListener('click', () => this.closeFileDeletionConfirmationModal());

      this.paginationNextButton.addEventListener('click', () => this.paginationNext());
      this.paginationPreviousButton.addEventListener('click', () => this.paginationPrevious());
    }
  };

  App.SubmittalResults.refs = { };
})();
