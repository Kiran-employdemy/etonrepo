package com.eaton.platform.core.servlets;
  
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;

import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.servlets.config.AttributeIdJsonGeneratorServletConfig;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.json.simple.JSONValue;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.asset.api.Asset;
import com.adobe.granite.asset.api.AssetManager;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;


@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_POST,
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/attributeIdGenerator",
        })
@Designate(ocd = AttributeIdJsonGeneratorServletConfig.class)
public class AttributeIdJsonGeneratorServlet extends SlingAllMethodsServlet{
private static final long serialVersionUID = 2598426539166789515L;

 public static final Logger LOGGER = LoggerFactory.getLogger(AttributeIdJsonGeneratorServlet.class);


 private static final String EXCEL_FILE_MIMETYPE ="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
 private static final String JSON_FILE_MIMETYPE = "application/json";
 
public static final String DEFAULT_JSON_ROOT_ELEMENT = "AttributeIds";
private static final String DEFAULT_JSON_FILENAME = "AttributeIds";
 
public static final String DEFAULT_EXCEL_FILE_UPLOAD_PATH ="/content/dam/eaton/resources/attribute/excel/";
public static final String DEFAULT_EXCEL_FILE_ARCHIVE_PATH ="/content/dam/eaton/resources/attribute/excel/archive/";
public static final String DEFAULT_JSON_FILE_UPLOAD_PATH ="/content/dam/eaton/resources/attribute/json/";
public static final String DEFAULT_JSON_FILE_ARCHIVE_PATH ="/content/dam/eaton/resources/attribute/json/archive/";

private String damExcelUploadPath;
private String damExcelArchivePath;
private String damJsonUploadPath;
private String damJsonArchivePath;
private String jsonRootElementName;
 
 private Session session;
   
 @Reference
 AdminService adminService;

 @Activate
 protected void activate(final AttributeIdJsonGeneratorServletConfig config) {
	 damExcelUploadPath = config.excel_file_path();
	 damExcelArchivePath = config.excel_file_archive_path();
	 damJsonUploadPath = config.json_file_path();
	 damJsonArchivePath = config.json_file_archive_path();
	 jsonRootElementName = config.json_root_element();
 }
    
