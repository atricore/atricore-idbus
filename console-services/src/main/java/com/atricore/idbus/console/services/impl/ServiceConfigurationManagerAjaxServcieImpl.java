package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.services.spi.ServiceConfigurationManagerAjaxService;
import com.atricore.idbus.console.settings.main.spi.ServiceConfigurationManager;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ServiceConfigurationManagerAjaxServcieImpl implements ServiceConfigurationManagerAjaxService {
    
    private ServiceConfigurationManager cfgManager;

    public ServiceConfigurationManager getCfgManager() {
        return cfgManager;
    }

    public void setCfgManager(ServiceConfigurationManager cfgManager) {
        this.cfgManager = cfgManager;
    }

    // TODO : implement methods, create DTOs (use dozer ?)
}
