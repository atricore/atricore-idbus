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

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;

/**
 * Helper class to make JAXB play nice with XMLDSig by removing corresponding prefixes from outcome
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id$
*/
public class NamespaceFilterXMLStreamWriter implements XMLStreamWriter {
        XMLStreamWriter writer;

        public NamespaceFilterXMLStreamWriter(Writer w) throws XMLStreamException {
            writer = XMLOutputFactory.newInstance().createXMLStreamWriter(w);
        }

        public void writeStartElement(String localName) throws XMLStreamException {
            writer.writeStartElement(localName);
        }

        public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
            writer.writeStartElement(namespaceURI, localName);
        }

        public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
            if (localName.equals("Signature")) {
                 writer.writeStartElement(namespaceURI, localName);
                 writeDefaultNamespace("http://www.w3.org/2000/09/xmldsig#");
            } else
            if (namespaceURI.equals("http://www.w3.org/2000/09/xmldsig#")) {
                writer.writeStartElement(namespaceURI, localName);
            } else {
                writer.writeStartElement(prefix, localName, namespaceURI);
            }
        }

        public void writeEmptyElement(String s, String s1) throws XMLStreamException {
            writer.writeEmptyElement(s, s1);
        }

        public void writeEmptyElement(String s, String s1, String s2) throws XMLStreamException {
            writer.writeEmptyElement(s, s1, s2);
        }

        public void writeEmptyElement(String s) throws XMLStreamException {
            writer.writeEmptyElement(s);
        }

        public void writeEndElement() throws XMLStreamException {
            writer.writeEndElement();
        }

        public void writeEndDocument() throws XMLStreamException {
            writer.writeEndDocument();
        }

        public void close() throws XMLStreamException {
            writer.close();
        }

        public void flush() throws XMLStreamException {
            writer.flush();
        }

        public void writeAttribute(String s, String s1) throws XMLStreamException {
            writer.writeAttribute(s, s1);
        }

        public void writeAttribute(String s, String s1, String s2, String s3) throws XMLStreamException {
            writer.writeAttribute(s, s1, s2, s3);
        }

        public void writeAttribute(String s, String s1, String s2) throws XMLStreamException {
            writer.writeAttribute(s, s1, s2);
        }

        public void writeNamespace(String s, String s1) throws XMLStreamException {
            writer.writeNamespace(s, s1);
        }

        public void writeDefaultNamespace(String s) throws XMLStreamException {
            writer.writeDefaultNamespace(s);
        }

        public void writeComment(String s) throws XMLStreamException {
            writer.writeComment(s);
        }

        public void writeProcessingInstruction(String s) throws XMLStreamException {
            writer.writeProcessingInstruction(s);
        }

        public void writeProcessingInstruction(String s, String s1) throws XMLStreamException {
            writer.writeProcessingInstruction(s, s1);
        }

        public void writeCData(String s) throws XMLStreamException {
            writer.writeCData(s);
        }

        public void writeDTD(String s) throws XMLStreamException {
            writer.writeDTD(s);
        }

        public void writeEntityRef(String s) throws XMLStreamException {
            writer.writeEntityRef(s);
        }

        public void writeStartDocument() throws XMLStreamException {
            writer.writeStartDocument();
        }

        public void writeStartDocument(String s) throws XMLStreamException {
            writer.writeStartDocument(s);
        }

        public void writeStartDocument(String s, String s1) throws XMLStreamException {
            writer.writeStartDocument(s, s1);
        }

        public void writeCharacters(String s) throws XMLStreamException {
            writer.writeCharacters(s);
        }

        public void writeCharacters(char[] chars, int i, int i1) throws XMLStreamException {
            writer.writeCharacters(chars, i, i1);
        }

        public String getPrefix(String s) throws XMLStreamException {
            return writer.getPrefix(s);
        }

        public void setPrefix(String s, String s1) throws XMLStreamException {
            writer.setPrefix(s, s1);
        }

        public void setDefaultNamespace(String s) throws XMLStreamException {
            writer.setDefaultNamespace(s);
        }

        public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
            writer.setNamespaceContext(namespaceContext);
        }

        public NamespaceContext getNamespaceContext() {
            return writer.getNamespaceContext();
        }

        public Object getProperty(String s) throws IllegalArgumentException {
            return writer.getProperty(s);
        }
    }

