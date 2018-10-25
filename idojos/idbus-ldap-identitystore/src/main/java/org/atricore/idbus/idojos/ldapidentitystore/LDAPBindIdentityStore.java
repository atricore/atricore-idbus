/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.atricore.idbus.idojos.ldapidentitystore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.ldap.codec.controls.ControlDecoder;
import org.atricore.idbus.idojos.ldapidentitystore.codec.ppolicy.PasswordPolicyControlContainer;
import org.atricore.idbus.idojos.ldapidentitystore.codec.ppolicy.PasswordPolicyResponseControl;
import org.atricore.idbus.kernel.main.authn.*;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.atricore.idbus.kernel.main.store.SimpleUserKey;
import org.atricore.idbus.kernel.main.store.UserKey;
import org.atricore.idbus.kernel.main.store.exceptions.CredentialsPolicyVerificationException;
import org.atricore.idbus.kernel.main.store.exceptions.InvalidCredentialsException;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;
import org.atricore.idbus.kernel.main.store.identity.BindContext;
import org.atricore.idbus.kernel.main.store.identity.BindableCredentialStore;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.BasicControl;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * An implementation of an Identity and Credential Store which obtains credential, user and
 * role information from an LDAP server using JNDI, based on the configuration properties.
 * <p/>
 * It allows to set whatever options your LDAP JNDI provider supports your Gateway
 * configuration file.
 * Examples of standard property names are:
 * <ul>
 * <li><code>initialContextFactory = "java.naming.factory.initial"</code>
 * <li><code>securityProtocol = "java.naming.security.protocol"</code>
 * <li><code>providerUrl = "java.naming.provider.url"</code>
 * <li><code>securityAuthentication = "java.naming.security.authentication"</code>
 * </ul>
 * <p/>
 * This store implementation is both an Identity Store and Credential Store.
 * Since in JOSSO the authentication of the user is left to the configured Authentication Scheme,
 * this store implementation cannot delegate user identity assertion by binding to the
 * LDAP server. For that reason it retrieves the required credentials from the directory
 * leaving the authentication procedure to the configured Authentication Scheme.
 * The store must be supplied with the configuratoin parameters so that it can retrieve user
 * identity information.
 * <p/>
 * <p/>
 * Additional component properties include:
 * <ul>
 * <li>securityPrincipal: the DN of the user to be used to bind to the LDAP Server
 * <li>securityCredential: the securityPrincipal password to be used for binding to the
 * LDAP Server.
 * <li>securityAuthentication: the security level to be used with the LDAP Server session.
 * Its value is one of the following strings:
 * "none", "simple", "strong".
 * If not set, "simple" will be used.
 * <li>usersCtxDN : the fixed distinguished name to the context to search for user accounts.
 * <li>principalUidAttributeID: the name of the attribute that contains the user login name.
 * This is used to locate the user.
 * <li>rolesCtxDN : The fixed distinguished name to the context to search for user roles.
 * <li>uidAttributeID: the name of the attribute that, in the object containing the user roles,
 * references role members. The attribute value should be the DN of the user associated with the
 * role. This is used to locate the user roles.
 * <li>roleAttributeID : The name of the attribute that contains the role name
 * <li>credentialQueryString : The query string to obtain user credentials. It should have the
 * following format : user_attribute_name=credential_attribute_name,...
 * For example :
 * uid=username,userPassword=password
 * <li>userPropertiesQueryString : The query string to obtain user properties. It should have
 * the following format : ldap_attribute_name=user_attribute_name,...
 * For example :
 * mail=mail,cn=description
 * </ul>
 * A sample LDAP Identity Store configuration :
 * <p/>
 * <pre>
 * &lt;sso-identity-store&gt;
 * &lt;class&gt;org.josso.gateway.identity.service.store.ldap.LDAPBindIdentityStore&lt;/class&gt;
 * &lt;initialContextFactory&gt;com.sun.jndi.ldap.LdapCtxFactory&lt;/initialContextFactory&gt;
 * &lt;providerUrl&gt;ldap://localhost&lt;/providerUrl&gt;
 * &lt;securityPrincipal&gt;cn=Manager\,dc=my-domain\,dc=com&lt;/securityPrincipal&gt;
 * &lt;securityCredential&gt;secret&lt;/securityCredential&gt;
 * &lt;securityAuthentication&gt;simple&lt;/securityAuthentication&gt;
 * &lt;usersCtxDN&gt;ou=People\,dc=my-domain\,dc=com&lt;/usersCtxDN&gt;
 * &lt;principalUidAttributeID&gt;uid&lt;/principalUidAttributeID&gt;
 * &lt;rolesCtxDN&gt;ou=Roles\,dc=my-domain\,dc=com&lt;/rolesCtxDN&gt;
 * &lt;uidAttributeID&gt;uniquemember&lt;/uidAttributeID&gt;
 * &lt;roleAttributeID&gt;cn&lt;/roleAttributeID&gt;
 * &lt;credentialQueryString&gt;uid=username\,userPassword=password&lt;/credentialQueryString&gt;
 * &lt;userPropertiesQueryString&gt;mail=mail\,cn=description&lt;/userPropertiesQueryString&gt;
 * &lt;/sso-identity-store&gt;
 * </pre>
 * <p/>
 * A sample LDAP Credential Store configuration :
 * <p/>
 * <pre>
 * &lt;credential-store&gt;
 * &lt;class&gt;org.josso.gateway.identity.service.store.ldap.LDAPBindIdentityStore&lt;/class&gt;
 * &lt;initialContextFactory&gt;com.sun.jndi.ldap.LdapCtxFactory&lt;/initialContextFactory&gt;
 * &lt;providerUrl&gt;ldap://localhost&lt;/providerUrl&gt;
 * &lt;securityPrincipal&gt;cn=Manager\,dc=my-domain\,dc=com&lt;/securityPrincipal&gt;
 * &lt;securityCredential&gt;secret&lt;/securityCredential&gt;
 * &lt;securityAuthentication&gt;simple&lt;/securityAuthentication&gt;
 * &lt;usersCtxDN&gt;ou=People\,dc=my-domain\,dc=com&lt;/usersCtxDN&gt;
 * &lt;principalUidAttributeID&gt;uid&lt;/principalUidAttributeID&gt;
 * &lt;rolesCtxDN&gt;ou=Roles\,dc=my-domain\,dc=com&lt;/rolesCtxDN&gt;
 * &lt;uidAttributeID&gt;uniquemember&lt;/uidAttributeID&gt;
 * &lt;roleAttributeID&gt;cn&lt;/roleAttributeID&gt;
 * &lt;credentialQueryString&gt;uid=username\,userPassword=password&lt;/credentialQueryString&gt;
 * &lt;userPropertiesQueryString&gt;mail=mail\,cn=description&lt;/userPropertiesQueryString&gt;
 * &lt;/credential-store&gt;
 * </pre>
 *
 * @org.apache.xbean.XBean element="ldap-bind-store"
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version CVS $Id: LDAPBindIdentityStore.java 1040 2009-03-05 00:56:52Z gbrigand $
 */

