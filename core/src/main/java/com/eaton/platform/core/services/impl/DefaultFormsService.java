package com.eaton.platform.core.services.impl;

import com.adobe.aemfd.docmanager.Document;
import com.adobe.fd.assembler.client.AssemblerOptionSpec;
import com.adobe.fd.assembler.client.AssemblerResult;
import com.adobe.fd.assembler.client.OperationException;
import com.adobe.fd.assembler.service.AssemblerService;
import com.day.cq.dam.api.Asset;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.FormsService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import javax.xml.XMLConstants;

@Component(service = FormsService.class,immediate = true)
public class DefaultFormsService implements FormsService {

    private static final String DDX_NAMESPACE_URI = "http://ns.adobe.com/DDX/1.0/";
    private static final String DDX_NAMESPACE_NAME = "DDX";
    private static final String PDF_DOCUMENT_TYPE = "PDF";
    private static final String ASSEMBLED_DOCUMENTS_ATTR_NAME_RESULT = "result";
    private static final String ASSEMBLED_DOCUMENTS_ATTR_NAME_SOURCE = "source";
    private static final String ASSEMBLED_DOCUMENTS_FILE_NAME = "MergedPDF";

    @Reference
    private AdminService adminService;

    @Reference
    private AssemblerService assemblerService;

    @Override
    public Document assembleDocuments(final List<Asset> assets, final Document ddxDocument) throws OperationException {
        final AssemblerOptionSpec aoSpec = new AssemblerOptionSpec();
        aoSpec.setFailOnError(true);

        final HashMap<String, Object> mapOfDocuments = new HashMap<>();

        for (int index = 0; index < assets.size(); ++index) {
            final InputStream documentDataStream = assets.get(index).getOriginal().getStream();
            final Document doc = new Document(documentDataStream);
            mapOfDocuments.put(String.valueOf(index), doc);
        }

        final AssemblerResult assemblerResult = assemblerService.invoke(ddxDocument, mapOfDocuments, aoSpec);
        return assemblerResult.getDocuments().get(ASSEMBLED_DOCUMENTS_FILE_NAME);

    }

    @Override
    public Document assembleDocumentsFromAssets(final List<Asset> assets) throws ParserConfigurationException, TransformerException, OperationException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); 
        documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); 
        final DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
        final org.w3c.dom.Document ddx = docBuilder.newDocument();

        final Element rootElement = ddx.createElementNS(DDX_NAMESPACE_URI, DDX_NAMESPACE_NAME);
        ddx.appendChild(rootElement);
        final Element pdfResult = ddx.createElement(PDF_DOCUMENT_TYPE);
        pdfResult.setAttribute(ASSEMBLED_DOCUMENTS_ATTR_NAME_RESULT, ASSEMBLED_DOCUMENTS_FILE_NAME);
        rootElement.appendChild(pdfResult);

        for(int index = 0; index < assets.size(); ++index) {
            final Element pdfSourceElement = ddx.createElement(PDF_DOCUMENT_TYPE);
            pdfSourceElement.setAttribute(ASSEMBLED_DOCUMENTS_ATTR_NAME_SOURCE, String.valueOf(index));
            pdfResult.appendChild(pdfSourceElement);
        }

        return assembleDocuments(assets, orgW3CDocumentToAEMFDDocument(ddx));
    }

    @Override
    public  Document assembleDocumentsFromAssetPaths(final List<String> assetPaths) throws ParserConfigurationException, TransformerException, OperationException {
        Document document;
        try (ResourceResolver adminResourceResolver = adminService.getReadService()){
            document = assembleDocumentsFromAssets(CommonUtil.getAssetsFromPath(adminResourceResolver, assetPaths));
        }
       return document;
    }

    private Document orgW3CDocumentToAEMFDDocument(final org.w3c.dom.Document xmlDocument) throws TransformerException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); 
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        transformerFactory.newTransformer().transform(new DOMSource(xmlDocument), new StreamResult(byteArrayOutputStream));
        return new Document(byteArrayOutputStream.toByteArray());
    }

}
