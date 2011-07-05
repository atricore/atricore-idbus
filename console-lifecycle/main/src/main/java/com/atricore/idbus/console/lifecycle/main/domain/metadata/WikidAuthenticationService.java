package com.atricore.idbus.console.lifecycle.main.domain.metadata;

public class WikidAuthenticationService extends AuthenticationService {

    private static final long serialVersionUID = -1319907262692599679L;

    private String serverHost;
    private int serverPort;
    private String serverCode;

    private Keystore caStore;
    private Keystore wcStore;

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

    public Keystore getCaStore() {
        return caStore;
    }

    public void setCaStore(Keystore caStore) {
        this.caStore = caStore;
    }

    public Keystore getWcStore() {
        return wcStore;
    }

    public void setWcStore(Keystore wcStore) {
        this.wcStore = wcStore;
    }
}
