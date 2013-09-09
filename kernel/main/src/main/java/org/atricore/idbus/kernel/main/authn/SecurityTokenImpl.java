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

package org.atricore.idbus.kernel.main.authn;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: JOSSO11BindingRouteTest.java 1077 2009-03-20 22:27:50Z ajadzinsky $
 */
public class SecurityTokenImpl<T> implements SecurityToken<T> {
    private String id;
    private String nameIdentifier;
    private T content;
    private String serializedContent;
    private long issueInstant;
    private long expiresOn;


    public SecurityTokenImpl(String id, T content) {
        this(id, null, content);
    }

    public SecurityTokenImpl(String id, String nameIdentifier, T content) {
        this.id = id;
        this.nameIdentifier = nameIdentifier;
        this.content = content;
        this.issueInstant = System.currentTimeMillis();
        this.expiresOn = -1;
    }

    public SecurityTokenImpl(String id, String nameIdentifier, T content, String serializedContent) {
        this.id = id;
        this.nameIdentifier = nameIdentifier;
        this.content = content;
        this.serializedContent = serializedContent;
        this.issueInstant = System.currentTimeMillis();
        this.expiresOn = -1;
    }

    public SecurityTokenImpl(String id, String nameIdentifier, T content, String serializedContent, long issueInstant) {
        this.id = id;
        this.nameIdentifier = nameIdentifier;
        this.content = content;
        this.serializedContent = serializedContent;
        this.issueInstant = issueInstant;
        this.expiresOn = -1;
    }

    public String getId() {
        return id;
    }

    public T getContent() {
        return content;
    }

    public String getNameIdentifier() {
        return nameIdentifier;
    }

    public String getSerializedContent() {
        return serializedContent;
    }

    public long getIssueInstant() {
        return issueInstant;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNameIdentifier(String nameIdentifier) {
        this.nameIdentifier = nameIdentifier;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public void setSerializedContent(String serializedContent) {
        this.serializedContent = serializedContent;
    }

    public void setIssueInstant(long issueInstant) {
        this.issueInstant = issueInstant;
    }


    public long getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(long expiresOn) {
        this.expiresOn = expiresOn;
    }
}
