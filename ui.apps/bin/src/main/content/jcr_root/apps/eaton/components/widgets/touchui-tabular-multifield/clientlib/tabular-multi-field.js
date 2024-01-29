(function ($, console) {
  "use strict";

  var listTemplate ='<div class="coral-TabPanel" data-init="tabs"><nav class="coral-TabPanel-navigation"></nav><div class="coral-TabPanel-content"></div></div>';

  CUI.TabularMultifield = new Class({
    toString: "TabularMultifield",
    extend: CUI.Widget,

    /**
     @extends CUI.Widget

     @classdesc A composite field that allows you to add/remove multiple instances of a component.
     The component is added based on a template defined in a <code>&lt;script type=&quot;text/html&quot;&gt;</code> tag.
     The current added components are managed inside a <code>div.tabular-Multifield</code> element.

     @desc Creates a TabularMultifield component.
     @constructs
     */
    construct: function (options) {
      this.script = this.$element.find(".js-tabular-Multifield-input-template");
      this.tabsContainer = this.$element.find(".coral-TabPanel").data("tabs");
      var prevTabIndex = 0;
      var tabs = this.tabsContainer._getTabs();
      tabs.each(function() {
          var $this = $(this);
          var i = $this.text().trim().replace("Tab ", "");
          if (!isNaN(i)) {
              var index = parseInt(i);
              if (index > prevTabIndex) {
                  prevTabIndex = index;
              }
          }
      });
      this.tabIndex = prevTabIndex === 0 ? 1: prevTabIndex + 1;

      if (this.tabsContainer.length === 0) {
        this.tabsContainer = $(listTemplate).prependTo(this.$element).find(".coral-TabPanel").data("tabs");
      }
      var tabularCompositeFieldName = this.$element.attr("name") || "";
      if (tabularCompositeFieldName) {
          tabularCompositeFieldName+= "/";
      }

      var panels = this.tabsContainer._getPanels();
      var self = this;
      panels.each(function(){
        var $this = $(this);
        var panelIndex = $this.index();
        var tabNumber = $(tabs[panelIndex]).text().trim().replace("Tab ", "");
        self.adjustInputNames($this, tabularCompositeFieldName + "tab" + tabNumber);
        self._handleRadioButtons($this);
      });

      this._adjustMarkup();
      this._addListeners();

  $('.product-grid-facet-group-tab').click(function() {
        var contentId = $(this).attr('aria-controls');
        var nameOfFacet = $('#' + contentId).find('select').attr("name");
        var splitnameOfFacet = nameOfFacet.split("/");
       facetGroupValueList("./tabularFields/"+splitnameOfFacet[2]+"/facetGroup","./tabularFields/"+splitnameOfFacet[2]+"/facetValue");
     });

    },

    _handleRadioButtons: function(item) {
        item.find("input[type='radio']").each(function(){
           var $this = $(this);
           if ($this.attr("checked") == "checked") {
               $this.attr("aria-selected",  true);
               $this.prop("checked", "checked");
           }
        });
    },

    /**
     * Enhances the markup required for multifield.
     * @private
     */
    _adjustMarkup: function () {
      this.$element.addClass("coral-Multifield");
    },

    /**
     * Initializes listeners.
     * @private
     */
    _addListeners: function () {
      var self = this;
      var tabularCompositeFieldName = self.$element.attr("name") || "";
      if (tabularCompositeFieldName) {
          tabularCompositeFieldName+= "/";
      }

      //add
      this.$element.on("click", ".js-tabular-Multifield-add", function (e) {
          var item = $(self.script.html().trim());
          self.adjustInputNames(item, tabularCompositeFieldName + "tab" + self.tabIndex);
          self.handleCheckBoxesDefaultValue(item);
          var tabCount = self.tabsContainer._getTabs().length;
          var tabContent = "<span>Tab " + self.tabIndex + "<span> <i class=\"close-tab-button js-tabular-Multifield-remove coral-Icon coral-Icon--close coral-Icon--sizeXS\"></i>"
          self.tabsContainer.addItem({tabContent:tabContent, panelContent:item, index:tabCount-1});
    		self.tabIndex++;

          $(self.$element).trigger("cui-contentloaded");
          var indexNum = self.tabIndex-1;
      facetGroupValueList("./tabularFields/tab"+indexNum+"/facetGroup","./tabularFields/tab"+indexNum+"/facetValue");

      });
      //remove
      this.$element.on("click", ".js-tabular-Multifield-remove", function (e) {
          var $this = $(this);
          var tabIndex = $(this).closest(".coral-TabPanel-tab.is-active").index();
          self.tabsContainer.removeItem(tabIndex);
          var tabCount = self.tabsContainer._getTabs().length;
          if (tabCount === 1) {
              self.tabIndex = 1;
          }
      });
    },
    handleCheckBoxesDefaultValue: function(item) {
        item.find("input[type='checkbox'][data-check-in-multifield='true']").each(function(){
            var $this = $(this);
            $this.prop("checked", true);
        });
    },
    adjustInputNames: function (item, prefix) {
        item.find("[name]").each(function(){
            var $this = $(this);
            var name = $this.attr("name");
            $this.attr("name", "./" + prefix + name.substr(1));
        });
      }
  });
        var facetGrpValue = {};
        var tabClickCount = 0;
    //Populate List of facet value
		function facetGroupValueList(facetGroupName,facetValueName){
        var $facetGroup = $("[name='" + facetGroupName + "']"),
            $facetValueDelete = $("[name='" + facetValueName + "@Delete']"),
            cuifacetGroup = $facetGroup.closest(".coral-Select").data("select");
        cuifacetGroup.on('selected.select', function(event){
            var $facetValueAdd = $facetValueDelete.siblings('button');
            var $facetValueAddProp = $facetValueAdd ? $facetValueDelete.siblings('button').attr('coral-multifield-add') : false;
            if (typeof $facetValueAddProp !== undefined && $facetValueAddProp !== false) {
                var $facetValue;
                $facetValueDelete.siblings('coral-multifield-item').find('.coral3-Multifield-remove').click();
                var facetGrp = $facetGroup.closest(".coral-Select").find("button span").html();
                _.each(facetGrpValue[facetGrp], function (facetValue) {
                    $facetValueAdd.click();
                });
                setTimeout(function () {
                    _.each(facetGrpValue[facetGrp], function (facetValue, index) {
                        if ($($facetValueDelete.siblings('coral-multifield-item')[index]).find('input[name="./facetValue"]').length)
                            $facetValueDelete.siblings('coral-multifield-item').find('input').attr("name", facetValueName);
                        if ($($facetValueDelete.siblings('coral-multifield-item')[index]).find("[name='" + facetValueName + "']").length) {
                            $facetValue = $($facetValueDelete.siblings('coral-multifield-item')[index]).find("[name='" + facetValueName + "']");
                            $facetValue.val(facetValue);
                        }
                    });
                }, 100);
            }
        });
        function fillFacetValues(data){
            var facetValues;
            _.each(data, function(facetValue, facetGroup){
                facetValues = facetValue;
                facetGrpValue[facetGroup] = facetValues;
            });
        }
        if(tabClickCount <=0) {
            $.getJSON("/eaton/content/tagAttributesFacetValuesDropdown.json?pageLink=" + window.location.pathname).done(fillFacetValues);
            tabClickCount += 1;
        }
       }

  CUI.Widget.registry.register("tabularmultifield", CUI.TabularMultifield);

  if (CUI.options.dataAPI) {
    $(document).on("cui-contentloaded.data-api", function (e) {
      CUI.TabularMultifield.init($("[data-init~=tabularmultifield]", e.target));
    });
  }

  /*
  If we need to store the ID but want to display the label this will change out
  the label for the id when the dialog loads. This would be needed if EAT-360 is implemented.
	$(document).on("dialog-ready", function () {
        $('.coral-TabPanel-tab').click(function() {
   		var tabName = $(this).find("span").text().toString().replace(/\s/g, '').toLowerCase();
        var facetGrp= './tabularFields/'+tabName+'/facetGroup';
        var facetVal= './tabularFields/'+tabName+'/facetValue';
            function populateFacets(data){
            _.each(data, function(facetValue, facetGroup){
                if(facetGroup == $("[name='"+facetGrp+"']").find('option:selected').html()){
                if($("[name='"+facetGrp+"']").find('option:selected').val() != ''){
                	$("[name='"+facetVal+"']").each(function(){
                        var currentFacetVal = $(this).val();
                        var facetValItem = $(this);
                            $(facetValue).each(function(){
                                if(currentFacetVal == $(this)[0].value && Number(currentFacetVal) == currentFacetVal){
                                var $label = $("<label>").addClass("coral-Textfield").addClass("tabularmultifield-label").text($(this)[0].label);
                                    facetValItem.siblings('label').remove();
                                    $label.insertBefore(facetValItem);
                                	facetValItem.val($(this)[0].value);
                                    facetValItem.hide();
                                }
                    	    });
            		    });
            	    }
                }
            });
        }
            $.getJSON("/eaton/content/tagAttributesFacetValuesDropdown.json?pageLink="+window.location.pathname).done(populateFacets);
        });
   });
   */
})(jQuery, window.console);
