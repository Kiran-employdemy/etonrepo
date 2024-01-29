package com.eaton.platform.core.servlets.assets;

import com.day.cq.dam.api.jobs.AssetDownloadService;
import com.eaton.platform.core.constants.AssetDownloadConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AssetZipDownloadServlet responds wit a zip file including the assets listed in the provided json as payload in the POST request
 * a Json with the list of assets in the body of the request
 */
@Component(service = Servlet.class,
		immediate = true,
		property = {
				Constants.SERVICE_DESCRIPTION + "=Asset Zip Download",
				ServletConstants.SLING_SERVLET_PATHS + "/eaton/content/assetzipdownload",
				ServletConstants.SLING_SERVLET_EXTENSION_ZIP,
				ServletConstants.SLING_SERVLET_METHODS_POST
		})
public class AssetZipDownloadServlet extends SlingAllMethodsServlet {
	private static final Logger LOG = LoggerFactory.getLogger(AssetZipDownloadServlet.class);

	@Reference
	transient AssetDownloadService assetDownloadService;

	/**
	 *  {@inheritDoc}
	 */
	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException {
		LOG.debug("Started AssetZipDownloadServlet.doPost");
		response.setContentType(AssetDownloadConstants.ZIP_CONTENT_TYPE);
		try (OutputStream outputStream = response.getOutputStream()) {
			AssetZipDownloadInput assetZipDownloadInput = new Gson().fromJson(new InputStreamReader(request.getInputStream()), AssetZipDownloadInput.class);
			List<String> documentLinks = assetZipDownloadInput.getDocumentLinks();
			if (documentLinks.isEmpty()) {
				return;
			}
			ResourceResolver resourceResolver = request.getResourceResolver();
			Set<Resource> assetSet = documentLinks.stream().map(resourceResolver::getResource).collect(Collectors.toSet());
			Resource resource = (Resource) assetSet.toArray()[0];
			AssetDownloadService.AssetDownloadParams params = getAssetDownloadParams(outputStream, assetSet, resource);
			String damAssets = assetDownloadService.assetDownload(params);
			String zipBytes = Base64.getEncoder().encodeToString(damAssets.getBytes(StandardCharsets.UTF_8));
			outputStream.write(zipBytes.getBytes());
		} catch ( IOException e) {
			LOG.error("IOException while trying to get download file: {}", e.getMessage());
		} catch (Exception e) {
			LOG.error("Exception while trying to get download file: {}", e.getMessage());
		}
	}

	private static AssetDownloadService.AssetDownloadParams getAssetDownloadParams(OutputStream outputStream, Set<Resource> assetSet, Resource resource) {
		return new AssetDownloadService.AssetDownloadParams(
				resource,
				assetSet,
				true,
				false,
				false,
				null,
				null,
				null,
				null,
				true,
				null,
				outputStream
		);
	}

}