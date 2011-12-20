package org.atricore.idbus.capabilities.oauth2.main.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.main.OAuth2Client;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JasonUtils {

    private static final Log logger = LogFactory.getLog(JasonUtils.class);

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.writerWithType(List.class).withType(ArrayList.class);
    }

    public static List<OAuth2Client> unmarshallClients(String clientsStr) throws IOException {
        return mapper.readValue(new ByteArrayInputStream(clientsStr.getBytes()),
                new TypeReference<List<OAuth2Client>>() { });
    }
}
