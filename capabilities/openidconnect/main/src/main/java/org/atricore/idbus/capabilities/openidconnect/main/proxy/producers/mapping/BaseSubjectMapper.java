package org.atricore.idbus.capabilities.openidconnect.main.proxy.producers.mapping;

import com.nimbusds.openid.connect.sdk.token.OIDCTokens;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.core.NameIDFormat;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectAttributeType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectNameIDType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectType;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class BaseSubjectMapper extends OpenIdSubjectMapper {

    private static final Log logger = LogFactory.getLog(BaseSubjectMapper.class);

    public BaseSubjectMapper(FederatedProvider provider,
                             OIDCTokens tokens) {
        super(provider, tokens );
    }

    @Override
    public SubjectType toSubject() {

        SubjectType subject = new SubjectType();

        SubjectNameIDType a = new SubjectNameIDType();
        a.setName(getUsername());
        a.setFormat(getNameIdFormat());
        a.setLocalName(getUsername());
        a.setNameQualifier(provider.getName().toUpperCase());
        a.setLocalNameQualifier(provider.getName().toUpperCase());

        subject.getAbstractPrincipal().add(a);

        return subject;
    }

    @Override
    public Collection<? extends SubjectAttributeType> getAttributes() {

        List<SubjectAttributeType> attrs = new ArrayList<SubjectAttributeType>();
        SubjectAttributeType accessTokenAttr = new SubjectAttributeType();
        accessTokenAttr.setName("accessToken");
        accessTokenAttr.setValue(tokens.getAccessToken().toJSONString());
        attrs.add(accessTokenAttr);

        SubjectAttributeType authnCtxClassAttr = new SubjectAttributeType();
        authnCtxClassAttr.setName("authnCtxClass");
        authnCtxClassAttr.setValue(AuthnCtxClass.PPT_AUTHN_CTX.getValue());
        attrs.add(authnCtxClassAttr);
        return attrs;
    }

    protected String getNameIdFormat() {
        return NameIDFormat.EMAIL.getValue();
    }

    protected String getUsername() {
        return claims.getSubject();
    }

    protected String getStringClaim(String name) {
        try {
            return claims.getStringClaim(name);
        } catch (ParseException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    protected Date getDateClaim(String name) {
        try {
            return claims.getDateClaim(name);
        } catch (ParseException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    protected Boolean getBooleanClaim(String name) {
        try {
            return claims.getBooleanClaim(name);
        } catch (ParseException e) {
            logger.error(e.getMessage());
            return null;
        }
    }


    protected Double getDoubleClaim(String name) {
        try {
            return claims.getDoubleClaim(name);
        } catch (ParseException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    protected void buildAttribute(List<SubjectAttributeType> attrs, String name, String value) {
        if (value != null) {
            attrs.add(buildAttribute(name, value));
        }
    }

    protected void buildAttribute(List<SubjectAttributeType> attrs, String name, Date value) {
        if (value != null) {
            attrs.add(buildAttribute(name, value.toGMTString()));
        }
    }

    protected void buildAttribute(List<SubjectAttributeType> attrs, String name, Long value) {
        if (value != null) {
            attrs.add(buildAttribute(name, value.toString()));
        }
    }

    protected void buildAttribute(List<SubjectAttributeType> attrs, String name, Double value) {
        if (value != null) {
            attrs.add(buildAttribute(name, value.toString()));
        }
    }

    protected void buildAttribute(List<SubjectAttributeType> attrs, String name, Boolean value) {
        if (value != null) {
            attrs.add(buildAttribute(name, value.toString()));
        }
    }



    protected SubjectAttributeType buildAttribute(String name, String value) {
        SubjectAttributeType attr = new SubjectAttributeType();
        attr.setName(name);
        attr.setValue(value);
        return attr;
    }


}
