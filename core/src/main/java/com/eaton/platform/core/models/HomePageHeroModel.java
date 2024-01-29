package com.eaton.platform.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;



/**
 * <html> Description: In post processing of this sling model, it will identify
 * the view of the component and; will load the respective model inputs passing
 * resource object</html> .
 * 
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HomePageHeroModel {


		/** The landing hero. */
		@Inject
		private Resource landingHero;

		/** The desktop trans. */
	    @Inject
	    private String desktopTrans;
	    
	    /** The mobile trans. */
	    @Inject
	    private String mobileTrans;
		
	    

		/** The links. */
		private List<HomePageHeroLinkListModel> links;
		
		/**
		 * Inits the.
		 */
		@PostConstruct
		protected void init() {
			if (landingHero != null) {
				populateModel(landingHero);
			}
		}

		/**
		 * Populate model.
		 *
		 * @param resource            the resource
		 * @return Landing Hero
		 */
		public List<?> populateModel(Resource resource) {
			if (resource != null) {
				Iterator<Resource> linkResources = resource.listChildren();
		
					List<HomePageHeroLinkListModel> linksModel = new ArrayList<>();
					while (linkResources.hasNext()) {
						HomePageHeroLinkListModel landingHeroListLink = linkResources
								.next().adaptTo(HomePageHeroLinkListModel.class);
						if (landingHeroListLink != null) {
							landingHeroListLink.setDesktopTransformedUrl(getDesktopTrans());
							landingHeroListLink.setMobileTransformedUrl(getMobileTrans());
							linksModel.add(landingHeroListLink);
						}
					}
					this.links = linksModel;
			
			}
			return this.links;
		}

		/**
		 * Gets the links.
		 *
		 * @return the links
		 */
		public List<HomePageHeroLinkListModel> getLinks() {
			return links;
		}
		
		/**
		 * Gets the desktop trans.
		 *
		 * @return the desktop trans
		 */
		public String getDesktopTrans() {
			return desktopTrans;
		}

		/**
		 * Gets the mobile trans.
		 *
		 * @return the mobile trans
		 */
		public String getMobileTrans() {
			return mobileTrans;
		}

}
