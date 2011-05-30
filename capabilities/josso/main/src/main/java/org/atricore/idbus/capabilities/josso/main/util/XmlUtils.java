package org.atricore.idbus.capabilities.josso.main.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.josso.main.JossoConstants;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.core.util.StringSource;
import org.atricore.idbus.kernel.main.databinding.JAXBUtils;
import org.springframework.util.StopWatch;

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class XmlUtils {
    private static final Log logger = LogFactory.getLog(XmlUtils.class);

    private static final TreeSet<String> ssoContextPackages = new TreeSet<String>();

    private static final Holder<JAXBUtils.CONSTRUCTION_TYPE> constructionType = new Holder<JAXBUtils.CONSTRUCTION_TYPE>();

    static {
        ssoContextPackages.add(JossoConstants.JOSSO_PROTOCOL_PKG);
    }



    public static String marshall(Object content , boolean encode) throws Exception {
        String type = content.getClass().getSimpleName();
        if (type.endsWith("Type"))
            type = type.substring(0, type.length() - 4);

        return marshall(content, type, encode);
    }


   public static String marshall(Object content,
                                     String contentType, boolean encode) throws Exception {

        String marshalled;

        marshalled = XmlUtils.marshal(content,
                JossoConstants.JOSSO_PROTOCOL_NS,
                contentType,
                new String[]{ JossoConstants.JOSSO_PROTOCOL_PKG }
        );


        return encode ? new String( new Base64().encode( marshalled.getBytes() ) ) : marshalled;
    }

   public static JAXBElement createJAXBelement(Object content) {

        Class clazz = (Class) content.getClass();

        // Remove the 'Type' suffix from the xml type name and use it as XML element!
        XmlType t = (XmlType) clazz.getAnnotation(XmlType.class);

        String element = t.name().substring(0, t.name().length() - 4);

        return new JAXBElement(new QName(JossoConstants.JOSSO_PROTOCOL_NS, element), clazz, content);

   }

    // JAXB Generic

    public static String marshal ( Object msg, String msgQName, String msgLocalName, String[] userPackages ) throws Exception {

        TreeSet<String> contextPackages = new TreeSet<String>();
        for (int i = 0; i < userPackages.length; i++) {
            String userPackage = userPackages[i];
            contextPackages.add(userPackage);
        }

        JAXBContext jaxbContext = JAXBUtils.getJAXBContext(contextPackages, constructionType,
                contextPackages.toString(), XmlUtils.class.getClassLoader(), new HashMap<String, Object>());
        Marshaller marshaller = JAXBUtils.getJAXBMarshaller(jaxbContext);

        JAXBElement jaxbRequest = new JAXBElement( new QName( msgQName, msgLocalName ),
                msg.getClass(),
                msg
        );
        Writer writer = new StringWriter();

        // Support XMLDsig
        jaxbContext.createMarshaller().marshal( jaxbRequest, writer);

        return writer.toString();
    }

    public static Object unmarshal(String msg, String userPackages[]) throws Exception {
        TreeSet<String> contextPackages = new TreeSet<String>();
        for (int i = 0; i < userPackages.length; i++) {
            String userPackage = userPackages[i];
            contextPackages.add(userPackage);
        }

        JAXBContext jaxbContext = JAXBUtils.getJAXBContext(contextPackages, constructionType,
                contextPackages.toString(), XmlUtils.class.getClassLoader(), new HashMap<String, Object>());
        Unmarshaller unmarshaller = JAXBUtils.getJAXBUnmarshaller(jaxbContext);
        Object o = unmarshaller.unmarshal(new StringSource(msg));

        if (o instanceof JAXBElement)
            return ((JAXBElement) o).getValue();

        return o;

    }

}
