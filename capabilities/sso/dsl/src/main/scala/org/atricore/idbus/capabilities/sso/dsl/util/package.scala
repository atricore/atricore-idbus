package org.atricore.idbus.capabilities.sso.dsl

package object util {

  def identityFunc[T]: T => T = _identityFunc.asInstanceOf[T => T]
  private lazy val _identityFunc: Any => Any = x => x

  // implicits
  //implicit def pimp...

}
