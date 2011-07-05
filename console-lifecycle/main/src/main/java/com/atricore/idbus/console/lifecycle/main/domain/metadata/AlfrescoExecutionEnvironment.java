package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AlfrescoExecutionEnvironment extends ExecutionEnvironment {

    private static final long serialVersionUID = 3324230985098604032L;
    private String tomcatInstallDir;

    public String getTomcatInstallDir() {
        return tomcatInstallDir;
    }

    public void setTomcatInstallDir(String tomcatInstallDir) {
        this.tomcatInstallDir = tomcatInstallDir;
    }
}
