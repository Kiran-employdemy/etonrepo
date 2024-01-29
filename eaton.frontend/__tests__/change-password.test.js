/* jshint esversion: 6 */

const ChangePassword = require('../src/components/eaton-profile-update-template-page/custom-functions/js/change-password.js');


const { changePasswordForm,
          changePasswordTranslations,
          changePasswordAjaxRequest } = require('../__test-fixtures__/ChangePasswordFixtures');

let changePassword;

const $ = require('jquery');
let ajaxSpy = {};

let toggleSaveButtonSpy, resetAllConditionsSpy, hideMessageSpy, setErrorMessageSpy;

const conditionValidatedClass = 'condition-validated';
let passwordFormContainer;
let passwordFormSel;
let oldPassword;
let newPassword;
let confirmPassword;
let passwordSaveButton;
let passwordSavedMessage;
let passwordFailedMessage;

beforeEach(() => {

  jest.restoreAllMocks();

  window.dataLayer = [];
  window.dataLayer.language = 'en-us';
  document.body.innerHTML = changePasswordForm + changePasswordTranslations;

  ajaxSpy = jest.spyOn($, 'ajax');

  changePassword = new ChangePassword();

  toggleSaveButtonSpy = jest.spyOn(changePassword, 'toggleSaveButton');
  resetAllConditionsSpy = jest.spyOn(changePassword, 'resetAllConditions');
  hideMessageSpy = jest.spyOn(changePassword, 'hideMessages');
  setErrorMessageSpy = jest.spyOn(changePassword, 'setErrorMessage');

  passwordFormSel = `[data-path^='/content/forms/af/eaton/${dataLayer.language}/profile-update/forms/password']`;
  passwordFormContainer = document.querySelector(`${ passwordFormSel }.guideContainerNode`);
  passwordSaveButton = document.querySelector(`${ passwordFormSel } .guidedsavebutton button`);

  oldPassword = document.querySelector('[name="oldPassword"]');
  oldPassword.value = 'Test1010$';
  newPassword = document.querySelector('[name="newPassword"]');
  newPassword.value = 'Test1111$';
  confirmPassword = document.querySelector('[name="confirmPassword"]');
  confirmPassword.value = 'Test1111$';

  passwordSavedMessage = document.querySelector('.__Profile_Saved_Message');
  passwordFailedMessage = document.querySelector('.__Profile_Failed_Message');
});

const clickSaveButton = () => {

  let clickEvent = new MouseEvent('click', {
    bubbles: true,
    cancelable: true,
    view: window
  });

  passwordSaveButton.dispatchEvent(clickEvent);

};

describe('successful change password servlet calls', () => {

  it('password change was successful',  () => {

    ajaxSpy.mockImplementationOnce(options => {
      typeof options.success === 'function' && options.success({
        message: 'SUCCESS',
        status: 200
      });
    });

    clickSaveButton();

    expect(toggleSaveButtonSpy).toHaveBeenCalledWith(false, true);
    expect(hideMessageSpy).toHaveBeenCalled();

    expect(ajaxSpy).toBeCalledWith(changePasswordAjaxRequest());

    expect(passwordSavedMessage.innerHTML).toBe('Password saved.');
    expect(passwordSavedMessage.style.display).toBe('block');

    expect(oldPassword.value).toBe('');
    expect(newPassword.value).toBe('');
    expect(confirmPassword.value).toBe('');

    expect(resetAllConditionsSpy).toHaveBeenCalled();
    expect(toggleSaveButtonSpy).toHaveBeenCalledWith(false, false);

  });

});

