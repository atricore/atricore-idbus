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

import java.util.Date;

public class GetCertificateInfoResponse extends AbstractManagementResponse {

    // signing certificate
    private String signingCertIssuerDN;
    private String signingCertSubjectDN;
    private Date signingCertNotBefore;
    private Date signingCertNotAfter;

    // encryption certificate
    private String encryptionCertIssuerDN;
    private String encryptionCertSubjectDN;
    private Date encryptionCertNotBefore;
    private Date encryptionCertNotAfter;

    public String getEncryptionCertIssuerDN() {
        return encryptionCertIssuerDN;
    }

    public void setEncryptionCertIssuerDN(String encryptionCertIssuerDN) {
        this.encryptionCertIssuerDN = encryptionCertIssuerDN;
    }

    public Date getEncryptionCertNotAfter() {
        return encryptionCertNotAfter;
    }

    public void setEncryptionCertNotAfter(Date encryptionCertNotAfter) {
        this.encryptionCertNotAfter = encryptionCertNotAfter;
    }

    public Date getEncryptionCertNotBefore() {
        return encryptionCertNotBefore;
    }

    public void setEncryptionCertNotBefore(Date encryptionCertNotBefore) {
        this.encryptionCertNotBefore = encryptionCertNotBefore;
    }

    public String getEncryptionCertSubjectDN() {
        return encryptionCertSubjectDN;
    }

    public void setEncryptionCertSubjectDN(String encryptionCertSubjectDN) {
        this.encryptionCertSubjectDN = encryptionCertSubjectDN;
    }

    public String getSigningCertIssuerDN() {
        return signingCertIssuerDN;
    }

    public void setSigningCertIssuerDN(String signingCertIssuerDN) {
        this.signingCertIssuerDN = signingCertIssuerDN;
    }

    public Date getSigningCertNotAfter() {
        return signingCertNotAfter;
    }

    public void setSigningCertNotAfter(Date signingCertNotAfter) {
        this.signingCertNotAfter = signingCertNotAfter;
    }

    public Date getSigningCertNotBefore() {
        return signingCertNotBefore;
    }

    public void setSigningCertNotBefore(Date signingCertNotBefore) {
        this.signingCertNotBefore = signingCertNotBefore;
    }

    public String getSigningCertSubjectDN() {
        return signingCertSubjectDN;
    }

    public void setSigningCertSubjectDN(String signingCertSubjectDN) {
        this.signingCertSubjectDN = signingCertSubjectDN;
    }
}
