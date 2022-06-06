/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.kernel.main.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Properties;

/**
 * @author <a href="mailto:sgonzalez@josso.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: ConfigurationContext.java 1040 2009-03-05 00:56:52Z gbrigand $
 */

public class ConfigurationContextImpl implements ConfigurationContext {

    public static final Log logger = LogFactory.getLog(ConfigurationContextImpl.class);

    private Properties properties;

    public ConfigurationContextImpl(Properties properties) {
        this.properties = properties;
        Map<String, String> env = System.getenv();

        for (String key : properties.stringPropertyNames()) {
            String envKey = toEnvVar(key);
            String envValue = env.get(envKey);
            if (envValue != null ) {
                properties.put(key, envValue);
                logger.info("Overriding kernel config [" + key + "] with environment value from [" + envKey + "]");
            }
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public Properties getProperties() {
        return properties;
    }

    protected String toEnvVar(String name) {
        return "JOSSO_" + name.toUpperCase().replace('.', '_');
    }

}
