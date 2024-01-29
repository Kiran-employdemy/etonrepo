(function($) {
    "use strict";
    $(document).on("dialog-ready", function() {
        $(document).on('change', '.newWindow_toggle', doNewWindow);
        $(document).on('change', '.modal_toggle', doModal);

        if( $('.newWindow_toggle').length > 0 ) {
            doNewWindow();
        }
        if( $('.modal_toggle').length > 0 ) {
            doModal();
        }

        function doNewWindow(){
            if($(".newWindow_toggle").attr("checked")){
            	$(".applyNoFollowTag_toggle" ).prop( "disabled", false );
            	$(".enableSourceTracking_toggle" ).prop( "disabled", false );
                $(".modal_toggle").prop("checked", false);
            }
        }

        function doModal(){
            if($(".modal_toggle").attr("checked")){
				$(".applyNoFollowTag_toggle" ).prop( "disabled", true );
                $(".applyNoFollowTag_toggle" ).prop( "checked", false );
                $(".enableSourceTracking_toggle" ).prop( "disabled", true );
                $(".enableSourceTracking_toggle" ).prop( "checked", false );
                $(".newWindow_toggle").prop("checked", false);
            }
        }

    });
})($);