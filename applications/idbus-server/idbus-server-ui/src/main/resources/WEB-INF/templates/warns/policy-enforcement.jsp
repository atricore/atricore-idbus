<%--
  ~ Atricore IDBus
  ~
  ~ Copyright (c) 2011, Atricore Inc.
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
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="tiles"  uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

        <div id="warning">

            <div id="subwrapper">

                  <div class="main">
                        <h2><fmt:message key="warning.title"/></h2>

                        <p><fmt:message key="warning.subtitle"/></p>

                        <div class="highlight horizontal info">

                        <c:if test="${not empty confirmWarnings}">
                         <c:forEach var="warningData" items="${confirmWarnings.warningData}">
                            <p><strong><fmt:message key="${warningData.msgKey}">
                                <c:if test="${warningData.hasMsgParams}">
                                <c:forEach var="msgParam" items="${warningData.msgParams}">
                                    <fmt:param value="${msgParam}"/>
                                </c:forEach>
                                </c:if>
                                </fmt:message>
                            </strong></p>
                         </c:forEach>
                            <div class="footer"></div>
                        </div><!-- /highlight -->
                        </c:if>

                      <form:form method="post" commandName="confirmWarnings" >
                        <div><input class="button indent" type="submit" value="Continue"/></div>
                      </form:form>

                        <div id="login-options" class="clearfix">

                            <!--
                        <div id="col1">
                            <h3 class="arrow">To access a protected resource</h3>
                            <p>Access a protected resource: <a href="<%=request.getContextPath()%>/protected/">protected</a>.</p>
                        </div>

                        <div id="col2">
                            <h3 class="arrow">To see extended user info</h3>
                            <p>See extended user info, try <a href="<%=request.getContextPath()%>/protected-josso">protected-josso</a>.</p>
                        </div>
                            -->

                        </div> <!-- /login-options -->


                  </div><!-- /main -->
            </div>

        </div> <!-- /warning -->
