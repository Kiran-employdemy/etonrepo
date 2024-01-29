package com.eaton.platform.core.services.impl;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.ActionConfig;
import com.day.cq.wcm.msm.api.LiveAction;
import com.day.cq.wcm.msm.api.LiveActionFactory;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.CustomRolloutConfigService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.nodetype.NodeType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


@Component(immediate = true, service = LiveActionFactory.class, property = {
		LiveActionFactory.LIVE_ACTION_NAME + "=" + RolloutPagePropertiesLiveActionFactory.LIVE_ACTION_NAME })
public class RolloutPagePropertiesLiveActionFactory implements LiveActionFactory<LiveAction> {

	private CustomRolloutConfigService CUSTOM_ROLLOUT_CONFIG_SERVICE = null;
	public static final String LIVE_ACTION_NAME = "rolloutPagePropertiesLiveAction";

	@Reference
	private Replicator replicator;

	@Reference
	private CustomRolloutConfigService customRolloutConfigService;

	@Activate
	@Modified
	protected void init() {
		CUSTOM_ROLLOUT_CONFIG_SERVICE = customRolloutConfigService;
	}

	public LiveAction createAction(Resource config) {
		return new RolloutPagePropertiesLiveAction(CUSTOM_ROLLOUT_CONFIG_SERVICE, replicator);
	}

	public String createsAction() {
		return LIVE_ACTION_NAME;
	}

	/************* LiveAction ****************/
	private static class RolloutPagePropertiesLiveAction implements LiveAction {
		final private Replicator replicatorInstance;
		private static final String PERMISSIONS = "permissions";
		private static final String CQ_LOGIN_PATH = "cq:loginPath";
		private static final String CUG_MIXIN_TYPE = "rep:CugMixin";
		private static final String REP_POLICY_NODE_NAME = "rep:policy";
		private static final String CLOSED_USER_GROUP = "closedUserGroup";
		private static final String FORM_ROUTING_TAG = "form_routing_tag";
		private static final String STRING_CLASS_NAME = "java.lang.String";
		private static final String GRANITE_LOGIN_PATH = "granite:loginPath";
		private static final String REP_CUG_POLICY_NODE_NAME = "rep:cugPolicy";
		private static final String FORM_ROUTING_CQ_TAGS = "form_routing_tag/cq:tags";
		private static final String ACCESS_CONTROL_MIXIN_TYPE = "rep:AccessControllable";
		private static final String ROLLOUT_PAGE_PROPERTY_CONFIG = "rolloutPropertyList";
		private static final String CQ_AUTHENTICATION_REQUIRED = "cq:authenticationRequired";
		private static final String GRANITE_AUTHENTICATION_REQUIRED = "granite:AuthenticationRequired";
		private static final String CQ_PROPERTY_INHERITANCE_CANCELLED = "cq:propertyInheritanceCancelled";
		private static final String ROLLOUT_PAGE_PROPERTY_LIVE_COPY_CONFIG = "rolloutPropertyLiveCopyList";

		private CustomRolloutConfigService customRolloutConfigServiceTmp = null;
		private static final Logger LOGGER = LoggerFactory.getLogger(RolloutPagePropertiesLiveAction.class);


		public RolloutPagePropertiesLiveAction(CustomRolloutConfigService customRolloutConfigService, Replicator replicator) {
			customRolloutConfigServiceTmp = customRolloutConfigService;
			replicatorInstance = replicator;
		}

