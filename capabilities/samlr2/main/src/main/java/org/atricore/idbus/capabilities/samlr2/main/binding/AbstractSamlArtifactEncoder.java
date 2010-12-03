package org.atricore.idbus.capabilities.samlr2.main.binding;

import java.nio.ByteBuffer;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public abstract class AbstractSamlArtifactEncoder implements SamlArtifactEncoder {

    /**
     * Only works for ints between 0 and 65535
     */
    protected int toInt(byte[] b) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.put((byte) 0x00);
        bb.put((byte) 0x00);
        bb.put(b[0]);
        bb.put(b[1]);

        return bb.getInt(0);


    }

    /**
     * It turns an int in a bin array (only uses two bytes!)
     * Only works for ints between 0 and 65535
     */
    protected byte[] toBin(int i) {

        byte[] bytes = ByteBuffer.allocate(4).putInt(i).array();
        return copyOfRange(bytes, 2, 4);
    }

    protected byte[] toBin(String value, int length) {

        byte[] b = value.getBytes();
        int pad = length - b.length;

        ByteBuffer bf = ByteBuffer.allocate(length);
        for (int i = 0; i < pad; i++) {
            bf.put((byte) 0x0);
        }
        bf.put(b);

        return bf.array();

    }

    protected String toString(byte[] b) {
        return new String(b).trim();
    }

    // To provide JDK 5 Compat
    protected byte[] copyOfRange(byte[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        byte[] copy = new byte[newLength];
        System.arraycopy(original, from, copy, 0,
                         Math.min(original.length - from, newLength));
        return copy;
    }

    
}
