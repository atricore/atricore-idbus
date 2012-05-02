package com.atricore.idbus.console.services.spi;

import com.atricore.idbus.console.services.dto.settings.ServiceConfigurationDTO;
import com.atricore.idbus.console.services.dto.settings.ServiceTypeDTO;
import com.atricore.idbus.console.services.spi.response.ConfigureServiceResponse;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ServiceConfigurationManagerAjaxService {

    ConfigureServiceResponse configureService(ServiceConfigurationDTO cfg) throws ServiceConfigurationException;

    ServiceConfigurationDTO lookupConfiguration(ServiceTypeDTO serviceName) throws ServiceConfigurationException;
}
