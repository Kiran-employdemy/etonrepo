$(document).ready(function() {
    var isMobile = false; //initiate as false
    $("#shareThisIcon").click(function() {
        if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
            $(".at-share-dock-outer").toggle();
            isMobile = true;
        } else {
            $(".at4-share").toggle();
            isMobile = false;
        }
    });
    $(".eaton-product-share-cta").parent(".social-share").addClass("container");
});