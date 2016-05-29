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

package org.atricore.idbus.kernel.main.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Use org.atricore.idbus.uuid.jdk system property to enable built-in JDK UUIDs
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: UUIDGenerator.java 1305 2009-06-18 14:19:47Z sgonzalez $
 */
public class UUIDGenerator extends AbstractIdGenerator {

    private static final Log logger = LogFactory.getLog(UUIDGenerator.class);

    private int artifactLength = 8;

    private boolean jdkIdGen = false;

    /**
     * Only works with legacy UUID generator
     */
    public UUIDGenerator(int artifactLength) {
        this.jdkIdGen = false;
        this.artifactLength = artifactLength;
        setPrefix("id");
    }

    public UUIDGenerator(boolean jdkIdGen) {
        this.jdkIdGen = jdkIdGen;
        setPrefix("id");
    }

    public UUIDGenerator() {
        this.jdkIdGen = false;
    }


    /**
     * Generates a string identifier
     */
    public synchronized String generateId() {
        if (System.getProperty("org.atricore.idbus.uuid.jdk") != null || jdkIdGen) {
            return jdkUUID();
        }

        return legacyUUID(this.artifactLength);
    }

    /**
     * Generates a long identifier
     */
    public synchronized long generateLongId() {
        return Long.parseLong(legacyUUID(4).substring(2), 16);
    }

    /**
     * Generates UUIDs using JDK's built-in implementation
     */
    protected String jdkUUID() {
        return "id-" + java.util.UUID.randomUUID().toString();
    }

    /**
     * Generates HEX identifier, twice as long as artifactLength
     */
    protected String legacyUUID(int artifactLength) {

        byte random[] = new byte[artifactLength * 2];

        // Render the result as a String of hexadecimal digits
        StringBuffer result = new StringBuffer();
        int resultLenBytes = 0;
        while (resultLenBytes < artifactLength) {
            getRandomBytes(random);
            random = getDigest().digest(random);
            for (int j = 0;
                 j < random.length && resultLenBytes < artifactLength;
                 j++) {
                byte b1 = (byte) ((random[j] & 0xf0) >> 4);
                byte b2 = (byte) (random[j] & 0x0f);
                if (b1 < 10)
                    result.append((char) ('0' + b1));
                else
                    result.append((char) ('A' + (b1 - 10)));
                if (b2 < 10)
                    result.append((char) ('0' + b2));
                else
                    result.append((char) ('A' + (b2 - 10)));
                resultLenBytes++;
            }
        }

        return (getPrefix() != null ? getPrefix() + result.toString() : result.toString());
    }

}
