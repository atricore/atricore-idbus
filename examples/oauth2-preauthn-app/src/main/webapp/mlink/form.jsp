<%
String issuer = request.getParameter("issuer");
String id = request.getParameter("request_id");
String relayState = request.getParameter("relay_state");

request.getSession().setAttribute("relay_state", relayState);

// TODO : Verify isuser

%>

<html>
<head>
    <title>Login Page</title>
</head>

<h2>Hello, please log in:</h2>
<br><br>
<form action="<%=request.getContextPath()%>/mlink/process.jsp" method=post>
    <p><strong>User Name: </strong>
        <input type="text" name="username" size="25">
    </p>
    <input type="submit" value="Submit">
    <input type="reset" value="Reset">
</form>
</html>