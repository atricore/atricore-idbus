package org.atricore.idbus.kernel.main.authn;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.security.Principal;
import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface SSOPolicyEnforcementStatement extends Serializable, Principal {

    QName getQName() ;

    String getNs();

    String getName();

    Set<Object> getValues();
}
