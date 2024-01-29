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
    Map<String, String> countries;
    final String ETC_ACS_COMMONS_LISTS_COUNTRIES_JCR_CONTENT_LIST = "/etc/acs-commons/lists/countries/jcr:content/list";
    // Define the path to the Excel file you want to read from the DAM
    String damPath = "/content/dam/eaton/map-config/Vanity-Redirect-URL-Final-Version-PROD-V2.xlsx"
    // Change the file extension to .xlsx
    String damPathOutPut = "/etc/acs-commons/redirect-maps/vanity/jcr:content/redirectMap.txt"

    // Get the DAM resource
    Resource damResource = resourceResolver.getResource(damPath)
    Resource damResourceOutPut = resourceResolver.getResource(damPathOutPut)
    Map<String, String> mapVanity = new HashMap()

    int columnIndexStatus = 1 // Change this to the desired column index
    int columnIndexKey = 2 // Change this to the desired column index
    int columnIndexValue = 3 // Change this to the desired column index
    int columnIndexRequireValue = 4 // Change this to the desired column index
    ModifiableValueMap modifiableValueMap = null
    Set<String> domains = new HashSet<>();

    // Add domains to the set
    domains.add("www.eaton.com");
    domains.add("eaton.com");
    String updatedContent = ""
    // Check if the resource exists and is an asset
    if (damResource != null && damResource.adaptTo(Asset) && damResourceOutPut != null) {
        Asset asset = damResource.adaptTo(Asset)

        Resource contentResource = damResourceOutPut.getChild("jcr:content")
        modifiableValueMap = contentResource.adaptTo(ModifiableValueMap.class)
        updatedContent = modifiableValueMap.get("jcr:data", String.class)
        Rendition originalRendition = asset.getOriginal()

        if (originalRendition != null && modifiableValueMap != null) {
            Resource resource = resourceResolver.getResource(ETC_ACS_COMMONS_LISTS_COUNTRIES_JCR_CONTENT_LIST);
            if (Objects.nonNull(resource)) {
                countries = new HashMap<>();
                @NotNull
                Iterable<Resource> children = resource.getChildren();
                if (children != null) {
                    for (Resource childResource : children) {
                        ValueMap valueMap = childResource.getValueMap();
                        if (valueMap != null) {
                            String title = valueMap.get("jcr:title", String.class);
                            String nodeValue = valueMap.get("value", String.class);
                            countries.put(nodeValue, title);
                        }
                    }
                }
            }

            inputStream = originalRendition.getStream()

            workbook = new XSSFWorkbook(inputStream)
            Sheet sheet = workbook.getSheetAt(0)

            for (Row row : sheet) {
                Cell cellStatus = row.getCell(columnIndexStatus)
                Cell cellKey = row.getCell(columnIndexKey)
                Cell cellValue = row.getCell(columnIndexValue)
                Cell cellMigrateValue = row.getCell(columnIndexRequireValue)

                if (cellKey != null && cellValue != null && cellStatus != null && cellMigrateValue != null) {
                    String status = getCellValueAsString(cellStatus)
                    String key = getCellValueAsString(cellKey)
                    String value = getCellValueAsString(cellValue).trim().replaceAll("\\s+", "").replace("\"", "");
                    String migrate = getCellValueAsString(cellMigrateValue).toLowerCase()


                    key = key.toLowerCase()
                    if (mapVanity.get(key) == null  &&  StringUtils.isNoneBlank(value)  && status.contains("301") && migrate != "" && migrate != null && migrate.equals("yes")) {
                        if((!startsWithAnyDomain(value, domains) && !value.startsWith("/content/")) || value.contains("/content/dam")){
                            mapVanity.put(key, value);
                            String linesToAddSimple = key + " " + value
                            updatedContent = updatedContent + "\n" + linesToAddSimple
                            println("File updated successfully with Simple vanity :" + linesToAddSimple)
                            modifiableValueMap.put("jcr:data", updatedContent)
                        }else if(startsWithAnyDomain(value, domains) || ((StringUtils.isNoneBlank(value) && value.startsWith("/content/") && !value.contains("/content/dam")))){
                            if ( countries != null && !countries.isEmpty()) {
                                for (Map.Entry<String, String> country : countries.entrySet()) {
                                    if ((mapVanity.get(key) == null || mapVanity.get(key) == "") && status.contains("301")) {
                                        String genericSource = key;
                                        String genericTarget = value;
                                        boolean existeLanguage = false;
                                        for (Map.Entry<String, String> countryLanguage : countries.entrySet()) {
                                            def containsCountryLanguage = countryLanguage.getKey() + "/" + countryLanguage.getValue();
                                            if (value.contains(containsCountryLanguage)) {
                                                genericTarget = value.replace(containsCountryLanguage, country.getKey() + "/" + country.getValue());
                                                genericSource = country.getKey() + ":" + key;
                                                existeLanguage = true;
                                            }
                                        }
                                        if (value.equals(genericTarget) && value.startsWith("/") && !existeLanguage) {
                                            genericTarget = "/" + country.getKey() + "/" + country.getValue() + value;
                                            genericSource = country.getKey() + ":" + key;
                                            println("Generate line successfully with One:" + "/" + genericTarget)

                                        }

                                        if (mapVanity.get(genericSource) == null && StringUtils.isNoneBlank(genericTarget) ) {
                                            String genericSourceUrl = replaceDomain(genericTarget);
                                            genericSourceUrl = genericSourceUrl.replaceAll("(\\.\\w+)*\\.html.*", "");
                                            resourceInternal = resourceResolver.getResource(genericSourceUrl);
                                            if(resourceInternal!=null) {
                                                mapVanity.put(genericSource, genericTarget);
                                                // Read the existing content property
                                                String linesToAdd = genericSource + " " + genericTarget
                                                countResourceInternalFound++
                                                // Append the new lines
                                                updatedContent = updatedContent + "\n" + linesToAdd
                                                println("File updated successfully with :" + linesToAdd)
                                                modifiableValueMap.put("jcr:data", updatedContent)
                                            }else{
                                                println("File updated fail with :" + genericSource + " " + genericTarget.toLowerCase())
                                                countResourceExternalFound++
                                            }

                                        }else{
                                            println("File updated already exite with genericSource :" + genericSource)
                                            println("File updated already exite with genericTarget :" + genericTarget)

                                        }
                                    }
                                }
                            }
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
private static boolean startsWithAnyDomain(String url, Set<String> domains) {
    url = url.replaceAll("^(http://|https://)", "");
    for (String domain : domains) {
        if (url.startsWith(domain)) {
            return true;
        }
    }
    return false;
}
private static String replaceDomain(String url) {
    String regex = "https?://[^/]+";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(url);

    if (matcher.find()) {
        return url.replaceFirst(regex, "/content/eaton");
    }

    return url; // Return the original URL if no match is found
}