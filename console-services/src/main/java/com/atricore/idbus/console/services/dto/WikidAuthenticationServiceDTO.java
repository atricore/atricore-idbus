package com.atricore.idbus.console.services.dto;

public class WikidAuthenticationServiceDTO extends AuthenticationServiceDTO {

    private static final long serialVersionUID = -3519891446230268640L;

    private String serverHost;
    private int serverPort;
    private String serverCode;

    private KeystoreDTO caStore;
    private KeystoreDTO wcStore;

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public KeystoreDTO getCaStore() {
        return caStore;
    }

    public void setCaStore(KeystoreDTO caStore) {
        this.caStore = caStore;
    }

    public KeystoreDTO getWcStore() {
        return wcStore;
    }

    public void setWcStore(KeystoreDTO wcStore) {
        this.wcStore = wcStore;
    }
}
