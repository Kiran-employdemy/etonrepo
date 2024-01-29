package com.eaton.platform.core.services.impl;

import com.adobe.aemfd.docmanager.Document;
import com.adobe.fd.assembler.client.OperationException;
import com.day.cq.dam.api.Asset;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.AssetDownloadService;
import com.eaton.platform.core.services.FormsService;
import com.eaton.platform.core.services.config.AssetDownloadServiceConfig;
import com.eaton.platform.core.servlets.assets.AssetDownloadValidator;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = AssetDownloadService.class,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "DefaultAssetDownloadService",
                AEMConstants.PROCESS_LABEL + "DefaultAssetDownloadService"
        })
@Designate(ocd = AssetDownloadServiceConfig.class)
public class DefaultAssetDownloadService implements AssetDownloadService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAssetDownloadService.class);

    private AssetDownloadServiceConfig assetDownloadServiceConfig;

    @Activate
    @Modified
    protected final void activate(AssetDownloadServiceConfig config) {
        assetDownloadServiceConfig = config;
    }

    @Reference
    private AdminService adminService;

    @Reference
    private FormsService formsService;

    @Override
    public byte[] createZipArchiveFromAssetPaths(final List<String> assetPaths, final String mergeAssetsFileName, final boolean mergeAssetsToSinglePDF) throws IOException, ParserConfigurationException, TransformerException, OperationException {
        List<Asset> assets;
        byte[] zipArchiveFromAssets;
        try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
            assets = CommonUtil.getAssetsFromPath(adminResourceResolver, assetPaths);
            AssetDownloadValidator.validateMinimum(assets);
            AssetDownloadValidator.validateMaxDownloadPackageSizeLimit(assets, assetDownloadServiceConfig.getMaxAllowedDownloadPackageSize());
            zipArchiveFromAssets = createZipArchiveFromAssets(assets, mergeAssetsFileName, mergeAssetsToSinglePDF);
        }
        return zipArchiveFromAssets;
    }

    @Override
    public byte[] createZipArchiveFromAssets(final List<Asset> assets, final String mergeAssetsFileName, final boolean mergeAssetsToSinglePDF) throws IOException, ParserConfigurationException, TransformerException, OperationException {
        return createZipArchive(assets, mergeAssetsFileName, mergeAssetsToSinglePDF);
    }

    @Override
    public byte[] createZipFileFromUrls(final List<String> documentLinks) throws IOException {
        LOGGER.debug("Started DefaultAssetDownloadService.createZipFileFromUrls");
        ByteArrayOutputStream byteArrayOutputStream = null;
        ZipOutputStream zipOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
            for (String documentLink : documentLinks) {
                downloadSingleFile(zipOutputStream, documentLink);
            }
        } catch (IOException e) {
            LOGGER.error("DefaultAssetDownloadService.createZipFileFromUrls", e);
        } finally {
        	if(null!=zipOutputStream) {
        		zipOutputStream.flush();
                zipOutputStream.close();
        	}
        	if(null!=byteArrayOutputStream) {
        		byteArrayOutputStream.flush();
        		byteArrayOutputStream.close();
        	}
        }
        LOGGER.debug("Finished DefaultAssetDownloadService.createZipFileFromUrls");

        return byteArrayOutputStream.toByteArray();
    }

    private byte[] createZipArchive(final List<Asset> assets, final String mergeAssetsFileName, final boolean mergeAssetsToSinglePDF) throws IOException, ParserConfigurationException, TransformerException, OperationException  {

        ByteArrayOutputStream byteArrayOutputStream = null;
        ZipOutputStream zipOutputStream = null;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
            if(CollectionUtils.isNotEmpty(assets)) {
                if(mergeAssetsToSinglePDF) {
                    final Document mergePDFDocument = formsService.assembleDocumentsFromAssets(assets);
                    writeToZipArchive(zipOutputStream, mergeAssetsFileName, new BufferedInputStream(mergePDFDocument.getInputStream()));
                } else {
                    for (int index = 0; index < assets.size(); index++) {
                        downloadSingleAsset(zipOutputStream, assets.get(index), index);
                    }
                }
            }

        } catch (OperationException e) {
            LOGGER.error("DefaultAssetDownloadService.createZipArchive", e);
        } finally {
        	if(null!=zipOutputStream) {
        		zipOutputStream.flush();
                zipOutputStream.close();
        	}
        	if(null!=byteArrayOutputStream) {
        		byteArrayOutputStream.flush();
        		byteArrayOutputStream.close();
        	}
        }

        return byteArrayOutputStream.toByteArray();
    }

    private void downloadSingleFile(final ZipOutputStream zipOutputStream, final String url) throws IOException {
        LOGGER.debug("DefaultAssetDownloadService.downloadSingleFile: Downloading single file: " + url);
        final HttpPost httpPost = new HttpPost(url);
        try {
            final PoolingHttpClientConnectionManager conMgr = new PoolingHttpClientConnectionManager();
            final HttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(conMgr)
                    .setRedirectStrategy(new LaxRedirectStrategy())
                    .setConnectionTimeToLive(assetDownloadServiceConfig.getDownloadFileTimeout(), TimeUnit.SECONDS)
                    .build();
            final HttpResponse response = httpClient.execute(httpPost);
            LOGGER.debug("DefaultAssetDownloadService.downloadSingleFile httpPost executed and response received.");
            final int status = response.getStatusLine().getStatusCode();

            if (status >= CommonConstants.HTTP_SUCCESS_MIN && status <= CommonConstants.HTTP_SUCCESS_MAX) {
                final String fileName = FilenameUtils.getName(httpPost.getURI().getPath());
                LOGGER.debug("DefaultAssetDownloadService.downloadSingleFile: 200 Response received. Writing single file to zip: " + fileName);
                final InputStream in = response.getEntity().getContent();
                writeToZipArchive(zipOutputStream, fileName, in);
                in.close();
                LOGGER.debug("DefaultAssetDownloadService.downloadSingleFile: InputStream for response closed.");
            } else {
                EntityUtils.consumeQuietly(response.getEntity());
                LOGGER.info("Status " + status + " for HTTP response when downloading " + url);
            }
        } finally {
            httpPost.releaseConnection();
            LOGGER.debug("DefaultAssetDownloadService.downloadSingleFile: httpPost connection released.");
        }
    }

    private void downloadSingleAsset(final ZipOutputStream zipOutputStream, final Asset asset, final int index) throws IOException {
        String count = String.valueOf((index + 1));
        if(count.length() < 2){
            count = "0" + count;
        }
        String filePrefix = count+assetDownloadServiceConfig .getPrefix();
        writeToZipArchive(zipOutputStream,filePrefix+asset.getName(), asset.getOriginal().getStream());
    }

    private void writeToZipArchive(final ZipOutputStream zipOutputStream, final String zipEntryName, final InputStream inputStream) throws IOException {
        zipOutputStream.putNextEntry(new ZipEntry(zipEntryName));
        byte[] contentBytes = IOUtils.toByteArray(inputStream);
        zipOutputStream.write(contentBytes);
        IOUtils.closeQuietly(inputStream);
        zipOutputStream.closeEntry();
    }

    public long getMaxAllowedDownloadPackageSize() {
        return assetDownloadServiceConfig.getMaxAllowedDownloadPackageSize();
    }
}