package com.atricore.idbus.console.liveservices.liveupdate.admin.service;

import com.atricore.liveservices.liveupdate._1_0.util.LiveUpdateKeyResolver;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface LiveUpdateAdminService {

    byte[] signUpdatesIndex(byte[] updatesIndexContent, LiveUpdateKeyResolver keyResolver) throws Exception;

    byte[] signArtifactDescriptor(byte[] artifactDescriptorContent, LiveUpdateKeyResolver keyResolver) throws Exception;

    void validateUpdatesIndex(byte[] signedUpdatesIndexContent, LiveUpdateKeyResolver keyResolver) throws Exception;

    void validateArtifactDescriptor(byte[] signedArtifactDescriptorContent, LiveUpdateKeyResolver keyResolver) throws Exception;
}
