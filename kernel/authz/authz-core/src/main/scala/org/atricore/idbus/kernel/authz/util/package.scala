package org.atricore.idbus.kernel.authz

package object util {

  def identityFunc[T]: T => T = _identityFunc.asInstanceOf[T => T]
  private lazy val _identityFunc: Any => Any = x => x

  // implicits
  //implicit def pimp...
}
