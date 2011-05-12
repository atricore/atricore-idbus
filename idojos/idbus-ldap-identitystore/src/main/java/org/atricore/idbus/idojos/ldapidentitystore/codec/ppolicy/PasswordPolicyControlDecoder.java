package org.atricore.idbus.idojos.ldapidentitystore.codec.ppolicy;

import org.apache.directory.shared.ldap.codec.controls.ControlDecoder;
import org.apache.directory.shared.ldap.message.control.Control;
import org.apache.directory.shared.asn1.Asn1Object;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.atricore.idbus.idojos.ldapidentitystore.ppolicy.PasswordPolicyResponseControl;

import java.nio.ByteBuffer;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PasswordPolicyControlDecoder extends Asn1Decoder implements ControlDecoder
{
    /** An instance of this decoder */
    private static final Asn1Decoder decoder = new Asn1Decoder();

    /**
     * Decode the paged search control
     *
     * @param controlBytes The bytes array which contains the encoded paged search
     *
     * @return A valid PagedSearch object
     *
     * @throws DecoderException If the decoding found an error
     * @throws javax.naming.NamingException It will never be throw by this method
     */
    public Asn1Object decode( byte[] controlBytes, Control control ) throws DecoderException
    {
        ByteBuffer bb = ByteBuffer.wrap( controlBytes );
        PasswordPolicyControlContainer container = new PasswordPolicyControlContainer();
        container.setPasswordPolicyResponseControl((PasswordPolicyResponseControl) control);
        decoder.decode( bb, container );
        return container.getPasswordPolicyControl();
    }
}
