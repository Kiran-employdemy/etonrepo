/**
 *
 *
 *
 * - THIS IS AN AUTOGENERATED FILE. DO NOT EDIT THIS FILE DIRECTLY -
 * - Generated by Gulp (gulp-babel).
 *
 *
 *
 *
 */


'use strict';

$('.submit-button,.submit-button-style').click(function () {
  if (window.form !== undefined && window.form) {
    $('.multiple_checkbox[value="0"]').each(function (i, el) {
      if ($('[type="hidden"][name="' + $(el).attr('name') + '"]').length === 0) {
        $('<input>').attr({
          type: 'hidden',
          name: $(el).attr('name'),
          value: '0'
        }).appendTo(window.form);
        el.value = '0';
      } else {
        el.value = '1';
      }
    });
  }
  $('.LV_invalid_field').first().focus();
});

$(document).ready(function () {
  $('input[type="checkbox"]').filter('.disclaimer-checkbox').attr('autocomplete', 'off');
  $('input[type="hidden"]').filter('[name^="tag_Optin"]').removeAttr('value');
  $('.elq-form input[type="text"]').removeAttr('value', '');
});

$('.multiple_checkbox').click(function () {
  if ($(this).attr('value') === '0') {
    $(this).attr('value', '1');
  } else {
    $(this).attr('value', '0');
  }
});

/**
 * Custom script to manage spinner and disable multi form submit.
 */

/**
 * Add eventlistner for .elf-form.
 */
var eloquaForms = document.querySelectorAll('.elq-form');
eloquaForms.forEach(function (form) {
  form.addEventListener('submit', function () {
    console.log('User has clicked on the button!');
    handleEloquaFormSubmit(form);
  });
});

/**
 * handleEloquaFormSubmit(form) - triggered on form submit.
 */
function handleEloquaFormSubmit(form) {
  var submitButton = form.querySelector('input[type=submit]');
  // Reset existing loader
  var tempElements = form.querySelectorAll('.loader');
  if (tempElements.length > 0) {
    tempElements.forEach(function (element) {
      element.remove();
    });
  }

  // Reset existing aem-eloqua-form-loader.
  var tempElements2 = form.querySelectorAll('.aem-eloqua-form-loader');
  if (tempElements2.length > 0) {
    tempElements2.forEach(function (element) {
      element.remove();
    });
  }

  // Create the loader HTML element.
  var spinner = document.createElement('span');
  spinner.classList.add('aem-eloqua-form-loader');
  spinner.innerHTML = '<span class="aem-loader-circle"></span>';
  submitButton.setAttribute('disabled', true);
  submitButton.disabled = true;
  submitButton.style.cursor = 'wait';
  form.appendChild(spinner);

  if (form.querySelectorAll('.LV_invalid_field').length > 0) {
    resetEloquaSubmitButton(form);
    form.querySelector('.LV_invalid_field').focus();
    return false;
  } else {
    return true;
  }
}

/**
 * resetEloquaSubmitButton(elqForm) - reset form submit button on error.
 */
function resetEloquaSubmitButton(elqForm) {
  var submitButtons = elqForm.querySelector('input[type=submit]');
  submitButtons.removeAttribute('disabled');
  submitButtons.disabled = false;
  submitButtons.style.cursor = 'pointer';

  // Reset eloqua loader
  var tempElements = elqForm.querySelectorAll('.loader');
  if (tempElements.length > 0) {
    tempElements.forEach(function (element) {
      element.remove();
    });
  }

  // Reset AEM default eloqua loader
  var tempElements2 = elqForm.querySelectorAll('.aem-eloqua-form-loader');
  if (tempElements2.length > 0) {
    tempElements2.forEach(function (element) {
      element.remove();
    });
  }
}

/*
* Add eventlistner for form submit button.
*/
var addEloquaChangeHandler = function addEloquaChangeHandler(elements) {
  var elementArray = Array.from(elements);
  elementArray.forEach(function (el) {
    el.addEventListener('change', function () {
      resetEloquaSubmitButton(el.form);
    });
  });
};

/**
 * Remove loader coming from eloqua when user come back via. browser back button.
 */
var loaders = document.querySelectorAll('.eloqua-form .loader');
window.addEventListener('popstate', function () {
  loaders.forEach(function (loader) {
    loader.remove();
  });
});

/**
 * Remove aem loader added on form submit when user come back via. browser back button.
 */
var aemLoaders = document.querySelectorAll('.eloqua-form .aem-eloqua-form-loader');
window.addEventListener('popstate', function () {
  aemLoaders.forEach(function (aemLoaders) {
    aemLoaders.remove();
  });
});

/**
 * EventListner to trgger resetsubmit and loader on changing values.
 */
addEloquaChangeHandler(document.querySelectorAll('.eloqua-form input'));
addEloquaChangeHandler(document.querySelectorAll('.eloqua-form select'));
addEloquaChangeHandler(document.querySelectorAll('.eloqua-form textarea'));