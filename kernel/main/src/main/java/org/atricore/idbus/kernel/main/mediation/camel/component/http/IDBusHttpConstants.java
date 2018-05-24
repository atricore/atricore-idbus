package org.atricore.idbus.kernel.main.mediation.camel.component.http;

/**
 * Created by sgonzalez on 6/25/14.
 */
public interface IDBusHttpConstants {

    public static final String HTTP_HEADER_IDBUS_REMOTE_ADDRESS = "X-IdBusRemoteAddress";

    public static final String HTTP_HEADER_IDBUS_REMOTE_HOST = "X-IdBusRemoteHost";

    public static final String HTTP_HEADER_IDBUS_SECURE = "X-IdBusSecure";

    public static final String HTTP_HEADER_IDBUS_PROXIED_REQUEST = "X-IdBusProxiedRequest";

    public static final String HTTP_HEADER_IDBUS_FOLLOW_REDIRECT = "X-IdBus-FollowRedirect";

    public static final String HTTP_HEADER_FOLLOW_REDIRECT = "FollowRedirect";

    public static final String HTTP_HEADER_FRAME_OPTIONS = "X-Frame-Options";

    public static final String HTTP_HEADER_CONTENT_SECURITY_POLICY = "Content-Security-Policy";

}
