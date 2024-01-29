var COMMAND_URL= Granite.HTTP.externalize("/bin/wcmcommand");
var REFERENCE_LIST_URL = '/eaton/content/referenceList';
var TAG_COMMAND_URL = "/bin/tagcommand";
function deletePages(collection, paths, force, checkChildren) {
        var ui = $(window).adaptTo("foundation-ui");
        ui.wait();

        $.ajax({
            url: COMMAND_URL,
            type: "POST",
            data: {
                _charset_: "UTF-8",
                cmd: "deletePage",
                path: paths,
                force: !!force,
                checkChildren: !!checkChildren
            },
            success: function() {
                ui.clearWait();

                var api = collection.adaptTo("foundation-collection");
                var layoutConfig = collection.data("foundationLayout");

                // In column layout the collection id may represent a preview column belonging to a deleted item.
                if (layoutConfig && layoutConfig.layoutId === "column") {
                    var previewShown = $("coral-columnview-preview").length > 0;

                    if (previewShown) {
                      if (api && "load" in api) {
                        var id = paths[0];

                        if (id) {
                          var parentId = id.substring(0, id.lastIndexOf('/')) || '';

                          if (parentId != '') {
                            api.load(parentId);
                          }
                        }
                      }
                    }

                    Coral.commons.nextFrame(function() {
                        if (api && "reload" in api) {
                            api.reload();
                        }
                    });

                    return;
                }

                if (api && "reload" in api) {
                    api.reload();
                    return;
                }

                var contentApi = $(".foundation-content").adaptTo("foundation-content");
                if (contentApi) {
                    contentApi.refresh();
                }
            },
            error: function(xhr) {
                ui.clearWait();

                var message = Granite.I18n.getVar($(xhr.responseText).find("#Message").html());

                if (xhr.status == 412) {
                    ui.prompt(getDeleteText(), message, "notice", [{
                        text: getCancelText()
                    }, {
                        text: Granite.I18n.get("Force Delete"),
                        warning: true,
                        handler: function() {
                            deletePages(collection, paths, true);
                        }
                    }]);
                    return;
                }

                ui.alert(Granite.I18n.get("Error"), message, "error");
            }
        });
    }


