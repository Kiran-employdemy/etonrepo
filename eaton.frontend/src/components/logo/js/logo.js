$(document).ready(() => {
  if (document.querySelectorAll('.header-primary-nav__logo--secondary .eaton-logo').length === 0 && $('.header-primary-nav__logo--secondary').val() === '') {
    $('.secondary-image').removeClass('eaton-logo__image');
    $('.secondary-image').addClass('secondary__logo');
  }
});


