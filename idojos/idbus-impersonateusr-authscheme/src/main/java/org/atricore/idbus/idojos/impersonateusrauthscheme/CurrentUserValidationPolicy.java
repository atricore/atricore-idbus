package org.atricore.idbus.idojos.impersonateusrauthscheme;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface CurrentUserValidationPolicy {

    boolean canImpersonate(String username, CurrentUserValidationCredential currentUserValidation) ;
}
