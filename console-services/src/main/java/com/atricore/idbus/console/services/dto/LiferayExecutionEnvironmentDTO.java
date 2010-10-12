package com.atricore.idbus.console.services.dto;

public class LiferayExecutionEnvironmentDTO extends ExecutionEnvironmentDTO {

    private static final long serialVersionUID = 112239879734953533L;

    private String containerType;

    private String containerPath;

    public String getContainerPath() {
        return containerPath;
    }

    public void setContainerPath(String containerPath) {
        this.containerPath = containerPath;
    }

    public String getContainerType() {
        return containerType;
    }

    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }
}
