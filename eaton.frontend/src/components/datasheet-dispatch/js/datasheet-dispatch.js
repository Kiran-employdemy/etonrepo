/* eslint-disable no-undef */
/* eslint-disable no-global-assign */
// noinspection JSConstantReassignment
//-----------------------------------
// List Component - Datasheet Dispatch
//-----------------------------------
const requirePresent = () => typeof require !== 'undefined';

if (requirePresent()) {
  const globalConstants = require('../../../global/js/etn-new-global-constants');
  const localConstants = require('./datasheet-dispatch-constants');
  datasheetDispatchConstants = localConstants.datasheetDispatchConstants;
  literals = globalConstants.literals;
  displayStyles = globalConstants.displayStyles;
  eventListeners = globalConstants.eventListeners;
  XLSX = require('xlsx');
}

const externalCallWrapperPresent = () => typeof externalCallWrapper !== literals.UNDEFINED && typeof externalCallWrapper !== literals.UNDEFINED;


class DatasheetDispatch {
  constructor() {
    this.xlsxuploader = document.getElementById(datasheetDispatchConstants.elementIds.xlsxUploader);
    this.createButtonBottom = document.getElementById(datasheetDispatchConstants.elementIds.buttons.createBottom);
    this.createButtonTop = document.getElementById(datasheetDispatchConstants.elementIds.buttons.createTop);
    this.checkboxes = document.getElementsByName(datasheetDispatchConstants.elementNames.locales);
    this.catalogNumberArea = document.querySelector(datasheetDispatchConstants.querySelectors.textArea);
    this.resetDispatcher = document.getElementById(datasheetDispatchConstants.elementIds.buttons.reset);
    this.sendMail = document.getElementById(datasheetDispatchConstants.elementIds.buttons.sendMail);
    this.textEmailId = document.getElementById(datasheetDispatchConstants.elementIds.emailTextField);
    this.sendMailButton = document.getElementById(datasheetDispatchConstants.elementIds.buttons.modalSendMailButton);
    this.errorEmailMessage = document.querySelector(datasheetDispatchConstants.querySelectors.emailError);
    this.successEmailMessage = document.querySelector(datasheetDispatchConstants.querySelectors.emailSuccess);
    this.resultSection = document.getElementById(datasheetDispatchConstants.elementIds.resultSection);
    this.mustacheTemplates = {
      result: document.getElementById(datasheetDispatchConstants.elementIds.mustacheTemplates.result),
      email: document.getElementById(datasheetDispatchConstants.elementIds.mustacheTemplates.email)
    };
    this.captioni18ArtNo = this.resultSection.dataset.ddNoCap;
    this.captioni18Lang = this.resultSection.dataset.ddLangCap;
    this.captioni18SKULink = this.resultSection.dataset.ddSkuLinkCap;
    this.captioni18PDFLink = this.resultSection.dataset.ddPdfLinkCap;
    this.captioni18SKUSpecLink = this.resultSection.dataset.ddSpecLinkCap;
    this.selectedLocales = [];
    this.EmailLinks = [];
    this.emailData = '';
  }

  init() {
    this.initialState();
    this.bindEvents();
  }

  initialState() {
    this.catalogNumberArea.classList.remove(datasheetDispatchConstants.elementClasses.disabled);
    this.disableCreateButtons();
    this.resetDispatcher.classList.add(datasheetDispatchConstants.elementClasses.disabled);
    this.sendMail.classList.add(datasheetDispatchConstants.elementClasses.disabled);
    this.catalogNumberArea.disabled = false;
    this.catalogNumberArea.value = '';
    this.xlsxuploader.value = '';
    this.resultSection.innerHTML = '';
    if (this.checkboxes.length > 0) {
      this.checkboxes.forEach((checkbox) => {
        checkbox.checked = checkbox.id === datasheetDispatchConstants.elementIds.englishCheckbox;
      });
    }
    this.selectedLocales = [datasheetDispatchConstants.literals.englishLocale];
  }

  disableCreateButtons() {
    this.createButtonTop.classList.add(datasheetDispatchConstants.elementClasses.disabled);
    this.createButtonBottom.classList.add(datasheetDispatchConstants.elementClasses.disabled);
  }
  enableCreateButtons() {
    this.createButtonTop.classList.remove(datasheetDispatchConstants.elementClasses.disabled);
    this.createButtonBottom.classList.remove(datasheetDispatchConstants.elementClasses.disabled);
  }

