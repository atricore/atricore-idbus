package org.atricore.idbus.capabilities.spnego.jaas;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.StringTokenizer;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class KerberosConfigUtil {
    
    private static Log logger = LogFactory.getLog(KerberosConfigUtil.class);

    /**
     * Translate from kerberos to ini format!
     * @param krb5Conf
     * @return
     * @throws IOException
     */
    public static InputStream toIni(InputStream krb5Conf) throws IOException {

        BufferedReader r =  new BufferedReader(new InputStreamReader(krb5Conf));
        String out = "";

        String l = r.readLine();
        l = l.replace('\r', ' ');
        l = l.replace('\n', ' ');
        boolean appendEOL =true;

        while (l != null) {


            if (l.indexOf('{') >= 0) {
                // all
                appendEOL = false;
            } else if (l.indexOf('}') >= 0) {
                appendEOL = true;
            }

            out += l + (appendEOL ? "\n" : "");
            if (logger.isTraceEnabled()) logger.trace((l + (appendEOL ? "\n" : "")));

            l = r.readLine();
        }

        if (logger.isTraceEnabled()) logger.trace(("--------------------------- BEGIN INI ----------------"));
        if (logger.isTraceEnabled()) logger.trace((out));
        if (logger.isTraceEnabled()) logger.trace(("--------------------------- END INI ----------------"));


        return new ByteArrayInputStream(out.getBytes());

    }

    /**
     * Translate from Ini to Kerberos syntax
     * @param ini
     * @return
     * @throws IOException
     */
    public static InputStream toKrb5(InputStream ini) throws IOException {
        BufferedReader r =  new BufferedReader(new InputStreamReader(ini));
        String out = "";

        String l = r.readLine();
        while (l != null) {

            l = l.replace('\r', ' ');
            l = l.replace('\n', ' ');

            if (l.indexOf('{') >= 0) {
                // change this xxx...=...{...<>...=...<>...<>...=...<>...}...
                // into this <>=<>....<>=<>...<>=<>

                String prefix = l.substring(0, l.indexOf('{') + 1);
                out += prefix + "\n";

                l = l.substring(l.indexOf('{') + 1);
                l = l.substring(0, l.indexOf('}'));

                l = l.replaceAll("=[ \t]+", "=");
                l = l.replaceAll("[ \t]+=", "=");

                if (logger.isTraceEnabled()) logger.trace((">>> " + l));

                StringTokenizer st = new StringTokenizer(l, " ", false);

                while(st.hasMoreTokens()) {
                    String nv = st.nextToken();

                    String name = nv.substring(0, nv.indexOf('='));
                    String value = nv.substring(nv.indexOf('=') + 1);

                    out += "  " + name + " = " + value + "\n";
                    if (logger.isTraceEnabled()) logger.trace((name + " = " + value + "\n"));


                }

                out += "}\n";
                if (logger.isTraceEnabled()) logger.trace(("}\n"));

            } else {
                out += l + "\n";
                if (logger.isTraceEnabled()) logger.trace((l + "\n"));
            }

            //out += l + (appendEOL ? "\n" : "");
            l = r.readLine();
        }

        if (logger.isTraceEnabled()) logger.trace(("--------------------------- BEGIN KRB5 ----------------"));
        if (logger.isTraceEnabled()) logger.trace((out));
        if (logger.isTraceEnabled()) logger.trace(("--------------------------- END KRB5 ----------------"));
        return new ByteArrayInputStream(out.getBytes());

    }
}
