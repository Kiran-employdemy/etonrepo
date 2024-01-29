/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

/* global jQuery:false, guideBridge:false */
/* eslint-disable no-unused-vars */

/**
 * @name eatonValidateRegexPattern Validate against a regular expression pattern
 * @param {string} value Value from field to validate
 * @param {string} pattern Regular Expression pattern
 * @returns {boolean} whether the field is valid or not
 */
function eatonValidateRegexPattern(value, pattern) {
  const regex = RegExp(pattern);

  return !value || regex.test(value);
}

/**
 * @name eatonValidateEmail Validate email addresses
 * @param {string} value Value from field to validate
 * @returns {boolean}
 */
function eatonValidateEmail(value) {
  const regex = RegExp(/[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/i);

  return !value || regex.test(value);
}

/**
 * @name eatonValidateUsCanPhone Validate US/Canada phone number
 * @param {string} value Value from field to validate
 * @returns {boolean}
 */
function eatonValidateUsCanPhone(value) {
  const regex = RegExp(/^[+]?1? ?(?:(?:\([2-9][0-9]{2}\))|[2-9][0-9]{2})[ \-]?[0-9]{3}[ \-]?[0-9]{4}$/);

  return !value || regex.test(value);
}

/**
 * @name eatonValidateZipPostal Validate US zip code or Canadian postal code
 * @param {string} value Value from field to validate
 * @param {string} countrySom SomExpression of the country field for switching between US/CA
 * @returns {boolean}
 */
function eatonValidateZipPostal(value, countrySom) {
  const usPattern = /^[0-9]{5}(?:[-\s+]?[0-9]{4})?$/;
  const caPattern = /^([A-Za-z]\d[A-Za-z][-\s]?\d[A-Za-z]\d)$/;

  const countryVal = guideBridge.resolveNode(countrySom).value;
  let pattern;

  if (countryVal.toLowerCase() === 'us') {
    pattern = usPattern;
  } else if (countryVal.toLowerCase() === 'ca') {
    pattern = caPattern;
  }

  const regex = RegExp(pattern);

  return !value || !pattern || regex.test(value);
}

/**
 * @name eatonLookupEmail Look up email address to check for existing account
 * @param {string} emailAddressSom Som expression of Email field to check
 * @param {string} messageSom Som expression of Message to hide/display based on result
 * @returns {boolean} true empty or if account is available
 */
function eatonLookupEmail(emailAddressSom, messageSom) {
  // initializing variables
  let email = guideBridge.resolveNode(emailAddressSom);
  let messageElem = guideBridge.resolveNode(messageSom);
  let validationResult = false;

  // Validating value of email field
  if (email.value) {
    let emailValid = eatonValidateEmail(email.value);
    if (emailValid) {
      // If value of email field is valid, check if the value is unique
      jQuery.ajax({
        method: 'GET',
        async: false,
        dataType: 'json',
        url: '/eaton/my-eaton/user-lookup',
        data: { email: email.value },
        complete: function(result) {
          if (result.responseJSON.accountExists) {
            // email already exists so setting error message for user
            email.validateExpMessage = '';
            messageElem.visible = true;
          } else {
            // email is unique so marking email field as valid field
            validationResult = true;
            email.validationState = true;
            messageElem.visible = false;

          }
        }
      });
    } else {
      // Value of email field is not a valid email
      email.validateExpMessage = 'Please enter a valid email';
    }
  } else {
    // Value of email field is null
    email.validateExpMessage = 'Please enter a valid email';
  }

  function emailCheck() {
    let emailAdd = document.querySelector('[aria-label="Email"]').value;
    let comfirmAdd = document.querySelector('[aria-label="Confirm Email"]').value;

    if (emailAdd === comfirmAdd) {
      $('.confirmemailAddress').removeClass('validation-failure');
      $('.confirmemailAddress .guideFieldError').hide();
    } else {
      $('.confirmemailAddress').addClass('validation-failure');
      $('.confirmemailAddress').removeClass('validation-success');
      $('.confirmemailAddress .guideFieldError').show();
      $('.confirmemailAddress .guideFieldError').html('Please ensure "Confirm Email" matches your "Email".');
    }
  }

  $('[aria-label="Email"]').change(function(e) {
    emailCheck();
  });
  $('[aria-label="Confirm Email"]').change(function(e) {
    emailCheck();
  });
  // returning validation state
  return validationResult;
}

/**
 * @name eatonLookupSupplier Looks up supplier number to check for existing Supplier
 * @param {string} supplierNumberSom Som expression of Supplier Number field to check
 * @returns {boolean} true empty or if supplier is available
 */
function eatonLookupSupplier(supplierNumberSom,messageSom) {
  // initializing variables
  let supplierNumber = guideBridge.resolveNode(supplierNumberSom);
  let messageElem = guideBridge.resolveNode(messageSom);
  let validationResult = false;
  // Validating value of supplier Number field
  if (supplierNumber.value) {
    // Check if the supplier Number is valid
    jQuery.ajax({
      method: 'GET',
      async: false,
      dataType: 'json',
      url: '/eaton/my-eaton/supplier-lookup',
      data: { supplierNumber: supplierNumber.value },
      complete: function(result) {
        if (result.responseJSON.supplierExists) {
          // supplier exists so entered supplier number is valid
          validationResult = true;
          supplierNumber.validationState = true;
          messageElem.visible = false;
        } else {
          // Supplier Number is not valid
          supplierNumber.validateExpMessage = '';
          messageElem.visible = true;
        }
      }
    });
  } else {
    // Value of email field is null
    supplierNumber.validateExpMessage = 'Please enter a valid Supplier Number';
  }
  // returning validation state
  return validationResult;
}

/**
 * @name resetPanelFields Reset fields of Panels from Step 3
 * @returns {boolean} true On Successful rest
 */

const resetPanelFields = () => {

  // initializing variables
  let resetResult = false;

  /* Supplier Panel*/
  guideBridge.resolveNode('supplierNumber').resetData();
  guideBridge.resolveNode('supplierGovernmentContractor').resetData();
  guideBridge.resolveNode('supplierDiversityOwned').resetData();
  guideBridge.resolveNode('supplierCommodityCodes').resetData();
  guideBridge.resolveNode('supplierQualityAccreditations').resetData();
  guideBridge.resolveNode('supplierRegionsServiced').resetData();
  guideBridge.resolveNode('supplierRoles').resetData();

    /* Cooper Power Series*/
    // guideBridge.resolveNode('cooper-power-series').value = null;
  guideBridge.resolveNode('cpsAgentRepClassifications').resetData();
  guideBridge.resolveNode('cpsAgentRepNumber').resetData();
  guideBridge.resolveNode('dropdownlist1583957559973').resetData();
  guideBridge.resolveNode('dropdownlist1583957559973_copy_1').resetData();

    /* Cooper Power Series Distributor*/
    // guideBridge.resolveNode('COOPER_POWER_SERIES_PRODUCTS').value = null;
  guideBridge.resolveNode('COOPER_POWER_SERIES_PRODUCTS').resetData();
  guideBridge.resolveNode('cpsAgreementIdNumber').resetData();
  guideBridge.resolveNode('DAsolutions').resetData();
  guideBridge.resolveNode('dropdownlist1583957559973').resetData();
  guideBridge.resolveNode('dropdownlist1583957559973_copy_1').resetData();

    /* Cooper Power Series End-User*/
  guideBridge.resolveNode('COOPER_POWER_SERIES_PRODUCTS_ENDUSER').resetData();
  guideBridge.resolveNode('dropdownlist1583957559973').resetData();
  guideBridge.resolveNode('dropdownlist1583957559973_copy_1').resetData();

    /* Vista Bid-Manager */
    // guideBridge.resolveNode('vista-bid-manager').value = null;
  guideBridge.resolveNode('vistaBidManagerAccessCheckbox').resetData();
  guideBridge.resolveNode('repClassifications').resetData();
  guideBridge.resolveNode('vistaEatonElectricalCustomerNumber').resetData();
  guideBridge.resolveNode('vistaAgencyTaxIdNumber').resetData();
  guideBridge.resolveNode('vistaAgencyDunsNumber').resetData();
  guideBridge.resolveNode('vistaSupplierNumber').resetData();
  guideBridge.resolveNode('vistaSalesGeographyNumbers').resetData();
  guideBridge.resolveNode('vistaVerticalPosition').resetData();

    /* CPQ */
    // guideBridge.resolveNode('cpq').value = null;
  guideBridge.resolveNode('checkbox1584028557827').resetData();
  guideBridge.resolveNode('cpqAgentNumber').resetData();
  guideBridge.resolveNode('cpqComments').resetData();

    /* VOTW or Bid-Manager */
    // guideBridge.resolveNode('votw-bidman').value = null;
  guideBridge.resolveNode('VOTWBidManagerAccessCheckbox').resetData();
  guideBridge.resolveNode('typecustomer').resetData();
  guideBridge.resolveNode('bidmanEatonElectricalCustomerNumber').resetData();
  guideBridge.resolveNode('eatonContact').resetData();

    /* Order Center */
    // guideBridge.resolveNode('order-center').value = null;
  guideBridge.resolveNode('OCProductCategories').resetData();
  guideBridge.resolveNode('userPermissionsDistributor').resetData();
  guideBridge.resolveNode('userPermissionsCustomer').resetData();
  guideBridge.resolveNode('SoldtoAccountNumber').resetData();
  guideBridge.resolveNode('SoldtoCompanyName').resetData();
  guideBridge.resolveNode('soldtoCountry').resetData();
  guideBridge.resolveNode('soldtoCompanyAddress').resetData();
  guideBridge.resolveNode('city').resetData();
  guideBridge.resolveNode('zip').resetData();

    /* WCC */
    // guideBridge.resolveNode('wcc').value = null;
  guideBridge.resolveNode('DUNSNumber').resetData();
  guideBridge.resolveNode('Role').resetData();
  guideBridge.resolveNode('OEMName').resetData();
  guideBridge.resolveNode('OEMDealerCode').resetData();
  guideBridge.resolveNode('ParentDealerName').resetData();
  guideBridge.resolveNode('Address').resetData();
  guideBridge.resolveNode('city').resetData();
  guideBridge.resolveNode('stateProvinceWCC').resetData();
  guideBridge.resolveNode('ZipPostal').resetData();

    /* Project Central */
    // guideBridge.resolveNode('project-central').value = null;
  guideBridge.resolveNode('customerNumber').resetData();
  guideBridge.resolveNode('contact').resetData();
  guideBridge.resolveNode('addtlCustomerNumbers').resetData();

    /* Energy Solutions */
    // guideBridge.resolveNode('energy-automation').value = null;
  guideBridge.resolveNode('EnergyAutomationSolutionsProducts').resetData();

    /* BidManager. VOTW or EU access */
    // guideBridge.resolveNode('bidman_EU_VOTW').value = null;
  guideBridge.resolveNode('BidManagerVOTWEUAccessCheckbox').resetData();
  guideBridge.resolveNode('VOTWCustomerNumber').resetData();
  guideBridge.resolveNode('eatonContact').resetData();
  guideBridge.resolveNode('typecustomer').resetData();
  guideBridge.resolveNode('vistaCustomerNumbers').resetData();

    /* C3 */
    // guideBridge.resolveNode('C3').value = null;
  guideBridge.resolveNode('c3ProductCategories').resetData();
  guideBridge.resolveNode('C3AgentNumber').resetData();
  guideBridge.resolveNode('c3AccountNumber').resetData();
  guideBridge.resolveNode('C3CompanyName').resetData();
  guideBridge.resolveNode('Country').resetData();
  guideBridge.resolveNode('C3CompanyAddress').resetData();
  guideBridge.resolveNode('City').resetData();
  guideBridge.resolveNode('ZipPostalCode').resetData();

    /* End User Other */
  guideBridge.resolveNode('Other').resetData();
  guideBridge.resolveNode('CustomerID').resetData();

    /* Vista */
    // guideBridge.resolveNode('Vista').value = null;
  guideBridge.resolveNode('VistarepClassifications').resetData();
  guideBridge.resolveNode('vistaEatonElectricalCustomerNumber').resetData();
  guideBridge.resolveNode('vistaAgencyTaxIdNumber').resetData();
  guideBridge.resolveNode('vistaAgencyDunsNumber').resetData();
  guideBridge.resolveNode('vistaSupplierNumber').resetData();
  guideBridge.resolveNode('vistaSalesGeographyNumbers').resetData();
  guideBridge.resolveNode('vistaVerticalPosition').resetData();

    /* BidMan-Contractor */
    // guideBridge.resolveNode('bidman-contractor').value = null;
  guideBridge.resolveNode('typecustomer').resetData();
  guideBridge.resolveNode('bidmanEatonElectricalCustomerNumber').resetData();
  guideBridge.resolveNode('eatonContact').resetData();
  guideBridge.resolveNode('comments').resetData();

    /* BidManager EU */
    // guideBridge.resolveNode('bidman-EU').value = null;
  guideBridge.resolveNode('BidManagerEUAccessCheckbox').resetData();
  guideBridge.resolveNode('VOTWCustomerNumber').resetData();
  guideBridge.resolveNode('eatonContact').resetData();
  guideBridge.resolveNode('typecustomer').resetData();
  guideBridge.resolveNode('vistaCustomerNumbers').resetData();

  resetResult = true;

  return resetResult;
};

/* eslint-enable no-unused-vars */
