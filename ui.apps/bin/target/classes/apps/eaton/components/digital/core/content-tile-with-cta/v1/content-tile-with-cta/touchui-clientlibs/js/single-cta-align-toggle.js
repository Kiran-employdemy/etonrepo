"use strict";
let multifield = document.getElementsByClassName('content-tile-cta-link-list');
$(document).on('dialog-ready', function (e) {
  let items = $(multifield).children('coral-multifield-item');
  if (multifield.length > 0) {
    validate(items.length);
    $(multifield).on('change', function (e) {
      let items = $(multifield).children('coral-multifield-item');
      validate(items.length);
    });
  }
});
function validate(len) {
  let dropdown = document.querySelector('select[name="./singleCtaAlign"]');
  if (dropdown) {
    if (len === 1) {
      dropdown.disabled = false;
      dropdown.style.cursor = "pointer";
    } else {
      dropdown.disabled = true;
      dropdown.style.cursor = "not-allowed";
    }
  }
}