package org.atricore.idbus.idojos.ldapidentitystore.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.directory.shared.ldap.codec.controls.ControlDecoder;
import org.apache.directory.shared.ldap.util.StringTools;
import org.atricore.idbus.idojos.ldapidentitystore.codec.ppolicy.PasswordPolicyControlContainer;
import org.atricore.idbus.idojos.ldapidentitystore.codec.ppolicy.PasswordPolicyResponseControl;
import org.atricore.idbus.idojos.ldapidentitystore.codec.ppolicy.PasswordPolicyErrorType;
import org.bouncycastle.asn1.*;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.BasicControl;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LDAPExtensionsTest {

    // 0x30 0x00

    // 0X30 0X03 0X81 0X01 0X00

    // 0X30 0X05 0XA0 0X03 0X81 0X01 0X01

    // 0X30 0X05 0XA0 0X03 0X81 0X01 0X02

    // 0X30 0X05 0XA0 0X03 0X80 0X01 0X32

    // 0X30 0X05 0XA0 0X03 0X80 0X01 0X2A

    private static final Log logger = LogFactory.getLog(LDAPExtensionsTest.class);

    private String ppolicyControlType = "1.3.6.1.4.1.42.2.27.8.5.1";

    private String securityPrincipal = "uid=user1,ou=people,dc=localhost";

    private String securityCredential = "user4pwd";

    private String initialContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";

    private String securityAuthentication = "simple";

    private String providerUrl = "ldap://localhost:389";

    private String securityProtocol = null;

    private String principalUidAttrName = "uid";

    private String usersCtxDN = "ou=people,dc=localhost";


    @Test
    public void testPasswordExpiredErrorControl() throws Exception {

        byte[] control = new byte[] {0x30 ,0x03,  (byte) 0x81, 0x01 ,0x00};

        PasswordPolicyControlContainer container = new PasswordPolicyControlContainer();
        container.setPasswordPolicyResponseControl(new PasswordPolicyResponseControl());
        ControlDecoder decoder = container.getPasswordPolicyControl().getDecoder();
        decoder.decode(control, container.getPasswordPolicyControl());

        PasswordPolicyResponseControl ctrl = container.getPasswordPolicyControl();

        assert ctrl.getWarningType() == null;

        assert ctrl.getErrorType() != null;
        assert ctrl.getErrorType() == PasswordPolicyErrorType.PASSWORD_EXPIRED;

    }

    @Test
    public void testAccountLockedErrorControl() throws Exception {

        byte[] control = new byte[] {0x30 ,0x03,  (byte) 0x81, 0x01 ,0x01};

        PasswordPolicyControlContainer container = new PasswordPolicyControlContainer();
        container.setPasswordPolicyResponseControl(new PasswordPolicyResponseControl());
        ControlDecoder decoder = container.getPasswordPolicyControl().getDecoder();
        decoder.decode(control, container.getPasswordPolicyControl());

        PasswordPolicyResponseControl ctrl = container.getPasswordPolicyControl();

        assert ctrl.getWarningType() == null;

        assert ctrl.getErrorType() != null;
        assert ctrl.getErrorType() == PasswordPolicyErrorType.ACCOUNT_LOCKED;

    }

    @Test
    public void testChangeAfterResetErrorControl() throws Exception {

        byte[] control = new byte[] {0x30 ,0x03,  (byte) 0x81, 0x01 ,0x02};

        PasswordPolicyControlContainer container = new PasswordPolicyControlContainer();
        container.setPasswordPolicyResponseControl(new PasswordPolicyResponseControl());
        ControlDecoder decoder = container.getPasswordPolicyControl().getDecoder();
        decoder.decode(control, container.getPasswordPolicyControl());

        PasswordPolicyResponseControl ctrl = container.getPasswordPolicyControl();

        assert ctrl.getWarningType() == null;

        assert ctrl.getErrorType() != null;
        assert ctrl.getErrorType() == PasswordPolicyErrorType.CHANGE_AFTER_RESET;

    }

    @Test
    public void testTimeBeforeExpirationWarningControl() throws Exception {

        byte[] control = new byte[] {0X30, 0X05, (byte)0XA0, 0X03, (byte)0X80, 0X01, 0X32};

        PasswordPolicyControlContainer container = new PasswordPolicyControlContainer();
        container.setPasswordPolicyResponseControl(new PasswordPolicyResponseControl());
        ControlDecoder decoder = container.getPasswordPolicyControl().getDecoder();
        decoder.decode(control, container.getPasswordPolicyControl());

        PasswordPolicyResponseControl ctrl = container.getPasswordPolicyControl();

        assert ctrl.getErrorType() == null;
        assert ctrl.getWarningType() != null;
        assert ctrl.getWarningValue() == 50;

    }

    @Test
    public void testGraceAuthNsRemaining() throws Exception {

        byte[] control = new byte[] {0X30, 0X05, (byte)0XA0, 0X03, (byte)0X81, 0X01, 0X05};

        PasswordPolicyControlContainer container = new PasswordPolicyControlContainer();
        container.setPasswordPolicyResponseControl(new PasswordPolicyResponseControl());
        ControlDecoder decoder = container.getPasswordPolicyControl().getDecoder();
        decoder.decode(control, container.getPasswordPolicyControl());

        PasswordPolicyResponseControl ctrl = container.getPasswordPolicyControl();

        assert ctrl.getErrorType() == null;
        assert ctrl.getWarningType() != null;
        assert ctrl.getWarningValue() == 5;

    }


    public void testApacheDSPPolicyDecode() throws Exception {

        try {
            InitialLdapContext ctx = createLdapInitialContext(securityPrincipal, securityCredential);

            assert ctx.getRequestControls() != null;
            assert ctx.getRequestControls().length > 0;

            for (Control ctrl : ctx.getResponseControls()) {

                assert ctrl != null;

                byte[] encValue = ctrl.getEncodedValue();

                ByteBuffer bb = ByteBuffer.allocate(encValue.length);
                bb.put(encValue);
                // bb.flip(); // ?!

                byte[] v = bb.array();

                logger.debug(ctrl.getID() +
                        " (critical:" + ctrl.isCritical() + ")" +
                        byteArrayToHexString(v));

                logger.debug("DUMP:" + StringTools.dumpBytes(v));

                PasswordPolicyControlContainer container = new PasswordPolicyControlContainer();
                container.setPasswordPolicyResponseControl(new PasswordPolicyResponseControl());
                ControlDecoder decoder = container.getPasswordPolicyControl().getDecoder();
                decoder.decode(v, container.getPasswordPolicyControl());

            }

        } catch (Exception e) {
            logger.error(e);
            throw e;
        }


    }
/*
    public void testBouncyCastlePPolicyDecode() throws Exception {

        try {
            InitialLdapContext ctx = createLdapInitialContext();

            String dn = selectUserDN(ctx, "user1");

            if (ctx.getResponseControls() != null) {
                for (Control ctrl :  ctx.getResponseControls()) {
                    logger.debug(ctrl.getID() +
                            " (critical:" + ctrl.isCritical() + ")" +
                            ctrl.getEncodedValue());
                }
            }

        } catch (Exception e) {
             logger.error(e);
            throw e;
        }


    }*/


    /**
     * Creates an InitialLdapContext by logging into the configured Ldap Server using the configured
     * username and credential.
     *
     * @return the Initial Ldap Context to be used to perform searches, etc.
     * @throws javax.naming.NamingException LDAP binding error.
     */
    protected InitialLdapContext createLdapInitialContext() throws NamingException {

        String securityPrincipal = getSecurityPrincipal();
        if (securityPrincipal == null)
            securityPrincipal = "";

        String securityCredential = getSecurityCredential();
        if (securityCredential == null)
            securityCredential = "";

        return createLdapInitialContext(securityPrincipal, securityCredential);
    }

    protected InitialLdapContext createLdapInitialContext(String securityPrincipal, String securityCredential) throws NamingException {

        Properties env = new Properties();

        env.setProperty(Context.INITIAL_CONTEXT_FACTORY, getInitialContextFactory());
        env.setProperty(Context.SECURITY_AUTHENTICATION, getSecurityAuthentication());
        env.setProperty(Context.PROVIDER_URL, getProviderUrl());
        env.setProperty(Context.SECURITY_PROTOCOL, (getSecurityProtocol() == null ? "" : getSecurityProtocol()));

        // Set defaults for key values if they are missing

        String factoryName = env.getProperty(Context.INITIAL_CONTEXT_FACTORY);
        if (factoryName == null) {
            factoryName = "com.sun.jndi.ldap.LdapCtxFactory";
            env.setProperty(Context.INITIAL_CONTEXT_FACTORY, factoryName);
        }


        String authType = env.getProperty(Context.SECURITY_AUTHENTICATION);
        if (authType == null)
            env.setProperty(Context.SECURITY_AUTHENTICATION, "DIGEST-MD5");

        //env.setProperty(Context.SECURITY_AUTHENTICATION, "simple");

        String protocol = env.getProperty(Context.SECURITY_PROTOCOL);
        String providerURL = getProviderUrl();
        // Use localhost if providerUrl not set
        if (providerURL == null)
            providerURL = "ldap://localhost:" + ((protocol != null && protocol.equals("ssl")) ? "636" : "389");

        env.setProperty(Context.PROVIDER_URL, providerURL);
//        env.setProperty(Context.SECURITY_PRINCIPAL, securityPrincipal);
//        env.put(Context.SECURITY_CREDENTIALS, securityCredential);


        // Logon into LDAP server
        if (logger.isDebugEnabled())
            logger.debug("Logging into LDAP server, env=" + env);


        Control ppolicyControl = new BasicControl(ppolicyControlType);
        InitialLdapContext ctx = new InitialLdapContext(env, new Control[]{ppolicyControl});

        ctx.setRequestControls(new Control[]{ppolicyControl});

        ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, securityPrincipal);
        ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, securityCredential);

        try {
            ctx.reconnect(new Control[]{ppolicyControl});

            if (logger.isDebugEnabled())
                logger.debug("Logged into LDAP server, " + ctx);

        } catch (Exception e) {

            logger.error("Error Logging into LDAP server " + e.getMessage(), e);

        }

        return ctx;
    }

    protected String selectUserDN(InitialLdapContext ctx, String uid) throws NamingException {

        String dn = null;


        try {
            // NamingEnumeration answer = ctx.search(usersCtxDN, matchAttrs, principalAttr);
            // This gives more control over search behavior :

            NamingEnumeration answer = ctx.search(usersCtxDN, "(&(" + principalUidAttrName + "=" + uid + "))", getSearchControls());

            while (answer.hasMore()) {
                SearchResult sr = (SearchResult) answer.next();
                Attributes attrs = sr.getAttributes();
                Attribute uidAttr = attrs.get(principalUidAttrName);

                if (uidAttr == null) {
                    logger.warn("Invalid user uid attribute '" + principalUidAttrName + "'");
                    continue;
                }

                String uidValue = uidAttr.get().toString();

                if (uidValue != null) {
                    dn = sr.getName() + "," + usersCtxDN;
                    if (logger.isDebugEnabled())
                        logger.debug("Found user '" + principalUidAttrName + "=" + uidValue + "' for user '" + uid + "' DN=" + dn);
                } else {
                    if (logger.isDebugEnabled())
                        logger.debug("User not found for user '" + uid + "'");
                }
            }
        } catch (NamingException e) {

            dumpCtrls(ctx);

            if (logger.isDebugEnabled())
                logger.debug("Failed to locate user", e);
        }

        return dn;

    }

    protected SearchControls getSearchControls() {
        SearchControls sc = new SearchControls();
        sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        return sc;
    }


    public String getSecurityPrincipal() {
        return securityPrincipal;
    }

    public void setSecurityPrincipal(String securityPrincipal) {
        this.securityPrincipal = securityPrincipal;
    }

    public String getSecurityCredential() {
        return securityCredential;
    }

    public void setSecurityCredential(String securityCredential) {
        this.securityCredential = securityCredential;
    }

    public String getInitialContextFactory() {
        return initialContextFactory;
    }

    public void setInitialContextFactory(String initialContextFactory) {
        this.initialContextFactory = initialContextFactory;
    }

    public String getSecurityAuthentication() {
        return securityAuthentication;
    }

    public void setSecurityAuthentication(String securityAuthentication) {
        this.securityAuthentication = securityAuthentication;
    }

    public String getProviderUrl() {
        return providerUrl;
    }

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }

    public String getSecurityProtocol() {
        return securityProtocol;
    }

    public void setSecurityProtocol(String securityProtocol) {
        this.securityProtocol = securityProtocol;
    }


    protected void dumpCtrls(InitialLdapContext ctx) throws NamingException {

        logger.debug("Response Controls :  ");
        if (ctx.getResponseControls() != null) {

            for (Control ctrl : ctx.getResponseControls()) {

                logger.debug(ctrl.getID() +
                        " (critical:" + ctrl.isCritical() + ") : ");

                if (ctrl.getEncodedValue() != null) {

                    logger.debug(ctrl.getID() +
                            " (encoded value length :" + ctrl.getEncodedValue().length + ")");


                    try {
                        ASN1InputStream is = new ASN1InputStream(ctrl.getEncodedValue());

                        ASN1Sequence seq = (DERSequence) is.readObject();

                        logger.debug(seq);



                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }


            }


        }

    }

    public static String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;

            sb.append(" 0x");
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }
}
