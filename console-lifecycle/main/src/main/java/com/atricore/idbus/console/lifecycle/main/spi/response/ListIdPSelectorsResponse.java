package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.EntitySelectionStrategy;

import java.util.ArrayList;
import java.util.List;

/**

 */
public class ListIdPSelectorsResponse {

    private List<EntitySelectionStrategy> selectionStrategies;

    public List<EntitySelectionStrategy> getSelectionStrategies() {
        if(selectionStrategies == null){
            selectionStrategies = new ArrayList<EntitySelectionStrategy>();
        }
        return selectionStrategies;
    }

    public void setSelectionStrategies(List<EntitySelectionStrategy> identitySources) {
        this.selectionStrategies = identitySources;
    }
}
