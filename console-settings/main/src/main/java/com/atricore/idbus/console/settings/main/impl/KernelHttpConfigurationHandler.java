package com.atricore.idbus.console.settings.main.impl;

import com.atricore.idbus.console.settings.main.spi.HttpServiceConfiguration;
import com.atricore.idbus.console.settings.main.spi.ServiceConfigurationException;
import com.atricore.idbus.console.settings.main.spi.ServiceType;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Handles kernel configuration parameters related with HTTP settings like HTTP redirect following.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class KernelHttpConfigurationHandler extends OsgiServiceConfigurationHandler<HttpServiceConfiguration> {

    public KernelHttpConfigurationHandler() {
        // This is actually the file name containing the properties, without the extension (cfg)
        super("org.atricore.idbus.kernel.main");
    }

    public boolean canHandle(ServiceType type) {
        return type.equals(ServiceType.HTTP);
    }

    public HttpServiceConfiguration loadConfiguration(ServiceType type, HttpServiceConfiguration config) throws ServiceConfigurationException {
        try {
            Dictionary<String, String> d = super.getProperties();
            return toConfiguration(d, config);
        } catch (Exception e) {
            throw new ServiceConfigurationException("Error loading Persistence configuration properties " + e.getMessage(), e);
        }
    }

    public boolean storeConfiguration(HttpServiceConfiguration config) throws ServiceConfigurationException {
        try {

            Dictionary<String, String> d = toDictionary(config);
            updateProperties(d);
            return true;
        } catch (IOException e) {
            throw new ServiceConfigurationException("Error storing Persistence configuration properties " + e.getMessage(), e);
        }
    }

    protected Dictionary<String, String> toDictionary(HttpServiceConfiguration config) {
        Dictionary<String, String> d = new Hashtable<String, String>();

        if (config.getBindAddresses() != null) {

            // Build local target base URL from HTTP service properties:
            String localUrl = "http://127.0.0.1";

            for (int i = 0; i < config.getBindAddresses().length; i++) {
                String address = config.getBindAddresses()[i];
                if (address.equals("127.0.0.1") || address.equals("0.0.0.0")) {
                    localUrl = "http://127.0.0.1:" + config.getPort();
                    break;
                } else {
                    localUrl = "http://" + address + ":" + config.getPort();
                }
            }

            d.put("binding.http.localTargetBaseUrl", localUrl);
            d.put("binding.http.followRedirects", config.isFollowRedirects() + "");

            if (config.getIncludeFollowUrls() != null)
                d.put("binding.http.followRedirects.includeUrls", config.getIncludeFollowUrls());

            if (config.getExcludeFollowUrls() != null)
                d.put("binding.http.followRedirects.excludeUrls", config.getExcludeFollowUrls());

        } else {
            // This is an error ...
        }



        return d;
    }

    protected HttpServiceConfiguration toConfiguration(Dictionary props, HttpServiceConfiguration currentCfg) {
        HttpServiceConfiguration cfg = currentCfg != null ? currentCfg : new HttpServiceConfiguration();

        String fr = (String) props.get("binding.http.followRedirects");
        String includeUrls = (String) props.get("binding.http.followRedirects.includeUrls");
        String excludeUrls = (String) props.get("binding.http.followRedirects.excludeUrls");

        cfg.setIncludeFollowUrls(includeUrls);
        cfg.setExcludeFollowUrls(excludeUrls);

        cfg.setFollowRedirects(fr != null && Boolean.parseBoolean(fr));

        return cfg;
    }
}
