package com.eaton.platform.core.services.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.exception.EatonSystemException;

/**
 * The class AbstractReportGenerator
 * 
 * Abstract class with methods useful for report generation.
 * 
 * @author Daniel Gruszka
 *
 */
public abstract class AbstractReportGenerator {

	/**
	 * Creates report resources in JCR tree.
	 *
	 * @param parentPath
	 *            the parent path
	 * @param name
	 *            the name
	 * @param content
	 *            the content
	 * @param mimeType
	 *            the mimeType       
	 * @param session
	 *            the session
	 * @return the string
	 * @throws RepositoryException
	 *             the repository exception
	 */
	public String createReportResource(final String parentPath, final String name, final String content, final String mimeType, final Session session) throws RepositoryException {
		final Node folderNode = JcrUtil.createPath(parentPath, JcrResourceConstants.NT_SLING_FOLDER, session);

		// if report with same name exists, delete it
		if (folderNode.hasNode(name)) {
			folderNode.getNode(name).remove();
		}
		final Node reportNode = folderNode.addNode(name, JcrConstants.NT_FILE);
		final Node reportContentNode = reportNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);

		try (InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
			final javax.jcr.ValueFactory valueFactory = session.getValueFactory();
			final javax.jcr.Binary contentValue = valueFactory.createBinary(inputStream);

			reportContentNode.setProperty(JcrConstants.JCR_MIMETYPE, mimeType);
			reportContentNode.setProperty(CommonConstants.JCR_DATA, contentValue);

		} catch (final IOException ioex) {
			throw new EatonSystemException(StringUtils.EMPTY, "Can not convert report content to input stream.", ioex);
		}
		final Calendar lastModified = Calendar.getInstance();
		reportContentNode.setProperty(CommonConstants.JCR_MODIFIED, lastModified);

		session.save();

		return reportNode.getPath();
	}
}
