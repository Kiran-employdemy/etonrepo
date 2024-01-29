package etc.groovyconsole.scripts.eaton

import com.day.cq.dam.api.Asset
import com.day.cq.dam.api.Rendition
import com.drew.lang.annotations.NotNull
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.sling.api.resource.ModifiableValueMap
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ValueMap

import java.util.regex.Matcher
import java.util.regex.Pattern

Workbook workbook = null
InputStream inputStream = null
int countResourceInternalFound = 0
int countResourceExternalFound = 0
try {
    String damPath = "/content/dam/eaton/map-config/WCM_Eaton_Com_Sub_Domain_Vanity_URLs-V4.0.xlsx"
    // Change the file extension to .xlsx
    String damPathOutPut = "/etc/acs-commons/redirect-maps/vanity-subdomains/jcr:content/redirectMap.txt"

    // Get the DAM resource
    Resource damResource = resourceResolver.getResource(damPath)
    Resource damResourceOutPut = resourceResolver.getResource(damPathOutPut)
    Map<String, String> mapVanity = new HashMap()

    int columnIndexStatus = 1 // Change this to the desired column index
    int columnIndexKey = 2 // Change this to the desired column index
    int columnIndexValue = 3 // Change this to the desired column index
    int columnIndexRequireValue = 4 // Change this to the desired column index
    ModifiableValueMap modifiableValueMap = null
    String updatedContent = ""
    // Check if the resource exists and is an asset
    if (damResource != null && damResource.adaptTo(Asset) && damResourceOutPut != null) {
        Asset asset = damResource.adaptTo(Asset)
        Resource contentResource = damResourceOutPut.getChild("jcr:content")
        modifiableValueMap = contentResource.adaptTo(ModifiableValueMap.class)
        updatedContent = modifiableValueMap.get("jcr:data", String.class)
        Rendition originalRendition = asset.getOriginal()

        if (originalRendition != null && modifiableValueMap != null) {
            inputStream = originalRendition.getStream()
            workbook = new XSSFWorkbook(inputStream)
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i)

                for (Row row : sheet) {
                    Cell cellStatus = row.getCell(columnIndexStatus)
                    Cell cellKey = row.getCell(columnIndexKey)
                    Cell cellValue = row.getCell(columnIndexValue)

                    if (cellKey != null && cellValue != null && cellStatus != null ) {
                        String status = getCellValueAsString(cellStatus)
                        String key = getCellValueAsString(cellKey)
                        String value = getCellValueAsString(cellValue).trim().replaceAll("\\s+", "").replace("\"", "");
                        key = key.toLowerCase()
                        if (mapVanity.get(key) == null  &&  StringUtils.isNoneBlank(value)  && status.contains("301") ) {
                                mapVanity.put(key, value);
                                String linesToAddSimple = key + " " + value
                                updatedContent = updatedContent + "\n" + linesToAddSimple
                                println("File updated successfully with Simple vanity :" + linesToAddSimple)
                                modifiableValueMap.put("jcr:data", updatedContent)
                        }
                    }
                }
            }
        }
    } else {
        println("The DAM resource does not exist or is not an asset.")
    }
} catch (Exception e) {
    println("Error: " + e.getMessage())
}
finally {
    if (inputStream != null) {
        inputStream.close()
    }
    if (workbook != null) {
        workbook.close()
    }
    if (resourceResolver.hasChanges()) {
        resourceResolver.commit()
    }
    println("Number of Internal Resource Existe for :" + countResourceInternalFound )
    println("Number of Ignored Internal Resource 404 for :" + countResourceExternalFound )
}
// Define the method to convert cell values to strings
String getCellValueAsString(Cell cell) {
    if (cell != null) {
        cell.setCellType(CellType.STRING)
        return cell.getStringCellValue()
    }
    return ""
}
