(function (document, $) {
  $(document).on('dialog-ready', function() {
    var productGridDialog = document.querySelector('form.cq-dialog .product-grid-dialog');
    if (productGridDialog) {
      // This is required because other multifield cusomization listen to the
      // dialog-ready event and this needs to occur after the field has been populated.
      setTimeout(function() {
        var form = productGridDialog.closest('form.foundation-form');
        $(form.querySelectorAll('[name="./sortByOptionsValue"]')).each(function( index, node ) {
            if (node.parentNode.querySelector('.coral-Select-button-text').textContent.trim() === '') {
                var container = node.closest('.coral-Multifield-input')
                container.innerHTML = "<input type='hidden' name='./sortByOptions/"+(index+1)+"@Delete'>";
            }
          });
      }, 1000);
    }
  });
})(document, Granite.$);
