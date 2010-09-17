/*
 * Atricore IDBus
 *
 *   Copyright 2009, Atricore Inc.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.services.dto;


/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2ProviderConfigDTO extends AbstractProviderConfigDTO {

    private KeystoreDTO signer;

    private KeystoreDTO encrypter;
    private static final long serialVersionUID = 8401310209898123598L;
    private boolean useSampleStore;

    public KeystoreDTO getSigner() {
        return signer;
    }

    public void setSigner(KeystoreDTO signer) {
        this.signer = signer;
    }

    public KeystoreDTO getEncrypter() {
        return encrypter;
    }

    public void setEncrypter(KeystoreDTO encrypter) {
        this.encrypter = encrypter;
    }

    public boolean isUseSampleStore() {
        return useSampleStore;
    }

    public void setUseSampleStore(boolean useSampleStore) {
        this.useSampleStore = useSampleStore;
    }
}