  bindEvents () {
    let self = this;
    if (this.checkboxes.length > 0) {
      this.checkboxes.forEach((checkbox) => {
        checkbox.onclick = () => {
          checkbox.checked ? self.selectedLocales.push(checkbox.value) : self.selectedLocales = self.selectedLocales.filter((value) => {
            return value !== checkbox.value;
          });
          self.enableCreateButtons();
        };
      });
    }
    if (this.xlsxuploader) {
      this.xlsxuploader.addEventListener(eventListeners.CHANGE, (event) => {
        this.xlsxParser(event, self);
      });
    }
    this.createButtonBottom.onclick = () => { self.getDeepLinks(); };
    this.createButtonTop.onclick = () => { self.getDeepLinks(); };
    this.resetDispatcher.onclick = () => { self.initialState(); } ;
    this.sendMail.onclick = () => {
      self.displayModal(document.getElementById(datasheetDispatchConstants.elementIds.modals.email));
    };
    this.sendMailButton.onclick = () => {self.sendMailUser();};
    this.catalogNumberArea.addEventListener(eventListeners.CHANGE, () => {
      this.enableCreateButtons();
    });
    this.catalogNumberArea.onblur = () => {
      // get data and remove extra spaces
      let textAreaValue = self.catalogNumberArea.value;
      if (textAreaValue.indexOf('\n') > -1) {
        textAreaValue = textAreaValue.replace(/\n/g, ',');
      }
      textAreaValue = textAreaValue.replace(datasheetDispatchConstants.regex.disallowedChars, '');
      let ajaxInputValueOnBlur = [];
      // convert string to string array.
      ajaxInputValueOnBlur = textAreaValue.split(',');
      // Sanitize string array by removing duplicate value
      ajaxInputValueOnBlur = ajaxInputValueOnBlur.reduce(function (noDupArr, entry) {
        if (noDupArr.includes(entry)) {
          return noDupArr;
        } else {
          // eslint-disable-next-line no-undef
          return [].concat(_toConsumableArray(noDupArr), [entry]);
        }
      }, []);

      // Sanitize string array by removing null, undefine & blank values.
      ajaxInputValueOnBlur = ajaxInputValueOnBlur.filter(function (el) {
        if (el && el !== '') {
          return el;
        }
      });
      self.catalogNumberArea.value = ajaxInputValueOnBlur;
      self.enableCreateButtons();
    };
  }

  xlsxParser (evt, datasheetDispatch) {

    if (evt.target.files.length === 1 && evt.target.files[0].size > datasheetDispatchConstants.MAX_FILE_UPLOAD_SIZE) {
      console.log(`The file size must be no more than ${ datasheetDispatchConstants.MAX_FILE_UPLOAD_SIZE / 1024 / 1024 }  MB`);
      datasheetDispatch.displayModal(document.getElementById(datasheetDispatchConstants.elementIds.modals.error));
      datasheetDispatch.disableAllButtonsButReset();
    } else if (evt.target.files.length === 1 && evt.target.files[0].size < datasheetDispatchConstants.MAX_FILE_UPLOAD_SIZE) {
      let selectedFile = evt.target.files[0];
      let reader = new FileReader();
      reader.onload = function (event) {
        let excelEntries = [];
        let data = event.target.result;
        /* eslint-disable*/
        let workbook = XLSX.read(data, {
          type: literals.BINARY
        });
        workbook.SheetNames.forEach(function (sheetName) {
          let xlRowObj = XLSX.utils.sheet_to_row_object_array(workbook.Sheets[sheetName]);
          /* eslint-enable*/
          xlRowObj.forEach(function (obz) {
            excelEntries.push(Object.values(obz).toString());
          });
        });
        excelEntries = excelEntries.filter((item, index) => excelEntries.indexOf(item) === index);
        datasheetDispatch.catalogNumberArea.value = excelEntries;
        datasheetDispatch.catalogNumberArea.dispatchEvent(new Event(eventListeners.BLUR));
        datasheetDispatch.catalogNumberArea.disabled = true;
      };
      reader.onerror = (event) => {
        console.error('File could not be read! Code ' + event.target.error.code);
        datasheetDispatch.displayModal(document.getElementById(datasheetDispatchConstants.elementIds.modals.error));
      };
      reader.readAsBinaryString(selectedFile);
    }
  }

  disableAllButtonsButReset() {
    this.createButtonTop.classList.add(datasheetDispatchConstants.elementClasses.disabled);
    this.createButtonBottom.classList.add(datasheetDispatchConstants.elementClasses.disabled);
    this.resetDispatcher.classList.remove(datasheetDispatchConstants.elementClasses.disabled);
    this.sendMail.classList.add(datasheetDispatchConstants.elementClasses.disabled);
  }
  enableSendMailButton() {
    this.sendMail.classList.remove(datasheetDispatchConstants.elementClasses.disabled);
  }

