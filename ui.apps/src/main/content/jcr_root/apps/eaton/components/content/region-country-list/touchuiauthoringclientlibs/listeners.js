(function($, $document) {
    "use strict";

    $(document).on("dialog-ready", function() {
        if($('.country-dropdown').length >=1) {

              /* To update collapsible title on dialog load */
              setTimeout(function() {
                 $(".country-multifield .country-item").each(function() {
                    var countryName = $(this).find('.country-dropdown span').text();
                    $(this).find('.coral-Collapsible-title').text(countryName);
                 });
              }, 100);

              /* To update collapsible title on country change */
              $('.country-dropdown').on("change", function() {
                  var $this =this;
                  var collapsibaleItem = $this.closest('.coral-Collapsible');
                    setTimeout(function() {
                           var selectedCountry = $($this).find("span").text();
                           $(collapsibaleItem).find(".coral-Collapsible-title").text(selectedCountry);
                    }, 100);
              });
        }

    });

}($, $(document)));