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
 * <html> Description: In post processing of this sling model, 
 *  it will load the respective model inputs passing
 * resource object</html> .
 * 
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class QuoteListModel {

	

		@Inject
		private Resource quoteListResource;

		/** The links. */
		private List<QuotesListInfoModel> quoteLists;
		
		/** The Mobile Transforms. */
		@Inject
		private String mobileTrans;
		
		/** The Desktop Transforms. */
		@Inject
		private String desktopTrans;

		
		/**
		 * Inits the.
		 */
		@PostConstruct
		protected void init() {
			if (quoteListResource != null) {				
				populateModel(quoteListResource);
			}
		}

		/**
		 * Populate model.
		 * @param quoteLists 
		 * @param Resource -the resource
		 * @return List
		 */
		public List<QuotesListInfoModel> populateModel(Resource resource) {
			if (resource != null) {
				Iterator<Resource> linkResources = resource.listChildren();	
				List<QuotesListInfoModel> quoteListsModel = new ArrayList<>();			
					while (linkResources.hasNext()) {
						QuotesListInfoModel quoteListFields = linkResources.next().adaptTo(QuotesListInfoModel.class);
						if (quoteListFields != null) {
							quoteListFields.setDesktopTransformedUrl(getDesktopTrans());
							quoteListFields.setMobileTransformedUrl(getMobileTrans());
							quoteListsModel.add(quoteListFields);
						}
					}
			this.quoteLists = 	quoteListsModel;	
			
			}
			return this.quoteLists;
		}

		
		/**
		 * Gets the links.
		 *
		 * @return the links
		 */
		public List<QuotesListInfoModel> getQuoteLists() {
			return quoteLists;
		}

		/**
		 * @return the mobileTrans
		 */
		public String getMobileTrans() {
			return mobileTrans;
		}

		/**
		 * @return the desktopTrans
		 */
		public String getDesktopTrans() {
			return desktopTrans;
		}


}
