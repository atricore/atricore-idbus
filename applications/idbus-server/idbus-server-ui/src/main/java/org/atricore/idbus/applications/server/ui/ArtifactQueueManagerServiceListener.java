/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.applications.server.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;

import java.util.Dictionary;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ArtifactQueueManagerServiceListener {

    private static final Log logger = LogFactory.getLog(ArtifactQueueManagerServiceListener.class);

    public static int BOUND_COUNT = 0;
    public static int UNBOUND_COUNT = 0;

    public void serviceAvailable(MessageQueueManager aqm, Dictionary properties) {
        BOUND_COUNT++;
        logger.debug("Artifact Queue Manager bound (" + getRegisteredServiceCount() + ")");

    }


    public void serviceUnavailable(MessageQueueManager aqm, Dictionary properties) {
        UNBOUND_COUNT++;
        logger.debug("Artifact Queue Manager unbound (" + getRegisteredServiceCount() + ")");
    }

    public int getRegisteredServiceCount() {
        return BOUND_COUNT - UNBOUND_COUNT;
    }
}
