package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Response;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class InternalHttpServletResponse extends Response {

    public InternalHttpServletResponse(HttpConnection connection) {
        super(connection);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("NOT SUPPORTED IN THIS IMPLEMENTATION!");
    }


}
