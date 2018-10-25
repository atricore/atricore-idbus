package org.atricore.idbus.capabilities.sso.component.container.impl

import org.apache.camel.Exchange

import collection.mutable.ListBuffer
import org.apache.commons.logging.{Log, LogFactory}
import org.atricore.idbus.capabilities.sso.component.container.{IdentityFlowComponent, IdentityFlowContainer, NoRouteResponseException, RouteRejectionException}
import org.atricore.idbus.capabilities.sso.dsl.core.{IdentityFlowRequestContext, Rejection}
import org.atricore.idbus.capabilities.sso.dsl.{IdentityFlowRequest, IdentityFlowResponse}
import org.atricore.idbus.kernel.main.mediation.provider.Provider
import org.atricore.idbus.kernel.main.mediation.Channel
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint
import org.atricore.idbus.kernel.main.mediation.claim.ClaimSet

import scala.collection.JavaConversions._

class IdentityFlowContainerImpl extends IdentityFlowContainer {
  private[this] val log: Log = LogFactory.getLog(this.getClass)

  private val _components = new ListBuffer[IdentityFlowComponent]

  def components: java.util.List[IdentityFlowComponent] = seqAsJavaList(_components.toList)

  def init() {
  }

  def register(component: IdentityFlowComponent, properties: java.util.Map[String, _]) {
    if (component != null) {
      _components += component
      log.debug("Registered IdentityFlow Component [" + component.name + "]")
    }
  }

  def unregister(component: IdentityFlowComponent, properties: java.util.Map[String, _]) {
    if (component != null) {
      _components -= component;
      log.debug("Unregistered IdentityFlow Component [" + component.name + "]")
    }
  }

  def dispatch( componentId : String, exchange : Exchange, provider : Provider, channel : Channel,
                endpoint : IdentityMediationEndpoint, claims : ClaimSet) = {
    var rejections : Option[Set[Rejection]] = None
    var response : Option[IdentityFlowResponse] = None

    val ctx = IdentityFlowRequestContext( IdentityFlowRequest(exchange, provider, channel, endpoint, Option(claims)))

    _components.find( _.name == componentId).headOption match {
      case Some(comp) =>
        comp.route(ctx.withResponse( resp => response = Some(resp)).withReject( rejs => rejections = Some(rejs)))
        rejections.foreach( r => if (!r.isEmpty) throw new RouteRejectionException(r) )
        response.getOrElse( throw new NoRouteResponseException )
      case None =>
        throw new IllegalArgumentException("Component identified as " + componentId + " cannot be found")
    }

  }

  def close() {}

}
