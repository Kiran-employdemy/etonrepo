(function ($, $document) {
    var COMPOSITE_MULTIFIELD_SELECTOR = "coral-multifield[data-granite-coral-multifield-composite]";

    $document.on("dialog-ready", addNumbering);

    function addNumbering() {
        if (COMPOSITE_MULTIFIELD_SELECTOR) {
            _.each([COMPOSITE_MULTIFIELD_SELECTOR], function (mfSel) {
                var $mField = $(mfSel);

                $mField.on("coral-collection:add coral-collection:remove", function (event) {
                    Coral.commons.ready(this, function () {
                        numberMFItem();
                    });
                });
                Coral.commons.ready(this, () => {
                    numberMFItem();
                });
            });
        }

        function numberMFItem() {
            $('p.mfIndex').remove();
            var $mfItem = $("coral-multifield-item")
            $mfItem.each(function () {
                if (!isNaN($(this).attr("aria-posinset"))) {
                    $(this).prepend('<p class="mfIndex">' + $(this).attr("aria-posinset") + '</p>');
                    if ($(this).attr("aria-posinset") !== $(this).attr("aria-setsize")) {
                        $(this).css('border-bottom', '1px solid #c1c1c1');
                        $(this).css('padding-bottom', '10px')
                    }
                }
            });
        }
    }
}(jQuery, jQuery(document), Granite.author));
