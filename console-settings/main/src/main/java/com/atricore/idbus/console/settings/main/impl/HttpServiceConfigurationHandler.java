package com.atricore.idbus.console.settings.main.impl;

import com.atricore.idbus.console.settings.main.spi.HttpServiceConfiguration;
import com.atricore.idbus.console.settings.main.spi.ServiceConfiguration;
import com.atricore.idbus.console.settings.main.spi.ServiceConfigurationException;
import com.atricore.idbus.console.settings.main.spi.ServiceType;

import java.io.IOException;
import java.util.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class HttpServiceConfigurationHandler extends OsgiServiceConfigurationHandler<HttpServiceConfiguration> {

    public HttpServiceConfigurationHandler() {
        // This is actually the file name containing the properties, without the extension (cfg)
        super("org.ops4j.pax.web");
    }

    public boolean canHandle(ServiceType type) {
        return type.equals(ServiceType.HTTP);
    }

    public HttpServiceConfiguration loadConfiguration(ServiceType type) throws ServiceConfigurationException {
        try {
            Dictionary<String, String> d = super.getProperties();
            return toConfiguration(d);
        } catch (Exception e) {
            throw new ServiceConfigurationException("Error loading HTTP configuration properties " + e.getMessage() , e);
        }
    }

    public void storeConfiguration(HttpServiceConfiguration config) throws ServiceConfigurationException {
        try {
            // Some service validations:

            // HTTP Port
            if (config.getPort() != null) {
                int port = config.getPort();
                if (port < 1 && port > 65535)
                    throw new ServiceConfigurationException("Invalid HTTP Port value " + port);
            } else {
                throw new ServiceConfigurationException("Invalid HTTP Port value null");
            }

            // TODO : HTTP Addresses

            // TODO : HTTP SSL Port

            Dictionary<String, String> d = toDictionary(config);
            updateProperties(d);
        } catch (IOException e) {
            throw new ServiceConfigurationException("Error storing HTTP configuration properties " + e.getMessage(), e);
        }
    }
    
    protected Dictionary<String, String> toDictionary(HttpServiceConfiguration config) {
        Dictionary<String, String> d = new Hashtable<String, String>();
        
        if (config.getBindAddresses() != null) 
            d.put("org.ops4j.pax.web.listening.addresses", toCsvString(config.getBindAddresses()));
        
        if (config.isDisableSessionUrl() != null) 
            d.put("org.ops4j.pax.web.session.url", config.isDisableSessionUrl().toString());
        
        if (config.isEnableSsl()) 
            d.put("org.osgi.service.http.secure.enabled", config.isEnableSsl().toString());
        
        if (config.getMaxHeaderBufferSize() != null) 
            d.put("org.ops4j.pax.web.max.header.buffer.size", config.getMaxHeaderBufferSize() + "");
        
        if (config.getPort() != null) 
            d.put("org.osgi.service.http.port", config.getPort() + "");
        
        if (config.getServerId() != null) 
            d.put("org.ops4j.pax.web.worker.name", config.getServerId());
        
        if (config.getSessionTimeout() != null)
            d.put("org.ops4j.pax.web.session.timeout", config.getSessionTimeout().toString());
        
        if (config.getSslKeyPassword() != null)
            d.put("org.ops4j.pax.web.ssl.keypassword", config.getSslKeyPassword());
        
        if (config.getSslKeystorePassword() != null)
            d.put("org.ops4j.pax.web.ssl.password", config.getSslKeystorePassword());

        if (config.getSslKeystorePath() != null)
            d.put("org.ops4j.pax.web.ssl.keystore", config.getSslKeystorePath());
        
        if (config.getSslPort() != null)
            d.put("org.osgi.service.http.port.secure", config.getSslPort().toString());
        
        return d;
    }
    
    protected HttpServiceConfiguration toConfiguration(Dictionary<String, String> props) {

        HttpServiceConfiguration cfg = new HttpServiceConfiguration ();
        
        if (props.get("org.ops4j.pax.web.listening.addresses") != null && !"".equals(props.get("org.ops4j.pax.web.listening.addresses")))
            cfg.setBindAddresses(getArrayFromCsv(props.get("org.ops4j.pax.web.listening.addresses")));
        else
            cfg.setBindAddresses(new String[0]);
        
        cfg.setDisableSessionUrl(getBoolean(props, "org.ops4j.pax.web.session.url"));
        cfg.setEnableSsl(getBoolean(props, "org.osgi.service.http.secure.enabled"));
        cfg.setMaxHeaderBufferSize(getInt(props, "org.ops4j.pax.web.max.header.buffer.size"));
        cfg.setPort(getInt(props, "org.osgi.service.http.port"));
        cfg.setServerId(getString(props, "org.ops4j.pax.web.worker.name"));
        cfg.setSessionTimeout(getInt(props, "org.ops4j.pax.web.session.timeout"));
        
        cfg.setSslKeyPassword(getString(props, "org.ops4j.pax.web.ssl.keypassword"));
        cfg.setSslKeystorePassword(getString(props, "org.ops4j.pax.web.ssl.password"));
        cfg.setSslKeystorePath(getString(props, "org.ops4j.pax.web.ssl.keystore"));
        cfg.setSslPort(getInt(props, "org.osgi.service.http.port.secure"));
        
        return cfg;
        
    }
    
    protected int getInt(Dictionary<String, String> props, String key) {
        String v = props.get(key);
        return v == null || "".equals(v) ? null : Integer.parseInt(v);
    }


    protected boolean getBoolean(Dictionary<String, String> props, String key) {
        String v = props.get(key);
        return v == null || "".equals(v) ? null : Boolean.parseBoolean(v);
    }
    
    protected String getString(Dictionary<String, String> props, String key) {
        String v = props.get(key);
        return v == null || "".equals(v) ? null : v;
    }

    protected String[] getArrayFromCsv(String v) {
        StringTokenizer st = new StringTokenizer(v);
        List<String> ts = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String t = st.nextToken().trim();
            ts.add(t);
        }
        return ts.toArray(new String[ts.size()]);
    }
    
    protected String toCsvString(String[] v) {
        StringBuffer b = new StringBuffer();
        
        for (int i = 0; i < v.length; i++) {
            String s = v[i];
            b.append(s);
            if (i + 1 < v.length)
                b.append(",");
        }
        
        return b.toString();
    }
}
