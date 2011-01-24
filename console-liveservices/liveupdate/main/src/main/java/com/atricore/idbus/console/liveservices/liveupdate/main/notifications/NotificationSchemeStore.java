package com.atricore.idbus.console.liveservices.liveupdate.main.notifications;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface NotificationSchemeStore {

    Collection<NotificationScheme> loadAll();

    NotificationScheme load(String name);

    void store(NotificationScheme scheme);


}
