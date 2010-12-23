package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.repository.RepositoryTransport;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.RepositoryTransportException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.IOException;
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
        GetMethod get = new GetMethod(uri.toString());

        try {
            int statusCode = httpClient.executeMethod(get);
            if (statusCode != HttpStatus.SC_OK) {
                throw new RepositoryTransportException("Error getting file: " + statusCode);
            }
            String response = get.getResponseBodyAsString();
            return response.getBytes();
        } catch (HttpException e) {
            throw new RepositoryTransportException(e);
        } catch (IOException e) {
            throw new RepositoryTransportException(e);
        } finally {
            get.releaseConnection();
        }
    }
}
