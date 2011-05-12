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

        byte[] control = new byte[] {
                0x30 , // TAG     : SEQUENCE
                0x03,  // LENGTH  : 3
                       // VALUE :
                    (byte) 0x81,   // TAG    : ERROR
                    0x01 ,         // LENGTH : 1
                    0x00};         // VALUE  : 0

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


        byte[] control = new byte[] {
                0x30 , // TAG     : SEQUENCE
                0x03,  // LENGTH  : 3
                       // VALUE :
                    (byte) 0x81,   // TAG    : ERROR
                    0x01 ,         // LENGTH : 1
                    0x01};         // VALUE  : 1

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
    public void testGraceAuthNsRemainingWarningControl() throws Exception {

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

    @Test
    public void testNoControl() throws Exception {
        byte[] control = new byte[] {0X30, 0X00};

        PasswordPolicyControlContainer container = new PasswordPolicyControlContainer();
        container.setPasswordPolicyResponseControl(new PasswordPolicyResponseControl());
        ControlDecoder decoder = container.getPasswordPolicyControl().getDecoder();
        decoder.decode(control, container.getPasswordPolicyControl());

        PasswordPolicyResponseControl ctrl = container.getPasswordPolicyControl();

        assert ctrl.getErrorType() == null;
        assert ctrl.getWarningType() == null;

    }



}
