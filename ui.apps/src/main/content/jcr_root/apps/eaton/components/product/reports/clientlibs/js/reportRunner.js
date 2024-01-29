var ScriptRunner = {
    SCAFFOLD_URL: "/mnt/overlay/commerce/gui/content/products/properties.html?item=",
    LOG_TEMPLATE: null,
    ROW_OFFSET: 4,
    init: function() {
        if (document.getElementById("processListing")) {
            ScriptRunner.processTable = document.getElementById("processListing");
            window.ScriptRunner = ScriptRunner;
            ScriptRunner.LOG_TEMPLATE = $('.logs').clone();
        }
    },
    processDefinitionSelected: function(event) {
        var baseFolder = document.getElementById("baseFolder");
        var formData = {};
        var result = null;
        var properties = [];
        $('.property input[type="checkbox"]:checked').each(function() {
            properties.push($(this).val());
        });
        if(baseFolder && baseFolder.value){
            formData.baseFolder = baseFolder.value;
            baseFolder.removeAttribute('invalid');
        }else{
            baseFolder.setAttribute('invalid',true);
            result = "Root Path is empty !!!";
        }
        if(result == null){
            ScriptRunner.submitForm(baseFolder.value, properties);
            document.getElementById("startProcessWizard").next();
        }
    },
    submitForm: function(formData, selectedProperties) {
        $('.logs').hide();
        $('.logs').parent().remove('.logs');
        $('#wait').show();
        var data = new FormData($('#catalog-report-form ', window.top.document)[0]);
        var url = Granite.HTTP.getPath() + "/jcr:content.report.xls";
        url = url+"?path="+formData+"&properties="+selectedProperties;
        jQuery.ajax({
            url: url,
            method: "GET",
            success: ScriptRunner.downloadExcel,
            error: ScriptRunner.error,
            cache: false,
            contentType: false,
            processData: false
        });

    },
    downloadExcel: function(response) {
        if (response) {
            $('.logs').show();
            var byteCharacters = atob(response);
            var byteNumbers = new Array(byteCharacters.length);
            for (var i = 0; i < byteCharacters.length; i++) {
                byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            var byteArray = new Uint8Array(byteNumbers);
            var todayTime = new Date().YYYYMMDDHHMMSS();
            var fileType = 'application/xls';
            var fileNameExt = 'Catalog-Report-'.concat(todayTime).concat('.xlsx');
            var blob = new Blob([byteArray], { type: fileType });
            var link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = fileNameExt;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        }
        $('#wait').hide();
    },
    error: function(e) {
        $('#wait').hide();
        console.log("Error Generating Report -- Please try after some time!");
        console.log(e);
        $('.logs').hide();
        var ui = $(window).adaptTo('foundation-ui');
        ui.alert('Notification', "Error while generating the report.", 'error');
    },
    reset: function() {
        var baseFolder = document.getElementById("baseFolder").value = "";
    }
};

Object.defineProperty(Date.prototype, 'YYYYMMDDHHMMSS', {
    value: function() {
        function pad2(n) {  // always returns a string
            return (n < 10 ? '0' : '') + n;
        }
        return this.getFullYear() +
            pad2(this.getMonth() + 1) +
            pad2(this.getDate()) +
            pad2(this.getHours()) +
            pad2(this.getMinutes()) +
            pad2(this.getSeconds());
    }
});

$( document ).ready(function() {
    if($("#processListing").length > 0){
        ScriptRunner.init();
    }
});
