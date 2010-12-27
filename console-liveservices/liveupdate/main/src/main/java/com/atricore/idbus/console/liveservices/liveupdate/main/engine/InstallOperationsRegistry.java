package com.atricore.idbus.console.liveservices.liveupdate.main.engine;

import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface InstallOperationsRegistry {

    Set<InstallOperation> getOperations();

    InstallOperation getOperation(String name);

    Set<InstallOperation> getOperations(String stepName);

    void register(String name, InstallOperation installOp);

    void unregister(String name);
}
