package org.atricore.idbus.bundles.apacheds.server.boot;

import org.apache.directory.server.configuration.ApacheDS;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by IntelliJ IDEA.
 * User: sgonzalez
 * Date: Nov 24, 2010
 * Time: 3:42:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class LdapServerBoot implements InitializingBean {

    private ApacheDS apacheDS;

    public void afterPropertiesSet() throws Exception {
        apacheDS.startup();
    }

    public ApacheDS getApacheDS() {
        return apacheDS;
    }

    public void setApacheDS(ApacheDS apacheDS) {
        this.apacheDS = apacheDS;
    }
}
