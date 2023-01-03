package org.atricore.idbus.kernel.main.mediation.camel.component.http;

public interface MediationLocationsRegistry {

    void register(String location);

    void unregister(String location);
}
