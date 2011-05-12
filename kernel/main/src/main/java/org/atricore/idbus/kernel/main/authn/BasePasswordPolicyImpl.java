package org.atricore.idbus.kernel.main.authn;

import java.security.Principal;
import java.util.Iterator;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class BasePasswordPolicyImpl implements SSOPasswordPolicy, Principal {

    private String name;

    public String getName() {
        return name;
    }

    public BasePasswordPolicyImpl(String name) {
        this.name = name;
    }

    public String toString() {
        return "PasswordPolicy[name=" + name + "]";
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
