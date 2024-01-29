package com.eaton.platform.core.models.tileblocklinks;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

	/**
	 * <html> Description: In post processing of this sling model,
	 *  it will load the respective model inputs passing resource object</html> .
	 * 
	 * @author TCS
	 * @version 1.0
	 * @since 2017
	 */
	@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
	public class TileBlockLinksModel {
		
		/** The Constant LOG. */
		private static final Logger LOG = LoggerFactory.getLogger(TileBlockLinksModel.class);

		/** The Constant VERTICAL. */
		private static final String TILE_LINK_ICON = "tile-with-icon";
		
		/** The Constant HORIZONTAL. */
		private static final String TILE_LINK_IMAGE = "tile-with-image";
		/** The view. */
		@Inject
		private String view;		
		
		
		/** The TileLinkListwithIcon. */
		@Inject
		private Resource tileLinkIcon;
		
		/** The TileLinkListwithImage. */
		@Inject
		private Resource tileLinkImage;

		/** The links. */
		private List<?> tileLinks;
		
		/** The Mobile Transforms. */
		@Inject
		private String mobileTrans;
		
		/** The Desktop Transforms. */
		@Inject
		private String desktopTrans;





		/**
		 *  Gets the view.
		 * @return the view
		 */
		public String getView() {
			return view;
		}

		

		/**
		 * Inits the.
		 */
		@PostConstruct
		protected void init() {
			LOG.debug("TileBlockLinksModel :: init() :: Start");	
			if (StringUtils.equals(TILE_LINK_ICON, this.view) ){
				List<TileLinkIconModel> tileLinksIconList = new ArrayList<>();
				populateTileLinkIconModel(tileLinkIcon, tileLinksIconList);
				
			}
			if (StringUtils.equals(TILE_LINK_IMAGE, this.view) ){
				List<TileLinkImageModel> tileLinksImageList = new ArrayList<>();
				populateTileLinkImageModel(tileLinkImage, tileLinksImageList);
			}
			LOG.debug("TileBlockLinksModel :: init() :: End");	
		}

		
		/**
		 * Populate Tile Link with image.
		 *
		 * @param resource the resource
		 * @param tileLinksImageList 
		 * @return List
		 */
		private List<?> populateTileLinkImageModel(Resource resource,
				List<TileLinkImageModel> tileLinksImageList) {
			
			if (resource != null) {
				Iterator<Resource> linkResources = resource.listChildren();                  
					
					while (linkResources.hasNext()) {
						TileLinkImageModel tileLinkImageModel = linkResources.next().adaptTo(TileLinkImageModel.class);
						if (tileLinkImageModel != null) {
							tileLinkImageModel.setDesktopTransformedUrl(getDesktopTrans());
							tileLinkImageModel.setMobileTransformedUrl(getMobileTrans());
							tileLinksImageList.add(tileLinkImageModel);
						}
					}
					this.tileLinks = tileLinksImageList;
			}
			return this.tileLinks;
			
		}

		/**
		 * Populate Tile Link with icon.
		 *
		 * @param resource the resource
		 * @param tileLinksIconList 
		 * @return List
		 */
		public List<?> populateTileLinkIconModel(Resource resource, List<TileLinkIconModel> tileLinksIconList) {
			if (resource != null) {
				Iterator<Resource> linkResources = resource.listChildren();                  
					
					while (linkResources.hasNext()) {
						TileLinkIconModel tileLinkIconModel = linkResources.next().adaptTo(TileLinkIconModel.class);
						if (tileLinkIconModel != null) {
							tileLinkIconModel.setDesktopTransformedUrl(getDesktopTrans());
							tileLinkIconModel.setMobileTransformedUrl(getMobileTrans());
							tileLinksIconList.add(tileLinkIconModel);
						}
					}
					this.tileLinks = tileLinksIconList;
			}
			return this.tileLinks;
		}



	

		/**
		 * Gets the links.
		 *
		 * @return the links
		 */		
		public List<?> getTileLinks() {
			return tileLinks;
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
