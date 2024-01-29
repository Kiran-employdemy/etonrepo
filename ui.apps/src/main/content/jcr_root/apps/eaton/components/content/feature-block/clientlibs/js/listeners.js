(function($) {
    "use strict";
    $(document).on("dialog-ready", function() {
        $(document).on('change', '.featureCTAOpenNewWindow_toggle', doNewWindow);
        $(document).on('change', '.modal_toggle', doModal);

        if( $('.featureCTAOpenNewWindow_toggle').length > 0 ) {
            doNewWindow();
        }
        if( $('.modal_toggle').length > 0 ) {
            doModal();
        }

        function doNewWindow(){
            if($(".featureCTAOpenNewWindow_toggle").attr("checked")){
                $(".modal_toggle").prop("checked", false);
                $(".enableSourceTracking_toggle" ).prop( "disabled", false );
            }
        }

        function doModal(){
            if($(".modal_toggle").attr("checked")){
                $(".featureCTAOpenNewWindow_toggle").prop("checked", false);
                $(".enableSourceTracking_toggle" ).prop( "disabled", true );
                $(".enableSourceTracking_toggle" ).prop( "checked", false );
            }
        }

    });
})($);