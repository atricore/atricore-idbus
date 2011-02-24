package com.atricore.idbus.console.licensing.main;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OsgiProductFeaturesRegistry {

    private static final Log logger = LogFactory.getLog(OsgiProductFeaturesRegistry.class);

    private LicenseManager lm;

    public void register(final ProductFeature productFeature, final Map<String, ?> properties) {
        logger.info("Identity Mediation Unit registered : " + productFeature.getName());
        if (logger.isDebugEnabled()) {
            logger.debug("IDMU registered " + productFeature);
        }

        lm.registerFeature(productFeature.getName(), productFeature);
    }

    public void unregister(final ProductFeature  productFeature, final Map<String, ?> properties) {
        logger.info("Identity Mediation Unit unregistered : " + productFeature.getName());
        if (logger.isDebugEnabled()) {
            logger.debug("IDMU unregistered " + productFeature);
        }

        lm.unregisterFeature(productFeature.getName());
    }

}
