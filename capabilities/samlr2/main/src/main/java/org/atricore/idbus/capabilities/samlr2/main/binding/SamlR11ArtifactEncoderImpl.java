package org.atricore.idbus.capabilities.samlr2.main.binding;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.SamlR2Exception;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR11ArtifactEncoderImpl extends AbstractSamlArtifactEncoder {

    private static final Log logger = LogFactory.getLog(SamlR11ArtifactEncoderImpl.class);

    public String encode(SamlArtifact artifact) {
        // Use SAML 1.1 Recommended format

        if (logger.isTraceEnabled())
            logger.trace("Encoding SAML 1.1 Artifact " + artifact);

        byte[] typeCodeBin = toBin(artifact.getType());

        // Make sure that each byte[] is exactly 20 bytes length.
        byte[] messageHandleBin = toBin(artifact.getMessageHandle(), 20);

        // Source is base 64 encoded
        byte[] sourceIdBin = Base64.decodeBase64(artifact.getSourceID().getBytes());
        if (sourceIdBin.length != 20)
            throw new IllegalArgumentException("SourceID value must be SHA-1 hash (20 bytes length), found " + sourceIdBin.length);

        ByteBuffer bf = ByteBuffer.allocate(typeCodeBin.length +
                messageHandleBin.length +
                sourceIdBin.length);

        // Be carefull with the order
        bf.put(typeCodeBin);
        bf.put(sourceIdBin);
        bf.put(messageHandleBin);

        String s = new String(Base64.encodeBase64(bf.array()));
        try {
            s = java.net.URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new  RuntimeException(e);
        }

        if (logger.isTraceEnabled())
            logger.trace("Encoded SAML 1.1 Artifact " + s);

        return s;

    }

    public SamlArtifact decode(String samlArtStr) throws SamlR2Exception {

        if (logger.isTraceEnabled())
            logger.trace("Decoding SAML 1.1 Artifact " + samlArtStr);

        byte[] samlArtBin = Base64.decodeBase64(samlArtStr.getBytes());

        if (samlArtBin.length < 42) // Using SAML 1.1 Recommended format !
            throw new SamlR2Exception("Invalid Saml 1.1 Artifact format " + samlArtBin.length);

        byte[] typeCodeBin = new byte[2];
        typeCodeBin[0] = samlArtBin[0];
        typeCodeBin[1] = samlArtBin[1];
        int typeCode = toInt(typeCodeBin);

        String sourceId = null;
        String messageHandle = null;

        byte[] remainingArtBin = copyOfRange(samlArtBin, 2, samlArtBin.length - 1);
        // Assume SAML 1.1 Recommended remaining
        if (logger.isTraceEnabled())
            logger.trace("Assuming SAML 1.1 Recommended artifact format");

        // First 20 bytes are sourceId
        byte[] sourceIdBin = copyOfRange(remainingArtBin, 0, 20);
        sourceIdBin = Base64.encodeBase64(sourceIdBin);
        sourceId = toString(sourceIdBin);

        // Last 20 bytes are messageHandle
        byte[] messageHandleBin = copyOfRange(remainingArtBin, 20, 40);
        messageHandle = toString(messageHandleBin);

        SamlArtifact a = new SamlArtifact(typeCode, 0, sourceId, messageHandle);

        if (logger.isTraceEnabled())
            logger.trace("Decoded SAML 1.1 artifact " + a);

        return a;


    }

}
