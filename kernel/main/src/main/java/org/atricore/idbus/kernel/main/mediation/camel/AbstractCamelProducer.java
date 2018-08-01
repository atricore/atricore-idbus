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
import org.apache.camel.StringSource;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.spi.Registry;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationFault;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;

import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.camel.logging.MediationLogger;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.springframework.context.ApplicationContext;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
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
public abstract class AbstractCamelProducer<E extends org.apache.camel.Exchange> extends DefaultProducer {

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

    public void process ( final Exchange exchange) throws Exception {

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

                if (exchange.getOut().isFault())
                    logger.logFault(exchange.getOut());

                logger.logOutgoing(exchange.getOut());
            }

        } catch (IdentityMediationFault err) {

            logger.error(err.getMessage(), err);
            
            String errorMsg = "[" + channel.getName() + "@" + channel.getLocation() + "] " +
                    getClass().getSimpleName() + ":'" + err.getMessage() + "'";

            if (logger.isDebugEnabled())
                logger.debug("Generating Fault message for " + errorMsg, err);

            CamelMediationMessage fault = exchange.getOut().isFault() ? (CamelMediationMessage) exchange.getOut() : null;
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

            CamelMediationMessage fault = exchange.getOut().isFault() ? (CamelMediationMessage) exchange.getOut() : null;
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

    protected Cookie getCookie ( Exchange he, String cookieName ) {

        HttpServletRequest hreq = he.getIn().getHeader(Exchange.HTTP_SERVLET_REQUEST, HttpServletRequest.class);

        Cookie[] cookies = hreq.getCookies();
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

    protected HashMap<String, String> getParameters ( Exchange he ) throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();

        HttpServletRequest hreq = he.getIn().getHeader(Exchange.HTTP_SERVLET_REQUEST, HttpServletRequest.class);

        if ( hreq.getMethod().equalsIgnoreCase( "post" ) )
            parameters = parseHttpPost( (InputStream) he.getIn().getBody() );

        if ( hreq.getQueryString() != null ) {
            String[] params = hreq.getQueryString().split( "\\&" );
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


    public boolean isMobile(String userAgent) {

        if(userAgent.matches("(?i).*((android|bb\\d+|meego).+mobile|avantgo|bada\\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\\.(browser|link)|vodafone|wap|windows ce|xda|xiino).*")|| userAgent.substring(0, 4).matches("(?i)1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\\-(n|u)|c55\\/|capi|ccwa|cdm\\-|cell|chtm|cldc|cmd\\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\\-s|devi|dica|dmob|do(c|p)o|ds(12|\\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\\-|_)|g1 u|g560|gene|gf\\-5|g\\-mo|go(\\.w|od)|gr(ad|un)|haie|hcit|hd\\-(m|p|t)|hei\\-|hi(pt|ta)|hp( i|ip)|hs\\-c|ht(c(\\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\\-(20|go|ma)|i230|iac( |\\-|\\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\\/)|klon|kpt |kwc\\-|kyo(c|k)|le(no|xi)|lg( g|\\/(k|l|u)|50|54|\\-[a-w])|libw|lynx|m1\\-w|m3ga|m50\\/|ma(te|ui|xo)|mc(01|21|ca)|m\\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\\-2|po(ck|rt|se)|prox|psio|pt\\-g|qa\\-a|qc(07|12|21|32|60|\\-[2-7]|i\\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\\-|oo|p\\-)|sdk\\/|se(c(\\-|0|1)|47|mc|nd|ri)|sgh\\-|shar|sie(\\-|m)|sk\\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\\-|v\\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\\-|tdg\\-|tel(i|m)|tim\\-|t\\-mo|to(pl|sh)|ts(70|m\\-|m3|m5)|tx\\-9|up(\\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\\-|your|zeto|zte\\-")) {
            if (logger.isDebugEnabled())
                logger.debug("UA["+userAgent+"] is  mobile");

            return true;
        }
        if (logger.isDebugEnabled())
            logger.debug("UA[" + userAgent + "] is NOT mobile");

        return false;


    }
}
