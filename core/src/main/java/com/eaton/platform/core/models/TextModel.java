package com.eaton.platform.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import com.adobe.livecycle.content.repository.exception.RepositoryException;
import com.day.cq.replication.PathNotFoundException;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This bean class used in TextHelper class to store content
 * </html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */

@Model(adaptables = { Resource.class, SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TextModel {

	/** The view. */
	@Inject
	@Via("resource")
	private String view;

	/** The text. */
	@Inject
	@Named("transText")
	@Via("resource")
	private String text;

	/** The statement. */
	@Inject
	@Via("resource")
	private String statement;

	/** The topline. */
	@Inject
	@Via("resource")
	private String topline;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(TextModel.class);

	/** The toggle inner grid. */
	@Inject
	@Via("resource")
	private String toggleInnerGrid;

	@Inject
	@ScriptVariable
	private Page currentPage;

	@Inject
	@Via("resource")
	private Resource res;

	@Inject
	@Source("sling-object")
	private SlingHttpServletRequest slingRequest;

	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;

	/** The Constant FOOTER_DESC_SELECTOR. */
	private static final String FOOTER_DESC_SELECTOR = "footer-desc";

	@PostConstruct
	protected void init() {
		LOGGER.debug("TextModel :: setComponentValues() :: Start");
		// local variables
		String selector = null;
		Resource textRes = null;
		Page homePage = CommonUtil.getHomePage(currentPage);
		// get selector from header & footer passed while including linklist
		// component
		selector = slingRequest.getRequestPathInfo().getSelectorString();
		/*
		 * if selector is available, component is statically included in footer
		 * of home page template, in pages other than homepage, text component
		 * needs to be derived programmatically because text resources are not
		 * present under page resources but inherited from home page
		 */
		if (null != selector && homePage != null
				&& StringUtils.equals(selector, FOOTER_DESC_SELECTOR)) {
			textRes = resourceResolver.getResource(homePage.getPath().concat(
					"/jcr:content/root/footer/footer-text"));
		} else {
			textRes = res;
		}
		LOGGER.debug("TextModel :: setComponentValues() :: Exit");
		// set default view
		if (null != res) {
			Node currentNode = res.adaptTo(Node.class);
			if (null != currentNode) {
				try {
					if (!currentNode.hasProperty("view")) {
						currentNode.setProperty("view", "default");
						currentNode.getSession().save();
					}
				} catch (javax.jcr.RepositoryException e) {
					LOGGER.error(String.format("PathNotFoundException -",e));
				}
			}
		}
	}

	/**
	 * Gets the toggle inner grid.
	 *
	 * @return the toggle inner grid
	 */
	public String getToggleInnerGrid() {
		return toggleInnerGrid;
	}

	/**
	 * Gets the view.
	 *
	 * @return the view
	 */
	public String getView() {
		return view;
	}

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Gets the statement.
	 *
	 * @return the statement
	 */
	public String getStatement() {
		return statement;
	}

	/**
	 * Gets the topline.
	 *
	 * @return the topline
	 */
	public String getTopline() {
		return topline;
	}

}