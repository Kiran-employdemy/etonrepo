package com.eaton.platform.core.models.multicolumn;
	

	import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;


		/**
		 * <html> Description: This is a Sling Model for TileLinkBlock</html> .
		 *
		 * @author TCS
		 * @version 1.0
		 * @since 2017
		 */
		@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
		public class MultiColumnListModel {

			/** The Item Title. */
			@Inject
			private String itemTitle;
			
			/** The Attribute title */
			@Inject
			private String attrTitle;
			
			/** The Attribute Value */
			@Inject
			private String attrValue;
			
			
		    /** The resource resolver. */
		    @Inject @Source("sling-object")
		    private ResourceResolver resourceResolver;


			/**
			 * Get the itemTitle
			 * @return the itemTitle
			 */
			public String getItemTitle() {
				return itemTitle;
			}


			/**
			 * 
			 * Get the attrTitle
			 * @return the attrTitle
			 */
			public String getAttrTitle() {
				return attrTitle;
			}


			/**
			 * Get the attrValue
			 * @return the attrValue
			 */
			public String getAttrValue() {
				return attrValue;
			}

			

			
		
	}


