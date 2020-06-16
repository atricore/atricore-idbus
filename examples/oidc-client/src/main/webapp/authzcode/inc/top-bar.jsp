<!-- head -->
<div class="gt-hd clearfix">
    <!-- leaf watermark -->
    <div class="gt-leaf-watermark clearfix">

        <!-- head top -->
        <div class="gt-hd-top clearfix">
            <div class="gt-fixed">
                <h1>&nbsp;OIDC Example</h1>
            </div>
            <!-- utility box -->
            <div class="gt-util-box">
                <div class="gt-util-box-inner">
                    <% if (request.getSession().getAttribute("username") != null) { %>
                    <p>Welcome, <a href="<%=request.getContextPath()%>/authzcode/user-info.jsp"><%=request.getSession().getAttribute("username")%>!</a> <span class="gt-util-separator">|</span> <a href="<%=request.getContextPath()%>/authzcode/logout.jsp">logout</a></p>
                    <%} else { %>
                    <p>-</span> <span class="gt-util-separator">|</span> <a href="<%=request.getContextPath()%>/authzcode/login.jsp">login</a></p>
                    <% } %>
                </div>
            </div>
            <!-- / utility box -->

        </div><!-- /head top -->


    </div><!-- /leaf watermark -->
</div>
<!-- / head -->
