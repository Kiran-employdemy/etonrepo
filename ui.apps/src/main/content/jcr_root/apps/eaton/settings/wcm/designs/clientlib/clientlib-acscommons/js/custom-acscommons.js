$(document).ready(function() {
  if (window.location.pathname.includes('/var/acs-commons/reports')) {
    // This is required to remove "All Results" checkbox from ACS Commons reports.
    var checkboxElement = $('coral-checkbox').get(0);
    if (checkboxElement) {
      checkboxElement.style.display = 'none';
    }
  }
});