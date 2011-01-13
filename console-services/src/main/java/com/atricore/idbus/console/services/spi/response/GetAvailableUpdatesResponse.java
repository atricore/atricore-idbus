package com.atricore.idbus.console.services.spi.response;

import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;

import java.util.Collection;

public class GetAvailableUpdatesResponse {

    private Collection<UpdateDescriptorType>  updateDescriptors;

    public Collection<UpdateDescriptorType> getUpdateDescriptors() {
        return updateDescriptors;
    }

    public void setUpdateDescriptors(Collection<UpdateDescriptorType> updateDescriptors) {
        this.updateDescriptors = updateDescriptors;
    }
}