 @Override
 protected void doPost(SlingHttpServletRequest slingRequest, SlingHttpServletResponse response) throws IOException {  
      try
         {
         final boolean isMultipart = ServletFileUpload.isMultipartContent(slingRequest);
         PrintWriter out = null;
           
           out = response.getWriter();
           if (isMultipart) {
             final Map<String, RequestParameter[]> params = slingRequest.getRequestParameterMap();
             for (final Entry<String,RequestParameter[]> pairs : params.entrySet()) {
               final RequestParameter[] pArr = pairs.getValue();
               final RequestParameter param = pArr[0];
               final InputStream stream = param.getInputStream();
                String fileName =param.getFileName();
                
                 /*Save the uploaded file into the Adobe CQ DAM*/
                
            	 if(null!=fileName && (fileName.endsWith(".xlsx")||fileName.endsWith(".xls"))){
                 writeToDam(out,slingRequest,stream,damExcelUploadPath,damExcelArchivePath,fileName,EXCEL_FILE_MIMETYPE);
                 
                 /*Generate the json data from excel*/
                 
                 excelToJson( out,damExcelUploadPath,param.getFileName(),slingRequest);
                 }else{out.println("only excel format allow");}
               
             }
           }
          session.save();       
         }catch (RepositoryException e) {
             LOGGER.error(e.getMessage());
         }finally{
        	 if(session!=null){
        	session.logout(); 
        	 }
        	 
            }
       
     }
       
     
/**
 * @param out
 * @param slingRequest
 * @param is
 * @param fileUploadPath
 * @param fileArchivePath
 * @param fileName
 * @param mimeType
 * @return
 * @throws RepositoryException
 * 
 * Method contain the logic to Save the uploaded file into the AEM DAM using AssetManager APIs
 */
private File writeToDam(PrintWriter out,SlingHttpServletRequest slingRequest,InputStream is,String fileUploadPath,String fileArchivePath, String fileName,String mimeType) throws RepositoryException
{
   
    ResourceResolver resourceResolver = slingRequest.getResourceResolver();
    session = resourceResolver.adaptTo(Session.class);
    Node uploadFilePathNode = JcrUtils.getOrCreateByPath(fileUploadPath,JcrResourceConstants.NT_SLING_FOLDER,JcrResourceConstants.NT_SLING_ORDERED_FOLDER, session, true);
    String newFilePath = uploadFilePathNode.getPath()+StringUtils.join("/")+fileName; 
    //Use AssetManager to place the file into the AEM DAM
    AssetManager graniteAssetMgr = resourceResolver.adaptTo(AssetManager.class);
    if(graniteAssetMgr!=null){
    if(graniteAssetMgr.assetExists(newFilePath)){
    	out.println(CommonUtil.archiveDAMAsset(session,graniteAssetMgr,fileUploadPath,fileArchivePath,fileName));
    }
    }else{
    	throw new IllegalArgumentException("Cannot adapt to Granite AssetManager");
    }
    com.day.cq.dam.api.AssetManager cqAssetMgr = resourceResolver.adaptTo(com.day.cq.dam.api.AssetManager.class);
    if(cqAssetMgr!=null){
    cqAssetMgr.createAsset(newFilePath, is,mimeType,true);
    }else{
    	throw new IllegalArgumentException("Cannot adapt to Day AssetManager");	
    }
    out.println("Uploaded file placed here: " + fileUploadPath);
    return new File(newFilePath); 


}

	
	
/**
 * @param out
 * @param filePath
 * @param fileName
 * @param slingRequest
 * 
 * Method to parse excel file and generate json object.
 */
@SuppressWarnings("deprecation")
private void excelToJson(PrintWriter out,String filePath,String fileName,SlingHttpServletRequest slingRequest){
	XSSFWorkbook workbook=null;
	JsonObject json = new JsonObject();
	String excelFilePath =filePath.concat(fileName);
	 List<String> attributeIdList = new ArrayList<>();

	try(InputStream in=(slingRequest.getResourceResolver().getResource(excelFilePath).adaptTo(Asset.class).getRendition("original")).getStream()){
	workbook= new XSSFWorkbook(in);
	Sheet sheet = workbook.getSheetAt(0);
	            for (int i=1;i<sheet.getPhysicalNumberOfRows();i++){
	            	Row row = sheet.getRow(i);
                	if(null!=row){
	            	for (int j=0;j<row.getPhysicalNumberOfCells();j++){
	            		Cell cell = row.getCell(j);
	            		if(cell!=null && cell.getCellType()!=CellType.BLANK){	            				            				            		
	            		attributeIdList.add(formatValueInString(row,j));			            	
	            		}
	            		
	            	}
	            }           	            
	           }
	            json.add(jsonRootElementName,new Gson().toJsonTree(attributeIdList));
		        String jsonString=  JSONValue.toJSONString(json);
		        InputStream is2 = new ByteArrayInputStream(jsonString.getBytes());
 	            writeToDam(out, slingRequest, is2, damJsonUploadPath,damJsonArchivePath,DEFAULT_JSON_FILENAME+".json",JSON_FILE_MIMETYPE);
	}catch(Exception ex){
		out.println("Error occurred while generating json data. Please check error logs for more detail" + ex.getMessage() );
	}
	
} 



/**
 * @param sheet
 * @param index
 * @return Method return read and return column title
 * 
 */
private String getHeaderCellTitle(Sheet sheet,int index){
	Row headerRow = sheet.getRow(0);
	 return headerRow.getCell(index).getStringCellValue();
}


/**
 * @param row
 * @param cellIndex
 * @return
 * Method to reformat catalog alphanumeric value into string.
 */
public String formatValueInString(Row row,int cellIndex){
	String value =new DataFormatter().formatCellValue(row.getCell(cellIndex));
	if(!value.isEmpty() &&""!=value){
		return value;	
	}
	return null;
}

}