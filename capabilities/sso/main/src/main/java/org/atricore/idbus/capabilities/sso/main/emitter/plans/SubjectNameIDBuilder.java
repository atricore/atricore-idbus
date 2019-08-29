package org.atricore.idbus.capabilities.sso.main.emitter.plans;

import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.protocol.NameIDPolicyType;

import javax.security.auth.Subject;

/**
 * TODO : This is a work-around, we need to use AccountLinkage, AccountLinkLifecycle and AccountLinkEmitter components instead of this!
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface SubjectNameIDBuilder {


    boolean supportsPolicy(String nameIDPolicy);

    boolean supportsPolicy(NameIDPolicyType nameIDPolicy);

    NameIDType buildNameID(NameIDPolicyType nameIDPolicy, Subject s);
}
