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

package org.atricore.idbus.capabilities.csca;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.CredentialProvider;
import org.atricore.idbus.kernel.main.authn.scheme.UserNameCredential;
import org.atricore.idbus.kernel.main.provisioning.domain.User;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class X509CertificateCredentialProvider implements CredentialProvider {
    private static final Log logger = LogFactory.getLog(X509CertificateCredentialProvider.class);

    /**
     * The name of the credential representing an X.509 Certificate.
     * Used to get a new credential instance based on its name and value.
     * Value : userCertificate
     *
     * @see Credential newCredential(String name, Object value)
     */
    private final static String X509_CERTIFICATE_CREDENTIAL_NAME = "cscaSecurityToken";

    public Credential newCredential(String name, Object value) {

        if (name.equalsIgnoreCase(X509_CERTIFICATE_CREDENTIAL_NAME)) {

            if (value instanceof X509Certificate)
                return new X509CertificateCredential(value);
            else if (value instanceof String) {
                logger.trace("Certificate is " + value);
                X509Certificate cert = buildX509Certificate((String) value);
                return new X509CertificateCredential(cert);
            } else {
                X509Certificate cert = buildX509Certificate((byte[]) value);
                return new X509CertificateCredential(cert);
            }
        }

        // Don't know how to handle this name ...
        if (logger.isDebugEnabled())
            logger.debug("Unknown credential name : " + name);

        return null;
    }

    public Credential newEncodedCredential(String name, Object value) {
        return newCredential(name, value);
    }

    @Override
    public Credential[] newCredentials(User user) {

        byte[] binCert = user.getUserCertificate();

        if (binCert == null)
            return null;
        X509Certificate cert = buildX509Certificate(binCert);

        return new Credential[] {newCredential(X509_CERTIFICATE_CREDENTIAL_NAME, cert)};
    }

    private X509Certificate buildX509Certificate(byte[] binaryCert) {
        X509Certificate cert = null;

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decodeBase64(binaryCert));
            CertificateFactory cf =
                    CertificateFactory.getInstance("X.509");

            cert = (X509Certificate) cf.generateCertificate(bais);

            if (logger.isDebugEnabled())
                logger.debug("Building X.509 certificate result :\n " + cert);

        } catch (CertificateException ce) {
            logger.error("Error instantiating X.509 Certificate", ce);
        }

        return cert;
    }

    private X509Certificate buildX509Certificate(String plainCert) {
        return buildX509Certificate(plainCert.getBytes());
    }

}
