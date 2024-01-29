/** NOTE:
 * This is a modified version of the legacy file from beacon-clientLibraries
 */
	/* eslint-disable */
if ($('.interactive-image-container').length > 0) {
  jQuery(document).ready(function($) {
    let Cdate = new Date(),
      currentMonth = Cdate.getMonth() + 1,
      isComplete = true,
      winterSeason = [1,2,3,10,11,12],
      summerJson,
      winterJson,
      randWinter,
      randDesk,
      randMob,
      randSummer,
      imageproperties = jQuery('.interactive-image-container').attr('imageComponentData').replace(/[\r\n]\s*/g, '\n'),
      imagedata = JSON.parse(imageproperties),
      weatherPickerBlock = $('.hero-panel__Weather-picker__block'),
      summerImageUrl = weatherPickerBlock.data('summer-image-url'),
      winterImageUrl = weatherPickerBlock.data('winter-image-url');

    function summerRandImages() {
      let summerJson = [
        {
          desk: image,
          mob: imagemob
        },
        {
          desk: image1,
          mob: image1mob
        }
      ];

      return summerJson;
    }

    function winterRandImages() {
      let winterJson = [
        {
          desk: image2,
          mob: image2mob
        },
        {
          desk: image3,
          mob: image3mob
        }
      ];

      return winterJson;
    }

    if (in_array(currentMonth, winterSeason) != '-1') {
      winterJson = winterRandImages();
      randWinter = winterJson[Math.floor(Math.random() * winterJson.length)];
      randDesk = randWinter.desk;
      randMob = randWinter.mob;
      weatherPickerBlock.css('background-image', 'url(' + winterImageUrl + ')');
      jQuery('.hero-panel__Weather-picker__winter').addClass('hero-panel__Weather-picker__winter--active');
      jQuery('.hero-panel__Weather-picker__winter').parent().addClass('hero-panel__Weather-picker__block--winter');
      jQuery('.hero-panel__Weather-picker__winter').parent().removeClass('hero-panel__Weather-picker__block--summer');
    } else {
      summerJson = summerRandImages();
      randSummer = summerJson[Math.floor(Math.random() * summerJson.length)];
      randDesk = randSummer.desk;
      randMob = randSummer.mob;
      weatherPickerBlock.css('background-image', 'url(' + summerImageUrl + ')');
      jQuery('.hero-panel__Weather-picker__summer').addClass('hero-panel__Weather-picker__summer--active');
      jQuery('.hero-panel__Weather-picker__summer').parent().addClass('hero-panel__Weather-picker__block--summer');
      jQuery('.hero-panel__Weather-picker__summer').parent().removeClass('hero-panel__Weather-picker__block--winter');

    }

    jQuery('.hero-panel__item-background--desktop').css('background-image', 'url(' + randDesk + ')');
    jQuery('.hero-panel__item-background--mobile').css('background-image', 'url(' + randMob + ')');

    jQuery('.hero-panel__Weather-picker__summer').click(function() {
      if (isComplete) {
                // start loader
        $('.loader').animate({
          top: '0%'
        },1000, function() {
          setTimeout(function() {
            isComplete = true;
          }, 1000);
        });

        isComplete = false;

        $('.loader-circle').show();
        summerJson = summerRandImages();
        randSummer = summerJson[Math.floor(Math.random() * summerJson.length)];
        randDesk = randSummer.desk;
        randMob = randSummer.mob;

                // $('<img/>').attr('src', randDesk).load(function() {
                //    $(this).remove();
        setTimeout(function() {
          jQuery('.hero-panel__item-background--desktop').css('background-image', 'url(' + randDesk + ')');
          $('.loader').css({top: '-100%'});
          $('.loader-circle').hide();
        }, 1000);
                // });

                // $('<img/>').attr('src', randMob).load(function() {
                //    $(this).remove();
        setTimeout(function() {
          jQuery('.hero-panel__item-background--mobile').css('background-image', 'url(' + randMob + ')');
          $('.loader').css({top: '-100%'});
          $('.loader-circle').hide();
                        // end loader
        }, 1000);
                // });

        weatherPickerBlock.css('background-image', 'url(' + summerImageUrl + ')');
        jQuery('.hero-panel__Weather-picker__winter').removeClass('hero-panel__Weather-picker__winter--active');
        jQuery('.hero-panel__Weather-picker__summer').addClass('hero-panel__Weather-picker__summer--active');
        jQuery('.hero-panel__Weather-picker__summer').parent().addClass('hero-panel__Weather-picker__block--summer');
        jQuery('.hero-panel__Weather-picker__summer').parent().removeClass('hero-panel__Weather-picker__block--winter');
        jQuery('#summer').parent().css('display','block');
        jQuery('#winter').parent().css('display','none');
      }
    });

    jQuery('.hero-panel__Weather-picker__winter').click(function() {
      if (isComplete) {
                // start loader
        $('.loader').animate({
          top: '0%'
        }, 1000, function() {
          setTimeout(function() {
            isComplete = true;
          }, 1000);
        });

        isComplete = false;

        $('.loader-circle').show();
        winterJson = winterRandImages();
        randWinter = winterJson[Math.floor(Math.random() * winterJson.length)];
        randDesk = randWinter.desk;
        randMob = randWinter.mob;

                // $('<img/>').attr('src', randDesk).load(function() {
                //    $(this).remove();
        setTimeout(function() {
          $('.hero-panel__item-background--desktop').css('background-image', 'url(' + randDesk + ')');
          $('.loader').css({top: '-100%'});
          $('.loader-circle').hide();
        }, 1000);
                // });

                // $('<img/>').attr('src', randMob).load(function() {
        $(this).remove();
        setTimeout(function() {
          $('.hero-panel__item-background--mobile').css('background-image', 'url(' + randMob + ')');
          $('.loader').css({top: '-100%'});
          $('.loader-circle').hide();
                        // end loader
        }, 1000);
                // });

        jQuery('.hero-panel__item-background--desktop').css('background-image', 'url(' + randDesk + ')');
        jQuery('.hero-panel__item-background--mobile').css('background-image', 'url(' + randMob + ')');

        weatherPickerBlock.css('background-image', 'url(' + winterImageUrl + ')');
        jQuery('.hero-panel__Weather-picker__summer').removeClass('hero-panel__Weather-picker__summer--active');
        jQuery('.hero-panel__Weather-picker__winter').addClass('hero-panel__Weather-picker__winter--active');
        jQuery('.hero-panel__Weather-picker__winter').parent().addClass('hero-panel__Weather-picker__block--winter');
        jQuery('.hero-panel__Weather-picker__winter').parent().removeClass('hero-panel__Weather-picker__block--summer');
        jQuery('#summer').parent().css('display','none');
        jQuery('#winter').parent().css('display','block');
      }
    });
  });

  function in_array(val, arr) {
    let cnt = 0;
    let index = -1;
    $(arr).each(function() {
      if (parseInt(this) == parseInt(val)) {
        index = cnt;
      }
      cnt++;
    });
    return index;
  }
}
/* eslint-enable */


