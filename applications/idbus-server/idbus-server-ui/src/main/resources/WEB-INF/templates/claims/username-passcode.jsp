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
<%@ taglib prefix="tiles"  uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

        <div id="authentication">

                <!-- TODO html:errors/-->

                <c:if test="${not empty statusMessageKey}">
                    <div class="message error">
                        <p>
                            <div>
                                <ul>
                                    <li><fmt:message key="${statusMessageKey}"/></li>
                                </ul>
                            </div>
                        </p>
                    </div>
                </c:if>

                <div id="subwrapper">

                    <div class="main">
                        <h2><fmt:message key="claims.title.userLogin"/></h2>
                        <p><fmt:message key="claims.text.userLogin"/></p>
                        <form:form method="post" commandName="collectUsernamePasscode" >
                            <fieldset>
                                <div><label for="username"><fmt:message key="claims.label.username"/> </label> <form:input cssClass="text" path="username" tabindex="10"/>
                                </div>
                                <div><label for="passcode"><fmt:message key="claims.label.passcode"/> </label> <form:password cssClass="text error" path="passcode" tabindex="20"/></div>
                                <div class="indent"><form:checkbox path="rememberMe" cssClass="checkbox" tabindex="30"/><fmt:message key="claims.label.rememberme"/></div>
                            </fieldset>
                            <div><input class="button indent" type="submit" value="Login"/></div>
                        </form:form>

                        <div class="highlight">
                            <h3 class="help"><fmt:message key="claims.title.help"/></h3>

                            <p><fmt:message key="claims.text.login.help"/>.</p>

                            <div class="footer"></div>

                        </div>
                        <!-- /highlight -->

                    </div>
                    <!-- /main -->
                </div>

            </div> <!-- /authentication -->
