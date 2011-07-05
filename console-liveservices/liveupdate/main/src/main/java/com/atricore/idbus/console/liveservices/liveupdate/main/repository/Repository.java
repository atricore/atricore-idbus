package com.atricore.idbus.console.liveservices.liveupdate.main.repository;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;

import java.net.URI;
import java.security.cert.Certificate;

/**

 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface Repository<T> {

    String getId();

    String getName();

    Certificate getCertificate();

    boolean isSignatureValidationEnabled();
    
    boolean isEnabled();

    String getUsername();

    String getPassword();

    URI getLocation();

    void init() throws LiveUpdateException;

    void clear() throws LiveUpdateException;


}
