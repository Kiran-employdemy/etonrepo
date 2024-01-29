$.validator.register({
  selector: '.validate-this-field',
  validate: function(el) {
    var field,

        value;

    field = el.closest("form.foundation-form");

      linkTitleField = field.find("[name='./landingHeroLinkTitle']");
      linkTitle = linkTitleField.val();
      linkDestinationField = field.find("[name='./landingHeroLinkPath']").addClass('validate-this-field');
      linkDestination = linkDestinationField.val();

      if(linkTitle == "" && linkDestination == "") {
			return;
        }
        else if( linkTitle == "" || linkDestination == "") {
      return Granite.I18n.get('Either both the fields should be filled or both should be empty');
    }

  },
  show: function (el, message) {
    var fieldErrorLinkTitle,
        fieldErrorLinkDestination,
        fieldLinkTitle,
        fieldLinkDestination,
        errorLinkTitle,
        errorLinkDestination,
        arrowLinkTitle,
        arrowLinkDestination;

    fieldErrorLinkTitle = $("<span class='coral-Form-fielderror coral-Icon coral-Icon--alert coral-Icon--sizeS' data-init='quicktip' data-quicktip-type='error' />");
    fieldErrorLinkDestination = $("<span class='coral-Form-fielderror coral-Icon coral-Icon--alert coral-Icon--sizeS' data-init='quicktip' data-quicktip-type='error' />");


    	fieldLinkTitle = linkTitleField.closest(".coral-Form-field");
        fieldLinkDestination = linkDestinationField.closest(".coral-Form-field");


    fieldLinkTitle.attr("aria-invalid", "true")
      .toggleClass("is-invalid", true);

    fieldLinkDestination.children().children('input').attr("aria-invalid", "true")
      .toggleClass("is-invalid", true);

    fieldLinkTitle.nextAll(".coral-Form-fieldinfo")
      .addClass("u-coral-screenReaderOnly");

    fieldLinkDestination.nextAll(".coral-Form-fieldinfo")
      .addClass("u-coral-screenReaderOnly");

    errorLinkTitle = fieldLinkTitle.nextAll(".coral-Form-fielderror");
    errorLinkDestination = fieldLinkDestination.nextAll(".coral-Form-fielderror");

    if (errorLinkTitle.length === 0) {
      arrowLinkTitle = fieldLinkTitle.closest("form").hasClass("coral-Form--vertical") ? "right" : "top";

      fieldErrorLinkTitle.attr("data-quicktip-arrow", arrowLinkTitle).attr("data-quicktip-content", message).insertAfter(fieldLinkTitle);
    } else {
      errorLinkTitle.data("quicktipContent", message);
    }

     if (errorLinkDestination.length === 0) {
      arrowLinkDestination = fieldLinkDestination.closest("form").hasClass("coral-Form--vertical") ? "right" : "top";

      fieldErrorLinkDestination.attr("data-quicktip-arrow", arrowLinkDestination).attr("data-quicktip-content", message).insertAfter(fieldLinkDestination);
    } else {
      errorLinkDestination.data("quicktipContent", message);
    }

  },
  clear: function (el) {
    var fieldlinkTitle = linkTitleField.closest(".coral-Form-field");

    fieldlinkTitle.removeAttr("aria-invalid").removeClass("is-invalid");

    fieldlinkTitle.nextAll(".coral-Form-fielderror").tooltip("hide").remove();
    fieldlinkTitle.nextAll(".coral-Form-fieldinfo").removeClass("u-coral-screenReaderOnly");

     var fieldDestinationField = linkDestinationField.closest(".coral-Form-field");

    fieldDestinationField.children().children('input').removeAttr("aria-invalid").removeClass("is-invalid");

    fieldDestinationField.nextAll(".coral-Form-fielderror").tooltip("hide").remove();
    fieldDestinationField.nextAll(".coral-Form-fieldinfo").removeClass("u-coral-screenReaderOnly");
  }
});

$.validator.register({
  selector: 'coral-multifield',
  validate: function(el) {
    var max,
        itemCount;

    max = el.attr("data-max-item");
    itemCount = el.children("coral-multifield-item").length;

    if(itemCount > max){
        return  Granite.I18n.get('The maximum number of CTA buttons that can be authored is two');
    } else{
        return "";
    }

  },
  show: function (el, message) {
    var multiFieldErrorCTALink,
        errorCTALink,
        arrowCTALink;

    multiFieldErrorCTALink = $("<span class='coral-Form-fielderror coral-Icon coral-Icon--alert coral-Icon--sizeS' data-init='quicktip' data-quicktip-type='error' />");
    el.nextAll(".coral-Form-fieldinfo").addClass("u-coral-screenReaderOnly");
    errorCTALink = el.nextAll(".coral-Form-fielderror");

    if (errorCTALink.length === 0) {
      arrowCTALink = el.closest("form").hasClass("coral-Form--vertical") ? "right" : "top";

      multiFieldErrorCTALink.attr("data-quicktip-arrow", arrowCTALink).attr("data-quicktip-content", message).insertAfter(el);
    } else {
      multiFieldErrorCTALink.data("quicktipContent", message);
    }

  },
  clear: function (el) {
    el.nextAll(".coral-Form-fieldinfo").removeClass("u-coral-screenReaderOnly");
    el.nextAll(".coral-Form-fielderror").tooltip("hide").remove();
  }
});
