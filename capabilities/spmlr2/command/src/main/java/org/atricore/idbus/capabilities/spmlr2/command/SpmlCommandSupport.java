package org.atricore.idbus.capabilities.spmlr2.command;

import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.search.ScopeType;
import oasis.names.tc.spml._2._0.search.SearchQueryType;
import oasis.names.tc.spml._2._0.search.SearchRequestType;
import oasis.names.tc.spml._2._0.search.SearchResponseType;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.karaf.shell.console.OsgiCommandSupport;
import org.atricore.idbus.capabilities.spmlr2.command.printer.CmdPrinter;
import org.atricore.idbus.capabilities.spmlr2.main.SPMLR2Constants;
import org.atricore.idbus.capabilities.spmlr2.main.SpmlR2Service;
import org.atricore.idbus.capabilities.spmlr2.main.binding.SpmlR2Binding;
import org.atricore.idbus.capabilities.spmlr2.main.psp.SpmlR2PSPMediator;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnit;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;
import org.atricore.idbus.kernel.main.mediation.channel.PsPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.ProvisioningServiceProvider;
import org.atricore.idbus.kernel.main.provisioning.exception.GroupNotFoundException;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.osgi.framework.ServiceReference;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class SpmlCommandSupport extends OsgiCommandSupport {

    protected UUIDGenerator uuidGenerator = new UUIDGenerator();

    @Argument(index = 0, name = "idauId", description = "The id if the identity appliance", required = true)
    String idauId;

    @Argument(index = 1, name = "pspId", description = "The id if the Provisioning Service Provider", required = true)
    String pspId;

    @Argument(index = 2, name = "targetId", description = "Provisionig Service Target id", required = false)
    String targetId;

    @Option(name = "-v", aliases = "--verbose", description = "Verbose command", required = false, multiValued = false)
    boolean verbose = false;

    protected CmdPrinter cmdPrinter;

    public CmdPrinter getCmdPrinter() {
        return cmdPrinter;
    }

    public void setCmdPrinter(CmdPrinter cmdPrinter) {
        this.cmdPrinter = cmdPrinter;
    }

    @Override
    protected Object doExecute() throws Exception {

        // Get repository admin service.
        ServiceReference ref = getBundleContext().getServiceReference(IdentityMediationUnitRegistry.class.getName());
        if (ref == null) {
            cmdPrinter.printMsg("Identity Mediation Unit Registry Service is unavailable. (no service reference)");
            return null;
        }
        try {
            IdentityMediationUnitRegistry svc = (IdentityMediationUnitRegistry) getBundleContext().getService(ref);
            if (svc == null) {
                cmdPrinter.printMsg("Identity Mediation Unit Registry  Service service is unavailable. (no service)");
                return null;
            }

            IdentityMediationUnit idau = svc.lookupUnit(idauId);

            if (idau == null) {
                throw new Exception("IdAU not found " + idauId);
            }

            if (verbose)
                cmdPrinter.printMsg("IdAU " + idau.getName());

            PsPChannel pspChannel = null;
            ProvisioningServiceProvider psp = null;

            for (Channel c : idau.getChannels()) {

                if (c instanceof PsPChannel) {

                    PsPChannel pc = (PsPChannel) c;

                    if (pc.getProvider() != null && pc.getProvider().getName().equals(pspId)) {
                        pspChannel = pc;
                        psp = pc.getProvider();
                        break;

                    }
                }
            }

            if (pspChannel == null || psp == null) {
                throw new Exception("PSP not found " + pspId);
            }
            if (verbose)
                cmdPrinter.printMsg("PSP " + psp.getName());

            if (verbose)
                cmdPrinter.printMsg("PSP Channel " + pspChannel.getName());

            doExecute(psp, pspChannel);

        } finally {
            getBundleContext().ungetService(ref);
        }
        return null;

    }

    protected EndpointDescriptor resolvePsPEndpoint(PsPChannel pspChannel, SpmlR2Binding binding) {

        String b = binding.getValue();
        QName qName = SpmlR2Service.PSPService.getQname();

        for (IdentityMediationEndpoint endpoint : pspChannel.getEndpoints()) {
            if (endpoint.getBinding().equals(b)) {
                if (endpoint.getType().equals("{" + qName.getNamespaceURI() + "}" + qName.getLocalPart())) {

                    String location = endpoint.getLocation();
                    if (location.startsWith("/"))
                        location = pspChannel.getLocation() + location;

                    return new EndpointDescriptorImpl(endpoint.getName(),
                            endpoint.getType(), endpoint.getBinding(), location, null);
                }

            }
        }

        cmdPrinter.printErrMsg("No SPML PSP Endpoint found in channel " + pspChannel.getName());

        return null;
    }

    protected Object doExecute(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception {

        SpmlR2PSPMediator mediator = (SpmlR2PSPMediator) pspChannel.getIdentityMediator();

        EndpointDescriptor ed = resolvePsPEndpoint(pspChannel, SpmlR2Binding.SPMLR2_LOCAL);

        RequestType spmlRequest = buildSpmlRequest(psp, pspChannel);

        if (verbose)
            cmdPrinter.printMsg("SPML Endpoint " + ed.getLocation());

        Object o = mediator.sendMessage(spmlRequest, ed, pspChannel);

        if (o instanceof ResponseType) {
            ResponseType spmlResponse = (ResponseType) o;

            if (verbose)
                cmdPrinter.printRequest(spmlRequest);

            if (verbose)
                cmdPrinter.printResponse(spmlResponse);

            cmdPrinter.printOutcome(spmlResponse);

        } else {
            cmdPrinter.printErrMsg("Unexpected message received, command execution error. Type 'log:display-exception' for details");
        }


        return null;

    }

    protected abstract RequestType buildSpmlRequest(ProvisioningServiceProvider psp, PsPChannel pspChannel) throws Exception;

    //----------------------------< SPML Utils >

    protected PSOType lookupGroup(PsPChannel pspChannel, String groupName) throws IdentityMediationException {
        SpmlR2PSPMediator mediator = (SpmlR2PSPMediator) pspChannel.getIdentityMediator();
        EndpointDescriptor ed = resolvePsPEndpoint(pspChannel, SpmlR2Binding.SPMLR2_LOCAL);

        SearchRequestType spmlRequest = new SearchRequestType();
        spmlRequest.setRequestID(uuidGenerator.generateId());
        spmlRequest.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        SearchQueryType spmlQry  = new SearchQueryType();
        spmlQry.setScope(ScopeType.ONE_LEVEL);
        spmlQry.setTargetID(targetId);

        spmlRequest.setQuery(spmlQry);

        SelectionType spmlSelect = new SelectionType();
        spmlSelect.setNamespaceURI("http://www.w3.org/TR/xpath20");

        String qry = "/groups[name='"+groupName+"']";

        spmlSelect.setPath(qry);
        spmlSelect.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        JAXBElement jaxbSelect= new JAXBElement(
                new QName( SPMLR2Constants.SPML_NS, "select"),
                spmlSelect.getClass(),
                spmlSelect
        );

        spmlQry.getAny().add(jaxbSelect);

        SearchResponseType spmlResponse = (SearchResponseType) mediator.sendMessage(spmlRequest, ed, pspChannel);

        List<PSOType> psoGroups = spmlResponse.getPso();

        if (psoGroups.size() > 1)
            throw new IdentityMediationException("Too many groups found for name " + groupName);

        if (psoGroups.size() < 1)
            throw new IdentityMediationException("Group not found for '" + groupName + "'");

        return psoGroups.get(0);

    }

    protected LookupResponseType lookupGroup(PsPChannel pspChannel, Long id) throws IdentityMediationException, 
            GroupNotFoundException {

        SpmlR2PSPMediator mediator = (SpmlR2PSPMediator) pspChannel.getIdentityMediator();
        EndpointDescriptor ed = resolvePsPEndpoint(pspChannel, SpmlR2Binding.SPMLR2_LOCAL);

        PSOIdentifierType psoGroupId = new PSOIdentifierType();
        psoGroupId.setTargetID(targetId);
        psoGroupId.setID(id + "");
        psoGroupId.getOtherAttributes().put(SPMLR2Constants.groupAttr, "true");

        LookupRequestType spmlRequest = new LookupRequestType();
        spmlRequest.setRequestID(uuidGenerator.generateId());
        spmlRequest.setPsoID(psoGroupId);

        return (LookupResponseType) mediator.sendMessage(spmlRequest, ed, pspChannel);


    }

    protected PSOType lookupUser(PsPChannel pspChannel, Long id) throws IdentityMediationException {

        SpmlR2PSPMediator mediator = (SpmlR2PSPMediator) pspChannel.getIdentityMediator();
        EndpointDescriptor ed = resolvePsPEndpoint(pspChannel, SpmlR2Binding.SPMLR2_LOCAL);

        PSOIdentifierType psoUserId = new PSOIdentifierType();
        psoUserId.setTargetID(targetId);
        psoUserId.setID(id + "");
        psoUserId.getOtherAttributes().put(SPMLR2Constants.userAttr, "true");

        LookupRequestType spmlRequest = new LookupRequestType();
        spmlRequest.setRequestID(uuidGenerator.generateId());
        spmlRequest.setPsoID(psoUserId);

        LookupResponseType spmlResponse = (LookupResponseType) mediator.sendMessage(spmlRequest, ed, pspChannel);

        return spmlResponse.getPso();

    }
    


}
