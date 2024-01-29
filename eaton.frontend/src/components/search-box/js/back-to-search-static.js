//-----------------------------------
// Component : Search
//-----------------------------------
$( '.results-list-submodule__name-link, .results-list-submodule__image-link, .results-list-submodule__url-link' ).click(function( event ) {
  sessionStorage.setItem('backToSearch_show', 'true');
  sessionStorage.setItem('backToSearch_url', document.URL);
});

$(document).ready(function() {
  if (sessionStorage.getItem('backToSearch_url') && sessionStorage.getItem('backToSearch_show')) {
    $('#back-to-search').attr('href',sessionStorage.getItem('backToSearch_url'));
    $('#back-to-search').removeClass('u-hide');
  }

  $('#back-to-search').click(function() {
    sessionStorage.removeItem('backToSearch_url');
    sessionStorage.removeItem('backToSearch_show');
  });
});