		public void execute(Resource source, Resource target, LiveRelationship liverel, boolean autoSave,
			boolean isResetRollout) throws WCMException {
			LOGGER.debug("Executing RolloutPagePropertiesLiveAction :: execute() starts");
			if (CommonUtil.getRunModes().contains(Externalizer.AUTHOR) && source != null && target != null && JcrConstants.JCR_CONTENT.equals(source.getName())) {
				ValueMap sourceValueMap = source.adaptTo(ValueMap.class);
				ValueMap targetValueMap = target.adaptTo(ValueMap.class);
				List<String> rolloutPagePropList = new ArrayList<>(customRolloutConfigServiceTmp.getRolloutPageProperties().values());
				if(targetValueMap != null && targetValueMap.get(ROLLOUT_PAGE_PROPERTY_LIVE_COPY_CONFIG) != null) {
					rolloutPagePropList = getRolloutPropList(targetValueMap, rolloutPagePropList, ROLLOUT_PAGE_PROPERTY_LIVE_COPY_CONFIG);
				} else if(sourceValueMap != null && sourceValueMap.get(ROLLOUT_PAGE_PROPERTY_CONFIG) != null) {
					rolloutPagePropList = getRolloutPropList(sourceValueMap, rolloutPagePropList, ROLLOUT_PAGE_PROPERTY_CONFIG);
				}

				for (String propertyName : rolloutPagePropList) {
					Resource srcParent = source.getParent();
					if (source.adaptTo(Node.class) != null && !isPropertyInheritanceCancelled(target, propertyName)) {
						if (FORM_ROUTING_CQ_TAGS.equals(propertyName) && source.getChild(FORM_ROUTING_TAG) != null) {
							setFormRoutingTag(source, target, autoSave, replicatorInstance);
						} else if (srcParent != null && ((CLOSED_USER_GROUP.equals(propertyName)) ||
								(PERMISSIONS.equals(propertyName) && srcParent.getChild(REP_POLICY_NODE_NAME) != null))) {
							setPermissionTabProps(srcParent, target, autoSave, replicatorInstance, propertyName);
						} else {
							rolloutByPropName(target, source, propertyName, autoSave, replicatorInstance);
						}
					}
				}
			}
			LOGGER.debug("Execution complete for RolloutPagePropertiesLiveAction :: execute() ends");

		}

		private static List<String> getRolloutPropList(ValueMap pageValueMap,List<String> rolloutPagePropList, String configName){
			List<String> rolloutPagePropsList = rolloutPagePropList;
			if (STRING_CLASS_NAME.equals(pageValueMap.get(configName).getClass().getName())) {
				rolloutPagePropsList.clear();
				rolloutPagePropsList.add(pageValueMap.get(configName, String.class));
			} else {
				rolloutPagePropsList = Arrays.asList((String[]) (pageValueMap.get(configName)));
			}
			return rolloutPagePropsList;
		}

		private static void rolloutByPropName(Resource target, Resource source, String propertyName,boolean autoSave,Replicator replicatorInstance){
			if (CQ_AUTHENTICATION_REQUIRED.equals(propertyName)) {
				setTargetNodeValue(target, source.getParent(), propertyName, autoSave, replicatorInstance, true);
			} else if (CQ_LOGIN_PATH.equals(propertyName)) {
				setTargetNodeValue(target, source.getParent(), GRANITE_LOGIN_PATH, autoSave, replicatorInstance, true);
			} else {
				setTargetNodeValue(target, source, propertyName, autoSave, replicatorInstance, false);
			}
		}

		private static boolean isPropertyInheritanceCancelled(Resource target, String propertyName){
			LOGGER.debug("Executing RolloutPagePropertiesLiveAction :: isPropertyInheritanceCancelled() starts");
			final ValueMap targetvm = target.adaptTo(ValueMap.class);
			if(targetvm != null){
				String[] inheritanceCancelledList = (String[]) targetvm.get(CQ_PROPERTY_INHERITANCE_CANCELLED);
				if (inheritanceCancelledList != null) {
					for (String inheritanceProp : inheritanceCancelledList) {
						if (inheritanceProp.equals(propertyName)) {
							LOGGER.debug("Executing RolloutPagePropertiesLiveAction :: isPropertyInheritanceCancelled() ends");
							return true;
						}
					}
				}
			}
			LOGGER.debug("Executing RolloutPagePropertiesLiveAction :: isPropertyInheritanceCancelled() starts");
			return false;
		}

