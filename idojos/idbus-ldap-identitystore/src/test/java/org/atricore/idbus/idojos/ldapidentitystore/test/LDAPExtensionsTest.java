package org.atricore.idbus.idojos.ldapidentitystore.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.ber.IAsn1Container;
import org.bouncycastle.asn1.*;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import javax.naming.AuthenticationException;
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
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LDAPExtensionsTest {

    private static final Log logger = LogFactory.getLog(LDAPExtensionsTest.class);

    private String ppolicyControlType = "1.3.6.1.4.1.42.2.27.8.5.1";

    private String securityPrincipal = "uid=user1,ou=people,dc=localhost";

    private String securityCredential = "goforit";

    private String initialContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";

    private String securityAuthentication = "simple";

    private String providerUrl = "ldap://localhost:389";

    private String securityProtocol = null;

    private String principalUidAttrName = "uid";

    private String usersCtxDN = "ou=people,dc=localhost";


    @Test
    public void testPWDPolicy() throws Exception {

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


    }


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
        //InitialLdapContext ctx = new InitialLdapContext(env, new Control[] {ppolicyControl});


        InitialLdapContext ctx = new InitialLdapContext(env, new Control[] {ppolicyControl});

        ctx.setRequestControls(new Control[] {ppolicyControl});

        ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, securityPrincipal);
        ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, securityCredential);



        try {
            ctx.reconnect(new Control[] {ppolicyControl});
            //ctx = (InitialLdapContext) ctx.newInstance(new Control[] {ppolicyControl});

            if (logger.isDebugEnabled())
                logger.debug("Logged into LDAP server, " + ctx);

            dumpCtrls(ctx);
        } catch (Exception e) {

            dumpCtrls(ctx);

            logger.error(e.getMessage(), e);

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

            for (Control ctrl :  ctx.getResponseControls()) {

                logger.debug(ctrl.getID() +
                        " (critical:" + ctrl.isCritical() + ") : ");

                if (ctrl.getEncodedValue() != null) {

                    logger.debug(ctrl.getID() +
                            " (encoded value length :" + ctrl.getEncodedValue().length + ")");



                    try {
                        ASN1InputStream is = new ASN1InputStream(ctrl.getEncodedValue());

                        ASN1Sequence seq = (DERSequence) is.readObject();

                        logger.debug(seq);

                        //ASN1Sequence seq = (ASN1Sequence) obj.getObjectParser(DERTags.SEQUENCE, false);

                        logger.debug("Sequence Size : " + seq.size());

                        ASN1Choice warnings = null;
                        ASN1TaggedObject errors = null;
                        Enumeration objs = seq.getObjects();
                        while (objs.hasMoreElements()) {
                            DERObject derObject = (DERObject) objs.nextElement();

                            if (derObject instanceof ASN1Choice) {
                                warnings = (ASN1Choice) derObject;
                            } else {
                                errors = (ASN1TaggedObject) derObject;

                                ASN1OctetString asn1o = (ASN1OctetString) errors.getObject();

                                byte[] str = asn1o.getOctets();

                                logger.debug(new String(Hex.encode(str)));
                            }

                        }


                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }


            }


        }

    }
}
