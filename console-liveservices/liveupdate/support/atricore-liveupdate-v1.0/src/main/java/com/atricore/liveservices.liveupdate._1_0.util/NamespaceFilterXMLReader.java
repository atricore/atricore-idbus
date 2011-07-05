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

package com.atricore.liveservices.liveupdate._1_0.util;

import org.xml.sax.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id$
*/
public class NamespaceFilterXMLReader implements XMLReader {

        private XMLReader _reader;

        public NamespaceFilterXMLReader() throws SAXException,
                ParserConfigurationException {
            SAXParserFactory parserFactory;
            parserFactory = SAXParserFactory.newInstance();
            parserFactory.setNamespaceAware(true);
            parserFactory.setValidating(false);
            _reader = parserFactory.newSAXParser().getXMLReader();
        }

        public ContentHandler getContentHandler() {
            return _reader.getContentHandler();
        }

        public DTDHandler getDTDHandler() {
            return _reader.getDTDHandler();
        }

        public EntityResolver getEntityResolver() {
            return _reader.getEntityResolver();
        }

        public ErrorHandler getErrorHandler() {
            return _reader.getErrorHandler();
        }

        public boolean getFeature(String name) throws SAXNotRecognizedException,
                SAXNotSupportedException {
            return _reader.getFeature(name);
        }

        public Object getProperty(String name) throws SAXNotRecognizedException,
                SAXNotSupportedException {
            return _reader.getProperty(name);
        }

        public void parse(InputSource input) throws IOException, SAXException {
            _reader.parse(input);
        }

        public void parse(String systemId) throws IOException, SAXException {
            _reader.parse(systemId);
        }

        public void setContentHandler(ContentHandler handler) {
// This is jaxb inserting its sax-&gt;jaxb connector
            _reader.setContentHandler(new NamespaceFilterHandler(handler));
        }

        public void setDTDHandler(DTDHandler handler) {
            _reader.setDTDHandler(handler);
        }

        public void setEntityResolver(EntityResolver resolver) {
            _reader.setEntityResolver(resolver);
        }

        public void setErrorHandler(ErrorHandler handler) {
            _reader.setErrorHandler(handler);
        }

        public void setFeature(String name, boolean value)
                throws SAXNotRecognizedException, SAXNotSupportedException {
            _reader.setFeature(name, value);
        }

        public void setProperty(String name, Object value)
                throws SAXNotRecognizedException, SAXNotSupportedException {
            _reader.setProperty(name, value);
        }

    }
