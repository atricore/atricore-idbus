package org.atricore.idbus.kernel.authz.command

import org.apache.felix.gogo.commands.Command
import org.apache.felix.gogo.commands.Option

import org.atricore.idbus.kernel.authz.core._

@Command(scope = "authz", name = "policy-test", description = "Test Authorization Policy")
class TestPolicyCommand extends AuthzCommandSupport {

  @Option(name = "-id", aliases = Array("--id"), description = "Authorization Policy Identifier", required = true, multiValued = false)
  var policyId: String = _

  override protected def doExecute = {
    val authzConfig = getAuthorizationConfiguration()

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
                    "http://www.w3.org/2001/XMLSchema#string",
                    "Julius Hibbert"
                  ))
              ),
              Attribute(
                "urn:oasis:names:tc:xacml:1.0:conformance-test:some-attribute",
                "med.example.com",
                List(
                  AttributeValue(
                    "http://www.w3.org/2001/XMLSchema#string",
                    "riddle me this"
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

    val oldCl = Thread.currentThread().getContextClassLoader
    // use this bundle class loader for instantiating policies instead of the system class loader which
    // does not include the authorization core libraries.
    Thread.currentThread.setContextClassLoader(this.getClass.getClassLoader)
    val response = authzConfig.engine.evaluate(policyId, simpleDecisionRequest)
    Thread.currentThread().setContextClassLoader(oldCl)
    println(response)
    null
  }
}
