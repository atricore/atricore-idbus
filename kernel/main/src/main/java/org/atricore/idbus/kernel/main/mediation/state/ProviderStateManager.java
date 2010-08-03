package org.atricore.idbus.kernel.main.mediation.state;

import java.util.Collection;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface ProviderStateManager {

    void store(ProviderStateContext ctx, LocalState state);

    LocalState retrieve(ProviderStateContext ctx, String key);

    LocalState retrieve(ProviderStateContext ctx, String keyName, String key);

    void remove(ProviderStateContext ctx, String key);

    LocalState createState(ProviderStateContext ctx);

    Collection<LocalState> retrieveAll(ProviderStateContext ctx);
}
