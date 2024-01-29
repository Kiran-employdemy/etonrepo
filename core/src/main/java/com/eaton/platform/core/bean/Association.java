//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.07.26 at 03:58:16 PM IST 
//


package com.eaton.platform.core.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element name="Association-Type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SKU" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Image" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Catalog" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ProductName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="SelectedTag" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Priority" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="seq" type="{http://www.w3.org/2001/XMLSchema}byte" />
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
    "associationType",
    "sku"
})
@XmlRootElement(name = "Association")
public class Association {

    @XmlElement(name = "Association-Type", required = true)
    protected String associationType;
    @XmlElement(name = "SKU")
    protected List<SKU> sku;

    /**
     * Gets the value of the associationType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssociationType() {
        return associationType;
    }

    /**
     * Sets the value of the associationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssociationType(String value) {
        this.associationType = value;
    }

    /**
     * Gets the value of the sku property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sku property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSKU().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SKU }
     * 
     * 
     */
    public List<SKU> getSKU() {
        if (sku == null) {
            sku = new ArrayList<SKU>();
        }
        return this.sku;
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
     *         &lt;element name="Image" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Catalog" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ProductName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="SelectedTag" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Priority" type="{http://www.w3.org/2001/XMLSchema}byte"/>
     *       &lt;/sequence>
     *       &lt;attribute name="seq" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "image",
        "catalog",
        "productName",
        "productFamily",
        "priority",
        "longDesc"
    })
    public static class SKU {

        @XmlElement(name = "Image", required = true)
        protected String image;
        @XmlElement(name = "Catalog", required = true)
        protected String catalog;
        @XmlElement(name = "ProductName", required = true)
        protected String productName;
        @XmlElement(name = "SelectedTag", required = true)
        protected String productFamily;
        @XmlElement(name = "Priority")
        protected byte priority;
        @XmlAttribute(name = "seq")
        protected Byte seq;
        @XmlElement(name = "LongDesc", required = true)
        protected String longDesc;

        /**
         * Gets the value of the image property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getImage() {
            return image;
        }

        /**
         * Sets the value of the image property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setImage(String value) {
            this.image = value;
        }

        /**
         * Gets the value of the catalog property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCatalog() {
            return catalog;
        }

        /**
         * Sets the value of the catalog property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCatalog(String value) {
            this.catalog = value;
        }

        /**
         * Gets the value of the productName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getProductName() {
            return productName;
        }

        /**
         * Sets the value of the productName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setProductName(String value) {
            this.productName = value;
        }

        /**
         * Gets the value of the productFamily property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getProductFamily() {
            return productFamily;
        }

        /**
         * Sets the value of the productFamily property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setProductFamily(String value) {
            this.productFamily = value;
        }

        /**
         * Gets the value of the priority property.
         * 
         */
        public byte getPriority() {
            return priority;
        }

        /**
         * Sets the value of the priority property.
         * 
         */
        public void setPriority(byte value) {
            this.priority = value;
        }

        /**
         * Gets the value of the seq property.
         * 
         * @return
         *     possible object is
         *     {@link Byte }
         *     
         */
        public Byte getSeq() {
            return seq;
        }

        /**
         * Sets the value of the seq property.
         * 
         * @param value
         *     allowed object is
         *     {@link Byte }
         *     
         */
        public void setSeq(Byte value) {
            this.seq = value;
        }
        
        /**
         * Gets the value of the long description property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLongDesc() {
            return longDesc;
        }

        /**
         * Sets the value of the long description property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLongDesc(String value) {
            this.longDesc = value;
        }

    }

}