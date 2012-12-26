package org.atricore.idbus.kernel.authz

import support.CompilerError
import scala.util.control.NoStackTrace
import scala.util.parsing.input.{NoPosition, Position}


class AuthorizationException(message: String, cause: Throwable) extends RuntimeException(message, cause) {
  def this(message: String) {
    this (message, null)
  }
}

/**
 * Indicates a Scala compiler error occurred when converting the rule into bytecode
 */
class CompilerException(msg: String, val errors: List[CompilerError]) extends AuthorizationException(msg)

class StaleCacheEntryException(source: PolicySource)
  extends AuthorizationException("The compiled policy for " + source + " needs to get recompiled") with NoStackTrace

/**
 * Indicates a syntax error trying to parse the policy
 */
class InvalidSyntaxException(val brief: String, val pos: Position = NoPosition) extends AuthorizationException(brief + " at " + pos) {
  var source:PolicySource = _
  def policy: String = if (source != null) source.uri else null
}
