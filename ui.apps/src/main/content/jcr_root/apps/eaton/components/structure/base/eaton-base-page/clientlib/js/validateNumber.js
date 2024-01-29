(function ($,window) {
	var registry = $(window).adaptTo("foundation-registry");
	registry.register("foundation.validation.validator", {
	selector: "[data-validation=txt-validate]",
		validate: function(el) {
			var percolateContent = $(el);
			var perConValue = percolateContent.val();
				if (perConValue.length > 0){
				var numPattern =/^\s*(\+|-)?((\d+(\.\d+)?)|(\.\d+))\s*$/;
				var resultMatch = perConValue.match(numPattern);
				if(resultMatch==null || perConValue < 0){
				return "Please Enter Valid Number.";
				}           
			}
		}
	});
})($,window);
