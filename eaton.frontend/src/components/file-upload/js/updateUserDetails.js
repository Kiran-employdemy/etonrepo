/*
 * Eaton
 * Copyright (C) 2021 Eaton. All Rights Reserved
 */

/* global jQuery:false, guideBridge:false */
/* eslint-disable no-unused-vars */

/**
 * @name updateEmailandUniqueID Update EmailID's and UniqueID before submission
 * @description Update EmailID's and UniqueID before form submission
 * @param {string} moderatorEmailIDs
 * @param {string} uniqueID
 * @param {string} fileAttachments
 * @returns {boolean}
 */
function updateEmailandUniqueID(moderatorEmailIDs, uniqueID, fileAttachments) {
  let moderatorEmailAddress = guideBridge.resolveNode('moderatorEmailIDs');
  let uniqueIdentifier = guideBridge.resolveNode('uniqueIdentifier');
  let fileUploadNode = guideBridge.resolveNode(fileAttachments);
  let fileUploadLimit = fileUploadNode.options.jsonModel.maxFileUploadLimit;

  if (fileUploadNode.value !== null && fileUploadNode.value !== '' ) {
    if (fileUploadLimit !== 'undefined' || fileUploadLimit.value !== '') {
      let fileUploadValues = fileUploadNode.value.split('\n');
      if (fileUploadValues.length > fileUploadLimit) {
        $('.fileRestriction').html(fileUploadNode.options.jsonModel.maxFileUploadErrorMessage);
        $('#myModalFileRestriction').modal('show');
        return false;
      }
    }
    if (moderatorEmailIDs !== null && uniqueID !== null) {
      moderatorEmailAddress.value = moderatorEmailIDs;
      uniqueIdentifier.value = uniqueID;
      guideBridge.submit();
    }
  } else {
    $('.onSubmitMsg').html(fileUploadNode.options.jsonModel.items.fileattachment.validateExpMessage);
    $('#myModalSubmit').modal('show');
    return false;
  }
}
/* eslint-enable no-unused-vars */
