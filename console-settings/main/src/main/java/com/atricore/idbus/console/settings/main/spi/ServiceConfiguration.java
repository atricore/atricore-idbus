package com.atricore.idbus.console.settings.main.spi;

import java.io.Serializable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ServiceConfiguration extends Serializable {
    
    ServiceType getServiceType();


}
