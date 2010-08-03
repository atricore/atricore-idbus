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

package org.atricore.idbus.kernel.main.mediation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.util.AbstractIdGenerator;

/**
 * @org.apache.xbean.XBean element="artifact-generator"
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: ArtifactGeneratorImpl.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class ArtifactGeneratorImpl extends AbstractIdGenerator implements ArtifactGenerator {

    private static final Log logger = LogFactory.getLog(ArtifactGeneratorImpl.class);

    private int artifactLength = 8;

    private String node;

    /**
     * Generate and return an artifact
     */
    public synchronized String generateId() {

        byte random[] = new byte[16];

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

        if (node != null)
            return node + result.toString();
        
        return result.toString();

    }

    public Artifact generate() {
        return new ArtifactImpl(generateId());
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }
}
