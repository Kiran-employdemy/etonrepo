//-----------------------------------
// M-48: Module Media Gallery
//-----------------------------------
'use strict';

let App = window.App || {};
App.mediaGallery = function () {

  const componentClass = '.module-media-gallery';
  const $componentEl = $(componentClass);

  // Placeholder variables for Multilanguage strings
  // let i18nStrings = {};

  // Cached DOM Elements
  //--------------
  const $slideCarousel = $componentEl.find('.module-media-gallery__slide-list');
  const $thumbnailCarousel = $componentEl.find('.module-media-gallery__thumbnail-list');
  const $thumbnailItems = $componentEl.find('.module-media-gallery__thumbnail-item');


  /**
   * Initialize Media Gallery
   */
  const init = () => {
    initializeSlideCarousel();
    initializeThumbnailCarousel();
  };


  /**
   * Determine the position of the thumbnail item and slide to the corresponding item in slide carousel
   */
  const navigateSlideCarousel = (event) => {
    event.preventDefault();

    const $activeSlide = $(event.currentTarget);
    const activeSlideIndex = $activeSlide.data('slick-index');
    const $activeMediaGallery = $activeSlide.closest(componentClass);

    // Add Visual "active" state only to the clicked thumbnail
    $activeMediaGallery.find('.module-media-gallery__thumbnail-item').removeClass('active');
    $activeSlide.addClass('active');

    $activeMediaGallery.find('.module-media-gallery__slide-list').slick('slickGoTo', activeSlideIndex, true);
  };


  /**
   * Configure Slick Carousel - Main Slide Container
   */
  const setDownloadUrlInDektopButtons = (slick) => {
    const $currentSlider = $(slick.$slides[slick.currentSlide]);
    const $downloadBtn = $currentSlider.find('.module-media-gallery__download');
    const $downloadImageUrl = $downloadBtn.length > 0 ? $downloadBtn.attr('href') : false;
    const $desktopBtn = $componentEl.find('.module-media-gallery__slide-utility--desktop [download]');

    if ($downloadImageUrl) {
      $desktopBtn.fadeIn().attr('href', $downloadImageUrl);
    } else {
      $desktopBtn.fadeOut();
    }
  };


  /**
   * Configure Slick Carousel - Main Slide Container
   */
  const initializeSlideCarousel = () => {

    $.each($slideCarousel, (index, item) => {

      const $currentSlider = $(item);
      const $currentComponent = $currentSlider.closest(componentClass);
      const $slideContainer = $currentComponent.find('.module-media-gallery__slide-container');

      // EATON-682: Blue line under image thumbnail doesn't change to match active image
      $currentSlider.on('afterChange', (event, slick, currentSlide) => {
        updateActiveThumbnail($currentComponent, slick.currentSlide);
        setDownloadUrlInDektopButtons(slick);
      });

      // init the desktop download button
      $currentSlider.on('init', function(event, slick) {
        setDownloadUrlInDektopButtons(slick);
      });

      // adds a unique identifier for each (top and desciption) slider
      let $descriptionSlider = $currentSlider
                               .parents('.module-media-gallery')
                               .find('.module-media-gallery__slide-list__description');
      $descriptionSlider.attr('desciption-slider', index);
      $currentSlider.attr('top-slider', index);

      $descriptionSlider.slick({
        asNavFor: '[top-slider=' + index + ']',
        slidesToShow: 1,
        slidesToScroll: 1,
        autoplay: false,
        dots: false,
        adaptiveHeight: true,
        accessibility: true,
        lazyLoad: 'ondemand',
        arrows: false
      });

      // Initialize Preview Area Carousel
      $currentSlider.slick({
        asNavFor: '[desciption-slider=' + index + ']',
        slidesToShow: 1,
        slidesToScroll: 1,
        autoplay: false,
        dots: false,
        adaptiveHeight: false,
        accessibility: true,
        lazyLoad: 'ondemand',
        prevArrow: $slideContainer.find('.module-media-gallery__prev-arrow'),
        nextArrow: $slideContainer.find('.module-media-gallery__next-arrow'),
        responsive: [
          {
            breakpoint: 991,
            settings: {
              dots: true,
              dotsClass: 'module-media-gallery__dots'
            }
          }
        ]
      });
    });
  };


  /**
   * Configure Slick Carousel - Thumbnail Container
   */
  const initializeThumbnailCarousel = () => {

    // If the Parent componet is Product card, show 5 thumbnails, else show 4 as default
    let numSlides = ($componentEl.closest('.eaton-product-detail-card').length > 0)
      ? 5
      : 4;

    // Subscribe Event Listeners before the Carousel is initialized
    //--------------
    // Bind the thumbnail carousel to the preview carousel
    $thumbnailItems.on('click', navigateSlideCarousel);

    // Determine the active thumbnail item on initialization
    $thumbnailCarousel.on('init', function(event, slick) {
      $thumbnailCarousel.find('[data-slick-index="0"]').addClass('active');
    });

    // Init SlickJS
    //--------------
    $.each($thumbnailCarousel, (index, item) => {

      const $currentSlider = $(item);
      const $thumbnailContainer = $currentSlider.closest(componentClass).find('.module-media-gallery__thumbnail-container');

      $currentSlider.slick({
        slidesToShow: numSlides,
        slidesToScroll: numSlides,
        autoplay: false,
        dots: false,
        accessibility: true,
        prevArrow: $thumbnailContainer.find('.module-media-gallery__prev-arrow'),
        nextArrow: $thumbnailContainer.find('.module-media-gallery__next-arrow')
      });
    });
  };


  /**
  * Set the active thumbnail to the given slideIndex number
  * @param {jQueyElement} $currentComponent
  * @param {number} slideIndex
  */
  const updateActiveThumbnail = ($currentComponent, slideIndex) => {
    const $currentThumbnailSlider = $currentComponent.find('.module-media-gallery__thumbnail-list');
    const $currentThumbnailItems = $currentComponent.find('.module-media-gallery__thumbnail-item');

    // Move to the slider to the active thumbnail if is not currently visible
    $currentThumbnailSlider.slick('slickGoTo', slideIndex, true);

    // Toggle the custom "active" class that highlights the active thumbnail in the UI
    $currentThumbnailItems.removeClass('active');
    $currentThumbnailItems.filter(`[data-slick-index="${ slideIndex }"]`).addClass('active');
  };


  /**
  * If containing DOM element is found, Initialize and Expose public methods
  */
  if ($componentEl.length > 0) {
    init();
  }

}();
