package com.atricore.idbus.console.brandservice.main.internal.store.dao;

import com.atricore.idbus.console.brandservice.main.NoSuchBrandingException;
import com.atricore.idbus.console.brandservice.main.domain.BrandingDefinition;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.io.IOException;
import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class BrandingDefinitionDAOImpl extends GenericDAOImpl<BrandingDefinition, Long> implements
        BrandingDefinitionDAO {

    public BrandingDefinition findByName(String name) throws NoSuchBrandingException {
        try {
            PersistenceManager pm = getPersistenceManager();
            Query query = pm.newQuery("SELECT FROM com.atricore.idbus.console.brandservice.main.domain.BrandingDefinition " +
                    " WHERE this.name == '" + name + "'");

            Collection<BrandingDefinition> brandings = (Collection<BrandingDefinition>) query.execute();

            if (brandings == null || brandings.size() < 1)
                throw new NoSuchBrandingException(name);

            if (brandings.size() > 1) // TODO : Improve exception!
                throw new RuntimeException("Too many branding definitions found for name '"+name+"'" + brandings.size());

            return brandings.iterator().next();
        } catch (NoSuchBrandingException e) {
            throw new NoSuchBrandingException(name);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
