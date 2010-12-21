package com.atricore.idbus.console.liveservices.liveupdate.main.repository;

import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;

import java.net.URI;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface RepositoryTransport {

    boolean canHandle(URI uri);

    byte[] loadContent(URI uri) throws RepositoryTransportException;
}
