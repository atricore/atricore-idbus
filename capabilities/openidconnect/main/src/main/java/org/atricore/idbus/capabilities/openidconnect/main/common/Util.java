package org.atricore.idbus.capabilities.openidconnect.main.common;

import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgonzalez.
 */
public class Util {

    public static final Map<String, String> unmarshall(String params) throws IOException, ClassNotFoundException {

        byte[] dec = Base64.decodeBase64(params.getBytes());
        ByteArrayInputStream bais = new ByteArrayInputStream (dec);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Map<String, String> map = (HashMap) ois.readObject();
        ois.close();
        bais.close();
        return map;
    }

    public static final Map<String, List<String>> unmarshallMultiValue(String params) throws IOException, ClassNotFoundException {

        byte[] dec = Base64.decodeBase64(params.getBytes());
        ByteArrayInputStream bais = new ByteArrayInputStream (dec);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Map<String, List<String>> map = (HashMap) ois.readObject();
        ois.close();
        bais.close();
        return map;
    }



    public static final String marshall(Map<String, String> params) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(params);
        oos.close();
        baos.close();

        byte[] enc = Base64.encodeBase64(baos.toByteArray());
        return new String (enc, "UTF-8");
    }

    public static final String marshallMultiValue(Map<String, List<String>> params) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(params);
        oos.close();
        baos.close();

        byte[] enc = Base64.encodeBase64(baos.toByteArray());
        return new String (enc, "UTF-8");
    }
}
