/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

/* global guideBridge:false */
/* eslint-disable no-unused-vars */
/**
 * @private
 */
function changeButtonVisibility(newPanelSom, continueButton, submitButton) {
  if (newPanelSom === guideBridge.eatonSubmitPanel) {
    guideBridge.eatonSubmitButton.visible = true;
    guideBridge.eatonContinueButton.visible = false;
  } else {
    guideBridge.eatonSubmitButton.visible = false;
    guideBridge.eatonContinueButton.visible = true;
  }
}

/**
 * Shows and Hides submit button when appropriate
 * @name eatonToolbarButtonVisibility Eaton Toolbar Button Visibility
 * @param {string} continueButtonSom Som expression of the continue button
 * @param {string} submitButtonSom Som expression of the submit button
 */
function eatonToolbarButtonVisibility(continueButtonSom, submitButtonSom) {
  guideBridge.on('elementNavigationChanged' , function(event, payload) {
    guideBridge.eatonSubmitButton = guideBridge.resolveNode(submitButtonSom);
    guideBridge.eatonContinueButton = guideBridge.resolveNode(continueButtonSom);

    if (payload.target.className !== 'guideToolbar') {
      changeButtonVisibility(payload.target.somExpression);
    }
  });
}

/**
 * Sets the panel which will be used for submitting
 * @name eatonSetSubmitPanel Eaton Set Submit Panel
 * @param {string} newSubmitPanelSom SOM expression of the submit panel
 */
function eatonSetSubmitPanel(newSubmitPanelSom) {
  guideBridge.eatonSubmitPanel = newSubmitPanelSom;
}

/**
 * Validate current panel
 * @name validateCurrentPanel Validate current Panel
 * @return {boolean} Result of validation
 */
function eatonValidateCurrentPanel() {
  const currentPanelSom = guideBridge._guide.rootPanel.navigationContext
    .currentItem.somExpression;

  return guideBridge.validate([], currentPanelSom);
}

/**
 * Navigates to next visible panel
 * @name eatonNextPanel Navigate to next navigable panel
 * @param {string} emailSom Som Expression for email field
 */
function eatonNextPanel(emailSom) {
  // Email needs to be checked separately because it has fancy custom validation
  if (eatonValidateCurrentPanel() && checkValidationStatus(emailSom)) {
    guideBridge.setFocus('nextItem');
    changeButtonVisibility(
      guideBridge._guide.rootPanel.navigationContext.currentItem.somExpression);
  }
}

/**
 * Navigates to previous visible panel
 * @name eatonPrevPanel Navigate to previous navigable panel
 * @param {string} emailSom Som Expression for email field
 */
function eatonPrevPanel(emailSom) {
  if (checkValidationStatus(emailSom)) {
    guideBridge.setFocus('prevItem');
    changeButtonVisibility(
      guideBridge._guide.rootPanel.navigationContext.currentItem.somExpression);
  }
}

/**
 * Submits the form
 * @name eatonSubmitForm Submits the form if valid
 * @param {string} emailSom Som Expression for email field
 * Email needs to be checked separately because it has fancy custom validation
 */
function eatonSubmitForm(emailSom) {
  // Email needs to be checked separately because it has fancy custom validation
  if (checkValidationStatus(emailSom)) {
    guideBridge.submit();
  }
}
/* eslint-enable no-unused-vars */

/**
 * @private
 */
function checkValidationStatus(fieldSom) {
  let fieldElem = guideBridge.resolveNode(fieldSom);

  return fieldElem.validationState;
}
