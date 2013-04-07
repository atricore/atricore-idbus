package com.atricore.idbus.console.services.dto;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/5/13
 */
public class LiferayExecutionEnvironmentDTO extends ExecutionEnvironmentDTO implements CaptiveExecutionEnvironmentDTO {

    private static final long serialVersionUID = 4755408700330082451L;

    private String containerType;

    private String containerPath;

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

}
