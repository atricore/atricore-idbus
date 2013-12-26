package org.atricore.idbus.kernel.main.util;

/**
 * Keeps track of used IDs
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface IdRegistry {

    boolean isUsed(String id);

    void register(String id);

    /**
     *
     * @param id new ID to be added to the registry
     * @param timeToLive time to live in seconds, the time the ID will be stored in the registry.
     */
    void register(String id, int timeToLive);
}