		private static void setFormRoutingTag(Resource source,Resource target, boolean autoSave, Replicator replicatorInstance){
			LOGGER.debug("Executing RolloutPagePropertiesLiveAction :: setFormRoutingTag() starts");
			try {
				final Node targetNode = target.adaptTo(Node.class);
				final Node sourceNode = source.adaptTo(Node.class);
				Session session = null;
				Workspace workspace = null;
				final ResourceResolver resolver = target.getResourceResolver();
				String sourceNodePath = source.getPath() + CommonConstants.SLASH_STRING + FORM_ROUTING_TAG;
				String targetNodePath = target.getPath() + CommonConstants.SLASH_STRING + FORM_ROUTING_TAG;
				session = resolver.adaptTo(Session.class);
				if(null != session) {
					workspace = session.getWorkspace();
				}

				if(source != null && targetNode != null && session !=null ) {
					if (sourceNode != null && sourceNode.hasNode(FORM_ROUTING_TAG) && workspace != null) {
						if(!targetNode.hasNode(FORM_ROUTING_TAG)) {
							workspace.copy(sourceNodePath, targetNodePath);
							session.save();
							saveToRepository(autoSave, target, replicatorInstance);
						} else {
							targetNode.getNode(FORM_ROUTING_TAG).remove();
							session.save();
							workspace.copy(sourceNodePath,targetNodePath);
							session.save();
							saveToRepository(autoSave, target, replicatorInstance);
						}
					} else {
						if(targetNode.hasNode(FORM_ROUTING_TAG)) {
							targetNode.getNode(FORM_ROUTING_TAG).remove();
							session.save();
							saveToRepository(autoSave, target, replicatorInstance);
						}
					}
				}
			} catch (RepositoryException e) {
				LOGGER.error(" Exception in creating form_routing_tag :: RolloutPagePropertiesLiveAction :: setFormRoutingTag() ",e);
			}

			LOGGER.debug("Executing RolloutPagePropertiesLiveAction :: setFormRoutingTag() ends");
		}

		private static void setPermissionTabProps(Resource sourceParent,Resource target, boolean autoSave, Replicator replicatorInstance,String propertyName) {
			LOGGER.debug("Executing RolloutPagePropertiesLiveAction :: setPermissionTabCUGProps() starts");
			try {
				Session session = null;
				final Node sourceNode = sourceParent.adaptTo(Node.class);
				final ResourceResolver resolver = sourceParent.getResourceResolver();
				Workspace workspace = null;
				session = resolver.adaptTo(Session.class);
				if(session != null) {
					workspace = session.getWorkspace();
				}

				Resource targetParent = target.getParent();
				Node targetNode = null;
				String targetNodePath = StringUtils.EMPTY;
				if(targetParent != null) {
					targetNode = targetParent.adaptTo(Node.class);
					targetNodePath = targetParent.getPath();
				}
				Node srcNode = sourceParent.adaptTo(Node.class);
				String sourceNodePath = StringUtils.EMPTY;
				if(srcNode != null) {
					sourceNodePath = srcNode.getPath();
				}
				String propMixinType;
				String propNodeName;

				if(propertyName.equals(PERMISSIONS)){
					propMixinType = ACCESS_CONTROL_MIXIN_TYPE;
					propNodeName = REP_POLICY_NODE_NAME;
				} else {
					propMixinType = CUG_MIXIN_TYPE;
					propNodeName = REP_CUG_POLICY_NODE_NAME;
				}

				boolean isMixinAvailable = checkIsMixinAvailable(targetNode, propMixinType);

				if(sourceNode != null && !sourceNode.hasNode(propNodeName)){
					removePermissionTabData(sourceParent,target, autoSave, replicatorInstance, isMixinAvailable, propMixinType, propNodeName);
				} else {

					if (isMixinAvailable) {
						targetNode.removeMixin(propMixinType);
						saveToRepository(autoSave, target, replicatorInstance);
						isMixinAvailable = false;
					}

					if (!isMixinAvailable) {
						if (targetNode != null && targetNode.hasNode(propNodeName)) {
							targetNode.getNode(propNodeName).remove();
						}
						if(targetNode != null) {
							targetNode.addMixin(propMixinType);
							isMixinAvailable = true;
							saveToRepository(autoSave, target, replicatorInstance);
						}
					}
					if (isMixinAvailable && !targetNode.hasNode(propNodeName) && srcNode != null && srcNode.hasNode(propNodeName) && workspace != null
							&& StringUtils.isNotBlank(sourceNodePath) && StringUtils.isNotBlank(targetNodePath)) {
						workspace.copy(sourceNodePath + CommonConstants.SLASH_STRING + propNodeName, targetNodePath + CommonConstants.SLASH_STRING + propNodeName);
					}
					saveToRepository(autoSave, target, replicatorInstance);
				}
			} catch (PathNotFoundException e) {
				LOGGER.error(" Exception in finding rep:cugPolicy node :: RolloutPagePropertiesLiveAction :: setPermissionTabCUGProps() ",e);
			} catch (RepositoryException e) {
				LOGGER.error(" Exception in creating rep:cugPolicy node :: RolloutPagePropertiesLiveAction :: setPermissionTabCUGProps() ",e);
			}
			LOGGER.debug("Executing RolloutPagePropertiesLiveAction :: setPermissionTabCUGProps() ends");
		}

