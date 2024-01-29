Ejst.CustomWidgetInternal = CQ.Ext.extend(CQ.form.CompositeField, {
 
    /**
     * @private
     * @type CQ.Ext.form.TextField
     */
    hiddenField: null,
 
    /**
     * @private
     * @type CQ.Ext.form.ComboBox
     */
    allowField: null,
 
    constructor: function(config) {
        config = config || { };
        var defaults = {
            "border": false,
            "layout": "table",
            "columns":2
        };
        config = CQ.Util.applyDefaults(config, defaults);
        Ejst.CustomWidgetInternal.superclass.constructor.call(this, config);
    },
 
    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        Ejst.CustomWidgetInternal.superclass.initComponent.call(this);
 
        this.hiddenField = new CQ.Ext.form.Hidden({
            name: this.name
        });
        this.add(this.hiddenField);
 
        this.allowField = new CQ.form.Selection({
            type:"select",
            cls:"ejst-customwidgetinternal-1",
            options : [
			           {
			        	   value: "2D_DRAWINGS",
			        	   text: "2D Drawings"
			           },
			           {
			        	   value: "2D_DRAWINGS_PRIMARY",
			        	   text: "2D Drawings Primary"
			           },
			           {
			        	   value: "3D_MODEL",
			        	   text: "3D Model"
			           },
                	   {
			        	   value: "3D_MODEL_PRIMARY",
			        	   text: "3D Model Primary"
			           },
			           {
			        	   value: "AUDIO_VISUAL",
			        	   text: "Audio visual"
			           },
			           {
			        	   value: "AUDIO_VISUAL_PRIMARY",
			        	   text: "Audio visual Primary"
			           },{
			        	   value: "BIM",
			        	   text: "BIM"
			           },
			           {
			        	   value: "BIM_PRIMARY",
			        	   text: "BIM Primary"
			           },
			           {
			        	   value: "BROCHURE",
			        	   text: "Brochure"
			           },{
			        	   value: "BROCHURE_PRIMARY",
			        	   text: "Brochure Primary"
			           },
			           {
			        	   value: "CATALOG",
			        	   text: "Catalog"
			           },
			           {
			        	   value: "CATALOG_PRIMARY",
			        	   text: "Catalog Primary"
			           },{
			        	   value: "CUST_DRAWING",
			        	   text: "Customer Drawing"
			           },
			           {
			        	   value: "CUST_DRAWING_PRIMARY",
			        	   text: "Customer Drawing Primary"
			           },
			           {
			        	   value: "DRAWING",
			        	   text: "Drawing"
			           },
			           {
			        	   value: "DRAWING_PRIMARY",
			        	   text: "Drawing Primary"
			           },
			           {
			        	   value: "EOLN",
			        	   text: "End of Life Notification (EOL)"
			           },{
			        	   value: "EOLN_PRIMARY",
			        	   text: "End of Life Notification (EOL) Primary"
			           },
			           {
			        	   value: "FAQS",
			        	   text: "FAQs"
			           },
			           {
			        	   value: "FAQS_PRIMARY",
			        	   text: "FAQs Primary"
			           },
			           {
                           value : "FLUID COMPATIBILITY",
                           text : "Fluid Compatibility"
                       },
			           {
			        	   value: "INST_LEAFLET",
			        	   text: "Instruction/Installation Leaflet"
			           },
			           {
			        	   value: "INST_LEAFLET_PRIMARY",
			        	   text: "Instruction/Installation Leaflet Primary"
			           },
			           {
			        	   value: "LINE_DRAWING",
			        	   text: "Line Drawing"
			           },{
			        	   value: "LINE_DRAWING_PRIMARY",
			        	   text: "Line Drawing Primary"
			           },
			           {
			        	   value: "MSDS",
			        	   text: "MSDS (Material Safety Data Sheets)"
			           },
			           {
			        	   value: "MSDS_PRIMARY",
			        	   text: "MSDS (Material Safety Data Sheets) Primary"
			           },{
			        	   value: "PRODUCT_AID",
			        	   text: "Product Aid"
			           },
			           {
			        	   value: "PRODUCT_AID_PRIMARY",
			        	   text: "Product Aid Primary"
			           },
			           {
			        	   value: "PRODUCT_PAGE",
			        	   text: "Product Page (web)"
			           },
			           {
			        	   value: "PRODUCT_PAGE_PRIMARY",
			        	   text: "Product Page (web) Primary"
			           },
			           {
			        	   value: "PROD_GUIDE",
			        	   text: "Product Guide"
			           },
			           {
			        	   value: "PROD_GUIDE_PRIMARY",
			        	   text: "Product Guide Primary"
			           },{
			        	   value: "SELL_POLICY_WARRANTIES",
			        	   text: "Selling Policies and Warranties"
			           },
			           {
			        	   value: "SELL_POLICY_WARRANTIES_PRIMARY",
			        	   text: "Selling Policies and Warranties Primary"
			           },
			           {
			        	   value: "SPECIFICATION",
			        	   text: "Specification"
			           },{
			        	   value: "SPECIFICATION_PRIMARY",
			        	   text: "Specification Primary"
			           },
			           {
			        	   value: "TECH_BULLETIN",
			        	   text: "Technical Bulletin"
			           },
			           {
			        	   value: "TECH_BULLETIN_PRIMARY",
			        	   text: "Technical Bulletin Primary"
			           },{
			        	   value: "TIME_CURR_CURVE",
			        	   text: "Time Current Curve"
			           },
			           {
			        	   value: "TIME_CURR_CURVE_PRIMARY",
			        	   text: "Time Current Curve Primary"
			           },
			           {
			        	   value: "WIRING_DIAGRAM",
			        	   text: "Wiring Diagram"
			           },
			           {
			        	   value: "WIRING_DIAGRAM_PRIMARY",
			        	   text: "Wiring Diagram Primary"
			           },
			           {
			        	   value: "APPLICATION",
			        	   text: "Application"
			           },
			           {
			        	   value: "APPLICATION_PRIMARY",
			        	   text: "Application Primary"
			           },
                	   {
			        	   value: "CONFLICT_MINERAL",
			        	   text: "Conflict Mineral"
			           },
                	   {
			        	   value: "CONFLICT_MINERAL_PRIMARY",
			        	   text: "Conflict Mineral Primary"
			           },
                	   {
			        	   value: "INSTALLATION_VIDEO",
			        	   text: "Installation Video"
			           },
			           {
			        	   value: "CASE_STUDIES",
			        	   text: "Case studies"
			           },
			           {
			        	   value: "COMP_INFORMATION",
			        	   text: "Compliance Information"
			           },
			           {
			        	   value: "COMP_COMPARISIONS",
			        	   text: "Competitive comparisons"
			           },
			           {
			        	   value: "PRESENTATIONS",
			        	   text: "Presentations"
			           },
			           {
			        	   value: "WHITE_PAPERS",
			        	   text: "White papers"
			           },
			           {
			        	   value: "CROSS_REFERENCES",
			        	   text: "Cross references"
			           },
			           {
			        	   value: "SALES_NOTES",
			        	   text: "Sales notes"
			           },
			           {
			        	   value: "SERVICE",
			        	   text: "Service"
			           }
           ],
            listeners: {
                selectionchanged: {
                    scope:this,
                    fn: this.updateHidden
                }
            },
            optionsProvider: this.optionsProvider
        });
        this.add(this.allowField);
 
    },
 
    // overriding CQ.form.CompositeField#processPath
    processPath: function(path) {
        console.log("CustomWidgetInternal#processPath", path);
        this.allowField.processPath(path);
    },
 
    // overriding CQ.form.CompositeField#processRecord
    processRecord: function(record, path) {
        console.log("CustomWidgetInternal#processRecord", path, record);
        this.allowField.processRecord(record, path);
    },
 
    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
        var parts = value.split("/");
        this.allowField.setValue(parts[0]);
        this.hiddenField.setValue(value);
    },
 
    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        return this.getRawValue();
    },
 
    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
        if (!this.allowField) {
            return null;
        }
        return this.allowField.getValue();
    },
 
    // private
    updateHidden: function() {
        this.hiddenField.setValue(this.getValue());
    }
 
});
 
// register xtype
CQ.Ext.reg('ejstcustominternal', Ejst.CustomWidgetInternal);