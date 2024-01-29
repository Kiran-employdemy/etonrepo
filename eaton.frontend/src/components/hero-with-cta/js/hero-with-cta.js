$( document ).ready(function() {
  $( '.hero-with-cta__cta' ).first().addClass( 'b-button__tertiary b-button__tertiary--light' );
  if ($( '.hero-with-cta__cta' ).length > 1) {
    $( '.hero-with-cta__cta' ).last().addClass( 'b-button__primary b-button__primary--dark' );
    $( '.hero-with-cta__cta' ).last().removeClass( 'b-button__tertiary b-button__tertiary--light' );
  }
});

