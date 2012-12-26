package org.atricore.idbus.kernel.authz

package object dsl {

  type AccessControlRoute = AccessControlRequestContext => Unit
  type AccessControlRouteFilter[T <: Product] = AccessControlRequestContext => AccessControlFilterResult[T]
  type AccessControlRequestContextBuilder = () => AccessControlRequestContext

}
