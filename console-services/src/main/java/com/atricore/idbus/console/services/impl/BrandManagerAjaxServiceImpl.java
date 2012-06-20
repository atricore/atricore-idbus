package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.brandservice.main.domain.BrandingDefinition;
import com.atricore.idbus.console.brandservice.main.domain.BuiltInBrandingDefinition;
import com.atricore.idbus.console.brandservice.main.domain.CustomBrandingDefinition;
import com.atricore.idbus.console.brandservice.main.spi.BrandManager;
import com.atricore.idbus.console.services.dto.branding.BrandingDefinitionDTO;
import com.atricore.idbus.console.services.dto.branding.BrandingTypeDTO;
import com.atricore.idbus.console.services.dto.branding.BuiltInBrandingDefinitionDTO;
import com.atricore.idbus.console.services.dto.branding.CustomBrandingDefinitionDTO;
import com.atricore.idbus.console.services.spi.BrandManagerAjaxService;
import com.atricore.idbus.console.services.spi.BrandingServiceException;
import org.dozer.DozerBeanMapper;

import java.util.ArrayList;
import java.util.Collection;

public class BrandManagerAjaxServiceImpl implements BrandManagerAjaxService {
    
    private BrandManager brandManager;

    private DozerBeanMapper dozerMapper;

    public void setBrandManager(BrandManager brandManager) {
        this.brandManager = brandManager;
    }

    public void setDozerMapper(DozerBeanMapper dozerMapper) {
        this.dozerMapper = dozerMapper;
    }

    public Collection<BrandingDefinitionDTO> list() throws BrandingServiceException {
        Collection<BrandingDefinition> brandingDefinitions;
        try {
            brandingDefinitions = brandManager.list();
        } catch (com.atricore.idbus.console.brandservice.main.BrandingServiceException e) {
            throw new BrandingServiceException(e);
        }

        Collection<BrandingDefinitionDTO> brandingDefinitionDTOs = new ArrayList<BrandingDefinitionDTO>();
        for (BrandingDefinition bd : brandingDefinitions) {
            BrandingDefinitionDTO bdDTO = null;
            if (bd instanceof BuiltInBrandingDefinition) {
                bdDTO = new BuiltInBrandingDefinitionDTO();
            } else if (bd instanceof CustomBrandingDefinition) {
                bdDTO = new CustomBrandingDefinitionDTO();
            }
            if (bd != null) {
                dozerMapper.map(bd, bdDTO);
                brandingDefinitionDTOs.add(bdDTO);
            }
        }

        return brandingDefinitionDTOs;
    }

    public BrandingDefinitionDTO create(BrandingDefinitionDTO brandingDefinitionDTO) throws BrandingServiceException {
        try {
            BrandingDefinition brandingDefinition = null;
            if (brandingDefinitionDTO.getType() == BrandingTypeDTO.CUSTOM) {
                brandingDefinition = new CustomBrandingDefinition();
            }
            dozerMapper.map(brandingDefinitionDTO, brandingDefinition);
            brandingDefinition = brandManager.create(brandingDefinition);
            dozerMapper.map(brandingDefinition, brandingDefinitionDTO);
            return brandingDefinitionDTO;
        } catch (com.atricore.idbus.console.brandservice.main.BrandingServiceException e) {
            throw new BrandingServiceException(e);
        }
    }

    public BrandingDefinitionDTO update(BrandingDefinitionDTO brandingDefinitionDTO) throws BrandingServiceException {
        try {
            BrandingDefinition brandingDefinition = brandManager.lookup(brandingDefinitionDTO.getId());
            dozerMapper.map(brandingDefinitionDTO, brandingDefinition);
            brandingDefinition = brandManager.update(brandingDefinition);
            dozerMapper.map(brandingDefinition, brandingDefinitionDTO);
            return brandingDefinitionDTO;
        } catch (com.atricore.idbus.console.brandservice.main.BrandingServiceException e) {
            throw new BrandingServiceException(e);
        }
    }

    public boolean remove(long id) throws BrandingServiceException {
        try {
            brandManager.delete(id);
            return true;
        } catch (com.atricore.idbus.console.brandservice.main.BrandingServiceException e) {
            throw new BrandingServiceException(e);
        }
    }
}
