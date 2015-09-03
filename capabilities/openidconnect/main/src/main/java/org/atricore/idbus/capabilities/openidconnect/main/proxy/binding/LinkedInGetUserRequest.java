package org.atricore.idbus.capabilities.openidconnect.main.proxy.binding;

import com.google.api.client.http.GenericUrl;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.Oauth2Request;
import org.atricore.idbus.capabilities.openidconnect.main.proxy.producers.LinkedInUser;

public class LinkedInGetUserRequest extends Oauth2Request<LinkedInUser> {

    private static final String REST_PATH = "v1/people/~";

    public LinkedInGetUserRequest(Oauth2 client) {
        super(client, "GET", REST_PATH, null, LinkedInUser.class);
    }

    @Override
    public GenericUrl buildHttpRequestUrl() {
        return new GenericUrl(getAbstractGoogleClient().getBaseUrl() + getUriTemplate() + ":(" + getFields() + ")?format=json");
    }
}
