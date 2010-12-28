package com.atricore.idbus.console.liveservices.liveupdate.main.engine;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface InstallEvent {

    Step getStep();

    UpdateContext getContext();

}
