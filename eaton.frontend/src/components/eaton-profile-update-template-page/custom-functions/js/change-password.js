/* jshint esversion: 6 */

class ChangePassword {
  constructor() {
    // eslint-disable-next-line no-undef
    this.passwordFormSel = `[data-path^="/content/forms/af/eaton/${ dataLayer.language }/profile-update/forms/password"]`;
    this.passwordFormEle = document.querySelector(this.passwordFormSel);

    if (this.passwordFormEle) {

      this.passwordFormContainerEle = document.querySelector(`${ this.passwordFormSel }.guideContainerNode`);
      this.passwordSaveButton = document.querySelector(`${ this.passwordFormSel } .guidedsavebutton button`);
      this.oldPasswordEle = document.querySelector(`${ this.passwordFormSel } [name="oldPassword"]`);
      this.newPasswordEle = document.querySelector(`${ this.passwordFormSel } [name="newPassword"]`);
      this.confirmPasswordEle = document.querySelector(`${ this.passwordFormSel } [name="confirmPassword"]`);
      this.allPasswordEles = document.querySelectorAll(`${ this.passwordFormSel } input[type="password"]`);

      this.passwordSavedMessageEle = document.querySelector('.__Profile_Saved_Message');
      this.passwordSavedSuccessMessage = document.getElementById('label-PASSWORD_SAVED_SUCCESS').getAttribute('data-label');

      this.passwordFailedMessageEle = document.querySelector('.__Profile_Failed_Message');
      this.passwordNotSavedErrorMessage = document.getElementById('label-PASSWORD_NOT_SAVED_ERROR').getAttribute('data-label');
      this.passwordPreviousUsedMessage = document.getElementById('label-PASSWORD_PREVIOUSLY_USED').getAttribute('data-label');
      this.invalidCurrentPasswordMessage = document.getElementById('label-INVALID_CURRENT_PASSWORD').getAttribute('data-label');

      this.conditionsIndices = [[1, 1], [1, 2], [2, 1], [2, 2], [2, 3], [2, 4]];
      this.conditionValidatedClass = 'condition-validated';

      this.isOldPasswordPopulated = false;
      this.newPasswordsMatch = false;
      this.isPasswordLength = false;
      this.numValidated = 0;

      this.toggleSaveButton(false, false);
      this.addEventListeners();
      this.preventEnterKey();

    }
  }

