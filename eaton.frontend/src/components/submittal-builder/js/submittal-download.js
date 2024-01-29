(function () {
  const BODY_ELEMENT = document.getElementsByTagName('body')[0];
  const MODAL_OPEN_CLASS = 'submittal-builder__modal--open';
  const HIDDEN_CLASS = 'hidden';
  const MODAL_CLASS = 'submittal-builder__modal';
  const CLOSE_MODAL_CLASS = MODAL_CLASS + '__close';
  const INNER_CONTENT_MODAL_CLASS = MODAL_CLASS + '__inner-content';
  const DOWNLOAD_CLASS = 'submittal-builder__download';
  const DOWNLOAD_PACKAGE_BUTTON_CLASS = DOWNLOAD_CLASS + '__download-button';
  const DOWNLOAD_PACKAGE_BUTTON_SELECTOR = '.' + DOWNLOAD_PACKAGE_BUTTON_CLASS;
  const EMAIL_PACKAGE_BUTTON_CLASS = DOWNLOAD_CLASS + '__email-button';
  const EMAIL_PACKAGE_BUTTON_SELECTOR = '.' + EMAIL_PACKAGE_BUTTON_CLASS;
  const FILE_ACCESS_RADIO_GROUP_NAME = 'file-access';
  const CLOSE_SELECTOR = '.' + CLOSE_MODAL_CLASS;
  const DOWNLOAD_RADIO_VALUE = 'download';
  const EMAIL_RADIO_VALUE = 'email';
  const PACKAGE_SIZE_CLASS = DOWNLOAD_CLASS + '-package-size';
  const PACKAGE_SIZE_SELECTOR = '.' + PACKAGE_SIZE_CLASS;
  const NO_COMMUNICATIONS_CLASS = DOWNLOAD_CLASS + '-no-communications';
  const NO_COMMUNICATIONS_SELECTOR = '.' + NO_COMMUNICATIONS_CLASS;

  let App = window.App || {};
  App.SubmittalDownload = class SubmittalDownload {
    static markup({title, preferredOptionText, downloadOptionText, emailOptionText, sizeText, sendEmailText, expirationText, fileSizeLimitText, closeText, thankYouDownloadMessage, thankYouEmailMessage, assetRequiredMessage, selectedRadioButton, formSubmitted, fileName, eatonCommunicationsPage, eatonCommunicationsMessage, yesText, noText, formattedPackageSize, mergeAssetsFileName}) {
      /**
       * NOTE: although the radio buttons are not a faceted navigation, faceted navigation classes are used to match radio button
       * styling and behaviors without duplicating CSS and JS
      */
      return `
        <div class="${ DOWNLOAD_CLASS } ${ INNER_CONTENT_MODAL_CLASS }">
          <h3 class="${ MODAL_CLASS }__header">${ title }</h3>
          <button aria-label="${ closeText }"
            class="button--reset ${ CLOSE_MODAL_CLASS }">
            <span class="sr-only">${ closeText } ${ title }</span>
            <i class="icon icon-close" aria-hidden="true"></i>
          </button>

          <div class="eaton-form ${ DOWNLOAD_CLASS }__form ${ formSubmitted ? 'hidden' : '' }">
            <p class="${ DOWNLOAD_CLASS }__preferred-option">${ preferredOptionText }</p>
            <p class="${ DOWNLOAD_CLASS }__disclaimer">${ expirationText }</p>
            <fieldset id="${ FILE_ACCESS_RADIO_GROUP_NAME }" class="faceted-navigation__facet-group ${ DOWNLOAD_CLASS }__radio-button__container">
              <ul class="faceted-navigation__list">
                <li class="faceted-navigation__list-item ${ DOWNLOAD_CLASS }__radio-button__list-item">
                  <label class="submittal-builder__filters__facet-value-label">
                  <input
                    data-analytics-name="submittal-builder-package-download-radio-button-email"
                    data-analytics-state=${ selectedRadioButton === EMAIL_RADIO_VALUE ? 'on' : 'off' }
                    type="radio"
                    class="input input--small"
                    value="${ EMAIL_RADIO_VALUE }"
                    ${ selectedRadioButton === EMAIL_RADIO_VALUE ? 'checked' : '' }
                    name="${ FILE_ACCESS_RADIO_GROUP_NAME }">
                    <span class="inner">${ emailOptionText }</span>
                  </label>
                </li>
                <li class="faceted-navigation__list-item ${ DOWNLOAD_CLASS }__radio-button__list-item">
                  <label class="submittal-builder__filters__facet-value-label">
                    <input
                      data-analytics-name="submittal-builder-package-download-radio-button-download"
                      data-analytics-state=${ selectedRadioButton === EMAIL_RADIO_VALUE ? 'on' : 'off' }
                      type="radio"
                      class="input input--small"
                      value="${ DOWNLOAD_RADIO_VALUE }"
                      ${ selectedRadioButton === DOWNLOAD_RADIO_VALUE ? 'checked' : '' }
                      name="${ FILE_ACCESS_RADIO_GROUP_NAME }">
                    <span class="inner">${ downloadOptionText }</span>
                  </label>
                </li>
              </ul>
            </fieldset>
            <div class="${ DOWNLOAD_CLASS }__email__container ${ selectedRadioButton !== EMAIL_RADIO_VALUE ? 'hidden' : '' }">
              <label for="download-email">${ emailOptionText }</label>
              <input type="text" id="download-email" />
            </div>
            <div class="${ DOWNLOAD_CLASS }__footer">
              <div class="${ selectedRadioButton === DOWNLOAD_RADIO_VALUE ? DOWNLOAD_PACKAGE_BUTTON_CLASS + '--visible' : DOWNLOAD_CLASS + '__footer__column' }">
                <button aria-label="[download package]"
                  data-analytics-name="submittal-builder-package-email-package"
                  class="b-button b-button__primary b-button__primary--light ${ DOWNLOAD_PACKAGE_BUTTON_CLASS } ${ selectedRadioButton !== DOWNLOAD_RADIO_VALUE ? 'hidden' : '' }">
                  <span class="icon icon-download ${ DOWNLOAD_PACKAGE_BUTTON_CLASS }__icon" aria-hidden="true"></span>
                </button>
                <div class="${ DOWNLOAD_CLASS }__file-info__container">
                  <p class="${ DOWNLOAD_CLASS }__file-info">
                    ${ fileName } <span class="${ DOWNLOAD_CLASS }__file-info__file-size">${ sizeText } <bdi>(<span class="${ PACKAGE_SIZE_CLASS }">${ formattedPackageSize }</span>)</bdi></span>
                  </p>
                  <p class="${ DOWNLOAD_CLASS }__disclaimer">${ fileSizeLimitText }</p>
                </div>
              </div>
              <div class="${ DOWNLOAD_CLASS }__footer__column">
                <button
                  data-analytics-name="submittal-builder-package-submit-email"
                  class="b-button b-button__primary b-button__primary--light ${ EMAIL_PACKAGE_BUTTON_CLASS } ${ selectedRadioButton !== EMAIL_RADIO_VALUE ? 'hidden' : '' }">
                    ${ sendEmailText }
                  </button>
              </div>
            </div>
          </div>
          <div class="${ DOWNLOAD_CLASS }__thank-you ${ formSubmitted ? '' : 'hidden' }">
            <span class="icon icon-circle-checkmark ${ DOWNLOAD_CLASS }__icon" aria-hidden="true"></span>
            <p class="${ DOWNLOAD_CLASS }__file-info"> ${ fileName } </p>
            <p class="${ DOWNLOAD_CLASS }__thank-you__message ${ selectedRadioButton !== DOWNLOAD_RADIO_VALUE ? 'hidden' : '' }">${ thankYouDownloadMessage }</p>
            <p class="${ DOWNLOAD_CLASS }__thank-you__message ${ selectedRadioButton !== EMAIL_RADIO_VALUE ? 'hidden' : '' }">${ thankYouEmailMessage }</p>

            ${ selectedRadioButton === EMAIL_RADIO_VALUE ? `
              <div class="row">
                <div class="col-md-6">
                <p class="${ DOWNLOAD_CLASS }__communications__message">${ eatonCommunicationsMessage }</p>
                </div>
                <div class="col-md-6">
                  <div class="${ DOWNLOAD_CLASS }__communications__group" role="group" aria-label="Basic example">
                  <button
                    data-analytics-name="submittal-builder-package-communication-preferences-opt-out"
                    class="b-button b-button__primary b-button__primary--light ${ DOWNLOAD_CLASS }__communications__button ${ NO_COMMUNICATIONS_CLASS }">
                      ${ noText }
                  </button>
                  <a
                    data-analytics-name="submittal-builder-package-communication-preferences-opt-in"
                    href="${ eatonCommunicationsPage }"
                    class="b-button b-button__primary b-button__primary--light ${ DOWNLOAD_CLASS }__communications__button">
                      ${ yesText }
                  </a>
                  </div>
                </div>
              </div>
            ` : '' }
          </div>
        </div>
      `;
    }

    constructor(container) {
      // The constructor should only contain the boiler plate code for finding or creating the reference.
      if (typeof container.dataset.ref === 'undefined') {
        this.ref = Math.random();
        App.SubmittalDownload.refs[this.ref] = this;
        container.dataset.ref = this.ref;
        this.init(container);
      } else {
        // If this element has already been instantiated, use the existing reference.
        return App.SubmittalDownload.refs[container.dataset.ref];
      }
    }

    init(container) {
      this.container = container;
      this.title = this.container.dataset.title;
      this.preferredOptionText = this.container.dataset.preferredOptionText;
      this.downloadOptionText = this.container.dataset.downloadOptionText;
      this.emailOptionText = this.container.dataset.emailOptionText;
      this.sizeText = this.container.dataset.sizeText;
      this.sendEmailText = this.container.dataset.sendEmailText;
      this.expirationText = this.container.dataset.expirationText;
      this.closeText = this.container.dataset.closeText;
      this.thankYouDownloadMessage = this.container.dataset.thankYouDownloadMessage;
      this.thankYouEmailMessage = this.container.dataset.thankYouEmailMessage;
      this.fileSizeLimitText = this.container.dataset.fileSizeLimitText;
      this.invalidEmailMessage = this.container.dataset.invalidEmailMessage;
      this.eatonCommunicationsPage = this.container.dataset.eatonCommunicationsPage;
      this.eatonCommunicationsMessage = this.container.dataset.eatonCommunicationsMessage;
      this.yesText = this.container.dataset.yesText;
      this.noText = this.container.dataset.noText;
      this.addressRequired = this.container.dataset.addressRequired;
      this.assetRequiredMessage = this.container.dataset.assetRequiredMessage;
      this.maxSizeExceededMessage = this.container.dataset.maxSizeExceededMessage;
      this.fileNamePrefix = this.container.dataset.fileNamePrefix;
      this.mergeAssetsFileName = this.container.dataset.mergeAssetsFileName;
      this.isEmailErrorDisplayed = false;

      this.render();
    }

    /**
     * @function get isOpen - check if SubmittalDownload is open (mobile only) and return
     * @returns {boolean}
    */
    get isOpen() {
      return !this.container.classList.contains(HIDDEN_CLASS);
    }

    /**
     * @function fileName - The file name based on the authored prefix and the current time.
    */
    get fileName() {
      const today = new Date();
      let dd = today.getDate();
      let mm = today.getMonth() + 1;
      let yyyy = today.getFullYear();
      if (dd < 10) {dd = '0' + dd;}
      if (mm < 10) {mm = '0' + mm;}

      return this.fileNamePrefix + dd + '_' + mm + '_' + yyyy;
    }

    /**
     * @function set packageSize - Sets the package size in bytes and updates the UI.
     */
    set packageSize(size) {
      this.packageSizeValue = size;
      this.packageSizeElement.textContent = this.formattedPackageSize;
    }

    /**
     * @function get packageSize - Get the package size in bytes.
     */
    get packageSize() {
      return this.packageSizeValue ? this.packageSizeValue : 0;
    }

    /**
     * @function get formattedPackageSize - Get the package size as a string in bytes, kb, mb, or gb.
     */
    get formattedPackageSize() {
      return this.formatPackageSize(this.packageSize);
    }

    formatPackageSize(size, toDecimalPlace) {
      const kb = 1024;
      const mb = 1048576;
      const gb = 1048576000;

      toDecimalPlace = typeof toDecimalPlace !== 'undefined' ? toDecimalPlace : 1;

      if (size > gb) {
        return (size / gb).toFixed(toDecimalPlace) + ' gb';
      } else if (size > mb) {
        return (size / mb).toFixed(toDecimalPlace) + ' mb';
      } else if (size > kb) {
        return (size / kb).toFixed(toDecimalPlace) + ' kb';
      } else if (size > 0) {
        return size.toFixed(toDecimalPlace) + ' bytes';
      } else {
        return '0 mb';
      }
    }

    /**
     * @function usersEmail - The email address the user entered.
     */
    get usersEmail() {
      return this.emailInput.value;
    }

    /**
     * @function emailIsValid - checks if a valid email address has been entered
    */
    get emailIsValid() {
      return /(.+)@(.+){2,}\.(.+){2,}/.test(this.usersEmail);
    }

    /**
     * @function open - shows SubmittalDownload on mobile
    */
    open() {
      this.container.classList.remove(HIDDEN_CLASS);
      BODY_ELEMENT.classList.add(MODAL_OPEN_CLASS);
    }

    /**
     * @function close - hides SubmittalDownload on mobile
    */
    close() {
      this.container.classList.add(HIDDEN_CLASS);
      BODY_ELEMENT.classList.remove(MODAL_OPEN_CLASS);
      this.resetState();
    }

    /**
     * @function resetState - return the form state to its initial form
     * and triggers a re-render
     */
    resetState() {
      this.selectedRadioButton = null;
      this.formSubmitted = false;
      this.removeEmailErrorMessage();

      this.render();
    }
    /**
     * @function removeEmailErrorMessage - removes error message element and sets isEmailErrorDisplayed to false
     */
    removeEmailErrorMessage() {
      const errorMessage = this.container.querySelector('.LV_validation_message');

      if (errorMessage !== null) {
        this.container.querySelector('.LV_invalid').classList.remove('LV_invalid');
        errorMessage.remove();
      }
      this.isEmailErrorDisplayed = false;
    }

    /**
     * @function updateFileAccessSelection - finds the currently selected radio button and sets this.selectedRadioButton equal to its value
     * and triggers a re-render
     */
    updateFileAccessSelection() {
      this.selectedRadioButton = this.container.querySelector('[name="' + FILE_ACCESS_RADIO_GROUP_NAME + '"]:checked').value;
      this.removeEmailErrorMessage();

      this.render();
    }

    /**
     * @function downloadPackage - sets formSubmitted to true and kicks off file download process
     */
    downloadPackage() {
      this.formSubmitted = true;
      this.container.dispatchEvent(new CustomEvent('packageDownloaded'));

      this.render();
    }

    /**
     * @function emailPackage - sets formSubmitted to true and kicks off email process.
     * Dispays an error message if entry is invalid or triggers the email event if
     * the entry is valid
     */
    emailPackage() {
      if (this.emailIsValid) {
        this.formSubmitted = true;
        this.container.dispatchEvent(new CustomEvent('packageEmailed', {detail: this.usersEmail}));
        this.render();
      } else if (!this.isEmailErrorDisplayed) {
        const message = document.createElement('span');
        message.classList.add('LV_validation_message');
        message.textContent = this.invalidEmailMessage;
        this.emailInput.parentNode.classList.add('LV_invalid');
        this.emailInput.parentNode.append(message);
        this.isEmailErrorDisplayed = true;
      }
    }

    render() {
      this.container.innerHTML = App.SubmittalDownload.markup(this);
      this.closeButton = this.container.querySelector(CLOSE_SELECTOR);
      this.downloadButton = this.container.querySelector(DOWNLOAD_PACKAGE_BUTTON_SELECTOR);
      this.emailButton = this.container.querySelector(EMAIL_PACKAGE_BUTTON_SELECTOR);
      this.fileAccessRadioButtons = this.container.querySelectorAll('[name="' + FILE_ACCESS_RADIO_GROUP_NAME + '"]');
      this.selectedRadioButton = this.container.querySelector('[name="' + FILE_ACCESS_RADIO_GROUP_NAME + '"]:checked') ? this.container.querySelector('[name="' + FILE_ACCESS_RADIO_GROUP_NAME + '"]:checked').value : null;
      this.packageSizeElement = this.container.querySelector(PACKAGE_SIZE_SELECTOR);
      this.emailInput = this.container.querySelector('#download-email');
      this.noCommunicationsButton = this.container.querySelector(NO_COMMUNICATIONS_SELECTOR);
      this.formSubmitted = false;

      this.addEventListeners();
    }

    /**
     * @function addEventListeners - Adds event listeners for the internal implementation
     * of this component.
     */
    addEventListeners() {
      this.closeButton.addEventListener('click', () => this.close());
      this.fileAccessRadioButtons.forEach(radioButton => radioButton.addEventListener('click', () => this.updateFileAccessSelection()));
      this.downloadButton.addEventListener('click', () => this.downloadPackage());
      this.emailButton.addEventListener('click', () => this.emailPackage());
      this.emailInput.addEventListener('keyup', () => this.removeEmailErrorMessage());

      if (this.noCommunicationsButton) {
        this.noCommunicationsButton.addEventListener('click', () => this.close());
      }
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

  App.SubmittalDownload.refs = { };
})();
