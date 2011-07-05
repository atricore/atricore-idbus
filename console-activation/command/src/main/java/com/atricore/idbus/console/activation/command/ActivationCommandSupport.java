package com.atricore.idbus.console.activation.command;

import com.atricore.idbus.console.activation.main.spi.ActivationService;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.osgi.framework.ServiceReference;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class ActivationCommandSupport extends OsgiCommandSupport {

    @Option(name = "-t", aliases = {"--target"}, description = "Target Install directory", required = true)
    protected String target = "";

    /**
     * In the futur we could find the platform looking in the target directory
     */
    @Option(name = "-p", aliases = {"--platform"}, description = "see list-platforms for a complete list", required = true)
    protected String targetPlatformId = "";

    @Option(name = "-f", aliases = {"--force-install"}, description = "Force installation if some target validations fail", required = false)
    protected boolean forceInstall;

    @Option(name = "-r", aliases = {"--replace"}, description = "Replace installed files, includes configuration", required = false)
    protected boolean replaceConfig;

    @Option(name = "-idphn", aliases = {"--idp-host-name"}, description = "Define identity provider host name", required = false)
    protected String idpHostName = "localhost";

    @Option(name = "-idpp", aliases = {"--idp-port"}, description = "Define identity provider port", required = false)
    protected String idpPort = "8081";

    // Only atricore-idbus idpType supported in JOSSO 2
    protected String idpType = "atricore-idbus";

    // Some platform specific properties

    @Option(name = "-i", aliases = {"--jboss-instance"}, description = "JBoss instance", required = false)
    protected String jbossInstance = "default";

    @Option(name = "-d", aliases = {"--weblogic-domain"}, description = "Weblogic domain path", required = false)
    protected String weblogicDomain = "samples/domains/wl_server";

    @Option(name = "-u", aliases = {"--user"}, description = "Define user for server login", required = false)
    protected String user;

    @Option(name = "-w", aliases = {"--password"}, description = "Define password for server login", required = false)
    protected String password;

    @Option(name = "-td", aliases = {"--tcdir"}, description = "Define Tomcat install directory", required = false)
    protected String tomcatInstallDir;

    @Option(name = "-jd", aliases = {"--jbdir"}, description = "Define JBoss install directory", required = false)
    protected String jbossInstallDir;

    @Override
    protected Object doExecute() throws Exception {

        // Get repository admin service.
        ServiceReference ref = getBundleContext().getServiceReference(ActivationService.class.getName());
        if (ref == null) {
            System.out.println("Identity Appliance Management Service is unavailable. (no service reference)");
            return null;
        }
        try {
            ActivationService svc = (ActivationService) getBundleContext().getService(ref);
            if (svc == null) {
                System.out.println("Identity Appliance Management Service service is unavailable. (no service)");
                return null;
            }

            doActivate(svc);

        } catch (Exception e) { // Force reference to exception class , do not change
            throw new RuntimeException(e.getMessage(), e);

        } finally {
            getBundleContext().ungetService(ref);
        }
        return null;

    }

    protected abstract Object doActivate(ActivationService svc) throws Exception;


    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTargetPlatformId() {
        return targetPlatformId;
    }

    public void setTargetPlatformId(String targetPlatformId) {
        this.targetPlatformId = targetPlatformId;
    }

    public boolean isForceInstall() {
        return forceInstall;
    }

    public void setForceInstall(boolean forceInstall) {
        this.forceInstall = forceInstall;
    }

    public boolean isReplaceConfig() {
        return replaceConfig;
    }

    public void setReplaceConfig(boolean replaceConfig) {
        this.replaceConfig = replaceConfig;
    }

    public String getIdpHostName() {
        return idpHostName;
    }

    public void setIdpHostName(String idpHostName) {
        this.idpHostName = idpHostName;
    }

    public String getIdpPort() {
        return idpPort;
    }

    public void setIdpPort(String idpPort) {
        this.idpPort = idpPort;
    }

    public String getIdpType() {
        return idpType;
    }

    public void setIdpType(String idpType) {
        this.idpType = idpType;
    }

    public String getJbossInstance() {
        return jbossInstance;
    }

    public void setJbossInstance(String jbossInstance) {
        this.jbossInstance = jbossInstance;
    }

    public String getWeblogicDomain() {
        return weblogicDomain;
    }

    public void setWeblogicDomain(String weblogicDomain) {
        this.weblogicDomain = weblogicDomain;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTomcatInstallDir() {
        return tomcatInstallDir;
    }

    public void setTomcatInstallDir(String tomcatInstallDir) {
        this.tomcatInstallDir = tomcatInstallDir;
    }

    public String getJbossInstallDir() {
        return jbossInstallDir;
    }

    public void setJbossInstallDir(String jbossInstallDir) {
        this.jbossInstallDir = jbossInstallDir;
    }

}
