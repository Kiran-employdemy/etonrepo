function createRequestObject(operation, domainName) {

	let defPublishPage = document.querySelectorAll('.default-published-url input.coral3-Textfield')[0].value
	let title = $('[name = "./vanityTitle"]').val();
	let notes = $('[name = "./vanityNote"]').val();
	let localDataStore = JSON.parse(localStorage.getItem("selectedRow"));
	let groupId = localDataStore != null ? localDataStore["groupId"]: "";
	let vanityUrlInputs = document.getElementsByClassName("vanity-url-input");
	let additionalPublishedUrls = document.querySelectorAll('.additional-url-input input.coral3-Textfield');
	let vanityUrlList = removeDuplicateFromArray(vanityUrlInputs),
		addPublishPage = removeDuplicateFromArray(additionalPublishedUrls);
	return {
		title,
		domainName,
		operation,
		groupId,
		vanityUrlList,
		defPublishPage,
		addPublishPage,
		notes
	};
}

function removeDuplicateFromArray(urlInputArray) {
	let urlInputValueArray = [];
	if (typeof (urlInputArray) !== "undefined" && urlInputArray !== null) {
		for (let i = 0; i < urlInputArray.length; i++) {
            if(urlInputArray[i].value.trim()) {
                var vanityUrl = urlInputArray[i].value.toLowerCase().replace(/\s/g, "");
                if(vanityUrl.charAt(0) !== "/"){
                    urlInputValueArray.push("/"+vanityUrl);
                } else {
                    urlInputValueArray.push(vanityUrl);
                }
            }
		}
	}

	urlInputValueArray = urlInputValueArray.filter(function (item, index, inputArray) {
		return inputArray.indexOf(item) === index;
	});

	return urlInputValueArray;
}

function getDomainName() {
	if (sessionStorage.getItem("selectedDomain")) {
		return sessionStorage.getItem("selectedDomain");
	} else {
		return "eaton.com";
	}
}

/* Formatting function for row details - modify as you need */
function format(d, data) {
	const vanityUrl = [];
	$.each(data, function (index, obj) {
		if ((!obj['primaryVanity']) && (d['groupId'] === obj['groupId'])) {
			vanityUrl.push(obj['vanity_url']);
		}
	});

	let vanityUtlDetails = '';
	let notesDetails = '';
	let additionalPageDetails = '';
	if (vanityUrl.length) {
		vanityUtlDetails = '<tr><td>Related Vanity URL(s):</td><td>' + vanityUrl.join(' , ') + '</td></tr>';
	}
	if(d.notes!=''){
	    notesDetails = '<tr><td>Notes:</td><td>' + d.notes + '</td></tr>';
	}
	if(d.hasOwnProperty("additionalPage") && d.additionalPage.length ){
        additionalPageDetails = '<tr><td>Additional Publish Page(s):</td><td>' + d.additionalPage.join(' , ') + '</td></tr>';
    }
    return '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">' +
        vanityUtlDetails +
        '<tr>' +
        '<td>Default Publish Page:</td>' +
        '<td>' + d.defaultPage + '</td>' +
        '</tr>' +
        additionalPageDetails +
        notesDetails +
        '</table>';
}

var globalJson;
let selectedDomainName = "";
let vanityUrlSelected = [];

function formatDisableVanityUrls(Obj) {
	vanityUrlSelected.push(Obj['vanity_url']);
	var tableData = globalJson.data;
	$.each(tableData, function (index, obj) {
		if ((!obj['primaryVanity']) && (Obj['groupId'] === obj['groupId'])) {
			vanityUrlSelected.push(obj['vanity_url']);
		}
	});
}

function getOtherVanity(d, data) {
	const otherVanityUrls = [];
	var tableData = data;
	$.each(tableData, function (index, obj) {
		if ((!obj['primaryVanity']) && (d['groupId'] === obj['groupId'])) {
			otherVanityUrls.push(obj['vanity_url']);
		}
	});
	return otherVanityUrls;
}

// use a global for the submit and return data rendering in the examples
var editor;

