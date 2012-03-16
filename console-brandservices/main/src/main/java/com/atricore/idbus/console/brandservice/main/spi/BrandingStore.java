package com.atricore.idbus.console.brandservice.main.spi;

import com.atricore.idbus.console.brandservice.main.BrandingDefinition;
import com.atricore.idbus.console.brandservice.main.BrandingServiceException;
import com.atricore.idbus.console.brandservice.main.NoSuchBrandingException;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface BrandingStore {

    // Persistence CRUD operations
    
    BrandingDefinition create(BrandingDefinition def) throws BrandingServiceException;
    
    BrandingDefinition retrieve(long id) throws BrandingServiceException;

    BrandingDefinition retrieveByName(String name) throws NoSuchBrandingException;
    
    BrandingDefinition update(BrandingDefinition def) throws BrandingServiceException;
    
    void delete(long id)  throws BrandingServiceException;
    
    // List all
    Collection<BrandingDefinition> list() throws BrandingServiceException;



}
