package com.eaton.platform.core.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class MockRepositoryUtils.
 *
 * @author Jaroslav Rassadin
 */
public final class MockRepositoryUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(MockRepositoryUtils.class);

	private static final String NODE_TYPES_RESOURCE_PATH = "com/eaton/platform/core/util/node_types.cnd";

	private MockRepositoryUtils() {

	}

	/**
	 * Register node types. To be used with ResourceResolverType.JCR_OAK mock repository.
	 *
	 * @param session
	 *            the session
	 */
	public static void registerNodeTypes(final Session session) {
		try (final InputStream inputStream = MockRepositoryUtils.class.getClassLoader().getResourceAsStream(NODE_TYPES_RESOURCE_PATH)) {
			final NodeType[] nodeTypes = CndImporter.registerNodeTypes(new InputStreamReader(inputStream), session);

			if (ArrayUtils.isNotEmpty(nodeTypes)) {
				LOGGER.debug("Registered node types: {}", Arrays.stream(nodeTypes).map(NodeType::getName).collect(Collectors.joining(",")));
			}
			session.save();

		} catch (final Exception ex) {
			LOGGER.error("An error has occurred while registering node types.", ex);
		}
	}
}
