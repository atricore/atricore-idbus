package com.atricore.idbus.console.brandservice.main.internal.store;

import com.atricore.idbus.console.brandservice.main.domain.BrandingDefinition;
import com.atricore.idbus.console.brandservice.main.BrandingServiceException;
import com.atricore.idbus.console.brandservice.main.spi.BrandingStore;
import com.atricore.idbus.console.brandservice.main.NoSuchBrandingException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class MemoryBrandingStore implements BrandingStore {
    
    private long lastKey;
    
    private Map<Long, BrandingDefinition> brandings = new HashMap<Long, BrandingDefinition>();

    public BrandingDefinition  create(BrandingDefinition def) throws BrandingServiceException {
        lastKey ++;
        long id = lastKey;
        def.setId(id);
        brandings.put(id, def);
        return def;
    }

    public BrandingDefinition retrieve(long id) throws BrandingServiceException {
        BrandingDefinition def = brandings.get(id);
        if (def == null)
            throw new NoSuchBrandingException(id);
        
        return def;

    }

    public BrandingDefinition retrieveByName(String name) throws NoSuchBrandingException {
        for (BrandingDefinition b : brandings.values())
            if (b.getName().equals(name))
                return b;
        throw new NoSuchBrandingException("name=" + name);
    }

    public BrandingDefinition update(BrandingDefinition def) throws BrandingServiceException {
        // Just to make sure that it exists
        retrieve(def.getId());
        brandings.put(def.getId(), def);
        return def;
    }

    public void delete(long id) throws BrandingServiceException {
        // Just to make sure that it exists
        retrieve(id);
        brandings.remove(id);
    }

    public Collection<BrandingDefinition> list() throws BrandingServiceException {
        return brandings.values();
    }

    // For spring compatibility only!
    public Map<Long, BrandingDefinition> getBrandings() {
        return brandings;
    }

    public void setBrandings(Map<Long, BrandingDefinition> brandings) {
        this.brandings = brandings;
    }
}
