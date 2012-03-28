package com.atricore.idbus.console.brandservice.main.internal.store.dao;

import com.atricore.idbus.console.brandservice.main.NoSuchBrandingException;
import com.atricore.idbus.console.brandservice.main.domain.BrandingDefinition;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface BrandingDefinitionDAO extends GenericDAO<BrandingDefinition, Long> {

    BrandingDefinition findByName(String name) throws NoSuchBrandingException;
}
