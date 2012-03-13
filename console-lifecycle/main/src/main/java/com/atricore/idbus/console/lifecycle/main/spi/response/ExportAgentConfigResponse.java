package com.atricore.idbus.console.lifecycle.main.spi.response;

public class ExportAgentConfigResponse extends AbstractManagementResponse {

    private String fileName;

    private byte[] agentConfig;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getAgentConfig() {
        return agentConfig;
    }

    public void setAgentConfig(byte[] agentConfig) {
        this.agentConfig = agentConfig;
    }
}
