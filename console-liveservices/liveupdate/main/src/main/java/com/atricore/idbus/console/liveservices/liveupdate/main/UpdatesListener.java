package com.atricore.idbus.console.liveservices.liveupdate.main;

import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;

import java.util.Collection;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface UpdatesListener {

    void notifyUpdates(Collection<UpdateDescriptorType> newUpdates);

}