		private static void removePermissionTabData(Resource sourceParent,Resource target,boolean autoSave,Replicator replicatorInstance,boolean isMixinAvailable,String propMixinType,String propNodeName){
			try {
				final Resource targetParent = target.getParent();
				if(targetParent != null) {
					Node targetNode = targetParent.adaptTo(Node.class);
					if (isMixinAvailable && targetNode != null) {
						targetNode.removeMixin(propMixinType);
						saveToRepository(autoSave, target, replicatorInstance);
					}
					if (targetNode != null && targetNode.hasNode(propNodeName)) {
						targetNode.getNode(propNodeName).remove();
					}
					saveToRepository(autoSave, target, replicatorInstance);
				}
			} catch (RepositoryException e) {
				LOGGER.error(" Exception in creating rep:cugPolicy node :: RolloutPagePropertiesLiveAction :: setPermissionTabCUGProps() ",e);
			}
		}


		private static void setTargetNodeValue(Resource target, Resource source, String propertyName, boolean autoSave,
											   Replicator replicatorInstance,boolean isParent){
			if (target != null && target.adaptTo(Node.class) != null) {
				LOGGER.debug("Executing RolloutPagePropertiesLiveAction :: setTargetNodeValue() starts");
				Node targetNode;
				Resource targetRes = target;
				try {
					targetNode = target.adaptTo(Node.class);
					Resource targetParent = target.getParent();
					if(isParent && targetParent != null) {
						boolean isMixinAvailableSrc = false;
						boolean isMixinAvailableTarget = false;

						targetNode = targetParent.adaptTo(Node.class);
						isMixinAvailableSrc = checkIsMixinAvailable(targetNode, GRANITE_AUTHENTICATION_REQUIRED);

						final Node sourceNode = source.adaptTo(Node.class);
						isMixinAvailableTarget = checkIsMixinAvailable(sourceNode, GRANITE_AUTHENTICATION_REQUIRED);
						if (targetNode != null && sourceNode != null && CQ_AUTHENTICATION_REQUIRED.equals(propertyName) && isMixinAvailableSrc) {
							targetNode.addMixin(GRANITE_AUTHENTICATION_REQUIRED);
							targetNode.getSession().save();
							return;
						} else if (targetNode != null && sourceNode != null && CQ_AUTHENTICATION_REQUIRED.equals(propertyName) && !isMixinAvailableSrc && isMixinAvailableTarget) {
							targetNode.removeMixin(GRANITE_AUTHENTICATION_REQUIRED);
							targetNode.getSession().save();
							return;
						}
						targetRes = target.getParent();
					}
					if(targetNode != null) {
						if (CQ_LOGIN_PATH.equals(propertyName)) {
							setPropertyToTarget(source, targetNode, GRANITE_LOGIN_PATH);
						} else {
							setPropertyToTarget(source, targetNode, propertyName);
						}
					}
					if(targetRes != null) {
						saveToRepository(autoSave, targetRes, replicatorInstance);
					}
					LOGGER.info("Target node property updated: {}", propertyName);
				} catch (RepositoryException e) {
					LOGGER.error("Error in :: RolloutPagePropertiesLiveAction :: setTargetNodeValue()",e);
				}
				LOGGER.debug("Executing RolloutPagePropertiesLiveAction :: setTargetNodeValue() ends");
			}
		}

