/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2016 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */
(function (document, $) {

    var $document = $(document);

    $document.on("granite-omnisearch-predicate-clear", function(event) {

        var $form = $(event.target);

        if (event.detail.reset) {
            var name = event.detail.reset ? "" : event.detail.item.getAttribute("name");
            var $items = $form.find(".durationrangepredicate");

            $items.each(function(index, value) {
                var $value = $(value);
                $value.find(".min-duration-range-val input").val("");
                $value.find(".max-duration-range-val input").val("");

                $value.find(".min-mm-range-val").val("");
                $value.find(".min-ss-range-val").val("");
                $value.find(".max-mm-range-val").val("");
                $value.find(".max-ss-range-val").val("");

                $value.parent().find(".search-predicate-durationrangepredicate").prop("disabled", true);
            });
        }

    });

    $document.on("granite-omnisearch-predicate-update", function(event) {

        var predicate = [];
        var queryParameters = event.detail.queryParameters;

        if (queryParameters) {
            predicate = $('.durationrangepredicate');

            predicate.each(function() {

                var $predicate = $(this);

                var min = $predicate.find(".search-predicate-durationrangepredicate-min");
                var minName =  min.get(0).getAttribute("name");
                var max = $predicate.find(".search-predicate-durationrangepredicate-max");
                var maxName = max.get(0).getAttribute("name");

                if (queryParameters[minName]) {

                    $predicate.find(".search-predicate-durationrangepredicate-min").prop("disabled", false);
                    $predicate.find(".search-predicate-durationrangepredicate-min-operation").prop("disabled", false);
                    $predicate.find(".search-predicate-durationrangepredicate-min").val(queryParameters[minName]);
                    //if queryParamerters[minName] then time has been converted to ms - convert back to m:s
                    $predicate.find(".min-mm-range-val").val(getMinutes(queryParameters[minName]));
                    $predicate.find(".min-ss-range-val").val(getSeconds(queryParameters[minName]));

                }

                if (queryParameters[maxName]) {

                    $predicate.find(".search-predicate-durationrangepredicate-max").prop("disabled", false);
                    $predicate.find(".search-predicate-durationrangepredicate-max-operation").prop("disabled", false);

                    $predicate.find(".search-predicate-durationrangepredicate-max").val(queryParameters[maxName]);
					//if queryParamerters[maxName] then time has been converted to ms - convert back to m:s
                    $predicate.find(".max-mm-range-val").val(getMinutes(queryParameters[maxName]));
                    $predicate.find(".max-ss-range-val").val(getSeconds(queryParameters[maxName]));
                }
            });
        }

    });


    $document.on('keypress', '.min-duration-range-val input', function (e) {
        if(e.keyCode === 13) {
            e.stopPropagation();
            e.preventDefault();

            submitForm(e);
        }
    });

    $document.on('keypress', '.max-duration-range-val input', function (e) {
        if(e.keyCode === 13) {
            e.stopPropagation();
            e.preventDefault();

            submitForm(e);
        }
    });

    function submitForm(event) {

        var $this = $(event.target);
        var $form = $this.closest(".granite-omnisearch-form");
        var $parent = $this.closest(".durationrangepredicate");

        var minmm = $parent.find(".min-mm-range-val").val();
        var minss = $parent.find(".min-ss-range-val").val();
        var maxmm = $parent.find(".max-mm-range-val").val();
        var maxss = $parent.find(".max-ss-range-val").val();

        var lowerValue = getMilliseconds(minmm, minss);
        var upperValue = getMilliseconds(maxmm, maxss);

        $parent.find(".search-predicate-durationrangepredicate").prop("disabled", false);
        if (lowerValue === "") {
            $parent.find(".search-predicate-durationrangepredicate-min").prop("disabled", true);
            $parent.find(".search-predicate-durationrangepredicate-min-operation").prop("disabled", true);
        } else {
            $parent.find(".search-predicate-durationrangepredicate-min").val(lowerValue);
        }

        if (upperValue === "") {
            $parent.find(".search-predicate-durationrangepredicate-max").prop("disabled", true);
            $parent.find(".search-predicate-durationrangepredicate-max-operation").prop("disabled", true);
        } else {
            $parent.find(".search-predicate-durationrangepredicate-max").val(upperValue);
        }

        $form.submit();
    }

    function getMilliseconds(mins, seconds) {
        if (mins === "" && seconds === "") {
            return "";
        }
        if (mins === ""){
            mins = 0;
        }
        if (seconds === ""){
            seconds = 0;
        }
        return (mins * 60000) + (seconds * 1000);
    }

    function getSeconds(milliseconds) {
		var seconds = Math.floor(milliseconds / 1000);
        seconds = seconds % 60;
        return (seconds < 10 ? "0"+ seconds : seconds);

    }
     function getMinutes(milliseconds) {
		var seconds = Math.floor(milliseconds / 1000);
        var minutes = Math.floor(seconds / 60 );
        minutes = minutes % 60;
       return minutes;

    }


})(document, Granite.$);
