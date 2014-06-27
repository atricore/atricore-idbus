package org.ops4j.pax.web.service.ext;

import org.ops4j.pax.web.service.spi.Configuration;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ConfigurationExt extends Configuration {

    Integer getHeaderBufferSize();

    String getTrustStore();

    String getTrustPassword();

    String getTrustStoreType();

    Boolean getSecureCookies();

}
