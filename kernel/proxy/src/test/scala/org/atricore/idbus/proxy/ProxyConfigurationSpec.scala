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

package org.atricore.idbus.proxy

import configuration.ProxyDSLParser._
import configuration.{Tenant, ProxyConfiguration}
import org.specs2.mutable._

/**
 * Proxy configuration tester.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
class ProxyConfigurationSpec extends Specification {

  "The proxy instance" should {
    "parse correctly" in {
      "proxy configuration" in {

        val config = """/* Example Proxy Configuration */
                        deliver service on jboss4 for
                        // First Tenant
                        (tenant T1 bind to host "localhost" path "/t1"
                          // Connections
                          (connection C1
                             with service provider SP1 of type josso1
                             at identity appliance IDA1
                             having (jossoLoginUrl "http://localhost:8080/josso/signon/login.do"),
                           connection C2
                             with service provider SP2 of type josso2
                             at identity appliance IDA2
                             having (jossoLoginUrl "http://localhost:8080/josso/signon/login.do")
                          ),
                         // Second Tenant
                         tenant T2 bind to host "localhost" path "/t2"
                          // Connections
                          (connection C1
                             with service provider SP1 of type josso1
                             at identity appliance IDA3
                             having (jossoLoginUrl "http://localhost:8080/josso/signon/login.do"),
                           connection C2
                             with service provider SP2 of type josso2
                             at identity appliance IDA4
                             having (jossoLoginUrl "http://localhost:8080/josso/signon/login.do")
                           )
                         )
                         """

        val proxyConfig = proxy_configuration(new lexical.Scanner(config))
        proxyConfig mustNotEqual null
      }
    }

    "build consistent" in {
      "proxy configuration" in {

        val config = """/* Example Proxy Configuration */
                        deliver service mySSO on platform "org.atricore.idbus.proxy.binding.test.MockProxyBindingModule" to
                        // First Tenant
                        (tenant T1 bind to host "localhost" path "/t1"
                          // Connections
                          (connection C1
                             with service provider SP1 of type josso1
                             at identity appliance IDA1
                             having (jossoLoginUrl "http://localhost:8080/josso/signon/login.do",
                                     securityContextEstablishmentResource "c1_acs.jsp"),
                           connection C2
                             with service provider SP2 of type josso2
                             at identity appliance IDA2
                             having (jossoLoginUrl "http://localhost:8080/josso/signon/login.do")
                          ),
                         // Second Tenant
                         tenant T2 bind to host "localhost" path "/t2"
                          // Connections
                          (connection C1
                             with service provider SP1 of type josso1
                             at identity appliance IDA3
                             having (jossoLoginUrl "http://localhost:8080/josso/signon/login.do"),
                           connection C2
                             with service provider SP2 of type josso2
                             at identity appliance IDA4
                             having (jossoLoginUrl "http://localhost:8080/josso/signon/login.do")
                           )
                         )
                         """

        val testModule = ProxyConfiguration.fromString(config)

        testModule.tenant("T1") mustNotEqual None
        testModule.tenant("T4") mustEqual None
        testModule.connection("T1", "C1") mustNotEqual None
        testModule.tenant("localhost", "/t2").get.name mustEqual "T2"
        testModule.connectionBySCER("T1", "c1_acs.jsp") mustNotEqual None
      }
    }
  }
}