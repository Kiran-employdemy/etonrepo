package etc.groovyconsole.scripts.eaton
//Run the script for each "region" during a down-time period in that region.
//Example:  North America, Central and South America, Western Europe, Eastern and Southern Europe, Mid-East and Africa, Asia, Pacific
//Example data below.

/*
{"propertyNames":["partnerProgrammeType", "tierLevel"],"countries": ["language-masters", "ca", "mx", "us"]}  //North America
{"propertyNames":["partnerProgrammeType", "tierLevel"],"countries": ["ar", "br", "cl", "co", "cr", "pe"]}  //Central and South America
{"propertyNames":["partnerProgrammeType", "tierLevel"],"countries": ["at", "be", "ch", "de", "dk", "ee", "es", "fi", "fr", "gb", "ie", "it", "nl", "no", "pt", "se"]}  //Western Europe
{"propertyNames":["partnerProgrammeType", "tierLevel"],"countries": ["bg", "cz", "gr", "hr", "hu", "lt", "lv", "pl", "ro", "rs", "ru", "sk", "ua"]}  //Eastern and Southern Europe
{"propertyNames":["partnerProgrammeType", "tierLevel"],"countries": ["ae", "il", "ke", "ma", "ng", "tr", "za"]}  //Mid-East and Africa
{"propertyNames":["partnerProgrammeType", "tierLevel"],"countries": ["cn", "in", "jp", "kr", "my", "sg", "th", "tw", "vn"]}  //Asia
{"propertyNames":["partnerProgrammeType", "tierLevel"],"countries": ["au", "id", "nz", "ph"]}  //Pacific
*/

def DRY_RUN = true;

def queryManager = session.workspace.queryManager;
def sqlQuery = null;
def queryObj = null;
def queryResults = null;

def propertyNames=data.propertyNames;
def countries=data.countries;

try {
	propertyNames.each { propertyName ->
		countries.eachWithIndex { countryCode, index ->
			def parentPath = '/content/eaton/'+countryCode;
			if(!session.nodeExists(parentPath)) {
				println 'Path does not exist in JCR!  Path: '+ parentPath + '.  Continuing to next country.'
			} else {
				def parentNode = session.getNode(parentPath);
				def childNodes = parentNode.getNodes();
				while (childNodes.hasNext()) {
					def childNode = childNodes.nextNode();

					// if the child node name is a language ("**-**"), proceed
					if (childNode.name ==~ /\w+(-)\w+/) {
						def pathToSearch = parentPath + '/' + childNode.name + "/secure";
						println 'Searching in: ' + pathToSearch;

						sqlQuery = 'select * from [nt:unstructured] as node where ISDESCENDANTNODE(node, "' + pathToSearch + '") AND node.' + propertyName + ' IS NOT NULL AND node.' + propertyName + ' <> "" ';
						queryObj = queryManager.createQuery(sqlQuery, 'JCR-SQL2');
						queryResults = queryObj.execute();

						count = 0;

						//Get initial number of nodes
						println 'No Of nodes with property ' + propertyName + ' found in ' + pathToSearch + ' :' + queryResults.nodes.size();

						queryResults.nodes.each { node ->
							//check if has property
							if (node.hasProperty(propertyName)) {
								node.getProperty(propertyName).remove();
								count++;
							}
							//Log the path updated
							println 'updated --' + node;
							//Save the session
							if (!DRY_RUN) {
								session.save();
							}
						}

						//Verify number of nodes updated
						println 'Number of nodes updated: ' + count;

					}//end if
				}//end while
			}//end else
		}
	}
}
catch(Exception e) {
	println "Exception :: " +e.getMessage();
}
