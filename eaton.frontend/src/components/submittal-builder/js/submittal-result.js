(function () {
  const RESULTS_CLASS = 'submittal-builder__results';
  const HIDDEN_CLASS = 'hidden';
  const REMOVE_FROM_PACKAGE_BUTTON_CLASS = RESULTS_CLASS + '__remove-from-package-button';
  const REMOVE_FROM_PACKAGE_BUTTON_SELECTOR = '.' + REMOVE_FROM_PACKAGE_BUTTON_CLASS;
  const REORDER_RESULTS_BUTTON_CLASS = RESULTS_CLASS + '__reorder-result-button';
  const REORDER_RESULTS_BUTTON_SELECTOR = '.' + REORDER_RESULTS_BUTTON_CLASS;
  const ADD_TO_PACKAGE_BUTTON_CLASS = RESULTS_CLASS + '__add-to-package-button';
  const ADD_TO_PACKAGE_BUTTON_SELECTOR = '.' + ADD_TO_PACKAGE_BUTTON_CLASS;
  const ANIMATION_DURATION_FAST = 500; // in milliseconds
  const ADDED_TO_PACKAGE_ANIMATION_CLASS = RESULTS_CLASS + '__row--added-to-package';
  const REMOVED_FROM_PACKAGE_ANIMATION_CLASS = RESULTS_CLASS + '__row--removed-from-package';
  const REMOVED_FROM_PACKAGE_ROW_ANIMATION_CLASS = RESULTS_CLASS + '__row--removed-from-package-row-animation';
  const ROW_ACTIONS_CLASS = RESULTS_CLASS + '__row-actions';
  const ROW_ACTIONS_SELECTOR = '.' + ROW_ACTIONS_CLASS;
  const STICKY_CLASS = RESULTS_CLASS + '-cell--sticky';
  const STICKY_SELECTOR = '.' + STICKY_CLASS;
  const UNSTICKY_CLASS = RESULTS_CLASS + '-cell--unsticky';
  const unescapeAttr = window.App.global.utils.unescapeAttr;
  const TYPE = 'Type';
  const MATERIAL = 'Material';
  const SERIES = 'Series';
  const NEMA_LOAD = 'NEMA load';
  const SIDE_RAIL = 'Side-rail height';
  const DESCRIPTION = 'Description';

  let App = window.App || {};
  App.SubmittalResult = class SubmittalResult {
    static markup({result, propertyList, previewText, selected, isFileSizeLimitReached, closeText, cannotAddMoreFilesMessage, showingEditPackageMode, itemHasBeenRemovedText}) {
      const resultPropertyElements = [];
      const mobilePropertiesList = [];
      let mobileTitle = '';
      const id = 'accordion' + App.global.utils.hashString(result.url);
      const toolTip =
        `<div class="${ RESULTS_CLASS }__tooltip">
          <button class="button--reset ${ RESULTS_CLASS }__tooltip__close-button">
            <i class="icon icon-close"></i>
          </button>
          <div class="${ RESULTS_CLASS }__tooltip__message">
            ${ cannotAddMoreFilesMessage }
          </div>
        </div>`;

      const itemRemovedMessage = `<div class="${ RESULTS_CLASS }__item-removed-message">${ itemHasBeenRemovedText }</div>`;

      let submittalResultFlags = {};
      submittalResultFlags = App.SubmittalResultsFlag;

      propertyList.forEach(function(prop, index) {
        let classToApply = '';

        if (index === 0) {
          classToApply = STICKY_CLASS;
        } else if (index === 1) {
          classToApply = UNSTICKY_CLASS;
        }

        mobileTitle = index === 0 ? result[prop.name] : (mobileTitle ? mobileTitle : '-');
        resultPropertyElements.push(`
          <div class="${ RESULTS_CLASS }-cell ${ classToApply } ${ prop.title === NEMA_LOAD && submittalResultFlags.nemaLoad ? HIDDEN_CLASS : '' } ${ prop.title === SIDE_RAIL && submittalResultFlags.sideRail ? HIDDEN_CLASS : '' } ${ prop.title === TYPE && submittalResultFlags.type ? HIDDEN_CLASS : '' } ${ prop.title === MATERIAL && submittalResultFlags.material ? HIDDEN_CLASS : '' } ${ prop.title === SERIES && submittalResultFlags.series ? HIDDEN_CLASS : '' } ${ prop.title === DESCRIPTION && submittalResultFlags.description ? HIDDEN_CLASS : '' } ">
            <div class="${ RESULTS_CLASS }-cell--heading">${ prop.title }</div>
            <div class="${ RESULTS_CLASS }-cell--content"><bdi>${ result[prop.name] ? result[prop.name] : '-' }</bdi></div>
          </div>
        `);

        mobilePropertiesList.push(prop.title);
      });
      /**
       * Add the desktop add/remove buttons manually as the first column in the row
       */
      resultPropertyElements.unshift(`
        <div class="${ ROW_ACTIONS_CLASS } hidden-xs">
          <div class="${ RESULTS_CLASS }__button-wrapper">
            <button data-url="${ result.url }" class="button--reset ${ ADD_TO_PACKAGE_BUTTON_CLASS } ${ selected ? 'hidden' : '' } ${ isFileSizeLimitReached ? ADD_TO_PACKAGE_BUTTON_CLASS + '--disabled' : '' }"
              aria-label="Add to package">
                <i class="icon icon-plus-with-circle" aria-hidden="${ selected }"></i>
            </button>

            ${ toolTip }

            <button data-url="${ result.url }"
              class="button--reset ${ REMOVE_FROM_PACKAGE_BUTTON_CLASS } ${ selected ? '' : 'hidden' }"
              aria-label="Remove from package">
                <i class="icon icon-x-with-circle" aria-hidden="${ selected }"></i>
            </button>
          </div>
        </div>
      `);

      return `
      <div class="${ RESULTS_CLASS }__accordion-container panel ${ RESULTS_CLASS }__panel"
           data-url="${ result.url }">

        ${ itemRemovedMessage }

        <div class="${ ROW_ACTIONS_CLASS } panel-heading ${ RESULTS_CLASS }__panel-heading" role="tab">
          <div class="${ RESULTS_CLASS }__button-wrapper">
            <button data-url="${ result.url }"
              class="button--reset ${ ADD_TO_PACKAGE_BUTTON_CLASS } ${ selected ? 'hidden' : '' } ${ isFileSizeLimitReached ? ADD_TO_PACKAGE_BUTTON_CLASS + '--disabled' : '' }"
              aria-label="Add to package">
                <i class="icon icon-plus-with-circle" aria-hidden="${ selected }"></i>
            </button>

            ${ toolTip }

            <button data-url="${ result.url }"
              class="button--reset ${ REMOVE_FROM_PACKAGE_BUTTON_CLASS } ${ selected ? '' : 'hidden' }"
              aria-label="Remove from package">
                <i class="icon icon-x-with-circle" aria-hidden="${ selected }"></i>
            </button>
          </div>
          <div class="${ RESULTS_CLASS }__mobile-title">
            ${ mobileTitle }
          </div>
          <div class="${ RESULTS_CLASS }__mobile-properties">
            ${ mobilePropertiesList.join(', ') }
          </div>
          <div class="${ RESULTS_CLASS }__accordion-control">
            <a href="#" role="button" class="button--reset ${ REORDER_RESULTS_BUTTON_CLASS } sortable-handle ${ showingEditPackageMode ? '' : 'hidden' }">
              <span class="icon icon-sortable-arrows"></span>
            </a>
            <a class="${ window.matchMedia(App.global.constants.MEDIA_QUERIES.MOBILE).matches ? 'collapsed' : '' } ${ showingEditPackageMode ? 'hidden' : '' }"
               data-toggle="collapse"
               data-parent="#accordion_div"
               href="#${ id }">
              <i class="icon icon-sign-minus ${ RESULTS_CLASS }__icon-sign-minus" aria-hidden="true"></i>
              <i class="icon icon-sign-plus ${ RESULTS_CLASS }__icon-sign-plus" aria-hidden="true"></i>
            </a>
          </div>
        </div>
        <div id="${ id }" class="${ window.matchMedia(App.global.constants.MEDIA_QUERIES.MOBILE).matches ? 'collapse' : '' }">
          <div data-url="${ result.url }" class="${ RESULTS_CLASS }__row">
            <div class="${ RESULTS_CLASS }-cell ${ RESULTS_CLASS }-cell--narrow ${ STICKY_CLASS } ${ showingEditPackageMode ? '' : 'hidden' }">
              <a href="#" role="button" class="button--reset ${ REORDER_RESULTS_BUTTON_CLASS } sortable-handle">
                <span class="icon icon-sortable-arrows"></span>
              </a>
            </div>
            ${ resultPropertyElements.join('') }
            <div class="${ RESULTS_CLASS }-cell ${ RESULTS_CLASS }-cell__preview">
              <div class="${ RESULTS_CLASS }-cell--heading">${ previewText }</div>
              <div class="${ RESULTS_CLASS }-cell--content">
                <a href="${ result.url }" target="_blank">
                  <span class="${ RESULTS_CLASS }-cell__preview__title">${ previewText }</span>
                  <span class="${ RESULTS_CLASS }-cell__preview__file-size">(${ App.SubmittalResult.bytesToSize(result.size) })</span>
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
      `;
    }

    unstickyCells() {
      // This converts the NodeList to an array.
      return Array.prototype.slice.call(this.container.querySelectorAll('.submittal-builder__results-cell:not(.submittal-builder__results-cell--sticky)'));
    }

    constructor(container) {
      // The constructor should only contain the boiler plate code for finding or creating the reference.
      if (typeof container.dataset.ref === 'undefined') {
        this.ref = Math.random();
        App.SubmittalResult.refs[this.ref] = this;
        container.dataset.ref = this.ref;
        this.init(container);
      } else {
        // If this element has already been instantiated, use the existing reference.
        return App.SubmittalResult.refs[container.dataset.ref];
      }
    }

    init(container) {
      this.container = container;
      this.result = unescapeAttr(container.dataset.result);
      this.propertyList = unescapeAttr(container.dataset.propertyList);
      this.previewText = container.dataset.previewText;
      this.packageSizeValue = parseInt(container.dataset.packageSize);
      this.sizeLimit = parseInt(container.dataset.sizeLimit);
      this.closeText = container.dataset.closeText;
      this.cannotAddMoreFilesMessage = container.dataset.cannotAddMoreFilesMessage;
      this.itemHasBeenRemovedText = this.container.dataset.itemHasBeenRemovedText;
      this.selected = container.dataset.selected === 'true';
      this.showingEditPackageMode = container.dataset.showingEditPackageMode === 'true';

      this.render();
    }

    get isFileSizeLimitReached() {
      return this.packageSize + parseInt(this.result.size) >= this.sizeLimit;
    }

    set packageSize(size) {
      this.packageSizeValue = size;
    }

    get packageSize() {
      return this.packageSizeValue;
    }

    get propertyValues() {
      return this.result;
    }

    /**
     * Get the height of the container
     * @function rowHeight
     * @returns {string} container's scrollHeight in pixels
    */
    get rowHeight() {
      return this.container.scrollHeight + 'px';
    }

    /**
     * Set the height of the sticky column contents to match its siblings in the row
     * Heights must be manually set on absolutely positioned items in order for the background color heights to match up
     * @function updateFixedColumnHeight
    */
    updateFixedColumnHeight() {
      this.fixedColumns.forEach((fixedColumn) => fixedColumn.style.height = this.rowHeight);
      this.desktopRowActions.style.height = this.rowHeight;
    }

    select() {
      this.addButtons.forEach(addButton => addButton.disabled = true);
      // Update this.container's classes to apply the proper CSS animations
      this.container.classList.remove(REMOVED_FROM_PACKAGE_ANIMATION_CLASS);
      this.container.classList.add(ADDED_TO_PACKAGE_ANIMATION_CLASS);
      this.addButtons.forEach(addButton => addButton.classList.add(HIDDEN_CLASS));
      this.removeButtons.forEach(removeButton => removeButton.classList.remove(HIDDEN_CLASS));

      // create promise to delay the render until after the CSS animation is complete
      return new Promise(resolve => setTimeout(() => {
        this.addButtons.forEach(addButton => addButton.disabled = false);
        resolve();
      }, ANIMATION_DURATION_FAST))
      .then(() => {
        this.selected = true;
        this.render();
      });
    }

    deselect() {
      let tasks = [];
      // create promise to update this.container's classes to apply the proper CSS animations
      const addCssAnimationClasses = () => new Promise(resolve => {
        this.removeButtons.forEach(removeButton => removeButton.disabled = true);
        this.container.classList.remove(ADDED_TO_PACKAGE_ANIMATION_CLASS);
        this.container.classList.add(REMOVED_FROM_PACKAGE_ANIMATION_CLASS);
        if (!this.showingEditPackageMode) {
          this.addButtons.forEach(addButton => addButton.classList.remove(HIDDEN_CLASS));
          this.removeButtons.forEach(removeButton => removeButton.classList.add(HIDDEN_CLASS));
        }

        setTimeout(() => {
          this.removeButtons.forEach(removeButton => removeButton.setAttribute.disabled = false);
          resolve();
        }, ANIMATION_DURATION_FAST);
      });

      // animate row height to zero
      const animateRowHeight = () => new Promise(resolve => {
        // get current row height
        const row = this.container;
        const initialRowHeight = this.rowHeight;

        this.container.classList.add(REMOVED_FROM_PACKAGE_ROW_ANIMATION_CLASS);
        // temporarily disable all css transitions
        const elementTransition = row.style.transition;
        row.style.transition = '';
        requestAnimationFrame(function() {
          row.style.height = initialRowHeight + 'px';
          row.style.transition = elementTransition;
          // on the next frame (as soon as the previous style change has taken effect),
          // have the element transition to height: 0
          requestAnimationFrame(function() {
            row.style.height = 0 + 'px';
          });
        });

        setTimeout(() => {
          resolve();
        }, ANIMATION_DURATION_FAST);
      });

      // if currently in edit package mode, item rows should collapse when removed from package
      if (this.showingEditPackageMode) {
        tasks = [
          addCssAnimationClasses,
          animateRowHeight
        ];
      } else {
        tasks = [
          addCssAnimationClasses
        ];
      }

      return tasks.reduce((promiseChain, currentTask) => {
        return promiseChain.then(chainResults =>
            currentTask().then(currentResult =>
                [...chainResults, currentResult]
            )
        );
      }, Promise.resolve([])).then(arrayOfResults => {
        this.selected = false;
        this.render();
      });
    }

    static bytesToSize(bytes) {
      let sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
      if (bytes === 0) {return '0 Byte';}
      let i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
      return Math.round(bytes / Math.pow(1024, i), 2) + ' ' + sizes[i];
    }

    render() {
      this.container.innerHTML = App.SubmittalResult.markup(this);

      this.addButtons = this.container.querySelectorAll(ADD_TO_PACKAGE_BUTTON_SELECTOR);
      this.removeButtons = this.container.querySelectorAll(REMOVE_FROM_PACKAGE_BUTTON_SELECTOR);
      this.fixedColumns = this.container.querySelectorAll(STICKY_SELECTOR);
      this.desktopRowActions = this.container.querySelector(ROW_ACTIONS_SELECTOR + '.hidden-xs');

      this.updateFixedColumnHeight();

      this.addEventListeners();
    }

    addEventListeners() {
      this.addButtons.forEach(addButton =>
        addButton.addEventListener('click', () => {
          if (!this.isFileSizeLimitReached && !addButton.disabled) {
            addButton.disabled = true;
            this.container.dispatchEvent(new CustomEvent('added', { detail: { resultComponent: this }}));
            setTimeout(() => addButton.disabled = false, ANIMATION_DURATION_FAST);
          }
        })
      );

      this.removeButtons.forEach(removeButton =>
        removeButton.addEventListener('click', () => {
          if (!removeButton.disabled) {
            removeButton.disabled = true;
            this.container.dispatchEvent(new CustomEvent('removed', { detail: { resultComponent: this }}));
            setTimeout(() => removeButton.disabled = false, ANIMATION_DURATION_FAST);
          }
        })
      );

      this.container.querySelectorAll(REORDER_RESULTS_BUTTON_SELECTOR).forEach(reorderButton =>
        reorderButton.addEventListener('click', (e) =>
          e.preventDefault()));
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

  App.SubmittalResult.refs = { };
})();
