package org.atricore.idbus.kernel.main.authn;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.security.Principal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 */
public class BasePolicyEnforcementStatement implements PolicyEnforcementStatement, Principal {

    private String ns;

    private String name;

    private Set<Object> values = new HashSet<Object>();

    public BasePolicyEnforcementStatement(String ns, String name) {
        this.ns = ns;
        this.name = name;
    }

    public String getDetails() {
        return "";
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasePolicyEnforcementStatement that = (BasePolicyEnforcementStatement) o;
        return Objects.equals(ns, that.ns) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ns, name);
    }
}
