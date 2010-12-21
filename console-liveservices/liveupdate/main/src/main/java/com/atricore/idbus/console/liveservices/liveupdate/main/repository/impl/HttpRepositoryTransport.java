package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.repository.RepositoryTransport;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.RepositoryTransportException;

import java.net.URI;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class HttpRepositoryTransport implements RepositoryTransport {

    public boolean canHandle(URI uri) {
        return uri.getScheme() != null &&
                (uri.getScheme().equals("https") || uri.getScheme().equals("https"));
    }

    public byte[] loadContent(URI uri) throws RepositoryTransportException {
        // Create HTTP Request for URI and retrieve content ...
        // TODO : Implement me

        return new byte[0];
    }
}
