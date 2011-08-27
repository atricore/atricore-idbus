package org.atricore.idbus.capabilities.openid.main.metadata;

import org.atricore.idbus.kernel.main.federation.metadata.MetadataDefinition;

import java.io.Serializable;

/**
 * Placeholder for OpenID metadata. The only purpose of this class is to fulfil the requirements
 * for a provider to become a member of a circle of trust.
 *
 * The metadata of an OpenID entity is fetched at authentication-time from the supplied OpenID.
 *
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class OpenIDMetadataDefinition extends MetadataDefinition<OpenIDMetadata> {


}
