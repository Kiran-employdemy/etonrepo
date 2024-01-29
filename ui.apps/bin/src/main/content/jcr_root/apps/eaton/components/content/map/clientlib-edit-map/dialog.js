(function ($, $document, Coral) {
    $document.on("dialog-ready", function() {

        const form = document.querySelector('form.cq-dialog');

        var categoryGroupListField = {},categoryListField = {}
            categoryGroupsData = [];

        var csvCategoryGroupListField = {},csvCategoryListField = {}
                    csvCategoryGroupsData = [];

        var preSeltCategoryGroupListField = {},preSeltCategoryListField = {}
                    preSeltCategoryGroupsData = [];

        function getData() {
            $.ajax({
                url: '/eaton/bullseye/bullsEyeAllCategories?responseFormat=dialogDropDownData',
                async: false,
                dataType: 'json',
                success: function (data) {
                    categoryGroupsData = data.categoryGroups;
                }
            });
        }

        function getCSVData() {
            $.ajax({
                url: '/eaton/bullseye/bullsEyeAllCategories?responseFormat=dialogDropDownData',
                async: false,
                dataType: 'json',
                success: function (data) {
                    csvCategoryGroupsData = data.categoryGroups;
                }
            });
        }

        function getPreSeltData() {
            $.ajax({
                url: '/eaton/bullseye/bullsEyeAllCategories?responseFormat=dialogDropDownData',
                async: false,
                dataType: 'json',
                success: function (data) {
                    preSeltCategoryGroupsData = data.categoryGroups;
                }
            });
        }

        // Common Function
        function getContent(i) {
            var content = {};
            $.ajax({
                url: `${form.action}/categoryGroupList/item${i}.-1.json`,
                async: false,
                dataType: 'json',
                success: function (data) {
					//console.log("data N--->"+JSON.stringify(data));
                    content.category = data.categoryGroup;
                    content.lists = data.categoryList;
                }
            });
            return content;
        }

        // Common Function
        function getCSVContent(i) {
            var content = {};
            $.ajax({
                url: `${form.action}/csvCategoryGroupList/item${i}.-1.json`,
                async: false,
                dataType: 'json',
                success: function (data) {
                    //console.log("data CSV--->"+JSON.stringify(data));
                    content.category = data.csvCategoryGroup;
                    content.lists = data.csvCategoryList;
                }
            });
            return content;
        }

        // Common Function
        function getPreSeltContent(i) {
            var content = {};
            $.ajax({
                url: `${form.action}/preSeltCategoryGroupList/item${i}.-1.json`,
                async: false,
                dataType: 'json',
                success: function (data) {
                    //console.log("data CSV--->"+JSON.stringify(data));
                    content.category = data.preSeltCategoryGroup;
                    content.lists = data.preSeltCategoryList;
                }
            });
            return content;
        }

        function getElementIndex(node) {
            var index = 0;
            while ( (node = node.previousElementSibling) ) {
                index++;
            }
            return index;
        }

        function getCategories(category) {
            for (var i = 0, len = categoryGroupsData.length; i < len; ++i) {
                if (category == categoryGroupsData[i].categoryGroup) {
                    return categoryGroupsData[i].categories;
                }
            }
        }

        function getCSVCategories(csvCategory) {
            for (var i = 0, len = csvCategoryGroupsData.length; i < len; ++i) {
                if (csvCategory == csvCategoryGroupsData[i].categoryGroup) {
                    return categoryGroupsData[i].categories;
                }
            }
        }

        function getPreSeltCategories(preSeltCategory) {
            for (var i = 0, len = preSeltCategoryGroupsData.length; i < len; ++i) {
                if (preSeltCategory == preSeltCategoryGroupsData[i].categoryGroup) {
                    return preSeltCategoryGroupsData[i].categories;
                }
            }
        }

        function populateModel(select, category) {
            var categories = getCategories(category);
            Coral.commons.ready(select, function (component) {
                component.items.clear();
                categories.forEach(function (item) {
                    var option = document.createElement('coral-select-item');
                    option.textContent = item;
                    component.items.add(option);
                });
            });
        }

        function populateCSVModel(select, csvCategory) {
            var csvCategories = getCSVCategories(csvCategory);
            Coral.commons.ready(select, function (component) {
                component.items.clear();
                csvCategories.forEach(function (item) {
                    var option = document.createElement('coral-select-item');
                    option.textContent = item;
                    component.items.add(option);
                });
            });
        }

        function populatePreSeltModel(select, preSeltCategory) {
            var preSeltCategories = getPreSeltCategories(preSeltCategory);
            Coral.commons.ready(select, function (component) {
                component.items.clear();
                preSeltCategories.forEach(function (item) {
                    var option = document.createElement('coral-select-item');
                    option.textContent = item;
                    component.items.add(option);
                });
            });
        }

        /**
         * @param {*} select (element)
         * @param {*} content (jcr:content)
         */
        function populateItem(select, key, index, content) {
            for (var i = 0, len = categoryGroupsData.length; i < len; ++i) {
                if (key == 'categoryGroup') {
                    var option = document.createElement('coral-select-item');
                    option.textContent = categoryGroupsData[i].categoryGroup
                    select.appendChild(option);
                } else if (content.category == categoryGroupsData[i].categoryGroup) { 
                    categoryGroupsData[i].categories.forEach(function (item) {
                        var option = document.createElement('coral-select-item');
                        option.textContent = item;
                        select.appendChild(option);
                    });
                }
            }
			
			Coral.commons.ready(select, function (component) {
                if (key == 'categoryGroup') {
                    if (content) {
                        component.value = content.category;
                    }
					categoryListField.subroot = form.querySelector(`[data-granite-coral-multifield-name="./categoryGroupList/item${index}/./categoryList"]`);
					categoryListField.subadd = categoryListField.subroot.querySelector(':scope > button[coral-multifield-add]');
					categoryListField.subadd.addEventListener('click', function() {
						setTimeout(function(){
							categoryListField.subroot = form.querySelector(`[data-granite-coral-multifield-name="./categoryGroupList/item${index}/./categoryList"]`);
							categoryListField.items = categoryListField.subroot.querySelectorAll(':scope > coral-multifield-item');
							var subindex = categoryListField.items.length - 1,
								select2 = categoryListField.items[subindex].querySelector(`coral-select[name="./categoryGroupList/item${index}/./categoryList/item${subindex}/./category"]`);
							
							populateModel(select2, component.value);
						}, 500);
					});
				} else if (content) {
						component.value = content.lists["item"+index].category;
                }
            });
        }

        function populateCSVItem(select, key, index, content) {
            for (var i = 0, len = csvCategoryGroupsData.length; i < len; ++i) {
                if (key == 'csvCategoryGroup') {
                    var option = document.createElement('coral-select-item');
                    option.textContent = csvCategoryGroupsData[i].categoryGroup
                    select.appendChild(option);
                } else if (content.category == csvCategoryGroupsData[i].categoryGroup) {
                    csvCategoryGroupsData[i].categories.forEach(function (item) {
                        var option = document.createElement('coral-select-item');
                        option.textContent = item;
                        select.appendChild(option);
                    });
                }
            }

            Coral.commons.ready(select, function (component) {
                if (key == 'csvCategoryGroup') {
                    if (content) {
                        component.value = content.category;
                    }
                    csvCategoryListField.subroot = form.querySelector(`[data-granite-coral-multifield-name="./csvCategoryGroupList/item${index}/./csvCategoryList"]`);
                    csvCategoryListField.subadd = csvCategoryListField.subroot.querySelector(':scope > button[coral-multifield-add]');
                    csvCategoryListField.subadd.addEventListener('click', function() {
                        setTimeout(function(){
                            csvCategoryListField.subroot = form.querySelector(`[data-granite-coral-multifield-name="./csvCategoryGroupList/item${index}/./csvCategoryList"]`);
                            csvCategoryListField.items = csvCategoryListField.subroot.querySelectorAll(':scope > coral-multifield-item');
                            var subindex = csvCategoryListField.items.length - 1,
                                select2 = csvCategoryListField.items[subindex].querySelector(`coral-select[name="./csvCategoryGroupList/item${index}/./csvCategoryList/item${subindex}/./csvCategory"]`);

                            populateCSVModel(select2, component.value);
                        }, 500);
                    });
                } else if (content) {
                    component.value = content.lists["item"+index].csvCategory;
                }
            });
        }

        function populatePreSeltItem(select, key, index, content) {
            for (var i = 0, len = preSeltCategoryGroupsData.length; i < len; ++i) {
                if (key == 'preSeltCategoryGroup') {
                    var option = document.createElement('coral-select-item');
                    option.textContent = preSeltCategoryGroupsData[i].categoryGroup
                    select.appendChild(option);
                } else if (content.category == preSeltCategoryGroupsData[i].categoryGroup) {
                    preSeltCategoryGroupsData[i].categories.forEach(function (item) {
                        var option = document.createElement('coral-select-item');
                        option.textContent = item;
                        select.appendChild(option);
                    });
                }
            }

            Coral.commons.ready(select, function (component) {
                if (key == 'preSeltCategoryGroup') {
                    if (content) {
                        component.value = content.category;
                    }
                    preSeltCategoryListField.subroot = form.querySelector(`[data-granite-coral-multifield-name="./preSeltCategoryGroupList/item${index}/./preSeltCategoryList"]`);
                    preSeltCategoryListField.subadd = preSeltCategoryListField.subroot.querySelector(':scope > button[coral-multifield-add]');
                    preSeltCategoryListField.subadd.addEventListener('click', function() {
                        setTimeout(function(){
                            preSeltCategoryListField.subroot = form.querySelector(`[data-granite-coral-multifield-name="./preSeltCategoryGroupList/item${index}/./preSeltCategoryList"]`);
                            preSeltCategoryListField.items = preSeltCategoryListField.subroot.querySelectorAll(':scope > coral-multifield-item');
                            var subindex = preSeltCategoryListField.items.length - 1,
                                select2 = preSeltCategoryListField.items[subindex].querySelector(`coral-select[name="./preSeltCategoryGroupList/item${index}/./preSeltCategoryList/item${subindex}/./preSeltCategory"]`);

                            populatePreSeltModel(select2, component.value);
                        }, 500);
                    });
                } else if (content) {
                    component.value = content.lists["item"+index].preSeltCategory;
                }
            });
        }

        function populateItems() {
			var count = categoryGroupListField.items.length;
            for (var i = 0, len = categoryGroupListField.items.length; i < len; ++i) {
                var content = getContent(i);
                var select1 = categoryGroupListField.items[i].querySelector(`coral-select[name="./categoryGroupList/item${i}/./categoryGroup"]`);
                populateItem(select1, 'categoryGroup', i, content);
                if(content.lists){
					for (var j = 0, length = Object.keys(content.lists).length-1; j < length; ++j) {
	                    var category = content.lists["item"+j].category;
						var select2 = categoryGroupListField.items[i].querySelector(`coral-select[name="./categoryGroupList/item${i}/./categoryList/item${j}/./category"]`);
						populateItem(select2, 'category', j, content);
	                }
                }
            }
        }

        function populateCSVItems() {
        			var count = csvCategoryGroupListField.items.length;
                    for (var i = 0, len = csvCategoryGroupListField.items.length; i < len; ++i) {
                        var content = getCSVContent(i);
                        var select1 = csvCategoryGroupListField.items[i].querySelector(`coral-select[name="./csvCategoryGroupList/item${i}/./csvCategoryGroup"]`);
                        populateCSVItem(select1, 'csvCategoryGroup', i, content);
                        if(content.lists){
	        				for (var j = 0, length = Object.keys(content.lists).length-1; j < length; ++j) {
	                            var category = content.lists["item"+j].csvCategory;
	        					var select2 = csvCategoryGroupListField.items[i].querySelector(`coral-select[name="./csvCategoryGroupList/item${i}/./csvCategoryList/item${j}/./csvCategory"]`);
	        					populateCSVItem(select2, 'csvCategory', j, content);
	                        }
                        }
                    }
                }

       function populatePreSeltItems() {
        			var count = preSeltCategoryGroupListField.items.length;
                    for (var i = 0, len = preSeltCategoryGroupListField.items.length; i < len; ++i) {
                        var content = getPreSeltContent(i);
                        var select1 = preSeltCategoryGroupListField.items[i].querySelector(`coral-select[name="./preSeltCategoryGroupList/item${i}/./preSeltCategoryGroup"]`);
                        populatePreSeltItem(select1, 'preSeltCategoryGroup', i, content);
                        if(content.lists){
	        				for (var j = 0, length = Object.keys(content.lists).length-1; j < length; ++j) {
	                            var category = content.lists["item"+j].preSeltCategory;
	        					var select2 = preSeltCategoryGroupListField.items[i].querySelector(`coral-select[name="./preSeltCategoryGroupList/item${i}/./preSeltCategoryList/item${j}/./preSeltCategory"]`);
	        					populatePreSeltItem(select2, 'preSeltCategory', j, content);
	                        }
                        }
                    }
                }

        function init() {
            try {
                categoryGroupListField.root = form.querySelector('[data-granite-coral-multifield-name="./categoryGroupList"]');
                categoryGroupListField.add = categoryGroupListField.root.querySelector(':scope > button[coral-multifield-add]');
                categoryGroupListField.items = categoryGroupListField.root.querySelectorAll(':scope > coral-multifield-item');

                csvCategoryGroupListField.root = form.querySelector('[data-granite-coral-multifield-name="./csvCategoryGroupList"]');
                csvCategoryGroupListField.add = csvCategoryGroupListField.root.querySelector(':scope > button[coral-multifield-add]');
                csvCategoryGroupListField.items = csvCategoryGroupListField.root.querySelectorAll(':scope > coral-multifield-item');

                preSeltCategoryGroupListField.root = form.querySelector('[data-granite-coral-multifield-name="./preSeltCategoryGroupList"]');
                preSeltCategoryGroupListField.add = preSeltCategoryGroupListField.root.querySelector(':scope > button[coral-multifield-add]');
                preSeltCategoryGroupListField.items = preSeltCategoryGroupListField.root.querySelectorAll(':scope > coral-multifield-item');
            }
            catch(err) {
                console.log(err.message + ', likely due to N/A component');
                return;
            }

            getData();
            getCSVData();
            getPreSeltData();

            if (categoryGroupListField.items) {
                // give coral a sec to inject fields
                setTimeout(function(){
                    populateItems();
                }, 500);
            }

            if (csvCategoryGroupListField.items) {
                // give coral a sec to inject fields
                setTimeout(function(){
                    populateCSVItems();
                }, 500);
            }

            if (preSeltCategoryGroupListField.items) {
                // give coral a sec to inject fields
                setTimeout(function(){
                    populatePreSeltItems();
                }, 500);
            }

            categoryGroupListField.add.addEventListener('click', function() {
                // give coral a sec to inject fields
                setTimeout(function(){
                    categoryGroupListField.items = categoryGroupListField.root.querySelectorAll(':scope > coral-multifield-item');
                    var index = categoryGroupListField.items.length - 1,
                        select = categoryGroupListField.items[index].querySelector(`coral-select[name="./categoryGroupList/item${index}/./categoryGroup"]`);
                    populateItem(select, 'categoryGroup', index, null);
                }, 500);
            });

            csvCategoryGroupListField.add.addEventListener('click', function() {
                // give coral a sec to inject fields
                setTimeout(function(){
                    csvCategoryGroupListField.items = csvCategoryGroupListField.root.querySelectorAll(':scope > coral-multifield-item');
                    var index = csvCategoryGroupListField.items.length - 1,
                        select = csvCategoryGroupListField.items[index].querySelector(`coral-select[name="./csvCategoryGroupList/item${index}/./csvCategoryGroup"]`);
                    populateCSVItem(select, 'csvCategoryGroup', index, null);
                }, 500);
            });

            preSeltCategoryGroupListField.add.addEventListener('click', function() {
                // give coral a sec to inject fields
                setTimeout(function(){
                    preSeltCategoryGroupListField.items = preSeltCategoryGroupListField.root.querySelectorAll(':scope > coral-multifield-item');
                    var index = preSeltCategoryGroupListField.items.length - 1,
                        select = preSeltCategoryGroupListField.items[index].querySelector(`coral-select[name="./preSeltCategoryGroupList/item${index}/./preSeltCategoryGroup"]`);
                    populatePreSeltItem(select, 'preSeltCategoryGroup', index, null);
                }, 500);
            });
        }

        init();
    });

 })($, $(document), Coral);