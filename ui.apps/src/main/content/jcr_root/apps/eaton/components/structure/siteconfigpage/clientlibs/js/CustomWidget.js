try {
      Ejst.CustomWidget= CQ.Ext.extend(CQ.form.CompositeField, {
        /**
         * @private
         * @type CQ.Ext.form.TextField
         */
        hiddenField: null,
         /**
            * @private
            * @type CQ.Ext.form.TextField
        */
        title: null,

         /**
         * @private
         * @type CQ.Ext.form.MultiField
         */
        skillSet: null,
        
        /**
        * @private
        * @type CQ.form.PathField
        */

        constructor: function(config) {
            config = config || {};
            var defaults = {
                "border": true,
                "padding": 10,
                "style": "padding:10px 0 0 5px;",
                "layout": "form",
                "labelWidth": 200
            };
            config = CQ.Util.applyDefaults(config, defaults);
            Ejst.CustomWidget.superclass.constructor.call(this, config);
        },
        // overriding CQ.Ext.Component#initComponent
        initComponent: function() {
            Ejst.CustomWidget.superclass.initComponent
                .call(this);
            // Hidden field
            this.hiddenField = new CQ.Ext.form.Hidden({
                name: this.name
            });
            this.add(this.hiddenField);
            this.title = new CQ.Ext.form.TextField({
                fieldLabel: "Group",
                allowBlank: false,
                width: 350,
                listeners: {
                    change: {
                        scope: this,
                        fn: this.updateHidden
                    },
                    dialogclose: {
                        scope: this,
                        fn: this.updateHidden
                    }
                }
            });
            this.add(new CQ.Ext.form.Label({
                cls:"customwidget-label",
                text: ""}));
            this.add(this.title);
            //define the inner multifield
            this.skillSet = new CQ.form.MultiField({
                fieldLabel: "Resource Value",
                delimiter:'#',
                fieldDescription:"Click '+' to add more links.",
                width: 350,
                fieldConfig: {
                    "xtype": "ejstcustominternal",
                    allowBlank: false
                },
                listeners: {
                    change: {
                        scope: this,
                        fn: this.updateHidden
                    }
                }
            });
            this.add(new CQ.Ext.form.Label({
                cls:"customwidget-label",
                text: ""}));
            this.add(this.skillSet);

        },
        // overriding CQ.form.CompositeField#setValue
        setValue: function(value) {
            var readVal = '';
            var skillSetValues = '';

            if (value) {
                var colValue = value.split('|');
                if (colValue.length > 0) {
                    readVal = colValue[0];
                    
                    skillSetValues = colValue[1];

                }
            }
            this.title.setValue(readVal);
            this.skillSet.setValue(skillSetValues.split(','));
            //this.iconPath.setValue(iconPathVal);
        },
        // overriding CQ.form.CompositeField#getValue
        getValue: function() {
            return this.getRawValue();
        },
        getRawValue: function() {
            var readVal = this.title.getValue() || "";

            var skillSetValues = this.skillSet.getValue() || "";
            
            var value = readVal + "|" + skillSetValues ;

            this.hiddenField.setValue(value);
            return value;
        },
        updateHidden: function() {
            this.hiddenField.setValue(this.getValue());
        },
        destroyRichText: function() {
            this.el.dom = {};
        }
    });
    CQ.Ext.reg('devprofile', Ejst.CustomWidget);
} catch (e) {
    // suppressing error.
    // error occurs for CQ.form.CompositeField in mobile devices.
}