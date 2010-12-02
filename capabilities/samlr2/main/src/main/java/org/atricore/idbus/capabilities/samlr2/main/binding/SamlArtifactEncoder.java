package org.atricore.idbus.capabilities.samlr2.main.binding;

import org.atricore.idbus.capabilities.samlr2.main.SamlR2Exception;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface SamlArtifactEncoder {

    String encode(SamlArtifact artifact);

    SamlArtifact decode(String samlArtStr) throws SamlR2Exception;
}