  getDeepLinks () {
    let textAreaValue = this.catalogNumberArea.value;
    if (textAreaValue.length > 0) {
      if (this.selectedLocales.length > 0) {
        let catalogNumbers = textAreaValue.split(',');
        if (catalogNumbers.length > 20) {
          this.displayModal(document.getElementById(datasheetDispatchConstants.elementIds.modals.aboveSku));
          this.resetDispatcher.classList.remove(datasheetDispatchConstants.elementClasses.disabled);
        } else {
          let data = {
            skuIDs: catalogNumbers.map(value => value.trim()),
            locales: this.selectedLocales
          };
          this.scrollToTop();
          this.disableAllButtonsButReset();
          this.makeCall(datasheetDispatchConstants.pathsTo.generator, data, this.handleData);
        }
      }
      else {
        this.displayModal(document.getElementById(datasheetDispatchConstants.elementIds.modals.localeError));
      }
    } else {
      this.displayModal(document.getElementById(datasheetDispatchConstants.elementIds.modals.createError));
    }
  }

  makeCall(url, data, handleData) {
    if (!externalCallWrapperPresent() && requirePresent()) {
      const {externalCallWrapper} = require('../../../global/js/external-call-wrapper');
      externalCallWrapper.post(url, JSON.stringify(data)).then((data) => {
        if (handleData) {
          handleData(data, this);
        }
      });
    } else {
      externalCallWrapper.post(url, JSON.stringify(data)).then((data) => {
        if (handleData) {
          handleData(data, this);
        }
      });
    }
  }

  handleData (results, dataSheetDispatch) {
    results = JSON.parse(results);
    results.datasheetGen.forEach(skuInfo => {
      let localeWithSkuLink = skuInfo.skuDeepLinksList.find(deepLink => deepLink.skuLink !== '');
      skuInfo.skuLink = localeWithSkuLink ? localeWithSkuLink.skuLink : '';
    });
    results.i18n = {
      articleNumber: dataSheetDispatch.captioni18ArtNo,
      language: dataSheetDispatch.captioni18Lang,
      productSpec: dataSheetDispatch.captioni18SKUSpecLink,
      productSpecAsPdf: dataSheetDispatch.captioni18PDFLink,
      resources: dataSheetDispatch.captioni18SKULink
    };
    dataSheetDispatch.resultSection.innerHTML = window.Mustache.render(dataSheetDispatch.mustacheTemplates.result.innerHTML, results);
    dataSheetDispatch.emailData = window.Mustache.render(dataSheetDispatch.mustacheTemplates.email.innerHTML, results).replace(/\n/g, '').replace(/\s{2,}/g,'');
    dataSheetDispatch.enableSendMailButton();
  }
  sendMailUser () {

    let validRegex = RegExp(datasheetDispatchConstants.regex.validEmail);

    let email = this.textEmailId.value;

    if (email.match(validRegex)) {

      this.errorEmailMessage.textContent = '';
      this.successEmailMessage.textContent = 'Sku Details sent to your email';

      // eslint-disable-next-line no-undef
      let data = {
        email: email,
        skuData: this.emailData,
        pagePath: dataLayer.language
      };

      this.makeCall(datasheetDispatchConstants.pathsTo.sendEmail, data);

      document.getElementById(datasheetDispatchConstants.elementIds.modals.email).querySelector(datasheetDispatchConstants.querySelectors.modalCloseButtons).click();
      this.textEmailId.value = '';
    }

    else {

      this.errorEmailMessage.textContent = 'Please enter a vaild email';
      this.successEmailMessage.textContent = '';
    }

  }
  displayModal(emailModal) {
    emailModal.style.display = displayStyles.block;
    emailModal.classList.add(datasheetDispatchConstants.elementClasses.inwards);
    emailModal.querySelectorAll(datasheetDispatchConstants.querySelectors.modalCloseButtons).forEach(dismiss => {
      dismiss.onclick = () => {
        emailModal.style.display = '';
        emailModal.classList.remove(datasheetDispatchConstants.elementClasses.inwards);
      };
    });
  }
  scrollToTop() {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
  }
}
document.addEventListener('DOMContentLoaded', () => {
  const dataSheetContainer = document.querySelector('.datasheet-dispatch');
  if (dataSheetContainer) {
    new DatasheetDispatch().init();
  }
});

