package etc.groovyconsole.scripts.eaton

import groovy.transform.Field
import java. util. Iterator
import com.day.cq.commons.jcr.JcrConstants

/**
 * EAT-2233 - Links on rolled out content to not rationalize.
 * This groovy script will list down all pages which has cq:LiveSyncConfig node under the mega menu which is not expected
 * and should only be present only under locales nodes say /content/us/en-us/jcr:content node only.
 * Intentionally, we are not counting pages under language-masters which we are expecting to working
 * as expected and only live copies has this issue.
 *
 * We are giving option to delete the reported nodes if the dry_run mode flag is set false
 * and delete_nodes flog is turned true.
 */

/**
 * set DRY_RUN_MODE flag is false, if you want to actually modigy the aem nodes
 */
@Field final DRY_RUN_MODE_FLAG = true

/**
 * set DELETE_REPORTED_NODES is true, if you want to actually delete the aem nodes
 */
@Field final DELETE_REPORTED_NODES_FLAG = false
@Field final EATON_CONTENT_LANGUAGE_MASTERS_PATH = "/content/eaton/language-masters"
@Field final EATON_CONTENT_PATH = "/content/eaton/"
@Field final CQ_LIVE_SYNC_CONFIG_NODE = "cq:LiveSyncConfig"
@Field final PRIMARY_SUB_CATEGORY_PROPERTY = "primarySubCategory"


PageManager pageManager = resourceResolver.adaptTo(PageManager.class)
Page contentEatonRootPage = pageManager.getPage(EATON_CONTENT_PATH)
Iterator<Page> contentEatonRootPageIterator = contentEatonRootPage.listChildren(null, false)
List<String> liveCopyCountryList = new ArrayList()
List<String> liveCopyLanguageCountryList = new ArrayList()
List<String> megaMenuWithCqLiveSyncConfigNode = new ArrayList()
@Field  primarySubCategoryChangeCount = 0

/**
 * This method is to keep the counter as per latest groovy changes
 */
def setCounter(){
    primarySubCategoryChangeCount++
}

/**
 * This is to get the list of all countries excluding language masters
 */
while(contentEatonRootPageIterator.hasNext())
{
    String path = contentEatonRootPageIterator.next().getPath()
    if( path != EATON_CONTENT_LANGUAGE_MASTERS_PATH ) {
        liveCopyCountryList.add(path)
    }
}

/**
 * This is to get the list of all locales i.e language_country excluding language masters
 */
liveCopyCountryList.each {
    languageCountyBasePath ->
        Iterator<Page> localesIterator = pageManager.getPage(languageCountyBasePath).listChildren(null, false)
        while(localesIterator.hasNext())
        {
            String path = localesIterator.next().getPath()
            liveCopyLanguageCountryList.add(path)
        }
}

/**
 * To get the list of all paths which has megamenu path with cq:LiveSyncConfig node in jcr:content
 */
liveCopyLanguageCountryList.each {
    localeMegaMenuBasePath ->
        Iterator<Page> megaMenuPageIterator = pageManager.getPage(localeMegaMenuBasePath).listChildren(null, false)
        while(megaMenuPageIterator.hasNext())
        {
            String path = megaMenuPageIterator.next().getPath()
            Resource res = resourceResolver.getResource(path.concat("/").concat(JcrConstants.JCR_CONTENT))
            if(null != res){
                Node tempNode = res.adaptTo(javax.jcr.Node)
                if(tempNode.hasNode(CQ_LIVE_SYNC_CONFIG_NODE)){
                    megaMenuWithCqLiveSyncConfigNode.add(tempNode.getPath().concat("/").concat(CQ_LIVE_SYNC_CONFIG_NODE))                }
            }
        }
}

print "Total Results Found for which megamenu nodes has cq:LiveSyncConfig: "+megaMenuWithCqLiveSyncConfigNode.size()
if(megaMenuWithCqLiveSyncConfigNode.size() > 0){
    println " \n Following nodes will be/are deleted if DRY_RUN_MODE value is false and DELETE_REPORTED_NODES is true."

}

/**
 * This method will display/delete the nodes with cq:LiveSyncConfigNode where not desired.
 */
megaMenuWithCqLiveSyncConfigNode.each {
    cqLiveSyncNode ->
        println cqLiveSyncNode
        Resource res = resourceResolver.getResource(cqLiveSyncNode)
        primarySubCategoryPropertyChange(res.getParent().getParent().getPath())
        Node tempNode = res.adaptTo(javax.jcr.Node)
        if( !DRY_RUN_MODE_FLAG && DELETE_REPORTED_NODES_FLAG ) {
            tempNode.remove()
            save()
        }
}

/**
 * This method will be responsible for changing primarySubCategory property on pages as  per
 * their countries and not language-masters
 * for the pages which has been already rolled out
 * * @param cqLiveSyncConfigNode
 * @return
 */
def primarySubCategoryPropertyChange (cqLiveSyncConfigNode){
    getNode( cqLiveSyncConfigNode ).recurse  { node ->
        Resource res = resourceResolver.getResource(node.getPath())
        if(null != res){
            Node tempNode = res.adaptTo(javax.jcr.Node)
            if(tempNode.hasProperty(PRIMARY_SUB_CATEGORY_PROPERTY) && tempNode.getProperty(PRIMARY_SUB_CATEGORY_PROPERTY).getValue().getString().startsWith(EATON_CONTENT_LANGUAGE_MASTERS_PATH) ){
                setCounter()
                if(primarySubCategoryChangeCount == 1){
                    println "\nFollowing nodes primarySubCategory property has been/ will be changed as per county from language-masters if dry_run_mode_flag value is set as false :  "
                }
                println tempNode.getPath().toString()
                if( !DRY_RUN_MODE_FLAG ) {
                    tempNode.setProperty(PRIMARY_SUB_CATEGORY_PROPERTY,tempNode.getProperty(PRIMARY_SUB_CATEGORY_PROPERTY).getValue().getString().replace("language-masters",tempNode.getPath().substring(EATON_CONTENT_LANGUAGE_MASTERS_PATH.indexOf("language-masters"),EATON_CONTENT_LANGUAGE_MASTERS_PATH.indexOf("language-masters")+2)))
                    save()
                }
            }
        }
    }
}

println "Total Count of Pages where primarySubCategory property has been changed "+primarySubCategoryChangeCount
