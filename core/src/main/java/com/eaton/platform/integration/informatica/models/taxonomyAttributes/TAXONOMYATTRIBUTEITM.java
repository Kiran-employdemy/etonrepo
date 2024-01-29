//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.08.10 at 03:42:37 PM IST 
//


package com.eaton.platform.integration.informatica.models.taxonomyAttributes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ROW" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ATTR_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ATTR_DISPLAY_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="LAST_UPDATE_DATE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="STATUS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="INVENTORY_ITEM_ID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="PRODUCT_FAMILY_EXT_ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="PRODUCT_BRAND" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="PRODUCT_SUB_BRAND" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="PRODUCT_TRADE_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ICC" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="SEQUENCE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ATTR_GROUP_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ATTR_GROUP_DISP_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "row"
})
@XmlRootElement(name = "TAXONOMY_ATTRIBUTE_ITM")
public class TAXONOMYATTRIBUTEITM {

    @XmlElement(name = "ROW")
    protected List<TAXONOMYATTRIBUTEITM.ROW> row;

    /**
     * Gets the value of the row property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the row property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getROW().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TAXONOMYATTRIBUTEITM.ROW }
     * 
     * 
     */
    public List<TAXONOMYATTRIBUTEITM.ROW> getROW() {
        if (row == null) {
            row = new ArrayList<TAXONOMYATTRIBUTEITM.ROW>();
        }
        return this.row;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="ATTR_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ATTR_DISPLAY_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="LAST_UPDATE_DATE" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="STATUS" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="INVENTORY_ITEM_ID" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *         &lt;element name="PRODUCT_FAMILY_EXT_ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="PRODUCT_BRAND" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="PRODUCT_SUB_BRAND" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="PRODUCT_TRADE_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ICC" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="SEQUENCE" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ATTR_GROUP_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ATTR_GROUP_DISP_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "attrname",
        "attrdisplayname",
        "lastupdatedate",
        "status",
        "inventoryitemid",
        "productfamilyextid",
        "productbrand",
        "productsubbrand",
        "producttradename",
        "icc",
        "sequence",
        "attrgroupname",
        "attrgroupdispname"
    })
    public static class ROW {

        @XmlElement(name = "ATTR_NAME", required = true)
        protected String attrname;
        @XmlElement(name = "ATTR_DISPLAY_NAME", required = true)
        protected String attrdisplayname;
        @XmlElement(name = "LAST_UPDATE_DATE", required = true)
        protected String lastupdatedate;
        @XmlElement(name = "STATUS", required = true)
        protected String status;
        @XmlElement(name = "INVENTORY_ITEM_ID")
        protected long inventoryitemid;
        @XmlElement(name = "PRODUCT_FAMILY_EXT_ID", required = true)
        protected String productfamilyextid;
        @XmlElement(name = "PRODUCT_BRAND", required = true)
        protected String productbrand;
        @XmlElement(name = "PRODUCT_SUB_BRAND", required = true)
        protected String productsubbrand;
        @XmlElement(name = "PRODUCT_TRADE_NAME", required = true)
        protected String producttradename;
        @XmlElement(name = "ICC", required = true)
        protected String icc;
        @XmlElement(name = "SEQUENCE", required = true)
        protected String sequence;
        @XmlElement(name = "ATTR_GROUP_NAME", required = true)
        protected String attrgroupname;
        @XmlElement(name = "ATTR_GROUP_DISP_NAME", required = true)
        protected String attrgroupdispname;

        /**
         * Gets the value of the attrname property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getATTRNAME() {
            return attrname;
        }

        /**
         * Sets the value of the attrname property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setATTRNAME(String value) {
            this.attrname = value;
        }

        /**
         * Gets the value of the attrdisplayname property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getATTRDISPLAYNAME() {
            return attrdisplayname;
        }

        /**
         * Sets the value of the attrdisplayname property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setATTRDISPLAYNAME(String value) {
            this.attrdisplayname = value;
        }

        /**
         * Gets the value of the lastupdatedate property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLASTUPDATEDATE() {
            return lastupdatedate;
        }

        /**
         * Sets the value of the lastupdatedate property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLASTUPDATEDATE(String value) {
            this.lastupdatedate = value;
        }

        /**
         * Gets the value of the status property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSTATUS() {
            return status;
        }

        /**
         * Sets the value of the status property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSTATUS(String value) {
            this.status = value;
        }

        /**
         * Gets the value of the inventoryitemid property.
         * 
         */
        public long getINVENTORYITEMID() {
            return inventoryitemid;
        }

        /**
         * Sets the value of the inventoryitemid property.
         * 
         */
        public void setINVENTORYITEMID(long value) {
            this.inventoryitemid = value;
        }

        /**
         * Gets the value of the productfamilyextid property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPRODUCTFAMILYEXTID() {
            return productfamilyextid;
        }

        /**
         * Sets the value of the productfamilyextid property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPRODUCTFAMILYEXTID(String value) {
            this.productfamilyextid = value;
        }

        /**
         * Gets the value of the productbrand property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPRODUCTBRAND() {
            return productbrand;
        }

        /**
         * Sets the value of the productbrand property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPRODUCTBRAND(String value) {
            this.productbrand = value;
        }

        /**
         * Gets the value of the productsubbrand property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPRODUCTSUBBRAND() {
            return productsubbrand;
        }

        /**
         * Sets the value of the productsubbrand property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPRODUCTSUBBRAND(String value) {
            this.productsubbrand = value;
        }

        /**
         * Gets the value of the producttradename property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPRODUCTTRADENAME() {
            return producttradename;
        }

        /**
         * Sets the value of the producttradename property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPRODUCTTRADENAME(String value) {
            this.producttradename = value;
        }

        /**
         * Gets the value of the icc property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getICC() {
            return icc;
        }

        /**
         * Sets the value of the icc property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setICC(String value) {
            this.icc = value;
        }

        /**
         * Gets the value of the sequence property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSEQUENCE() {
            return sequence;
        }

        /**
         * Sets the value of the sequence property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSEQUENCE(String value) {
            this.sequence = value;
        }

        /**
         * Gets the value of the attrgroupname property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getATTRGROUPNAME() {
            return attrgroupname;
        }

        /**
         * Sets the value of the attrgroupname property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setATTRGROUPNAME(String value) {
            this.attrgroupname = value;
        }

        /**
         * Gets the value of the attrgroupdispname property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getATTRGROUPDISPNAME() {
            return attrgroupdispname;
        }

        /**
         * Sets the value of the attrgroupdispname property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setATTRGROUPDISPNAME(String value) {
            this.attrgroupdispname = value;
        }

    }

}