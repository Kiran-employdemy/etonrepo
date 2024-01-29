$(document).ready(function () {
  if (window.location.href.indexOf('catalog') > 0) {
    if ($('.spinnerimg').data('src').includes('eaton.sirv')) {
      $('.staticPrimaryImg').addClass('hide');
    } else {
      $('.spinnerimg').addClass('hide');
    }
  }
});
