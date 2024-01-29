package com.eaton.platform.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.webservicesupport.Configuration;
import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.ChatBean;
import com.eaton.platform.core.models.ChatConfigBean;

public class ChatConfigUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatConfigUtil.class);

	/**
	 * Instantiates a new ChatConfigUtil util.
	 */
	private ChatConfigUtil() {
		LOGGER.debug("Inside ChatConfigUtil constructor");
	}

	/**
	 * This method returns ChatConfig cloud configuration page's jcr:content
	 * resource.
	 *
	 * @param configManagerFctry
	 *            the config manager factory
	 * @param the
	 *            Resource Resolver
	 * @param pageResource
	 *            the page resource
	 * @return the Chat config resource
	 */
	public static Resource getChatConfigResource(ConfigurationManagerFactory configManagerFctry,
			ResourceResolver resolver, Resource pageResource) {
		LOGGER.debug("ChatConfigUtil :: getChatConfigResource() :: Start");
		Resource chatconfigJCRResource = null;
		// get chatConfig cloud configuration object
		Configuration configObj = CommonUtil.getCloudConfigObj(configManagerFctry, resolver, pageResource,
				CommonConstants.C_CLOUD_CONFIG_NODE_NAME);

		// if cloud config object is not null, get the details
		if (null != configObj) {
			chatconfigJCRResource = resolver.resolve(
					configObj.getPath().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_STR));
		}
		LOGGER.debug("ChatConfigUtil :: getChatConfigResource() :: Exit");
		return chatconfigJCRResource;
	}

	/**
	 * This method populates all the configuration data in the ChatConfig Bean
	 * List
	 *
	 * @param resource
	 *            resolver
	 * @return the Chat config Bean contains all the different config Beans list
	 */
	public static ChatConfigBean populateChatList(Resource chatConfigRes) {
		LOGGER.debug("ChatConfigUtil :: populateChatList() :: Start");
		List<ChatBean> chatList = new ArrayList<>();
		JsonObject chatObj = null;
		ChatBean chatBean = null;
		ChatConfigBean chatConfigBean = new ChatConfigBean();
		String[] chatConfig = null;
		Page chatConfigPage = null;
		// check if the chat Config Object is not null
		if (null != chatConfigRes) {
			try {
				// get the chat views which contain different version of the
				// configuration
				Resource res = chatConfigRes.getParent();
				if (null != res)
					chatConfigPage = res.adaptTo(Page.class);

				if (null != chatConfigPage) {
					ValueMap vmp = chatConfigPage.getProperties();
					if (null != vmp)
						chatConfig = vmp.get(CommonConstants.CHAT_VIEWS, String[].class);
				}
				// check if chat view configurations data is present/configured
				if (chatConfig != null) {
					if (LOGGER.isDebugEnabled())
						LOGGER.debug(
								"ChatConfigUtil :: populateChatList() :: ChatConfigViewLength:" + chatConfig.length);
					// loop through chat configuration list
					for (String chatStr : chatConfig) {
						chatObj = new JsonParser().parse(chatStr).getAsJsonObject();
						chatBean = new ChatBean();
						// check if values are configured and get it
						if (chatObj.has(CommonConstants.BUSINESS_DIVISION)) {
							chatBean.setBusinessdivision(chatObj.get(CommonConstants.BUSINESS_DIVISION).getAsString());
						}
						if (chatObj.has(CommonConstants.URL_TO_CHAT)) {
							chatBean.setUrltochat(chatObj.get(CommonConstants.URL_TO_CHAT).getAsString());
						}
						if (chatObj.has(CommonConstants.URL_TO_AGENT)) {
							chatBean.setUrltoagent(chatObj.get(CommonConstants.URL_TO_AGENT).getAsString());
						}
						if (chatObj.has(CommonConstants.ONLINE_CONTENT)) {
							chatBean.setOnlinecontent(chatObj.get(CommonConstants.ONLINE_CONTENT).getAsString());
						}
						if (chatObj.has(CommonConstants.OFFLINE_CONTENT)) {
							chatBean.setOfflinecontent(chatObj.get(CommonConstants.OFFLINE_CONTENT).getAsString());
						}
						if (chatObj.has(CommonConstants.ENTRY_POINT_ID)) {
							chatBean.setEntrypointid(chatObj.get(CommonConstants.ENTRY_POINT_ID).getAsString());
						}
						if (chatObj.has(CommonConstants.AEM_TAGS)) {
							chatBean.setAemtags(chatObj.getAsJsonArray(CommonConstants.AEM_TAGS));
						}
						if (chatObj.has(CommonConstants.ERR_MSG)) {
							chatBean.setErrMsgs(chatObj.get(CommonConstants.ERR_MSG).getAsString());
						}
						chatList.add(chatBean);
					}
				}
				chatConfigBean.setListChatBean(chatList);
				chatConfigBean.setNoOfChatViews(chatList.size());
				
			} catch (NullPointerException e) {
				LOGGER.error("NullPointerException occured while getting chatConfigs details", e);
			} catch (Exception e) {
				LOGGER.error("JSONException occured while getting chatConfigs details", e);
			}
			LOGGER.debug("ChatConfigUtil :: populateChatList() :: Exit");
		}
		return chatConfigBean;
	}

	/**
	 * This method compare the config page and the present page AEM Tags
	 *
	 * @param Chat
	 *            Config bean which contain from which tags will be fetched and
	 *            matched.
	 * @param String
	 *            array that contains the page AEM tags
	 * @return Chat Bean of configuration for whose AEM tags has been matched
	 * @throws JSONException
	 */
	public static ChatBean compareTags(ChatConfigBean chatConfigBean, String[] pageAemTags) {
		LOGGER.debug("ChatConfigUtil :: compareTags() :: Start");
		ChatBean chatBean = null;
		List<String> aemTags = Arrays.asList(pageAemTags);
		List<ChatBean> chatBeans = chatConfigBean != null ? chatConfigBean.getListChatBean() : new ArrayList<>();
		
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("ChatConfigUtil :: compareTags() :: PageAEMTagsSize:" + pageAemTags.length);
		for (ChatBean matchchatBean : chatBeans) {
			List<String> missingTags = new ArrayList<>();
			List<String> configTags = new ArrayList<>();
			
			JsonArray jArray = matchchatBean.getAemtags();
			if (jArray != null) {
				for (int i = 0; i < jArray.size(); i++) {
					configTags.add(jArray.get(i).getAsString());
				}
			}
			
			if (!configTags.isEmpty()) {
				for (String process : configTags) {
					if (!aemTags.contains(process)) {
						missingTags.add(process);
					}
				}

				if (missingTags.isEmpty()) {
					chatBean = matchchatBean;
					break;
				} 
			}
		}
		LOGGER.debug("ChatConfigUtil :: compareTags() :: Exit");
		return chatBean;
	}
}
