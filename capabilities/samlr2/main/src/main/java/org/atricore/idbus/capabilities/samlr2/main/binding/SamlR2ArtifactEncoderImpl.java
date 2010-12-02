package org.atricore.idbus.capabilities.samlr2.main.binding;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.SamlR2Exception;

import java.nio.ByteBuffer;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2ArtifactEncoderImpl implements SamlArtifactEncoder {

    private static final Log logger = LogFactory.getLog(SamlR2ArtifactEncoderImpl.class);

    public String encode(SamlArtifact artifact) {
        // Use SAML 2.0 Recommended format

        byte[] typeCodeBin = toBin(artifact.getType());
        byte[] endpointIdxBin = toBin(artifact.getEndpointIndex());

        // TODO : Make sure that each byte[] is exactly 20 bytes length.


        byte[] messageHandleBin = toBin(artifact.getMessageHandle(), 20);
        byte[] sourceIdBin = toBin(artifact.getSourceID(), 20);

        ByteBuffer bf = ByteBuffer.allocate(typeCodeBin.length +
                endpointIdxBin.length +
                messageHandleBin.length +
                sourceIdBin.length);

        bf.put(typeCodeBin);
        bf.put(endpointIdxBin);
        bf.put(messageHandleBin);
        bf.put(sourceIdBin);

        return new String(Base64.encodeBase64(bf.array()));

    }

    public SamlArtifact decode(String samlArtStr) throws SamlR2Exception {
        byte[] samlArtBin = Base64.decodeBase64(samlArtStr.getBytes());

        if (samlArtBin.length < 5)
            throw new SamlR2Exception("Invalid SamlArtifact length " + samlArtBin.length);

        byte[] typeCodeBin = new byte[2];
        typeCodeBin[0] = samlArtBin[0];
        typeCodeBin[1] = samlArtBin[1];
        int typeCode = toInt(typeCodeBin);

        byte[] endpointIdxBin = new byte[2];
        endpointIdxBin[0] = samlArtBin[3];
        endpointIdxBin[1] = samlArtBin[4];
        int endpointIndex = toInt(endpointIdxBin);

        String sourceId = null;
        String messageHandle = null;

        byte[] remainingArtBin = copyOfRange(samlArtBin, 4, samlArtBin.length);
        if (remainingArtBin.length == 40) {
            // Assume SAML 2.0 Recommended remaining
            if (logger.isTraceEnabled())
                logger.trace("Assuming SAML 2.0 Recommended artifact format");

            byte[] sourceIdBin = copyOfRange(remainingArtBin, 0, 20);
            sourceId = toString(sourceIdBin);

            byte[] messageHandleBin = copyOfRange(remainingArtBin, -20, 40);
            messageHandle = toString(messageHandleBin);

        } else {
            if (logger.isTraceEnabled())
                logger.trace("Assuming non SAML 2.0 artifact format");

            messageHandle = toString(remainingArtBin);
        }

        return new SamlArtifact(typeCode, endpointIndex, sourceId, messageHandle);


    }

    /**
     * Only works for ints between 0 and 65535
     */
    protected int toInt(byte[] b) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.put((byte) 0x00);
        bb.put((byte) 0x00);
        bb.put(b[0]);
        bb.put(b[1]);

        return bb.getInt(0);


    }

    /**
     * It turns an int in a bin array (only uses two bytes!)
     * Only works for ints between 0 and 65535
     */
    protected byte[] toBin(int i) {

        byte[] bytes = ByteBuffer.allocate(4).putInt(i).array();
        return copyOfRange(bytes, 2, 4);
    }

    protected byte[] toBin(String value, int length) {

        byte[] b = value.getBytes();
        int pad = length - b.length;

        ByteBuffer bf = ByteBuffer.allocate(length);

        for (int i = 0; i < pad; i++) {
            System.out.println(i);
            bf.put((byte) 0x0);
        }
        bf.put(b);

        return bf.array();

    }

    protected String toString(byte[] b) {
        return new String(b).trim();
    }

    // To provide JDK 5 Compat
    protected byte[] copyOfRange(byte[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        byte[] copy = new byte[newLength];
        System.arraycopy(original, from, copy, 0,
                         Math.min(original.length - from, newLength));
        return copy;
    }


}
