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
 * @class CQ.form.customColorField
 * @extends CQ.Ext.form.TriggerField
 * The customColorField lets the user enter a color hex value either
 * directly or using a {@link CQ.Ext.menu.ColorMenu}.
 * @constructor
 * Creates a new customColorField.
 * @param {Object} config The config object
 */
CQ.form.customColorField = CQ.Ext.extend(CQ.Ext.form.TriggerField, {

    /**
     * @cfg {Boolean} showHexValue
     * <code>true</code> to display the hex value of the selected color. This
     * also allows to edit the hex value manually.
     */

    /**
     * @cfg {Array} colors
     * <p>An array of 6-digit color hex code strings (without the # symbol). This array can contain any number
     * of colors.</p>
     * <p>Defaults to the default set of colors from {@link CQ.Ext.ColorPalette}.</p>
     <pre><code>
     var cf = new CQ.form.customColorField({
      colors: ['000000', '993300', '333300']
 });
     </pre></code>
     */

    /**
     * @property {Array} colors
     * <p>An array of 6-digit color hex code strings (without the # symbol). This array can contain any number
     * of colors.</p>
     * <p>Example:</p>
     <pre><code>
     var cf = new CQ.form.customColorField();
     cf.colors[0] = 'FF0000';  // change the first box to red
     </code></pre>
     */
    colors : undefined,

    /**
     * @cfg {String} paletteCls
     * A class for the drop-down color palette.  Defaults to 'x-color-palette'.
     */
    paletteCls: 'x-color-palette',

    /**
     * @cfg {String} paletteStyle
     * A style applied to the drop-down color palette.  Particularly useful for setting the height and width
     * for a customColorField instantiated by a dialog.
     */
    paletteStyle: "",

    constructor: function(config) {
        config = CQ.Util.applyDefaults(config, {
            showHexValue:false,
            triggerClass:"x-form-color-trigger",

            defaultAutoCreate: {
                tag:"input",
                type:"text",
                size:"10",
                autocomplete:"off",
                maxlength:"6"
            },

            lengthText:CQ.I18n.getMessage("Hexadecimal color values must have either 3 or 6 characters and can only contain a-f, A-F, 0-9."),
            blankText:CQ.I18n.getMessage("Must have a hexidecimal value of the format ABCDEF."),

            defaultColor:"FFFFFF",
            curColor:"ffffff",
            colors: new CQ.Ext.ColorPalette().colors.slice(0),  // array copy
            maskRe:/[a-fA-F0-9]/i,
            regex:/[a-fA-F0-9]/i
        });
        CQ.form.customColorField.superclass.constructor.call(this, config);
        this.on("render", this.setDefaultColor);
    },

    validateValue: function(value) {
        if (!this.showHexValue) {
            return true;
        }
        if (value.length < 1) {
            this.el.setStyle( {
                "background-color":"#" + this.defaultColor
            });
            if (!this.allowBlank) {
                this.markInvalid(String.format(this.blankText, value));
                return false
            }
            return true;
        }
        var colorRegex = new RegExp('^(?:[0-9a-fA-F]{3}){1,2}$');
        if (!colorRegex.test(value)) {
            this.markInvalid(String.format(this.lengthText, value));
            return false;
        }
        this.setColor(value);
        return true;
    },

    validateBlur: function() {
        return !this.menu || !this.menu.isVisible();
    },

    getValue: function() {
        return this.curColor || this.defaultColor;
    },

    setValue: function(hex) {
        CQ.form.customColorField.superclass.setValue.call(this, hex);
        this.setColor(hex);
    },

    setColor: function(hex) {
        this.curColor = hex;

        this.el.setStyle( {
            "background-color":"#" + hex,
            "background-image":"none"
        });
        if (!this.showHexValue) {
            this.el.setStyle( {
                "text-indent":"-100px"
            });
            if (CQ.Ext.isIE) {
                this.el.setStyle( {
                    "margin-left":"100px"
                });
            }
        }
    },

    setDefaultColor: function() {
        this.setValue(this.defaultColor);
    },

    menuListeners: {
        select: function(m, d) {
            this.setValue(d);
        },
        show: function() { // retain focus styling
            this.onFocus();
        },
        hide: function() {
            this.focus();
            var ml = this.menuListeners;
            this.menu.un("select", ml.select, this);
            this.menu.un("show", ml.show, this);
            this.menu.un("hide", ml.hide, this);
        }
    },

    handleSelect: function(palette, selColor) {
        this.setValue(selColor);
    },

    onTriggerClick: function() {
        if (this.disabled) {
            return;
        }
        if (this.menu == null) {
            var menuConfig = {
                colors: this.colors.slice(0),  // array copy
                itemCls: this.paletteCls,
                style: this.paletteStyle
            };
            this.menu = new CQ.Ext.menu.ColorMenu(menuConfig);
            this.menu.palette.on("select", this.handleSelect, this);
        }
        this.menu.on(CQ.Ext.apply( {}, this.menuListeners, {
            scope:this
        }));
        this.menu.show(this.el, "tl-bl?");
        var $paletteId = $('#' + this.menu.palette.id);
        $paletteId.find('a').removeClass('x-color-palette-sel');
        if (!this.el.hasClass('x-form-invalid') && $('#' + this.el.id).val() !== "") {
            var newColor = this.curColor;
            $paletteId.find('a.color-' + newColor).addClass('x-color-palette-sel');
        }

    }
});
CQ.Ext.reg("customColorField", CQ.form.customColorField);