const datasheetDispatchConstants = {
  MAX_FILE_UPLOAD_SIZE: 2097152,
  literals: {
    englishLocale: 'en-us'

  },
  elementClasses: {
    inwards: 'in',
    disabled: 'disp-disabled'
  },
  pathsTo: {
    generator: '/eaton/productDatasheetGenerator',
    sendEmail: '/eaton/datasheets/sendEmail'
  },
  querySelectors: {
    textArea: '.dispatcher-textarea',
    emailError: '.email-error',
    emailSuccess: '.email-success',
    modalCloseButtons: '[data-dismiss=modal]'
  },
  elementIds: {
    xlsxUploader: 'xlsxUploader',
    buttons: {
      createTop: 'createDeepLinks2',
      createBottom: 'createDeepLinks',
      reset: 'reset-dispatcher',
      sendMail: 'send-mail',
      modalSendMailButton: 'MailButton'
    },
    emailTextField: 'txtEmail',
    resultSection: 'target',
    englishCheckbox: 'English',
    modals: {
      error: 'dispatcher_modal',
      aboveSku: 'above_sku_error',
      localeError: 'locale_error',
      createError: 'create_error',
      email: 'dispatcher_Email_modal'
    },
    mustacheTemplates: {
      result: 'mustache-datasheet-result',
      email: 'mustache-datasheet-email'
    }
  },
  elementNames: {
    locales: 'locales'
  },
  dataFields: {
    headers: {
      articleNumber: 'ddNoCap',
      language: 'ddLangCap',
      skuLink: 'ddSkuLinkCap',
      pdfLink: 'ddPdfLinkCap',
      skuSpecLink: 'ddSpecLinkCap'

    }

  },
  regex: {
    disallowedChars: /[|'{})(!$%*~`?#&@^;=+_\[\]\\]/g,
    validEmail: /[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/i
  }

};

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  module.exports = {datasheetDispatchConstants};
}
