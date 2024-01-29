/*
 ADOBE CONFIDENTIAL

 Copyright 2015 Adobe Systems Incorporated
 All Rights Reserved.

 NOTICE:  All information contained herein is, and remains
 the property of Adobe Systems Incorporated and its suppliers,
 if any.  The intellectual and technical concepts contained
 herein are proprietary to Adobe Systems Incorporated and its
 suppliers and may be covered by U.S. and Foreign Patents,
 patents in process, and are protected by trade secret or copyright law.
 Dissemination of this information or reproduction of this material
 is strictly forbidden unless prior written permission is obtained
 from Adobe Systems Incorporated.
 */
(function(document, $) {
	
	"use strict";
	
	var damDel;
	
	$(document).on("foundation-contentloaded", function(e){

        var deleteActivator = ".cq-damadmin-admin-actions-delete-activator";

		$(document).off("click", deleteActivator).on("click", deleteActivator, function(e) {
            var activator = $(this);
            var type = "asset";
            if (activator.data("type")) {
                type = activator.data("type").split(" ")[0];
            }
			var paths = [];
        	var selectedItems = $(".foundation-selections-item");
       		selectedItems.each(function() {
            	paths.push($(this).get(0).getAttribute('data-foundation-collection-item-id'));
        	});
            var collection = document.querySelector(".cq-damadmin-admin-childpages");
            checkReferenceForResource('deleteAsset', paths, selectedItems, collection);
		});

	});
	
})(document, Granite.$);