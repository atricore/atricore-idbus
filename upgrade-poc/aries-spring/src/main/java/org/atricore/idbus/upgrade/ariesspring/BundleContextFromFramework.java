package org.atricore.idbus.upgrade.ariesspring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class BundleContextFromFramework implements BundleContextServiceSample, InitializingBean, ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(BundleContextFromSpring.class);

    private String name;

    // BundleContextAware
    private BundleContext bundleContext;

    private ApplicationContext applicationContext;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public BundleContext getServiceContext() {
        return bundleContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        this.bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();

        if (applicationContext == null)
            logger.error("APP CONTEXT NOT SET! " + getName());
        else
            logger.info("ApplicattionContext: " + getName() + "=" + applicationContext.getApplicationName());

        if (bundleContext == null)
            logger.error("BUNDLE CONTEXT NOT SET!" + getName());
        else
            logger.info("BundleContext: " + getName() + "=" + bundleContext.getBundle().getSymbolicName());

    }
}
