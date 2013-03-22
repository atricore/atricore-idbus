package org.atricore.idbus.capabilities.sso.dsl

package object core {

  type IdentityFlowRoute = IdentityFlowRequestContext => Unit
  type IdentityFlowRouteFilter[T <: Product] = IdentityFlowRequestContext => IdentityFlowFilterResult[T]
  type IdentityFlowRequestContextBuilder = () => IdentityFlowRequestContext

}
