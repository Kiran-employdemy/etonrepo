findAssetsNotHavingRenditions();

/**
 * Script to find missing rendition for each Asset.
 * @param page
 */

def findAssetsNotHavingRenditions() {
   def childFolders = []
  def queryManager = session.workspace.queryManager
  def parentPath = "/content/dam/eaton"
def parentNode = session.getNode(parentPath)

def childNodes = parentNode.getNodes()
while (childNodes.hasNext()) {
    def childNode = childNodes.nextNode()
    if (childNode.isNodeType("sling:OrderedFolder") || childNode.isNodeType("sling:Folder")) {
        childFolders.add(childNode.path)
    }
}
println "Asset Path, Name, Type";
  for (folderName in childFolders) {
    def statement = "SELECT parent.* FROM [dam:AssetContent] AS parent INNER JOIN [nt:folder] AS child ON ISDESCENDANTNODE(child,parent) WHERE ISDESCENDANTNODE(parent, '" + folderName + "') "
    def query = queryManager.createQuery(statement, "JCR-SQL2")
    def results = query.execute();

    if(null != results) {
      nodes = results.getNodes();
      if(null != nodes) {
        while (nodes.hasNext()) {
          node = nodes.nextNode();
          if(node.getNode("renditions") != null){
            Node renditions = node.getNode("renditions");
            if(!renditions.hasNode("cq5dam.thumbnail.140.100.png") && !renditions.hasNode("cq5dam.thumbnail.319.319.png")
                    && !renditions.hasNode("cq5dam.thumbnail.48.48.png") && !renditions.hasNode("cq5dam.web.1280.1280.jpeg") ){
              def path = node.parent.path.replaceAll(",","#");
              def assetName = node.parent.name.replaceAll(",","#");
              def item = path +","+assetName;
              if(node.hasNode("metadata") && node.getNode("metadata").hasProperty("dc:format")){
                item  =item+","+node.getNode("metadata").getProperty("dc:format").value;
              }
              println item
            }
          }
        }
      }
    }
  }
}
