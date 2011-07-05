package com.atricore.idbus.console.activation.main.spi.request;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class AbstractActivationRequest {

    private String targetPlatformId = "";
    private boolean forceInstall = false;
    private String idpHostName = "localhost";
    private String idpPort = "8081";
    private String idpType = "atricore-idbus";

    private String jbossInstance = "default";
    private String weblogicDomain = "samples/domains/wl_server";
    private String user;
    private String password;
    private String target;
    private String tomcatInstallDir;
    private String jbossInstallDir;


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

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
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
