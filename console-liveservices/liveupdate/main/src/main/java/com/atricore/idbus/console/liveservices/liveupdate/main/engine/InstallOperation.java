package com.atricore.idbus.console.liveservices.liveupdate.main.engine;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface InstallOperation {

    String getStepName();

    String getName();

    void init();

    void shutdown();

    OperationStatus execute(InstallEvent event) throws LiveUpdateException;

}
