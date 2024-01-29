$(document).ready(function () {
  $('.accordion').each((index, accordionElement) => {
    let accordion = $(accordionElement);

    initPanels(accordion);

    accordion.find('.toggle-accordion').on('click', e => {
      e.stopImmediatePropagation();
      toggleAccordion(accordion);
    });

    accordion.find('.panel').on('hidden.bs.collapse', () => setExpandCollapseText(accordion));
    accordion.find('.panel').on('shown.bs.collapse', () => setExpandCollapseText(accordion));


    accordion.find('.panel__header__title').on('click', e => {
      e.stopImmediatePropagation();
      e.stopPropagation();
      e.preventDefault();
      togglePanel($(e.target).closest('.panel'));
    });
  });


  function togglePanel(panel) {
    panel.find('.panel__header__title').toggleClass('collapsed');

    if (panel.find('.panel-collapse').hasClass('in')) {
      panel.find('.panel-collapse').collapse('hide');
    } else {
      panel.find('.panel-collapse').collapse('show');
    }
  }

  function toggleAccordion(accordion) {
    accordion.find('.toggle-accordion').toggleClass('active');

    let numPanelsOpen = accordion.find('.collapse.in').length;

    if (numPanelsOpen === 0) {
      openAllPanels(accordion);
    } else {
      closeAllPanels(accordion);
    }
  }

  function setExpandCollapseText(accordion) {
    let numPanelsOpen = accordion.find('.collapse.in').length;
    if (numPanelsOpen === 0) {
      let expandText = accordion.find('.toggle-accordion').attr('data-active-text');
      accordion.find('.toggle-accordion').text(expandText);
    } else {
      let collapseText = accordion.find('.toggle-accordion').attr('data-inactive-text');
      accordion.find('.toggle-accordion').text(collapseText);
    }
  }

  function initPanels(accordion) {
    let collapseText = accordion.find('.toggle-accordion').attr('data-active-text');
    if (window.innerWidth < 992) {
      accordion.find('.toggle-accordion').text(collapseText);
      accordion.find('.panel-collapse.in').collapse('hide');
    } else {
      let expandText = accordion.find('.toggle-accordion').attr('data-inactive-text');
      let defaultClosed = accordion.find('.panel-group').attr('data-default-closed');
      if (defaultClosed === 'true') {
        accordion.find('.toggle-accordion').text(collapseText);
      } else {
        accordion.find('.first-panel').addClass('in');
        accordion.find('.first-panel').closest('.panel').find('.panel__header__title').removeClass('collapsed');
        accordion.find('.toggle-accordion').text(expandText);
      }
    }
  }

  function openAllPanels(accordion) {
    let collapseText = accordion.find('.toggle-accordion').attr('data-inactive-text');
    accordion.find('.toggle-accordion').text(collapseText);
    accordion.find('.panel-collapse:not(".in")').collapse('show');
    accordion.find('.panel__header__title').removeClass('collapsed');
  }

  function closeAllPanels(accordion) {
    let expandText = accordion.find('.toggle-accordion').attr('data-active-text');
    accordion.find('.toggle-accordion').text(expandText);
    accordion.find('.panel-collapse.in').collapse('hide');
    accordion.find('.panel__header__title').addClass('collapsed');
  }
});
