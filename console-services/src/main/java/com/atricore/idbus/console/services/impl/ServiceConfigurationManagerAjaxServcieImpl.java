package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.services.dto.settings.ServiceConfigurationDTO;
import com.atricore.idbus.console.services.dto.settings.ServiceTypeDTO;
import com.atricore.idbus.console.services.spi.ServiceConfigurationException;
import com.atricore.idbus.console.services.spi.ServiceConfigurationManagerAjaxService;
import com.atricore.idbus.console.services.spi.response.ConfigureServiceResponse;
import com.atricore.idbus.console.settings.main.spi.ServiceConfiguration;
import com.atricore.idbus.console.settings.main.spi.ServiceConfigurationManager;
import com.atricore.idbus.console.settings.main.spi.ServiceType;
import org.dozer.DozerBeanMapper;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ServiceConfigurationManagerAjaxServcieImpl implements ServiceConfigurationManagerAjaxService {
    
    private ServiceConfigurationManager cfgManager;

    private DozerBeanMapper dozerMapper;

    public ServiceConfigurationManager getCfgManager() {
        return cfgManager;
    }

    public void setCfgManager(ServiceConfigurationManager cfgManager) {
        this.cfgManager = cfgManager;
    }

    public void setDozerMapper(DozerBeanMapper dozerMapper) {
        this.dozerMapper = dozerMapper;
    }

    public ConfigureServiceResponse configureService(ServiceConfigurationDTO cfg) throws ServiceConfigurationException {
        ServiceConfiguration serviceCfg = dozerMapper.map(cfg, ServiceConfiguration.class);

        try {
            boolean restart = cfgManager.configureService(serviceCfg);
            return new ConfigureServiceResponse(cfg.getServiceType(), restart);
        } catch (com.atricore.idbus.console.settings.main.spi.ServiceConfigurationException e) {
            throw new ServiceConfigurationException(cfg.getServiceType(), e);
        }
    }

    public ServiceConfigurationDTO lookupConfiguration(ServiceTypeDTO serviceName) throws ServiceConfigurationException {
        ServiceType st = null;
        if (serviceName.equals(ServiceTypeDTO.HTTP)) {
            st = ServiceType.HTTP;
        } else if (serviceName.equals(ServiceTypeDTO.SSH)) {
            st = ServiceType.SSH;
        } else if (serviceName.equals(ServiceTypeDTO.PERSISTENCE)) {
            st = ServiceType.PERSISTENCE;
        } else if (serviceName.equals(ServiceTypeDTO.MANAGEMENT)) {
            st = ServiceType.MANAGEMENT;
        } else if (serviceName.equals(ServiceTypeDTO.AQM)) {
            st = ServiceType.AQM;
        } else if (serviceName.equals(ServiceTypeDTO.LOG)) {
            st = ServiceType.LOG;
        }

        ServiceConfiguration sc;
        try {
            sc = cfgManager.lookupConfiguration(st);
        } catch (com.atricore.idbus.console.settings.main.spi.ServiceConfigurationException e) {
            throw new ServiceConfigurationException(serviceName, e);
        }
        return dozerMapper.map(sc, ServiceConfigurationDTO.class);
    }
}
