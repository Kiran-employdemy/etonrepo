package etc.groovyconsole.scripts.eaton

import java.text.SimpleDateFormat
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import com.day.cq.dam.api.AssetManager

path = '/home/users'
stringStartDate = "2023-12-05"
stringEndDate = "2023-12-16"
sdf = new SimpleDateFormat("yyyy-MM-dd")
startDate = sdf.parse(stringStartDate)
endDate = sdf.parse(stringEndDate)

InputStream inputStream
workbook = new XSSFWorkbook()
// Create a new sheet in the workbook
sheet = workbook.createSheet("user-details")
rowNum = 1
rowColumn = sheet.createRow(0)
createColumnValues(rowColumn)

getUsers()
//function to get all users
def getUsers() {

    def damFolderPath = "/content/dam/eaton/user-report"
    def currentTime = System.currentTimeMillis()
    def outputFileName = "lastloggedin-" + currentTime + ".xlsx"
    // Define the output file path
    def outputPath = "${damFolderPath}/${outputFileName}"

    getNode(path).recurse { node ->
        def tokenExpiryDate = ''

        if (node?.getPrimaryNodeType()?.isNodeType('rep:User')) { //top node primaryType is always rep:User
            node?.getNodes()?.eachWithIndex { childNode, index ->
                if (childNode.getName() == '.tokens') {
                    if (childNode.hasNodes()) {
                        if (startDate <= findLatestTokenExpiryDate(childNode) && findLatestTokenExpiryDate(childNode) <= endDate) tokenExpiryDate = findLatestTokenExpiryDate(childNode).format('E MMM dd HH:mm:ss z yyyy')
                    } else tokenExpiryDate = "No activity"
                }
                if (childNode.getName() == 'profile' && tokenExpiryDate) {
                    def row = sheet.createRow(rowNum++)
                    createCellValues(row, node.getProperty("rep:principalName").string, findUserName(childNode, node), tokenExpiryDate)
                }
            }

            // Create a temporary file to save the Excel data
            def tempFile = File.createTempFile("exported-data", ".xlsx")
            def fileOutputStream = new FileOutputStream(tempFile)
            workbook.write(fileOutputStream)
            fileOutputStream.close()
            // Get the AssetManager
            AssetManager assetManager = resourceResolver.adaptTo(AssetManager)
            // Create an InputStream from the temporary file
            inputStream = new FileInputStream(tempFile)
            // Create an Asset from the InputStream and store it in DAM
            def asset = assetManager.createAsset(outputPath, inputStream, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", true)
            // Delete the temporary file
            tempFile.delete()
            // Return the path to the stored Excel file
            asset.path
        }
    }
    println 'Script Execution Complete!'
    println 'Report genearated at ' + outputPath;
}

private void createColumnValues(XSSFRow rowColumn) {
    def cellIdColumn = rowColumn.createCell(0)
    def cellPathColumn = rowColumn.createCell(1)
    def cellMessageColumn = rowColumn.createCell(2)
    cellIdColumn.setCellValue("UserID")
    cellPathColumn.setCellValue("Username")
    cellMessageColumn.setCellValue("Last Logged in")
}

private void createCellValues(XSSFRow row, String userId, String userName, String tokenExpiry) {
    def cellId = row.createCell(0)
    def cellPath = row.createCell(1)
    def cellMessage = row.createCell(2)
    cellId.setCellValue(userId)
    cellPath.setCellValue(userName)
    cellMessage.setCellValue(tokenExpiry)
}

//function to find latest token expiry date
def findLatestTokenExpiryDate(childNode) {
    def maxDate = null
    childNode?.getNodes()?.each { subchild ->
        def dateProperty = subchild.getProperty('rep:token.exp')
        if (dateProperty.type == PropertyType.DATE) {
            def dateValue = dateProperty.getDate().timeInMillis - (12 * 60 * 60 * 1000)
            if (maxDate == null || dateValue > maxDate) {
                maxDate = dateValue
            }
        }
    }
    return maxDate ? new Date(maxDate) : null
}

//function to get user name
def findUserName(childNode, node) {
    if (childNode.hasProperty('givenName')) return childNode.getProperty('givenName').string
    if (childNode.hasProperty('familyName')) return childNode.getProperty('familyName').string else node.getProperty('rep:principalName').string
}