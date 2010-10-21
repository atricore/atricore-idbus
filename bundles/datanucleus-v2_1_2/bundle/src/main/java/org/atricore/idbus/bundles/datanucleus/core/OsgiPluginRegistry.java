package org.atricore.idbus.bundles.datanucleus.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.OMFContext;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.plugin.*;
import org.datanucleus.util.Localiser;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.StringUtils;

import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class OsgiPluginRegistry implements PluginRegistry {
    
    private static final Log logger = LogFactory.getLog(OsgiPluginRegistry.class);
    
    protected static final Localiser LOCALISER = Localiser.getInstance("org.datanucleus.Localisation",
        OMFContext.class.getClassLoader());

    /** DataNucleus package to define whether to check for deps, etc. */
    private static final String DATANUCLEUS_PKG = "org.datanucleus";

    /** directories that are searched for plugin files */
    private static final String PLUGIN_DIR = "/";

    /** filters all accepted manifest file names */
    private static final FilenameFilter MANIFEST_FILE_FILTER = new FilenameFilter()
    {
        public boolean accept(File dir, String name)
        {
            // accept a directory named "meta-inf"
            if (name.equalsIgnoreCase("meta-inf"))
            {
                return true;
            }
            // or accept /meta-inf/manifest.mf
            if (!dir.getName().equalsIgnoreCase("meta-inf"))
            {
                return false;
            }
            return name.equalsIgnoreCase("manifest.mf");
        }
    };

    /**
     * Character that is used in URLs of jars to separate the file name from the path of a resource inside
     * the jar.<br/> example: jar:file:foo.jar!/META-INF/manifest.mf
     */
    private static final char JAR_SEPARATOR = '!';

    /** ClassLoaderResolver corresponding to the persistence context. */
    private final ClassLoaderResolver clr;

    /** extension points keyed by Unique Id (plugin.id +"."+ id) * */
    Map<String, ExtensionPoint> extensionPointsByUniqueId = new HashMap();

    /** registered bundles files keyed by bundle symbolic name * */
    Map<String, Bundle> registeredPluginByPluginId = new HashMap();

    /** extension points * */
    ExtensionPoint[] extensionPoints;

    private boolean registeredExtensions;

    /** Type of check on bundles (EXCEPTION, LOG, NONE). */
    private String bundleCheckType = "EXCEPTION";

    /**
     * Constructor.
     * @param clr the ClassLoaderResolver
     */
    public OsgiPluginRegistry(ClassLoaderResolver clr)
    {

        logger.debug("Initializing Datanucleus OSGi Plugin Registry");

        this.clr = clr;
        extensionPoints = new ExtensionPoint[0];
    }   

    /**
     * Accessor for the ExtensionPoint with the specified id.
     * @param id the unique id of the extension point
     * @return null if the ExtensionPoint is not registered
     */
    public ExtensionPoint getExtensionPoint(String id)
    {
        return extensionPointsByUniqueId.get(id);
    }

    /**
     * Accessor for the currently registered ExtensionPoints.
     * @return array of ExtensionPoints
     */
    public ExtensionPoint[] getExtensionPoints()
    {
        return extensionPoints;
    }

    /**
     * Look for Bundles/Plugins and register them. Register also ExtensionPoints and Extensions declared in /plugin.xml
     * files
     */
    public void registerExtensionPoints()
    {
        registerExtensions();
    }

    /**
     * Register extension and extension points for the specified plugin.
     * @param pluginURL the URL to the plugin
     * @param bundle the bundle
     */
    public void registerExtensionsForPlugin(URL pluginURL, Bundle bundle)
    {
        DocumentBuilder docBuilder = PluginParser.getDocumentBuilder();
        List[] elements = PluginParser.parsePluginElements(docBuilder, this, pluginURL, bundle, clr);
        registerExtensionPointsForPluginInternal(elements[0], true);

        // Register extensions
        Iterator<Extension> pluginExtensionIter = elements[1].iterator();
        while (pluginExtensionIter.hasNext())
        {
            Extension extension = pluginExtensionIter.next();
            ExtensionPoint exPoint = extensionPointsByUniqueId.get(extension.getExtensionPointId());
            if (exPoint == null)
            {
                NucleusLogger.PLUGIN.warn(LOCALISER.msg("024002", extension.getExtensionPointId(),
                    extension.getPlugin().getSymbolicName(), extension.getPlugin().getManifestLocation().toString()));
            }
            else
            {
                extension.setExtensionPoint(exPoint);
                exPoint.addExtension(extension);
            }
        }
    }

    /**
     * Look for Bundles/Plugins and register them.
     * Register also ExtensionPoints and Extensions declared in "/plugin.xml" files.
     */
    public void registerExtensions()
    {
        if (logger.isDebugEnabled())
            logger.debug("registerextensions:" + registeredExtensions);

        if (registeredExtensions)
        {
            return;
        }

        List registeringExtensions = new ArrayList();

        // parse the plugin files
        DocumentBuilder docBuilder = PluginParser.getDocumentBuilder();
        Set<URL> urls = getPluginURLs();

        if (logger.isDebugEnabled())
            logger.debug("Using plugin urls " + urls.size());

        Iterator<URL> it = urls.iterator();
        while (it.hasNext())
        {
            URL pluginURL = it.next();
            URL manifest = getManifestURL(pluginURL);

            if (logger.isDebugEnabled())
                logger.debug("Plugin URLs " + pluginURL + "/" + manifest);

            if (manifest == null)
            {
                if (logger.isDebugEnabled())
                    logger.debug("No MANIFEST.MF for this plugin.xml so ignore it (1)");

                // No MANIFEST.MF for this plugin.xml so ignore it
                continue;
            }

            Bundle bundle = registerBundle(manifest);
            if (bundle == null)
            {
                if (logger.isDebugEnabled())
                    logger.debug("No MANIFEST.MF for this plugin.xml so ignore it (2)");

                // No MANIFEST.MF for this plugin.xml so ignore it
                continue;
            }

            if (logger.isDebugEnabled())
                logger.debug("Accepted bundle with plugins: " + bundle.getSymbolicName());

            List[] elements =
                PluginParser.parsePluginElements(docBuilder, this, pluginURL, bundle, clr);

            if (logger.isDebugEnabled())
                logger.debug("Registering extension points for " + pluginURL + " with configuration elements " + elements.length);

            registerExtensionPointsForPluginInternal(elements[0], false);
            registeringExtensions.addAll(elements[1]);
        }
        extensionPoints = extensionPointsByUniqueId.values().toArray(
            new ExtensionPoint[extensionPointsByUniqueId.values().size()]);

        if (logger.isDebugEnabled())
            logger.debug("Registered extension points ok, now registering extensions");

        // Register the extensions now that we have the extension-points all loaded
        for (int i = 0; i < registeringExtensions.size(); i++)
        {
            Extension extension = (Extension)registeringExtensions.get(i);
            ExtensionPoint exPoint = getExtensionPoint(extension.getExtensionPointId());

            if (logger.isDebugEnabled())
                logger.debug("ExtensionPoint:" + (exPoint != null ? exPoint.getId() : null)+ " has extension with " +
                    extension.getConfigurationElements().length + " configuration elements");

            if (exPoint == null)
            {
                if (extension.getPlugin().getSymbolicName().startsWith(DATANUCLEUS_PKG))
                {
                    NucleusLogger.PLUGIN.warn(LOCALISER.msg("024002", extension.getExtensionPointId(),
                        extension.getPlugin().getSymbolicName(), extension.getPlugin().getManifestLocation().toString()));
                }
            }
            else
            {
                extension.setExtensionPoint(exPoint);
                exPoint.addExtension(extension);
            }
        }
        registeredExtensions = true;
    }

    /**
     * Register extension-points for the specified plugin.
     * @param extPoints ExtensionPoints for this plugin
     * @param updateExtensionPointsArray Whether to update "extensionPoints" array
     */
    protected void registerExtensionPointsForPluginInternal(List extPoints, boolean updateExtensionPointsArray)
    {
        // Register extension-points
        Iterator<ExtensionPoint> pluginExtPointIter = extPoints.iterator();
        while (pluginExtPointIter.hasNext())
        {
            ExtensionPoint exPoint = pluginExtPointIter.next();
            extensionPointsByUniqueId.put(exPoint.getUniqueId(), exPoint);
        }
        if (updateExtensionPointsArray)
        {
            extensionPoints = extensionPointsByUniqueId.values().toArray(
                new ExtensionPoint[extensionPointsByUniqueId.values().size()]);
        }
    }

    /**
     * Search and retrieve the URL for the "/plugin.xml" files located in the classpath.
     * @return a set of {@link java.net.URL}
     */
    private Set<URL> getPluginURLs()
    {
        Set<URL> set = new HashSet();
        try
        {

            // TODO : Make configurable ;)
            String[] pluginPaths = { PLUGIN_DIR + "plugin.xml", "META-INF" + PLUGIN_DIR + "plugin.xml"} ;

            for (int i = 0; i < pluginPaths.length; i++) {

                String pluginPath = pluginPaths[i];

                if(logger.isDebugEnabled())
                    logger.debug("Looking for plugins " + pluginPath + "plugin.xml, using clr " + clr);

                Enumeration<URL> paths =
                    clr.getResources(pluginPath, JDOPersistenceManagerFactory.class.getClassLoader());

                while (paths.hasMoreElements()) {
                    URL u = paths.nextElement();
                    if (logger.isDebugEnabled())
                        logger.debug("Found plugin resource " + u.toExternalForm());
                    set.add(u);
                }

            }

        }
        catch (IOException e)
        {
            logger.debug("Error loading resource : "+ PLUGIN_DIR + "plugin.xml");
            throw new NucleusException("Error loading resource", e).setFatal();
        }
        return set;
    }

    /**
     * Register the plugin bundle.
     * @param manifest the url to the "meta-inf/manifest.mf" file or a jar file
     * @return the Plugin
     */
    protected Bundle registerBundle(URL manifest)
    {
        if (manifest == null)
        {
            throw new IllegalArgumentException(LOCALISER.msg("024007"));
        }

        InputStream is = null;

        try
        {
            Manifest mf = null;
            if (manifest.getProtocol().equals("jar") || manifest.getProtocol().equals("zip") ||
                manifest.getProtocol().equals("wsjar"))
            {
                if (manifest.getPath().startsWith("http://"))
                {
                  // protocol formats:
                  //     jar:http:<path>!<manifest-file>, zip:http:<path>!<manifest-file>
                  // e.g jar:http://<host>[:port]/[app-path]/jpox-java5.jar!/plugin.xml
                  JarURLConnection jarConnection = (JarURLConnection) manifest.openConnection();
                  URL url = jarConnection.getJarFileURL();
                  mf = jarConnection.getManifest();
                  if (mf == null)
                  {
                      return null;
                  }
                  return registerBundle(mf, url);
                }
                else
                {
                    int begin = 4;
                    if (manifest.getProtocol().equals("wsjar"))
                    {
                        begin = 6;
                    }
                    // protocol formats:
                    //     jar:<path>!<manifest-file>, zip:<path>!<manifest-file>
                    //     jar:file:<path>!<manifest-file>, zip:file:<path>!<manifest-file>
                    String path = StringUtils.getDecodedStringFromURLString(manifest.toExternalForm());
                    int index = path.indexOf(JAR_SEPARATOR);
                    String jarPath = path.substring(begin, index);
                    if (jarPath.startsWith("file:"))
                    {
                        // remove "file:" from path, so we can use in File constructor
                        jarPath = jarPath.substring(5);
                    }
                    File jarFile = new File(jarPath);
                    mf = new JarFile(jarFile).getManifest();
                    if (mf == null)
                    {
                        return null;
                    }
                    return registerBundle(mf, jarFile.toURI().toURL());
                }
            }
            else if (manifest.getProtocol().equals("rar") || manifest.getProtocol().equals("war"))
            {
                // protocol formats:
                //     rar:<rar-path>!<jar-path>!<manifest-file>, war:<war-path>!<jar-path>!<manifest-file>
                String path = StringUtils.getDecodedStringFromURLString(manifest.toExternalForm());
                int index = path.indexOf(JAR_SEPARATOR);
                String rarPath = path.substring(4, index);
                File file = new File(rarPath);
                URL rarUrl = file.toURI().toURL();

                String jarPath = path.substring(index+1, path.indexOf(JAR_SEPARATOR,index+1));
                JarFile rarFile = new JarFile(file);
                mf = new JarInputStream(rarFile.getInputStream(rarFile.getEntry(jarPath))).getManifest();
                if (mf == null)
                {
                    return null;
                }
                return registerBundle(mf, rarUrl);
            }
            else if (manifest.getProtocol().equals("vfsfile") || manifest.getProtocol().equals("vfsjar") ||
                manifest.getProtocol().equals("vfszip"))
            {
                // protocol formats:
                // vfsfile:<path>!<manifest-file>, vfsjar:<path>!<manifest-file>, vfszip:<path>!<manifest-file>
                String path = StringUtils.getDecodedStringFromURLString(manifest.toExternalForm());
                int index = path.indexOf(JAR_SEPARATOR);
                String jarPath = path.substring(0, index);
                URL jarUrl = new URL(jarPath);

                JarInputStream jis = new JarInputStream(jarUrl.openConnection().getInputStream());
                mf = jis.getManifest();
                if (mf == null)
                {
                    return null;
                }
                return registerBundle(mf, jarUrl);
            }
            else
            {
                is = manifest.openStream();
                mf = new Manifest(is);
                return registerBundle(mf,manifest);
            }
        }
        catch (IOException e)
        {
            throw new NucleusException(LOCALISER.msg("024008", manifest), e).setFatal();
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    // ignored
                }
            }
        }
    }

    /**
     * Register the plugin bundle.
     * @param mf the Manifest
     * @param manifest the url to the "meta-inf/manifest.mf" file or a jar file
     * @return the Plugin
     */
    protected Bundle registerBundle(Manifest mf, URL manifest)
    {
        Bundle bundle = PluginParser.parseManifest(mf, manifest);
        if (registeredPluginByPluginId.get(bundle.getSymbolicName()) == null)
        {
            if (NucleusLogger.PLUGIN.isDebugEnabled())
            {
                NucleusLogger.PLUGIN.debug("Registering bundle " + bundle.getSymbolicName() + " version " +
                    bundle.getVersion() + " at URL " + bundle.getManifestLocation() + ".");
            }
            registeredPluginByPluginId.put(bundle.getSymbolicName(), bundle);
        }
        else
        {
            Bundle previousBundle = registeredPluginByPluginId.get(bundle.getSymbolicName());
            if (bundle.getSymbolicName().startsWith(DATANUCLEUS_PKG) &&
                !bundle.getManifestLocation().toExternalForm().equals(previousBundle.getManifestLocation().toExternalForm()))
            {
                String msg = LOCALISER.msg("024009", bundle.getSymbolicName(),
                    bundle.getManifestLocation(), previousBundle.getManifestLocation());
                if (bundleCheckType.equalsIgnoreCase("EXCEPTION"))
                {
                    throw new NucleusException(msg);
                }
                else if (bundleCheckType.equalsIgnoreCase("LOG"))
                {
                    NucleusLogger.PLUGIN.warn(msg);
                }
                else
                {
                    // Nothing
                }
            }
        }
        return bundle;
    }

    /**
     * Get the URL to the "manifest.mf" file relative to the plugin URL ($pluginurl/meta-inf/manifest.mf)
     * @param pluginURL the url to the "plugin.xml" file
     * @return a URL to the "manifest.mf" file or a URL for a jar file
     */
    private URL getManifestURL(URL pluginURL)
    {
        if (pluginURL == null)
        {
            return null;
        }
        if (pluginURL.toString().startsWith("jar") || pluginURL.toString().startsWith("zip") ||
            pluginURL.toString().startsWith("rar") || pluginURL.toString().startsWith("war") ||
            pluginURL.toString().startsWith("wsjar"))
        {
            // URL for file containing the manifest
            return pluginURL;
        }
        else if (pluginURL.toString().startsWith("vfsfile") || pluginURL.toString().startsWith("vfsjar") ||
            pluginURL.toString().startsWith("vfszip"))
        {
            // JBoss (5+) proprietary protocols input:
            // vfsfile:C:/appserver/jboss-5.0.0.Beta4/server/default/deploy/datanucleus-jca-1.0.0.rar/datanucleus-core-1.0-SNAPSHOT.jar/plugin.xml
            // output:
            // vfsfile:C:/appserver/jboss-5.0.0.Beta4/server/default/deploy/datanucleus-jca-1.0.0.rar/datanucleus-core-1.0-SNAPSHOT.jar!/plugin.xml
            String urlStr = pluginURL.toString().replaceAll("\\.jar/", ".jar!/");
            try
            {
                return new URL(urlStr);
            }
            catch (MalformedURLException e)
            {
                NucleusLogger.PLUGIN.warn(LOCALISER.msg("024010", urlStr), e);
                return null;
            }
        }
        else if (pluginURL.toString().startsWith("jndi"))
        {
            // "Oracle AS" uses JNDI protocol. For example
            // input:  jndi:/opt/oracle/product/10.1.3.0.3_portal/j2ee/OC4J_Portal/applications/presto/presto/WEB-INF/lib/jpox-rdbms-1.2-SNAPSHOT.jar/plugin.xml
            // output: jar:file:/opt/oracle/product/10.1.3.0.3_portal/j2ee/OC4J_Portal/applications/presto/presto/WEB-INF/lib/jpox-rdbms-1.2-SNAPSHOT.jar!/plugin.xml
            String urlStr = pluginURL.toString().substring(5);
            urlStr = urlStr.replaceAll("\\.jar/", ".jar!/");
            urlStr = "jar:file:" + urlStr;
            try
            {
                // URL for file containing the manifest
                return new URL(urlStr);
            }
            catch (MalformedURLException e)
            {
                NucleusLogger.PLUGIN.warn(LOCALISER.msg("024010", urlStr), e);
                return null;
            }
        }
        else if (pluginURL.toString().startsWith("code-source"))
        {
            // "Oracle AS" also uses code-source protocol. For example
            // input:  code-source:/opt/oc4j/j2ee/home/applications/presto/presto/WEB-INF/lib/jpox-rdmbs-1.2-SNAPSHOT.jar!/plugin.xml
            // output: jar:file:/opt/oc4j/j2ee/home/applications/presto/presto/WEB-INF/lib/jpox-rdmbs-1.2-SNAPSHOT.jar!/plugin.xml
            String urlStr = pluginURL.toString().substring(12); //strip "code-source:"
            urlStr = "jar:file:" + urlStr;
            try
            {
                // URL for file containing the manifest
                return new URL(urlStr);
            }
            catch (MalformedURLException e)
            {
                NucleusLogger.PLUGIN.warn(LOCALISER.msg("024010", urlStr), e);
                return null;
            }
        } else if (pluginURL.toString().startsWith("bundle")) {
            String urlString = pluginURL.toString();
            urlString = urlString.substring(0, urlString.lastIndexOf("/"));

            if (urlString.lastIndexOf("META-INF") >= 0)
                urlString = urlString + "/MANIFEST.MF";
            else
                urlString = urlString + "/META-INF/MANIFEST.MF";
            try {

                logger.debug(urlString.toString());
                return new URL(urlString);
            } catch (MalformedURLException e) {
                NucleusLogger.PLUGIN.warn(LOCALISER.msg("024010", urlString), e);
                return null;
            }

        }

        try
        {
            File file = new File(new URI(pluginURL.toString()).getPath());
            File[] dirs = new File(file.getParent()).listFiles(MANIFEST_FILE_FILTER);
            if (dirs != null && dirs.length > 0)
            {
                File[] files = dirs[0].listFiles(MANIFEST_FILE_FILTER);
                if (files != null && files.length > 0)
                {
                    try
                    {
                        return files[0].toURI().toURL();
                    }
                    catch (MalformedURLException e)
                    {
                        NucleusLogger.PLUGIN.warn(LOCALISER.msg("024011", pluginURL), e);
                        return null;
                    }
                }
            }
        }
        catch (URISyntaxException use)
        {
            use.printStackTrace();
            NucleusLogger.PLUGIN.warn(LOCALISER.msg("024011", pluginURL), use);
            return null;
        }

        NucleusLogger.PLUGIN.warn(LOCALISER.msg("024012", pluginURL));
        return null;
    }

    /**
     * Loads a class (do not initialize) from an attribute of {@link org.datanucleus.plugin.ConfigurationElement}
     * @param confElm the configuration element
     * @param name the attribute name
     * @return the Class
     */
    public Object createExecutableExtension(ConfigurationElement confElm, String name, Class[] argsClass, Object[] args)
        throws ClassNotFoundException,
        SecurityException,
        NoSuchMethodException,
        IllegalArgumentException,
        InstantiationException,
        IllegalAccessException,
            InvocationTargetException
    {
        Class cls = clr.classForName(confElm.getAttribute(name),OMFContext.class.getClassLoader());
        Constructor constructor = cls.getConstructor(argsClass);
        return constructor.newInstance(args);
    }

    /**
     * Loads a class (do not initialize)
     * @param pluginId the plugin id
     * @param className the class name
     * @return the Class
     * @throws ClassNotFoundException
     */
    public Class loadClass(String pluginId, String className) throws ClassNotFoundException
    {
        return clr.classForName(className, OMFContext.class.getClassLoader());
    }

    /**
     * Converts a URL that uses a user-defined protocol into a URL that uses the file protocol.
     * @param url the url to be converted
     * @return the converted URL
     * @throws java.io.IOException
     */
    public URL resolveURLAsFileURL(URL url) throws IOException
    {
        return url;
    }

    /**
     * Resolve constraints declared in bundle manifest.mf files.
     * This must be invoked after registering all bundles.
     * Should log errors if bundles are not resolvable, or raise runtime exceptions.
     */
    public void resolveConstraints()
    {
        Iterator<Bundle> it = registeredPluginByPluginId.values().iterator();
        while (it.hasNext())
        {
            Bundle bundle = it.next();
            List set = bundle.getRequireBundle();
            Iterator requiredBundles = set.iterator();
            while (requiredBundles.hasNext())
            {
                Bundle.BundleDescription bd = (Bundle.BundleDescription) requiredBundles.next();
                String symbolicName = bd.getBundleSymbolicName();

                Bundle requiredBundle = registeredPluginByPluginId.get(symbolicName);
                if (requiredBundle == null) // TODO Add option of only logging problems in DataNucleus deps
                {
                    if (bd.getParameter("resolution") != null &&
                        bd.getParameter("resolution").equalsIgnoreCase("optional"))
                    {
                        NucleusLogger.PLUGIN.debug(LOCALISER.msg("024013", bundle.getSymbolicName(), symbolicName));
                    }
                    else
                    {
                        NucleusLogger.PLUGIN.error(LOCALISER.msg("024014", bundle.getSymbolicName(), symbolicName));
                    }
                }

                if (bd.getParameter("bundle-version") != null)
                {
                    if (requiredBundle != null &&
                        !isVersionInInterval(requiredBundle.getVersion(), bd.getParameter("bundle-version")))
                    {
                        NucleusLogger.PLUGIN.error(LOCALISER.msg("024015", bundle.getSymbolicName(),
                            symbolicName, bd.getParameter("bundle-version"), bundle.getVersion()));
                    }
                }
            }
        }
    }

    /**
     * Check if the version is in interval
     * @param version
     * @param interval
     * @return
     */
    private boolean isVersionInInterval(String version, String interval)
    {
        //versionRange has only floor
        Bundle.BundleVersionRange versionRange = PluginParser.parseVersionRange(version);
        Bundle.BundleVersionRange intervalRange = PluginParser.parseVersionRange(interval);
        int compare_floor=versionRange.floor.compareTo(intervalRange.floor);
        boolean result = true;
        if (intervalRange.floor_inclusive)
        {
            result = compare_floor >= 0;
        }
        else
        {
            result = compare_floor > 0;
        }
        if (intervalRange.ceiling != null)
        {
            int compare_ceiling = versionRange.floor.compareTo(intervalRange.ceiling);
            if (intervalRange.ceiling_inclusive)
            {
                result = compare_ceiling <= 0;
            }
            else
            {
                result = compare_ceiling<0;
            }
        }
        return result;
    }

    /**
     * Accessor for all registered bundles
     * @return the bundles
     * @throws UnsupportedOperationException if this operation is not supported by the implementation
     */
    public Bundle[] getBundles()
    {
        return registeredPluginByPluginId.values().toArray(new Bundle[registeredPluginByPluginId.values().size()]);
    }
}