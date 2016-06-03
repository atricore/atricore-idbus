package org.atricore.idbus.kernel.main.provisioning.spi;

import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;

import java.util.List;

/**
 * Created by sgonzalez.
 */
public interface PasswordPolicy {

    String ENFORCMENT_STMT_NS = "urn:org:atricore:idbus:policy:provisioning:password";

    String ENFORCMENT_STMT_EMPTY = "empty";

    String ENFORCMENT_STMT_TOO_SHORT = "tooShort";

    String ENFORCMENT_STMT_TOO_LONG = "tooLong";

    String ENFORCMENT_STMT_TOO_FEW_NUMBERS = "tooFewNumbers";

    String ENFORCMENT_STMT_TOO_FEW_CHARS = "tooFewChars";

    String ENFORCMENT_STMT_TOO_FEW_SPECIAL_CHARS = "tooFewSpecialChars";

    void init();

    List<PolicyEnforcementStatement> validate(String password);
}
