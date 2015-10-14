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

package org.atricore.idbus.kernel.main.mediation.camel;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.component.http.HttpExchange;
import org.apache.camel.converter.jaxp.StringSource;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.spi.Registry;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationFault;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.logging.MediationLogger;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.springframework.context.ApplicationContext;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.servlet.http.Cookie;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;


/**
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: SAMLR2WebSSOProducer.java 1170 2009-04-29 15:03:21Z ajadzinsky $
 */
public abstract class AbstractCamelProducer<E extends org.apache.camel.Exchange> extends DefaultProducer<E> {

    private static final Log logger = LogFactory.getLog( AbstractCamelProducer.class );

    protected String channelRef;
    protected Channel channel;
    protected String endpointRef;
    protected IdentityMediationEndpoint endpoint;
    protected boolean isResponse = false;

    protected String action;
    protected Registry registry;
    protected ApplicationContext applicationContext;

    public AbstractCamelProducer ( Endpoint endpoint ) {
        super( endpoint );
        assert endpoint != null : "Endpoint MUST be spedified when creating producers!";
    }

    public void process ( final Exchange e) throws Exception {

        CamelMediationExchange exchange = (CamelMediationExchange) e;

        channelRef = ( (AbstractCamelEndpoint) getEndpoint() ).getChannelRef();
        endpointRef = ( (AbstractCamelEndpoint) getEndpoint() ).getEndpointRef();
        action = ( (AbstractCamelEndpoint) getEndpoint() ).getAction();
        isResponse = ( (AbstractCamelEndpoint) getEndpoint() ).isResponse();

        if (logger.isDebugEnabled())
            logger.debug( "Processing Message Exchange [" + exchange +
                "] Channel Reference [" + channelRef + "] " +
                "] Endpoint Reference [" + endpointRef + "] " +
                "] Channel Action [" + action + "] " +
                "] Is Response [" + isResponse + "] " +
                "] Producer [" + getClass().getSimpleName()+ "] "
             );

        registry = exchange.getContext().getRegistry();
        applicationContext = registry.lookup( "applicationContext", ApplicationContext.class );

        if (channelRef == null)
            throw new IdentityMediationException("Channel reference cannot be null, check your camel route definition!");

        channel = (Channel) applicationContext.getBean( channelRef );

        if (channel == null)
            throw new IdentityMediationException("Cannot resovle channel reference ["+channelRef+"], check your configuration!");

        if (endpointRef == null)
            throw new IdentityMediationException("Endpoint reference cannot be null, check your camel route definition!");

        for (IdentityMediationEndpoint endpoint : channel.getEndpoints()) {
            if (endpoint.getName().equals(endpointRef)) {
                this.endpoint = endpoint;
                break;
            }
        }

        if (this.endpoint == null)
            throw new IdentityMediationException("Cannot resolve endpoint reference ["+endpointRef+"] for channel ["+channelRef+"]");

        if (logger.isDebugEnabled())
            logger.debug( "Processing Message Exchange [" + exchange +
                "] Channel Object [" + channel + "] " +
                "] Endpoint Object [" + endpoint + "] "                    
            );

        try {

            if (isResponse) {
                doProcessResponse((E) exchange);
            } else {
                doProcess((E) exchange);
            }

            // TODO : This could be in a better place
            MediationLogger logger = channel.getIdentityMediator().getLogger();
            if (logger != null && channel.getIdentityMediator().isLogMessages()) {
                if (exchange.getFault(false) != null)
                    logger.logFault(exchange.getFault(false));
                logger.logOutgoing(exchange.getOut());
            }

        } catch (IdentityMediationFault err) {

            logger.error(err.getMessage(), err);
            
            String errorMsg = "[" + channel.getName() + "@" + channel.getLocation() + "] " +
                    getClass().getSimpleName() + ":'" + err.getMessage() + "'";

            if (logger.isDebugEnabled())
                logger.debug("Generating Fault message for " + errorMsg, err);

            CamelMediationMessage fault = (CamelMediationMessage) exchange.getFault();
            fault.setBody(new MediationMessageImpl(fault.getMessageId(), errorMsg, err));

        } catch (Exception err) {

            logger.error(err.getMessage(), err);

            IdentityMediationFault f = null;
            Throwable cause = err.getCause();
            while (cause != null) {
                if (cause instanceof IdentityMediationFault) {
                    f = (IdentityMediationFault) cause;
                    break;
                }
                cause = cause.getCause();
            }
            if (f == null) {
                f = new IdentityMediationFault("urn:org:atricore:idbus:error:fatal",
                        null, null, err.getMessage(), err);
            }

            if (logger.isDebugEnabled())
                logger.debug("Generating Fault message for " + f.getMessage(), f);

            CamelMediationMessage fault = (CamelMediationMessage) exchange.getFault();
            fault.setBody(new MediationMessageImpl(fault.getMessageId(), f.getMessage(), f));
            
        }
    }

    /**
     * Process an exchange received on a response location
     */
    protected void doProcessResponse ( E exchange ) throws Exception {
        // By default, trigger doProcess
        doProcess(exchange);
    }

    /**
     * Process an exchange received on a location
     */
    protected abstract void doProcess ( E exchange ) throws Exception;

