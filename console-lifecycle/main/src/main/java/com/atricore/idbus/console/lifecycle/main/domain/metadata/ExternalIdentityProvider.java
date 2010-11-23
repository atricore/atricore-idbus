package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * Created by IntelliJ IDEA.
 * User: sgonzalez
 * Date: Nov 23, 2010
 * Time: 2:48:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExternalIdentityProvider extends FederatedProvider {

    private Resource metadata;

    public Resource getMetadata() {
        return metadata;
    }

    public void setMetadata(Resource metadata) {
        this.metadata = metadata;
    }
}
