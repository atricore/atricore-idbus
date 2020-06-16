<html>

<jsp:include page="authzcode/inc/header.jsp" />

<body class="gt-fixed">

<jsp:include page="authzcode/inc/top-bar.jsp" />

<div id="idbus-error" class="gt-bd clearfix">
    <div class="gt-content">
        <div>
            <h2 class="gt-table-head">OIDC Login</h2>
        </div>

        <div>
            <ul>
                <li><a href="authzcode/client-secret/login.jsp">Login (client-secret)</a></li>
                <li><a href="authzcode/pkce/login.jsp">Login (pkce)</a></li>
            </ul>
        </div>
    </div>
</div>
</body>
</html>
