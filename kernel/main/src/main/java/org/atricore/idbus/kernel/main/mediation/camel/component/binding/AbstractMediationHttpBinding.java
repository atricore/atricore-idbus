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

package org.atricore.idbus.kernel.main.mediation.camel.component.binding;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.*;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelMediator;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.main.mediation.state.LocalState;
import org.atricore.idbus.kernel.main.mediation.state.ProviderStateContext;
import org.w3._1999.xhtml.*;

import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.io.*;
import java.lang.Object;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public abstract class AbstractMediationHttpBinding extends AbstractMediationBinding {

    private static final Log logger = LogFactory.getLog(AbstractMediationHttpBinding.class);

    public AbstractMediationHttpBinding(String binding, Channel channel) {
        super(binding, channel);
    }

    public void copyFaultMessageToExchange(CamelMediationMessage fault, Exchange exchange) {
        // Store error and redirect to an error display page!

        String errorUrl = getChannel().getIdentityMediator().getErrorUrl();

        errorUrl = buildHttpTargetLocation(exchange.getIn(),
                new EndpointDescriptorImpl("idbus-error-page",
                        "ErrorUIService",
                        getBinding(),
                        errorUrl,
                        null),
                true);

        if (logger.isDebugEnabled())
            logger.debug("Processing Fault Message " + fault.getMessageId() + ". Redirecting to " +
                    errorUrl);

        // ------------------------------------------------------------
        // Prepare HTTP Resposne
        // ------------------------------------------------------------
        // copyBackState(fault.getMessage().getState(), exchange);

        Message httpOut = exchange.getOut();

        httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
        httpOut.getHeaders().put("Pragma", "no-cache");
        httpOut.getHeaders().put("Content-Type", "text/html");

        if (errorUrl != null) {

            if (logger.isDebugEnabled())
                logger.debug("Configured error URL " + errorUrl + ".  Redirecting.");

            if (getChannel().getIdentityMediator() instanceof AbstractCamelMediator) {

                try {
                    AbstractCamelMediator mediator = (AbstractCamelMediator) getChannel().getIdentityMediator();
                    Artifact a = mediator.getArtifactQueueManager().pushMessage(fault.getMessage());

                    errorUrl += "?IDBusErrArt=" + a.getContent();

                    if (logger.isDebugEnabled())
                        logger.debug("Configured error URL " + errorUrl + ".  Redirecting.");

                    httpOut.getHeaders().put("http.responseCode", 302);
                    httpOut.getHeaders().put("Location", errorUrl);
                    return;
                } catch (Exception e) {
                    logger.error("Cannot forward error to error URL:" + errorUrl, e);
                    // Go on, error will be locally displayed
                }

            } else {
                httpOut.getHeaders().put("http.responseCode", 302);
                httpOut.getHeaders().put("Location", errorUrl);
                return;
            }

        }

        if (logger.isDebugEnabled())
            logger.debug("No configured error URL. Generating error page.");

        httpOut.getHeaders().put("http.responseCode", 200);
        Html htmlErr = this.createHtmlErrorPage(fault.getMessage());
        try {
            String htmlStr = this.marshal(htmlErr, "http://www.w3.org/1999/xhtml", "html",
                    new String[]{"org.w3._1999.xhtml"});

            ByteArrayInputStream baos = new ByteArrayInputStream(htmlStr.getBytes());
            httpOut.setBody(baos);

        } catch (Exception e) {
            logger.error("Cannot generate error page : " + e.getMessage(), e);
            httpOut.setBody("<html><body>Unhandled IDBus Error, verify log files for details!</body></html>");
        }


    }

    protected void copyBackState(MediationState state, Exchange exchange) {
        if (state == null) {
            logger.warn("No state received ...!");
            return;
        }

        // Loca Variables are supported by Provider State Manage or HTTP Session

        FederatedLocalProvider p = null;
        if (channel instanceof FederationChannel) {
            FederationChannel fc = (FederationChannel) channel;
            p = fc.getProvider();
        } else if (channel instanceof BindingChannel) {
            BindingChannel bc = (BindingChannel) channel;
            p = bc.getProvider();
        } else if (channel instanceof ClaimChannel) {
            ClaimChannel cc = (ClaimChannel) channel;
            p = cc.getProvider();
        }

        if (p != null) {

            String localStateVarName = p.getName().toUpperCase() + "_STATE";

            if (logger.isDebugEnabled())
                logger.debug("Using Provider State manager to store local state (" + p.getName() + "). Channel (" + channel.getName() + ")");

            LocalState pState = state.getLocalState();
            String stateId = (String) exchange.getIn().getHeader("org.atricore.idbus.http.Cookie." + localStateVarName);
            if (stateId == null || !stateId.equals(pState.getId())) {

                if (logger.isDebugEnabled())
                    logger.debug("Updating state id " + stateId + " with new id " + pState.getId() + " (" + p.getName() + "). Channel (" + channel.getName() + ")");

                state.setRemoteVariable(localStateVarName, pState.getId());
            }

            // Store this state
            ProviderStateContext ctx = createProviderStateContext();
            ctx.getStateManager().store(ctx, pState);

        } else {

            if (logger.isDebugEnabled())
                logger.debug("Using local HTTP session to store local state. Channel (" + channel.getName() + ")");

        }

        // Remove Variables are supported by cookies in HTTP
        for (String name : state.getRemoteVarNames()) {

            String value = (String) exchange.getIn().getHeader("org.atricore.idbus.http.Cookie." + name);

            if (value == null || !value.equals(state.getRemoteVariable(name))) {
                // Set the cookie because value is not present or is different than what is store locally.
                exchange.getOut().getHeaders().put("org.atricore.idbus.http.Set-Cookie." + name,
                        state.getRemoteVariable(name) + ";Path=/");

            }


        }

    }

    protected MediationState createMediationState(Exchange exchange) {

        if (logger.isDebugEnabled())
            logger.debug("Creating Mediation State from Exchange " + exchange.getExchangeId());

        FederatedLocalProvider p = getProvider();
        MediationStateImpl state = null;
        if (p != null) {

            if (logger.isDebugEnabled())
                logger.debug("Using Provider State manager to store local state (" + p.getName() + "). Channel (" + channel.getName() + ")");

            String localStateVarName = p.getName().toUpperCase() + "_STATE";
            String localStateId = (String) exchange.getIn().getHeader("org.atricore.idbus.http.Cookie." + localStateVarName);

            if (logger.isDebugEnabled())
                logger.debug("Using Provider State ID " + localStateId + " from cookie " + localStateVarName);

            LocalState lState = null;

            ProviderStateContext ctx = createProviderStateContext();

            if (localStateId == null) {

                if (logger.isDebugEnabled())
                    logger.debug("Created new local state for provider " + p.getName() + ". Channel (" + channel.getName() + ") " + localStateId);

                lState = ctx.createState();
                localStateId = lState.getId();

            } else {

                int retryCount = getRetryCount();
                if (retryCount > 0)
                    lState = ctx.retrieve(localStateId, retryCount, getRetryDelay());
                else
                    lState = ctx.retrieve(localStateId);


                if (lState == null) {

                    lState = ctx.createState();
                    localStateId = lState.getId();

                    if (logger.isDebugEnabled())
                        logger.debug("Created new local state for provider " + p.getName() + ". Channel (" + channel.getName() + ") " + localStateId);

                } else {

                    if (logger.isDebugEnabled())
                        logger.debug("Found local state for provider " + p.getName() + ". Channel (" + channel.getName() + ") " + lState.getId());
                }
            }

            state = new MediationStateImpl(lState);

        } else {

            if (logger.isDebugEnabled())
                logger.debug("Using local HTTP session to store local state. Channel (" + channel.getName() + ")");

            HttpSession session = (HttpSession) exchange.getIn().getHeaders().get("org.atricore.idbus.http.HttpSession");
            LocalState lState = new HttpLocalState(session);
            state = new MediationStateImpl(lState);
        }

        // Cookies as remote vars
        java.util.Map<String, Object> headers = exchange.getIn().getHeaders();
        for (String headerName : headers.keySet()) {

            if (headerName.startsWith("org.atricore.idbus.http.Cookie.")) {
                String varName = headerName.substring("org.atricore.idbus.http.Cookie.".length());
                String varValue = exchange.getIn().getHeader(headerName, String.class);

                logger.debug("Remote Variable from HTTP Cookie (" + headerName + ") " + varName + "=" + varValue);
                state.getRemoteVars().put(varName, varValue);
            }

        }

        // Parameters as transient variables
        try {

            java.util.Map<String, String> params = getParameters(exchange.getIn().getHeader("org.apache.camel.component.http.query", String.class));

            if (exchange.getIn().getHeader("http.requestMethod").equals("POST"))
                params.putAll(getParameters((InputStream) exchange.getIn().getBody()));

            state.getTransientVars().putAll(params);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return state;

    }

    public String buildHttpTargetLocation(Object httpData, EndpointDescriptor ed, boolean isResponse) {
        return (isResponse && ed.getResponseLocation() != null ? ed.getResponseLocation() : ed.getLocation());
    }

    public String buildHttpTargetLocation(Object httpData, EndpointDescriptor ed) {
        return buildHttpTargetLocation(httpData, ed, false);
    }

    // -------------------------------------------------------------
    // HTML Utils
    // -------------------------------------------------------------
    protected Html createHtmlRedirectMessage(String location) throws Exception {

        Html html = createHtmlBaseMessage();
        Body body = html.getBody();

        Div pageDiv = (Div) body.getPOrH1OrH2().iterator().next();

        Script loadJs = new Script();
        loadJs.setType("text/javascript");
        loadJs.setContent(
                        "\n         Event.observe(window, 'load', function() {\n" +
                        "                    window.location.href='" + location + "';\n" +
                        "            });");


        pageDiv.getContent().add(loadJs);

        return html;

    }

    protected Html createHtmlArtifactMessage(String location) throws Exception {
        Html html = createHtmlBaseMessage();
        Body body = html.getBody();

        Div pageDiv = (Div) body.getPOrH1OrH2().iterator().next();

        Script redirectJs = new Script();
        redirectJs.setType("text/javascript");
        redirectJs.setContent(
                "\n         Event.observe(window, 'load', function() {\n" +
                "                    window.location.href='" + location + "';\n" +
                "            });");

        pageDiv.getContent().add(redirectJs);

        return html;
    }


    protected Html createHtmlPostMessage(String url,
                                         String relayState,
                                         String msgName,
                                         String msgValue) throws Exception {


        Html html = createHtmlBaseMessage();
        Body body = html.getBody();


        // Non-Ajax form
        Div pageDiv = (Div) body.getPOrH1OrH2().iterator().next();
        Form form = new Form();

        form.setMethod("post");
        form.setAction(url);
        form.setId("postbinding");
        form.setEnctype("application/x-www-form-urlencoded");

        {
            // Noscript paragraph

            P paragraph = new P();
            paragraph.setTitle("Note: Since your browser does not support JavaScript, you must press the Continue button once to proceed.");  // TODO : i18n
            Noscript noscript = new Noscript();
            noscript.getPOrH1OrH2().add(paragraph);
            body.getPOrH1OrH2().add(noscript);
        }

        {
            // Div with form fields
            Div divFields = new Div();
            if (relayState != null) {
                Input input1 = new Input();
                input1.setType(InputType.HIDDEN);
                input1.setName("RelayState");
                input1.setValue(relayState);

                divFields.getContent().add(input1);
            }

            Input input2 = new Input();
            input2.setType(InputType.HIDDEN);
            input2.setName(msgName);

            input2.setValue(msgValue);

            divFields.getContent().add(input2);

            // Add first filds to form
            form.getPOrH1OrH2().add(divFields);
        }


        {
            // Create noscript submit button
            Noscript noscript = new Noscript();
            Div divNoScript = new Div();
            noscript.getPOrH1OrH2().add(divNoScript);

            Input submit = new Input();
            submit.setType(InputType.SUBMIT);
            submit.setValue("Continue");
            divNoScript.getContent().add(submit);

            form.getPOrH1OrH2().add(noscript);

        }

        // Part of post binding
        body.setOnload("document.forms.postbinding.submit();");

        pageDiv.getContent().add(form);

        return html;
    }

    protected Html createHtmlAjaxPostMessage(String url,
                                         String relayState,
                                         String msgName,
                                         String msgValue) throws Exception {


        Html html = createHtmlBaseMessage();
        Body body = html.getBody();

        // Ajax Form
        Form form = new Form();

        form.setMethod("post");
        form.setAction(url);
        form.setId("postbinding");
        form.setEnctype("application/x-www-form-urlencoded");

        Div fields = new Div();

        if (relayState != null) {
            Input input1 = new Input();
            input1.setType(InputType.HIDDEN);
            input1.setName("RelayState");
            input1.setValue(relayState);

            fields.getContent().add(input1);
        }

        Input input2 = new Input();
        input2.setType(InputType.HIDDEN);
        input2.setName(msgName);

        input2.setValue(msgValue);

        fields.getContent().add(input2);

        form.getPOrH1OrH2().add(fields);

        Div pageDiv = (Div) body.getPOrH1OrH2().iterator().next();
        Div div = (Div) pageDiv.getContent().iterator().next();
        Div divFixedFull = (Div) div.getContent().iterator().next();
        Div waitContainer = (Div) divFixedFull.getContent().iterator().next();

        Script submitJs = new Script();
        submitJs.setType("text/javascript");
        submitJs.setContent("              Event.observe(window, 'load', \n" +
                "                                function() {\n" +
                "                                    document.forms.postbinding.submit(); \n" +
                "                                }\n" +
                "                        );");

        waitContainer.getContent().add(submitJs);

        for (Object c : waitContainer.getContent()) {
            if (c instanceof Div) {
                Div waitBox = (Div) c;
                waitBox.getContent().add(form);

            }
        }

        return html;
    }

    protected Html createHtmlBaseMessage() {

        Html html = new Html();
        html.setLang("en");


        Head head = new Head();

        // Title :
        Title title = new Title();
        title.setContent("JOSSO 2 - Processing ..."); // TODO : i18n
        head.getContent().add(title);

        if (isEnableAjax()) {

            // Meta :
            Meta m1 = new Meta();
            m1.setHttpEquiv("X-UA-Compatible");
            m1.setContent("IE=EmulateIE7,chrome=1");
            head.getContent().add(m1);


            // Style:
            Link processingCss = new Link();
            processingCss.setHref("/idbus-ui/resources/css/processing.css");
            processingCss.getRel().add("stylesheet");
            processingCss.setType("text/css");
            //processingCss.setMedia("screen, projector");
            head.getContent().add(processingCss);

            Script processingJs = new Script();
            processingJs.setSrc("/idbus-ui/resources/script/prototype.js");
            processingJs.setType("text/javascript");
            processingJs.setContent(" ");

            head.getContent().add(processingJs);
        }

        html.setHead(head);

        Body body = new Body();

        Div pageDiv = new Div();
        pageDiv.setId("page");

        if (isEnableAjax()) {
            // To brand this, we need to replace images in IDBUS UI Bundle , can be improved though.

            Div div = new Div();
            {

                Div divFixedFull = new Div();
                divFixedFull.getClazz().add("fixed full");
                {
                    Div waitContainer = new Div();
                    waitContainer.setId("waitContainer");
                    waitContainer.getClazz().add("clearit");
                    {
                        Img atcLogo = new Img();
                        atcLogo.setId("waitLogo");
                        atcLogo.setSrc("/idbus-ui/resources/img/content/atricore-logo-2.png");

                        Div waitBox = new Div();
                        waitBox.setId("waitBox");
                        {

                            Img spinner = new Img();
                            spinner.setSrc("/idbus-ui/resources/img/processing-04.gif");
                            spinner.setAlt("Wait ...");

                            H1 h1 = new H1();
                            h1.getContent().add(spinner);
                            h1.getContent().add("Processing");

                            P processingP = new P();
                            Br br = new Br();
                            processingP.getContent().add(br);
                            processingP.getContent().add("You'll get redirected shortly, please wait ..."); // TODO : i18n
                            processingP.getContent().add(br);
                            processingP.getContent().add(br);

                            waitBox.getContent().add(h1);
                            waitBox.getContent().add(processingP);

                            // TODO : Add form here

                        }
                        waitContainer.getContent().add(atcLogo);
                        waitContainer.getContent().add(waitBox);

                    }
                    divFixedFull.getContent().add(waitContainer);
                }
                div.getContent().add(divFixedFull);
            }
            pageDiv.getContent().add(div);
        }


        body.getPOrH1OrH2().add(pageDiv);
        html.setBody(body);

        return html;

    }

    protected Html createHtmlErrorPage(MediationMessage fault) {

        Exception error = fault.getFault();

        Html html = new Html();
        html.setLang("en");

        Head head = new Head();
        html.setHead(head);
        {
            Title t = new Title();
            t.setContent("JOSSO 2 - Error"); // TODO : i18n
            head.getContent().add(t);

        }

        // Body
        Body body = new Body();

        {
            // Main error
            P paragraph = new P();
            H1 h1 = new H1();
            h1.getContent().add("Error while processing your request " + (error != null ? error.getMessage() : "UNKNOWN ERROR"));
            body.getPOrH1OrH2().add(h1);
        }

        {

            H3 h3 = new H3();
            h3.getContent().add(fault.getFaultDetails());
            body.getPOrH1OrH2().add(h3);

        }

        {
            // Stack Trace

            if (error != null && logger.isDebugEnabled()) {

                P paragraph = new P();
                paragraph.getContent().add("Error Debug Information:");
                paragraph.getContent().add(new Br());
                paragraph.getContent().add(new Br());

                // Dump errors
                Throwable t = error;

                while (t != null) {

                    Writer rootWriter = new StringWriter();
                    PrintWriter rootPrintWriter = new PrintWriter(rootWriter);

                    paragraph.getContent().add(new Br());
                    paragraph.getContent().add(new Br());
                    paragraph.getContent().add("Caused By:");
                    paragraph.getContent().add(new Br());
                    paragraph.getContent().add(new Br());

                    t.printStackTrace(rootPrintWriter);
                    paragraph.getContent().add(rootWriter.toString());
                    paragraph.getContent().add(new Br());

                    t = t.getCause();
                }

                body.getPOrH1OrH2().add(paragraph);
            }

        }

        {
            // Footer
            P paragraph = new P();
            body.getPOrH1OrH2().add(paragraph);
        }


        html.setBody(body);

        return html;
    }

    protected java.util.Map<String, String> getParameters(InputStream httpMsgBody) throws IOException {

        if (httpMsgBody == null) {
            return new HashMap<String, String>();
        }

        // Parse HTTP MSG BODY
        byte[] buf = new byte[2048];

        int read = httpMsgBody.read(buf, 0, 2048);
        StringBuffer httpBody = new StringBuffer(2048);
        while (read > 0) {
            httpBody.append(new String(buf, 0, read));
            read = httpMsgBody.read(buf, 0, 2048);
        }

        return getParameters(httpBody.toString());
    }

    protected java.util.Map<String, String> getParameters(String httpBody) throws IOException {

        java.util.Map<String, String> params = new HashMap<String, String>();
        if (httpBody == null)
            return params;

        StringTokenizer st = new StringTokenizer(httpBody, "&");
        while (st.hasMoreTokens()) {
            String param = st.nextToken();
            int pos = param.indexOf('=');
            String key = URLDecoder.decode(param.substring(0, pos), "UTF-8"); // TODO : Can encoding be modified?
            String value = URLDecoder.decode(param.substring(pos + 1), "UTF-8");

            if (logger.isDebugEnabled()) {
                logger.debug("HTTP Parameter " + key + "=[" + value + "]");
            }
            params.put(key, value);
        }

        return params;
    }

    protected String marshal(Object obj, String msgQName, String msgLocalName, String[] userPackages) throws Exception {

        JAXBContext jaxbContext = createJAXBContext(obj, userPackages);
        JAXBElement jaxbRequest = new JAXBElement(new QName(msgQName, msgLocalName),
                obj.getClass(),
                obj
        );
        Writer writer = new StringWriter();

        // Support XMLDsig
        jaxbContext.createMarshaller().marshal(jaxbRequest, writer);

        return writer.toString();
    }

    protected JAXBContext createJAXBContext(Object obj, String[] userPackages) throws JAXBException {
        StringBuilder packages = new StringBuilder();
        for (String userPackage : userPackages) {
            packages.append(userPackage).append(":");
        }
        // Use our classloader to build JAXBContext so it can find binding classes.
        return JAXBContext.newInstance(packages.toString(), obj.getClass().getClassLoader());
    }

    protected boolean isEnableAjax() {
        if (getConfigurationContext() == null) {
            logger.warn("No Configuration context find in binding " + getBinding());
            return false;
        }

        return Boolean.parseBoolean(getConfigurationContext().getProperty("binding.http.ajax"));
    }

    protected int getRetryCount() {
        if (getConfigurationContext() == null) {
            logger.warn("No Configuration context find in binding " + getBinding());
            return -1;
        }

        String retryCountStr = getConfigurationContext().getProperty("binding.http.loadStateRetryCount");
        if (retryCountStr == null)
            return -1;

        int retryCount = Integer.parseInt(retryCountStr);
        if (retryCount < 1) {
            logger.warn("Configuratio property 'binding.http.loadStateRetryCount' cannot be " + retryCount);
            retryCount = 3;
        }

        return retryCount;
    }

    protected long getRetryDelay() {
        if (getConfigurationContext() == null) {
            logger.warn("No Configuration context find in binding " + getBinding());
            return -1;
        }

        String retryDelayStr = getConfigurationContext().getProperty("binding.http.loadStateRetryDelay");
        if (retryDelayStr == null)
            return -1;

        long retryDelay = Long.parseLong(retryDelayStr);
        if (retryDelay < 0) {
            logger.warn("Configuratio property 'binding.http.loadStateRetryDelay' cannot be " + retryDelay);
            retryDelay = 100;
        }

        return retryDelay;

    }

}


