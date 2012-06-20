package com.atricore.idbus.console.services.spi;

import com.atricore.idbus.console.services.dto.branding.BrandingDefinitionDTO;

import java.util.Collection;

public interface BrandManagerAjaxService {

    Collection<BrandingDefinitionDTO> list() throws BrandingServiceException;

    BrandingDefinitionDTO create(BrandingDefinitionDTO brandingDefinitionDTO) throws BrandingServiceException;

    BrandingDefinitionDTO update(BrandingDefinitionDTO brandingDefinitionDTO) throws BrandingServiceException;

    boolean remove(long id) throws BrandingServiceException;
}
