<%@ page import="org.atricore.idbus.capabilities.oauth2.client.OAuth2Client" %>
<%@ page import="org.atricore.idbus.capabilities.oauth2.client.OAuth2ClientException" %>
<%@ page import="org.atricore.idbus.common.oauth._2_0.protocol.SSOPolicyEnforcementStatementType" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Properties" %>
<%

    // Create oauth 2 client using properties file and initialize it
    // (This can be configured as part of the app. initialization.
    OAuth2Client client = new OAuth2Client("/oauth2.properties");
    client.init();

    // Request the pre-authentication URL for the received credentials
    Throwable error = null;

    try {

        String defaultSPAlias = "https://sso.atricore.com/IDBUS/MLINK/SP-1/SAML2/MD";
        Properties props = new Properties();
        props.setProperty("remember_me", "true");

        // **/SOAP

        client.sendPasswordlessLink(request.getParameter("username"), defaultSPAlias, null, props);

    } catch (OAuth2ClientException e) {
        error = e;
    }

%>
<html>
<head>
    <title>Sent!</title>
</head>
<body>

<h2>Check your email to complete the login process!.</h2>

<p>

</p>
<% if (error != null) { %>
<p>
    Error Details:

    <%
        List<SSOPolicyEnforcementStatementType> ssoPolicyEnforcements = ((OAuth2ClientException) error).getSsoPolicyEnforcements();
        if (ssoPolicyEnforcements != null && ssoPolicyEnforcements.size() > 0) {%>
            <br/><br/>Policy Enforcements:<br/><br/>
            <%for (SSOPolicyEnforcementStatementType stmt : ssoPolicyEnforcements) {%>
                <%=stmt.getName()%>
                <br/><br/>
            <%}
        }
    %>

    <% // Look for the root cause of the problem
        Throwable cause = error.getCause();
        while (cause != null) {
            error = cause;
            cause = error.getCause();
        }
        error.printStackTrace();
    %>

    <%=error.getMessage()%>

</p>
<% } %>
</body>
</html>

