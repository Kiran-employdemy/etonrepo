package com.eaton.platform.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.Servlet;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.util.CommonUtil;


/***
 * This servlet is used for populating resource tags for resource list component of select accordion drop down in dialog.
 */

@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_PATHS + "/eaton/resource/list",
		})
public class ResourceListServlet extends SlingSafeMethodsServlet{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2148181491826914878L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceListServlet.class);
	private static final Pattern REGEX_PATTERN = Pattern.compile("[^a-zA-Z0-9]");
	private static final String ETN_RESOURCE_TAGS_PATH = "eaton:resources";


	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws IOException {
		LOGGER.debug("Start of ResourceListServlet - doGet().");
		final ResourceResolver resourceResolver = request.getResourceResolver();
		final Locale locale = getLocaleFromRequest(request, resourceResolver); 
		List<Resource> elementResourceList = new ArrayList<>();
		List<Tag> tagsList = new ArrayList<>();
		TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
		Tag rootTag = tagManager.resolve(ETN_RESOURCE_TAGS_PATH);
		getTags(request,tagsList,rootTag);
		List<Tag> sortedTagsList = tagsList.stream().sorted(Comparator.comparing(Tag::getTitle)).collect(Collectors.toList());
		for (Tag tag : sortedTagsList) {
			ValueMapDecorator valueMapDecorator = getResourceList(tag,locale);
			elementResourceList.add(new ValueMapResource(resourceResolver, new ResourceMetadata(),JcrConstants.NT_UNSTRUCTURED, valueMapDecorator));
		}
		DataSource dataSource = new SimpleDataSource(elementResourceList.iterator());
		request.setAttribute(DataSource.class.getName(), dataSource);
		LOGGER.debug("End of ResourceListServlet - doGet().");
	}

	private static Locale getLocaleFromRequest(final SlingHttpServletRequest request,
			final ResourceResolver resourceResolver) {
		LOGGER.debug("Start of ResourceListServlet - getLocaleFromRequest().");
		String resourcePath = CommonUtil.getContentPath(resourceResolver, CommonUtil.getRefererURL(request));
		resourcePath = StringUtils.substringBefore(resourcePath, JcrConstants.JCR_CONTENT);
		Resource currentPageRes = resourceResolver.resolve(resourcePath);
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		Page currentPage = pageManager.getContainingPage(currentPageRes);
		LOGGER.debug("End of ResourceListServlet - getLocaleFromRequest().");
		return CommonUtil.getLocaleFromPagePath(currentPage);
	}
	
	private static List<Tag> getTags(SlingHttpServletRequest request,List<Tag> tagsList,Tag tag) {
		LOGGER.debug("Start of ResourceListServlet - getTags().");
		if (null != tag) {
			Iterator<Tag> childTags = tag.listChildren();
			if(childTags.hasNext()) {
				while (childTags.hasNext()) {
					Tag childTag = childTags.next();
					getTags(request,tagsList,childTag);
				}
			}else {
				tagsList.add(tag);
			}
		}
		LOGGER.debug("End of ResourceListServlet - getTags().");
		return tagsList;
		
	}
	
	private static ValueMapDecorator getResourceList(Tag tag,Locale locale)  {
		LOGGER.debug("Start of ResourceListServlet - getResourceList().");
		final ValueMapDecorator valueMapDecorator = new ValueMapDecorator(new HashMap<String, Object>());
		if (null != tag) {
			final String groupName = tag.getTitle(locale);
			final String anchorId = getAnchorId(groupName);
			valueMapDecorator.put(CommonConstants.VALUE, anchorId);
			valueMapDecorator.put(CommonConstants.TEXT, groupName);
		}
		LOGGER.debug("End of ResourceListServlet - getResourceList().");
		return valueMapDecorator;
	}
	
	/**
	 * @param groupName
	 * @return will return the string by replacing special char with "-".
	 */
	private static String getAnchorId(String groupName) {
		LOGGER.debug("Start of ResourceListServlet - getAnchorId().");
		String anchorId = StringUtils.EMPTY;
		if (StringUtils.isNotEmpty(groupName)) {
			Matcher matcher = REGEX_PATTERN.matcher(groupName);
			anchorId = matcher.replaceAll(CommonConstants.HYPHEN).toLowerCase(Locale.ENGLISH);
		}
		LOGGER.debug("End of ResourceListServlet - getAnchorId().");
		return anchorId;
	}
}
