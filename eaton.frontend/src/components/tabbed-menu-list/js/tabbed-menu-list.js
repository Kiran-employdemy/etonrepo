//-----------------------------------
// Module M-46: Tabbed Menu List / Category Menu
//-----------------------------------
'use strict';

let App = App || window.App || {};

App.tabbedMenuList = (function() {
  const $component = $('.tabbed-menu-list');
  const $titles = $component.find('.tabbed-menu-list__tab-title');
  const mediaquery = App.global.constants.MEDIA_QUERIES.DESKTOP;
  const mediaquerylg = App.global.constants.MEDIA_QUERIES.DESKTOP_LG;
  const matchmedia = window.matchMedia(mediaquery);
  const matchmedialg = window.matchMedia(mediaquerylg);
  const $collapses = $component.find('.collapse');
  const $tabsTitles = $component.find('.tabbed-menu-list__title.desktop');
  const $tabs = $component.find('.tab-pane');
  const $tabContets = $component.find('.tabbed-menu-list__tab-content');

  const init = () => {
    bindEvents();
    matchFunction(matchmedia);
    matchFunctionLg(matchmedialg);
  };

  const bindEvents = () => {
    $titles.on('click', toogleTitles);
    matchmedia.addListener(matchFunction);
    matchmedialg.addListener(matchFunctionLg);
  };

  const toogleTitles = (event) => {
    const $this = $(event.target);
    let index = $this.data('index');

    // TODO: This can be Cleaned-up, there is no need to use a "$.each()" method here since
    // the jquery method ".on()" handles each element independantly
    // and all behaviors can be attached to the element that was clicked
    // insted of looping over all the existing tabs.

    $.each($titles, function(i, element) {
      let $element = $(element);
      let $tabToggleLink = $element.find('[data-toggle="tab"]');
      let currentIndex = $tabToggleLink.data('index');
      let $tabPane = $component.find('.tab-pane[data-index="' + currentIndex + '"]');

      // Don't proceed if the Tab Link is an External Link (Instead of being a tab)
      // If is an external link there is no need to update aria-attributes/CSS-classes
      if ($tabToggleLink.length <= 0) {
        return;
      }

      // set all to false
      if ($element.hasClass('desktop')) {
        $tabToggleLink.attr('aria-selected', 'false');
      }

      if (currentIndex === index) {
        $element.toggleClass('active');

        let isDesktop = $element.hasClass('desktop');
        $tabToggleLink.attr('aria-selected', isDesktop);

        let isActive = $element.hasClass('active');
        $tabPane.css('display', (isActive ? 'block' : 'none'));
      } else {
        $element.removeClass('active');
        $tabPane.css('display', 'none');
      }
    });
  };

  const matchFunction = (matchmediaParam) => {
    $titles.removeClass('active');
    if (matchmediaParam.matches) {// to desktop
      $collapses.collapse('hide');

      if ($tabContets.length > 0) {
        const windowWidth = $(window).outerWidth();
        let diffWidth;
        if (windowWidth >= App.global.constants.GRID.LG) {
          diffWidth = windowWidth - 1140;
        } else {
          diffWidth = windowWidth - 940;
        }

        $tabContets.css({
          'margin-left': Math.floor(-1 * diffWidth / 2 - 10) + 'px',
          'margin-right': Math.floor(-1 * diffWidth / 2 - 10) + 'px',
          width: 'calc( 100% + ' + (diffWidth + 20) + 'px)'
        });
      }

    } else {// to mobile
      $tabsTitles.removeClass('active');
      $tabs.removeClass('active');
    }
  };

  const matchFunctionLg = (matchmediaParam) => {
    if (matchmediaParam.matches) {
      if ($tabContets.length > 0) {
        const windowWidth = $(window).outerWidth();
        let diffWidth;
        if (windowWidth >= App.global.constants.GRID.LG) {
          diffWidth = windowWidth - 1140;
        } else {
          diffWidth = windowWidth - 940;
        }

        $tabContets.css({
          'margin-left': Math.floor(-1 * diffWidth / 2 - 10) + 'px',
          'margin-right': Math.floor(-1 * diffWidth / 2 - 10) + 'px',
          width: 'calc( 100% + ' + (diffWidth + 20) + 'px)'
        });
      }

    }
  };

  if ($component.length > 0 ) {
    init();
  }

}());
