(function() {
    var previouslySelectedValue;
    $(document).on("foundation-contentloaded", function(e) {
        $('.txnmyAttributeGroup', e.target).each(function(i, element) {
            Coral.commons.ready(element, function(component) {
                $(component).on("change", function(event) {
                    if (component) {
                        requestFacetList(component.value);
                    }
                });

                $.getJSON(component.closest('form').action + '.json', function(data) {
                    requestFacetList(component.value, data.txnmyAttributeValue);
                });
            });
        });

        $(".txnmyAttributeValue").change(function() {
            previouslySelectedValue = this.value;
        });
    });

    function requestFacetList(activeFacets, selectedId) {
        $.ajax({
            url: '/eaton/content/GuidedFacetValuesDropdown',
            data: {
                groupId: activeFacets
            },
            headers: {
                'Content-Type': 'application/json'
            },
            type: 'GET',
            success: function(data) {
                if (data && data.length > 0) {
                    var facetList = data[0].facetValueList;
                    $(".txnmyAttributeValue").find("coral-select-item").remove()
                    $(".txnmyAttributeValue").append("<coral-select-item value='Select Value'>Select Value</coral-select-item>");
                    facetList.forEach(function(value) {
                        var facetValueLabel = value.facetValueLabel;
                        var facetValueId = value.facetValueId;

                        if (facetValueId === selectedId) {
                            $(".txnmyAttributeValue").append("<coral-select-item value='" + selectedId + "' selected>" + facetValueLabel + "</coral-select-item>");
                        } else if (facetValueId === previouslySelectedValue) {
                            $(".txnmyAttributeValue").append("<coral-select-item value='" + previouslySelectedValue + "' selected>" + facetValueLabel + "</coral-select-item>");
                        } else {
                            $(".txnmyAttributeValue").append("<coral-select-item value='" + facetValueId + "'>" + facetValueLabel + "</coral-select-item>");
                        }
                    });
                }
            }
        });
    }
}());
