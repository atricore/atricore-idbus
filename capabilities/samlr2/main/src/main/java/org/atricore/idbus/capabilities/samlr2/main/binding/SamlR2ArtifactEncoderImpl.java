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
public class SamlR2ArtifactEncoderImpl extends AbstractSamlArtifactEncoder {

    private static final Log logger = LogFactory.getLog(SamlR2ArtifactEncoderImpl.class);

    public String encode(SamlArtifact artifact) {
        // Use SAML 2.0 Recommended format

        byte[] typeCodeBin = toBin(artifact.getType());
        byte[] endpointIdxBin = toBin(artifact.getEndpointIndex());

        // Make sure that each byte[] is exactly 20 bytes length.
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

            byte[] messageHandleBin = copyOfRange(remainingArtBin, 20, 40);
            messageHandle = toString(messageHandleBin);

        } else {
            if (logger.isTraceEnabled())
                logger.trace("Assuming non SAML 2.0 artifact format");

            messageHandle = toString(remainingArtBin);
        }

        return new SamlArtifact(typeCode, endpointIndex, sourceId, messageHandle);


    }

}
