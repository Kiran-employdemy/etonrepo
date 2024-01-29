/*
 * Copyright 1997-2008 Day Management AG
 * Barfuesserplatz 6, 4001 Basel, Switzerland
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Day Management AG, ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Day.
 */

/**
 * @class CQ.BrowseDialog
 * @extends CQ.Dialog
 * The BrowseDialog lets the user browse the repository in order to
 * select a path. It is typically used through a {@link CQ.form.BrowseField BrowseField}.
 * @constructor
 * Creates a new BrowseDialog.
 * @param {Object} config The config object
 */
CQ.custombrowsedialog = CQ.Ext.extend(CQ.Dialog, {

    /**
     * @cfg {CQ.Ext.tree.TreeNode} treeLoader
     * The config options for the tree loader in the browse dialog.
     * See {@link CQ.Ext.tree.TreeLoader} for possible options.
     */

    /**
     * @cfg {CQ.Ext.tree.TreeNode} treeRoot
     * The config options for the tree root node in the browse dialog.
     * See {@link CQ.Ext.tree.TreeNode} for possible options.
     */

    /**
     * @cfg {Boolean} parBrowse
     * True to allow paragraph browsing and selection.
     */

    /**
     * The browse dialog's tree panel.
     * @private
     * @type CQ.Ext.tree.TreePanel
     */
    treePanel: null,

    /**
     * The browse dialog's browse field.
     * @private
     * @type CQ.form.BrowseField
     */
    browseField: null,

    initComponent: function(){
        CQ.custombrowsedialog.superclass.initComponent.call(this);
    },

    /**
     * Selects the specified path in the tree.
     * @param {String} path The path to select
     */
    loadContent: function(path) {
        if (typeof path == "string") {
            this.path = path;
            this.treePanel.selectPath(path,"name");
            if(this.parBrowse){
                // reload paragraph store
                this.paraProxy.api["read"].url = CQ.HTTP.externalize(path, true) + ".paragraphs.json";
                this.paraStore.reload();
            }
        }
    },

    /**
     * Returns the path of the selected tree node (or an empty string if no
     * tree node has been selected yet).
     * @return {String} The path
     */
    getSelectedPath: function() {
        try {
            return this.treePanel.getSelectionModel().getSelectedNode().getPath();
        } catch (e) {
            return "";
        }
    },

    /**
     * Returns the anchor of the selected paragraph (or an empty string if
     * no paragraph has been selected yet).
     * @return {String} The anchor
     */
    getSelectedAnchor: function() {
        try {
            var anchorID = this.data.getSelectedRecords()[0].get("path");
            anchorID = anchorID.substring(anchorID.indexOf("jcr:content")
                    + "jcr:content".length + 1);
            return anchorID.replace(/\//g, "_").replace(/:/g, "_");
        } catch (e) {
            return "";
        }
    },

    constructor: function(config){

        var treeRootConfig = CQ.Util.applyDefaults(config.treeRoot, {
            "name": "content",
            "text": CQ.I18n.getMessage("Site"),
            "draggable": false,
            "singleClickExpand": true,
            "expanded":true
        });

        var treeLoaderConfig = CQ.Util.applyDefaults(config.treeLoader, {
            "dataUrl": CQ.HTTP.externalize("/content.ext.json"),
            "requestMethod":"GET",
            "baseParams": {
                "predicate": "hierarchy",
                "_charset_": "utf-8"
            },
            "baseAttrs": {
                "singleClickExpand":true
            },
            "listeners": {
                "beforeload": function(loader, node){
                    //checking if default servlet is used
                        var extJsonString = ".ext.json";
                        if (this.dataUrl && ((this.dataUrl.length < extJsonString.length) ||
                            (this.dataUrl.lastIndexOf(extJsonString) !== this.dataUrl.length - extJsonString.length))) {
                        // the node path needs to be added to the baseParams for passing information to the
                        // servlet which is being used in place of the default one # CQ-62988
                        this.baseParams.path = node.getPath();
                    } else {
                        // need to set the url to the node path for correct resolution by the default servlet
                        // as per CQ-38976
                        this.dataUrl = node.getPath() + ".ext.json";
                    }
                }
            }
        });

        this.treePanel = new CQ.Ext.tree.TreePanel({
            "region":"west",
            "lines": CQ.themes.BrowseDialog.TREE_LINES,
            "bodyBorder": CQ.themes.BrowseDialog.TREE_BORDER,
            "bodyStyle": CQ.themes.BrowseDialog.TREE_STYLE,
            "height": "100%",
            "width": 200,
            "autoScroll": true,
            "containerScroll": true,
            "root": new CQ.Ext.tree.AsyncTreeNode(treeRootConfig),
            "loader": new CQ.Ext.tree.TreeLoader(treeLoaderConfig),
            "defaults": {
                "draggable": false
            }
        });

        var width = CQ.themes.BrowseDialog.WIDTH;
        var items = this.treePanel;

        if (config.parBrowse) {
            this.treePanel.on("click", this.onSelectPage.createDelegate(this));

            // Paragraph store
            var reader = new CQ.Ext.data.JsonReader({
                "id":            "path",
                "root":          "paragraphs",
                "totalProperty": "count",
                "fields":        [ "path", "html" ]
            });
            this.paraProxy = new CQ.Ext.data.HttpProxy({
                "url": "/"
            });
            this.paraStore = new CQ.Ext.data.Store({
                "proxy":    this.paraProxy,
                "reader":   reader,
                "autoLoad": false
            });

            // Paragraph template
            var paraTemplate = new CQ.Ext.XTemplate(
                '<tpl for=".">',
                    '<div class="cq-paragraphreference-paragraph">{html}</div>',
                '</tpl>'
            );

            // Paragraph view
            this.data = new CQ.Ext.DataView({
                "id": "cq-paragraphreference-data",
                "region": "center",
                "store": this.paraStore,
                "tpl": paraTemplate,
                "itemSelector": "div.cq-paragraphreference-paragraph",
                "selectedClass": "cq-paragraphreference-selected",
                "singleSelect": true,
                "style": { "overflow": "auto" }
            });

            // init dialog width and fields
            width = 550;
            items = new CQ.Ext.Panel({
                "border":false,
                "layout": "border",
                "items": [ this.treePanel, this.data ]
            });
        }

        CQ.Util.applyDefaults(config, {
            "title": CQ.I18n.getMessage("Select Path"),
            "closable": true,
            "width": width,
            "height": CQ.themes.BrowseDialog.HEIGHT,
            "minWidth": CQ.themes.BrowseDialog.MIN_WIDTH,
            "minHeight": CQ.themes.BrowseDialog.MIN_HEIGHT,
            "resizable": CQ.themes.BrowseDialog.RESIZABLE,
            "resizeHandles": CQ.themes.BrowseDialog.RESIZE_HANDLES,
            "autoHeight": false,
            "autoWidth": false,
            "cls":"cq-browsedialog",
            "ok": function() { this.hide(); },
           // "buttons": CQ.Dialog.OKCANCEL,
            buttons:[{
                text:"Preview Image",
                handler: function() {
                	var path = this.getSelectedPath();
                	window.open(path);
                }
            },{
                text:"OK",
                handler: function() {
                var path = this.getSelectedPath();
                var anchor = this.parBrowse ? this.getSelectedAnchor() : null;

                var value;
                if (anchor) {
                    value = CQ.Util.patchText(this.pathField.parLinkPattern, [path, anchor]);
                } else {
                    value = CQ.Util.patchText(this.pathField.linkPattern, path);
                }
                if (this.pathField.suffix) {
                    value += this.pathField.suffix;
                }

                this.pathField.setValue(value);

                this.pathField.fireEvent("dialogselect", this.pathField, path, anchor);
                this.hide();
                }
            },
               {
                text:"Cancel",
                handler: function() {
                	this.hide();
                }
            }],
            "items": items
        });
        CQ.custombrowsedialog.superclass.constructor.call(this, config);
    },

    /**
     * @private
     */
    onSelectPage: function(node, event) {
        this.paraProxy.api["read"].url = CQ.HTTP.externalize(node.getPath() + ".paragraphs.json", true);
        this.paraStore.reload();
    }
});

CQ.Ext.reg('custombrowsedialog', CQ.custombrowsedialog);
