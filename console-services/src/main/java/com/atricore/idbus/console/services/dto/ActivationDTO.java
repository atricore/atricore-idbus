package com.atricore.idbus.console.services.dto;

/**
 * Author: Dejan Maric
 */
public class ActivationDTO extends ConnectionDTO {

    private static final long serialVersionUID = 3889745220384784875L;

    private ExecutionEnvironmentDTO executionEnv;

    private ServiceProviderDTO sp;

    public ExecutionEnvironmentDTO getExecutionEnv() {
        return executionEnv;
    }

    public void setExecutionEnv(ExecutionEnvironmentDTO executionEnv) {
        this.executionEnv = executionEnv;
    }

    public ServiceProviderDTO getSp() {
        return sp;
    }

    public void setSp(ServiceProviderDTO sp) {
        this.sp = sp;
    }
}