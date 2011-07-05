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

package com.atricore.josso2.licensing._1_0.util;

import javax.xml.transform.stream.StreamSource;
import java.io.*;

/**
 * A helper class which provides a JAXP {@link javax.xml.transform.Source Source} from a String which can
 * be read as many times as required. Encoding is default UTF-8.
 *
 * @version $Revision: 1307 $
 */
public class StringSource extends StreamSource implements Externalizable {
    private String text;
    private String encoding = "UTF-8";

    public StringSource() {
    }

    public StringSource(String text) {
        assert text != null : "text cannot be null";
        this.text = text;
    }

    public StringSource(String text, String systemId) {
        this(text);
        assert systemId != null : "systemId cannot be null";
        setSystemId(systemId);
    }

    public StringSource(String text, String systemId, String encoding) {
        this(text, systemId);
        assert encoding != null : "encoding cannot be null";
        this.encoding = encoding;
    }

    public InputStream getInputStream() {
        try {
            return new ByteArrayInputStream(text.getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public Reader getReader() {
        return new StringReader(text);
    }

    public String toString() {
        return "StringSource[" + text + "]";
    }

    public String getText() {
        return text;
    }

    public String getEncoding() {
        return encoding;
    }

    /**
     * @deprecated will be removed in Camel 2.0
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * @deprecated will be removed in Camel 2.0
     */
    public void setText(String text) {
        this.text = text;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        int b = (text != null ? 0x01 : 0x00) + (encoding != null ? 0x02 : 0x00)
                + (getPublicId() != null ? 0x04 : 0x00) + (getSystemId() != null ? 0x08 : 0x00);
        out.writeByte(b);
        if ((b & 0x01) != 0) {
            out.writeUTF(text);
        }
        if ((b & 0x02) != 0) {
            out.writeUTF(encoding);
        }
        if ((b & 0x04) != 0) {
            out.writeUTF(getPublicId());
        }
        if ((b & 0x08) != 0) {
            out.writeUTF(getSystemId());
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int b = in.readByte();
        if ((b & 0x01) != 0) {
            text = in.readUTF();
        }
        if ((b & 0x02) != 0) {
            encoding = in.readUTF();
        }
        if ((b & 0x04) != 0) {
            setPublicId(in.readUTF());
        }
        if ((b & 0x08) != 0) {
            setSystemId(in.readUTF());
        }
    }
}