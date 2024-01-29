import com.day.cq.wcm.api.components.ComponentManager
import com.day.cq.wcm.commons.ReferenceSearch



def componentManager = resourceResolver.adaptTo(ComponentManager)
def validResourceTypes = componentManager.components *.resourceType;
def Replicator replicator = getService("com.day.cq.replication.Replicator");
def sites = ["/content/eaton/"]

def final dry_run_replication = false;

/**
* This script finds the pages which have eloqua form component and publish the pages.
* Why we need to run this?
* if eloqua server are down and we make configuration change the pages needs to be published.
*/

sites.eachWithIndex {
  value,
  key ->
  getPage(value).recurse {
    page ->
      def content = page.node
    content?.recurse {
      node ->
        def template = node.get("sling:resourceType");
      def path = node.path;
      def lastReplicationAction = node.get("cq:lastReplicationAction");
      def cloudServices = content.get("cq:cloudserviceconfigs");
      if (template && lastReplicationAction && cloudServices &&
        "eaton/components/content/eloqua-form" == template && lastReplicationAction == "Activate" && cloudServices.size > 0) {
        if (!dry_run_replication && !node.path.contains("language-masters")) {
          for (def i = 0; i < cloudServices.size; i++) {
            if (cloudServices[0].contains("eloqua-configuration")) {
              replicator.replicate(session, ReplicationActionType.ACTIVATE, cloudServices[0])
			   println "replicated successful cloudServices" + cloudServices[0];
            }
          }
          replicator.replicate(session, ReplicationActionType.ACTIVATE, content.parent.path)
          println "replicated successful -> " + content.parent.path;
        } else {
          println "Not Replicated -> Turn the flag to replicate the resource OR the path contains language-master --> " + content.parent.path;
        }
      }
    }
  }
}