package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class FederatedConnection extends Connection {

    private static final long serialVersionUID = 2960169484758674128L;

    private Provider roleA;

    private Channel channelA;

    private Provider roleB;

    private Channel channelB;

    public Provider getRoleA() {
        return roleA;
    }

    public void setRoleA(Provider roleA) {
        this.roleA = roleA;
    }

    public Channel getChannelA() {
        return channelA;
    }

    public void setChannelA(Channel channelA) {
        this.channelA = channelA;
    }

    public Provider getRoleB() {
        return roleB;
    }

    public void setRoleB(Provider roleB) {
        this.roleB = roleB;
    }

    public Channel getChannelB() {
        return channelB;
    }

    public void setChannelB(Channel channelB) {
        this.channelB = channelB;
    }
}