public class LDAPBindIdentityStore extends LDAPIdentityStore implements BindableCredentialStore {

    private static final Log logger = LogFactory.getLog(LDAPBindIdentityStore.class);

    private boolean validateBindWithSearch = false;

    private boolean passwordPolicySupport = false;

    private boolean updatePasswordEnabled = false;

    private LDAPVendor ldapVendor;
    private Map<LDAPVendor, Map<String, String>> ldapVendorErrorCodes;

    public boolean isValidateBindWithSearch() {
        return this.validateBindWithSearch;
    }

    public void setValidateBindWithSearch(boolean validateBindWithSearch) {
        this.validateBindWithSearch = validateBindWithSearch;
    }

    public boolean isPasswordPolicySupport() {
        return passwordPolicySupport;
    }

    public void setPasswordPolicySupport(boolean passwordPolicySupport) {
        this.passwordPolicySupport = passwordPolicySupport;
    }

    public LDAPVendor getLdapVendor() {
        return ldapVendor;
    }

    public void setLdapVendor(LDAPVendor ldapVendor) {
        this.ldapVendor = ldapVendor;
    }

    public Map<LDAPVendor, Map<String, String>> getLdapVendorErrorCodes() {
        return ldapVendorErrorCodes;
    }

    public void setLdapVendorErrorCodes(Map<LDAPVendor, Map<String, String>> ldapVendorErrorCodes) {
        this.ldapVendorErrorCodes = ldapVendorErrorCodes;
    }

    public LDAPBindIdentityStore() {
        super();

        ldapVendor = LDAPVendor.APACHE;

        // Initialize LDAP Vendor error code mappings
        ldapVendorErrorCodes = new HashMap<LDAPVendor, Map<String, String>>();

        Map<String, String> apacheErrorCodes = new HashMap<String, String>();
        apacheErrorCodes.put(CredentialsPolicyVerificationException.PASSWORD_IN_HISTORY, "password history");
        apacheErrorCodes.put(CredentialsPolicyVerificationException.PASSWORD_TOO_SHORT, "have a minimum of");
        apacheErrorCodes.put(CredentialsPolicyVerificationException.PASSWORD_CONTAINS_USERNAME, "parts of the username");
        ldapVendorErrorCodes.put(LDAPVendor.APACHE, apacheErrorCodes);
    }

// ----------------------------------------------------- CredentialStore Methods