  addEventListeners() {

    let self = this;

    this.passwordSaveButton.addEventListener('click', function (event) {

      self.toggleSaveButton(false, true);
      self.hideMessages();

      $.ajax({
        url: `/eaton/my-eaton/save-password.auth.json?oldPassword=${ self.oldPasswordEle.value }&newPassword=${ self.newPasswordEle.value }`,
        async: false,
        type: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        success: function(response) {

          if (response.status === 200) {

            self.passwordSavedMessageEle.innerHTML = self.passwordSavedSuccessMessage;
            self.passwordSavedMessageEle.style.display = 'block';

            self.oldPasswordEle.value = self.newPasswordEle.value = self.confirmPasswordEle.value = '';

            self.resetAllConditions();
            self.toggleSaveButton(false, false);

          } else {

            self.setErrorMessage(response.message);
            self.toggleSaveButton(false, false);

          }

        },
        error: function(error) {
          self.setErrorMessage(error.message);
          self.toggleSaveButton(false, false);
        }

      });

    });

    this.newPasswordEle.addEventListener('keyup', function (event) {

      let passwordValue = event.target.value;

      self.numValidated = 0;

      self.isPasswordLength = passwordValue.length >= 9;
      self.toggleCondition(self.isPasswordLength, 1, 1);

      let passwordContainsUppercase = /[A-Z]+/.test(passwordValue);
      self.toggleCondition(passwordContainsUppercase, 2, 1);
      passwordContainsUppercase && self.numValidated++;

      let passwordContainsLowercase = /[a-z]+/.test(passwordValue);
      self.toggleCondition(passwordContainsLowercase, 2, 2);
      passwordContainsLowercase && self.numValidated++;

      let passwordContainsNumber = /\d/.test(passwordValue);
      self.toggleCondition(passwordContainsNumber, 2, 3);
      passwordContainsNumber && self.numValidated++;

      let passwordContainsSpecial = /[!@#$%^&*()_+\-=\[\]{};":"\\|,.<>\/?]+/.test(passwordValue);
      self.toggleCondition(passwordContainsSpecial, 2, 4);
      passwordContainsSpecial && self.numValidated++;

    });

    this.allPasswordEles.forEach(passwordEle => {
      passwordEle.addEventListener('keyup', function (event) {

        self.hideMessages();

        self.newPasswordsMatch = ( self.newPasswordEle.value !== '' || self.confirmPasswordEle.value !== '' ) && ( self.newPasswordEle.value === self.confirmPasswordEle.value );
        self.toggleCondition(self.newPasswordsMatch, 1, 2);

        self.isOldPasswordPopulated = self.oldPasswordEle.value !== '';

        let isNewPasswordValid = self.isOldPasswordPopulated && self.newPasswordsMatch && self.isPasswordLength && self.numValidated === 4;

        self.toggleSaveButton( isNewPasswordValid, false );

      });
    });
  }

  preventEnterKey() {
    this.passwordFormContainerEle.addEventListener('keydown', function (event) {
      event.keyCode === 13 && event.preventDefault();
    });
  }

  toggleSaveButton(isEnabled, loadingCursor) {

    this.passwordSaveButton.disabled = !isEnabled;

    if (loadingCursor) {
      this.passwordSaveButton.classList.add('loading');
    } else {
      this.passwordSaveButton.classList.remove('loading');
    }

  }

  hideMessages() {
    this.passwordSavedMessageEle.style.display = 'none';
    this.passwordFailedMessageEle.style.display = 'none';
  }

  resetAllConditions() {
    this.conditionsIndices.forEach(condition => {
      this.toggleCondition(false, condition[0], condition[1]);
    });
  }

  toggleCondition(isValid, conditionListNum, conditionSubListNum) {
    let conditionMessageSel = `.profile-landing__list li:nth-child(${ conditionListNum }) > .profile-landing__list-sub li:nth-child(${ conditionSubListNum })`;
    let conditionIconSel = `${ conditionMessageSel } .icon`;

    if (isValid) {
      document.querySelector(conditionIconSel) !== null && document.querySelector(conditionIconSel).remove();
      document.querySelector(conditionMessageSel).classList.add(this.conditionValidatedClass);
      document.querySelector(conditionMessageSel).appendChild(this.getConditionIconEle());
    } else {
      document.querySelector(conditionMessageSel).classList.remove(this.conditionValidatedClass);
      document.querySelector(conditionIconSel) !== null && document.querySelector(conditionIconSel).remove();
    }

  }

  getConditionIconEle() {
    let passwordConditionIconEle = document.createElement('i');
    passwordConditionIconEle.classList.add('icon', 'icon-green', 'icon-checkmark', 'icon-user-desktop-only');
    passwordConditionIconEle.setAttribute('aria-hidden', 'true');

    return passwordConditionIconEle;
  }

  setErrorMessage(responseText) {

    let errorMessage = this.passwordNotSavedErrorMessage;
    if (responseText.includes('PASSWORD_PREVIOUSLY_USED')) {
      errorMessage += ' ' + this.passwordPreviousUsedMessage;
    } else if (responseText.includes('INVALID_CURRENT_PASSWORD')) {
      errorMessage += ' ' + this.invalidCurrentPasswordMessage;
    } else {
      errorMessage += ` ${ responseText }`;
    }
    this.passwordFailedMessageEle.innerHTML = errorMessage;
    this.passwordFailedMessageEle.style.display = 'block';

  }

}


document.addEventListener('DOMContentLoaded', function() {
  // eslint-disable-next-line no-unused-vars
  let changePassword = new ChangePassword();
});



if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {

  // eslint-disable-next-line no-global-assign
  $ = require('jquery');

  window.dataLayer = [];
  window.dataLayer.language = 'en-us';

  module.exports = ChangePassword;
}
