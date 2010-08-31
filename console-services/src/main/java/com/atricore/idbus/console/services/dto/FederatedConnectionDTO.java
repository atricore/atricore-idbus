package com.atricore.idbus.console.services.dto;

/**
 * Author: Dejan Maric
 */
public class FederatedConnectionDTO extends ConnectionDTO {

    private static final long serialVersionUID = 2960169484758674128L;

    private FederatedProviderDTO roleA;

    private FederatedChannelDTO channelA;

    private FederatedProviderDTO roleB;

    private FederatedChannelDTO channelB;

    public FederatedProviderDTO getRoleA() {
        return roleA;
    }

    public void setRoleA(FederatedProviderDTO roleA) {
        this.roleA = roleA;
    }

    public FederatedChannelDTO getChannelA() {
        return channelA;
    }

    public void setChannelA(FederatedChannelDTO channelA) {
        this.channelA = channelA;
    }

    public FederatedProviderDTO getRoleB() {
        return roleB;
    }

    public void setRoleB(FederatedProviderDTO roleB) {
        this.roleB = roleB;
    }

    public FederatedChannelDTO getChannelB() {
        return channelB;
    }

    public void setChannelB(FederatedChannelDTO channelB) {
        this.channelB = channelB;
    }
}
