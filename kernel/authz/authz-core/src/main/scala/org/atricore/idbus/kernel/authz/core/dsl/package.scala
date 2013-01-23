package org.atricore.idbus.kernel.authz.core

package object dsl {

  type AccessControlRoute = AccessControlRequestContext => Unit
  type AccessControlRouteFilter[T <: Product] = AccessControlRequestContext => AccessControlFilterResult[T]
  type AccessControlRequestContextBuilder = () => AccessControlRequestContext

}
