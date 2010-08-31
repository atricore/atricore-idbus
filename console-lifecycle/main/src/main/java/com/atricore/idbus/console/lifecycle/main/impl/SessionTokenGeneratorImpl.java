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

package com.atricore.idbus.console.lifecycle.main.impl;

import com.atricore.idbus.console.lifecycle.main.spi.SessionTokenGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * User: cdbirge
 * Date: Nov 4, 2009
 * Time: 12:48:47 PM
 * email: cbirge@atricore.org
 */
public class SessionTokenGeneratorImpl implements SessionTokenGenerator {

    private static final Log logger = LogFactory.getLog(SessionTokenGeneratorImpl.class);

    private int assertionIdLength = 8;

    protected static final String DEFAULT_ALGORITHM = "MD5";

    /**
    * The message digest algorithm to be used when generating Assertion
    * identifiers.  This must be an algorithm supported by the
    * <code>java.security.MessageDigest</code> class on your platform.
    */
    private String _algorithm = DEFAULT_ALGORITHM;

    private String _entropy;
    private Random _random;

    /**
     * The Java class name of the random number generator class to be used
     * when generating assertion identifiers.
     */
    protected String _randomClass = "java.security.SecureRandom";

    /**
     * Generate and return a new assertion identifier.
     * @return -
     */
    public synchronized String generateToken() {

        byte random[] = new byte[16];

        // Render the result as a String of hexadecimal digits
        StringBuffer result = new StringBuffer();
        int resultLenBytes = 0;
        while (resultLenBytes < assertionIdLength) {
            getRandomBytes(random);
            random = getDigest().digest(random);
            for (int j = 0;
                 j < random.length && resultLenBytes < assertionIdLength;
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
        return (result.toString());

    }

    /**
     * Return the MessageDigest object to be used for calculating
     * assertion identifiers.  If none has been created yet, initialize
     * one the first time this method is called.
     * @return -
     */
    public synchronized MessageDigest getDigest() {

        MessageDigest digest = null;
        if (_algorithm != null) {

            try {
                digest = MessageDigest.getInstance(_algorithm);
                logger.debug("Using hash algorithm/encoding : " + _algorithm);
            } catch (NoSuchAlgorithmException e) {
                logger.error("Algorithm not supported : " + _algorithm, e);
                try {
                    digest = MessageDigest.getInstance(DEFAULT_ALGORITHM);
                } catch (NoSuchAlgorithmException f) {
                    logger.error("Algorithm not supported : " + DEFAULT_ALGORITHM, e);
                    digest = null;
                }
            }
        }

        return digest;

    }


    public synchronized Random getRandom() {

        if (_random == null) {
            synchronized (this) {
                if (_random == null) {
                    // Calculate the new random number generator seed
                    long seed = System.currentTimeMillis();
                    long t1 = seed;
                    char entropy[] = getEntropy().toCharArray();
                    for (int i = 0; i < entropy.length; i++) {
                        long update = ((byte) entropy[i]) << ((i % 8) * 8);
                        seed ^= update;
                    }
                    try {
                        // Construct and seed a new random number generator
                        Class clazz = Class.forName(_randomClass);
                        _random = (Random) clazz.newInstance();
                        _random.setSeed(seed);
                    } catch (Exception e) {
                        // Can't instantiate random class, fall back to the simple case
                        logger.error("Can't use random class : " + _randomClass + ", fall back to the simple case.", e);
                        _random = new java.util.Random();
                        _random.setSeed(seed);
                    }
                    // Log a debug msg if this is taking too long ...
                    long t2 = System.currentTimeMillis();
                    if ((t2 - t1) > 100)
                        logger.debug("Delay getting Random with class : " + _randomClass + " [getRandom()] " + (t2 - t1) + " ms.");
                }
            }
        }

        return (_random);

    }

    /**
     * Generate a byte array containing a assertion identifier
     * @param bytes -
     */
    protected void getRandomBytes(byte[] bytes) {
        // Performance may be improved by using O.S. Specific devices like /dev/urandom (see tomcat 5.0)
        Random random = getRandom();
        random.nextBytes(bytes);
    }


    /**
     * Return the entropy increaser value, or compute a semi-useful value
     * if this String has not yet been set.
     * @return -
     */
    public String getEntropy() {

        // Calculate a semi-useful value if this has not been set
        if (_entropy == null)
            setEntropy(this.toString());

        return (_entropy);

    }


    /**
     * Set the entropy increaser value.
     *
     * @param entropy The new entropy increaser value
     */
    public void setEntropy(String entropy) {
        // String oldEntropy = entropy;
        _entropy = entropy;

    }


    /**
     * Return the digest algorithm for the id generator.
     *
     * @return String the algorithm name (i.e. MD5).
     */
    public String getAlgorithm() {
        return _algorithm;
    }

    /**
     * Set the message digest algorithm for the id generator.
     *
     * @param algorithm The new message digest algorithm
     */
    public void setAlgorithm(String algorithm) {
        _algorithm = algorithm;
    }

}
