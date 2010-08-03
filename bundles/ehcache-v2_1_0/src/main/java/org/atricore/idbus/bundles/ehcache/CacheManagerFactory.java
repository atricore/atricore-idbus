package org.atricore.idbus.bundles.ehcache;

import net.sf.ehcache.CacheManager;

import java.net.URL;

/**
 * Convenience factory that allows CacheManager to be exported as OSGi service using spring-dm
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface CacheManagerFactory {

    CacheManager getCacheManager();

    CacheManager getCacheManager(URL config);
}
