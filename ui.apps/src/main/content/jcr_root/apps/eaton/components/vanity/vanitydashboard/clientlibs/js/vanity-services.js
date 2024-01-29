function createNewVanity(request, thisParam) {
    const waitingTime = 5000;
    const checkMandatoryFields = window.Vanity.checkMandatoryFields(request);
    const invalidVanityPaths = window.Vanity.getInvalidVanityPaths(request);
    if (checkMandatoryFields) {
        if(invalidVanityPaths === null){
            $.ajax({
                type: "POST",
                url: "/eaton/vanity/authoring",
                data: request,
                success: function(data) {
                    var newString = data.slice(0, -1);
                    if (newString.localeCompare("true") === 0) {
                        $('.alert-msg').remove();
                        const alertHtml = `<h3 class="coral-Form-fieldset-legend alert-msg">This vanity URL has already been created.</h3>`;
                        $('#customForm').after(alertHtml);
                        $('.alert-msg').delay(waitingTime).fadeOut('slow');
                    } else {
                        thisParam.close();
                        window.Vanity.showDialog('create', data);
                    }
                },
                error: function(data) {
                    if(data.status === 403){
                       window.Vanity.noAccessErrorMsgDialog();
                    }else{
                    $('#vanity-url-form .form-body').hide();
                        const errorMessageTemplate = `
                                   <div class="vanity-url-heading success-message">
                                       <h3 class="coral-Form-fieldset-legend">Something went wrong, Please try again</h3>
                                   </div>
                                                        `;
                        $('#vanity-url-form').append(errorMessageTemplate);
                    }
                }
            });
        } else {
             $('.alert-msg').remove();
            let alertHtml = `<h3 class="coral-Form-fieldset-legend alert-msg">Invalid vanity path(s)&nbsp;&nbsp;&nbsp;&nbsp;`;
             invalidVanityPaths.forEach(function(vanity){
                alertHtml = alertHtml+vanity+"&nbsp;&nbsp;&nbsp;&nbsp;";
             });
            alertHtml = alertHtml+`</h3>`;
             $('#customForm').after(alertHtml);
             $('.alert-msg').delay(waitingTime).fadeOut('slow');
        }
   } else {
        $('.alert-msg').remove();
        const alertHtml = `<h3 class="coral-Form-fieldset-legend alert-msg">Title, Vanity URL or Default publish page is blank.</h3>`;
        $('#customForm').after(alertHtml);
        $('.alert-msg').delay(waitingTime).fadeOut('slow');
    }
}

function updateVanityData(request, thisParam ) {
    const waitingTime = 5000;
    const checkMandatoryFields = window.Vanity.checkMandatoryFields(request);
    const invalidVanityPaths = window.Vanity.getInvalidVanityPaths(request);
    if (checkMandatoryFields) {
        if(invalidVanityPaths === null){
            $.ajax({
                type: "POST",
                url: "/eaton/vanity/authoring",
                data: request,
                success: function (data) {
                    thisParam.close();
                    window.Vanity.showDialog('update',data);
                },
                error: function (data) {
                    if(data.status === 403){
                           window.Vanity.noAccessErrorMsgDialog();
                    }else{
                        window.Vanity.showErrorMsgDialog();
                    }
                }
            });
        } else {
            $('.alert-msg').remove();
            let alertHtml = `<h3 class="coral-Form-fieldset-legend alert-msg">Invalid vanity path(s)&nbsp;&nbsp;&nbsp;&nbsp;`;
            invalidVanityPaths.forEach(function(vanity){
                alertHtml = alertHtml+vanity+"&nbsp;&nbsp;&nbsp;&nbsp;";
            });
            alertHtml = alertHtml+`</h3>`;
            $('#customForm').after(alertHtml);
            $('.alert-msg').delay(waitingTime).fadeOut('slow');
        }
    } else {
        $('.alert-msg').remove();
        const alertHtml = `<h3 class="coral-Form-fieldset-legend alert-msg">Title, Vanity URL or Default publish page is blank.</h3>`;
        $('#customForm').after(alertHtml);
        $('.alert-msg').delay(waitingTime).fadeOut('slow');
    }

}

function disableVanityUrls(domainName, vanityUrlInputsValue) {
    $.ajax({
        type: "POST",
        url: "/eaton/vanity/authoring",
        data: {
            domainName: domainName,
            operation: 'disabled',
            vanityUrlList: vanityUrlInputsValue
        },
        success: function (data) {
            window.Vanity.showDialog('disable',data);
        },
        error: function (data) {
            if(data.status === 403){
                window.Vanity.noAccessErrorMsgDialog();
            }else{
                window.Vanity.showErrorMsgDialog();
            }
        }
    });
}

function publishVanityData(domainName) {
    $.ajax({
        type: "POST",
        url: "/eaton/vanity/authoring",
        data: {
            domainName: domainName,
            operation: 'publish'
        },
        success: function () {
            window.Vanity.showConfirmationDialog();
        },
        error: function (data) {
            if(data.status === 403){
                   window.Vanity.noAccessErrorMsgDialog();
            }else{
                window.Vanity.showErrorMsgDialog();
            }
        }
    });
}

function deleteVanityData(domainName,vanityUrlInputsValue) {
    $.ajax({
        type: "POST",
        url: "/eaton/vanity/authoring",
        data: {
                domainName: domainName,
                operation: 'delete',
                vanityUrlList: vanityUrlInputsValue
            },
        success: function (data) {
            publishVanityData(domainName);
        },
        error: function (data) {
           if(data.status === 403){
                  window.Vanity.noAccessErrorMsgDialog();
           }else{
               window.Vanity.showErrorMsgDialog();
           }
        }
    });
}

function getVanityData(domainName) {
    $.ajax({
        type: "GET",
        url: "/eaton/vanity/authoring",
        data: {
            domainName: domainName
        },
        success: function () {
            //success
        },
        error: function () {
            window.Vanity.showErrorMsgDialog();
        }
    });
}
