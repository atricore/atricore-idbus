package org.atricore.idbus.connectors.jdoidentityvault.domain;

import java.io.Serializable;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class JDOAclEntry implements Serializable {

    private static final long serialVersionUID = 4595183658527599864L;

    private Long id;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JDOAclEntry)) return false;

        JDOAclEntry that = (JDOAclEntry) o;

        if(id == 0) return false;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}