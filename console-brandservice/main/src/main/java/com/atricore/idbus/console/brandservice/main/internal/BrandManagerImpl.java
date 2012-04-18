package com.atricore.idbus.console.brandservice.main.internal;

import com.atricore.idbus.console.brandservice.main.domain.BrandingDefinition;
import com.atricore.idbus.console.brandservice.main.BrandingServiceException;
import com.atricore.idbus.console.brandservice.main.domain.BuiltInBrandingDefinition;
import com.atricore.idbus.console.brandservice.main.NoSuchBrandingException;
import com.atricore.idbus.console.brandservice.main.domain.CustomBrandingDefinition;
import com.atricore.idbus.console.brandservice.main.spi.BrandManager;
import com.atricore.idbus.console.brandservice.main.spi.BrandingInstaller;
import com.atricore.idbus.console.brandservice.main.spi.BrandingStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.transaction.annotation.Transactional;
import org.atricore.idbus.kernel.common.support.services.IdentityServiceLifecycle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class BrandManagerImpl implements BrandManager, BundleContextAware,
        InitializingBean, IdentityServiceLifecycle {

    private static final Log logger = LogFactory.getLog(BrandManagerImpl.class);
    
    private List<BrandingInstaller> installers = new ArrayList<BrandingInstaller>();
    
    private List<BuiltInBrandingDefinition> builtInBrandings;

    private BrandingStore store;
    
    // TODO : Move to installers ?!
    private BundleContext bundleContext;
    
    private boolean booted = false;

    public void afterPropertiesSet() throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Transactional
    public void boot() throws Exception {
        
        if (booted)
            return;

        logger.info("Initializing Brand Manager serivce ....");

        try {
            registerDefaultBrandings();
            syncBrandingDefinitions();
            booted = true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    protected boolean syncBrandingDefinitions() {
        try {
            Collection<BrandingDefinition> brandings = store.list();
            
            if (brandings == null || brandings.size() < 1)
                return true;

            for (BrandingDefinition bd : brandings) {

                if (bd instanceof BuiltInBrandingDefinition)
                    continue;

                logger.debug("Installing branding definition : " + bd.getName());
                install(bd.getId());
            }
            
            publish();
            
            return true;
        } catch (BrandingServiceException e) {
            logger.error("Cannot syncrhonize branding definitions");
            return false;
        }
    }

    @Transactional
    protected void registerDefaultBrandings() {
        for (BuiltInBrandingDefinition b : builtInBrandings) {
            
            try {
                store.retrieveByName(b.getName());
                continue;
            } catch (NoSuchBrandingException e) {
                // We need to create this branding
            }

            try {
                store.create(b);
            } catch (BrandingServiceException e) {
                throw new RuntimeException("Cannot create built-in branding " + b.getName() + " : " + e.getMessage(), e);
            }                                                                 

        }
         
    }

    @Transactional
    public BrandingDefinition create(BrandingDefinition def) throws BrandingServiceException {
        try {

            // If branding def. is custom, try to install the bundle
            if (def instanceof CustomBrandingDefinition) {
                CustomBrandingDefinition customDef = (CustomBrandingDefinition) def;
                String bu = customDef.getBundleUri();

                if (customDef.getResource() != null && customDef.getResource().length > 0) {
                    // Install the bundle locally, based on the provided bundle uri
                    if (!bu.startsWith("mvn:")) {
                        throw new BrandingServiceException("Unknown URI protocol type for " + bu);
                    }

                    String group = bu.substring(4, bu.indexOf("/"));

                    // Some groups that may produce
                    if (group.startsWith("org.atricore.") ||
                            group.startsWith("org.josso.") ||
                            group.startsWith("org.apache.") ||
                            group.startsWith("org.codehaus.") ||
                            group.startsWith("org.osgi.") ||
                            group.startsWith("org.springframework.") ||
                            group.startsWith("org.mortbay.") ||
                            group.startsWith("java.") ||
                            group.startsWith("javax.") ||
                            group.startsWith("com.sun.") ||
                            group.startsWith("java.") ||
                            group.startsWith("commons-")) {
                        throw new BrandingServiceException("Illegal brandding bundle group name " + group);
                    }

                    group = group.replace('.', '/');
                    String name = bu.substring(bu.indexOf("/") + 1, bu.lastIndexOf("/"));
                    String version = bu.substring(bu.lastIndexOf("/") + 1);

                    String karafHome = System.getProperty("karaf.home");

                    // Create the bundle in the extensions folder :
                    String resourceFolder = karafHome + "/extensions/" + group + "/" + name;
                    String resourceFile = resourceFolder + "/" + name + "-" + version + ".jar";

                    if (logger.isDebugEnabled())
                        logger.debug("Writing bundle resource to " + resourceFile);

                    File f = new File(resourceFile);
                    File d = new File(resourceFolder);
                    FileOutputStream fos = null;
                    try {

                        if (!d.exists())
                            d.mkdirs();

                        if (!f.exists())
                            f.createNewFile();

                        // Replace file, if it exists.
                        fos = new FileOutputStream(f, false);
                        fos.write(customDef.getResource());
                    } catch (IOException e) {
                        throw new BrandingServiceException("Cannot create branding bundle : " + e.getMessage(), e);
                    }

                    // We have the bundle in place ...

                }
            }

            return store.create(def);
        } catch (BrandingServiceException e) {
            throw new BrandingServiceException ("Cannot create branding definition " + e.getMessage(), e);
        }
    }

    @Transactional
    public BrandingDefinition update(BrandingDefinition def) throws BrandingServiceException {
        BrandingDefinition oldDef = store.retrieve(def.getId());
        if (oldDef instanceof BuiltInBrandingDefinition)
            throw new BrandingServiceException("Cannot update buil-in branding definition");
        return store.update(def);
    }

    @Transactional
    public void delete(long id) throws BrandingServiceException {
        BrandingDefinition def = store.retrieve(id);
        if (def instanceof BuiltInBrandingDefinition)
            throw new BrandingServiceException("Cannot delete buil-in branding definition");

        store.delete(id);
    }

    @Transactional
    public void install(long id) throws BrandingServiceException {

        BrandingDefinition def = store.retrieve(id);
        if (def instanceof BuiltInBrandingDefinition)
            throw new BrandingServiceException("Cannot install buil-in branding definition");

        for (BrandingInstaller installer : installers) {

            if (installer.canHandle(def)) {
                def = installer.install(def);
                store.update(def);
            }
        }

    }

    public void publish() throws BrandingServiceException {
        
        ServiceReference ref = getBundleContext().getServiceReference(PackageAdmin.class.getName());
        if (ref == null) {
            throw new BrandingServiceException("PackageAdmin service is unavailable.");
        }
        try {
            PackageAdmin pa = (PackageAdmin) getBundleContext().getService(ref);
            if (pa == null) {
                throw new BrandingServiceException("PackageAdmin service is unavailable.");
            }
            
            List<Bundle> bundles = BrandingUtil.getBundleByName(bundleContext, SSO_UI_BUNDLE);
            if (bundles == null || bundles.size() < 1) {
                throw new BrandingServiceException("Bundle not found for " + SSO_UI_BUNDLE);
            }

            pa.refreshPackages(bundles.toArray(new Bundle[bundles.size()]));

        } finally {
            getBundleContext().ungetService(ref);
        }

    }

    @Transactional
    public BrandingDefinition lookupByName(String name) throws BrandingServiceException {
        return store.retrieveByName(name);
    }

    @Transactional
    public BrandingDefinition lookup(long id) throws BrandingServiceException {
        return store.retrieve(id);
    }

    @Transactional
    public Collection<BrandingDefinition> list() throws BrandingServiceException {
        return store.list();
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public BrandingStore getStore() {
        return store;
    }

    public void setStore(BrandingStore store) {
        this.store = store;
    }

    public List<BrandingInstaller> getInstallers() {
        return installers;
    }

    public void setInstallers(List<BrandingInstaller> installers) {
        this.installers = installers;
    }

    public List<BuiltInBrandingDefinition> getBuiltInBrandings() {
        return builtInBrandings;
    }

    public void setBuiltInBrandings(List<BuiltInBrandingDefinition> builtInBrandings) {
        this.builtInBrandings = builtInBrandings;
    }

}
