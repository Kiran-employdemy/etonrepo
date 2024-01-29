package com.eaton.platform.core.services.impl;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.ActionConfig;
import com.day.cq.wcm.msm.api.LiveAction;
import com.day.cq.wcm.msm.api.LiveActionFactory;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.config.PublishOnRolloutConfig;

@Designate(ocd = PublishOnRolloutConfig.class)
@Component(immediate = true, service = LiveActionFactory.class, property = {
		LiveActionFactory.LIVE_ACTION_NAME + "=" + PublishOnRolloutLiveActionFactory.LIVE_ACTION_NAME })
public class PublishOnRolloutLiveActionFactory implements LiveActionFactory<LiveAction> {

	@Reference
	private Replicator replicator;

	private String[] LIVESITESCOUNTRYCODE;
	public static final String LIVE_ACTION_NAME = "publishOnRolloutLiveAction";

	@Activate
	@Modified
	protected void init(final PublishOnRolloutConfig publishOnRolloutConfig) {
		LIVESITESCOUNTRYCODE = publishOnRolloutConfig.LIVE_COPY_LIST();
	}

	public LiveAction createAction(Resource config) {
		return new PublishOnRolloutLiveAction(replicator, LIVESITESCOUNTRYCODE);
	}

	public String createsAction() {
		return LIVE_ACTION_NAME;
	}

	/************* LiveAction ****************/
	private static class PublishOnRolloutLiveAction implements LiveAction {
		private String[] liveSitesCountryCodeArray;
		private Replicator replicatorInstance;

		private static final Logger log = LoggerFactory.getLogger(PublishOnRolloutLiveAction.class);

		public PublishOnRolloutLiveAction(Replicator replicator, String[] liveSitesCountryCode) {
			liveSitesCountryCodeArray = liveSitesCountryCode;
			replicatorInstance = replicator;
		}

		public void execute(Resource source, Resource target, LiveRelationship liverel, boolean autoSave,
				boolean isResetRollout) throws WCMException {
			log.debug(" *** Executing RolloutAndPublishLiveAction  *** ");
			Session session = null;
			String targetPath = null;
			/*
			 * Publish the content to specific live copies on Rollout
			 */
			try {
				if (target != null && replicatorInstance != null) {
					ResourceResolver resolver = target.getResourceResolver();
					session = resolver.adaptTo(Session.class);
					targetPath = target.getPath();
					log.debug(" *** Publish Page Path : {} ***", targetPath);
					// Check for specific live copies (US, BR, MX, SG, CN, IN) and replicate
					for (String livePageCountryCode : liveSitesCountryCodeArray) {
							if (targetPath.contains(livePageCountryCode)) {
								replicatorInstance.replicate(session, ReplicationActionType.ACTIVATE, targetPath);
								log.debug(" *** Activation completed for page path : {} ***", targetPath);
							}
						}
					}
			} catch (ReplicationException e) {
				log.error(" *** Replication failed for page path : {} ***", e);
			} catch (Exception e) {
				log.error(e.getMessage());
			}

			if (autoSave) {
				try {
					if (session != null) {
						session.save();
					}
				} catch (Exception e) {
					try {
						session.refresh(true);
					} catch (RepositoryException e1) {
						log.error(" *** Unable to refresh the session on publish : {} ***", e);
					}
					log.error(" *** Save to session object failed in RolloutAndPublishLiveAction : {} ***", e);
				}
			}
			log.debug(" *** Execution complete for RolloutAndPublishLiveAction *** ");
		}

		@Override
		public void execute(ResourceResolver arg0, LiveRelationship arg1, ActionConfig arg2, boolean arg3)
				throws WCMException {
		}

		@Override
		public void execute(ResourceResolver arg0, LiveRelationship arg1, ActionConfig arg2, boolean arg3, boolean arg4)
				throws WCMException {
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public String getParameterName() {
			return null;
		}

		@Override
		public String[] getPropertiesNames() {
			return null;
		}

		@Override
		public int getRank() {
			return 0;
		}

		@Override
		public String getTitle() {
			return null;
		}

		@Override
		public void write(JSONWriter arg0) throws JSONException {

		}

	}

	/************* Deprecated *************/
	@Deprecated
	public void execute(ResourceResolver arg0, LiveRelationship arg1, ActionConfig arg2, boolean arg3)
			throws WCMException {
	}

	@Deprecated
	public void execute(ResourceResolver arg0, LiveRelationship arg1, ActionConfig arg2, boolean arg3, boolean arg4)
			throws WCMException {
	}

	@Deprecated
	public String getParameterName() {
		return null;
	}

	@Deprecated
	public String[] getPropertiesNames() {
		return null;
	}

	@Deprecated
	public int getRank() {
		return 0;
	}

	@Deprecated
	public String getTitle() {
		return null;
	}

	@Deprecated
	public void write(JSONWriter arg0) throws JSONException {
	}
}
