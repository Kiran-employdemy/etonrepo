function getStateList(country){
     var stateList = [];
     var xhttp = new XMLHttpRequest();
     xhttp.open("GET", "/content/dam/eaton/resources/Country_state_Json_for_Eloqua_form.json", false);
     xhttp.send();
 	 var countryObj = JSON.parse(xhttp.responseText);
     for (var i=0; i< countryObj.Country.length;i++){
         if(countryObj.Country[i].OptionValue == country){
			 for (var k=0; k< countryObj.Country[i].State.length;k++){
				stateList[k] = countryObj.Country[i].State[k].OptionValue+"="+countryObj.Country[i].State[k].OptionName;
             }
     	}
	}
return stateList;
}