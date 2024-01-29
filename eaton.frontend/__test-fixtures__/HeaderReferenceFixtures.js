module.exports = {

  megaMenuSectionEmpty: () => {
    return `
      <nav class="primary-navigation__child-list primary-navigation__items">
         <a href="#" data-link-value="/content/eaton/us/en-us/support.html" data-menu-category="mega-menu-support" aria-expanded="false" class="primary-navigation__menu">
          <span>Support</span>
          <i class="icon icon-chevron-right" aria-hidden="true"></i>
         </a>
      </nav>
      <div class="mega-menu__content" data-target="mega-menu-support">
        <div class="menuoverlay">
           <div class="mega-menu-title">
              <div class="container">
                 <a href="http://aem-dev-disp01.tcc.etn.com:8889/content/eaton/us/en-us/support.html" class="mega-menu-title__level1-link">
                    <i class="mega-menu-title__icon glyphicon glyphicon-menu-left" aria-hidden="true"></i>
                    <div class="mega-menu-title__border">Support</div>
                    <i class="mega-menu-title__icon glyphicon glyphicon-menu-right" aria-hidden="true"></i>
                 </a>
                 <button class="button--reset mega-menu-title__close-menu">
                 <span class="sr-only">Close Search</span>
                 <i class="mega-menu-title__icon icon icon-close" aria-hidden="true"></i>
                 </button>
              </div>
           </div>
           <div class="mega-menu__category">
              <div class="container">
                 <div class="mega-menu__row">
                    <div class="mega-menu__col mega-menu-custom-col">
                       <div class="mega-menu__row">
                          <!-- Integrated code goes here -->
                       </div>
                    </div>
                 </div>
              </div>
           </div>
        </div>
      </div>
    `;
  },

  megaMenuSectionFull: () => {
    return `
      <nav class="primary-navigation__child-list primary-navigation__items">
         <a href="#" data-link-value="/content/eaton/us/en-us/markets.html" data-menu-category="mega-menu-markets" aria-expanded="true" class="primary-navigation__menu">
           <span>Markets</span>
           <i class="icon icon-chevron-right" aria-hidden="true"></i>
         </a>
      </nav>
      <div class="mega-menu__content mega-menu__content--active" data-target="mega-menu-markets">
         <div class="menuoverlay">
            <div class="mega-menu-title">
               <div class="container">
                  <a href="http://aem-dev-disp01.tcc.etn.com:8889/content/eaton/us/en-us/markets.html" class="mega-menu-title__level1-link">
                     <i class="mega-menu-title__icon glyphicon glyphicon-menu-left" aria-hidden="true"></i>
                     <div class="mega-menu-title__border">Markets</div>
                     <i class="mega-menu-title__icon glyphicon glyphicon-menu-right" aria-hidden="true"></i>
                  </a>
                  <button class="button--reset mega-menu-title__close-menu">
                  <span class="sr-only">Close Search</span>
                  <i class="mega-menu-title__icon icon icon-close" aria-hidden="true"></i>
                  </button>
               </div>
            </div>
            <div class="mega-menu__category">
               <div class="container">
                  <div class="mega-menu__row">
                     <div class="mega-menu__col mega-menu-custom-col" style="flex-basis: 75%;">
                        <div class="mega-menu__row">
                           <!-- Integrated code goes here -->
                           <div class="mega-menu__col mega-menu-custom-col-2 mega-menu__col--flex" style="flex-basis: 100%;">
                              <!--/* Include: List - Links */ -->
                              <div class="mega-menu__category-item">
                                 <div class="links-list">
                                    <ul class="links-list__col">
                                       <li class="links-list__item">
                                          <a href="/content/eaton/us/en-us/markets/aviation.html" aria-label="Go to Aerospace" class="links-list__link b-body-copy-small" target="_self" data-source-tracking="false" rel="nofollow">
                                          <span class="links-list__text"><bdi>Aerospace</bdi></span>
                                          </a>
                                       </li>
                                       <li class="links-list__item">
                                          <a href="/content/eaton/us/en-us/markets/buildings.html" aria-label="Go to Buildings" class="links-list__link b-body-copy-small" target="_self" data-source-tracking="false" rel="nofollow">
                                          <span class="links-list__text"><bdi>Buildings</bdi></span>
                                          </a>
                                       </li>
                                    </ul>
                                 </div>
                              </div>
                           </div>
                        </div>
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </div>
    `;
  },


};
