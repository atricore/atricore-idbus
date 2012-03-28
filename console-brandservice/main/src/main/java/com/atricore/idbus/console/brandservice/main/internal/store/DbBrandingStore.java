package com.atricore.idbus.console.brandservice.main.internal.store;

import com.atricore.idbus.console.brandservice.main.BrandingServiceException;
import com.atricore.idbus.console.brandservice.main.NoSuchBrandingException;
import com.atricore.idbus.console.brandservice.main.domain.BrandingDefinition;
import com.atricore.idbus.console.brandservice.main.internal.store.dao.BrandingDefinitionDAO;
import com.atricore.idbus.console.brandservice.main.spi.BrandingStore;

import javax.jdo.FetchPlan;
import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DbBrandingStore implements BrandingStore {

    private BrandingDefinitionDAO brandingDefinitionDAO;


    public BrandingDefinition create(BrandingDefinition def) throws BrandingServiceException {
        BrandingDefinition b = brandingDefinitionDAO.save(def);
        return brandingDefinitionDAO.detachCopy(b, FetchPlan.FETCH_SIZE_GREEDY);
    }

    public BrandingDefinition retrieve(long id) throws BrandingServiceException {
        BrandingDefinition d = brandingDefinitionDAO.findById(id);
        return brandingDefinitionDAO.detachCopy(d, FetchPlan.FETCH_SIZE_GREEDY);
    }

    public BrandingDefinition retrieveByName(String name) throws NoSuchBrandingException {
        BrandingDefinition d = brandingDefinitionDAO.findByName(name);
        return brandingDefinitionDAO.detachCopy(d, FetchPlan.FETCH_SIZE_GREEDY);
    }

    public BrandingDefinition update(BrandingDefinition def) throws BrandingServiceException {
        BrandingDefinition d = brandingDefinitionDAO.save(def);
        return brandingDefinitionDAO.detachCopy(d, FetchPlan.FETCH_SIZE_GREEDY);
    }

    public void delete(long id) throws BrandingServiceException {
        brandingDefinitionDAO.delete(id);
    }

    public Collection<BrandingDefinition> list() throws BrandingServiceException {
        Collection<BrandingDefinition> dc = brandingDefinitionDAO.findAll();
        return brandingDefinitionDAO.detachCopyAll(dc, FetchPlan.FETCH_SIZE_GREEDY);
    }


    public BrandingDefinitionDAO getBrandingDefinitionDAO() {
        return brandingDefinitionDAO;
    }

    public void setBrandingDefinitionDAO(BrandingDefinitionDAO brandingDefinitionDAO) {
        this.brandingDefinitionDAO = brandingDefinitionDAO;
    }
}
