package com.eaton.platform.integration.qr.bean;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.lang.reflect.Field;

@JsonSerialize(include=JsonSerialize.Inclusion.ALWAYS)
@JsonPropertyOrder({
"serialNumber",
"catalogNumber",
"authCode",
"ip4Address",
"ip6Address",
"geolocationCountry",
"geolocationCity",
"gpsLocation",
"mobileLocation",
"authenticationToolType",
"scanImage",
"registeredUser",
"userId",
"language",
"eventId",
"contactEmail",
"fullName",
"comments"
})
public class QRSerialValidationBean implements Serializable {

    private static final long serialVersionUID = 6941740308025470888L;

    @JsonProperty("serialNumber")
    private String serialNumber;
    
    @JsonProperty("authCode")
    private String authCode;

    @JsonProperty("catalogNumber")
    private String catalogNumber;

	@JsonProperty("ip4Address")
    private String ip4Address;

    @JsonProperty("ip6Address")
    private String ip6Address;

    @JsonProperty("geolocationCountry")
    private String geolocationCountry;

    @JsonProperty("geolocationCity")
    private String geolocationCity;

    @JsonProperty("gpsLocation")
    private String gpsLocation;

    @JsonProperty("mobileLocation")
    private String mobileLocation;

    @JsonProperty("authenticationToolType")
    private String authenticationToolType;

    @JsonProperty("scanImage")
    private String scanImage;

    @JsonProperty("registeredUser")
    private String registeredUser;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("language")
    private String language;

    @JsonProperty("eventId")
    private String eventId;
	
	@JsonProperty("contactEmail")
    private String contactEmail;
    
    @JsonProperty("fullName")
    private String fullName;
    
    @JsonProperty("comments")
    private String comments;

    @JsonProperty("serialNumber")
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    @JsonProperty("serialNumber")
    public String getSerialNumber() { return serialNumber; }

    @JsonProperty("authCode")
    public void setAuthCode(String authCode) { this.authCode = authCode; }
    @JsonProperty("authCode")
    public String getAuthCode() { return authCode; }
    
    @JsonProperty("catalogNumber")
    public String getCatalogNumber() { return catalogNumber; }
    @JsonProperty("catalogNumber")
    public void setCatalogNumber(String catalogNumber) { this.catalogNumber = catalogNumber; }

    @JsonProperty("ip4Address")
    public String getIp4Address() { return ip4Address; }

    @JsonProperty("ip4Address")
    public void setIp4Address(String ip4Address) { this.ip4Address = ip4Address; }

    @JsonProperty("ip6Address")
    public String getIp6Address() { return ip6Address; }
    @JsonProperty("ip6Address")
    public void setIp6Address(String ip6Address) { this.ip6Address = ip6Address; }

    @JsonProperty("geolocationCountry")
    public String getGeolocationCountry() { return geolocationCountry; }
    @JsonProperty("geolocationCountry")
    public void setGeolocationCountry(String geolocationCountry) { this.geolocationCountry = geolocationCountry; }

    @JsonProperty("geolocationCity")
    public String getGeolocationCity() { return geolocationCity; }
    @JsonProperty("geolocationCity")
    public void setGeolocationCity(String geolocationCity) { this.geolocationCity = geolocationCity; }

    @JsonProperty("gpsLocation")
    public String getGpsLocation() { return gpsLocation;}
    @JsonProperty("gpsLocation")
    public void setGpsLocation(String gpsLocation) { this.gpsLocation = gpsLocation; }

    @JsonProperty("mobileLocation")
    public String getMobileLocation() { return mobileLocation; }
    @JsonProperty("mobileLocation")
    public void setMobileLocation(String mobileLocation) { this.mobileLocation = mobileLocation; }

    @JsonProperty("authenticationToolType")
    public String getAuthenticationToolType() { return authenticationToolType; }
    @JsonProperty("authenticationToolType")
    public void setAuthenticationToolType(String authenticationToolType) { this.authenticationToolType = authenticationToolType; }

    @JsonProperty("scanImage")
    public String getScanImage() { return scanImage; }
    @JsonProperty("scanImage")
    public void setScanImage(String scanImage) { this.scanImage = scanImage; }

    @JsonProperty("registeredUser")
    public String getRegisteredUser() { return registeredUser; }
    @JsonProperty("registeredUser")
    public void setRegisteredUser(String registeredUser) { this.registeredUser = registeredUser; }

    @JsonProperty("userId")
    public String getUserId() { return userId; }
    @JsonProperty("userId")
    public void setUserId(String userId) { this.userId = userId; }

    @JsonProperty("language")
    public String getLanguage() { return language; }
    @JsonProperty("language")
    public void setLanguage(String language) { this.language = language; }

    @JsonProperty("eventId")
    public String getEventId() { return eventId; }
    @JsonProperty("eventId")
    public void setEventId(String eventId) { this.eventId = eventId; }
	
	@JsonProperty("contactEmail")
    public String getcontactEmail() { return contactEmail; }
    @JsonProperty("contactEmail")
    public void setcontactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    
    @JsonProperty("fullName")
    public String getfullName() { return fullName; }
    @JsonProperty("fullName")
    public void setfullName(String fullName) { this.fullName = fullName; }

    
    @JsonProperty("comments")
    public String getComments() { return comments; }
    @JsonProperty("comments")
    public void setComments(String comments) { this.comments = comments; }

    /**
     * Intended only for debugging.
     *
     * <P>Here, the contents of every field are placed into the result, with
     * one field per line.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        for (Field field : fields) {
            if(!field.getName().equalsIgnoreCase("serialVersionUID")){
                result.append("  ");
                try {
                    result.append(field.getName());
                    result.append(": ");
                    //requires access to private field:
                    result.append(field.get(this));
                }
                catch (IllegalAccessException ex) {
                    return "Error in toString of EndecaServiceRequestBean"+ex.getMessage();
                }
                result.append(newLine);
            }
        }

        return result.toString();
    }


}
