<%@ page import="com.nimbusds.oauth2.sdk.ResponseType" %>
<%@ page import="com.nimbusds.oauth2.sdk.Scope" %>
<%@ page import="com.nimbusds.oauth2.sdk.id.ClientID" %>
<%@ page import="com.nimbusds.oauth2.sdk.id.State" %>
<%@ page import="com.nimbusds.openid.connect.sdk.AuthenticationRequest" %>
<%@ page import="com.nimbusds.openid.connect.sdk.Nonce" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.util.Properties" %>
<%@ page import="com.nimbusds.oauth2.sdk.AuthorizationGrant" %>
<%@ page import="com.nimbusds.oauth2.sdk.pkce.CodeVerifier" %>
<%@ page import="com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod" %>
<%@ page import="java.util.UUID" %>
<%@ page import="java.util.Random" %>
<%@ page import="java.nio.charset.Charset" %>
<%@ page import="com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata" %>
<%@ page import="com.nimbusds.oauth2.sdk.id.Issuer" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%

    String error = null;

    try {
        Properties props = new Properties();
        InputStream is = getClass().getResourceAsStream("/oidc.properties");
        props.load(is);
        // This is the OpenID Connect Identity Provider ID (in JOSSO is the base URI for the OP services)
        Issuer issuer = new Issuer(props.getProperty("oidc.idp.id"));
        OIDCProviderMetadata op = OIDCProviderMetadata.resolve(issuer);
        URI authnEndpoint = op.getAuthorizationEndpointURI();

        URI redirectUri = new URI(props.getProperty("oidc.client.redirectUriBase") + request.getContextPath() + "/authzcode/pkce/process.jsp");
        Scope scope = Scope.parse(props.getProperty("oidc.client.scopes"));

        ResponseType rt = new ResponseType();
        rt.add(ResponseType.Value.CODE);

        ClientID clientId = new ClientID(props.getProperty("oidc.client.id"));

        State state = new State();
        Nonce nonce = new Nonce();

        CodeVerifier codeVerifier = new CodeVerifier();
        CodeChallengeMethod codeChallengeMethod = CodeChallengeMethod.S256;

        request.getSession().setAttribute("code_verifier", codeVerifier);

        AuthenticationRequest authnRequest = new AuthenticationRequest.Builder(rt, scope, clientId, redirectUri).
                endpointURI(authnEndpoint).state(state).nonce(nonce).codeChallenge(codeVerifier, codeChallengeMethod).
                build();

        response.sendRedirect(authnRequest.toURI().toString());

        //} catch (Exception e) {
        //error = e.getClass().getName();
    } catch (Exception e) {
        error = e.getMessage() + " ["+e.getClass()+"]";
    } finally {
        //
    }


%>

<html>
<head>
    <title>ODIC Client Test - Login</title>
</head>

<h3>Errors:</h3>
<% if (error != null) out.println(error); %>
<br>
</html>