    protected Cookie getCookie ( HttpExchange he, String cookieName ) {
        Cookie[] cookies = he.getRequest().getCookies();
        if ( cookies == null )
            return null;

        for ( Cookie cookie : cookies ) {
            if ( cookie.getName().equals( cookieName ) ) {
                return cookie;
            }
        }
        return null;
    }

    @Deprecated
    protected String marshal ( Class endpointInterface, Object msg, String msgQName, String msgLocalName, String[] userPackages ) throws Exception {
        WebService ws = getWebServiceAnnotation( endpointInterface );
        JAXBContext jaxbContext = createJAXBContext( endpointInterface, userPackages );
        JAXBElement jaxbRequest = new JAXBElement( new QName( msgQName, msgLocalName ),
                msg.getClass(),
                msg
        );
        StringWriter writer = new StringWriter();
        jaxbContext.createMarshaller().marshal( jaxbRequest, writer );

        return writer.toString();
    }

    @Deprecated
    protected String marshal ( Object msg, String msgQName, String msgLocalName, String[] userPackages ) throws Exception {
        JAXBContext jaxbContext = createJAXBContext( userPackages );
        JAXBElement jaxbRequest = new JAXBElement( new QName( msgQName, msgLocalName ),
                msg.getClass(),
                msg
        );
        StringWriter writer = new StringWriter();
        jaxbContext.createMarshaller().marshal( jaxbRequest, writer );

        return writer.toString();
    }

    @Deprecated
    protected Object unmarshal ( String msg, Class endpointInterface, String userPackages[] ) throws Exception {
        JAXBContext jaxbContext = createJAXBContext( endpointInterface, userPackages );
        return jaxbContext.createUnmarshaller().unmarshal( new StringSource( msg ) );
    }

    @Deprecated
    protected Object unmarshal ( String msg, String userPackages[] ) throws Exception {
        JAXBContext jaxbContext = createJAXBContext( userPackages );
        return jaxbContext.createUnmarshaller().unmarshal( new StringSource( msg ) );
    }

    protected JAXBContext createJAXBContext ( Class interfaceClass, String[] userPackages ) throws JAXBException {
        StringBuilder packages = new StringBuilder();

        for ( String userPackage : userPackages ) {
            packages.append( userPackage ).append( ":" );
        }

        //classes.add(JbiFault.class);
        for ( Method mth : interfaceClass.getMethods() ) {
            WebMethod wm = (WebMethod) mth.getAnnotation( WebMethod.class );
            if ( wm != null ) {
                packages.append( getPackages( mth.getParameterTypes() ) );
                packages.append( ":" );
            }
        }

        return JAXBContext.newInstance( packages.toString() );
    }

    protected JAXBContext createJAXBContext ( String[] userPackages ) throws JAXBException {
        StringBuilder packages = new StringBuilder();

        for ( String userPackage : userPackages ) {
            packages.append( userPackage ).append( ":" );
        }

        return JAXBContext.newInstance( packages.toString() );
    }


    @SuppressWarnings("unchecked")
    protected WebService getWebServiceAnnotation ( Class clazz ) {
        for ( Class cl = clazz; cl != null; cl = cl.getSuperclass() ) {
            WebService ws = (WebService) cl.getAnnotation( WebService.class );
            if ( ws != null ) {
                return ws;
            }
        }
        return null;
    }

    protected HashMap<String, String> parseHttpPost ( InputStream is ) throws Exception {

        String postSubmission;

        postSubmission = IOUtils.toString( is );
        HashMap<String, String> parameters = new HashMap<String, String>();
        String[] pairs = postSubmission.split( "\\&" );
        for ( String pair : pairs ) {
            String[] fields = pair.split( "=" );
            if ( fields.length == 2 ) {
                String name = URLDecoder.decode( fields[ 0 ], "UTF-8" );
                String value = URLDecoder.decode( fields[ 1 ], "UTF-8" );
                parameters.put( name, value );
            }
        }

        return parameters;
    }

    protected HashMap<String, String> getParameters ( HttpExchange he ) throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();

        if ( he.getRequest().getMethod().equalsIgnoreCase( "post" ) )
            parameters = parseHttpPost( (InputStream) he.getIn().getBody() );

        if ( he.getRequest().getQueryString() != null ) {
            String[] params = he.getRequest().getQueryString().split( "\\&" );
            for ( String param : params ) {
                String[] nameValue = param.split( "=" );
                String name = URLDecoder.decode( nameValue[ 0 ], "UTF-8" );
                String value = URLDecoder.decode( nameValue[ 1 ], "UTF-8" );
                parameters.put( name, value );
            }
        }

        return parameters;
    }

    private static String getPackages ( Class[] classes ) {
        StringBuilder pkgs = new StringBuilder();
        boolean first = true;

        for ( Class aClass : classes ) {
            if ( !first ) pkgs.append( ":" );
            else first = false;

            pkgs.append( getPackage( aClass.getName() ) );

        }

        return pkgs.toString();
    }

    private static String getPackage ( String pckg ) {
        int i = pckg.lastIndexOf( "." );
        if ( i != -1 )
            pckg = pckg.substring( 0, i );
        return pckg;
    }
}
