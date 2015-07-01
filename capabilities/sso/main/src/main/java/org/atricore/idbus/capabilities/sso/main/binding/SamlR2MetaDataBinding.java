package org.atricore.idbus.capabilities.sso.main.binding;

import oasis.names.tc.saml._2_0.idbus.MetadataRequestType;
import oasis.names.tc.saml._2_0.metadata.EntityDescriptorType;
import oasis.names.tc.saml._2_0.protocol.RequestAbstractType;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.capabilities.sso.support.core.util.XmlUtils;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.MediationMessage;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.AbstractMediationHttpBinding;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

import java.io.ByteArrayInputStream;

/**
 *
 */
public class SamlR2MetaDataBinding extends AbstractMediationHttpBinding {

    private static final Log logger = LogFactory.getLog(SamlR2HttpRedirectBinding.class);

    public SamlR2MetaDataBinding(Channel channel) {
        super(SSOBinding.SAMLR2_MD.getValue(), channel);
    }


    @Override
    public MediationMessage createMessage(CamelMediationMessage message) {
        // The nested exchange contains HTTP information
        Exchange exchange = message.getExchange().getExchange();
        logger.debug("Create Message Body from exchange " + exchange.getClass().getName());

        Message httpMsg = exchange.getIn();

        if (httpMsg.getHeader("http.requestMethod") == null ||
                !httpMsg.getHeader("http.requestMethod").equals("GET")) {
            throw new IllegalArgumentException("Unknown message, no valid HTTP Method header found!");
        }

        MetadataRequestType request = new MetadataRequestType();

        return new MediationMessageImpl<MetadataRequestType>(httpMsg.getMessageId(),
                request,
                null,
                null,
                null,
                null,
                null);
    }

    @Override
    public void copyMessageToExchange(CamelMediationMessage samlOut, Exchange exchange) {

        try {

            MediationMessage out = samlOut.getMessage();
            EndpointDescriptor ed = out.getDestination();

            Message httpOut = exchange.getOut();
            Message httpIn = exchange.getIn();

            // ------------------------------------------------------------
            // Prepare HTTP Resposne
            // ------------------------------------------------------------
            copyBackState(out.getState(), exchange);

            httpOut.getHeaders().put("Cache-Control", "no-cache, no-store");
            httpOut.getHeaders().put("Pragma", "no-cache");
            httpOut.getHeaders().put("http.responseCode", 200);
            httpOut.getHeaders().put("Content-Type", "text/xml");
            handleCrossOriginResourceSharing(exchange);

            //  Marshal MD and write HTTP out put
            MetadataEntry md = (MetadataEntry) out.getContent();
            EntityDescriptorType saml2Md = (EntityDescriptorType) md.getEntry();

            String xmlMd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + XmlUtils.masrhalSamlR2Metadata(saml2Md, false);

            ByteArrayInputStream baos = new ByteArrayInputStream(xmlMd.getBytes());
            httpOut.setBody(baos);


        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
