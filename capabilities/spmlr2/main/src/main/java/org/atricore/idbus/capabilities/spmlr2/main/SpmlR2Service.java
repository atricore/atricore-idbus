package org.atricore.idbus.capabilities.spmlr2.main;

import javax.xml.namespace.QName;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public enum SpmlR2Service {
    
    //

    PSPService(new QName("urn:oasis:names:tc:SPML:2:0", "PSPService"))
    ;
    
    private QName qname;


    SpmlR2Service(String uri, String localPart) {
        this(new QName(uri, localPart));
    }

    SpmlR2Service(QName qname) {
        this.qname = qname;
    }

    public QName getQname() {
        return qname;
    }


    public static SpmlR2Service asEnum(String name) {
        String localPart = name.substring(name.lastIndexOf("}") + 1);
        String uri = name.lastIndexOf("}") > 0 ? name.substring(1, name.lastIndexOf("}")) : "";

        QName qname = new QName(uri,  localPart);
        return asEnum(qname);
    }

    public static SpmlR2Service asEnum(QName qname) {
        for (SpmlR2Service et : values()) {
            if (et.getQname().equals(qname))
                return et;
        }

        throw new IllegalArgumentException("Invalid endpoint type: " + qname);
    }

    @Override
    public String toString() {
        return qname.toString();
    }
    
}
