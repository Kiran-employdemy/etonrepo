$( document ).ready(function() {
  let url = 'https://my.eaton.com/extranet/faces/oracle/webcenter/portalapp/pages/forgotPassword.jspx';
  $('.js-forgot-password').attr('href',url);
  $('.js-forgot-password').removeAttr( 'data-se' );
  $('.js-forgot-password').removeAttr( 'class' );
});
