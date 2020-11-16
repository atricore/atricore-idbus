package org.atricore.idbus.capabilities.openidconnect.main.proxy.producers.mapping;

import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectAttributeType;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AzureSubjectMapper extends BaseSubjectMapper {

    public AzureSubjectMapper(FederatedProvider provider, OIDCTokens tokens) {
        super(provider, tokens);
    }

    @Override
    protected String getUsername() {
        if (claims.getClaim("upn") != null)
            return claims.getClaim("upn").toString();

        return super.getUsername();
    }

    @Override
    public Collection<? extends SubjectAttributeType> getAttributes() {
        List<SubjectAttributeType> attrs = new ArrayList<SubjectAttributeType>();
        attrs.addAll(super.getAttributes());

        buildAttribute(attrs, "email", getStringClaim("email"));
        buildAttribute(attrs, "family_name", getStringClaim("family_name"));
        buildAttribute(attrs, "given_name", getStringClaim("given_name"));
        buildAttribute(attrs, "name", getStringClaim("name"));
        buildAttribute(attrs, "ipaddr", getStringClaim("ipaddr"));
        buildAttribute(attrs, "authn_time", getDateClaim("authn_time"));
        buildAttribute(attrs, "subject", claims.getSubject());

        return attrs;
    }

    public static class AzureSubjectMapperFactory implements OpenIdSubjectMapperFactory {
        @Override
        public OpenIdSubjectMapper newInstance(FederatedProvider p, OIDCTokens tokens) {
            return new AzureSubjectMapper(p, tokens);
        }
    }

}
