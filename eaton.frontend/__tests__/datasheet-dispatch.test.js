/* eslint-disable no-undef */
const Mustache = require('../src/global/js/vendors/mustache.min');
const {initialScreenLoad, expectedTableInnerHtmlSingleLocaleSingleSku, expectedTableInnerHtmlMultipleLocaleSingleSku,
  expectedTableInnerHtmlMultipleLocaleMultipleSku
} = require( '../__test-fixtures__/DatasheeetDispatchFixtures');
require('../src/components/datasheet-dispatch/js/datasheet-dispatch');
require('../src/components/datasheet-dispatch/js/datasheet-dispatch-constants');
const {singleSkuResponse, singleSkuResponseMultipleLocales, sendMailDataSingle, sendMailDataMultipleLocales,
  multipleSkuResponseMultipleLocales, sendMailDataMultipleLocalesMultipleSkuIds
} = require('../__test-fixtures__/DatasheeetDispatchAjaxFixtures');
const fs = require('fs');
const path = require('path');
let callWrapperMock = {};

dataLayer = {

};

_toConsumableArray = (arr) => {
  if (Array.isArray(arr)) {
    let arr2 = Array(arr.length);
    for (let i = 0; i < arr.length; i++) {
      arr2[i] = arr[i];
    }
    return arr2;
  } else { return Array.from(arr); } };

const prepareExpectationOnCallWrapperMock = (jsonResponse) => {
  externalCallWrapper.post = jest.fn().mockReturnValue(new Promise((resolve) => resolve(jsonResponse)));
  callWrapperMock = jest.spyOn(externalCallWrapper, 'post');
};

const performSendEMailAction = async (emailAddress = 'homer.simpsons@springfield.us') => {
  let emailModalButton = document.getElementById('send-mail');
  expect(emailModalButton.classList).not.toContain('disp-disabled');
  await emailModalButton.click();
  expect(document.getElementById('dispatcher_Email_modal').style.display).toBe('block');
  document.getElementById('txtEmail').value = emailAddress;
  let sendEmailButton = document.getElementById('MailButton');
  await sendEmailButton.click();
};

const prepareFile = (fileName = 'example.xlsx') => {
  return new Promise((resolve, reject) => {
    fs.readFile(path.join(__dirname, fileName), {
      type: 'binary'
    }, (err, data) => {
      const blob = new Blob([data], { type: 'application/octet-stream' });
       // Set the desired file name
      const file = new File([blob], fileName, { type: 'application/octet-stream' });

      resolve(file);
    });
  }).then((file) => {return file;});
};

const dispatchChangeEvent = (excelFile) => {
  return new Promise(async (resolve, reject) => {
    let uploader = document.getElementById('xlsxUploader');
    const changeEvent = new Event('change', {bubbles: true});
    Object.defineProperty(changeEvent, 'target', {
      writable: false,
      value: {
        files: [excelFile]
      }
    });
    uploader.dispatchEvent(changeEvent);
    setTimeout(() => resolve(document.getElementById('articleTA').value), 2000);
  }).then((data) => {return data;});
};
const dispatchBlurEvent = () => {
  return new Promise((resolve, reject) => {
    let textAreField = document.getElementById('articleTA');
    textAreField.dispatchEvent(new Event('blur', {bubles: true}));
    setTimeout(() => resolve(textAreField.value), 2000);
  }).then((data) => {return data;});
};

const prepareTests = (withDescription) => {
  window.Mustache = Mustache;
  document.body.innerHTML = initialScreenLoad(withDescription);
  document.dispatchEvent(new Event('DOMContentLoaded', {bubbles: true}));
};

const prepareClickOnCreateButtonAfterInsertingSkusSingleLocale = async (skus = 'br120') => {
  let textArea = document.getElementById('articleTA');
  textArea.value = skus;
  let createButton = document.getElementById('createDeepLinks2');
  externalCallWrapper = {};
  prepareExpectationOnCallWrapperMock(singleSkuResponse());
  await createButton.click();
  checkThatTheTextAreaIsEnabled();
};

const checkThatTheTextAreaIsEnabled = () => {
  expect(document.getElementById('articleTA').classList).not.toContain('disp-disabled');
  expect(document.getElementById('articleTA').disabled).toBeFalsy();
};

const checkThatCreatButtonsAreEnabled = () => {
  expect(document.getElementById('createDeepLinks2').classList).not.toContain('disp-disabled');
  expect(document.getElementById('createDeepLinks').classList).not.toContain('disp-disabled');
};

const checkThatTheTextAreaAreEnabledButtonsNot = () => {
  checkThatTheTextAreaIsEnabled();
  expect(document.getElementById('reset-dispatcher').classList).toContain('disp-disabled');
  expect(document.getElementById('send-mail').classList).toContain('disp-disabled');
  expect(document.getElementById('createDeepLinks2').classList).toContain('disp-disabled');
  expect(document.getElementById('createDeepLinks').classList).toContain('disp-disabled');
};

