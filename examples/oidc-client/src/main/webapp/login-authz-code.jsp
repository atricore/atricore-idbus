<%@ page import="com.nimbusds.oauth2.sdk.ResponseType" %>
<%@ page import="com.nimbusds.oauth2.sdk.Scope" %>
<%@ page import="com.nimbusds.oauth2.sdk.id.ClientID" %>
<%@ page import="com.nimbusds.oauth2.sdk.id.State" %>
<%@ page import="com.nimbusds.openid.connect.sdk.AuthenticationRequest" %>
<%@ page import="com.nimbusds.openid.connect.sdk.Nonce" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.util.Properties" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%

    String error = null;

    try {
        Properties props = new Properties();
        InputStream is = getClass().getResourceAsStream("/oidc.properties");
        props.load(is);

        URI authnEndpoint = new URI(props.getProperty("oidc.authn.endpoint"));
        URI redirectUri = new URI(props.getProperty("oidc.authn.redirectUri"));

        Scope scope = Scope.parse(props.getProperty("oidc.client.scopes"));

        ResponseType rt = new ResponseType();
        rt.add(ResponseType.Value.CODE);

        ClientID clientId = new ClientID(props.getProperty("oidc.client.id"));

        State state = new State();
        Nonce nonce = new Nonce();

        AuthenticationRequest authnRequest = new AuthenticationRequest(authnEndpoint, rt, scope, clientId, redirectUri, state, nonce);
        response.sendRedirect(authnRequest.toURI().toString());

        //} catch (Exception e) {
        //error = e.getClass().getName();
    } finally {
        //
    }


%>

<html>
<head>
    <title>ODIC Client Test - Authorization code flow</title>
</head>

<h3>Errors:</h3>
<% if (error != null) out.println(error); %>
<br>
</html>
