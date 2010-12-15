package org.atricore.idbus.capabilities.spmlr2.main.client;

import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.async.CancelRequestType;
import oasis.names.tc.spml._2._0.async.CancelResponseType;
import oasis.names.tc.spml._2._0.async.StatusRequestType;
import oasis.names.tc.spml._2._0.async.StatusResponseType;
import oasis.names.tc.spml._2._0.batch.BatchRequestType;
import oasis.names.tc.spml._2._0.batch.BatchResponseType;
import oasis.names.tc.spml._2._0.bulk.BulkModifyRequestType;
import oasis.names.tc.spml._2._0.password.*;
import oasis.names.tc.spml._2._0.search.CloseIteratorRequestType;
import oasis.names.tc.spml._2._0.search.SearchRequestType;
import oasis.names.tc.spml._2._0.search.SearchResponseType;
import oasis.names.tc.spml._2._0.suspend.ActiveRequestType;
import oasis.names.tc.spml._2._0.suspend.ActiveResponseType;
import oasis.names.tc.spml._2._0.suspend.ResumeRequestType;
import oasis.names.tc.spml._2._0.suspend.SuspendRequestType;
import oasis.names.tc.spml._2._0.updates.IterateRequestType;
import oasis.names.tc.spml._2._0.updates.UpdatesRequestType;
import oasis.names.tc.spml._2._0.updates.UpdatesResponseType;
import oasis.names.tc.spml._2._0.wsdl.SPMLRequestPortType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.SpmlR2Client;
import org.atricore.idbus.capabilities.spmlr2.main.psp.SpmlR2PSPMediator;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;
import org.springframework.beans.factory.InitializingBean;

import javax.jws.WebParam;
import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SpmlR2MediationClientImpl implements SpmlR2Client, InitializingBean {

    private static final Log logger = LogFactory.getLog(SpmlR2MediationClientImpl.class);

    private String serviceType;
    
    private String binding;

    // TODO : Try to avoid direct refernce to PSP and maybe mediator.
    private ProvisioningServiceProvider psp;
    
    private SpmlR2PSPMediator mediator;

    public void afterPropertiesSet() throws Exception {
        this.mediator = (SpmlR2PSPMediator) psp.getChannel().getIdentityMediator();
    }

    public String getPSProviderName() {
        return psp.getName();
    }

    public boolean hasTarget(String psTargetName) {
        for (ProvisioningTarget pt : psp.getProvisioningTargets()) {
            if (pt.getName().equals(psTargetName))
                return true;
        }
        return false;
    }

    public ActiveResponseType spmlActiveRequest(ActiveRequestType request) {
        try {
            return (ActiveResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public UpdatesResponseType spmlUpdatesIterateRequest(IterateRequestType request) {
        try {
            return (UpdatesResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseType spmlSetPasswordRequest(SetPasswordRequestType request) {
        try {
            return (ResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public ResetPasswordResponseType spmlResetPasswordRequest(ResetPasswordRequestType request) {
        try {
            return (ResetPasswordResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public BatchResponseType spmlBatchRequest(BatchRequestType request) {
        try {
            return (BatchResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public CancelResponseType spmlCancelRequest(CancelRequestType request) {
        try {
            return (CancelResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseType spmlExpirePasswordRequest(ExpirePasswordRequestType request) {
        try {
            return (ResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public AddResponseType spmlAddRequest(AddRequestType request) {
        try {
            return (AddResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public SearchResponseType spmlSearchRequest(SearchRequestType request) {
        try {
            return (SearchResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseType spmlSuspendRequest(SuspendRequestType request) {
        try {
            return (ResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseType spmlDeleteRequest(DeleteRequestType request) {
        try {
            return (ResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public SearchResponseType spmlSearchIterateRequest(oasis.names.tc.spml._2._0.search.IterateRequestType request) {
        try {
            return (SearchResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseType spmlSearchCloseIteratorRequest(CloseIteratorRequestType request) {
        try {
            return (ResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public ListTargetsResponseType spmlListTargetsRequest(ListTargetsRequestType request) {
        try {
            return (ListTargetsResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public LookupResponseType spmlLookupRequest(LookupRequestType request) {
        try {
            return (LookupResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public UpdatesResponseType spmlUpdatesRequest(UpdatesRequestType request) {
        try {
            return (UpdatesResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseType spmlBulkModifyRequest(BulkModifyRequestType request) {
        try {
            return (ResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public StatusResponseType spmlStatusRequest(StatusRequestType request) {
        try {
            return (StatusResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseType spmlUpdatesCloseIteratorRequest(oasis.names.tc.spml._2._0.updates.CloseIteratorRequestType request) {
        try {
            return (ResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseType spmlResumeRequest(ResumeRequestType request) {
        try {
            return (ResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public ValidatePasswordResponseType spmlValidatePasswordRequest(ValidatePasswordRequestType request) {
        try {
            return (ValidatePasswordResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    public ModifyResponseType spmlModifyRequest(ModifyRequestType request) {
        try {
            return (ModifyResponseType) mediator.sendMessage(request, doMakeDestination(request), psp.getChannel());
        } catch (IdentityMediationException e) {
            throw new RuntimeException(e);
        }
    }

    protected EndpointDescriptor doMakeDestination(RequestType request) throws IdentityMediationException {

        Channel c = psp.getChannel();

        for (IdentityMediationEndpoint ie : c.getEndpoints()) {

            if (ie.getType().equals(serviceType)) {
                if (ie.getBinding().equals(binding)) {
                    return c.getIdentityMediator().resolveEndpoint(c, ie);
                }
            }
        }
        logger.error("Cannot resolve endpoint for " + serviceType + "/" + binding);
        return null;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public SpmlR2PSPMediator getMediator() {
        return mediator;
    }

    public void setMediator(SpmlR2PSPMediator mediator) {
        this.mediator = mediator;
    }

    public ProvisioningServiceProvider getPsp() {
        return psp;
    }

    public void setPsp(ProvisioningServiceProvider psp) {
        this.psp = psp;
    }
}
