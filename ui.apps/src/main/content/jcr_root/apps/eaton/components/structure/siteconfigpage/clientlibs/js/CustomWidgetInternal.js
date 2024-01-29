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
        var jsonDataFromDAM = [];
        $.ajax({
            type: "GET",
            url: "/content/dam/eaton/resources/siteconfig/resourceList.json",
            async: false,
			success: function(data) {
				jsonDataFromDAM = $.parseJSON(JSON.stringify(data));
            }
        });

        this.allowField = new CQ.form.Selection({
            type:"select",
            cls:"ejst-customwidgetinternal-1",
            options : jsonDataFromDAM,
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