beforeEach(async () => {
  prepareTests(true);
});

describe('Datasheet Dispatch Component default locale, insert one catalog number, click create', () => {
  beforeEach(async () => {
    await prepareClickOnCreateButtonAfterInsertingSkusSingleLocale();
  });
  it('should call datasheet dispatch generator API with the selected catalog number', () => {
    expect(callWrapperMock).toBeCalledWith('/eaton/productDatasheetGenerator', '{"skuIDs":["br120"],"locales":["en-us"]}');
  });
  it('should add rows to target ', () => {
    let target = document.getElementById('target');
    expect(target.innerHTML.replace(/\n/g, '').replace(/(>\s{2,}<)/g, '><').trim()).toBe(expectedTableInnerHtmlSingleLocaleSingleSku());
  });
  it('should call the send email API with correct data when clicked on send email', async () => {
    prepareExpectationOnCallWrapperMock('success');
    await performSendEMailAction();
    expect(callWrapperMock).toBeCalledWith('/eaton/datasheets/sendEmail', sendMailDataSingle());
  });
  it('should be possible to edit the text area and click create again with an updated result', () => {
    let textArea = document.getElementById('articleTA');
    textArea.dispatchEvent(new Event ('change', {bubbles: true}));
    textArea.value = 'CH120';
    checkThatTheTextAreaIsEnabled();
    checkThatCreatButtonsAreEnabled();
  });
});

describe('Datasheet Dispatch Component multiple locales, insert one catalog number, click create', () => {
  beforeEach(async () => {
    let textArea = document.getElementById('articleTA');
    textArea.value = 'br120';
    let createButton = document.getElementById('createDeepLinks2');
    externalCallWrapper = {};
    externalCallWrapper.post = jest.fn().mockReturnValue(new Promise((resolve) => resolve(singleSkuResponseMultipleLocales())));
    callWrapperMock = jest.spyOn(externalCallWrapper, 'post');
    document.querySelectorAll('.localeselector input').forEach(checkbox => {
      if (checkbox.value !== 'en-us') {
        checkbox.click();
      }
    });
    await createButton.click();
  });
  it('should call datasheet dispatch generator API with all locales selected catalog number =', () => {
    expect(callWrapperMock).toBeCalledWith('/eaton/productDatasheetGenerator', '{"skuIDs":["br120"],"locales":["en-us","de-de","fr-fr"]}');
  });
  it('should add rows to target ', () => {
    let target = document.getElementById('target');
    expect(target.innerHTML.replace(/\n/g, '').replace(/(>\s{2,}<)/g, '><').trim()).toBe(expectedTableInnerHtmlMultipleLocaleSingleSku());
  });
  it('should call the send email API with the correct data when clicked on send email', async () => {
    prepareExpectationOnCallWrapperMock('success');
    await performSendEMailAction();
    expect(callWrapperMock).toBeCalledWith('/eaton/datasheets/sendEmail', sendMailDataMultipleLocales());
  });
});

