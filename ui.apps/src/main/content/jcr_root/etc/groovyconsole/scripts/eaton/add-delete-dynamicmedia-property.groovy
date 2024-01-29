//Run the script thrice for below 3 JSON data and below set of languages one by one.
//Make sure that replication queue is empty before script execution

/*
{
	"compName":"/landinghero","compPath": "eaton/components/content/landinghero", "deskRendationName":
	":landing-hero-desktop", "mobRendationName": ":landing-hero-mobile" ,"imageName":"landingHeroImage"
} 

{
	"compName":"/homepage_hero","compPath": "eaton/components/content/homePage-hero", "deskRendationName":
	":homepage-hero-desktop", "mobRendationName": ":homepage-hero-mobile", "imageName":"imagePath"
} 

{
	"compName":"/category_hero","compPath": "eaton/components/content/category-hero", "deskRendationName":
	":category-hero-desktop", "mobRendationName": ":category-hero-mobile" ,"imageName":"imagePath"
}
*/


//Please use above JSON data to provide input in groovy console before running script.
//You can access it like data.compName


import com.day.cq.replication.ReplicationStatus
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page



def queryManager = session.workspace.queryManager;
def sqlQuery = null;
def queryObj = null;
def queryResults = null;


def DRY_RUN = true;
def final dry_run_replication = true;

def languages=["pe","ar","co","cl","cr","th","vn"];
//def languages=["id","my","ph","zw","zm","sz","ug","tn"];

//def languages=[ "test", "tz", "tw", "sd", "kr","sl","sc"];
//def languages=["rs", "sa", "rw", "re", "qa", "om"];
//def  languages=["ne","na","mz","ma","mu","mw"]; 
//def languages=["mg", "lu", "lb", "kw", "jo", "jp"];
//def languages=["gr", "gh", "ga", "et", "eg", "dj"];
//def languages=["hr","country","cd","cg","cf","cm"];

//def languages=["bw","by","bh","ao","dz","monitoring"]; 
//def languages=["gb", "ge", "ua", "tr", "ch", "se"]; 
//def languages=["es", "za", "sk", "sg", "ru", "ro"]; 
//def languages=["pt", "pl", "no", "ng", "nz", "nl"];
//def languages=["mx","lt","lv","ke","it","il"];

//def languages=["ie","in","hu","de","fr","fi"];
//def languages=["ee","dk","cz","cn","ca","bg"];
//def languages=["br","be","at","au","us"];

def Page page =null;
def Replicator replicator = null;
def String[] arrOfNodePath=null;
def Set<String> hashPagePathSet = null;
def Set<String> hashImagePathSet = null;
def ReplicationStatus rs=null;
def int imageCount=0;
def int pageCount=0;
def String imagePath=null;

try {
	hashPagePathSet = new HashSet<String>();
	hashImagePathSet = new HashSet<String>();
	replicator = getService("com.day.cq.replication.Replicator");
	languages.eachWithIndex { langCode, index ->
		// 'langCode' is the current element value, while 'index' is the index
		/* Query to list all paths which has specific component authored - Result will be a direct component path node under "/content" */
		sqlQuery = "select [jcr:path], * from [nt:unstructured] as a where isdescendantnode(a, '/content/eaton/"+langCode+"') and [sling:resourceType] = '" + data.compPath + "'";
		queryObj = queryManager.createQuery(sqlQuery, 'sql');
		queryResults = queryObj.execute();


		queryResults.nodes.each { node ->
			String nodePath = node.path;
			println "nodePath :: "+nodePath;

			if(nodePath.contains(data.compName)){

				arrOfNodePath = nodePath.split("/jcr");
				if(!arrOfNodePath[0].contains("language-masters")){
					page=getPage(arrOfNodePath[0]);
					rs=replicator.getReplicationStatus(session,page.getPath());
					if(rs !=null && rs.isActivated()) {
						println "pagePath :: "+page.getPath();
						println("Is pagePath activated: " + rs.isActivated());
						hashPagePathSet.add(arrOfNodePath[0]);

						if(node.hasProperty(data.imageName) && node.getProperty(data.imageName).getValue().toString()!=null) {
							imagePath=node.getProperty(data.imageName).getValue().toString();
							println "imagePath :: "+imagePath;
							hashImagePathSet.add(imagePath);
						}
					}
				}

				if(node.hasProperty("mobileTrans")){
					node.getProperty("mobileTrans").remove();
				}
				if(node.hasProperty("desktopTrans")){
					node.getProperty("desktopTrans").remove();
				}
				println "Removed desktopTrans & mobileTrans Property for "+data.compName;


				node.setProperty("desktopDynam",data.deskRendationName);
				node.setProperty("mobileDynam",data.mobRendationName);


				println "Added desktopDynam & mobileDynam Property for "+data.compName;
			}
		}
	}


	for (String pagePath: hashPagePathSet) {
		try {
			pageCount++;
			if(!dry_run_replication){
				replicator.replicate(session, ReplicationActionType.ACTIVATE, pagePath);
				println "Replicated pagePath :: "+ pagePath;
			}
			if(pageCount%100==0) {
				sleep(30000);
				println "Wait 30s for page replication";
			}
		}
		catch(Exception e) {
			println "Replication failed for pagePath:: " +pagePath + "  with exception:: "+e.getMessage();
		}
	}


	for (String imagePathAttr: hashImagePathSet) {
		try {
			imageCount++;
			if(!dry_run_replication){
				activate(imagePathAttr);
				println "Replicated image path :: "+ imagePathAttr;
			}
			if(imageCount%100==0) {
				sleep(30000);
				println "Wait 30s for image replication";
			}
		}

		catch(Exception e) {
			println "Replication failed for imagePath:: " +imagePathAttr+"  with exception:: "+e.getMessage();
		}
	}
}
catch(Exception e) {
	println "Exception :: " +e.getMessage();
}


if (! DRY_RUN){
	save()
}

