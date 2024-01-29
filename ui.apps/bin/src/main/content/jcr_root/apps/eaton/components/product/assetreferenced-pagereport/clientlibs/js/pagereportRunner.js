var pageTitleValue = "";
var pageLinkToEdit = "";
var assetLinkToEdit = "";
let pathIndex = "";
let tdCount = 0;
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
           // document.getElementById("startProcessWizard").next();
        }
    },
    submitForm: function(formData, selectedProperties) {
        $('.logs').hide();
        $('.logs').parent().remove('.logs');
        $('#wait').show();
        var inputVal = baseFolder.value;
		const indexOfLastSlash = inputVal.lastIndexOf('/');
		pageLinkToEdit = "/editor.html"+inputVal+".html";


		if (indexOfLastSlash === -1)
        {
            pageTitleValue =  inputVal;
        }else{
            pageTitleValue = inputVal.substring(indexOfLastSlash + 1);
        }
        $("#downLoadBtn").show();
        var data = new FormData($('#asset-report-form ', window.top.document)[0]);
        var url=baseFolder.value+".assetreferences.json";
        jQuery.ajax({
            url: url,
            method: "GET",
            success: function(response){
                responseText = response;
                if(typeof(responseText[0]) !== null && typeof(responseText[0]) !== 'undefined') {
                    document.getElementById("downLoadBtn").show();
                    ScriptRunner.tablemaker(responseText,inputVal);
                }else{
                    document.getElementById("downLoadBtn").hide();
                    document.getElementById("report-result-table").innerHTML="<div class='text-center'>Data not available!</div>";
                    document.getElementById("wait").hide();
                }
            },
            error: ScriptRunner.error,
            cache: false,
            contentType: false,
            processData: false
        });

    },
    tablemaker: function(response,inputVal) {
	if (response) {
        $('.logs').show();
        // Get the container element where the table will be inserted
        let container = document.getElementById("mcp-workspace");
        let containerTable = document.getElementById("report-result-table");
        // Create the table element
        let table = document.createElement("table");
        table.setAttribute('class', 'results-table');
        // Get the keys (column names) of the first object in the JSON data
            let cols = Object.keys(response[0]);

            // Create the header element
            let thead = document.createElement("thead");
            let thCount = 0;

            let tr = document.createElement("tr");

            // Loop through the column names and create header cells
            cols.forEach((item) => {
                let th = document.createElement("th");
                th.innerText = item.toUpperCase(); // Set the column name as the text of the header cell
                th.setAttribute('id', 'id-table-' + item);
                tr.appendChild(th); // Append the header cell to the header row

                if (item === "path") {
                    pathIndex = thCount;
                }
                thCount++;
            });

            var pageNametHead = document.createElement('th');
            var editAsssetHead = document.createElement('th');
            var editPageHead = document.createElement('th');
            pageNametHead.innerHTML = 'PAGE TITLE';
            editAsssetHead.innerHTML = 'EDIT ASSET';
            editPageHead.innerHTML = 'EDIT PAGE';
            tr.appendChild(pageNametHead);
            tr.appendChild(editAsssetHead);
            tr.appendChild(editPageHead);
            thead.appendChild(tr); // Append the header row to the header
            table.append(tr) // Append the header to the table

            // Loop through the JSON data and create table rows
            response.forEach((item) => {
                let tr = document.createElement("tr");
                // Get the values of the current object in the JSON data
                let vals = Object.values(item);
                let pathId = 'id-table-path';
                let tempAssetPath = "";
                tdCount = 0;
                vals.forEach((elem) => {
                    let td = document.createElement("td");
                    td.innerText = elem; // Set the value as the text of the table cell
                    tr.appendChild(td); // Append the table cell to the table row
                    if (tdCount === pathIndex) {
                        tempAssetPath = elem;
                    }
                    tdCount++;
                });
                var pageTitle = document.createElement('td');
                var pageOrigin = window.location.origin;
                var editAssset = document.createElement('td');
                editAssset.setAttribute('class', 'text-center');
                var editPage = document.createElement('td');
                editPage.setAttribute('class', 'text-center');
                pageTitle.innerHTML = pageTitleValue == "" || pageTitleValue == undefined ? "-" : pageTitleValue;
                assetLinkToEdit = pageOrigin + "/mnt/overlay/dam/gui/content/assets/metadataeditor.external.html?_charset_=utf-8&item=" + tempAssetPath;
                editAssset.innerHTML = '<a href="' + assetLinkToEdit + '" target="_blank"  class="coral-Button coral-Button--square"><i class="coral-Icon coral-Icon--gear coral-Icon--sizeS"></i> Edit</a>';
                editPage.innerHTML = '<a href="' + pageOrigin + pageLinkToEdit + '" target="_blank"  class="coral-Button coral-Button--square"><i class="coral-Icon coral-Icon--gear coral-Icon--sizeS"></i> Edit</a>';
                tr.appendChild(pageTitle);
                tr.appendChild(editAssset);
                tr.appendChild(editPage);
                table.appendChild(tr); // Append the table row to the table
            });

            containerTable.innerHTML = "";
            containerTable.appendChild(table); // Append the table to the container element
        }
        $('#wait').hide();
        $('.logs').show();
        $('.coral-Well').show();
    },
downloadExcel: function () {
    var excelTemplateHead = '<html><head><meta http-equiv="content-type" content="text/plain; charset=UTF-8"/><style> table, td {border:thin solid black} table {border-collapse:collapse}</style></head><body><table>';
    var dataTable = document.getElementById("report-result-table").innerHTML;
    var excelTemplateFooter = '</table></body></html>';
    var mainResultTable = excelTemplateHead + dataTable + excelTemplateFooter;
    var myBlob =  new Blob( [mainResultTable] , {type:'text/html'});
    var url = window.URL.createObjectURL(myBlob);
    var a = document.createElement("a");
    document.body.appendChild(a);
    a.href = url;
    a.download = "download.xls";
    a.click();
    setTimeout(function() {window.URL.revokeObjectURL(url);},0);
},
    error: function(e) {
        $("#downLoadBtn").hide();
        $('#wait').hide();
        console.log("Error Generating Report -- Please try after some time!");
        console.log(e);
        $('.logs').hide();
        var ui = $(window).adaptTo('foundation-ui');
        ui.alert('Notification', "Error while generating the report.", 'error');
    },
    reset: function() {
       $("#downLoadBtn").hide();
       var baseFolder = document.getElementById("baseFolder").value = "";
       document.getElementById("report-result-table").innerHTML = "";
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
     $("#downLoadBtn").hide();
    if($("#processListing").length > 0){
        ScriptRunner.init();
    }
});
