<%--
  ~ Atricore IDBus
  ~
  ~ Copyright (c) 2009, Atricore Inc.
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
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="idbus-error">

    <div class="message error">
        <p>
        <div>
            <ul>
                <li><fmt:message key="error.title"/></li>
            </ul>
        </div>
        </p>
    </div>

    <c:if test="${not empty IDBusError}">


        <div>
            <p>
                <strong><fmt:message key="${IDBusError.status}"/></strong>
                <c:if test="${not empty IDBusError.secStatus}"><br/><fmt:message key="${IDBusError.secStatus}"/></c:if>
                <c:if test="${not empty IDBusError.details}"><br/><fmt:message key="${IDBusError.details}"/></c:if>
            </p>
            <div class="footer"></div>
        </div>

        <%-- TODO : Make debug info available in a different page! --%>

        <div>
            <h3><fmt:message key="error.debug.title"/></h3>

            <ul>
                <li><c:out value="${IDBusError.status}"/></li>
                <c:if test="${not empty IDBusError.secStatus}"><li><c:out value="${IDBusError.secStatus}"/></li></c:if>
                <c:if test="${not empty IDBusError.details}"><li><c:out value="${IDBusError.details}"/></li></c:if>
            </ul>

            <%-- Error Message --%>
            <h4><fmt:message key="error.message.label"/></h4>
            <p><c:out value="${IDBusError.errDetails}"/></p>
            <%-- Error Caused By --%>
            <c:forEach var="cause" items="${IDBusError.causes}">
                <h5><fmt:message key="error.causedBy.label"/></h5>

                <p style="font-size:10px;"><c:out value="${cause}"/></p>
            </c:forEach>
        </div>
    </c:if>

</div>
