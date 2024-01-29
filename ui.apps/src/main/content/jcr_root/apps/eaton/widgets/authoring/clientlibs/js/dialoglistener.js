"use strict";
(function ($, $document, $window) {
    $(document).on('change', ".cq-FileUpload", function() {

		/** Header Component Image**/
        var headerImagePath = $("[name='./primaryNavBGImage']").val();
        if(typeof headerImagePath !== "undefined"){
            var headerImgAltTxt = getAltText(headerImagePath);
            $("[name='./primaryNavImgAtlTxt']").val(headerImgAltTxt);
            $("[name='./primaryNavImgAtlTxt']").attr("value",headerImgAltTxt);
        }

        /** Logo Component Image**/
        var logoImagePath = $("[name='./logoReference']").val();
        if(typeof logoImagePath !== "undefined"){
            var logoaAltTxt = getAltText(logoImagePath);
            $("[name='./logoAltText']").val(logoaAltTxt);
            $("[name='./logoAltText']").attr("value",logoaAltTxt);
        }

        /** General Image Component**/
        var imagePath = $("[name='./fileReference']").val();
        if(typeof imagePath !== "undefined"){
            var altTxt = getAltText(imagePath);
            $("[name='./alt']").val(altTxt);
            $("[name='./alt']").attr("value",altTxt);
        }

		/** Landing-Hero Component Image**/
        var heroImagePath = $("[name='./landingHeroImage']").val();
        if(typeof heroImagePath !== "undefined"){
            var heroAltTxt = getAltText(heroImagePath);
            $("[name='./landingHeroImageAltText']").val(heroAltTxt);
            $("[name='./landingHeroImageAltText']").attr("value",heroAltTxt);
        }

        /** Feature-Card Component Image**/
        var featureCardImgPath = $("[name='./cardImage']").val();
        if(typeof featureCardImgPath !== "undefined"){
            var ftrCardAltTxt = getAltText(featureCardImgPath);
            $("[name='./cardImageAltText']").val(ftrCardAltTxt);
            $("[name='./cardImageAltText']").attr("value",ftrCardAltTxt);
        }

        /** Feature-Block Component Image**/
        var featureBlockImgPath = $("[name='./featureImage']").val();
        if(typeof featureBlockImgPath !== "undefined"){
            var ftrBlockAltTxt = getAltText(featureBlockImgPath);
            $("[name='./featureImageAltText']").val(ftrBlockAltTxt);
            $("[name='./featureImageAltText']").attr("value",ftrBlockAltTxt);
        }

        /** Article-Feature Component Image**/
        var articleImgPath = $("[name='./articleImage']").val();
        if(typeof articleImgPath !== "undefined"){
            var articleAltTxt = getAltText(articleImgPath);
            $("[name='./articleImageAltText']").val(articleAltTxt);
            $("[name='./articleImageAltText']").attr("value",articleAltTxt);
        }

        /** Category-Hero Component Image**/
        var catHeroImgPath = $("[name='./imagePath']").val();
        if(typeof catHeroImgPath !== "undefined"){
            var catHeroAltTxt = getAltText(catHeroImgPath);
            $("[name='./imageAltText']").val(catHeroAltTxt);
            $("[name='./imageAltText']").attr("value",catHeroAltTxt);
        }

		/** Media-Gallery Component Manual Links Image**/
        var mediaImageName = $(this).prop( 'name' );
        if(mediaImageName){
            var mediaGalImgPathName = mediaImageName.replace('imageName','galleryImagePath');
            var mediaGalImgPath = $("[name='"+mediaGalImgPathName+"']").val();
            if(typeof mediaGalImgPath !== "undefined"){
                var mediaGalAltTxt = getAltText(mediaGalImgPath);
                var altTextName = mediaImageName.replace('imageName','manualListGalleryImageAltText');
                $("[name='"+altTextName+"']").val(mediaGalAltTxt);
                $("[name='"+altTextName+"']").attr("value",mediaGalAltTxt);
            }
        }

        /** Home-Page Hero, Tile-Block-Links(ttil-links), Media-Gallery(fixedlist) and Quote-List Component Image**/
        var homeImageName = $(this).prop( 'name' );
        if(homeImageName){
            var homeGalImgPathName = homeImageName.replace('imageName','imagePath');
            var homeGalImgPath = $(this).find("[name='"+homeGalImgPathName+"']").val();
            if(typeof homeGalImgPath !== "undefined"){
                var homeGalAltTxt = getAltText(homeGalImgPath);
                var homeAltTextName = homeImageName.replace('imageName','imageAltText');
                $("[name='"+homeAltTextName+"']").val(homeGalAltTxt);
                $("[name='"+homeAltTextName+"']").attr("value",homeGalAltTxt);
            }
        }

        /** Feature List Component Image**/
        var ftrListImageName = $(this).prop( 'name' );
        if(ftrListImageName){
            var ftrListImgPathName = ftrListImageName.replace('imageName','featureListImage');
            var ftrListImgPath = $("[name='"+ftrListImgPathName+"']").val();
            if(typeof ftrListImgPath !== "undefined"){
                var ftrListAltTxt = getAltText(ftrListImgPath);
                var ftrListAltTextName = ftrListImageName.replace('imageName','featureListImageAltText');
                $("[name='"+ftrListAltTextName+"']").val(ftrListAltTxt);
                $("[name='"+ftrListAltTextName+"']").attr("value",ftrListAltTxt);
            }
        }

        /** Article List, Card-List and Related-Products Component Image**/
        var articleListImageName = $(this).prop( 'name' );
        if(articleListImageName){
            var articleListImgPathName = articleListImageName.replace('imageName','manualLinkImage');
            var articleListImgPath = $("[name='"+articleListImgPathName+"']").val();
            if(typeof articleListImgPath !== "undefined"){
                var articleListAltTxt = getAltText(articleListImgPath);
                var articleListAltTextName = articleListImageName.replace('imageName','manualLinkImageAltText');
                $("[name='"+articleListAltTextName+"']").val(articleListAltTxt);
                $("[name='"+articleListAltTextName+"']").attr("value",articleListAltTxt);
            }
        }

        /** Feature Story Component Image**/
        var ftrStoryImageName = $(this).prop( 'name' );
        if(ftrStoryImageName){
            var ftrStoryImgPathName = ftrStoryImageName.replace('imageName','featureStoryImage');
            var ftrStoryImgPath = $("[name='"+ftrStoryImgPathName+"']").val();
            if(typeof ftrStoryImgPath !== "undefined"){
                var ftrStoryAltTxt = getAltText(ftrStoryImgPath);
                var ftrStoryAltTextName = ftrStoryImageName.replace('imageName','featureStoryImageAltText');
                $("[name='"+ftrStoryAltTextName+"']").val(ftrStoryAltTxt);
                $("[name='"+ftrStoryAltTextName+"']").attr("value",ftrStoryAltTxt);
            }
        }

        /** Category-Article Component Image**/
        var catHLArticleImageName = $(this).prop( 'name' );
        if(catHLArticleImageName){
            var catHLArticleImgPathName = catHLArticleImageName.replace('imageName','cardManualLinkImage');
            var catHLArticleImgPath = $("[name='"+catHLArticleImgPathName+"']").val();
            if(typeof catHLArticleImgPath !== "undefined"){
                var catHLArticleAltTxt = getAltText(catHLArticleImgPath);
                var catHLArticleAltTextName = catHLArticleImageName.replace('imageName','cardManualLinkImageAltText');
                $("[name='"+catHLArticleAltTextName+"']").val(catHLArticleAltTxt);
                $("[name='"+catHLArticleAltTextName+"']").attr("value",catHLArticleAltTxt);
            }
        }

        /** Thumbnail Image List Component Image**/
        var tnListImageName = $(this).prop( 'name' );
        if(tnListImageName){
            var tnListImgPathName = tnListImageName.replace('imageName','thumbnailImage');
            var tnListImgPath = $("[name='"+tnListImgPathName+"']").val();
            if(typeof tnListImgPath !== "undefined"){
                var tnListAltTxt = getAltText(tnListImgPath);
                var tnListAltTextName = tnListImageName.replace('imageName','thumbnailImageAltText');
                $("[name='"+tnListAltTextName+"']").val(tnListAltTxt);
                $("[name='"+tnListAltTextName+"']").attr("value",tnListAltTxt);
            }
        }

        /** Tile-Block_links Component Image**/
        var tileBlockImageName = $(this).prop( 'name' );
        if(tileBlockImageName){
            var tileBlockImgPathName = tileBlockImageName.replace('imageName','tileBlockLinksImage');
            var tileBlockImgPath = $("[name='"+tileBlockImgPathName+"']").val();
            if(typeof tileBlockImgPath !== "undefined"){
                var tileBlockAltTxt = getAltText(tileBlockImgPath);
                var tileBlockAltTextName = tileBlockImageName.replace('imageName','tileBlockLinksImageAltText');
                $("[name='"+tileBlockAltTextName+"']").val(tileBlockAltTxt);
                $("[name='"+tileBlockAltTextName+"']").attr("value",tileBlockAltTxt);
            }
        }

    });

        function getAltText(imagePath) {
            var altText = "";
            if (imagePath) {
                    $.ajax({
                        type: 'GET',
                        url: '/eaton/getimagealttextservlet',
                        async: false,
                        data: {
                            imagePath: imagePath
                        },
                        success: function (response) {
                            if( null != response){
                                altText = response.altText;
                            }

                        },
                        error: function (response) {
                            console.log("Error fetching the response");
                            var dialog = document.querySelector('#errorDialog');
                            dialog.show();
                        }
                    });
                }
            return altText;
        }
    $(document).ready(function() {
        var dialog = new Coral.Dialog().set({
                id: 'errorDialog',
                variant: "error",
                header: {
                  innerHTML: 'Error Dialog'
                },
                content: {
                  innerHTML: 'Error fetching the alt-text response'
                },
                footer: {
                  innerHTML: '<button is="coral-button" variant="primary" coral-close>Close</button>'
                }
        });
        document.body.appendChild(dialog);
    });
})($, $(document), $(window));