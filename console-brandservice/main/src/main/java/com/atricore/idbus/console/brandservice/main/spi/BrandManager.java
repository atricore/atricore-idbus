package com.atricore.idbus.console.brandservice.main.spi;

import com.atricore.idbus.console.brandservice.main.domain.BrandingDefinition;
import com.atricore.idbus.console.brandservice.main.BrandingServiceException;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface BrandManager {

    public static final String SSO_UI_BUNDLE = "org.atricore.idbus.capabilities.sso.ui";

    // CRUD Operations ...
    BrandingDefinition create(BrandingDefinition def) throws BrandingServiceException;

    BrandingDefinition update(BrandingDefinition def) throws BrandingServiceException;

    void delete(long id) throws BrandingServiceException;

    // Lifecycle Operations ...
    void install(long id) throws BrandingServiceException;

    void publish() throws BrandingServiceException;

    BrandingDefinition lookupByName(String name) throws BrandingServiceException;

    // TODO : Work-around method (transactionless) to avoid clashing multimple JDO Managers
    BrandingDefinition lookupByNameNT(String name) throws BrandingServiceException;

    BrandingDefinition lookup(long id) throws BrandingServiceException;

    Collection<BrandingDefinition> list() throws BrandingServiceException;


}