describe('Datasheet Dispatch Component multiple locales, insert 2 catalog numbers, different case and space, click create', () => {
  beforeEach(async () => {
    let textArea = document.getElementById('articleTA');
    textArea.value = 'br120, CH120';
    let createButton = document.getElementById('createDeepLinks');
    externalCallWrapper = {};
    externalCallWrapper.post = jest.fn().mockReturnValue(new Promise((resolve) => resolve(multipleSkuResponseMultipleLocales())));
    callWrapperMock = jest.spyOn(externalCallWrapper, 'post');
    document.querySelectorAll('.localeselector input').forEach(checkbox => {
      if (checkbox.value !== 'en-us') {
        checkbox.click();
      }
    });
    await createButton.click();
  });
  it('should call datasheet dispatch generator API with all locales selected catalog number =', () => {
    expect(callWrapperMock).toBeCalledWith('/eaton/productDatasheetGenerator', '{"skuIDs":["br120","CH120"],"locales":["en-us","de-de","fr-fr"]}');
  });
  it('should add rows to target ', () => {
    let target = document.getElementById('target');
    expect(target.innerHTML.replace(/\n/g, '').replace(/(>\s{2,}<)/g, '><').trim()).toBe(expectedTableInnerHtmlMultipleLocaleMultipleSku());
  });
  it('should call the send email API with the correct data when clicked on send email', async () => {
    prepareExpectationOnCallWrapperMock('success');
    await performSendEMailAction();
    expect(callWrapperMock).toBeCalledWith('/eaton/datasheets/sendEmail', sendMailDataMultipleLocalesMultipleSkuIds());
  });
  it('should set error message when email not valid', async () => {
    prepareExpectationOnCallWrapperMock('success');
    await performSendEMailAction('notvalid');
    expect(callWrapperMock).not.toBeCalledWith('/eaton/datasheets/sendEmail', sendMailDataMultipleLocalesMultipleSkuIds());
    expect(document.querySelector('.email-error').innerHTML).toBe('Please enter a vaild email');
  });
});
describe('Datasheet Dispatch Component read excel file, click create', () => {
  it('should set text area with catalog numbers out of the excel file', async () => {
    let excelFile = await prepareFile();
    let textOfTextArea = await dispatchChangeEvent(excelFile);
    expect(textOfTextArea).toBe('BR120,CH120,BR220,CH220,9PX1000GRT');
  });
  it('file too large, should display error modal, only reset button should be enabled', async () => {
    let tooLarge = await prepareFile('too-large-file.har');
    let textOfTextArea = await dispatchChangeEvent(tooLarge);
    expect(textOfTextArea).toBe('');
    expect(document.getElementById('dispatcher_modal').style.display).toBe('block');
    expect(document.getElementById('createDeepLinks2').classList).toContain('disp-disabled');
    expect(document.getElementById('createDeepLinks').classList).toContain('disp-disabled');
    expect(document.getElementById('send-mail').classList).toContain('disp-disabled');
    expect(document.getElementById('reset-dispatcher').classList).not.toContain('disp-disabled');
  });
});
describe('Datasheet Dispatch Component text area blur', () => {
  it('should remove special chars, but leave spaces, dots, hyphens, double quote, and slashes be', async () => {
    document.getElementById('articleTA').value = 'BR120 Part.two - 23/4,BR130`~!@#$%^&*()_+={[}]|\\ catalog/number "';
    let textOfTextArea = await dispatchBlurEvent();
    expect(textOfTextArea).toBe('BR120 Part.two - 23/4,BR130 catalog/number "');
  });
  it('should remove doubles', async () => {
    document.getElementById('articleTA').value = 'br110,br111,br112,br113,br114,br115,br116,br117,br118,br119,br120,br121,br110,br111,br112,br113,br114,br115,br116,br117,br118,br119,br120,br121';
    let textOfTextArea = await dispatchBlurEvent();
    expect(textOfTextArea).toBe('br110,br111,br112,br113,br114,br115,br116,br117,br118,br119,br120,br121');
  });
  it('should replace enter with comma, remove last enter', async () => {
    document.getElementById('articleTA').value = 'br110\nbr111\nbr112\nbr113\n';
    let textOfTextArea = await dispatchBlurEvent();
    expect(textOfTextArea).toBe('br110,br111,br112,br113');
  });
});
describe('Datasheet Dispatch Component default locale, insert more than 20 catalog numbers, click create', () => {
  beforeEach(async () => {
    await prepareClickOnCreateButtonAfterInsertingSkusSingleLocale('br110,br111,br112,br113,br114,br115,br116,br117,br118,br119,br120,br121,br110,br111,br112,br113,br114,br115,br116,br117,br118,br119,br120,br121');
  });
  it('should not call datasheet dispatch generator API', () => {
    expect(callWrapperMock).not.toBeCalled();
  });
  it('should show too much skus modal', async () => {
    expect(document.getElementById('above_sku_error').style.display).toBe('block');
    expect(document.getElementById('articleTA').classList).not.toContain('disp-disabled');
    expect(document.getElementById('reset-dispatcher').classList).not.toContain('disp-disabled');
  });
});

describe('Datasheet Dispatch Component no locale', () => {
  beforeEach(async () => {
    document.querySelectorAll('.localeselector input').forEach(checkbox => {
      if (checkbox.value === 'en-us') {
        checkbox.click();
      }
    });
    await prepareClickOnCreateButtonAfterInsertingSkusSingleLocale('br110');
  });
  it('should not call datasheet dispatch generator API', () => {
    expect(callWrapperMock).not.toBeCalled();
  });
  it('should show no locale modal', async () => {
    expect(document.getElementById('locale_error').style.display).toBe('block');
    checkThatCreatButtonsAreEnabled();
  });
});

describe('Datasheet Dispatch Component no skus', () => {
  beforeEach(async () => {
    document.querySelectorAll('.localeselector input').forEach(checkbox => {
      if (checkbox.value === 'en-us') {
        checkbox.click();
      }
    });
    await prepareClickOnCreateButtonAfterInsertingSkusSingleLocale('');
  });
  it('should not call datasheet dispatch generator API', () => {
    expect(callWrapperMock).not.toBeCalled();
  });
  it('should show no sku modal', async () => {
    expect(document.getElementById('create_error').style.display).toBe('block');
    checkThatCreatButtonsAreEnabled();
  });
});

afterEach(() => {
  document.getElementById('reset-dispatcher').click();
  checkThatTheTextAreaAreEnabledButtonsNot();
  document.querySelectorAll('.localeselector input').forEach(checkbox => {
    if (checkbox.value === 'en-us') {
      expect(checkbox.checked).toBeTruthy();
    } else {
      expect(checkbox.checked).toBeFalsy();
    }
  });
});
