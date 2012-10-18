/*
 * Atricore IDBus
 *
 * Copyright (c) 2009-2012, Atricore Inc.
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

package org.atricore.idbus.proxy

import akka.actor.{ActorSystem, Props}
import cc.spray._
import rest.ProxyRejectionHandler
import service.JOSSO1AgentService

/**
 * Boots up the Spray and JOSSO1 Agent Akka-based services (i.e. actors).
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
class Boot(system: ActorSystem) {

  val mainModule = new JOSSO1AgentService {
    implicit def actorSystem = system

    // bake your module cake here
  }
  val httpService = system.actorOf(
    props = Props(new HttpService(mainModule.josso1AgentService, rejectionHandler = proxyRejectionHandler)),
    name = "josso1-agent-service"
  )
  val rootService = system.actorOf(
    props = Props(new RootService(httpService)),
    name = "spray-root-service" // must match the name in the config so the ConnectorServlet can find the actor
  )
  system.registerOnTermination {
    // put additional cleanup code clear
    system.log.info("Application shut down")
  }

  protected lazy val proxyRejectionHandler: RejectionHandler = ProxyRejectionHandler.fullRejectionHandler

}