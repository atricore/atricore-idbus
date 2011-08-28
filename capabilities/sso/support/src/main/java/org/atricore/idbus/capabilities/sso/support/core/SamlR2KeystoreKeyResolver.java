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

package org.atricore.idbus.capabilities.sso.support.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;


/**
 * @org.apache.xbean.XBean element="keystore-keyresolver"
 *
 * @author <a href=mailto:ajadzinsky@atricore.org>Alejandro Jadzinsky</a>
 *         User: ajadzinsky
 *         Date: Jun 9, 2009
 */
public class SamlR2KeystoreKeyResolver extends SamlR2KeyResolverImpl {

    private static final Log logger = LogFactory.getLog(SamlR2KeystoreKeyResolver.class);

    private KeyStore keystore;
    private Boolean initiated = false;
    private String keystoreType = "JKS";
    private Resource  keystoreFile;
    private String keystorePass;
    private String privateKeyAlias;
    private String publicKeyAlias;
    private String privateKeyPass;
    private String certificateAlias;

    public String getKeystoreType() {
        return keystoreType;
    }

    public void setKeystoreType(String keystoreType) {
        this.keystoreType = keystoreType;
    }

    public Resource getKeystoreFile() {
        return keystoreFile;
    }

    public void setKeystoreFile(Resource keystoreFile) {
        this.keystoreFile = keystoreFile;
    }

    public String getKeystorePass() {
        return keystorePass;
    }

    public void setKeystorePass(String keystorePass) {
        this.keystorePass = keystorePass;
    }

    public String getPublicKeyAlias() {
        return publicKeyAlias;
    }

    public void setPublicKeyAlias(String publicKeyAlias) {
        this.publicKeyAlias = publicKeyAlias;
    }

    public String getPrivateKeyAlias() {
        return privateKeyAlias;
    }

    public void setPrivateKeyAlias(String privateKeyAlias) {
        this.privateKeyAlias = privateKeyAlias;
    }

    public String getPrivateKeyPass() {
        return privateKeyPass;
    }

    public void setPrivateKeyPass(String privateKeyPass) {
        this.privateKeyPass = privateKeyPass;
    }

    public String getCertificateAlias() {
        return certificateAlias;
    }

    public void setCertificateAlias(String certificateAlias) {
        this.certificateAlias = certificateAlias;
    }

    public void init() throws SamlR2KeyResolverException {
        InputStream is = null;
        try {
            keystore = KeyStore.getInstance(keystoreType);

            if (keystoreFile == null)
                throw new IllegalStateException("No keystore resource defined!");        

            // DO NOT CLOSE THE INPUT STREAM !
            is = keystoreFile.getInputStream();

            if (is == null)
                throw new SamlR2KeyResolverException("Keystore not found " + keystoreFile);                     

            //load the keystore
            keystore.load(is, keystorePass.toCharArray());

            if (certificateAlias != null) {
                certificate = keystore.getCertificate(certificateAlias);
                publicKey = certificate.getPublicKey();
            }

            if (privateKeyAlias != null)
                privateKey = (PrivateKey) keystore.getKey(privateKeyAlias, privateKeyPass != null ? privateKeyPass.toCharArray() : null);

            initiated = true;
        } catch (Exception e) {
            throw new SamlR2KeyResolverException("Error accessing or reading keystore", e);
        }

        if (privateKeyAlias != null && privateKey == null)
            throw new SamlR2KeyResolverException("No private key found for : " + privateKeyAlias + " in " + keystoreFile);

        if (logger.isDebugEnabled())
            logger.debug("Found private key : " +
                " Format:" + privateKey.getFormat() +
                " Algorithm:" + privateKey.getAlgorithm() +
                " Class:" + privateKey.getClass().getName());
        
        if (certificateAlias != null && certificate == null)
            throw new SamlR2KeyResolverException("No certificate found for : " + certificateAlias + " in " + keystoreFile);

        if (logger.isDebugEnabled())
            logger.debug("Found certificate : " +
                 " Type:" + certificate.getType() +
                 " Class:" + certificate.getClass().getName());


    }

    @Override
    public Certificate getCertificate() throws SamlR2KeyResolverException {
        if (!initiated)
            init();

        return super.getCertificate();
    }

    @Override
    public PrivateKey getPrivateKey() throws SamlR2KeyResolverException {
        if (!initiated)
            init();

        return super.getPrivateKey();
    }

    @Override
    public PublicKey getPublicKey() throws SamlR2KeyResolverException {
        if (!initiated)
            init();

        return super.getPublicKey();
    }

    public KeyStore getKeystore() {
        return this.keystore;
    }
}
