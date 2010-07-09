package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.services.spi.request.CreateSimpleSsoRequest;
import com.atricore.idbus.console.services.spi.response.CreateSimpleSsoResponse;
import org.atricore.idbus.capabilities.management.main.domain.IdentityAppliance;
import org.atricore.idbus.capabilities.management.main.domain.metadata.*;
import org.atricore.idbus.capabilities.management.main.exception.IdentityServerException;
import org.atricore.idbus.capabilities.management.main.spi.IdentityApplianceManagementService;
import org.atricore.idbus.capabilities.management.main.spi.request.*;
import org.atricore.idbus.capabilities.management.main.spi.response.*;
import com.atricore.idbus.console.services.spi.IdentityApplianceManagementAjaxService;

/**
 * Author: Dejan Maric
 */
public class IdentityApplianceManagementAjaxServiceImpl implements IdentityApplianceManagementAjaxService {

    private IdentityApplianceManagementService idApplianceManagementService;

    public DeployIdentityApplianceResponse deployIdentityAppliance(DeployIdentityApplianceRequest req) throws IdentityServerException {
        return idApplianceManagementService.deployIdentityAppliance(req);
    }

    public UndeployIdentityApplianceResponse undeployIdentityAppliance(UndeployIdentityApplianceRequest req) throws IdentityServerException {
        return idApplianceManagementService.undeployIdentityAppliance(req);
    }

    public ImportIdentityApplianceResponse importIdentityAppliance(ImportIdentityApplianceRequest request) throws IdentityServerException {
        return idApplianceManagementService.importIdentityAppliance(request);
    }

    public ExportIdentityApplianceResponse ExportIdentityAppliance(ExportIdentityApplianceRequest request) throws IdentityServerException {
        return idApplianceManagementService.exportIdentityAppliance(request);
    }

    public ManageIdentityApplianceLifeCycleResponse manageIdentityApplianceLifeCycle(ManageIdentityApplianceLifeCycleRequest req) throws IdentityServerException {
        return idApplianceManagementService.manageIdentityApplianceLifeCycle(req);
    }

    public void setIdApplianceManagementService(IdentityApplianceManagementService idApplianceManagementService) {
        this.idApplianceManagementService = idApplianceManagementService;
    }

    public CreateSimpleSsoResponse createSimpleSso(CreateSimpleSsoRequest req)
            throws IdentityServerException {

        IdentityApplianceDefinition iad = req.getIdentityApplianceDefinition();

        //providers that are currently in providers list are service providers
        for(Provider sp : iad.getProviders()){
            if(sp.getRole().equals(ProviderRole.SSOServiceProvider)){
                populateServiceProvider((ServiceProvider)sp, iad);
            }
        }
        iad.getProviders().add(createIdentityProvider(iad));
        iad.getProviders().add(createBindingProvider(iad));

        //TODO set Locations for all objects
        //TODO add bindings and profiles to channels

        IdentityAppliance idAppliance = new IdentityAppliance();
        idAppliance.setIdApplianceDefinition(iad);

        AddIdentityApplianceRequest addIdApplianceReq = new AddIdentityApplianceRequest();
        addIdApplianceReq.setIdentityAppliance(idAppliance);
        idApplianceManagementService.addIdentityAppliance(addIdApplianceReq);
        return null;
    }

    public AddIdentityApplianceResponse addIdentityAppliance(AddIdentityApplianceRequest req) throws IdentityServerException {
        return idApplianceManagementService.addIdentityAppliance(req);
    }

    public LookupIdentityApplianceByIdResponse lookupIdentityApplianceById(LookupIdentityApplianceByIdRequest req) throws IdentityServerException {
        return idApplianceManagementService.lookupIdentityApplianceById(req);
    }

    public RemoveIdentityApplianceResponse removeIdentityAppliance(RemoveIdentityApplianceRequest req) throws IdentityServerException{
        return idApplianceManagementService.removeIdentityAppliance(req);
    }

    public ListIdentityAppliancesResponse listIdentityAppliances(ListIdentityAppliancesRequest req) throws IdentityServerException {
        ListIdentityAppliancesResponse ret = idApplianceManagementService.listIdentityAppliances(req);
        return ret;
    }

    public AddIdentityApplianceDefinitionResponse addIdentityApplianceDefinition(AddIdentityApplianceDefinitionRequest req) throws IdentityServerException {
        return idApplianceManagementService.addIdentityApplianceDefinition(req);
    }

    public UpdateIdentityApplianceResponse updateApplianceDefinition(UpdateIdentityApplianceRequest request) throws IdentityServerException {
        return idApplianceManagementService.updateIdentityAppliance(request);
    }

    public LookupIdentityApplianceDefinitionByIdResponse lookupIdentityApplianceDefinitionById(LookupIdentityApplianceDefinitionByIdRequest req) throws IdentityServerException {
        return idApplianceManagementService.lookupIdentityApplianceDefinitionById(req);
    }

    public LookupIdentityApplianceDefinitionResponse lookupIdentityApplianceDefinition(LookupIdentityApplianceDefinitionRequest req) throws IdentityServerException {
        return idApplianceManagementService.lookupIdentityApplianceDefinition(req);
    }

    public ListIdentityApplianceDefinitionsResponse listIdentityApplianceDefinitions(ListIdentityApplianceDefinitionsRequest req) throws IdentityServerException {
        return idApplianceManagementService.listIdentityApplianceDefinitions(req);
    }

