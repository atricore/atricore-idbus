package org.atricore.idbus.kernel.main.authn;

import java.io.Serializable;
import java.security.Principal;
import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface SSOPolicy extends Serializable, Principal {

    Set<Object> getValues();
}
