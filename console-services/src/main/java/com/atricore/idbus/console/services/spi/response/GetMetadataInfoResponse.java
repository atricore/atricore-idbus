/*
 * Atricore Console
 *
 * Copyright 2009-2010, Atricore Inc.
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

package com.atricore.idbus.console.services.spi.response;

public class GetMetadataInfoResponse extends GetCertificateInfoResponse {

    private String entityId;

    // profiles
    private boolean ssoEnabled;
    private boolean sloEnabled;

    // bindings
    private boolean postEnabled;
    private boolean redirectEnabled;
    private boolean artifactEnabled;
    private boolean soapEnabled;

    private boolean wantAuthnRequestsSigned;
    private boolean signAuthnRequests;
    private boolean wantAssertionSigned;

    public GetMetadataInfoResponse() {
        super();
        ssoEnabled = false;
        sloEnabled = false;
        postEnabled = false;
        redirectEnabled = false;
        artifactEnabled = false;
        soapEnabled = false;
        wantAuthnRequestsSigned = false;
        signAuthnRequests = false;
        wantAssertionSigned = false;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public boolean isSloEnabled() {
        return sloEnabled;
    }

    public void setSloEnabled(boolean sloEnabled) {
        this.sloEnabled = sloEnabled;
    }

    public boolean isSsoEnabled() {
        return ssoEnabled;
    }

    public void setSsoEnabled(boolean ssoEnabled) {
        this.ssoEnabled = ssoEnabled;
    }

    public boolean isArtifactEnabled() {
        return artifactEnabled;
    }

    public void setArtifactEnabled(boolean artifactEnabled) {
        this.artifactEnabled = artifactEnabled;
    }

    public boolean isPostEnabled() {
        return postEnabled;
    }

    public void setPostEnabled(boolean postEnabled) {
        this.postEnabled = postEnabled;
    }

    public boolean isRedirectEnabled() {
        return redirectEnabled;
    }

    public void setRedirectEnabled(boolean redirectEnabled) {
        this.redirectEnabled = redirectEnabled;
    }

    public boolean isSoapEnabled() {
        return soapEnabled;
    }

    public void setSoapEnabled(boolean soapEnabled) {
        this.soapEnabled = soapEnabled;
    }

    public boolean isSignAuthnRequests() {
        return signAuthnRequests;
    }

    public void setSignAuthnRequests(boolean signAuthnRequests) {
        this.signAuthnRequests = signAuthnRequests;
    }

    public boolean isWantAssertionSigned() {
        return wantAssertionSigned;
    }

    public void setWantAssertionSigned(boolean wantAssertionSigned) {
        this.wantAssertionSigned = wantAssertionSigned;
    }

    public boolean isWantAuthnRequestsSigned() {
        return wantAuthnRequestsSigned;
    }

    public void setWantAuthnRequestsSigned(boolean wantAuthnRequestsSigned) {
        this.wantAuthnRequestsSigned = wantAuthnRequestsSigned;
    }
}
