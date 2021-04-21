package org.ops4j.pax.web.service.ext;

import org.ops4j.pax.web.service.WebContainerConstants;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface WebContainerConstantsExt extends WebContainerConstants {

    String PROPERTY_MAX_HEADER_BUFFER_SIZE = "org.ops4j.pax.web.max.header.buffer.size";

    String PROPERTY_TRUST_STORE  = "org.ops4j.pax.web.trustStore";

    String PROPERTY_TRUST_PASSWORD  = "org.ops4j.pax.web.trustPassword";

    String PROPERTY_TRUST_STORE_TYPE  = "org.ops4j.pax.web.trustStoreType";

    String PROPERTY_SECURE_COOKIES = "org.ops4j.pax.web.secureCookies";

}
