dryRun = true;
setDamAuthorPropertyInAssets();
/**
 * Script to set the dam:Author value in assets of mimeType: "application/pdf".
 * @param page
 */
import javax.jcr.Node

def setDamAuthorPropertyInAssets() {
    def childFolders = []
    def queryManager = session.workspace.queryManager
    def parentPath = "/content/dam/eaton"
    def parentNode = session.getNode(parentPath)
    def rights_managed_photos_limited_permissions = "/content/dam/eaton/rights-managed-photos---limited-permissions"
    def eaton_own_or_royalty_free_purchased_images = "/content/dam/eaton/eaton-own-or-royalty-free-purchased-images"

    def childNodes = parentNode.getNodes()
    while (childNodes.hasNext()) {
        def childNode = childNodes.nextNode()
        if (childNode.isNodeType("sling:OrderedFolder") || childNode.isNodeType("sling:Folder")) {
            childFolders.add(childNode.path)
        }
    }

    for (folderName in childFolders) {
        def statement = "SELECT parent.* FROM [dam:Asset] AS parent WHERE ISDESCENDANTNODE(parent, '" + folderName + "') AND parent.[jcr:content/metadata/dc:format] = 'application/pdf'"
        def query = queryManager.createQuery(statement, "JCR-SQL2")
        def results = query.execute()

        if (!folderName.equals(rights_managed_photos_limited_permissions) && !folderName.equals(eaton_own_or_royalty_free_purchased_images)) {
            try {
                def nodes = results.getNodes()
                while (nodes.hasNext()) {
                    def node = nodes.nextNode()
                    def metadataNodePath = getMetadataNodePath(node)
                    def metadataNode = session.getNode(metadataNodePath)

                    if (!metadataNode.hasProperty("dam:Author") && metadataNode.hasProperty("author")) {
                        def author = metadataNode.getProperty("author")?.getString();
                        if (dryRun) {
                            println "PDF: " + node.path;
                        } else {
                            metadataNode.setProperty("dam:Author", author);
                            session.save()
                        }
                    } else if (!metadataNode.hasProperty("dam:Author") && !metadataNode.hasProperty("author")) {
                        if (dryRun) {
                            println "PDF: " + node.path;
                        } else {
                            metadataNode.setProperty("dam:Author", "Eaton");
                            session.save();
                        }
                    }
                }
            } catch (PathNotFoundException e) {
                println "Exception: " + e.getMessage();
            }
        }
    }
}

def getMetadataNodePath(Node node) {
    def currentNode = node
    while (currentNode && !currentNode.hasNode("jcr:content/metadata")) {
        currentNode = currentNode.getParent()
    }
    currentNode.path + "/jcr:content/metadata"
}
