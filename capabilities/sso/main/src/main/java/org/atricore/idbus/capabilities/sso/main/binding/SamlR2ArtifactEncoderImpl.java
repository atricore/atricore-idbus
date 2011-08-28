package org.atricore.idbus.capabilities.sso.main.binding;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SamlR2Exception;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2ArtifactEncoderImpl extends AbstractSamlArtifactEncoder {

    private static final Log logger = LogFactory.getLog(SamlR2ArtifactEncoderImpl.class);

    public String encode(SamlArtifact artifact) {
        // Use SAML 2.0 Recommended format

        if (logger.isTraceEnabled())
            logger.trace("Encoding SAML 2.0 Artifact " + artifact);

        byte[] typeCodeBin = toBin(artifact.getType());
        byte[] endpointIdxBin = toBin(artifact.getEndpointIndex());

        // Make sure that each byte[] is exactly 20 bytes length.
        byte[] messageHandleBin = toBin(artifact.getMessageHandle(), 20);
        // Source is base 64 encoded
        byte[] sourceIdBin = Base64.decodeBase64(artifact.getSourceID().getBytes());
        if (sourceIdBin.length != 20)
            throw new IllegalArgumentException("SourceID value must be SHA-1 hash (20 bytes length), found " + sourceIdBin.length);

        ByteBuffer bf = ByteBuffer.allocate(typeCodeBin.length +
                endpointIdxBin.length +
                messageHandleBin.length +
                sourceIdBin.length);

        // Be carefull with the order
        bf.put(typeCodeBin);
        bf.put(endpointIdxBin);
        bf.put(sourceIdBin);
        bf.put(messageHandleBin);

        String s = new String(Base64.encodeBase64(bf.array()));
        try {
            s = java.net.URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new  RuntimeException(e);
        }

        if (logger.isTraceEnabled())
            logger.trace("Encoded SAML 2.0 Artifact " + s);

        return s;

    }

    public SamlArtifact decode(String samlArtStr) throws SamlR2Exception {

        if (logger.isTraceEnabled())
            logger.trace("Decoding SAML 2.0 Artifact " + samlArtStr);

        byte[] samlArtBin = Base64.decodeBase64(samlArtStr.getBytes());

        if (samlArtBin.length < 44) // Using SAML 2.0 Recommended format !
            throw new SamlR2Exception("Invalid Saml 2.0 Artifact format " + samlArtBin.length);

        byte[] typeCodeBin = new byte[2];
        typeCodeBin[0] = samlArtBin[0];
        typeCodeBin[1] = samlArtBin[1];
        int typeCode = toInt(typeCodeBin);

        byte[] endpointIdxBin = new byte[2];
        endpointIdxBin[0] = samlArtBin[2];
        endpointIdxBin[1] = samlArtBin[3];
        int endpointIndex = toInt(endpointIdxBin);

        String sourceId = null;
        String messageHandle = null;

        byte[] remainingArtBin = copyOfRange(samlArtBin, 4, samlArtBin.length);
        // Assume SAML 2.0 Recommended remaining
        if (logger.isTraceEnabled())
            logger.trace("Assuming SAML 2.0 Recommended artifact format");

        // First 20 bytes are sourceId
        byte[] sourceIdBin = copyOfRange(remainingArtBin, 0, 20);
        sourceIdBin = Base64.encodeBase64(sourceIdBin);
        sourceId = toString(sourceIdBin);

        // Last 20 bytes are messageHandle
        byte[] messageHandleBin = copyOfRange(remainingArtBin, 20, 40);
        messageHandle = toString(messageHandleBin);

        SamlArtifact a = new SamlArtifact(typeCode, endpointIndex, sourceId, messageHandle);

        if (logger.isTraceEnabled())
            logger.trace("Decoded SAML 2.0 artifact " + a);

        return a;   


    }

}
