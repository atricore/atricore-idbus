package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * IdBus dumps download servlet, only parameter supported is dump ID, dumps are found at:
 *
 * $JOSSO2_HOME/data/tmp/dump-<ID>.zip
 */
public class OsgiIDBusDumpServlet extends HttpServlet {

    private static final Log logger = LogFactory.getLog(OsgiIDBusServlet2.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        final int bufferSize = 65536;

        String dumpId = req.getParameter("id");

        logger.info("Requested dump: " + dumpId);

        if (dumpId == null)
            dumpId = "0";

        String dumpFile = "dump-" + dumpId + ".zip";
        String dumpFolder = System.getProperty("java.io.tmpdir");
        if (!dumpFolder.endsWith(File.separator))
            dumpFolder += File.separator;

        String dumpResource = dumpFolder + dumpFile;

        logger.info("Requested dump file " +  dumpResource);

        resp.setBufferSize(bufferSize);
        OutputStream outStream = resp.getOutputStream();

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(new File(dumpResource));
            int bytesRead;
            byte[] buffer = new byte[bufferSize];
            while( (bytesRead = stream.read(buffer, 0, bufferSize)) > 0 ) {
                outStream.write(buffer, 0, bytesRead);
                outStream.flush();
            }
        } finally   {
            if( stream != null )
                stream.close();
            outStream.close();
        }
    }
}
