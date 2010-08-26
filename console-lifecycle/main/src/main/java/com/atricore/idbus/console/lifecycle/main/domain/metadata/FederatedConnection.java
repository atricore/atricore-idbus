package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class FederatedConnection extends Connection {

    private static final long serialVersionUID = 2960169484758674128L;

    private FederatedProvider roleA;

    private FederatedChannel channelA;

    private FederatedProvider roleB;

    private FederatedChannel channelB;

    public FederatedProvider getRoleA() {
        return roleA;
    }

    public void setRoleA(FederatedProvider roleA) {
        this.roleA = roleA;
    }

    public FederatedChannel getChannelA() {
        return channelA;
    }

    public void setChannelA(FederatedChannel channelA) {
        this.channelA = channelA;
    }

    public FederatedProvider getRoleB() {
        return roleB;
    }

    public void setRoleB(FederatedProvider roleB) {
        this.roleB = roleB;
    }

    public FederatedChannel getChannelB() {
        return channelB;
    }

    public void setChannelB(FederatedChannel channelB) {
        this.channelB = channelB;
    }
}