$(document).ready(function () {
	const vanity = window.Vanity || {};
	$('.foundation-layout-table-panel').hide();
	const select = $('#select_Domain_URL').get(0);
	selectedDomainName = select.firstElementChild.innerText;
	select.addEventListener('coral-selectlist:change', function (event) {
		selectedDomainName = event.target.selectedItem.value;
		sessionStorage.setItem("selectedDomain", selectedDomainName);
		table.ajax.url("/eaton/vanity/authoring?domainName=" + getDomainName()).load();
		window.location.reload();
	});
	var selectList = document.getElementById('select_Domain_URL');

	var DomainURLListItem = selectList.getElementsByTagName('coral-select-item');
	for (var k = 0; k < DomainURLListItem.length; k++) {
		if (getDomainName() == DomainURLListItem[k].innerText) {
			DomainURLListItem[k].setAttribute('selected', 'selected')
		}
	}

	$('.additional-published-pages coral-multifield button').on("click", function(){
          var selectedDomain = $('#vanity-url-creation coral-select').find(":selected").val();
          setRootPath(selectedDomain,false);
    });

	vanity.showDialog = function (type, count) {
		var domainName = getDomainName();
		var createNewMsg = `Vanity Url(s) created successfully. There are ${count} unpublished changes. Do you want to publish all changes for ${domainName}?`;
		var upDateMsg = `Vanity Url(s) updated successfully. There are ${count} unpublished changes. Do you want to publish all changes for ${domainName}?`;
		var publishMsg = `Do you want to publish all changes for ${domainName}?`;
		var disableMsg = `Vanity Url(s) disabled successfully. There are ${count} unpublished changes. Do you want to publish all changes for ${domainName}?`;
		var deleteInfoMsg = `Vanity Url(s) deleted successfully. There are ${count} unpublished changes. Do you want to publish all changes for ${domainName}?`;
		var infoMsg = "";
		switch (type) {
			case "create":
				infoMsg = createNewMsg;
				break;
			case "disable":
				infoMsg = disableMsg;
				break;
			case "update":
				infoMsg = upDateMsg;
				break;
			case "publish":
				infoMsg = publishMsg;
				break;
			case "delete":
				infoMsg = deleteInfoMsg;
				break;
			default:
				break;
		}
		var Dialog = new Coral.Dialog().set({

			id: "demoDialog",
			header: {
				innerHTML: ''
			},
			content: {
				innerHTML: infoMsg
			},
			footer: {
				innerHTML: '<button id="cancelButton" is="coral-button" class="cancel" variant="default" onClick="window.location.reload()" coral-close>No</button>'
					+ '<button id="acceptButton" is="coral-button" variant="primary">Yes</button>'
			}
		});
		document.body.appendChild(Dialog);
		Dialog.show();
		table.ajax.reload();
		Dialog.on('click', '#acceptButton', function () {
			Dialog.hide();
			publishVanityData(getDomainName());
			window.location.reload();
		});
	};

	vanity.askConfirmationDialog = function () {
		var Dialog = new Coral.Dialog().set({
			id: "confirmationDialog",
			header: {
				innerHTML: ''
			},
			content: {
				innerHTML: confirmationMsg
			},
			footer: {
				innerHTML: '<button id="cancelButton" is="coral-button" class="cancel" variant="default" coral-close>No</button>'
					+ '<button id="acceptButton" is="coral-button" variant="primary">Yes</button>'
			}
		});
		document.body.appendChild(Dialog);
		Dialog.show();

		Dialog.on('click', '#acceptButton', function () {
			Dialog.hide();
			disableVanityUrls(getDomainName(), vanityUrlSelected);
		});
	};

	vanity.showConfirmationDialog = function () {
		var timeout = 3000;
		var infoMsg = "Changes has been published successfully.";
		var Dialog = new Coral.Dialog().set({
			id: "demoDialog",
			header: {
				innerHTML: '<div class="ui-dialog-titlebar dialog-header ui-corner-all ui-widget-header ui-helper-clearfix ui-draggable-handle">'
					+ '<button type="button" class="ui-button ui-corner-all ui-widget ui-button-icon-only ui-dialog-titlebar-close confirmation-close" title="Close" onClick="window.location.reload()">'
					+ '<span class="ui-button-icon ui-icon ui-icon-closethick" is="coral-button" coral-close></span><span class="ui-button-icon-space"> </span>Close</button></div>'
			},
			content: {
				innerHTML: infoMsg
			},
			footer: {
				innerHTML: ''
			}
		});
		Dialog.show();
		setTimeout(function () {
			window.location.reload();
			Dialog.hide();
		}, timeout);
	};

	vanity.showErrorMsgDialog = function () {
		var infoMsg = "Something went wrong. Please try again later.";
		var Dialog = new Coral.Dialog().set({
			id: "demoDialog",
			header: {
				innerHTML: '<div class="ui-dialog-titlebar dialog-header ui-corner-all ui-widget-header ui-helper-clearfix ui-draggable-handle">'
					+ '<button type="button" class="ui-button ui-corner-all ui-widget ui-button-icon-only ui-dialog-titlebar-close" title="Close">'
					+ '<span class="ui-button-icon ui-icon ui-icon-closethick" is="coral-button" coral-close></span><span class="ui-button-icon-space"> </span>Close</button></div>'
			},
			content: {
				innerHTML: infoMsg
			},
			footer: {
				innerHTML: ''
			}
		});
		Dialog.show();
	};

	vanity.noAccessErrorMsgDialog = function () {
		var infoMsg = "Write Access has been Denied";
		var Dialog = new Coral.Dialog().set({
			id: "demoDialog",
			header: {
				innerHTML: '<div class="ui-dialog-titlebar dialog-header ui-corner-all ui-widget-header ui-helper-clearfix ui-draggable-handle">'
					+ '<button type="button" class="ui-button ui-corner-all ui-widget ui-button-icon-only ui-dialog-titlebar-close" title="Close">'
					+ '<span class="ui-button-icon ui-icon ui-icon-closethick" is="coral-button" coral-close></span><span class="ui-button-icon-space"> </span>Close</button></div>'
			},
			content: {
				innerHTML: infoMsg
			},
			footer: {
				innerHTML: ''
			}
		});
		Dialog.show();
	};

	vanity.checkMandatoryFields = function (request) {
		var currentTitle = request['title'];
		var vanityUrl = request['vanityUrlList'];
		var defaultPublish = request['defPublishPage'];

		if (defaultPublish.trim() && currentTitle.trim() && Array.isArray(vanityUrl) && vanityUrl.length) {
			return true;
		} else {
			return false;
		}
	}

	vanity.getInvalidVanityPaths = function (request) {
      var vanityUrl = request['vanityUrlList'];
      var domain = request['domainName'];
      var invalidVanityPaths = [];
      var globalValidationRegx = /^\/(apps|etc|etc\.clientlibs|libs|bin|api|content\/eaton|content\/eaton-cummins|content\/phoenixtec|content\/greenswitching|content\/dam)($|\/.*)/
      var eatonValidationRegx = /\/(ecm\/groups|ecm\/|EatonInternalJobs|mdmfiles|INTERNAL4USE2ONLY)|^\/(Eaton\/ProductsServices\/ProductsbyCategory|Eaton\/ProductsServices\/Truck|Eaton\/ProductsServices\/Automotive|takeoff-redirect|Aerospace($|\/$)|EatonComPoland|images|Eaton\/OurCompany)|(.*\.htm(?!l)(\?.*)?)|(.*\/idcplg)/i

      vanityUrl.forEach(function(vanity){
           if (globalValidationRegx.test(vanity)){
             invalidVanityPaths.push(vanity);
           }
           if (domain === "eaton.com") {
               if(eatonValidationRegx.test(vanity)){
                   invalidVanityPaths.push(vanity);
                }
            }
         });
         if(invalidVanityPaths.length > 0){
           return invalidVanityPaths;
         } else {
           return null;
         }
    };

	window.Vanity = vanity;
	const confirmationMsg = "Vanity Url(s) will be disabled. Do you want to continue?";
	const deleteMsg = "This vanity record will be deleted from the system. Do you want to continue?";
	const pickersrc = "/mnt/overlay/granite/ui/content/coral/foundation/form/pathfield/picker.html?_charset_=utf-8&path={value}&root=%2frootpath&filter=hierarchyNotFile&selectionCount=single";
    const overlayPickersrc = "/mnt/overlay/granite/ui/content/coral/foundation/form/pathfield/suggestion{.offset,limit}.html?_charset_=utf-8&root=%2frootpath&filter=hierarchyNotFile{&query}";
	var onEditOpen = false;
	var onVanityOpen = false;
	$('#dataTable tfoot th').each(function () {

		var title = $(this).text();
		var customSelect = $('<select><option value=""></option></select>');
		if (title === "Status") {
			$(this).addClass('custom-select');
			$(this).html(customSelect);
		}

	});

	editor = new $.fn.dataTable.Editor({
		table: "#dataTable",
		template: '#customForm',
		idSrc: 'groupId',
		fields: [{
			label: "Title:",
			name: "title"
		}, {
			label: "Vanity URL:",
			name: "vanity_url"
		}, {
			label: "Domain:",
			name: "domain"
		}, {
			label: "Created Date:",
			name: "created_date",
			type: "datetime"
		}, {
			label: "Last Modified Date:",
			name: "last_modified_date",
			type: "datetime"
		}, {
			label: "Publish Date:",
			name: "publish_date",
			type: "datetime"
		}, {
			label: "Status:",
			name: "Status"
		}]
	});

	editor.on('initCreate', function () {
		editor.show();
		editor.hide('vanity_url');
		$(editor.__dialouge[0]).find('#select_Domain_URL2').prop('disabled', false)
		$('[data-granite-coral-multifield-name="./additionalPageList"]').addClass('disable-multifields').find('button, input').prop('disabled', false);
		$('[name="./vanityTitle"]').val('');
		$('[name="./vanityDomainName"]').val('');
		$('[name="./vanityNote"]').val('');
		$('.vanitydefault-published-url').val('');
		$('#processName').text("Vanity URL Creation");
		if (localStorage.getItem("allVanity") != null) {
			var vanityUrl = JSON.parse(localStorage.getItem("allVanity"));
			for (var i = 0; i < vanityUrl.length; i++) {
				$('#coral-collection-id-60 .coral3-Icon--delete').click();
			}
		}
		if (localStorage.getItem("selectedRow") != null) {
			var localDataStore = JSON.parse(localStorage.getItem("selectedRow"));
			var additionalPage = localDataStore['additionalPage'];
			if (typeof (additionalPage) !== "undefined" && additionalPage !== null) {
				for (var j = 0; j < additionalPage.length; j++) {
					$('#coral-collection-id-62 .coral3-Icon--delete').click();
				}
			}
		}

		let additionalPublishedUrls = document.querySelectorAll('.additional-url-input input.coral3-Textfield');
        if (typeof (additionalPublishedUrls) !== "undefined" && additionalPublishedUrls !== null) {
            for (let p = 0; p < additionalPublishedUrls.length; p++) {
                $('#coral-collection-id-62 .coral3-Icon--delete').click();
            }
        }
		onEditOpen = true;
		onVanityOpen = true;
		setTimeout(function () {
			var selectList = $(editor.__dialouge[0]).find('#select_Domain_URL2');

			var DomainURLListItem = selectList[0].getElementsByTagName('coral-select-item');
			for (var k = 0; k < DomainURLListItem.length; k++) {
				if (getDomainName() == DomainURLListItem[k].innerText) {
					DomainURLListItem[k].setAttribute('selected', 'selected')
				}
			}
			if( getDomainName()!=="eaton.com"){
                $('[data-granite-coral-multifield-name="./additionalPageList"]').addClass('disable-multifields').find('button, input').prop('disabled', true);
            }
            $(function() {
            	$("#select_Domain_URL2").change(function() {
            	var domainName = $("#select_Domain_URL2").val();
            		if (domainName !== "eaton.com") {
            		let additionalPublishedUrls = document.querySelectorAll('.additional-url-input input.coral3-Textfield');
                        if (typeof (additionalPublishedUrls) !== "undefined" && additionalPublishedUrls !== null) {
                            for (let p = 0; p < additionalPublishedUrls.length; p++) {
                                $('#coral-collection-id-62 .coral3-Icon--delete').click();
                            }
                        }
            			$('[data-granite-coral-multifield-name="./additionalPageList"]').addClass('disable-multifields').find('button, input').prop('disabled', true);
            		} else {
            			$('[data-granite-coral-multifield-name="./additionalPageList"]').addClass('disable-multifields').find('button, input').prop('disabled', false);
            		}
                      setRootPath(domainName,true);
            	});
            });
            setRootPath(getDomainName(), true);
		})
	});

	editor.on('initEdit', function () {
		const timeInMls = 25;
		$('#coral-collection-id-62 .coral3-Button--secondary').off('click');
		$('[data-granite-coral-multifield-name="./additionalPageList"]').addClass('disable-multifields').find('button, input').prop('disabled', false);
		setTimeout(function () {
			var localDataStore = JSON.parse(localStorage.getItem("selectedRow"));
			$('[name="./vanityTitle"]').val(localDataStore['title']);
			$('[name="./vanityDomainName"]').val(localDataStore['domain']);
			$('[name="./vanityNote"]').val(localDataStore['notes']);
			$('[name="./defaultPublishPage"]').val(localDataStore['defaultPage']);
			$('#processName').text(" Vanity Update");
			var additionalPage = localDataStore['additionalPage'];
			if (typeof additionalPage === 'string') {
				additionalPage = [additionalPage];
			}
			if (typeof (additionalPage) !== "undefined" && additionalPage !== null) {
				onEditOpen = populateAdditionalPage(additionalPage, onEditOpen, timeInMls);
			}else{
			if( getDomainName()!=="eaton.com"){
                $('[data-granite-coral-multifield-name="./additionalPageList"]').addClass('disable-multifields').find('button, input').prop('disabled', true);
                }
			}
			var vanityUrl = JSON.parse(localStorage.getItem("allVanity"));
			onVanityOpen = populateVanityUrl(vanityUrl, onVanityOpen, timeInMls);
			$(editor.__dialouge[0]).find('#select_Domain_URL2').prop('disabled', true)
			var selectList = $(editor.__dialouge[0]).find('#select_Domain_URL2');

			var DomainURLListItem = selectList[0].getElementsByTagName('coral-select-item');
			for (var k = 0; k < DomainURLListItem.length; k++) {
				if (getDomainName() == DomainURLListItem[k].innerText) {
					DomainURLListItem[k].setAttribute('selected', 'selected')
				}
			}

			setRootPath(getDomainName(), true);
		});
	});

	function populateAdditionalPage(additionalPage, onEditOpenOp, timeInMls) {
		for (var i = 0; i < additionalPage.length; i++) {
			if (!onEditOpenOp) {
				$('#coral-collection-id-62 .coral3-Button--secondary').click();
			} else {
				$('#coral-collection-id-62 .coral3-Button--secondary').off('click');
			}
		}
		setTimeout(function () {
			for (var j = 0; j < additionalPage.length; j++) {
				$('[name="./additionalPageList/item' + j + '/./additionalPage"]').val(additionalPage[j]);
			}
		if( getDomainName()!=="eaton.com"){
		    $('[data-granite-coral-multifield-name="./additionalPageList"]').addClass('disable-multifields').find('button, input').prop('disabled', true);
         }
		}, timeInMls);
		return true;
	}

	function populateVanityUrl(vanityUrl, onVanityOpenOp, timeInMls) {
		for (var k = 0; k < vanityUrl.length; k++) {
			if (!onVanityOpenOp) {
				$('#coral-collection-id-60 .coral3-Button--secondary').click();
			} else {
				$('#coral-collection-id-60 .coral3-Button--secondary').off('click');
			}
		}
		setTimeout(function () {
			for (var l = 0; l < vanityUrl.length; l++) {
				$('[name="./vanityUrlList/item' + l + '/./vanityUrl"]').val(vanityUrl[l]);
			}
		}, timeInMls);
		return true;
	}

	var table = $('#dataTable').DataTable({
		dom: "Bfrtip",
		ajax: "/eaton/vanity/authoring?domainName=" + getDomainName(),
		language: {
			"infoEmpty": "No records available.Please add entries"
		},

		"createdRow": function (row, data, rowIndex) {
			// Per-cell function to do whatever needed with cells
			$(row).attr('is', 'coral-table-row');
			$.each($('td', row), function (colIndex) {
				// For example, adding data-* attributes to the cell
				$(this).attr('is', "coral-table-cell");
			});

			if (!data.primaryVanity) {
				$(row).addClass('primaryVanity');
				var vanityUrltable = $('#dataTable').DataTable();

				vanityUrltable.rows('.primaryVanity').remove().draw();
			}
		},
		"initComplete": function (settings, json) {
			globalJson = json;
			$('.foundation-layout-table-panel').show();
			$("#dataTable tfoot th").each(function (i) {
				var columnNum = 6;
				var selectStatus = $('<select class=' + i + '><option value="" selected>Select status</option></select>')
					.appendTo($(this).empty())
					.on('change', function () {

						var val = $(this).val();

						table.column(columnNum)
							.search(val ? '^' + $(this).val() + '$' : val, true, false)
							.draw();
					});

				table.column(columnNum).data().unique().sort().each(function (d, j) {
					selectStatus.append('<option value="' + d + '">' + d + '</option>');
				});
			});


			// Add event listener for opening and closing details
			$('#dataTable tbody').on('click', 'td.details-control', function () {
				var tr = $(this).closest('tr');
				var row = table.row(tr);
				$(tr).find('.coral3-Icon').attr('icon', 'Minus');

				if (row.child.isShown()) {
					// This row is already open - close it
					row.child.hide();
					tr.removeClass('shown');
					$(tr).find('.coral3-Icon').attr('icon', 'add');

				} else {
					// Open this row
					row.child(format(row.data(), json.data)).show();
					tr.addClass('shown');
				}
			});



		},
		columns: [
			{
				data: "title", className: 'details-control',
				render: function (data, type) {
					return '<coral-icon icon="add" size="M"></coral-icon> ' + data;
				}
			},
			{ data: "vanity_url", defaultContent: "" },
			{ data: "domain", defaultContent: "" },
			{ data: "createdDate", defaultContent: "" },
			{ data: "lastModifiedDate", defaultContent: "" },
			{ data: "publishDate", defaultContent: "" },
			{ data: "status", defaultContent: "" }
		],
		select: true,
		buttons: [
			{
				extend: "create", editor: editor,
				formButtons: [
					{
						label: 'Cancel',
						className: 'coral3-Button--warning cancel',
						fn: function () {
							if (localStorage.getItem("allVanity") != null) {
								var vanityUrl = JSON.parse(localStorage.getItem("allVanity"));
								for (var i = 0; i < vanityUrl.length; i++) {
									$('#coral-collection-id-60 .coral3-Icon--delete').click();
								}
							}
							if (localStorage.getItem("selectedRow") != null) {
								var localDataStore = JSON.parse(localStorage.getItem("selectedRow"));
								var additionalPage = localDataStore['additionalPage'];
								if (typeof (additionalPage) !== "undefined" && additionalPage !== null) {
									for (var j = 0; j < additionalPage.length; j++) {
										$('#coral-collection-id-62 .coral3-Icon--delete').click();
									}
								}
							}
							onEditOpen = false;
							onVanityOpen = false;
							this.close();
						}
					},
					{
						label: 'Create',
						className: 'dt-button form-update',
						fn: function () {
						    var domainName = $('#select_Domain_URL2').find('.coral3-Select-label');
							var requestJson = createRequestObject('create', domainName.text());
							createNewVanity(requestJson, this);
						}
					}
				]
			},
			{
				extend: "edit",
				editor: editor,
				className: 'edit-button',
				formButtons: [
					{
						label: 'Cancel',
						className: 'coral3-Button--warning cancel',
						fn: function () {
							$(editor.__dialouge[0]).find('#select_Domain_URL2').prop('disabled', false)
							if (localStorage.getItem("allVanity") != null) {
								var vanityUrl = JSON.parse(localStorage.getItem("allVanity"));
								for (var i = 0; i < vanityUrl.length; i++) {
									$('#coral-collection-id-60 .coral3-Icon--delete').click();
								}
							}
							if (localStorage.getItem("selectedRow") != null) {
								var localDataStore = JSON.parse(localStorage.getItem("selectedRow"));
								var additionalPage = localDataStore['additionalPage'];
								if (typeof (additionalPage) !== "undefined" && additionalPage !== null) {
									for (var j = 0; j < additionalPage.length; j++) {
										$('#coral-collection-id-62 .coral3-Icon--delete').click();
									}
								}
							}
							onEditOpen = false;
							onVanityOpen = false;
							$(editor.__dialouge[0]).find('#select_Domain_URL2').prop('disabled', false)
							this.close();
						}
					},
					{
						label: 'Update',
						className: 'dt-button form-update',
						fn: function () {
						    var domainName = $('#select_Domain_URL2').find('.coral3-Select-label');
							var requestJson = createRequestObject('update', domainName.text());
							updateVanityData(requestJson, this);
							//this.close();
						}
					}
				]
			},
			{
				name: 'disable',
				text: 'Disable',
				className: 'btn btn-success btn-sm ui-state-disabled disable-button',
				attr: {
					id: 'confirmationDialog'
				},
				action: function (e, dt, node, config) {
					vanity.askConfirmationDialog();
				}
			},
			{
				name: 'publish',
				text: 'Publish',
				className: 'searchAll btn btn-success btn-sm',
				attr: {
					id: 'demoDialog'
				},
				action: function (e, dt, node, config) {
					vanity.showDialog('publish');
				}
			},
			{
				extend: 'remove',
				editor: editor,
				className: 'delete-button',
				action: function (e, dt, node, config) {
					askDeleteConfirmationDialog();

				}
			}


		]

	});


	// update attributes
	table.button('disable:name')
		.nodes()
		.attr('is', 'coral-button');

	table.on('select', function (e, dt, type, indexes) {
		if (type === 'row') {
			var data = table.row(indexes).data();
			formatDisableVanityUrls(data);
			var otherVanity = getOtherVanity(data, globalJson.data);
			var allVanity = [data['vanity_url']];
			if (typeof (otherVanity) !== "undefined" && otherVanity !== null) {
				for (var index = 0; index < otherVanity.length; index++) {
					allVanity.push(otherVanity[index]);
				}
			}
			localStorage.setItem("selectedRow", JSON.stringify(data));
			localStorage.setItem("allVanity", JSON.stringify(allVanity));
			$('.disable-button').removeClass('ui-state-disabled');
			if (table.rows('.selected').data().length > 1) {
				$('.edit-button').addClass('ui-state-disabled');
				$('.disable-button').addClass('ui-state-disabled');
				$('.delete-button').addClass('ui-state-disabled');
			}
		}
	});

	table.on('deselect', function (e, dt, type, indexes) {
		if (type === 'row') {
			vanityUrlSelected = [];
			if (table.rows('.selected').data().length === 0) {
				$('.disable-button').addClass('ui-state-disabled');
			}
		}
	});

	function askDeleteConfirmationDialog() {
		var Dialog = new Coral.Dialog().set({
			id: "deleteConfirmationDialog",
			header: {
				innerHTML: ''
			},
			content: {
				innerHTML: deleteMsg
			},
			footer: {
				innerHTML: '<button id="cancelButton" is="coral-button" class="cancel" variant="default" coral-close>No</button>'
					+ '<button id="deleteAcceptButton" is="coral-button" variant="primary">Yes</button>'
			}
		});
		document.body.appendChild(Dialog);
		Dialog.show();

		Dialog.on('click', '#deleteAcceptButton', function () {
			Dialog.hide();
			deleteVanityData(getDomainName(), vanityUrlSelected);
		});
	}

  function setRootPath(domain, updateAllPathBrowser) {
       var updatedPickersrc;
       var updatedOverlayPickersrc;
       switch (domain) {
           case 'eaton.com':
              updatedPickersrc = pickersrc.replaceAll("rootpath" , "content%2featon");
             updatedOverlayPickersrc = overlayPickersrc.replaceAll("rootpath","content%2featon");
              break;
           case 'eaton-cummins.com':
              updatedPickersrc = pickersrc.replaceAll("rootpath" , "content%2featon-cummins");
               updatedOverlayPickersrc = overlayPickersrc.replaceAll("rootpath","content%2featon-cummins");
              break;
           case 'greenswitching.com':
              updatedPickersrc = pickersrc.replaceAll("rootpath" , "content%2fgreenswitching");
               updatedOverlayPickersrc = overlayPickersrc.replaceAll("rootpath","content%2fgreenswitching");
              break;
           case 'phoenixtecpower.com':
              updatedPickersrc = pickersrc.replaceAll("rootpath" , "content%2fphoenixtec");
               updatedOverlayPickersrc = overlayPickersrc.replaceAll("rootpath","content%2fphoenixtec");
              break;
           default:
              break;
       }
       if(updateAllPathBrowser){
           $('.vanitydefault-published-url').attr("pickersrc", updatedPickersrc);
           $('.vanitydefault-published-url coral-overlay').attr("data-foundation-picker-buttonlist-src", updatedOverlayPickersrc);
           if (domain === "eaton.com") {
               $('.additional-url-input').attr("pickersrc", updatedPickersrc);
               $('.additional-url-input coral-overlay').attr("data-foundation-picker-buttonlist-src", updatedOverlayPickersrc);
           }
       } else {
            setTimeout(function () {
                 $('.additional-url-input').attr("pickersrc", updatedPickersrc);
                 $('.additional-url-input coral-overlay').attr("data-foundation-picker-buttonlist-src", updatedOverlayPickersrc);
            });
           }
   }

});
