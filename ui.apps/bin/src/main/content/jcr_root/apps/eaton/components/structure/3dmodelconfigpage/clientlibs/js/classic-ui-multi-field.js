CQ.Ext.ns("EatonMultifield");
 
EatonMultifield.MultiFieldPanel = CQ.Ext.extend(CQ.Ext.Panel, {
    panelValue: '',
 
    constructor: function(config){
        config = config || {};
        EatonMultifield.MultiFieldPanel.superclass.constructor.call(this, config);
    },
 
    initComponent: function () {
        EatonMultifield.MultiFieldPanel.superclass.initComponent.call(this);
 
        this.panelValue = new CQ.Ext.form.Hidden({
            name: this.name
        });
 
        this.add(this.panelValue);
 
        var dialog = this.findParentByType('dialog');

        dialog.on('beforesubmit', function(){
            var value = this.getValue();
            var parsedJSON = JSON.parse(value);
            if (!parsedJSON.name) {
                 parsedJSON.name = Math.round(Math.random() * 1000000);
                 value =  JSON.stringify(parsedJSON);
            }
            if(value){
                this.panelValue.setValue(value);
            }
        },this);
 
    },
 
    afterRender : function(){
        EatonMultifield.MultiFieldPanel.superclass.afterRender.call(this);
 
        this.items.each(function(){
            if(!this.contentBasedOptionsURL
                    || this.contentBasedOptionsURL.indexOf(CQ.form.Selection.PATH_PLACEHOLDER) < 0){
                return;
            }
 
            this.processPath(this.findParentByType('dialog').path);
        })
    },
 
    getValue: function () {
        var pData = {};
 
        this.items.each(function(i){
            if(i.xtype == "label" || i.xtype == "hidden" || !i.hasOwnProperty("dName")){
                return;
            }
 
            pData[i.dName] = i.getValue();
        });
 
        return $.isEmptyObject(pData) ? "" : JSON.stringify(pData);
    },
 
    setValue: function (value) {
        this.panelValue.setValue(value);
 
        var pData = JSON.parse(value);
 
        this.items.each(function(i){
            if(i.xtype == "label" || i.xtype == "hidden" || !i.hasOwnProperty("dName")){
                return;
            }
 
            i.setValue(pData[i.dName]);
        });
    },
 
    validate: function(){
        return true;
    },
 
    getName: function(){
        return this.name;
    }
});
 
CQ.Ext.reg("multi-field-panel", EatonMultifield.MultiFieldPanel);