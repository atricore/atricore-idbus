package org.atricore.idbus.capabilities.sso.main.idp;

import org.atricore.idbus.capabilities.sso.main.common.ChannelConfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SPChannelConfiguration extends ChannelConfiguration {

    public static final int ROLES_MATCH_MODE_ANY = 0;

    public static final int ROLES_MATCH_MODE_ALL = 1;

    private boolean encryptAssertion;

    private String encryptAssertionAlgorithm;

    private String signatureHash;

    private int requiredRolesMatchMode = ROLES_MATCH_MODE_ANY;
    private List<String> requiredRoles = new ArrayList<String>();

    private int restrictedRolesMatchMode = ROLES_MATCH_MODE_ANY;
    private List<String> restrictedRoles = new ArrayList<String>();

    public SPChannelConfiguration(String name) {
        super(name);
    }

    public boolean isEncryptAssertion() {
        return encryptAssertion;
    }

    public void setEncryptAssertion(boolean encryptAssertion) {
        this.encryptAssertion = encryptAssertion;
    }

    public String getEncryptAssertionAlgorithm() {
        return encryptAssertionAlgorithm;
    }

    public void setEncryptAssertionAlgorithm(String encryptAssertionAlgorithm) {
        this.encryptAssertionAlgorithm = encryptAssertionAlgorithm;
    }

    public String getSignatureHash() {
        return signatureHash;
    }

    public void setSignatureHash(String signatureHash) {
        this.signatureHash = signatureHash;
    }

    public int getRequiredRolesMatchMode() {
        return requiredRolesMatchMode;
    }

    public void setRequiredRolesMatchMode(int requiredRolesMatchMode) {
        this.requiredRolesMatchMode = requiredRolesMatchMode;
    }

    public List<String> getRequiredRoles() {
        return requiredRoles;
    }

    public void setRequiredRoles(List<String> requiredRoles) {
        this.requiredRoles = requiredRoles;
    }

    public int getRestrictedRolesMatchMode() {
        return restrictedRolesMatchMode;
    }

    public void setRestrictedRolesMatchMode(int restrictedRolesMatchMode) {
        this.restrictedRolesMatchMode = restrictedRolesMatchMode;
    }

    public List<String> getRestrictedRoles() {
        return restrictedRoles;
    }

    public void setRestrictedRoles(List<String> restrictedRoles) {
        this.restrictedRoles = restrictedRoles;
    }
}
