package com.atricore.idbus.console.liveservices.liveupdate.main.notifications;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;

import java.util.Collection;
import java.util.Properties;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface NotificationSchemeStore {

    void init() throws LiveUpdateException;
    
    Collection<NotificationScheme> loadAll() throws LiveUpdateException;

    NotificationScheme load(String name) throws LiveUpdateException;

    void store(NotificationScheme scheme) throws LiveUpdateException;

    void remove(String name) throws LiveUpdateException;

    String[] getProcessedUpdates(String name) throws LiveUpdateException;

    void addProcessedUpdates(String name, String[] updates) throws LiveUpdateException;
    
    Properties marshall(NotificationScheme scheme);

    NotificationScheme unmarshall(Properties props);
}
