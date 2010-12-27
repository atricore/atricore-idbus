package com.atricore.liveservices.liveupdate._1_0.util;

import com.atricore.liveservices.liveupdate._1_0.md.ArtifactDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LiveUpdateSigner {

    // TODO ! pass private key as argument
    public static UpdatesIndexType sign(UpdatesIndexType unsigned) {
        return null;
    }

    // TODO ! pass private key as argument
    public static ArtifactDescriptorType sign(ArtifactDescriptorType unsigned) {
        return null;
    }


    // TODO : pass public key as argument
    public static void validate(UpdatesIndexType signed) throws InvalidSignatureException {

    }

    // TODO : pass public key as argument
    public static void validate(ArtifactDescriptorType signed) throws InvalidSignatureException {

    }

}
