/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
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

package org.atricore.idbus.capabilities.samlr2.main.test;

import oasis.names.tc.saml._2_0.metadata.EndpointType;
import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.metadata.IDPSSODescriptorType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.atricore.idbus.capabilities.samlr2.support.binding.SamlR2Binding;
import org.atricore.idbus.capabilities.samlr2.support.metadata.SAMLR2MetadataConstants;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManager;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SamlR2MetadataTest.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */

public class SamlR2MetadataTest {

    private static final Log logger = LogFactory.getLog( SamlR2MetadataTest.class );

    private static final String ENTITY_ID = "http://suse-IdP2.workgroup:8080/nidp/saml2/metadata";

    protected ClassPathXmlApplicationContext applicationContext;

    private CircleOfTrustManager cotMgr;

    @Before
    public void setUp () throws Exception {

        applicationContext = new ClassPathXmlApplicationContext(
                new String[]{"/org/atricore/idbus/capabilities/samlr2/main/test/josso2-samlr2-md-test.xml"}
        );

        Map<String, CircleOfTrustManager> cots = (Map<String, CircleOfTrustManager>)
                applicationContext.getBeansOfType(CircleOfTrustManager.class );

        assert cots.size() == 1 : "Too many/few COTs : " + cots.size();
        cotMgr = cots.values().iterator().next();

    }

    @Test
    public void findEntityDescriptorTest() throws Exception {

        MetadataEntry e = cotMgr.findEntityMetadata(ENTITY_ID);
        assert e !=  null : "No entity metadta found";

        EntityDescriptorType entityDescriptor = (EntityDescriptorType) e.getEntry();
        assert entityDescriptor != null : "No enitity descriptor found";
        assert entityDescriptor.getEntityID().equals(ENTITY_ID) : "Invalid entity id received : " + entityDescriptor.getEntityID();

    }

    @Test
    public void findRoleDescriptorTest() throws Exception {

        MetadataEntry e = cotMgr.findEntityRoleMetadata(ENTITY_ID, "IDPSSODescriptor");
        assert e !=  null : "No IDP SSO Role found";

        IDPSSODescriptorType idpSsoDescriptor = (IDPSSODescriptorType) e.getEntry();
        assert idpSsoDescriptor != null : "No IDP SSO Role descriptor found";

        logger.debug("Found IDP SSO Role descriptor " + idpSsoDescriptor.getID());

    }

    @Test
    public void findEndpointDescriptorTest() throws Exception {
        EndpointDescriptor endpoint = new EndpointDescriptorImpl("TEST-SSOEndpoint",
                SAMLR2MetadataConstants.SingleSignOnService_QNAME.toString(),
                SamlR2Binding.SAMLR2_POST.getValue());
        
        MetadataEntry e = cotMgr.findEndpointMetadata(ENTITY_ID, "IDPSSODescriptor", endpoint);
        assert e !=  null : "No IDP SSO Role found";

        EndpointType endpointType = (EndpointType) e.getEntry();
        assert endpointType != null : "No Endpoint descriptor found";

        logger.debug("Found Endpoint descriptor " + endpointType.getLocation());

    }

}
