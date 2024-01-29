package com.eaton.platform.core.services;

import com.adobe.fd.assembler.client.OperationException;
import com.day.cq.dam.api.Asset;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

public interface AssetDownloadService {

    byte[] createZipArchiveFromAssetPaths(final List<String> assetPaths, final String mergeAssetsFileName, final boolean mergeAssetsToSinglePDF) throws IOException, ParserConfigurationException, TransformerException, OperationException;

    byte[] createZipArchiveFromAssets(final List<Asset> assets, final String mergeAssetsFileName, final boolean mergeAssetsToSinglePDF) throws IOException, ParserConfigurationException, TransformerException, OperationException;

    long getMaxAllowedDownloadPackageSize();

    byte[] createZipFileFromUrls(final List<String> documentLinks) throws IOException;

}