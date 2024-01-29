package com.eaton.platform.core.services.msm.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.msm.api.MSMNameConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

/**
 * The Class RolloutConflictUtils.
 *
 * @author Jaroslav Rassadin
 */
public final class RolloutConflictUtils {

	private RolloutConflictUtils() {

	}

	/**
	 * Builds the link.
	 *
	 * @param path
	 *            the path
	 * @param externalizer
	 *            the externalizer
	 * @param resourceResolver
	 *            the resource resolver
	 * @return the string
	 */
	public static String buildLink(final String path, final Externalizer externalizer, final ResourceResolver resourceResolver) {
		if (StringUtils.isEmpty(path)) {
			return "";
		}
		return externalizer.externalLink(resourceResolver, Externalizer.AUTHOR, path) + CommonConstants.HTML_EXTN;
	}

	/**
	 * Builds the component paths.
	 *
	 * @param componentPaths
	 *            the component paths
	 * @return the string
	 */
	public static String buildComponentPaths(final List<String> componentPaths) {
		if (CollectionUtils.isEmpty(componentPaths)) {
			return "";
		}
		return componentPaths.stream().map(p -> p.substring(p.indexOf(JcrConstants.JCR_CONTENT) + 12)).collect(Collectors.joining("; "));
	}

	/**
	 * Builds the component path.
	 *
	 * @param componentPath
	 *            the component path
	 * @return the string
	 */
	public static String buildComponentPath(final String componentPath) {
		return componentPath.substring(componentPath.indexOf(JcrConstants.JCR_CONTENT) + 12);
	}

	/**
	 * Checks for live relationship.
	 *
	 * @param resource
	 *            the resource
	 * @return true, if live relationship mixin is present
	 * @throws RepositoryException
	 *             the repository exception
	 */
	public static boolean hasLiveRelationship(final Resource resource) throws RepositoryException {
		if (resource == null) {
			return false;
		}
		final Node node = resource.adaptTo(Node.class);
		CommonUtil.adapt(resource, Node.class);
		final NodeType[] mixins = node.getMixinNodeTypes();

		if (ArrayUtils.isEmpty(mixins)) {
			return false;
		}
		for (final NodeType nodeType : mixins) {
			if (nodeType.getName().equals(MSMNameConstants.NT_LIVE_RELATIONSHIP)) {
				return true;
			}
		}
		return false;
	}
}
