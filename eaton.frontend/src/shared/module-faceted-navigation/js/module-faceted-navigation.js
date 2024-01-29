'use strict';

let App = window.App || {};
App.facets = (function() {
  const $sortOptionsNode = Array.from(document.querySelectorAll('.faceted-navigation-header .dropdown-menu li a'));
  const $initialSort = document.querySelector('.faceted-navigation-header__default--option:not(.cross-reference-sort)');

  const init = () => {
    $(function() {
      addEventListeners();
      matchPath();
      localStorage.setItem('backToSearch','true');
    });
  };

  const addEventListeners = () => {
    $('.native-select').on('change', navigateSelect);
  };

  /** grabing the value of the sorting order from the url and setting that as the placeholder, adding class to currently selected sort order */
  const matchPath = () => {
    if ($initialSort) {
      let currentSort = $initialSort.getAttribute('data-default-sorting-title');
      let buildLabel = '<span class="faceted-navigation-header__sort-link"> ' + currentSort + '</span>';
      let defaultSortingValue = true;
      let urlSorting = false;
      $sortOptionsNode.forEach(index => {
        let urlParams = window.location.pathname;
        if (index.pathname === urlParams) {
          buildLabel = index.innerHTML;
          defaultSortingValue = false;
          index.className += ' faceted-navigation-header__sort-options--selected';
          return urlSorting = true;
        }
        return urlSorting = false;

      });

      if (urlSorting === false) {
        $sortOptionsNode.forEach(index => {
          if (currentSort && defaultSortingValue === true) {
            if (index.innerHTML.trim() === currentSort.trim()) {
              index.className += ' faceted-navigation-header__sort-options--selected';
              defaultSortingValue = false;
            }
          }
        });
      }
      $initialSort.innerHTML = buildLabel;
      const emptyCheck = $initialSort.childNodes[0].innerText;

      if (emptyCheck !== undefined) {
        if (emptyCheck.replace(/^\s\s*/, '').replace(/\s\s*$/, '') === 'null') {
          $initialSort.childNodes[0].innerText = $initialSort.getAttribute('data-default-fallback');
          document.querySelector('.faceted-navigation-header__title').style.display = 'none';
        }
      }
    }
  };

  /**
  * toggle the mobile factes modal
  * @param  { Object } event - the click event object
  */
  const navigateSelect = function(e) {
    e.preventDefault();
    let urlToNavigate = $('.native-select option:selected')[0].value;
    document.location.href = urlToNavigate;
  };

  init();
}());
