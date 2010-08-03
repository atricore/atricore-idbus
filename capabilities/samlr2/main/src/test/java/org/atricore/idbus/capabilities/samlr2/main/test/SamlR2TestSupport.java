/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.capabilities.samlr2.main.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.support.core.util.XmlUtils;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.SessionHandler;
import org.springframework.context.ApplicationContext;
import org.w3._1999.xhtml.Form;
import org.w3._1999.xhtml.Html;

import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SamlR2TestSupport.java 1492 2009-09-02 21:58:23Z sgonzalez $
 */
public class SamlR2TestSupport {

    private static final Log logger = LogFactory.getLog(SamlR2TestSupport.class);

    protected Html unmarshallNonXhtml(String html) throws Exception {

        // TODO : Improve this !!!
        String xhtml = "<?xml version='1.0' encoding='UTF-8'?>\n<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">";
        int post = html.indexOf("<html>");
        xhtml += html.substring(post + 6);

        // Replace <META HTTP-EQUIV="expires" CONTENT="0"> with <META HTTP-EQUIV="expires" CONTENT="0"/>
        xhtml = xhtml.replaceAll("CONTENT=\\\"0\\\">", "CONTENT=\"0\"\\/>");

        // Remove end of lines!
        xhtml = xhtml.replaceAll("\n", "");

        return unmarshallHtml(xhtml);

    }

    protected Html unmarshallHtml(String xhtmlStr) throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("Unmarshalling:\n" + xhtmlStr);

        return (Html) XmlUtils.unmarshal(xhtmlStr, new String[]{"org.w3._1999.xhtml"});
    }

    protected Form getForm(Html html) throws Exception {

        List pOrH1OrH2 = html.getBody().getPOrH1OrH2();

        for (Object obj : pOrH1OrH2) {
            if (obj instanceof Form) {
                return (Form) obj;
            }
        }

        return null;
    }


    protected IDBusServlet createServlet(ApplicationContext applicationContext,
                                         Server server, String contextPath) throws Exception {

        // We only support ONE connector!
        // Connector connector = server.getConnectors()[0];

        for (Connector connector : server.getConnectors()) {
            logger.debug(connector.getName());
        }

        Connector connector = server.getConnectors()[0];

        // Prepare context : 
        Context context = new Context(server, contextPath, Context.NO_SECURITY);
        context.addHandler(new SessionHandler());
        context.setConnectorNames(new String[] {connector.getName()});

        // Prepare serlvet :
        ServletHolder holder = new ServletHolder();
        IDBusServlet IDBusServlet = new IDBusServlet();
        holder.setServlet(IDBusServlet);
        context.addServlet(holder, "/*");

        // Start all
        context.start();

        // Set servlet attribute with Spring App. Context
        IDBusServlet.getServletContext().setAttribute(IDBusServlet.SPRING_APP_CTX, applicationContext);

        return IDBusServlet;
    }


}
