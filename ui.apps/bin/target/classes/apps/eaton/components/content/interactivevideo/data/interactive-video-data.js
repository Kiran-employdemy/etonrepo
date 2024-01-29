"use strict";

var JSONObject = org.apache.sling.commons.json.JSONObject;

use(function() {

 var buttonlink1 = isExternalUrl(properties.get("buttonlink1", ""));
 var buttonlink2 = isExternalUrl(properties.get("buttonlink2", ""));
 var buttonlink3 = isExternalUrl(properties.get("buttonlink3", ""));
 var buttonlink4 = isExternalUrl(properties.get("buttonlink4", ""));
 var buttonlink5 = isExternalUrl(properties.get("buttonlink5", ""));
 var buttonlink6 = isExternalUrl(properties.get("buttonlink6", ""));

 var roomtype = properties.get("room", "");
 roomtype = roomtype.substring(0, 1).toUpperCase() + roomtype.substring(1);
 var imgClass = "bg-peace";
 if (roomtype === "Bedroom") {
  imgClass = "bg-energy";
 } else if (roomtype === "Livingroom") {
  imgClass = "bg-lifestyle";
 }

//data array to be used in javascript for this component interactive-video.js
    var videoJsData = new JSONObject();
    videoJsData.put("redirectLink", isExternalUrl(properties.get("redirectlink", "")));
    videoJsData.put("title1", properties.get("title1", "").trim());
    videoJsData.put("intro1", properties.get("intro1", ""));
    videoJsData.put("buttontext1", properties.get("buttontext1", ""));
    videoJsData.put("buttonlink1", buttonlink1);

    videoJsData.put("title2", properties.get("title2", ""));
    videoJsData.put("intro2", properties.get("intro2", "").trim());
    videoJsData.put("buttontext2", properties.get("buttontext2", ""));
    videoJsData.put("buttonlink2", buttonlink2);

    videoJsData.put("title3", properties.get("title3", ""));
    videoJsData.put("intro3", properties.get("intro3", ""));
    videoJsData.put("buttontext3", properties.get("buttontext3", ""));
    videoJsData.put("buttonlink3", buttonlink3);

    videoJsData.put("title4", properties.get("title4", ""));
    videoJsData.put("intro4", properties.get("intro4", ""));
    videoJsData.put("buttontext4", properties.get("buttontext4", ""));
    videoJsData.put("buttonlink4", buttonlink4);

    videoJsData.put("title5", properties.get("title5", ""));
    videoJsData.put("intro5", properties.get("intro5", ""));
    videoJsData.put("buttontext5", properties.get("buttontext5", ""));
    videoJsData.put("buttonlink5", buttonlink5);

    videoJsData.put("title6", properties.get("title6", ""));
    videoJsData.put("intro6", properties.get("intro6", ""));
    videoJsData.put("buttontext6", properties.get("buttontext6", ""));
    videoJsData.put("buttonlink6", buttonlink6);


 return {
  roomtype: roomtype,
  imgClass: imgClass,
  buttonlink1: buttonlink1,
  buttonlink2: buttonlink2,
  buttonlink3: buttonlink3,
  buttonlink4: buttonlink4,
  buttonlink5: buttonlink5,
  buttonlink6: buttonlink6,
  videoJsData :videoJsData.toString()
 };

 function isExternalUrl(url) {
  if (url.indexOf('://') > 0 || url.indexOf('//') === 0 || url.isEmpty()) {
   return url;
  } else {
   return url + ".html";
  }

 }

});