package org.atricore.idbus.capabilities.sso.component.container

import org.atricore.idbus.capabilities.sso.dsl.core.Rejection

case class RouteRejectionException(rejections : Set[Rejection]) extends Exception

class NoRouteResponseException extends Exception