package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href="mailto:gbrigandi@atricore.com">Gianluca Brigandi</a>
 * @version $Id$
 */
public class JBossEPPAuthenticationService extends AuthenticationService {

    private static final long serialVersionUID = 6035314548009890586L;

    private String host;
    private String port;
    private String context;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }


}
