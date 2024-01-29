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
public class FeatureListModel {

		@Inject
		private String featureListHeader;

		@Inject
		private Resource featureList;
		
		/** The desktop trans. */
		@Inject
		private String desktopTrans;
		
		/** The mobile trans. */
		@Inject
		private String mobileTrans;

		/** The links. */
		private List<FeatureListInfoModel> featureLists;
		
		/**
		 * Inits the.
		 */
		@PostConstruct
		protected void init() {
			if (null != featureList) {				
				populateModel(featureList);
			}
		}

		/**
		 * Populate model.
		 * @param quoteLists 
		 * @param Resource -the resource
		 * @return List
		 */
		public List<FeatureListInfoModel> populateModel(Resource resource) {
			if (resource != null) {
				Iterator<Resource> linkResources = resource.listChildren();	
				List<FeatureListInfoModel> featureListsModel = new ArrayList<>();
				//	List<HomePageHeroLinkListModel> links = new ArrayList<QuotesListInfoModel>();CommentedCode
					while (linkResources.hasNext()) {
						FeatureListInfoModel featureListFields = linkResources.next().adaptTo(FeatureListInfoModel.class);
						if (featureListFields != null) {
							featureListFields.setDesktopTransformedUrl(getDesktopTrans());
							featureListFields.setMobileTransformedUrl(getMobileTrans());
							featureListsModel.add(featureListFields);
						}
					}
			this.featureLists = featureListsModel;	
			
			}
			return this.featureLists;
		}

		
		/**
		 * Gets the links.
		 *
		 * @return the links
		 */
		public List<FeatureListInfoModel> getFeatureLists() {
			return featureLists;
		}

		/**
		 * @return the featureListHeader
		 */
		public String getFeatureListHeader() {
			return featureListHeader;
		}

		/**
		 * @return the desktopTrans
		 */
		public String getDesktopTrans() {
			return desktopTrans;
		}

		/**
		 * @return the mobileTrans
		 */
		public String getMobileTrans() {
			return mobileTrans;
		}
		
}
