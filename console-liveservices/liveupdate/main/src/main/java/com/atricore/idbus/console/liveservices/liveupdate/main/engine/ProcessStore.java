package com.atricore.idbus.console.liveservices.liveupdate.main.engine;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl.UpdateProcessState;

import java.util.Collection;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface ProcessStore {

    void init() throws LiveUpdateException;

    void save(UpdateProcessState proc) throws LiveUpdateException;

    UpdateProcessState load(String id) throws LiveUpdateException;

    Collection<UpdateProcessState> load() throws LiveUpdateException;

    void remove(String id) throws LiveUpdateException;
}
