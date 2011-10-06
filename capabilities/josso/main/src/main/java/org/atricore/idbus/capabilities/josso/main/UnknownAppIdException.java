package org.atricore.idbus.capabilities.josso.main;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UnknownAppIdException extends JossoException {

    private String appId;

    public UnknownAppIdException(String appId) {
        super("Unknown Application ID: " + appId);
        this.appId = appId;
    }
}
