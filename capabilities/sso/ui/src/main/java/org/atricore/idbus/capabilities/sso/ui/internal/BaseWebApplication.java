package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.IRequestCycleProvider;
import org.apache.wicket.markup.html.IPackageResourceGuard;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.markup.html.pages.AccessDeniedPage;
import org.apache.wicket.markup.html.pages.PageExpiredErrorPage;
import org.apache.wicket.markup.parser.filter.RelativePathPrefixHandler;
import org.apache.wicket.markup.resolver.IComponentResolver;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.settings.IRequestCycleSettings;
import org.atricore.idbus.capabilities.sso.ui.*;
import org.atricore.idbus.capabilities.sso.ui.agent.JossoAuthorizationStrategy;
import org.atricore.idbus.capabilities.sso.ui.resources.AppResourceLocator;
import org.atricore.idbus.capabilities.sso.ui.spi.ApplicationRegistry;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingEvent;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingEventListener;
import org.atricore.idbus.capabilities.sso.ui.spi.WebBrandingService;
import org.atricore.idbus.kernel.main.mail.MailService;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnit;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.channel.IdPChannel;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProvider;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProvider;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.util.StringUtils;

import java.io.File;
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

    protected boolean ready;

    // Dependency injection does not work for application objects (pax-wicket)!
    
    protected BundleContext bundleContext;

    protected ApplicationRegistry appConfigRegistry;

    protected WebBrandingService brandingService;

    protected MailService mailService;

    protected WebBranding branding;

    protected IdentityProvider identityProvider;

    protected ServiceProvider selfServicesSP;

    protected List<AppResource> appResources = new ArrayList<AppResource>();

    protected IdentityMediationUnitRegistry idsuRegistry;

    protected Set<PageMountPoint> mounts;

    protected Map<String, PageMountPoint> mountsByPath;

    protected ConfigurationContext kernelConfig;


    static {

        fontExtensions.add("ttf"); // TrueType font
        fontExtensions.add("eot"); // Embedded OpenType font
        fontExtensions.add("woff"); // Web Open Font Format
        fontExtensions.add("woff2"); // Web Open Font Format 2.0

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

    public IdentityProvider getIdentityProvider() {
        if (identityProvider == null)
            resolveProviders();
        return identityProvider;
    }

    public void setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    public ServiceProvider getSelfServicesSP() {
        if (selfServicesSP == null)
            resolveProviders();

        return selfServicesSP;
    }

    public void setSelfServicesSP(ServiceProvider selfServicesSP) {
        this.selfServicesSP = selfServicesSP;
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

    public MailService getMailService() {
        return mailService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
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
    public void internalDestroy() {
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

    protected abstract void buildPageMounts();

    protected PageMountPoint addPageMount(PageMountPoint m) {
        if (mounts == null) {
            mounts = new HashSet<PageMountPoint>();
            mountsByPath = new HashMap<String, PageMountPoint>();
        }

        mounts.add(m);
        mountsByPath.put(m.getPath(), m);
        return m;
    }

    protected PageMountPoint addPageMount(String path, Class pageClass) {
        return addPageMount(new PageMountPoint(path, pageClass));
    }

    protected void setupSettingPages() {
        getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
        getApplicationSettings().setPageExpiredErrorPage(PageExpiredErrorPage.class);
        //getApplicationSettings().setInternalErrorPage(ApplicationErrorPage.class);
    }

    protected void mountPages() {
        buildPageMounts();
        for (PageMountPoint mount : mounts) {
            mountPage(mount.getPath(), mount.getPageClass());
        }
        setupSettingPages();
    }

    public Class resolvePage(String path) {
        PageMountPoint m = resolveMountPoint(path);
        if (m == null) {
            logger.warn("Page not found for " + path);
            return null;
        }
        return m.getPageClass();
    }

    public PageMountPoint resolveMountPoint(String path)  {
        PageMountPoint m = mountsByPath.get(path);
        if (m == null) {
            logger.warn("page Mount point not found for " + path);
            return null;
        }

        return m;
    }


    protected void preInit() {
        setRequestCycleProvider(new IRequestCycleProvider()
        {
            public RequestCycle get(RequestCycleContext context)
            {
                return new IdBusRequestCycle(context, BaseWebApplication.this);
            }
        });

        //getRequestCycleSettings().addResponseFilter(new AjaxServerAndClientTimeFilter());
        getDebugSettings().setAjaxDebugModeEnabled(false);

        //Security settings
        getSecuritySettings().setAuthorizationStrategy(new JossoAuthorizationStrategy());

        // Resource settings
        getResourceSettings().setEncodeJSessionId(false);

        // Avoid redirections on UI pages
        getRequestCycleSettings().setRenderStrategy(IRequestCycleSettings.RenderStrategy.ONE_PASS_RENDER);

    }

    /**
     * Injected services are available here
     */
    protected void postConfig() {

        // Markup settings
        getMarkupSettings().setMarkupFactory(new IdBusMarkupParserFactory(getAppConfig()));

        List<IComponentResolver> currentList = getPageSettings().getComponentResolvers();
        List<IComponentResolver> newComponentsList = new ArrayList<IComponentResolver>(currentList.size());

        // Alter prefix handling
        for (IComponentResolver iComponentResolver : currentList) {
            if (iComponentResolver instanceof RelativePathPrefixHandler) {
                newComponentsList.add(new IdBusRelativePathPrefixHandler(getAppConfig().getMountPoint()));
            } else {
                newComponentsList.add(iComponentResolver);
            }
        }

        // Page settings
        getPageSettings().getComponentResolvers().clear();
        getPageSettings().getComponentResolvers().addAll(newComponentsList);

        if (branding.getAllowedResourcePatterns() != null && branding.getAllowedResourcePatterns().size() > 0) {
            IPackageResourceGuard guard = this.getResourceSettings().getPackageResourceGuard();
            if (guard instanceof SecurePackageResourceGuard) {

                SecurePackageResourceGuard secureGuard = (SecurePackageResourceGuard) guard;
                for (String pattern : branding.getAllowedResourcePatterns()) {
                    secureGuard.addPattern(pattern);
                }

            } else {
                logger.error("Cannot add resource pattern to IPackageResourceGuard of type " + guard.getClass());
            }
        }


        // Authn settings

        // Do we have an IDP ? Resolve SSO endpoint

        // Trigger automatic login

        // Create security context, if available

        // Session keep alive / validate (accessSession)


    }

    public WebBranding getBranding() {
        return branding;
    }

    public WebAppConfig getAppConfig() {

        if (!this.ready)
            throw new IllegalStateException("Application has not been configured yet !");

        if (appConfigRegistry == null)
            throw new IllegalStateException("Application Configuration registry not found !");

        WebAppConfig cfg = appConfigRegistry.lookupConfig(getApplicationKey());
        if (cfg == null)
            logger.error("No configuration found for Wicket application " + getApplicationKey());

        return cfg;
    }

    public ConfigurationContext getKernelConfig() {
        return kernelConfig;
    }

    public List<AppResource> getAppResources() {
        return appResources;
    }

    public final synchronized void config(BundleContext bundleContext, ApplicationRegistry appConfigRegistry, WebBrandingService brandingService, IdentityMediationUnitRegistry idsuRegistry, ConfigurationContext kernelConfig, MailService mailService) {

        // We're ready
        this.ready = true;

        this.bundleContext = bundleContext;
        this.appConfigRegistry = appConfigRegistry;
        this.brandingService = brandingService;
        this.idsuRegistry = idsuRegistry;
        this.kernelConfig = kernelConfig;
        this.mailService = mailService;

        // Register the application to the branding service
        String brandingId = getAppConfig().getBrandingId();
        branding = brandingService.lookup(brandingId);
        if (branding != null) {
            brandingService.register(this);

            if (branding.getDefaultLocale() != null) {
                logger.debug("Setting default locale to " + branding.getDefaultLocale());
                Locale.setDefault(StringUtils.parseLocaleString((branding.getDefaultLocale())));
            }
        } else {
            logger.error("No branding configured for " + getAppConfig().getAppName() + " using ID : " + brandingId);
        }

        postConfig();
        refreshBranding();
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

                    PackageResourceReference ref = new PackageResourceReference(AppResourceLocator.class, resource.getPath());
                    this.appResources.add(new AppResource(resource, ref));
                    mountResource("/" + resource.getPath(), ref);
                    resourcePaths.add(resource.getPath());
                    if (logger.isTraceEnabled())
                        logger.trace("Mounting EXPLICITLY shared resource ["+resource.getId()+"] at /" + resource.getPath());
                }
            }
            
            // Auto-discovery all resources bound to AppResourceLocator class package.
            // Make them available as global resources
            Bundle b = bundleContext.getBundle();
            String basePath = "/" + AppResourceLocator.class.getPackage().getName().replace('.', '/');
            
            Enumeration e = b.findEntries(basePath, "*", true);
            while (e.hasMoreElements()) {
                URL location = (URL) e.nextElement();
                String path = location.getPath();
                String mountPath = path.substring(basePath.length() + 1);

                if (resourcePaths.contains(mountPath)) {
                    if (logger.isDebugEnabled())
                        logger.debug("Resource declared EXPLICITLY : " + path);
                    continue;
                }

                BrandingResourceType type = getTypeFromPath(path);
                if (type == null || type.equals(BrandingResourceType.OTHER))
                    continue;

                String id = mountPath.replace('/', '-');
                id = id.replace('.', '-');

                if (logger.isTraceEnabled())
                    logger.trace("Mounting DISCOVERED shared resource [" + id + "] at /" + mountPath);

                BrandingResource resource = new BrandingResource(id, mountPath, null, type);

                PackageResourceReference ref = new PackageResourceReference(AppResourceLocator.class, resource.getPath());
                this.appResources.add(new AppResource(resource, ref));
                mountResource("/" + resource.getPath(), ref);
                resourcePaths.add(resource.getPath());
                if (logger.isTraceEnabled())
                    logger.trace("Mounting shared resource [" + resource.getId() + "] at /" + resource.getPath());

            }

            {
                WebAppConfig appConfig = getAppConfig();

                String exteranlResourcesPath = System.getProperty("karaf.home") + File.separator + "data" + File.separator + "branding";

                if (branding.getExternalResourcesPath() != null)
                    exteranlResourcesPath = branding.getExternalResourcesPath();

                String unitName = appConfig.getUnitName();
                String applianceName = unitName.substring(0, unitName.length() - "-mediation-unit".length());

                String brandingExternalResources = exteranlResourcesPath + File.separator + applianceName;

                if (logger.isDebugEnabled())
                    logger.debug("Loading external branding resources from : " + brandingExternalResources);

                FolderContentResource extResourcesFolder = new FolderContentResource(new File(brandingExternalResources));
                Collection<String> extResources = extResourcesFolder.scan();
                getSharedResources().add(applianceName, extResourcesFolder);

                for (String extResource : extResources) {
                    mountResource(extResource, new SharedResourceReference(applianceName));
                }
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

        if (extension.equalsIgnoreCase("html"))
            return BrandingResourceType.HTML;

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

        private PackageResourceReference ref;

        public AppResource(BrandingResource resource, PackageResourceReference ref) {
            this.resource = resource;
            this.ref = ref;
        }

        public BrandingResource getResource() {
            return resource;
        }

        public PackageResourceReference getRef() {
            return ref;
        }
    }

    // This method must be invoked when the appliance is up and running
    protected void resolveProviders() {

        if (!ready)
            throw new IllegalStateException("Application not configured yet !");

        // Resolve identity provider
        String unitName = getAppConfig().getUnitName();
        String idpName = getAppConfig().getIdpName();
        String ssSpName = getAppConfig().getSelfServicesSpName();

        if (idpName == null)
            logger.debug("IdP Name not provided for application " + getAppConfig().getAppName());

        if (ssSpName == null)
            logger.debug("IdP Name not provided for application " + getAppConfig().getAppName());

        if(unitName != null) {

            IdentityMediationUnit unit =  idsuRegistry.lookupUnit(unitName);

            for (Channel c : unit.getChannels()) {

                // Look for the configured IDP, if any
                if (idpName != null && c instanceof SPChannel) {
                    SPChannel spChannel = (SPChannel) c;
                    if (spChannel.getProvider().getName().equalsIgnoreCase(idpName))
                        identityProvider = (IdentityProvider) spChannel.getProvider();

                } else if (ssSpName != null && c instanceof IdPChannel) {
                    IdPChannel idpChannel = (IdPChannel) c;
                    if (idpChannel.getProvider().getName().equalsIgnoreCase(ssSpName))
                        selfServicesSP = (ServiceProvider) idpChannel.getProvider();

                }
            }

            if (idpName != null && identityProvider == null) {
                logger.error("No IDP found with name " + idpName + " in Mediation Unit " + unitName);
            }

            if (ssSpName != null && selfServicesSP == null) {
                logger.error("No SP found with name " + ssSpName + " in Mediation Unit " + unitName);
            }
        }

    }



}
