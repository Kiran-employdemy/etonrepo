import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import com.day.cq.dam.api.AssetManager
InputStream inputStream
try {
    // Create a new Excel workbook
    def workbook = new XSSFWorkbook()
    // Create a new sheet in the workbook
    def sheet = workbook.createSheet("Inherit Tags")
    def sourceSearch = "/content/eaton/language-masters"
    // Define the tags you want to check for
    // Create a query engine
    def queryManager = session.workspace.queryManager
    def statement = "SELECT * FROM [cq:PageContent] AS node WHERE ISDESCENDANTNODE(node, '"+sourceSearch+"') AND (node.[jcr:mixinTypes] = 'cq:LiveRelationship')"
    def query = queryManager.createQuery(statement, "JCR-SQL2")
    def result = query.execute();
    def nodes = result.getNodes();
    def message ="Not Found "
    // Iterate through the result nodes
    def countNode = 0;
    def countNotFoundNode = 0;
    def countFoundNode = 0;
    def listFounded = "";
    // Populate the sheet with data
    def rowNum = 1
    def rowColumn = sheet.createRow(0)
    createColumnValues(rowColumn)

    while (nodes.hasNext()) {
        def row = sheet.createRow(rowNum++)
        def node  = nodes.nextNode()
        // You can perform actions on nodes that don't contain the specific tag
        def existe = node.hasProperty("partnerProgramAndTierLevel")
        // Do something with the node
        // For example, print its path
        if(existe){
            countFoundNode++
            message = "Found"
            listFounded = listFounded + "N°:"+countNode+","
        }else{
            countNotFoundNode++
        }
        countNode++;
        createCellValues(row, countNode, node, message)
        println("Page n° "+countNode+" Path : "+ node.path +" Status :" +message)

    }
    println("Total Node Founded : "+countNode)
    println("Total Node Founded With Tags : "+countFoundNode+" List : "+listFounded)
    println("Total Node Founded Without Tags  : "+countNotFoundNode)
    // Define the output file name
    // Create a Random object
    def random = new Random()

    // Generate a random integer within a specified range
    def damFolderPath = "/content/dam/output-files"
    def currentTimeMillis = System.currentTimeMillis()
    def outputFileName = "exported-data-"+ currentTimeMillis +".xlsx"

    // Define the output file path
    def outputPath = "${damFolderPath}/${outputFileName}"

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
} catch (Exception e) {
    // Handle any exceptions that occur during the process
    log.error("Error exporting data to Excel: ${e.message}", e)
    null // Return null to indicate an error
} finally {
    if(inputStream != null) {
        inputStream.close();
    }
}

private void createColumnValues(XSSFRow rowColumn) {
    def cellIdColumn = rowColumn.createCell(0)
    def cellPathColumn = rowColumn.createCell(1)
    def cellMessageColumn = rowColumn.createCell(2)
    cellIdColumn.setCellValue("N° Node")
    cellPathColumn.setCellValue("Path")
    cellMessageColumn.setCellValue("Status")
}

private void createCellValues(XSSFRow row, int i, node, String message) {
    def cellId = row.createCell(0)
    def cellPath = row.createCell(1)
    def cellMessage = row.createCell(2)
    cellId.setCellValue(i)
    cellPath.setCellValue(node.path)
    cellMessage.setCellValue(message)
}
