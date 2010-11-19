<%@page contentType="text/html; charset=UTF-8" language="java" session="true" %>
<%--
  ~ Atricore IDBus
  ~
  ~ Copyright (c) 2009-2010, Atricore Inc.
  ~
  ~ This is free software; you can redistribute it and/or modify it
  ~ under the terms of the GNU Lesser General Public License as
  ~ published by the Free Software Foundation; either version 2.1 of
  ~ the License, or (at your option) any later version.
  ~
  ~ This software is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  ~ Lesser General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Lesser General Public
  ~ License along with this software; if not, write to the Free
  ~ Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  ~ 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  --%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
        <title>Atricore Console</title>
		<style type="text/css">
			html {
			  height: 100%;
			  overflow: hidden; /* Hides scrollbar in IE */
			}

			body {
			  height: 100%;
			  margin: 0;
			  padding: 0;
			}
		</style>
        <script type="text/javascript" src="<%=request.getContextPath()%>/js/swf/swfobject.js"></script>
        <script type="text/javascript">
            var flashvars = {};
            var params = {
                play: "true",
                loop: "false",
                quality: "high",
                allowscriptaccess: "always",
                menu: "false",
                scale: "noScale"
            };
            var attributes = {
                id: "atricore-console",
                name: "atricore-console"
            };
            swfobject.embedSWF("<%=request.getContextPath()%>/console-ria-1.0.0-m3.swf", "flashcontent", "100%", "100%", "10.0.0", "<%=request.getContextPath()%>/js/swf/expressInstall.swf", flashvars, params, attributes);

            function init() {
                var fl = document.getElementById("atricore-console");
                if (fl) { fl.focus(); }
            }
		</script>
    </head>
    <body scroll="no" onload="init();">
        <div id="flashcontent">
            You either have JavaScript turned off or an old version of Adobe's Flash Player. <a href="http://get.adobe.com/flashplayer/">Get the latest Flash player</a>.
        </div>
    </body>
</html>
