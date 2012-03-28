package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.*;
import org.apache.wicket.markup.parser.filter.RelativePathPrefixHandler;
import org.apache.wicket.markup.resolver.IComponentResolver;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.atricore.idbus.capabilities.sso.ui.*;
import org.atricore.idbus.capabilities.sso.ui.resources.AppResourceLocator;
import org.atricore.idbus.capabilities.sso.ui.spi.ApplicationRegistry;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingEvent;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingEventListener;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.io.Serializable;
import java.net.URL;
import java.util.*;

/**
 * TODO : Implement a resource locator that can search for resources (pages, images, properties) in other bundles
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class BaseWebApplication extends WebApplication implements WebBrandingEventListener {

    private static final Log logger = LogFactory.getLog(BaseWebApplication.class);
    
    private static final Set<String> imageExtensions = new HashSet<String>();
    private static final Set<String> fontExtensions = new HashSet<String>();

    private boolean ready;

    // Dependency injection does not work for application objects (pax-wicket)!
    
    protected BundleContext bundleContext;

    protected ApplicationRegistry appConfigRegistry;

    protected WebBrandingService brandingService;

    protected WebBranding branding;

    protected List<AppResource> appResources = new ArrayList<AppResource>();
    
    static {

        fontExtensions.add("ttf"); // TrueType font
        fontExtensions.add("eot"); // Embedded OpenType font

        imageExtensions.add("bmp"); // Bitmap Image File
        imageExtensions.add("dds"); // DirectDraw Surface
        imageExtensions.add("gif"); // Graphical Interchange Format File
        imageExtensions.add("jpg"); // JPEG Image
        imageExtensions.add("png"); // Portable Network Graphic
        imageExtensions.add("psd"); // Adobe Photoshop Document
        imageExtensions.add("pspimage"); // PaintShop Pro Image
        imageExtensions.add("tga"); // Targa Graphic
        imageExtensions.add("thm"); // Thumbnail Image File
        imageExtensions.add("tif"); // Tagged Image File
        imageExtensions.add("yuv"); // YUV Encoded Image File
        imageExtensions.add("ico"); // Icon file
    }

    public BaseWebApplication() {
        super();
    }

    @Override
    public RequestCycle newRequestCycle(Request request, Response response) {
        return new CssWebRequestCycle(this, (WebRequest) request, response);
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public ApplicationRegistry getAppConfigRegistry() {
        return appConfigRegistry;
    }

    public void setAppConfigRegistry(ApplicationRegistry appConfigRegistry) {
        this.appConfigRegistry = appConfigRegistry;
    }

    public WebBrandingService getBrandingService() {
        return brandingService;
    }

    public void setBrandingService(WebBrandingService brandingService) {
        this.brandingService = brandingService;
    }

    public boolean isReady() {
        return ready;
    }
    
    @Override
    protected void init() {
        super.init();
        preInit();
        mountPages();
    }

    @Override
    protected void internalDestroy() {
        super.internalDestroy();
        if (brandingService != null) {
            try {
                brandingService.unregister(this);
            } catch (Exception e) {
                if (logger.isTraceEnabled())
                    logger.trace(e.getMessage(), e);
            }
        }
    }

    protected void mountPages() {

    }

    protected void preInit() {

    }

    /**
     * Injected services are available here
     */
    protected void postInit() {

        List<IComponentResolver> currentList = getPageSettings().getComponentResolvers();
        List<IComponentResolver> newComponentsList = new ArrayList<IComponentResolver>(currentList.size());

        for (IComponentResolver iComponentResolver : currentList) {
            if (iComponentResolver instanceof RelativePathPrefixHandler) {
                newComponentsList.add(new IdBusRelativePathPrefixHandler(getAppConfig().getMountPoint()));
            } else {
                newComponentsList.add(iComponentResolver);
            }
        }

        getPageSettings().getComponentResolvers().clear();
        getPageSettings().getComponentResolvers().addAll(newComponentsList);

        getMarkupSettings().setMarkupParserFactory(new IdBusMarkupParserFactory(getAppConfig()));
    }

    public WebBranding getBranding() {
        return branding;
    }

    public WebAppConfig getAppConfig() {
        WebAppConfig cfg = appConfigRegistry.lookupConfig(getApplicationKey());
        if (cfg == null)
            logger.error("No configuration found for Wicket application " + getApplicationKey());

        return cfg;
    }

    public List<AppResource> getAppResources() {
        return appResources;
    }

    public final void config(BundleContext bundleContext, ApplicationRegistry appConfigRegistry, WebBrandingService brandingService) {
        this.bundleContext = bundleContext;
        this.appConfigRegistry = appConfigRegistry;
        this.brandingService = brandingService;

        String brandingId = getAppConfig().getBrandingId();
        branding = brandingService.lookup(brandingId);
        if (branding != null) {
            brandingService.register(this);
        }
        postInit();
        refreshBranding();
        this.ready = true;
    }
    
    public void refreshBranding() {
        
        Set<String> resourcePaths = new HashSet<String>();

        // TODO : Instead of taking resources list from branding, also support scanning specific packages of specific bundles !!!!
        if (branding != null) {
            
            // TODO : Reset locale

            // Mount branding shared resources explicitly declared 
            for (BrandingResource resource : branding.getResources()) {
                // All shared resource MUST be scoped to AppResourceLocator
                if (resource.isShared()) {

                    ResourceReference ref = new ResourceReference(AppResourceLocator.class, resource.getPath());
                    this.appResources.add(new AppResource(resource, ref));
                    mountSharedResource("/" + resource.getPath(), ref.getSharedResourceKey());
                    resourcePaths.add(resource.getPath());
                    if (logger.isTraceEnabled())
                        logger.trace("Mounting EXPLICITY shared resource ["+resource.getId()+"] at /" + resource.getPath());
                }
            }
            
            // TODO : COMPLETE THIS
            
            // Auto-discovery all resources in AppResourceLocator class' package
            Bundle b = bundleContext.getBundle();
            String basePath = "/" + AppResourceLocator.class.getPackage().getName().replace('.', '/');
            
            Enumeration e = b.findEntries(basePath, "*", true);
            while (e.hasMoreElements()) {
                URL location = (URL) e.nextElement();
                String path = location.getPath();
                String mountPath = path.substring(basePath.length() + 1);
                
                if (resourcePaths.contains(mountPath)) {
                    if (logger.isDebugEnabled())
                        logger.debug("Resource declared EXPLICITLY : "+ path);
                    continue;
                }

                BrandingResourceType type = getTypeFromPath(path);
                if (type == null || type.equals(BrandingResourceType.OTHER))
                    continue;
                
                String id = mountPath.replace('/', '-');
                id = id.replace('.', '-');
                
                if (logger.isTraceEnabled())
                    logger.trace("Mounting DISCOVERED shared resource ["+id+"] at /" + mountPath);
                
                BrandingResource resource = new BrandingResource(id, mountPath, null, type);
                
                ResourceReference ref = new ResourceReference(AppResourceLocator.class, resource.getPath());
                this.appResources.add(new AppResource(resource, ref));
                mountSharedResource("/" + resource.getPath(), ref.getSharedResourceKey());
                resourcePaths.add(resource.getPath());
                if (logger.isTraceEnabled())
                    logger.trace("Mounting shared resource ["+resource.getId()+"] at /" + resource.getPath());
                
            }
            

        }

    }
    
    protected BrandingResourceType getTypeFromPath(String path) {
        //String imgs
        int mid = path.lastIndexOf('.');
        if (mid < 0) return null;
        String extension = path.substring(mid + 1, path.length());

        if (extension.equalsIgnoreCase("css"))
            return BrandingResourceType.CSS;

        if (extension.equalsIgnoreCase("js"))
            return BrandingResourceType.SCRIPT;
        
        if (imageExtensions.contains(extension))
            return BrandingResourceType.IMAGE;

        if (fontExtensions.contains(extension))
            return BrandingResourceType.FONT;

        return BrandingResourceType.OTHER;
    }
    
    public void removeBranding() {
        // TODO : What happens when the branding is removed ?!
        logger.warn("Configured branding was removed ! ["+branding.getId()+"]");
    }


    public void handleEvent(WebBrandingEvent event) {

        // Not our branding
        if (!branding.getId().equals(event.getBrandingId()))
            return;

        switch (event.getType()) {

            case WebBrandingEvent.PUBLISH:
                logger.debug("Processing branding event type PUBLISH : " + event.getType());
                refreshBranding();
                break;

            case WebBrandingEvent.REMOVE:
                logger.debug("Processing branding event type REMOVE : " + event.getType());
                removeBranding();
                break;

            default:
                logger.debug("Unknown branding event type " + event.getType());
                break;
        }

    }

    public class AppResource implements Serializable {

        private BrandingResource resource;

        private ResourceReference ref;

        public AppResource(BrandingResource resource, ResourceReference ref) {
            this.resource = resource;
            this.ref = ref;
        }

        public BrandingResource getResource() {
            return resource;
        }

        public ResourceReference getRef() {
            return ref;
        }
    }



}
