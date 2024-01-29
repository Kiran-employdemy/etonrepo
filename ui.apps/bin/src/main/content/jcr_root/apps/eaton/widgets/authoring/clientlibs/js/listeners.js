var enablePreviewRefresh = function() { 
	$('iframe').bind("load",function(){
			$(document).on("click", ".js-editor-LayerSwitcherTrigger", function () {
				location.reload();
			});
	});
};

$(document).ready(function() {
	
		  
	//refresh page while shifting between Edit and Preview Mode
	enablePreviewRefresh();

});

/* For disabling the manual input in pathbrowser field*/
(function($, $document) {
	"use strict";

	$document.on("dialog-ready", function() {

        var microSiteHeaderOptElem = '.eaton-microsite-header-options';
        if ($(ContentFrame).contents().find('.header-primary-nav__logo--secondary').length) {
            $(microSiteHeaderOptElem).parent().removeClass('hidden');
        }
        else {
            $(microSiteHeaderOptElem).parent().addClass('hidden');
        }

		setTimeout(function() {
			$(".path-readonly").find(".js-coral-pathbrowser-input").each(
					function() {
						$(this).attr("readonly", "readonly");
					});
		}, 250);

		/* To make Datefield read only */
		setTimeout(function() {
			$(".date-readonly").find(".js-coral-DatePicker-input").each(
					function() {
						$(this).attr("readonly", "readonly");
					});
		}, 250);

	});

})(jQuery, jQuery(document));
