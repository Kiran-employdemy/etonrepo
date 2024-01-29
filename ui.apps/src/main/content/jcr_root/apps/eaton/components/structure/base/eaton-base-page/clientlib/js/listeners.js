(function ($) {
	"use strict";

	var toggleProductFamilyAttribute = function() {
		var pimPagePath = $("input[name='./pimPagePath']").val();
		if (pimPagePath == "" || pimPagePath == null) {
			$('nav .productFamilyAttribute').addClass('hide');
		} else {
			$('nav .productFamilyAttribute').removeClass('hide');
		}
	}

	$(document).ready(function () {
		$(document).on("dialog-ready", function () {
			toggleProductFamilyAttribute();
		});

		toggleProductFamilyAttribute();
	});
})(jQuery);