    /**
     * This store performs a bind to the configured LDAP server and closes the connection immediately.
     * If the connection fails, an exception is thrown, otherwise this method returns silentrly
     *
     * @return true if the bind is successful
     */
    public boolean bind(String username, String password, BindContext bindCtx) throws SSOAuthenticationException {

        String dn = null;
        boolean error = false;

        try {

            // Try to resolve a user DN using the username
            dn = selectUserDN(username);
            if (dn == null || "".equals(dn)) {
                if (logger.isDebugEnabled())
                    logger.debug("No DN found for user : " + username);

                if (validateBindWithSearch) {
                    bindCtx.addPolicyEnforcementStatement(new AccountNotFoundAuthnPolicy(null));
                    return false;
                }

                // Infer a DN, this only works if all users are stored under the configured context DN for users (ONELEVEL)
                dn = getPrincipalUidAttributeID() +  "=" + username + "," + getUsersCtxDN();
            }

            if (logger.isDebugEnabled())
                logger.debug("user dn = [" + dn + "]");

            // Create context without binding!
            InitialLdapContext ctx = this.createLdapInitialContext(null, null);
            Control[] ldapControls = null;

            try {

                ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, dn);
                ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);

                if (isPasswordPolicySupport()) {
                    // Configure request control for password policy:
                    ctx.reconnect(new Control[] { new BasicControl(PasswordPolicyResponseControl.CONTROL_OID) } );
                } else {
                    ctx.reconnect(new Control[] {});
                }

                // Get response controls from reconnect BEFORE dn search, or they're lost
                ldapControls = ctx.getResponseControls();

                // Bind to LDAP an check for authentication warning/errors reported in password policy control:
                if (validateBindWithSearch) {
                    selectUserDN(ctx, username);

                    // Perhaps controls are not send during reconnet, try to get them now
                    if (ldapControls == null || ldapControls.length == 0)
                        ldapControls = ctx.getResponseControls();
                }

                if (logger.isTraceEnabled())
                    logger.trace("LDAP Bind with user credentials succeeded");

            } catch (AuthenticationException e) {

                if (logger.isDebugEnabled())
                    logger.debug("LDAP Bind Authentication error : " + e.getMessage(), e);

                if (dn == null)
                    bindCtx.addPolicyEnforcementStatement(new AccountNotFoundAuthnPolicy(null));
                else
                    bindCtx.addPolicyEnforcementStatement(new InvalidPasswordAuthnPolicy(null));

                return false;

            } finally {

                if (isPasswordPolicySupport()) {

                    // If an exception occurred, controls are not retrieved yet
                    if (ldapControls == null || ldapControls.length == 0)
                        ldapControls = ctx.getResponseControls();

                    // Check password policy LDAP Control
                    PasswordPolicyResponseControl ppolicyCtrl = decodePasswordPolicyControl(ldapControls);
                    if (ppolicyCtrl != null)
                        addPasswordPolicyToBindCtx(ppolicyCtrl, bindCtx);

                    // Check for errors
                    for (PolicyEnforcementStatement policyStatement : bindCtx.getSSOPolicies()) {
                        if (policyStatement instanceof PasswordPolicyEnforcementError) {
                            error = true;
                            break;
                        }
                    }
                }

                ctx.close();
            }

