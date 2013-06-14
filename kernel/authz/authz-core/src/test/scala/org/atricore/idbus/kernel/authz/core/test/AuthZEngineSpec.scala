package org.atricore.idbus.kernel.authz.core.test

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

import org.specs2.mutable._
import org.atricore.idbus.kernel.authz.core._
import Decisions.{Permit => PermitDecision, Deny => DenyDecision}
import dsl._
import dsl.directives.{AccessControlDirectives}
import util.Logging

/**
 * Simple AuthZ policy tester.
 */
class AuthZEngineSpec extends Specification with AccessControlDirectives with Logging {

  "The authz service" should {
    "handle correctly" in {

      "simple authorization rule" in {

        val obligations = new Obligations {
          obligation is {
            id := 'obligation_1
            fulfillOn := "Permit"

            attributeAssignment is {
              id := 'urn_oasis_names_tc_xacml_2_0_example_attribute_text
              dataType := "http://www.w3.org/2001/XMLSchema#string"
              attributeValue := "Your medical record has been accessed by:"

            }

            attributeAssignment is {
              id := 'urn_oasis_names_tc_xacml_2_0_example_attribute_text2
              dataType := "http://www.w3.org/2001/XMLSchema#string"
              attributeValue := "Your medical record has been accessed by:"
            }

          }
        }

        debug("obligations = " + obligations.toAST)

        val simpleDecisionRequest =
          DecisionRequest(
            Some(
              SubjectAttributes("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject",
                List(
                  Attribute(
                    "urn:oasis:names:tc:xacml:1.0:subject:subject-id",
                    "med.example.com",
                    List(
                      AttributeValue(
                        "urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name",
                        "bs@simpsons.com"
                      ))
                  ),
                  Attribute(
                    "urn:oasis:names:tc:xacml:1.0:subject:authn-locality:ip-address",
                    "med.example.com",
                    List(
                      AttributeValue(
                        "urn:oasis:names:tc:xacml:2.0:data-type:ipAddress",
                        "192.168.1.1"
                      ))
                  )
                )
              )
            ),
            Some(
              ResourceAttributes(
                List(
                  Attribute(
                    "urn:oasis:names:tc:xacml:1.0:resource:resource-id",
                    "med.example.com",
                    List(
                      AttributeValue(
                        "http://www.w3.org/2001/XMLSchema#anyURI",
                        "http://medico.com/record/patient/BartSimpson"
                      ))
                  )
                )
              )
            ),
            Some(
              ActionAttributes(
                List(
                  Attribute(
                    "urn:oasis:names:tc:xacml:1.0:action:action-id",
                    "med.example.com",
                    List(
                      AttributeValue(
                        "http://www.w3.org/2001/XMLSchema#string",
                        "read"
                      ))
                  ),
                  Attribute(
                    "urn:oasis:names:tc:xacml:1.0:action:action-id",
                    "med.example.com",
                    List(
                      AttributeValue(
                        "http://www.w3.org/2001/XMLSchema#string",
                        "write"
                      ))
                  )
                )
              )
            )

          )

        val simpleRequestContext = AccessControlRequestContext(
          simpleDecisionRequest
        )

        (denyOverrides(Some(new Obligations {
          obligation is {
            id := 'urn_oasis_names_tc_xacml_example_obligation_email
            fulfillOn := "Permit"

            attributeAssignment is {
              id := 'urn_oasis_names_tc_xacml_2_0_example_attribute_text
              category := "category-1"
              issuer := "issuer-1"
              dataType := "http://www.w3.org/2001/XMLSchema#string"
              attributeValue := "Sample text"
            }

            attributeAssignment is {
              id := 'urn_oasis_names_tc_xacml_2_0_example_attribute_mailto
              category := "category-2"
              issuer := "issuer-2"
              dataType := "http://www.w3.org/2001/XMLSchema#string"
              attributeValue := "foo@acme.com"
            }

          }
        })) {
          (rule(name = "simple-rule-one", description = "This is simple rule one") {
            subjectAttributeDesignator(
              mustBePresent = false,
              attributeId = "urn:oasis:names:tc:xacml:1.0:subject:subject-id",
              dataType = "urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name") {
              (targetAttributeId, targetAttributeValues) =>
                matchString(
                  targetAttributeValues,
                  "bs@simpsons.com"
                ) {
                  respond
                }
              } ~
              resourceAttributeDesignator(
                mustBePresent = false,
                attributeId = "urn:oasis:names:tc:xacml:1.0:resource:resource-id",
                dataType = "http://www.w3.org/2001/XMLSchema#anyURI") {
                (targetAttributeId, targetAttributeValues) =>
                  matchURI(
                    targetAttributeValues,
                    "http://medico.com/record/patient/BartSimpson"
                  ) {
                    respond
                  }
              } ~
              actionAttributeDesignator(
                mustBePresent = false,
                attributeId = "urn:oasis:names:tc:xacml:1.0:action:action-id",
                dataType = "http://www.w3.org/2001/XMLSchema#string") {
                (targetAttributeId, targetAttributeValues) =>
                  matchString(
                    targetAttributeValues,
                    "read"
                  ) {
                    respond
                  }
              } ~
              actionAttributeDesignator(
                mustBePresent = false,
                attributeId = "urn:oasis:names:tc:xacml:1.0:action:action-id",
                dataType = "http://www.w3.org/2001/XMLSchema#string") {
                (targetAttributeId, targetAttributeValues) =>
                  matchString(
                    targetAttributeValues,
                    "write"
                  ) {
                    respond
                  }
              }
          } ~
            rule(name = "simple-rule-two", description = "This is simple rule two") {
              subjectAttributeDesignator(
                mustBePresent = false,
                attributeId = "urn:oasis:names:tc:xacml:1.0:subject:authn-locality:ip-address",
                dataType = "urn:oasis:names:tc:xacml:2.0:data-type:ipAddress") {
                (targetAttributeId, targetAttributeValues) =>
                  matchIPAddress(
                    targetAttributeValues,
                    "192.168.1.1"
                  ) {
                    respond
                  }
              }
            })
        })(simpleRequestContext)

        debug("Decision is = " + simpleRequestContext.response)

        simpleRequestContext.response.obligations.isDefined mustEqual true

        simpleRequestContext.response.obligations mustNotEqual None

        simpleRequestContext.response.obligations.map(_.size mustNotEqual 0)

        implicit val module = MockObligationFulfillmentModule

        val config = new ObligationFulfillmentConfig

        simpleRequestContext.response.obligations foreach (obl => {
            val fulFillmentRes = ObligationFulfillment.fullfil(obl, config)

            fulFillmentRes.size mustNotEqual 0

            fulFillmentRes foreach (
              oblfres => {
                oblfres mustEqual Right(ObligationFulfillmentSuccess(obl.head))
              }
            )
          }
        )

        simpleRequestContext.response.decision mustEqual PermitDecision

      }
    }

  }
}

object MockObligationFulfillmentModule extends ObligationFulfillmentModule(
{
  implicit configModule =>
    configModule.bind [String] identifiedBy('smtpServer) toSingle "localhost"

}
)

