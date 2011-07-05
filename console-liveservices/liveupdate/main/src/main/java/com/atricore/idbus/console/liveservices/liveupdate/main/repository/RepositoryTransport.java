package com.atricore.idbus.console.liveservices.liveupdate.main.repository;

import java.io.InputStream;
import java.net.URI;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface RepositoryTransport {

    boolean canHandle(URI uri);

    byte[] loadContent(URI uri) throws RepositoryTransportException;

    InputStream getContentStream(URI uri) throws RepositoryTransportException;
}
