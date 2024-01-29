(function($) {
    "use strict";
    $(document).on("dialog-ready", function() {
        $(document).on('change', '.topicLinkCTAOpenNewWindow_toggle', doNewWindow);
        $(document).on('change', '.topicLinkCTAModal_toggle', doModal);

        if( $('.topicLinkCTAOpenNewWindow_toggle').length > 0 ) {
            doNewWindow();
        }
        if( $('.topicLinkCTAModal_toggle').length > 0 ) {
            doModal();
        }

        function doNewWindow(){
            if($(".topicLinkCTAOpenNewWindow_toggle").attr("checked")){
                $(".topicLinkCTAModal_toggle").prop("checked", false);
                $(".enableSourceTracking_toggle" ).prop( "disabled", false );
            }
        }

        function doModal(){
            if($(".topicLinkCTAModal_toggle").attr("checked")){
                $(".topicLinkCTAOpenNewWindow_toggle").prop("checked", false);
                $(".enableSourceTracking_toggle" ).prop( "disabled", true );
                $(".enableSourceTracking_toggle" ).prop( "checked", false );
            }
        }

    });
})($);