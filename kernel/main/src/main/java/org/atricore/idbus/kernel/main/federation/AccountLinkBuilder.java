package org.atricore.idbus.kernel.main.federation;

import javax.security.auth.Subject;
import java.util.Map;

public interface AccountLinkBuilder<T extends AccountLinkBuilder> {

    T idpSubject(Subject subject);

    T localAccount(String account);

    T accountFormat(String accountFormat);

    T enabled(boolean e);

    T deleted(boolean e);

    T property(String property, String name);

    T properties(Map<String, String> props);

    AccountLink build();

}
