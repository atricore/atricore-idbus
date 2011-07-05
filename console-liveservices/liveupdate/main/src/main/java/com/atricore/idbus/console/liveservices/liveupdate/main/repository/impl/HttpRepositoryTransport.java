package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.repository.RepositoryTransport;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.RepositoryTransportException;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class HttpRepositoryTransport implements RepositoryTransport {

    private HttpClient httpClient;
    
    public HttpRepositoryTransport() {
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		httpClient = new HttpClient(connectionManager);
    }
    
    public boolean canHandle(URI uri) {
        return uri.getScheme() != null &&
                (uri.getScheme().equals("http") || uri.getScheme().equals("https"));
    }

    public byte[] loadContent(URI uri) throws RepositoryTransportException {

        InputStream is = null;
        try {

            // Reuse IS creation operaiton!
            is = getContentStream(uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
            IOUtils.copy(is, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RepositoryTransportException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }



    }

    public InputStream getContentStream(URI uri) throws RepositoryTransportException {
        GetMethod get = new GetMethod(uri.toString());

        try {

            String userInfo = uri.getUserInfo();
            if (userInfo != null && userInfo.contains(":")) {

                int idx = userInfo.indexOf(":");

                String user = userInfo.substring(0, idx);
                String pwd = userInfo.substring(idx + 1);

                UsernamePasswordCredentials creds =
                    new UsernamePasswordCredentials(user, pwd);

                AuthScope authscope = new AuthScope(
                    uri.getHost(),
                    uri.getPort(),
                    AuthScope.ANY_REALM);

                httpClient.getState().setCredentials(authscope, creds);
            }

            int statusCode = httpClient.executeMethod(get);
            if (statusCode != HttpStatus.SC_OK) {
                throw new RepositoryTransportException("Error getting file: " + statusCode);
            }
            return get.getResponseBodyAsStream();
        } catch (HttpException e) {
            throw new RepositoryTransportException(e);
        } catch (IOException e) {
            throw new RepositoryTransportException(e);
        } finally {
            // Do not release, since it closes the InputStream : get.releaseConnection();
        }
    }
}