		private static boolean checkIsMixinAvailable(Node node, String mixinTypeName){
			boolean isMixinAvailable = false;
			if(node != null) {
				try {
					for (NodeType mixinNode : node.getMixinNodeTypes()) {
						if (mixinTypeName.equals(mixinNode.getName())) {
							isMixinAvailable = true;
						}
					}
				} catch (RepositoryException e) {
					LOGGER.error("Error in :: RolloutPagePropertiesLiveAction :: checkIsMixinAvailable()",e);
				}
			}
			return isMixinAvailable;
		}

		private static void setPropertyToTarget(Resource source, Node targetNode, String propertyName) throws RepositoryException {
			final ValueMap sourcevm = source.getValueMap();
			if(sourcevm.get(propertyName) != null && targetNode.hasProperty(propertyName)) {
				targetNode.getProperty(propertyName).remove();
			}
			if(sourcevm.get(propertyName) != null && sourcevm.get(propertyName).getClass() != null && sourcevm.get(propertyName).getClass().getName() != null) {
				switch (sourcevm.get(propertyName).getClass().getName()) {
					case "java.lang.String":
						targetNode.setProperty(propertyName, sourcevm.get(propertyName, String.class));
						break;
					case "[Ljava.lang.String;":
						targetNode.setProperty(propertyName, (String[]) sourcevm.get(propertyName));
						break;
					case "java.util.GregorianCalendar":
						targetNode.setProperty(propertyName, (Calendar) sourcevm.get(propertyName));
						break;
					case "java.lang.Double":
						targetNode.setProperty(propertyName, (Double) sourcevm.get(propertyName));
						break;
					case "java.lang.Long":
						targetNode.setProperty(propertyName, (Long) sourcevm.get(propertyName));
						break;
					case "java.lang.Boolean":
						targetNode.setProperty(propertyName, (Boolean) sourcevm.get(propertyName));
						break;
					default:
						targetNode.setProperty(propertyName, sourcevm.get(propertyName, String.class));
						break;
				}
			} else if(sourcevm.get(propertyName) == null && targetNode.hasProperty(propertyName)) {
				targetNode.getProperty(propertyName).remove();
			}
		}

		private static void saveToRepository(boolean autoSave, Resource target, Replicator replicatorInstance){
			Session session = null;
			final ResourceResolver resolver = target.getResourceResolver();
			session = resolver.adaptTo(Session.class);

			if (autoSave) {
				try {
					if (session != null) {
						session.save();
					}
				} catch (RepositoryException e) {
					try {
						session.refresh(true);
					} catch (RepositoryException e1) {
						LOGGER.error(" Unable to refresh the session on publish :: saveToRepository()", e1);
					}
					LOGGER.error("Save to session object failed in RolloutPagePropertiesLiveAction :: saveToRepository()", e);
				}
			}

			try {
				ReplicationStatus replicationStatus = target.adaptTo(ReplicationStatus.class);
				if(!target.getPath().contains(CommonConstants.LANGUAGE_MASTERS_NODE_NAME)) {
					if(null != replicationStatus && replicationStatus.isActivated()) {
						replicatorInstance.replicate(session, ReplicationActionType.ACTIVATE, target.getPath());
					}
				}
			} catch (ReplicationException e) {
				LOGGER.error("Unable to publish changes :: saveToRepository() ", e);
			}
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
