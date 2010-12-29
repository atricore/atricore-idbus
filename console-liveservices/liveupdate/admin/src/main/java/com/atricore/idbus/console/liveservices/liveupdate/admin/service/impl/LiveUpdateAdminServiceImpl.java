package com.atricore.idbus.console.liveservices.liveupdate.admin.service.impl;

import com.atricore.idbus.console.liveservices.liveupdate.admin.service.LiveUpdateAdminService;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;
import com.atricore.liveservices.liveupdate._1_0.util.LiveUpdateKeyResolver;
import com.atricore.liveservices.liveupdate._1_0.util.LiveUpdateSigner;
import com.atricore.liveservices.liveupdate._1_0.util.XmlUtils1;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LiveUpdateAdminServiceImpl implements LiveUpdateAdminService {

    public byte[] signUpdatesIndex(byte[] updatesIndexContent, LiveUpdateKeyResolver keyResolver) throws Exception {
        UpdatesIndexType updatesIndex = XmlUtils1.unmarshallUpdatesIndex(new String(updatesIndexContent), false);
        UpdatesIndexType signedUpdatesIndex = LiveUpdateSigner.sign(updatesIndex, keyResolver);
        return XmlUtils1.marshalUpdatesIndex(signedUpdatesIndex, false).getBytes();
    }

    public byte[] signArtifactDescriptor(byte[] artifactDescriptorContent, LiveUpdateKeyResolver keyResolver) throws Exception {
        ArtifactDescriptorType artifactDescriptor = XmlUtils1.unmarshallArtifactDescriptor(new String(artifactDescriptorContent), false);
        ArtifactDescriptorType signedArtifactDescriptor = LiveUpdateSigner.sign(artifactDescriptor, keyResolver);
        return XmlUtils1.marshalArtifactDescriptor(signedArtifactDescriptor, false).getBytes();
    }

    public void validateUpdatesIndex(byte[] signedUpdatesIndexContent, LiveUpdateKeyResolver keyResolver) throws Exception {
        UpdatesIndexType signedUpdatesIndex = XmlUtils1.unmarshallUpdatesIndex(new String(signedUpdatesIndexContent), false);
        LiveUpdateSigner.validate(signedUpdatesIndex, keyResolver);
    }

    public void validateArtifactDescriptor(byte[] signedArtifactDescriptorContent, LiveUpdateKeyResolver keyResolver) throws Exception {
        ArtifactDescriptorType signedArtifactDescriptor = XmlUtils1.unmarshallArtifactDescriptor(new String(signedArtifactDescriptorContent), false);
        LiveUpdateSigner.validate(signedArtifactDescriptor, keyResolver);
    }
}
