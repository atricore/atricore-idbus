package org.atricore.idbus.bundles.ehcache;

import net.sf.ehcache.CacheManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.URL;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SpringCacheManagerFactoryImpl implements CacheManagerFactory, ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(SpringCacheManagerFactoryImpl.class);

    private CacheManager cacheManager;

    private URL configuration;

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public CacheManager getCacheManager() {

        if (cacheManager == null) {

            logger.info("Creating EH Cache Manager instance using configuration " + configuration);
            ClassLoader orig = Thread.currentThread().getContextClassLoader();

            try {
                Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());
                cacheManager = CacheManager.create(configuration);
            } finally {
                Thread.currentThread().setContextClassLoader(orig);
            }



        }

        return cacheManager;
    }

    public CacheManager getCacheManager(URL config) {
        if (cacheManager == null) {
            logger.info("Creating EH Cache Manager instance using configuration from URL " + config);
            cacheManager = CacheManager.create(config);
        }

        return cacheManager;
    }

    public void setConfiguration(URL configuration) {
        this.configuration = configuration;
    }

    public URL getConfiguration() {
        return configuration;
    }
}
