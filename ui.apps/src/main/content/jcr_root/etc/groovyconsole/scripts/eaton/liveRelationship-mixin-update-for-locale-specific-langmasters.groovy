/* Function to create SQL-2 query */
def buildQuery(page, propertyValue) {
    def queryManager = session.workspace.queryManager;
    def statement = 'select [jcr:path] from [cq:Page] as a where not([jcr:content/jcr:mixinTypes] like \''+propertyValue+'\') and isdescendantnode(a, \''+page.path+'\')';
    queryManager.createQuery(statement, 'sql');
}
/* Function to add mixin to the node */
def addLiveRelationship(node){
    node.addMixin("cq:LiveRelationship");
}
/* Function to retrieve the passed child node and add mixin to the same  */
def addLiveRelationshipToChildNode(node, nodeName) {
   Node childNode = node.getNode(nodeName);
   addLiveRelationship(childNode); 
   println "Child Nodes of Current Page Path = " + childNode.path;
   return childNode;
}
 
/* Language Master Page Hierarchy */
final def page = getPage('/content/eaton/language-masters/cs-cz')
/* Mixin  Value that is missing is searched in the above page hierarchy */
final def query = buildQuery(page, 'cq:LiveRelationship');
final def result = query.execute()
boolean dryRun = true;


println page.title + " pages that are missing live relationship with Global Prime = " + result.nodes.size();

result.nodes.each { node ->
   nodePath = node.path;  
   println "Page Path = " + nodePath
    if(!dryRun){
        if(node.hasNode("jcr:content")){
                Node jcrContentNode = addLiveRelationshipToChildNode(node, "jcr:content");
                if(jcrContentNode.hasNode("root")){
                    Node rootNode = addLiveRelationshipToChildNode(jcrContentNode, "root");
                    if(rootNode.hasNode("responsivegrid")){
                        Node responsiveGridNode = addLiveRelationshipToChildNode(rootNode, "responsivegrid");  
                        if(responsiveGridNode.hasNode("cq:responsive")){
                            Node responsiveNode = addLiveRelationshipToChildNode(responsiveGridNode, "cq:responsive"); 
                        }
                    }
                }
            } 
            session.save();  
        }
}