describe('change password servlet error responses', () => {

  let expectedErrorMessage;

  afterEach(() => {

    ajaxSpy.mockImplementationOnce(options => {
      typeof options.success === 'function' && options.success({
        message: expectedErrorMessage,
        status: 400
      });
    });

    clickSaveButton();

    expect(setErrorMessageSpy).toHaveBeenCalledWith(expectedErrorMessage);
    expect(toggleSaveButtonSpy).toHaveBeenCalledWith(false, false);

    // password fields should still have user input
    expect(oldPassword.value).toBe('Test1010$');
    expect(newPassword.value).toBe('Test1111$');
    expect(confirmPassword.value).toBe('Test1111$');

  });

  it('should show error message when user enters previously used password',  () => {

    expectedErrorMessage = 'Error:PASSWORD_PREVIOUSLY_USED:userPassword';

  });

  it('should show error message when user enters invalid current password',  () => {

    expectedErrorMessage = 'Error:INVALID_CURRENT_PASSWORD:currentUserPassword';

  });

});

describe('change password servlet other errors', () => {


  it('should show error message and disable save button when error occurs', () => {

    ajaxSpy.mockImplementationOnce(options => {
      typeof options.error === 'function' && options.error({
        message: 'Internal Server Error',
        status: 500
      });
    });

    clickSaveButton();

    expect(toggleSaveButtonSpy).toHaveBeenCalledWith(false, true);
    expect(hideMessageSpy).toHaveBeenCalled();

    expect(ajaxSpy).toHaveBeenCalledWith(changePasswordAjaxRequest());

    expect(toggleSaveButtonSpy).toHaveBeenCalledWith(false, false);
    expect(setErrorMessageSpy).toHaveBeenCalledWith('Internal Server Error');


  });


});

