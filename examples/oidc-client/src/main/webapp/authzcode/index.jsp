<%@ page contentType="text/html; charset=UTF-8" %>
<html>

<jsp:include page="inc/header.jsp" />

<body class="gt-fixed">

<jsp:include page="inc/top-bar.jsp" />

<div id="idbus-error" class="gt-bd clearfix">

<p>
    <%
    out.println("<ul>");
    out.println("<li><b>IDToken:</b> " + session.getAttribute("id_token") + "</li>");
    out.println("<li><b>RefreshToken:</b> " + session.getAttribute("refresh_token") + "</li>");
    out.println("<li><b>BearerAccessToken:</b> " + session.getAttribute("bearer_access_token") + "</li>");
    out.println("<li><b>SessionState:</b> " + session.getAttribute("session_state") + "</li>");
    out.println("</ul>");
    %>
</p>
</div>

</body>
</html>
