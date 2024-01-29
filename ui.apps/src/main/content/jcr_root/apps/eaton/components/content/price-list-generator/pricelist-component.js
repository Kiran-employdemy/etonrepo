"use strict";

use( function () {
 var Constants = {
     	UPLOAD_TITLE: "uploadTitle",
        SUCCESS_MESSAGE: "successMsg",
        FAILURE_MESSAGE: "failureMsg",
    };

    var uploadTitle = properties.get(Constants.UPLOAD_TITLE, "")
    var successMsg = properties.get(Constants.SUCCESS_MESSAGE, "");
    var failureMsg = properties.get(Constants.FAILURE_MESSAGE, "");

    return {
    	uploadTitle: uploadTitle,
        successMsg: successMsg,
        failureMsg: failureMsg
    };
});


