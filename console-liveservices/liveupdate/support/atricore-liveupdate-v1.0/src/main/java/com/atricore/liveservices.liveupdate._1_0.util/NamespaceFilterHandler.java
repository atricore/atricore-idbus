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

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id$
*/
public class NamespaceFilterHandler implements ContentHandler {

        ContentHandler _handler;

        public NamespaceFilterHandler(ContentHandler ch) {
            _handler = ch;
        }

        public void startElement(String uri, String localName, String qName,
                                 Attributes atts) throws SAXException {

            if (!qName.equals(localName)) {
                _handler.startElement("", localName, localName, atts);
            } else {
                _handler.startElement(uri, localName, qName, atts);
            }
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            _handler.characters(ch, start, length);
        }

        public void endDocument() throws SAXException {
            _handler.endDocument();
        }

        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            _handler.endElement(uri, localName, qName);
        }

        public void endPrefixMapping(String prefix) throws SAXException {
            _handler.endPrefixMapping(prefix);
        }

        public void ignorableWhitespace(char[] ch, int start, int length)
                throws SAXException {
            _handler.ignorableWhitespace(ch, start, length);
        }

        public void processingInstruction(String target, String data)
                throws SAXException {
            _handler.processingInstruction(target, data);
        }

        public void setDocumentLocator(Locator locator) {
            _handler.setDocumentLocator(locator);
        }

        public void skippedEntity(String name) throws SAXException {
            _handler.skippedEntity(name);
        }

        public void startDocument() throws SAXException {
            _handler.startDocument();
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            _handler.startPrefixMapping(prefix, uri);
        }

    }
