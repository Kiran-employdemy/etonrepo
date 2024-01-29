(function (document, $, Coral) {
    var $doc = $(document);

	var showHideRolloutPropField = function(configField, className) {
	    if(configField.val() !== "/apps/msm/eaton/rolloutconfigs/pagepropertyrolloutconfig"){
            $(className).parent().hide();
        } else {
            $(className).parent().show();
        }
	};

	var showHideRolloutPropFieldOnSelect = function(configField, className) {
        if (configField === "/apps/msm/eaton/rolloutconfigs/pagepropertyrolloutconfig") {
            $(className).parent().show();
        } else {
            $(className).parent().hide();
        }
    };

    var errMsgOnmultipleSelect = function(coralSelectArr, rolloutConfigField) {
		$.each(coralSelectArr , function(index, val) {
            if(val.value === "/apps/msm/eaton/rolloutconfigs/pagepropertyrolloutconfig" && coralSelectArr.length>1){
                rolloutConfigField.parent().find('p.error-msg').remove();
                rolloutConfigField.parent().append( "<p style='color:red' class='error-msg'>'Property-update-on-rollout' should not be clubbed with other rollout configs.</p>" );
                return;
            } else if(coralSelectArr.length<2) {
                rolloutConfigField.parent().find('p.error-msg').remove();
            }
        });
    };

    $doc.on('foundation-contentloaded', function(e) {
        var bluePrintConfigField = $("coral-select[name='msm:blueprintRolloutConfigs']");
        var liveCopyConfigField = $("coral-select[name='msm:liveCopyRolloutConfigs']");

        /* To wait for rollout config field value */
        setTimeout(function() {
           showHideRolloutPropField(bluePrintConfigField,'.rolloutpropertiesfieldcss');
           showHideRolloutPropField(liveCopyConfigField,'.rolloutpropertiesLivecopyfieldcss');
        }, 1000);

        bluePrintConfigField.on("change",function (event) {
            var coralSelectArr = bluePrintConfigField.find("coral-tag");
            var selectedBlueprintConfig = bluePrintConfigField.val();
            showHideRolloutPropFieldOnSelect(selectedBlueprintConfig, '.rolloutpropertiesfieldcss');
			errMsgOnmultipleSelect(coralSelectArr, bluePrintConfigField);
        });

        liveCopyConfigField.on("change",function (event) {
            var coralSelectArr = liveCopyConfigField.find("coral-tag");
            var selectedLiveCopyConfig = liveCopyConfigField.val();
            showHideRolloutPropFieldOnSelect(selectedLiveCopyConfig, '.rolloutpropertiesLivecopyfieldcss');
            errMsgOnmultipleSelect(coralSelectArr, liveCopyConfigField);
        });
    });

})(document, Granite.$, Coral);
