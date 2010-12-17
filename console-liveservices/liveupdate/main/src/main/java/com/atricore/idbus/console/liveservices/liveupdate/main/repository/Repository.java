package com.atricore.idbus.console.liveservices.liveupdate.main.repository;

import java.net.URI;

/**

 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface Repository<T> {

    String getId();

    String getName();

    String getPublicKey();

    boolean isEnabled();

    String getUsername();

    String getPassword();

    URI getLocation();

}
