package com.eaton.platform.integration.cad;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a Javadoc comment
 * Formats class
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "formats",
        "format"
})
@XmlRootElement(name="formats")

public class Formats {

    @XmlElement(name="formats")
    protected String formats;

    @XmlElement(name="format")
    protected List<Formats.format> format;
    public String getFormats() {
        return formats;
    }
    public void setFormats(String formats) {
        this.formats = formats;
    }
    public List<Formats.format> getFormat() {
        if(format == null){
            format = new ArrayList<Formats.format>();
        }
        return this.format;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "name",
            "version",
            "qualifier",
            "directintocad",
            "macro",
            "helplink"
    })

    public static class format {

        @XmlAttribute(name = "cad")
        protected String cad;

        @XmlAttribute(name = "available")
        protected String available;

        @XmlAttribute(name = "type")
        protected String type;

        @XmlElement(name="name")
        protected String name;

        @XmlElement(name="version")
        protected String version;

        @XmlElement(name="qualifier")
        protected String qualifier;
        @XmlElement(name="directintocad")
        protected String directintocad;

        @XmlElement(name="macro")
        protected String macro;
        @XmlElement(name="helplink")
        protected String helplink;

        public String getCad() {
            return cad;
        }

        public void setCad(String cad) {
            this.cad = cad;
        }

        public String getAvailable() {
            return available;
        }

        public void setAvailable(String available) {
            this.available = available;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getQualifier() {
            return qualifier;
        }

        public void setQualifier(String qualifier) {
            this.qualifier = qualifier;
        }

        public String getDirectintocad() {
            return directintocad;
        }

        public void setDirectintocad(String directintocad) {
            this.directintocad = directintocad;
        }

        public String getMacro() {
            return macro;
        }

        public void setMacro(String macro) {
            this.macro = macro;
        }

        public String getHelplink() {
            return helplink;
        }

        public void setHelplink(String helplink) {
            this.helplink = helplink;
        }


    }

}
