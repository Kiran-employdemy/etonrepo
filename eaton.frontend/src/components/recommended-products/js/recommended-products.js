(function(App) {

  document.addEventListener('DOMContentLoaded', () => {
    [...document.querySelectorAll('.product-card-container')].forEach(container => {
      let count = 0;
      let images = container.querySelectorAll('.product-card img');

      setCardHeight(container);
      window.addEventListener('resize', App.global.utils.throttled(1000, () => setCardHeight(container)));
      let panel = container.closest('.panel-collapse');
      if (panel) {
        App.global.utils.watchForClass(panel, 'in').then(() => setCardHeight(container));
      }

      [...images].forEach(img => img.addEventListener('load', () => {
        count++;

        if (count >= images.length) {
          setCardHeight(container);
        }
      }));
    });
  });

  function setCardHeight(container) {
    if (window.innerWidth >= 992) {
      let maxHeight = 0;

      // Reset the height to be whatever the inner card content makes it.
      [...container.querySelectorAll('.product-card')]
      .forEach(card => card.style.height = '');

      // With the height reset, find the tallest card.
      [...container.querySelectorAll('.product-card')]
      .forEach(card => {
        if (card.clientHeight > maxHeight) {
          maxHeight = card.clientHeight;
        }
      });

      // If a max height was found, set all of the cards to be that max height.
      if (maxHeight > 0) {
        [...container.querySelectorAll('.product-card')]
        .forEach(card => card.style.height = maxHeight + 'px');
      }
    }
  }

})(window.App);
