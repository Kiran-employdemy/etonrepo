module.exports = {
  templateCtaButton: () => {
    return `
      <div class="col-xs-12 col-md-6 product-family-card__cta padding-bottom-40">
         <a href="http://localhost:4502/content/eaton/us/en-us/forms/vehicle/contact-us-vehicle-group.html"
             data-analytics-event="request quote"
             data-source-tracking="true"
             data-page-path-tracking="true"
             class="b-button b-button__tertiary b-button__tertiary--light"
             title="Contact Eaton"
             target="_blank">
             Contact Eaton
          </a>
      </div>
   `;
  },
};
