package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.EntitySelectionStrategyDTO;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class ListIdPSelectorsResponse extends AbstractManagementResponse {

    private List<EntitySelectionStrategyDTO> selectionStrategies;

    public List<EntitySelectionStrategyDTO> getSelectionStrategies() {
        if(selectionStrategies == null){
            selectionStrategies = new ArrayList<EntitySelectionStrategyDTO>();
        }
        return selectionStrategies;
    }

    public void setSelectionStrategies(List<EntitySelectionStrategyDTO> selectionStrategies) {
        this.selectionStrategies = selectionStrategies;
    }
}
