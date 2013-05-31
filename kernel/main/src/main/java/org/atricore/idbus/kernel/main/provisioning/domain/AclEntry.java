package org.atricore.idbus.kernel.main.provisioning.domain;

import java.io.Serializable;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class AclEntry implements Serializable {

    private static final long serialVersionUID = 4595183658527599864L;

    private Long id;
    private String principalNameClaim;
    private String passwordClaim;
    private AclDecisionType decision;
    private String from;
    private AclEntryStateType state;
    private String approvalToken;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrincipalNameClaim() {
        return principalNameClaim;
    }

    public void setPrincipalNameClaim(String principalNameClaim) {
        this.principalNameClaim = principalNameClaim;
    }

    public String getPasswordClaim() {
        return passwordClaim;
    }

    public void setPasswordClaim(String passwordClaim) {
        this.passwordClaim = passwordClaim;
    }


    public AclDecisionType getDecision() {
        return decision;
    }

    public void setDecision(AclDecisionType decision) {
        this.decision = decision;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public AclEntryStateType getState() {
        return state;
    }

    public void setState(AclEntryStateType state) {
        this.state = state;
    }

    public String getApprovalToken() {
        return approvalToken;
    }

    public void setApprovalToken(String approvalToken) {
        this.approvalToken = approvalToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AclEntry)) return false;

        AclEntry that = (AclEntry) o;

        if(id == 0) return false;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}