describe('password conditions', () => {

  let lengthConditionMessage, lengthConditionIcon, matchConditionMessage, matchConditionIcon,
    uppercaseConditionMessage, uppercaseConditionIcon, lowercaseConditionMessage, lowercaseConditionIcon,
    numberConditionMessage, numberConditionIcon, specialCharConditionMessage, specialCharConditionIcon;

  let iconEle;
  let toggleConditionSpy;
  const conditions = [[1, 1], [1, 2], [2, 1], [2, 2], [2, 3], [2, 4]];

  const keyUpEvent = (element) => {
    let event = new KeyboardEvent('keyup', { key: 'ArrowUp' });
    element.dispatchEvent(event);
  };

  beforeEach(() => {

    toggleConditionSpy = jest.spyOn(changePassword, 'toggleCondition');

    lengthConditionMessage = document.querySelector('.profile-landing__list li:nth-child(1) > .profile-landing__list-sub li:nth-child(1)');
    lengthConditionIcon = document.querySelector('.profile-landing__list li:nth-child(1) > .profile-landing__list-sub li:nth-child(1) .icon');
    matchConditionMessage = document.querySelector('.profile-landing__list li:nth-child(1) > .profile-landing__list-sub li:nth-child(2)');
    matchConditionIcon = document.querySelector('.profile-landing__list li:nth-child(1) > .profile-landing__list-sub li:nth-child(2) .icon');
    uppercaseConditionMessage = document.querySelector('.profile-landing__list li:nth-child(2) > .profile-landing__list-sub li:nth-child(1)');
    uppercaseConditionIcon = document.querySelector('.profile-landing__list li:nth-child(2) > .profile-landing__list-sub li:nth-child(1) .icon');
    lowercaseConditionMessage = document.querySelector('.profile-landing__list li:nth-child(2) > .profile-landing__list-sub li:nth-child(2)');
    lowercaseConditionIcon = document.querySelector('.profile-landing__list li:nth-child(2) > .profile-landing__list-sub li:nth-child(2) .icon');
    numberConditionMessage = document.querySelector('.profile-landing__list li:nth-child(2) > .profile-landing__list-sub li:nth-child(3)');
    numberConditionIcon = document.querySelector('.profile-landing__list li:nth-child(2) > .profile-landing__list-sub li:nth-child(3) .icon');
    specialCharConditionMessage = document.querySelector('.profile-landing__list li:nth-child(2) > .profile-landing__list-sub li:nth-child(4)');
    specialCharConditionIcon = document.querySelector('.profile-landing__list li:nth-child(2) > .profile-landing__list-sub li:nth-child(4) .icon');

    iconEle = document.createElement('i');
    iconEle.classList.add('icon', 'icon-green', 'icon-checkmark', 'icon-user-desktop-only');
    iconEle.setAttribute('aria-hidden', 'true');
  });

  it('should show invalid conditions when password has no uppercase characters', () => {

    newPassword.value = confirmPassword.value = 'test12345$';

    keyUpEvent(newPassword);

    expect(uppercaseConditionMessage.classList).not.toContain(conditionValidatedClass);
    expect(uppercaseConditionIcon).toBeNull();

    expect(lengthConditionMessage.classList).toContain(conditionValidatedClass);
    expect(lengthConditionIcon).toBeDefined();

    expect(lowercaseConditionMessage.classList).toContain(conditionValidatedClass);
    expect(lowercaseConditionIcon).toBeDefined();

    expect(numberConditionMessage.classList).toContain(conditionValidatedClass);
    expect(numberConditionIcon).toBeDefined();

    expect(specialCharConditionMessage.classList).toContain(conditionValidatedClass);
    expect(specialCharConditionIcon).toBeDefined();

  });

  it('should show invalid conditions when password has no lowercase characters', () => {

    newPassword.value = confirmPassword.value = 'TEST12345$';

    keyUpEvent(newPassword);

    expect(uppercaseConditionMessage.classList).toContain(conditionValidatedClass);
    expect(uppercaseConditionIcon).toBeDefined();

    expect(lengthConditionMessage.classList).toContain(conditionValidatedClass);
    expect(lengthConditionIcon).toBeDefined();

    expect(lowercaseConditionMessage.classList).not.toContain(conditionValidatedClass);
    expect(lowercaseConditionIcon).toBeNull();

    expect(numberConditionMessage.classList).toContain(conditionValidatedClass);
    expect(numberConditionIcon).toBeDefined();

    expect(specialCharConditionMessage.classList).toContain(conditionValidatedClass);
    expect(specialCharConditionIcon).toBeDefined();

  });

  it('should show invalid conditions when password has no numerical characters', () => {
    newPassword.value = confirmPassword.value = 'Testtest$$';

    keyUpEvent(newPassword);

    expect(uppercaseConditionMessage.classList).toContain(conditionValidatedClass);
    expect(uppercaseConditionIcon).toBeDefined();

    expect(lengthConditionMessage.classList).toContain(conditionValidatedClass);
    expect(lengthConditionIcon).toBeDefined();

    expect(lowercaseConditionMessage.classList).toContain(conditionValidatedClass);
    expect(lowercaseConditionIcon).toBeDefined();

    expect(numberConditionMessage.classList).not.toContain(conditionValidatedClass);
    expect(numberConditionIcon).toBeNull();

    expect(specialCharConditionMessage.classList).toContain(conditionValidatedClass);
    expect(specialCharConditionIcon).toBeDefined();


  });

  it('should show invalid conditions when password has no special characters', () => {
    newPassword.value = confirmPassword.value = 'Testtest444';

    keyUpEvent(newPassword);

    expect(uppercaseConditionMessage.classList).toContain(conditionValidatedClass);
    expect(uppercaseConditionIcon).toBeDefined();

    expect(lengthConditionMessage.classList).toContain(conditionValidatedClass);
    expect(lengthConditionIcon).toBeDefined();

    expect(lowercaseConditionMessage.classList).toContain(conditionValidatedClass);
    expect(lowercaseConditionIcon).toBeDefined();

    expect(numberConditionMessage.classList).toContain(conditionValidatedClass);
    expect(numberConditionIcon).toBeDefined();

    expect(specialCharConditionMessage.classList).not.toContain(conditionValidatedClass);
    expect(specialCharConditionIcon).toBeNull();
  });

  it('should show invalid conditions when password has less than 9 characters', () => {
    newPassword.value = confirmPassword.value = 'Test4!';

    keyUpEvent(newPassword);

    expect(uppercaseConditionMessage.classList).toContain(conditionValidatedClass);
    expect(uppercaseConditionIcon).toBeDefined();

    expect(lengthConditionMessage.classList).not.toContain(conditionValidatedClass);
    expect(lengthConditionIcon).toBeNull();

    expect(lowercaseConditionMessage.classList).toContain(conditionValidatedClass);
    expect(lowercaseConditionIcon).toBeDefined();

    expect(numberConditionMessage.classList).toContain(conditionValidatedClass);
    expect(numberConditionIcon).toBeDefined();

    expect(specialCharConditionMessage.classList).toContain(conditionValidatedClass);
    expect(specialCharConditionIcon).toBeDefined();
  });

  it('should confirm all conditions are reset to invalid', () => {

    lengthConditionMessage.classList.add(conditionValidatedClass);
    matchConditionMessage.classList.add(conditionValidatedClass);
    uppercaseConditionMessage.classList.add(conditionValidatedClass);
    lowercaseConditionMessage.classList.add(conditionValidatedClass);
    numberConditionMessage.classList.add(conditionValidatedClass);
    specialCharConditionMessage.classList.add(conditionValidatedClass);

    lengthConditionMessage.appendChild(iconEle);
    matchConditionMessage.appendChild(iconEle);
    uppercaseConditionMessage.appendChild(iconEle);
    lowercaseConditionMessage.appendChild(iconEle);
    numberConditionMessage.appendChild(iconEle);
    specialCharConditionMessage.appendChild(iconEle);

    changePassword.resetAllConditions();

    expect(lengthConditionMessage.classList).not.toContain(conditionValidatedClass);
    expect(matchConditionMessage.classList).not.toContain(conditionValidatedClass);
    expect(uppercaseConditionMessage.classList).not.toContain(conditionValidatedClass);
    expect(lowercaseConditionMessage.classList).not.toContain(conditionValidatedClass);
    expect(numberConditionMessage.classList).not.toContain(conditionValidatedClass);
    expect(specialCharConditionMessage.classList).not.toContain(conditionValidatedClass);

    expect(lengthConditionIcon).toBeNull();
    expect(matchConditionIcon).toBeNull();
    expect(uppercaseConditionIcon).toBeNull();
    expect(lowercaseConditionIcon).toBeNull();
    expect(numberConditionIcon).toBeNull();
    expect(specialCharConditionIcon).toBeNull();

  });

  it('should confirm all conditions toggle to valid', () => {

    conditions.forEach(condition => {
      changePassword.toggleCondition(true, condition[0], condition[1]);
    });

    expect(lengthConditionMessage.classList).toContain(conditionValidatedClass);
    expect(matchConditionMessage.classList).toContain(conditionValidatedClass);
    expect(uppercaseConditionMessage.classList).toContain(conditionValidatedClass);
    expect(lowercaseConditionMessage.classList).toContain(conditionValidatedClass);
    expect(numberConditionMessage.classList).toContain(conditionValidatedClass);
    expect(specialCharConditionMessage.classList).toContain(conditionValidatedClass);

    expect(lengthConditionIcon).toBeDefined();
    expect(matchConditionIcon).toBeDefined();
    expect(uppercaseConditionIcon).toBeDefined();
    expect(lowercaseConditionIcon).toBeDefined();
    expect(numberConditionIcon).toBeDefined();
    expect(specialCharConditionIcon).toBeDefined();

  });

  it('should confirm all conditions toggle to not valid', () => {

    conditions.forEach(condition => {
      changePassword.toggleCondition(false, condition[0], condition[1]);
    });

    expect(lengthConditionMessage.classList).not.toContain(conditionValidatedClass);
    expect(matchConditionMessage.classList).not.toContain(conditionValidatedClass);
    expect(uppercaseConditionMessage.classList).not.toContain(conditionValidatedClass);
    expect(lowercaseConditionMessage.classList).not.toContain(conditionValidatedClass);
    expect(numberConditionMessage.classList).not.toContain(conditionValidatedClass);
    expect(specialCharConditionMessage.classList).not.toContain(conditionValidatedClass);

    expect(lengthConditionIcon).toBeNull();
    expect(matchConditionIcon).toBeNull();
    expect(uppercaseConditionIcon).toBeNull();
    expect(lowercaseConditionIcon).toBeNull();
    expect(numberConditionIcon).toBeNull();
    expect(specialCharConditionIcon).toBeNull();

  });

  it('should show invalid conditions when new and confirm passwords don\'t match', () => {
    newPassword.value = 'Test2222$';
    confirmPassword.value = 'Test1111$';

    keyUpEvent(newPassword);

    expect(hideMessageSpy).toHaveBeenCalled();
    expect(toggleConditionSpy).toHaveBeenCalledTimes(6);
    expect(toggleSaveButtonSpy).toHaveBeenCalledWith(false, false);

    expect(matchConditionMessage.classList).not.toContain(conditionValidatedClass);
    expect(matchConditionIcon).toBeNull();

  });

  it('should show valid conditions when new and confirm passwords match', () => {
    newPassword.value = 'Test1010$';
    confirmPassword.value = 'Test1010$';

    keyUpEvent(newPassword);

    expect(hideMessageSpy).toHaveBeenCalled();
    expect(toggleConditionSpy).toHaveBeenCalledTimes(6);
    expect(toggleSaveButtonSpy).toHaveBeenCalledWith(true, false);

    expect(matchConditionMessage.classList).toContain(conditionValidatedClass);
    expect(matchConditionIcon).toBeDefined();

  });

  it('should disable save button when old password is not populated', () => {
    oldPassword.value = '';
    newPassword.value = 'Test1010$';
    confirmPassword.value = 'Test1010$';

    keyUpEvent(newPassword);

    expect(toggleConditionSpy).toHaveBeenCalledTimes(6);
    expect(hideMessageSpy).toHaveBeenCalled();
    expect(toggleSaveButtonSpy).toHaveBeenCalledWith(false, false);

  });

  it('should enable save button when all conditions are met', () => {
    oldPassword.value = 'test';
    newPassword.value = 'Test1010$';
    confirmPassword.value = 'Test1010$';

    keyUpEvent(newPassword);

    expect(toggleConditionSpy).toHaveBeenCalledTimes(6);
    expect(hideMessageSpy).toHaveBeenCalled();
    expect(toggleSaveButtonSpy).toHaveBeenCalledWith(true, false);
  });

  it('should return a valid condition icon element', () => {
    expect(changePassword.getConditionIconEle()).toEqual(iconEle);
  });

});

