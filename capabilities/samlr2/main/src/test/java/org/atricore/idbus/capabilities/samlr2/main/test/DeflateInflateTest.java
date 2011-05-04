package org.atricore.idbus.capabilities.samlr2.main.test;

import org.apache.commons.codec.binary.Base64;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.net.URLDecoder;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DeflateInflateTest {

    @BeforeClass
    public static void setupTestSuite() throws Exception {
    }

    @Test
    public void deflateInflate() throws Exception {

        String s = deflate("Hello, World!", true);
        System.out.println("DF:" + s) ;
        s = inflate(s, true);
        System.out.println("IF:" + s);



    }

    @Test
    public void inflateXml() throws Exception {
        String s = "pZJNb+IwEIb/iuUeOBHbST+CRagoCVKk0kWEVtVeVm7sgqXERhlTuv9+HSCoXan00IMtefTMO+94Znj7XlfoTTWgrUl6LKA9pExppTarpPe4nPbj3u1oCKKuNnyhYGMNKORzDPB3kAleO7fhhOx2u2AXBbZZkZBSRp5n90W5VrXAR9iLfgnTS+IhT1x09L5egreN4VaABm5ErYC7khfj2T0PA8o3jXW2tFWXIuELfUrooNWXoFefCpzXFwCqcf5XMJq0TRt3ni8PELcvTmijJEapAqeNaDVO1ipbimptwfGYxozk6d1j4e8xI8XcHy8WkvGk+LPIivmvhyIjiyzNFxjlAFuVG3Ci9RFSxvr0qk+jZcg4ozwMA0YHvzF6Ok4Se0s+y3QjW9oEaxlN0uv4ckzpzdV1FjHmibSNT+8GLKRxlmZZPMjiCB8mzvdVGzS1TS2+ab+NaNl/3aN+2k67v3j0bdd5emp7lg7Jh6rd0hVOuC18fk2sVOhJVFt13hTs6ePeStXgH8k82LlfCv2mMBkdrH4U+i90enYTGP0D";

        System.out.println("DFXML:" + s);
        s = inflate(s, true);
        System.out.println("IFXML:" + s);
    }

    protected String deflate(String in, boolean encode) throws Exception {

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        DeflaterOutputStream deflated = new DeflaterOutputStream(bytesOut, new Deflater(Deflater.DEFAULT_COMPRESSION, true));
        ByteArrayInputStream inflated = new ByteArrayInputStream(in.getBytes("UTF-8"));


        byte[] buf = new byte[1024];
        int read = inflated.read(buf);
        while (read > 0) {
            deflated.write(buf, 0, read);
            read = inflated.read(buf);
        }


        deflated.flush();
        deflated.finish();


        byte[] encodedbytes = bytesOut.toByteArray();
        if (encode) {
            encodedbytes = new Base64().encode(encodedbytes);
        }

        deflated.close();

        return new String(encodedbytes);

    }

    protected String inflate(String in, boolean decode) throws Exception {

        byte[] decodedBytes = in.getBytes();
        if (decode) {
            decodedBytes = new Base64().decode(in.getBytes());
        }

        ByteArrayInputStream bytesIn = new ByteArrayInputStream(decodedBytes);
        InputStream inflater = new InflaterInputStream(bytesIn, new Inflater(true));

        // This gets rid of platform specific EOL chars ...
        BufferedReader r = new BufferedReader(new InputStreamReader(inflater));
        StringBuffer sb = new StringBuffer();

        String l = r.readLine();
        while (l != null) {
            sb.append(l);
            l = r.readLine();
        }

        return sb.toString();

    }

}
