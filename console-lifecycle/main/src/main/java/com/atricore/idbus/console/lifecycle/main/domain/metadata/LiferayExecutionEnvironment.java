package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/5/13
 */
public class LiferayExecutionEnvironment extends ExecutionEnvironment implements CaptiveExecutionEnvironment {

    private static final long serialVersionUID = 8294875871223858432L;

    private String containerType;

    private String containerPath;

    private boolean installDemoApps;

    public String getContainerType() {
        return containerType;
    }

    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }

    public String getContainerPath() {
        return containerPath;
    }

    public void setContainerPath(String containerPath) {
        this.containerPath = containerPath;
    }

    public boolean isInstallDemoApps() {
        return installDemoApps;
    }

    public void setInstallDemoApps(boolean installDemoApps) {
        this.installDemoApps = installDemoApps;
    }
}
