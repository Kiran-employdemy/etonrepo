import com.day.cq.wcm.api.components.ComponentManager

def componentManager = resourceResolver.adaptTo(ComponentManager)

def validResourceTypes = componentManager.components*.resourceType;

def Replicator replicator = getService("com.day.cq.replication.Replicator");

def sites = ["/content/eaton"]

def final dry_run_node = true;

def final dry_run_replication = true;

/**
 * This script finds mega-menu header reference page and creates 'datalayer' node under 'headerReference' node.
 * Why we need to create this node ?
 * dataLayer component is an embedded component under base template, which means JCR node won't be created by default. ESI calls are failing, if components doesn't
 * have JCR node created.
 */
sites.eachWithIndex { value, key ->
    getPage(value).recurse { page ->
        def content = page.node
        content?.recurse { node ->
            def template = node.get("cq:template");
            def componentType = node.get("component-type");
            def lastReplicationAction = node.get("cq:lastReplicationAction");
            if (template && componentType && lastReplicationAction &&
                    "/apps/eaton/templates/eaton-reference-template" == template
                    && componentType == "header" && lastReplicationAction == "Activate" ) {
                if(node.hasNode("headerReference")){
                    Node headerReferenceNode =  node.getNode("headerReference");
                    if(headerReferenceNode != null && !headerReferenceNode.hasNode("securedatalayer")){
                        Node dataLayerNode = headerReferenceNode.addNode("securedatalayer");
                        dataLayerNode.setProperty("sling:resourceType", "eaton/components/structure/securedatalayer");
                        if(!dry_run_replication && !node.path.contains("language-masters")){
                            replicator.replicate(session, ReplicationActionType.ACTIVATE, dataLayerNode.path)
                            println "Data Layer Node created and replicated successful -> " + dataLayerNode.path;
                        }else{
                            println "Not Replicated -> Turn the flag to replicate the resource OR the path contains language-master --> "+ dataLayerNode.path
                        }
                    }else{
                        println "DataLayer Node Already Exist "+ headerReferenceNode.path;
                    }
                }
            }

        }
    }
}


if(!dry_run_node){
    session.save();
}
