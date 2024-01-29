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
import com.eaton.platform.core.models.NanoRepChatBean;
import com.eaton.platform.core.models.NanoRepChatConfigBean;

public class NanoRepChatConfigUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(NanoRepChatConfigUtil.class);

	/**
	 * Instantiates a new ChatConfigUtil util.
	 */
	private NanoRepChatConfigUtil() {
		LOGGER.debug("Inside NanoRepChatConfigUtil constructor");
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
		LOGGER.debug("NanoRepChatConfigUtil :: getChatConfigResource() :: Start");
		Resource chatconfigJCRResource = null;
		// get chatConfig cloud configuration object
		Configuration configObj = CommonUtil.getCloudConfigObj(configManagerFctry, resolver, pageResource,
				CommonConstants.NR_CLOUD_CONFIG_NODE_NAME);

		// if cloud config object is not null, get the details
		if (null != configObj) {
			chatconfigJCRResource = resolver.resolve(
					configObj.getPath().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_STR));
		}
		LOGGER.debug("NanoRepChatConfigUtil :: getChatConfigResource() :: Exit");
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
	public static NanoRepChatConfigBean populateChatList(Resource chatConfigRes) {
		LOGGER.debug("NanoRepChatConfigUtil :: populateChatList() :: Start");

		List<NanoRepChatBean> chatList = new ArrayList<>();
		JsonObject chatObj = null;
		NanoRepChatBean chatBean = null;
		NanoRepChatConfigBean chatConfigBean = new NanoRepChatConfigBean();

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
						chatConfig = vmp.get(CommonConstants.NANOREP_CHAT_VIEWS, String[].class);
				}
				// check if chat view configurations data is present/configured
				if (chatConfig != null) {
					if (LOGGER.isDebugEnabled())
						LOGGER.debug("NanoRepChatConfigUtil :: populateChatList() :: ChatConfigViewLength:"
								+ chatConfig.length);

					// loop through chat configuration list
					for (String chatStr : chatConfig) {
						chatObj = new JsonParser().parse(chatStr).getAsJsonObject();

						chatBean = new NanoRepChatBean();
						// check if values are configured and get it
						if (chatObj.has(CommonConstants.NANOREP_BUSINESS_DIVISION)) {
							chatBean.setBusinessdivision(chatObj.get(CommonConstants.NANOREP_BUSINESS_DIVISION).getAsString());
						}
						
						if (chatObj.has(CommonConstants.NANOREP_KB_NUMBER)) {
							chatBean.setKbNumber(chatObj.get(CommonConstants.NANOREP_KB_NUMBER).getAsString());
						}
						if (chatObj.has(CommonConstants.NANOREP_AEM_TAGS)) {
							chatBean.setAemtags(chatObj.getAsJsonArray(CommonConstants.NANOREP_AEM_TAGS));
						}
						if (chatObj.has(CommonConstants.NANOREP_ERR_MSG)) {
							chatBean.setErrmsgs(chatObj.get(CommonConstants.NANOREP_ERR_MSG).getAsString());
						}

						chatList.add(chatBean);
					}

				}
				chatConfigBean.setListChatBean(chatList);
				chatConfigBean.setNoOfChatViews(chatList.size());
				return chatConfigBean;
			} catch (Exception e) {
				LOGGER.error("JSONException occured while getting chatConfigs details", e);
			}
			LOGGER.debug("NanoRepChatConfigUtil :: populateChatList() :: Exit");
		}
		return null;
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
	public static NanoRepChatBean compareTags(NanoRepChatConfigBean chatConfigBean, String[] pageAemTags)
		 {
		LOGGER.debug("NanoRepChatConfigUtil :: compareTags() :: Start");

		NanoRepChatBean chatBean = null;
		if(null != chatConfigBean && null != pageAemTags) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("NanoRepChatConfigUtil :: compareTags() :: PageAEMTagsSize:" + pageAemTags.length);
			}
			List<String> aemTags = Arrays.asList(pageAemTags);
			for (NanoRepChatBean matchchatBean : chatConfigBean.getListChatBean()) {
				ArrayList<String> missingTags = new ArrayList<>();
				ArrayList<String> configTags = new ArrayList<>();


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
		}
		LOGGER.debug("NanoRepChatConfigUtil :: compareTags() :: Exit");
		return chatBean;
		
	}

}