    private void populateServiceProvider(ServiceProvider sp, IdentityApplianceDefinition iad) {
        sp.setIdentityAppliance(iad);

        BindingChannel bindingChannel = new BindingChannel();
        bindingChannel.setName(sp.getName() + " binding channel");
        bindingChannel.setTarget(sp);

        Location bpLocation = new Location();
        bpLocation.setProtocol(sp.getLocation().getProtocol());
        bpLocation.setHost(sp.getLocation().getHost());
        bpLocation.setPort(sp.getLocation().getPort());
        bpLocation.setContext(iad.getLocation().getContext());////not sp.getLocation.uri but iad.getLocation.Context
        bpLocation.setUri("/" + createUrlSafeString(sp.getName()) + "/SSOP");//remove sp.getLocation.uri
        bindingChannel.setLocation(bpLocation);

        bindingChannel.getActiveBindings().add(Binding.SSO_ARTIFACT);
        bindingChannel.getActiveBindings().add(Binding.SSO_REDIRECT);

        bindingChannel.getActiveProfiles().add(Profile.SSO);
        bindingChannel.getActiveProfiles().add(Profile.SSO_SLO);

        sp.setBindingChannel(bindingChannel);

        IdentityProviderChannel idpChannel = new IdentityProviderChannel();
        idpChannel.setName(sp.getName() + " to idp default channel");
        idpChannel.setTarget(sp);

        Location idpLocation = new Location();
        idpLocation.setProtocol(sp.getLocation().getProtocol());
        idpLocation.setHost(sp.getLocation().getHost());
        idpLocation.setPort(sp.getLocation().getPort());
        idpLocation.setContext(iad.getLocation().getContext());//this will be IDBUS
        idpLocation.setUri("/" + createUrlSafeString(sp.getName()) + "/SAML2");
        idpChannel.setLocation(idpLocation);

        idpChannel.getActiveBindings().add(Binding.SAMLR2_ARTIFACT);
        idpChannel.getActiveBindings().add(Binding.SAMLR2_HTTP_REDIRECT);

        idpChannel.getActiveProfiles().add(Profile.SSO);
        idpChannel.getActiveProfiles().add(Profile.SSO_SLO);

        sp.setDefaultChannel(idpChannel);
    }

    private Provider createBindingProvider(IdentityApplianceDefinition iad) {
        BindingProvider bp = new BindingProvider();
        bp.setIdentityAppliance(iad);
        bp.setName(iad.getName() + " bp");
        BindingChannel bindingChannel = new BindingChannel();
        bindingChannel.setName(bp.getName() + " josso binding channel");
        bindingChannel.setTarget(bp);

        //TODO set bp.location
        Location bpLocation = new Location();
        bpLocation.setProtocol(iad.getLocation().getProtocol());
        bpLocation.setHost(iad.getLocation().getHost());
        bpLocation.setPort(iad.getLocation().getPort());
        bpLocation.setContext(iad.getLocation().getContext());
        bpLocation.setUri("/" + createUrlSafeString(bp.getName()) + "/SAML2");//obrisi saml2
        bindingChannel.setLocation(bpLocation);

        bindingChannel.getActiveBindings().add(Binding.SSO_ARTIFACT);
        bindingChannel.getActiveBindings().add(Binding.SSO_REDIRECT);
        bindingChannel.getActiveBindings().add(Binding.JOSSO_SOAP);

        bindingChannel.getActiveProfiles().add(Profile.SSO);
        bindingChannel.getActiveProfiles().add(Profile.SSO_SLO);
        bp.setBindingChannel(bindingChannel);

        return bp;
    }

    private Provider createIdentityProvider(IdentityApplianceDefinition iad) {
        IdentityProvider idp = new IdentityProvider();
        idp.setName(iad.getName() + " idp");
        idp.setIdentityAppliance(iad);

        //TODO set idp.location

        ServiceProviderChannel spChannel = new ServiceProviderChannel();
        spChannel.setName(idp.getName() + " to sp default channel");
        spChannel.setTarget(idp);

        spChannel.getActiveBindings().add(Binding.SAMLR2_ARTIFACT);
        spChannel.getActiveBindings().add(Binding.SAMLR2_HTTP_REDIRECT);
        spChannel.getActiveBindings().add(Binding.SAMLR2_HTTP_POST);
        spChannel.getActiveBindings().add(Binding.SAMLR2_SOAP);

        spChannel.getActiveProfiles().add(Profile.SSO);
        spChannel.getActiveProfiles().add(Profile.SSO_SLO);

        Location idpLocation = new Location();
        idpLocation.setProtocol(iad.getLocation().getProtocol());
        idpLocation.setHost(iad.getLocation().getHost());
        idpLocation.setPort(iad.getLocation().getPort());
        idpLocation.setContext(iad.getLocation().getContext());
        idpLocation.setUri(createUrlSafeString(idp.getName()));

        spChannel.setLocation(idpLocation);

        //simple sso wizard creates only one vault
        spChannel.setIdentityVault(iad.getIdentityVaults().get(0));

        idp.setDefaultChannel(spChannel);

        return idp;
    }

    /**
     * Creates stringToCheck safe string. String will consist of letters, numbers, underscores and dashes
     * @param stringToCheck
     * @return url safe string
     */
    private String createUrlSafeString(String stringToCheck){
    	String regex = "[^a-zA-Z0-9-_]";
        return stringToCheck.replaceAll(regex, "-");
    }    
}
