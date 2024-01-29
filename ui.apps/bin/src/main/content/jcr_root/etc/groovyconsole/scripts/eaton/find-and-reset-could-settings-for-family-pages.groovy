dryRun = true;
productFamilyTemplate = "/conf/eaton/settings/wcm/templates/product-family-page";
generateDryRunReport=true;
reportCSV = '';
findAndResetFamilyPagesHavingCouldServicesSettings();
/**
 * Script is to remove cq:couldserviceconfigs properties for all product-family pages.
 * Replicate the already activated pages and ignore Deactivated pages or Not Published pages.
 * @param page
 */
def findAndResetFamilyPagesHavingCouldServicesSettings() {
    def queryManager = session.workspace.queryManager
    def statement = "SELECT page.* FROM [cq:PageContent] AS page  WHERE ISDESCENDANTNODE(page, '/content/eaton') AND page.[cq:cloudserviceconfigs] LIKE '/etc/cloudservices/%'"
    def query = queryManager.createQuery(statement, "JCR-SQL2")
    def results = query.execute();

    if(null != results) {
        nodes = results.getNodes();
        if(null != nodes) {
            while (nodes.hasNext()) {
                node = nodes.nextNode();
                if(node.hasProperty("cq:cloudserviceconfigs") && node.getProperty("cq:template").value.toString() == productFamilyTemplate){
                    if(!dryRun){
                        node.getProperty("cq:cloudserviceconfigs").remove();
                    }
                    if(!dryRun && node.hasProperty("cq:lastReplicationAction") && node.getProperty("cq:lastReplicationAction").value.toString() == 'Activate'){
                        activate(node.getParent().path);
                        println "Cloud properties has been Removed && Activated Successfully -- >"+node.getParent().path;

                    }
                    if(generateDryRunReport){

                        reportCSV = reportCSV + node.path+","+
                                node.getParent().getParent().getParent().getParent().name+","+
                                node.getParent().getParent().getParent().getParent().getParent().name;

                        if(node.hasProperty("cq:lastReplicationAction")) {
                            reportCSV = reportCSV + "," + node.getProperty("cq:lastReplicationAction").value.toString();
                        }else{
                            reportCSV = reportCSV + "," + "Not Publihsed";
                        }
                        reportCSV  = reportCSV + "\n";
                    }

                }
            }
            if(generateDryRunReport){
                println reportCSV;
            }
        }
    }
}

if(!dryRun){
    session.save();
}