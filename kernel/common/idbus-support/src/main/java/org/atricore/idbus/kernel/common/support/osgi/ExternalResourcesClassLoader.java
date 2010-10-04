package org.atricore.idbus.kernel.common.support.osgi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ExternalResourcesClassLoader extends URLClassLoader {

    private static final Log logger = LogFactory.getLog(ExternalResourcesClassLoader.class);

    private BundleContext bundleContext;

    private Collection<String> classPath;

    private HashSet<String> fileSet = new HashSet<String>();

    private FileFilter resourceFilter;

    /**
     * For testing purposes only!
     */
    @Deprecated
    public ExternalResourcesClassLoader(ClassLoader parent, Collection<String> classPath, FileFilter filter) {
        super(new URL[0], parent);
        this.classPath = classPath;
        this.resourceFilter = filter;
    }


    public ExternalResourcesClassLoader(BundleContext bundleContext, Collection<String> classPath) {
        super(new URL[0], new OsgiBundleClassLoader(bundleContext.getBundle()));
        this.bundleContext = bundleContext;
        this.classPath = classPath;
    }

    /**
     * @param classPath
     */
    public ExternalResourcesClassLoader(BundleContext bundleContext, Collection<String> classPath, FileFilter filter) {
        super(new URL[0], new OsgiBundleClassLoader(bundleContext.getBundle()));
        this.bundleContext = bundleContext;
        this.classPath = classPath;
        this.resourceFilter = filter;
    }


    protected PermissionCollection getPermissions(CodeSource codesource) {
        return this.getClass().getProtectionDomain().getPermissions();
    }


    public boolean refreshClasspath()
            throws Exception {
        boolean foundNewFlag = false;

        if (classPath != null && classPath.size() > 0) {
            try {

                for (String classPathEntry : classPath) {

                    // TODO : Improve URI resource resolution, suport bundle:// , file://, mvn://, etc ?!
                    File resourceFile = new File(new URI(classPathEntry));
                    if (!resourceFile.exists()) {
                        logger.warn("Resource not found, ignoring! " + resourceFile.getAbsolutePath());
                        continue;
                    }

                    logger.debug("ExternalResourceClassloader: found resource file "
                            + resourceFile.getName()
                            + ". URL="
                            + resourceFile.toURI().toURL());

                    addURL(resourceFile.toURI().toURL());

                    // If this is a folder, look for children
                    if (resourceFile.isDirectory()) {

                        File[] childrenFiles = resourceFilter != null ?
                                resourceFile.listFiles(resourceFilter) :
                                resourceFile.listFiles();

                        for (File childFile : childrenFiles) {

                            if (fileSet.contains(childFile.getName()))
                                continue;


                            // This is a new file not previously
                            // added to URL list
                            foundNewFlag = true;
                            fileSet.add(childFile.getName());

                            addURL(childFile.toURI().toURL());
                            logger.debug("ExternalResourceClassloader: found resource file "
                                    + childFile.getName()
                                    + ". URL="
                                    + childFile.toURI().toURL());
                        }
                    }
                }

            } catch (MalformedURLException e) {
                throw new Exception(e);
            }
        }
        return foundNewFlag;
    }


}
