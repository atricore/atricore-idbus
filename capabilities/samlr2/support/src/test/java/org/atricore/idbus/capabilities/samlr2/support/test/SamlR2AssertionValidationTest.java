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

package org.atricore.idbus.capabilities.samlr2.support.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;

/**
 * @author <a href=mailto:ajadzinsky@atricor.org>Alejandro Jadzinsky</a>
 *         User: ajadzinsky
 *         Date: Jun 12, 2009
 */
public class SamlR2AssertionValidationTest {
    private static final Log logger = LogFactory.getLog( SamlR2AssertionValidationTest.class );

    @Test
    public void validateTest () throws Exception {
        // parse an XML document into a DOM tree
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware( true );
        DocumentBuilder parser = dbf.newDocumentBuilder();
        logger.debug( "DocumentBuilder: " + parser.getClass().getCanonicalName() );

//        Document document = parser.parse( new File( "src/test/resources/assertion-001.xml" ) );
        Document document = parser.parse( new File( "src/test/resources/nam-idp-encrypted-assertion.xml" ) );
        logger.debug( "Document loaded: " + (document != null) );

        // create a SchemaFactory capable of understanding WXS schemas
        SchemaFactory factory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
        logger.debug( "SchemeFactory: " + factory.getClass().getCanonicalName() );
        // load a WXS schema, represented by a Schema instance
        File schemaFile = new File( "src/test/resources/saml-schema-assertion-2.0.xsd" );
        logger.debug( "schemaFile name: " + schemaFile.getName() );
        Source schemaSource = new StreamSource( schemaFile );
        logger.debug( "schemaSource system id: " + schemaSource.getSystemId() );
        Schema schema = factory.newSchema( schemaSource );

        // create a Validator instance, which can be used to validate an instance document
        Validator validator = schema.newValidator();
        logger.debug( "Validator: " + validator.getClass().getCanonicalName() );
        // validate the DOM tree
        try {
            validator.validate( new DOMSource( document ) );
            logger.debug( "validation OK !!!" );
        } catch ( Exception e ) {
            logger.debug( "validation ERROR !!!" );
            throw e;
        }
    }
}
