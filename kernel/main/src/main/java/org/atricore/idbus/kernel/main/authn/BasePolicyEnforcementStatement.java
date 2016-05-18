package org.atricore.idbus.kernel.main.authn;

import javax.xml.namespace.QName;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class BasePolicyEnforcementStatement implements SSOPasswordPolicyEnforcement, Principal {

    private String ns;

    private String name;

    private Set<Object> values = new HashSet<Object>();

    public BasePolicyEnforcementStatement(String ns, String name) {
        this.ns = ns;
        this.name = name;
    }

    public QName getQName() {
        return new QName(ns, name);
    }

    public String getNs() {
        return ns;
    }

    public String getName() {
        return name;
    }

    public Set<Object> getValues() {
        return values;
    }

    public String toString() {
        return "Policy[name=" + name + "]";
    }

    /**
     * Compare this BaseRole's name against another BaseRole
     *
     * @return true if name equals another.getName();
     */
    public boolean equals(Object another) {
        if (!(another instanceof BaseRole))
            return false;
        String anotherName = ((BaseRole) another).getName();
        boolean equals = false;
        if (name == null)
            equals = anotherName == null;
        else
            equals = name.equals(anotherName);
        return equals;
    }

    public int hashCode() {
        return (name == null ? 0 : name.hashCode());
    }
}
