package org.atricore.idbus.kernel.main.util;

import java.util.Properties;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface ConfigurationContext {

    String getProperty(String key);

    String getProperty(String key, String defaultValue);

    Properties getProperties();
}
