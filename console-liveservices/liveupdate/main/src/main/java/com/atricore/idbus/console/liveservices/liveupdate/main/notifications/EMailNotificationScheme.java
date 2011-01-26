package com.atricore.idbus.console.liveservices.liveupdate.main.notifications;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class EMailNotificationScheme extends AbstractNotificationScheme {

    private String[] addresses;

    public String[] getAddresses() {
        return addresses;
    }

    public void setAddresses(String[] addresses) {
        this.addresses = addresses;
    }
}
