/* For disabling the manual input in pathbrowser field*/
(function($, $document) {
	"use strict";
    $(document).ready(function() {

        $document.on("dialog-ready", function() {
         var pimPagePath = $("input[name='./pimPagePath']").val();
        //console.log(pimPagePath.length);
        if (pimPagePath == "" ||pimPagePath == null ){
		//console.log("null or empty");
		$('nav .productFamilyAttribute').addClass('hide');
        }else {
          //  console.log("not null");
			$('nav .productFamilyAttribute').removeClass('hide');
        }
	});

	//$('#shell-propertiespage-saveactivator').click(function(){
		//alert("pimpath");
         var pimPagePath = $("input[name='./pimPagePath']").val();
       //alert(pimPagePath.length);
        //alert(pimPagePath);
        if (pimPagePath == "" ||pimPagePath == null ){
		//alert("null or empty");
		$('nav .productFamilyAttribute').addClass('hide');


        }else {
          //  alert("not null");
			$('nav .productFamilyAttribute').removeClass('hide');


        }
//});
        });

})(jQuery, jQuery(document));