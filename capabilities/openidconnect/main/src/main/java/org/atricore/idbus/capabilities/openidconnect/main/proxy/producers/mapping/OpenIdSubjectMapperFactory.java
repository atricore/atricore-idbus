package org.atricore.idbus.capabilities.openidconnect.main.proxy.producers.mapping;

import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;

public interface OpenIdSubjectMapperFactory {

    OpenIdSubjectMapper newInstance(FederatedProvider p, OIDCTokens tokens);
}
