package org.atricore.idbus.kernel.main.mediation.claim;

import java.io.Serializable;

/**
 */
public interface ClaimsRequest extends Serializable {

    String getId();

    String getRelayState();

    String getLastErrorId();

    String getLastErrorMsg();


}
