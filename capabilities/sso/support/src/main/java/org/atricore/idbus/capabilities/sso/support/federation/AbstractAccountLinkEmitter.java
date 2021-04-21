package org.atricore.idbus.capabilities.sso.support.federation;

import org.atricore.idbus.kernel.main.federation.*;

import javax.security.auth.Subject;
import java.util.Map;

/**
 * Abstract Account Link Emitter
 */
public abstract class AbstractAccountLinkEmitter implements AccountLinkEmitter {

    protected AccountLinkBuilder newBuilder(Subject subject, String localAccount, String accountFormat, Object cxt) {
        return new DynamicAccountLinkImpl.Builder().
                        idpSubject(subject).
                        localAccount(localAccount).
                        accountFormat(accountFormat);
    }

}