            return !error;


        } catch (Exception e) {
            throw new SSOAuthenticationException("Cannot bind as user : " + username + " ["+dn+"]" + e.getMessage(), e);
        }

    }

    private void addPasswordPolicyToBindCtx(PasswordPolicyResponseControl ppolicyCtrl, BindContext bindCtx) {

        if (ppolicyCtrl.getWarningType() != null) {

            PasswordPolicyWarningType type = null;
            int value = 0;

            switch(ppolicyCtrl.getWarningType()) {
                case TIME_BEFORE_EXPIRATION:
                    type = PasswordPolicyWarningType.TIME_BEFORE_EXPIRATION;
                    value = ppolicyCtrl.getWarningValue();
                    break;
                case GRACE_AUTHNS_REMAINING:
                    type = PasswordPolicyWarningType.GRACE_AUTHNS_REMAINING;
                    value = ppolicyCtrl.getWarningValue() - 1;
                    break;
                default:
                    logger.error("Unsupported LDAP Password Policy Warning Type : " + ppolicyCtrl.getWarningType().name());
            }

            PasswordPolicyEnforcementWarning warningPPolicy = new PasswordPolicyEnforcementWarning(type);
            warningPPolicy.getValues().add(value);

            bindCtx.addPasswordPolicyMessages(warningPPolicy);
        }

        if (ppolicyCtrl.getErrorType() != null) {
            PasswordPolicyErrorType type = null;

            switch(ppolicyCtrl.getErrorType()) {
                case PASSWORD_EXPIRED:
                    type = PasswordPolicyErrorType.PASSWORD_EXPIRED;
                    break;
                case ACCOUNT_LOCKED:
                    type = PasswordPolicyErrorType.ACCOUNT_LOCKED;
                    break;
                case CHANGE_AFTER_RESET:
                    type = PasswordPolicyErrorType.CHANGE_PASSWORD_REQUIRED;
                    break;
                default:
                    logger.error("Unsupported LDAP Password Policy Error Type : " + ppolicyCtrl.getErrorType().name());
            }

            PasswordPolicyEnforcementError errorPPolicy = new PasswordPolicyEnforcementError(type);
            bindCtx.addPasswordPolicyMessages(errorPPolicy);
        }

    }

    protected PasswordPolicyResponseControl decodePasswordPolicyControl(Control[] ldapControls) throws DecoderException {

        if (ldapControls == null)
            return null;

        for (Control ldapControl : ldapControls) {

            if (ldapControl.getID().equals(PasswordPolicyResponseControl.CONTROL_OID)) {

                PasswordPolicyControlContainer container = new PasswordPolicyControlContainer();
                container.setPasswordPolicyResponseControl(new PasswordPolicyResponseControl());
                ControlDecoder decoder = container.getPasswordPolicyControl().getDecoder();
                if (ldapControl.getEncodedValue() != null) {
                    decoder.decode(ldapControl.getEncodedValue(), container.getPasswordPolicyControl());

                    PasswordPolicyResponseControl ctrl = container.getPasswordPolicyControl();

                    if (logger.isDebugEnabled())
                        logger.debug("Password Policy Control : " + ctrl.toString());

                    return ctrl;
                } else {
                    return null;
                }
            }
        }

        logger.warn("No LDAP Control found for " + PasswordPolicyResponseControl.CONTROL_OID);

        return null;
    }

    @Override
    public boolean isUpdatePasswordEnabled() {
        return updatePasswordEnabled;
    }

    public void setUpdatePasswordEnabled(boolean updatePasswordEnabled) {
        this.updatePasswordEnabled = updatePasswordEnabled;
    }

    @Override
    public void updatePassword(UserKey key, String currentPassword, String newPassword) throws SSOIdentityException {
        if (!(key instanceof SimpleUserKey)) {
            throw new SSOIdentityException("Unsupported key type : " + key.getClass().getName());
        }

        InitialLdapContext ctx = null;

        try {
            String dn = selectUserDN(((SimpleUserKey) key).getId());

            ctx = this.createLdapInitialContext(dn, currentPassword);

            // Set new password
            String userPasswordAttribute = getUpdateableCredentialAttribute();
            if (userPasswordAttribute == null || userPasswordAttribute.trim().equals("")) {
                userPasswordAttribute = "userPassword";
            }
            Attributes attrs = new BasicAttributes();
            Attribute pwdAttr = new BasicAttribute(userPasswordAttribute, newPassword);
            attrs.put(pwdAttr);
            ctx.modifyAttributes(dn, InitialLdapContext.REPLACE_ATTRIBUTE, attrs);

            // Remove pwdReset attribute
            ctx = this.createLdapInitialContext();
            Attributes attrs1 = new BasicAttributes();
            Attribute pwdResetAttr = new BasicAttribute("pwdReset", false);
            attrs1.put(pwdResetAttr);
            ctx.modifyAttributes(dn, InitialLdapContext.REPLACE_ATTRIBUTE, attrs1);

        } catch (AuthenticationException e) {
            // Invalid old password value
            throw new InvalidCredentialsException(e.getMessage(), e);
        } catch (InvalidAttributeValueException e) {
            // Invalid new password value
            throw buildCredentialsPolicyVerificationException(e);
        } catch (NamingException e) {
            throw new SSOIdentityException(e);
        } finally {
            try {
                if (ctx != null)
                    ctx.close();
            } catch (NamingException e) {
                throw new SSOIdentityException(e);
            }
        }
    }

    protected CredentialsPolicyVerificationException buildCredentialsPolicyVerificationException(InvalidAttributeValueException e) {
        String errorMessage = e.getMessage();
        List<String> errorCodes = new ArrayList<String>();

        Map<String, String> ldapVendorErrorCodeMappings = ldapVendorErrorCodes.get(ldapVendor);
        for (String errorCode : ldapVendorErrorCodeMappings.keySet()) {
            if (errorMessage.contains(ldapVendorErrorCodeMappings.get(errorCode))) {
                errorCodes.add(errorCode);
            }
        }

        if (errorCodes.size() == 0) {
            errorCodes.add(CredentialsPolicyVerificationException.INVALID_PASSWORD);
        }

        return new CredentialsPolicyVerificationException(errorCodes.toArray(new String[errorCodes.size()]), e.getMessage(), e);
    }
}
