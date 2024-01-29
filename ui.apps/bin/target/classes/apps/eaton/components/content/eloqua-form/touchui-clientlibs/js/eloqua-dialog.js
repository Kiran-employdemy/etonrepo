$(document).on("dialog-ready", function () {
  var eloquaDialog = document.querySelector('form.cq-dialog .eloqua-dialog');
  if(eloquaDialog) {
	  var percolateid = "";
	  let tempPath = $("input[name='./tag_asset']").val();
	  if (tempPath === null || tempPath === "" || tempPath === undefined) {
		document.querySelector('[name="./tag_percolateAssetId"]').value = "";
	  }
	  $("input[name='./tag_asset']").change(function () {
		let path = $("input[name='./tag_asset']").val();
		if (path === null || path === "" || path === undefined) {
		  document.querySelector('[name="./tag_percolateAssetId"]').value = "";
		}
		$.ajax({
		  type: 'GET',
		  dataType: 'json',
		  url: path + ".infinity.json",
		  success: function (response) {
			let json = JSON.stringify(response);
			let jsonObj = JSON.parse(json);
			percolateid = jsonObj["jcr:content"]["metadata"]["xmp:eaton-percolate-asset-id"];

			if (document.querySelector('[name="./tag_percolateAssetId"]').value !== percolateid) {
			  if (percolateid == null) {
				document.querySelector('[name="./tag_percolateAssetId"]').value = "";
			  } else {
				document.querySelector('[name="./tag_percolateAssetId"]').value = "";
				document.querySelector('[name="./tag_percolateAssetId"]').value = percolateid;
			  }
			} else {
			  if (percolateid === null || percolateid === "" || percolateid === undefined) {
				document.querySelector('[name="./tag_percolateAssetId"]').value = "";
			  } else {
				document.querySelector('[name="./tag_percolateAssetId"]').value = percolateid;
			  }
			}
		  },
		  error: function () {
			console.log('err')
		  }
		});
	  });
	  $("input[name='./tag_percolateAssetId']").focus(function () {
		alert('You cannot edit this field.');
		$("input[name='./tag_asset']").focus();
	  });
	  //Form ID validation
	  var FORMID_SELECTOR = "formId.validation",
		foundationReg = $(window).adaptTo("foundation-registry");

	  foundationReg.register("foundation.validation.validator", {
		selector: "[data-validation='" + FORMID_SELECTOR + "']",
		validate: function (el) {
		  var input_pattern = /^[0-9]+$/;
		  var error_message = "Invalid input. Only numbers are accepted.";
		  var result = el.value.match(input_pattern);

		  if (result === null) {
			return error_message;
		  }
		}
	  });
	}
});