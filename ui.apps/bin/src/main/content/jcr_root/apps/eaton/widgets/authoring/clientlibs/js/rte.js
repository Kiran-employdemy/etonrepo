(function ($, $document, $window) {
    
    // Hide/show formatting toolbar for pim node rte fields
    $(document).ready(function() {
        $('.coral-RichText-editable').click(function() {
            $('.coral-Form-fieldwrapper').find(".rte-toolbar.is-sticky").removeClass('show-toolbar');
            $(this).parent('div').find(".rte-toolbar.is-sticky").addClass('show-toolbar');
        });

        $('.coral-FixedColumn').click(function() {
            $(".rte-toolbar.is-sticky").removeClass('show-toolbar');
        });
    });

    // Hide/show formatting toolbar for dialog rte fields
    $document.on("dialog-ready", function() {
        $('.coral-RichText-editable').click(function() {
            $('.coral-Form-fieldwrapper').find(".rte-toolbar.is-sticky").removeClass('show-toolbar');
            $(this).parent('div').find(".rte-toolbar.is-sticky").addClass('show-toolbar');
        });

        $('.coral-FixedColumn').click(function() {
            $(".rte-toolbar.is-sticky").removeClass('show-toolbar');
        });
    });
    
})(Granite.$, $(document), $(window));