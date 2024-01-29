import groovy.transform.Field

@Field final BOOLEAN_TRUE = "true"
@Field final BOOLEAN_FALSE = "false"
@Field final INITIAL_CONTENT_NODE = "initial/jcr:content"
@Field final CQ_LAST_REPLICATED = "cq:lastReplicated"
@Field final CQ_LAST_REPLICATED_BY = "cq:lastReplicatedBy"
@Field final CQ_LAST_REPLICATION_ACTION = "cq:lastReplicationAction"

def eaton_page_base_array =["/conf/eaton/settings/wcm/templates"] as String[]
def final dry_run = true
def templateCount = 0
def data =[]
eaton_page_base_array.each {
    pageBasePath ->
        getNode(pageBasePath).recurse {
            templatePath ->
                if(templatePath.hasNode(INITIAL_CONTENT_NODE)){
                    templateCount ++
                    def templateInitialPage =  templatePath.getNode(INITIAL_CONTENT_NODE)
                    def lastReplicatedFlag = BOOLEAN_FALSE;
                    def lastReplicatedByFlag = BOOLEAN_FALSE;
                    def lastReplicationActionFlag = BOOLEAN_FALSE;
                    if(templateInitialPage.hasProperty(CQ_LAST_REPLICATED)){
                        templateInitialPage.setProperty(CQ_LAST_REPLICATED,(Value)null)
                        lastReplicatedFlag = BOOLEAN_TRUE
                    }
                    if(templateInitialPage.hasProperty(CQ_LAST_REPLICATED_BY)){
                        templateInitialPage.setProperty(CQ_LAST_REPLICATED_BY,(Value)null)
                        lastReplicatedByFlag = BOOLEAN_TRUE
                    }
                    if(templateInitialPage.hasProperty(CQ_LAST_REPLICATION_ACTION)){
                        templateInitialPage.setProperty(CQ_LAST_REPLICATION_ACTION,(Value)null)
                        lastReplicationActionFlag = BOOLEAN_TRUE
                    }
                    data.add([templateInitialPage.path,lastReplicatedFlag,lastReplicatedByFlag,lastReplicationActionFlag])
                }
        }
}

if(!dry_run){
    session.save()
    println("Total Updated templates count:"+templateCount)
}else{
    println("This is dry run.")
    println("Total Template count:"+templateCount)
}


table {
    columns("Template Path",
            "cq:lastReplicated available",
            "cq:lastReplicatedBy available",
            "cq:lastReplicationAction available")
    rows(data)
}