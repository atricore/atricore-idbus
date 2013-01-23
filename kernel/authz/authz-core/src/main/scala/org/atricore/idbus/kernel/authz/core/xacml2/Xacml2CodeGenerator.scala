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

package org.atricore.idbus.kernel.authz.core.xacml2

import org.atricore.idbus.kernel.authz.core.{AuthorizationEngine, PolicySource}
import org.atricore.idbus.kernel.authz.core.{PolicySource, AuthorizationEngine}
import xml.Node
import org.atricore.idbus.kernel.authz.core.support.{Code, CodeGenerator}
import collection.immutable.TreeMap
import util.parsing.input.OffsetPosition

/**
 * Transforms an XACML v2 policy definition to DSL-ish Scala
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
class Xacml2CodeGenerator extends CodeGenerator {

  def generate(engine: AuthorizationEngine, source: PolicySource) : Code = {

    val uri = source.uri
    val xacml2Source = source.inputStream
    val policy = scala.xml.XML.load(xacml2Source)

    code = acTemplate(uri.replaceAll("/", ""), rules(policy))

    Code(source.className, code, Set(uri), positions)

  }

  var code = ""
  var generatedPositions = Map[OffsetPosition, Int]()

  def positions() = {
    var rc = new TreeMap[OffsetPosition,OffsetPosition]()( new Ordering[OffsetPosition] {
      def compare(p1:OffsetPosition, p2:OffsetPosition):Int = {
        val rc = p1.line - p2.line
        if( rc==0 ) {
          p1.column - p2.column
        } else {
          rc
        }
      }
    })
    generatedPositions.foreach {
      entry=>
        rc = rc + (OffsetPosition(code, entry._2)->entry._1)
    }
    rc
  }

  val stratumName = "xacml2"

  def toScalaIdentifier(identifier: String) = identifier.replace('.', '_').replace(":", "$colon").replace("-", "$dash")

  def quotedText(text: String): String = "\"" + text + "\""

  def multilineQuotedText(text: String): String = "\"\"\"" + text + "\"\"\""

  def rules(policy: Node) = {

    ("" /: policy \\ "Rule") {
      (text: String, rule: Node) =>


        val combiningAlgorithmDirective = (policy \ "@RuleCombiningAlgId" text) match {
          case "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:deny-overrides" =>
            "denyOverrides"
          case unknownRuleCombiningAlgorithm =>
            throw new IllegalArgumentException("Invalid rule combining algorithm : " + unknownRuleCombiningAlgorithm)
        }

        "val policy = %s(Some(new Obligations {\n%s\n})) {\n(rule(name= %s, description = %s) { %s })\n}".format(
          combiningAlgorithmDirective,
          obligations(policy),
          quotedText((rule \ "@RuleId" text)),
          multilineQuotedText(rule.text),
          targets(rule)
        )

    }
  }

  def targets(rule : Node) =
    subjectMatch(rule) + " ~\n" + resourceMatch(rule) + " ~\n" + actionMatch(rule) + "\n"

  def subjectMatch(rule: Node) =
    (("", 0) /: rule \\ "SubjectMatch") {
      ( text : (String, Int), sm: Node) =>
        val concatChar : Option[String] = { if (text._2 > 0) Some(" ~ \n") else None }

        (("%s%s%s {\n%s\n}".format(text._1, concatChar.getOrElse(""),subjectAttributeDesignator(sm),attributeValue(sm))),
          (text._2 + 1))
    } match { case (code, pos) => code }


  def subjectAttributeDesignator(sm: Node) =
    ("" /: sm \\ "SubjectAttributeDesignator") {
      (text: String, sad: Node) =>

        "subjectAttributeDesignator(mustBePresent = false,attributeId = %s,dataType = %s)\n".format(
          quotedText(sad \ "@AttributeId" text),
          quotedText(sad \ "@DataType" text)
        )
    }

  def resourceMatch(rule: Node) =
    (("", 0) /: rule \\ "ResourceMatch") {
      ( text : (String, Int), sm: Node) =>
        val concatChar : Option[String] = { if (text._2 > 0) Some(" ~ \n") else None }

        (("%s%s%s {\n%s\n}".format(text._1, concatChar.getOrElse(""),resourceAttributeDesignator(sm),attributeValue(sm))),
          (text._2 + 1))
    } match { case (code, pos) => code }


  def resourceAttributeDesignator(sm: Node) =
    ("" /: sm \\ "ResourceAttributeDesignator") {
      (text: String, sad: Node) =>

        "resourceAttributeDesignator(mustBePresent = false,attributeId = %s,dataType = %s)\n".format(
          quotedText(sad \ "@AttributeId" text),
          quotedText(sad \ "@DataType" text)
        )
    }

  def actionMatch(rule: Node) =
    (("", 0) /: rule \\ "ActionMatch") {
      ( text : (String, Int), sm: Node) =>
        val concatChar : Option[String] = { if (text._2 > 0) Some(" ~ \n") else None }

        (("%s%s%s {\n%s\n}".format(text._1, concatChar.getOrElse(""),actionAttributeDesignator(sm),attributeValue(sm))),
          (text._2 + 1))
    } match { case (code, pos) => code }


  def actionAttributeDesignator(sm: Node) =
    ("" /: sm \\ "ActionAttributeDesignator") {
      (text: String, sad: Node) =>

        "actionAttributeDesignator(mustBePresent = false,attributeId = %s,dataType = %s)\n".format(
          quotedText(sad \ "@AttributeId" text),
          quotedText(sad \ "@DataType" text)
        )
    }

  def attributeValue(sm: Node) =
    ("" /: sm \\ "AttributeValue") {
      (text: String, av: Node) =>

        val matchDirective = (sm \ "@MatchId" text) match {
          case "urn:oasis:names:tc:xacml:1.0:function:string-equal" =>
            "matchString(targetAttributeValues, %s) { respond }".format(quotedText(av.text))
          case "urn:oasis:names:tc:xacml:1.0:function:anyURI-equal" =>
            "matchString(targetAttributeValues, %s) { respond }".format(quotedText(av.text))
          case unknownMatch =>
            throw new IllegalArgumentException("Unknown match :" + unknownMatch)
        }

        "(targetAttributeId, targetAttributeValues) => %s".format(matchDirective)

    }


  def obligations(policy: Node) = {

    ("" /: policy \\ "Obligation") {
      (text: String, obligation: Node) =>

        val obligationId = obligation \ "@ObligationId"
        val fulFillOn = obligation \ "@FulfillOn"

        "obligation is {\nid := %s\nfulfillOn := %s\n%s}\n".format(
          quotedText(obligationId.text),
          quotedText(fulFillOn.text),
          attributeAssignments(obligation)
        )
    }
  }

  def attributeAssignments(obligation : Node) = {
    ("" /: obligation \\ "AttributeAssignment") {
      (text: String, aa : Node) =>

        val attributeId = aa \ "@AttributeId"
        val dataType = aa \ "@DataType"

        "attributeAssignment is { id := %s\ncategory := %s\nissuer := %s\ndataType := %s\nattributeValue := %s}\n".
          format(
            quotedText(attributeId.text),
            "\"\"",
            "\"\"",
            quotedText(dataType.text),
            "\"\"")
    }
  }


  def acTemplate(acFileName: String, policies: String) ="""import org.atricore.idbus.kernel.authz.core._
import dsl._
import dsl.directives.{AccessControlDirectives}

  class %s extends Policy with AccessControlDirectives {

    def evaluate(request: DecisionRequest): Response = {
        val requestContext = AccessControlRequestContext(request)

        policy(requestContext)

        requestContext.response
    }

    //Policies---
    %s

    //---Policies


  }""" format(toScalaIdentifier(acFileName), policies)

}
