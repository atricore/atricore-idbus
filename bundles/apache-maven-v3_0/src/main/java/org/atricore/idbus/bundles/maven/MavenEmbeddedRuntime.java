package org.atricore.idbus.bundles.maven;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.Maven;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.cli.MavenLoggerManager;
import org.apache.maven.execution.*;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.building.*;
import org.atricore.idbus.kernel.common.support.osgi.OsgiBundleClassLoader;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;
import org.osgi.framework.Bundle;
import org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;

import java.beans.PropertyEditorSupport;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import org.osgi.framework.BundleContext;

/**
 * OSGi based maven runtime
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class MavenEmbeddedRuntime implements MavenRuntime {

    private static final Log logger = LogFactory.getLog(MavenEmbeddedRuntime.class);

    private static final String MAVEN_MAVEN_EMBEDDER_BUNDLE_ID = "org.atricore.idbus.bundles.apache-maven-v3_0";

    private static final String PLEXUS_CLASSWORLD_NAME = "plexus.core";

    // Make this more flexible ?
    private static final String DEPLOY_FOLDER = System.getProperty("karaf.home") + "/system";

    private URL[] plexusLauncherClasspath;
    private URL[] plexusClasspath;

    private String workingDirectory;

    private String baseDirectory;

    private String localRepositoryDirectory;

    private List<String> goals;

    private String mavenHome;

    private DefaultPlexusContainer container;

    private ModelProcessor modelProcessor;

    private Maven maven;
    
    private MavenExecutionRequestPopulator executionRequestPopulator;

    private SettingsBuilder settingsBuilder;

    private DefaultSecDispatcher dispatcher;

    private ClassWorld classWorld;

    private int logLevel;

    private BundleContext bundleContext;

    private ClassLoader originalClassLoader;

    public MavenEmbeddedRuntime(BundleContext bundleContext, String baseDirectory, List<String> goals) {
        this.bundleContext = bundleContext;
        this.baseDirectory = baseDirectory;
        this.goals = goals;

        // TODO : We may need to create a specific classloader for this!
        originalClassLoader = Thread.currentThread().getContextClassLoader();

        initialize();
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getLocalRepositoryDirectory() {
        return localRepositoryDirectory;
    }

    public void setLocalRepositoryDirectory(String localRepositoryDirectory) {
        this.localRepositoryDirectory = localRepositoryDirectory;
    }

    public MavenRuntimeExecutionOutcome doExecute() throws Exception {
        
        MavenEmbeddedRequest cliRequest = new MavenEmbeddedRequest();

        // logging( cliRequest );
        // properties( cliRequest );
        prepareMavenContainer(cliRequest);
        settings( cliRequest);
        // populateRequest( cliRequest);
        // encryption( );
        return executeMaven(cliRequest);

    }


    protected void initialize() {

        if (workingDirectory == null) {
            workingDirectory = System.getProperty("karaf.home");
        }

        if (logger.isTraceEnabled())
            logger.trace("Working directory " + workingDirectory);

        //
        // Make sure the Maven home directory is an absolute path to save us from confusion with say drive-relative
        // Windows paths.
        //
        mavenHome = System.getProperty("maven.home");
        if (mavenHome != null) {
            System.setProperty("maven.home", new File(mavenHome).getAbsolutePath());
        }
        if (logger.isTraceEnabled())
            logger.trace("Maven home " + workingDirectory);

        initClasspath(findMavenEmbedderBundle());

        File deployDir = new File(DEPLOY_FOLDER);
        if (!deployDir.exists()) {
            if (logger.isDebugEnabled())
                logger.debug("Creating deploy folder " + DEPLOY_FOLDER);
            deployDir.mkdirs();
        }
    }

    public void destroy() {

        //
        if (logger.isTraceEnabled())
            logger.trace("Disponsing of Plexus Container");
        container.dispose();

        if (logger.isTraceEnabled())
            logger.trace("Disponsing of ClassWorld Realm " + PLEXUS_CLASSWORLD_NAME);
        try {
            classWorld.disposeRealm(PLEXUS_CLASSWORLD_NAME);
        } catch (NoSuchRealmException e) {
            if (logger.isDebugEnabled())
                logger.debug(e.getMessage(), e);
        }

        Thread.currentThread().setContextClassLoader(originalClassLoader);
    }

    private Bundle findMavenEmbedderBundle() {
        Bundle bundle = null;
        Bundle[] bundles = bundleContext.getBundles();
        for (int i = 0; i < bundles.length; i++) {
            if (MAVEN_MAVEN_EMBEDDER_BUNDLE_ID.equals(bundles[i].getSymbolicName())) {
                bundle = bundles[i];
                break;
            }
        }
        return bundle;
    }


    private synchronized void initClasspath(Bundle bundle) {

        List<URL> cp = new ArrayList<URL>();
        List<URL> lcp = new ArrayList<URL>();

        @SuppressWarnings("unchecked")
        Enumeration<URL> entries = bundle.findEntries("/", "*", true);
        while (entries.hasMoreElements()) {
            URL url = entries.nextElement();
            String path = url.getPath();

            if (logger.isTraceEnabled())
                logger.trace("Processing URL for classpath " + url);

            if (path.endsWith(".jar") || path.endsWith("bin/")) {

                try {

                    if (path.contains("plexus-classworlds")) {
                        lcp.add(url);
                        cp.add(url);
                    } else {
                        cp.add(url);
                    }
                } catch (Exception ex) {
                    logger.error("Error adding classpath entry " + url.toString(), ex);
                }
            }
        }

        plexusClasspath = cp.toArray(new URL[cp.size()] );
        plexusLauncherClasspath = lcp.toArray(new URL[cp.size()] );

    }


    protected void prepareMavenContainer(MavenEmbeddedRequest cliRequest) throws Exception {

        // Build class world for plexus using special classloader, OSGi friendly.
        if (classWorld == null) {

            if (logger.isDebugEnabled())
                logger.debug("Creating ClassWorld instance ...");

            ClassLoader cl = new URLClassLoader(plexusClasspath);

            if (logger.isTraceEnabled()) {
                // Dump plexus descriptors found in classpath.
                Enumeration r = cl.getResources("META-INF/plexus/components.xml");
                while (r.hasMoreElements()) {
                    URL resource = (URL) r.nextElement();
                    logger.trace("Plexus Components at " + resource);
                }
            }

            // Initialize Classworld
            classWorld = new ClassWorld(PLEXUS_CLASSWORLD_NAME, new OsgiBundleClassLoader(findMavenEmbedderBundle()));
            classWorld.getRealm(PLEXUS_CLASSWORLD_NAME).setParentClassLoader(cl);
            
        } else {
            if (logger.isDebugEnabled())
                logger.debug("Reusing ClassWorld instance ...");
        }

        DefaultPlexusContainer container = this.container;
        if (container == null) {

            if (logger.isDebugEnabled())
                logger.debug("Creating Plexus Container instance ...");

            ContainerConfiguration cc = new DefaultContainerConfiguration()
                    .setClassWorld(classWorld)
                    .setName("maven");

            container = new DefaultPlexusContainer(cc);
            container.setLoggerManager(new MavenLoggerManager(new EmbeddedLogger(Logger.LEVEL_DEBUG, getClass().getName())));
            container.getLoggerManager().setThresholds(logLevel);
            customizeContainer(container);

            this.container = container;

        } else {
            if (logger.isDebugEnabled())
                logger.debug("Reusing Plexus Container instance ...");
        }

        
        // Lookup Maven
        maven = container.lookup(org.apache.maven.Maven.class);

        if (logger.isTraceEnabled())
            logger.trace("Found Maven instance " + maven);

        executionRequestPopulator = container.lookup(MavenExecutionRequestPopulator.class);
        modelProcessor = createModelProcessor(container);
        settingsBuilder = container.lookup(SettingsBuilder.class);
        dispatcher = (DefaultSecDispatcher) container.lookup(SecDispatcher.class, "maven");

    }
    
    protected void settings(MavenEmbeddedRequest cliRequest) 
        throws Exception{
        
        SettingsBuildingRequest settingsRequest = new DefaultSettingsBuildingRequest();
        settingsRequest.setGlobalSettingsFile( new File(baseDirectory + "/settings.xml" ));
        settingsRequest.setUserSettingsFile( new File(baseDirectory + "/settings.xml"));
        settingsRequest.setSystemProperties( new Properties());
        settingsRequest.setUserProperties( new Properties() );

        SettingsBuildingResult settingsResult = settingsBuilder.build( settingsRequest );

        executionRequestPopulator.populateFromSettings( cliRequest.request, settingsResult.getEffectiveSettings() );

        if ( !settingsResult.getProblems().isEmpty() && logger.isWarnEnabled() )
        {
            logger.warn( "" );
            logger.warn( "Some problems were encountered while building the effective settings" );

            for ( SettingsProblem problem : settingsResult.getProblems() )
            {
                logger.warn( problem.getMessage() + " @ " + problem.getLocation() );
            }

            logger.warn( "" );
        }
    }

    protected void customizeContainer(PlexusContainer container) {
    }

    protected ModelProcessor createModelProcessor(PlexusContainer container)
            throws ComponentLookupException {
        return container.lookup(ModelProcessor.class);
    }


    class EmbeddedLogger extends AbstractLogger {

        public EmbeddedLogger(int threshold, String name) {
            super(threshold, name);
        }

        public void debug(String s, Throwable throwable) {
            logger.debug(s, throwable);
        }

        public void info(String s, Throwable throwable) {
            logger.info(s, throwable);
        }

        public void warn(String s, Throwable throwable) {
            logger.warn(s, throwable);
        }

        public void error(String s, Throwable throwable) {
            logger.error(s, throwable);
        }

        public void fatalError(String s, Throwable throwable) {
            logger.fatal(s, throwable);
        }

        public Logger getChildLogger(String s) {
            return this;
        }
    }

    protected MavenRuntimeExecutionOutcome executeMaven(MavenEmbeddedRequest cliRequest) throws Exception {

        MavenRuntimeExecutionOutcome outcome = new MavenRuntimeExecutionOutcomeImpl();
        MavenExecutionRequest request = cliRequest.request;

        // TODO : Listen for upload events when deploying the artifacts ?
        PrintStream out = new PrintStream(System.out);
        request.setTransferListener(new org.apache.maven.cli.AbstractMavenTransferListener(out) {

        } );

        // 1. Setup local repository, where all artifacts will be published
        if (logger.isTraceEnabled())
            logger.trace("Setting up local repository");

        String lr = localRepositoryDirectory;
        if (localRepositoryDirectory == null) {
            lr = workingDirectory + "/data/work/maven/repository";
        }

        if (logger.isTraceEnabled())
            logger.trace("Local Repository:" + lr);
        request.setLocalRepositoryPath(lr);

        // 2.1 Disable central repository, and replace it with system
        if (logger.isTraceEnabled())
            logger.trace("Setting up 'central' repository mirror");

        Mirror defaultMirror = new Mirror();
        defaultMirror.setId("central-mirror");
        defaultMirror.setMirrorOf("central");
        defaultMirror.setName("Atricore OSGi based maven repository");
        defaultMirror.setUrl("osgi://atricore-central");

        request.addMirror(defaultMirror);

        // 2. Add remote repositories,
        if (logger.isTraceEnabled())
            logger.trace("Setting up 'snapshots' repository");

        MavenArtifactRepository remoteRepository = new MavenArtifactRepository("atricore-snapshots",
                "osgi://atricore-system",
                new DefaultRepositoryLayout(),
                new ArtifactRepositoryPolicy(true, null, null),
                new ArtifactRepositoryPolicy(true, null, null));

        request.addRemoteRepository(remoteRepository);

        // 3. Setup project folders
        if (logger.isTraceEnabled())
            logger.trace("Setting up project folders");

        File base = new File(baseDirectory);
        File pom = new File(baseDirectory + "/pom.xml");

        if (!base.exists() || !base.isDirectory())
            logger.error("Project base directory not found or is not a folder " + base.getAbsolutePath());

        if (!pom.exists() || !pom.isFile())
            logger.error("Project POM file not found or is not a file " + pom.getAbsolutePath());

        if (logger.isTraceEnabled())
            logger.trace("Project folder " + base.getAbsolutePath());

        if (logger.isTraceEnabled())
            logger.trace("Project POM File " + pom.getAbsolutePath());

        request.setBaseDirectory(base);
        request.setPom(pom);

        // 4. Configure plugins
        if (logger.isTraceEnabled())
            logger.trace("Setting up plugin groups");

        request.addPluginGroup("org.apache.maven.plugins");
        request.addPluginGroup("org.codehaus.mojo");
        request.addPluginGroup("org.codehaus.plexus");

        // 5. Setup goals
        if (logger.isTraceEnabled())
            logger.trace("Setting up goals");

        if (logger.isTraceEnabled()) {

            String g = "";
            for (String goal : goals) {
                g += goal + " ";
            }
            logger.trace("Executing goals : " + g);
        }

        request.setGoals(goals);
        request.setInteractiveMode(false);

        // 5. Run maven.
        if (logger.isTraceEnabled())
            logger.trace("Executing MAVEN ... " + request);

        MavenExecutionResult result = maven.execute(request);
        
        // 6. Check results
        if (result.hasExceptions()) {

            logger.error("Maven build has errors ...");
            int i = 1;
            for (Throwable t : result.getExceptions()) {
                outcome.addException(t);
                logger.error(i + ":" + t.getMessage(), t);
                i++;
            }

        }

        return outcome;
    }
    
    static class MavenEmbeddedRequest {
        
        MavenExecutionRequest request; 
        
        MavenEmbeddedRequest () {
            this.request = new DefaultMavenExecutionRequest();
        }
    }


}
