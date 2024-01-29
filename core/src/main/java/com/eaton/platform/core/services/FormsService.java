package com.eaton.platform.core.services;

import com.adobe.aemfd.docmanager.Document;
import com.adobe.fd.assembler.client.OperationException;
import com.day.cq.dam.api.Asset;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.List;

public interface FormsService {

    public Document assembleDocuments(final List<Asset> assets, final Document ddxDocument) throws OperationException;

    public Document assembleDocumentsFromAssetPaths(final List<String> assetPaths) throws ParserConfigurationException, TransformerException, OperationException;

    public Document assembleDocumentsFromAssets(final List<Asset> assets) throws ParserConfigurationException, TransformerException, OperationException;
}
