package org.atricore.idbus.capabilities.openidconnect.main.op;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;

/**
 * Created by sgonzalez on 7/30/15.
 */
public class OpenIDConnectProviderException extends OpenIDConnectException {

    private ErrorObject error;

    private String description;

    public OpenIDConnectProviderException(ErrorObject error, String description, Throwable cause) {
        super(error.toString() + description != null ? " ["+description+"]" : "", cause);
        this.error = error;
        this.description = description;
    }

    public OpenIDConnectProviderException(ErrorObject error, String description) {
        super(error.toString() + description != null ? " ["+description+"]" : "");
        this.error = error;
        this.description = description;
    }

    public ErrorObject getProtocolError() {
        return error;
    }
}
