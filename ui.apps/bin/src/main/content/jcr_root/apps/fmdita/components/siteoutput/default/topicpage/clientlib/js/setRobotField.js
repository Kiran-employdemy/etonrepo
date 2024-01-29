(function ($) {
	"use strict";

	var setRobot = function() {
        var metaRobotTag = $("input[name='./meta-robot-tags']").val();
        var robotId = document.getElementsByName("./robot")[1].id;
    
        if (metaRobotTag == "noindex,nofollow" ) {
    
            $("#"+robotId).val(true);
            $("#"+robotId).prop( "checked", true );

        } else {
    
            $("#"+robotId).val(false);
            $("#"+robotId).prop( "checked", false );
    
        }
	}
    $(window).on('change', function() {
        if(window.location.pathname == '/mnt/overlay/wcm/core/content/sites/properties.html'){
        	setRobot();
        }
    });

})(jQuery);