function checkReferenceForResource(deleteType, paths, selections, collection) {
    $.ajax({
        type: 'GET',
        url: REFERENCE_LIST_URL,
        data: {
            actionType: deleteType,
            path: paths
        },
        success: function(resultData) {
            promptDelete(resultData, selections, deleteType, collection);
        }
    });
}


 function promptDelete(resultData, selections, deleteType, collection) {
    var accordion = new Coral.Accordion();
    resultData.forEach(function(item, index){
        var referenceHTML = "<ol style=' overflow: scroll;height: 100px;'>";
        var itemLength = item.deleteReference.length;

        if (itemLength > 0) {
            item.deleteReference.forEach(function(referenceItem) {
                referenceHTML = referenceHTML + "<li>"+referenceItem+"</li>";
            });
        } else {
            referenceHTML = "<span>No reference found for this item.</span>";

        }
        var selectedFlag = false;
        if (index == 0) {
            selectedFlag = true;
        }
        accordion.items.add({
           label: {
               innerHTML: item.path
             },
             content: {
               innerHTML: referenceHTML
             },
             selected : selectedFlag
        });
    });

    var ui = $(window).adaptTo("foundation-ui");
    var messageLabel = "<p>You are going to delete the following item:<br><b>Note: </b>Please correct item references listed below before deleting the item.</p>";

    ui.prompt(getDeleteText(), messageLabel + accordion.outerHTML, "warning", [{
        text: getCancelText()
    }, {
        text: getDeleteText(),
        warning: true,
        handler: function() {
            if (selections) {
                var paths = selections.map(function(v) {
                                return $(v).data("foundationCollectionItemId");
                });
            }
            switch (deleteType) {
                case 'deletePage' :
                if (collection) {
                    deletePages($(collection), paths, true, true);
                }
                break;

                case 'deleteTag' :
                deleteTags();
                break;

                case 'deleteAsset' :
                deleteAssets(true);
                break;

                default :
                break;

            }


        }
    }]);
 }


 function deleteTags() {
    var self = this;
    var collection;
    var url, data;
    var paths = [];

    var items = $(".foundation-collection").find(".foundation-selections-item");
    if (items.length) {
        for (var i = 0; i < items.length; i++) {
            var dataPath = $(items[i]).data("path");
            if (dataPath.indexOf("jcr:content/renditions/original") < 0) {
                paths.push(dataPath);
            }
        }
    }

    $.ajax({
        url: TAG_COMMAND_URL,
        type: "post",
        data: {
            cmd: "deleteTag",
            path: paths,
            "_charset_": "utf-8"
        },
        success: function() {
            window.location = window.location.href;
        },
        error: function() {
            new Coral.Dialog().set({
                variant: 'error',
                header: {
                    innerHTML: Granite.I18n.get("Error")
                },
                content: {
                    innerHTML: '<p>' + Granite.I18n.get('Failed to delete tag(s).') + '</p>'
                },
                footer: {
                    innerHTML:'<button is="coral-button" class="coral-Button" variant="primary" coral-close size="M">' + Granite.I18n.get('Ok') + '</button>'
                }
            }).show();
        }
    });
 }

 function deleteAssets(force, type) {
     var url, data;
     var paths = [];
     var selectedItems = $(".foundation-selections-item");
     selectedItems.each(function() {
         paths.push($(this).get(0).getAttribute('data-foundation-collection-item-id'));
     });

     var collection;
     var layoutId = "";
     if (type === "collection") {
         collection = document.querySelector(".cq-damadmin-admin-childcollections");
         url = Granite.HTTP.externalize(paths[0] + ".collection.html");
         data = {
             ":operation": "deleteCollection",
             "path": paths,
             "force": force == undefined ? false : force,
             "_charset_": "utf-8"
         }
     } else {
         collection = document.querySelector(".cq-damadmin-admin-childpages");
         url = COMMAND_URL;
         data = {
             cmd: "deletePage",
             path: paths,
             force: force == undefined ? false : force,
             "_charset_": "utf-8"
         }
     }
     // hack for omnisearch. can't figure a better way right now
     var omnisearchResult = document.querySelector("#granite-omnisearch-result");
     if (omnisearchResult) {
         collection = omnisearchResult;
     }
     layoutId = JSON.parse(collection.dataset.foundationLayout).layoutId;
     var pageId = collection.getAttribute("data-foundation-collection-id");

     $.ajax({
         url: url,
         type: "post",
         data: data,
         success: function() {
             $(collection).adaptTo("foundation-collection").load(pageId);
             if (layoutId === "column") {
                 //page reload required in column view
                 window.location.reload();
             }
         },
         statusCode: {
             412: function() {
                 var isDirectory = true;
                 var content;
                 if (paths.length == 1) {
                     var dataType = $(collection.selectedItem).data("item-type");
                     if(dataType === "asset") {
                         isDirectory = false;
                     }
                 }
                 if (paths.length == 1 && !isDirectory) {
                     var request = new XMLHttpRequest();
                     request.open("GET", Granite.HTTP.externalize("/bin/wcm/references.json?path="+paths[0]+"&_charset_=utf-8&predicate=wcmcontent"),false);
                     request.send(null);
                     var jsonData = eval("(" + request.responseText + ")");
                     var times = jsonData["pages"].length;
                     if (times == 1){
                         content = Granite.I18n.get("This item is referenced once.");
                     }else{
                         content = Granite.I18n.get("This item is referenced {0} times.", times);
                     }
                 } else {
                     content = Granite.I18n.get("One or more item(s) are referenced.");
                 }

                 var forceDelDialog = new Coral.Dialog().set(({
                     id: 'forceDelDialog',
                     variant: 'error',
                     header: {
                         innerHTML: Granite.I18n.get("Force Delete")
                     },
                     content: {
                         innerHTML: content
                     },
                     footer: {
                         innerHTML:'<button is="coral-button" class="coral-Button" variant="primary" coral-close size="M">' + Granite.I18n.get('Cancel') + '</button>'
                     }
                 }));
                 var footer = forceDelDialog.querySelector('coral-dialog-footer');

                 var forceDeleteButton = new Coral.Button();
                 forceDeleteButton.label.textContent = Granite.I18n.get('Delete');
                 footer.appendChild(forceDeleteButton).on('click', function (){
                     deleteResources(true);
                     forceDelDialog.hide();
                 });

                 forceDelDialog.show();
             }
         },
         error: function(response) {
             if (response.status === 412) {
                 // Ignore. Already Handled
                 return;
             }
             new Coral.Dialog().set({
                 id: 'delErrorDialog',
                 variant: 'error',
                 header: {
                     innerHTML: Granite.I18n.get("Error")
                 },
                 content: {
                     innerHTML: '<p>' + Granite.I18n.get('Failed to delete.') + '</p>'
                 },
                 footer: {
                     innerHTML:'<button is="coral-button" class="coral-Button" variant="primary" coral-close size="M">' + Granite.I18n.get('Ok') + '</button>'
                 }
             }).show();

         }
     });

 }

	var ui = $(window).adaptTo("foundation-ui");
    function createEl(name) {
        return $(document.createElement(name));
    }

    var deleteText;
    function getDeleteText() {
        if (!deleteText) {
            deleteText = Granite.I18n.get("Delete");
        }
        return deleteText;
    }

    var cancelText;
    function getCancelText() {
        if (!cancelText) {
            cancelText = Granite.I18n.get("Cancel");
        }
        return cancelText;
    }

