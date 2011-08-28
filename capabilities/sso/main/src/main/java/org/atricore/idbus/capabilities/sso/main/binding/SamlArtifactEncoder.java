package org.atricore.idbus.capabilities.sso.main.binding;

import org.atricore.idbus.capabilities.sso.main.SamlR2Exception;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface SamlArtifactEncoder {

    String encode(SamlArtifact artifact);

    SamlArtifact decode(String samlArtStr) throws SamlR2Exception;
}