describe('password messages', () => {

  it('should hide all password user messages', () => {
    passwordSavedMessage.style.display = 'block';
    passwordFailedMessage.style.display = 'block';

    changePassword.hideMessages();

    expect(passwordSavedMessage.style.display).toBe('none');
    expect(passwordFailedMessage.style.display).toBe('none');

  });

  it('should set correct error messages', () => {

    changePassword.setErrorMessage('Error:PASSWORD_PREVIOUSLY_USED:userPassword');
    expect(passwordFailedMessage.innerHTML).toBe('Password not saved. Previous password was used.');
    expect(passwordFailedMessage.style.display).toBe('block');


    changePassword.setErrorMessage('Error:INVALID_CURRENT_PASSWORD:currentUserPassword');
    expect(passwordFailedMessage.innerHTML).toBe('Password not saved. Current password is invalid.');
    expect(passwordFailedMessage.style.display).toBe('block');

    changePassword.setErrorMessage('Error:OTHER');
    expect(passwordFailedMessage.innerHTML).toBe('Password not saved. Error:OTHER');
    expect(passwordFailedMessage.style.display).toBe('block');

  });

});

describe('save button', () => {

  it('should be enabled without the loading cursor', () => {

    changePassword.toggleSaveButton(true, false);
    expect(passwordSaveButton.disabled).toBeFalsy();
    expect(passwordSaveButton.classList).not.toContain('loading');
  });

  it('should be disabled without the loading cursor', () => {
    changePassword.toggleSaveButton(false, false);
    expect(passwordSaveButton.disabled).toBeTruthy();
    expect(passwordSaveButton.classList).not.toContain('loading');

  });

  it('should be disabled with the loading cursor', () => {
    changePassword.toggleSaveButton(false, true);
    expect(passwordSaveButton.disabled).toBeTruthy();
    expect(passwordSaveButton.classList).toContain('loading');

  });


});
