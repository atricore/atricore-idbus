package org.atricore.idbus.capabilities.sso.main.select.selectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.select.SSOEntitySelectorMediator;
import org.atricore.idbus.capabilities.sso.main.select.spi.AbstractEntitySelector;
import org.atricore.idbus.capabilities.sso.main.select.spi.EntitySelectionContext;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManager;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.claim.UserClaim;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProvider;
import org.atricore.idbus.kernel.main.mediation.select.SelectorChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Selects an IdP based on user input (UserClaims).  It will also keep track of the selected IdP using a mediation state
 * local variable (This is a browser session based state variable)
 *
 * @author: sgonzalez@atriocore.com
 * @date: 6/12/13
 */
public class UserSelectedIdPEntitySelector extends AbstractEntitySelector {

    private static final Log logger = LogFactory.getLog(RequestedIdPEntitySelector.class);

    @Override
    public boolean canHandle(EntitySelectionContext ctx) {
        return true;
    }

    public CircleOfTrustMemberDescriptor selectCotMember(EntitySelectionContext ctx, SelectorChannel channel) throws SSOException {

        CircleOfTrustMemberDescriptor idp = null;


        // Try previous COT member
        // TODO : Consider that the previous selection may not be trusted/available for the current SP!
        String spName = ctx.getRequest().getIssuer();

        // The issuer must be a local SP ... we do not support remote access to this service yet!
        ServiceProvider sp = null;
        for (FederatedProvider provider : ctx.getCotManager().getCot().getProviders()) {
            if (provider.getName().equals(spName)) {
                sp = (ServiceProvider) provider;
                break;
            }
        }

        if (sp == null) {
            throw new SSOException("Entity Selector request issued by non-local SP " + spName);
        }

        idp = lookupPreviouslySelectedIdP(ctx, sp);
        if (idp != null)
            return idp;

        // Try with selected IDP alias first
        {
            UserClaim idpAlias = ctx.getUserClaim(SELECTED_IDP_ALIAS_ATTR);
            if (idpAlias != null) {

                String idpAliasValue = (String) idpAlias.getValue();

                if (logger.isDebugEnabled())
                    logger.debug("Using IdP alias " + idpAlias.getValue());

                // Support both encoded and decoded IDP alias values
                idp = ctx.getCotManager().lookupMemberByAlias(idpAliasValue);
                if (idp == null) {
                    String decodedIdpAlias = new String(Base64.decodeBase64(idpAliasValue.getBytes()));
                    idp = ctx.getCotManager().lookupMemberByAlias(decodedIdpAlias);
                }

            }
        }

        // Now try with selected IDP ID
        {
            UserClaim idpId = ctx.getUserClaim(SELECTED_IDP_ID_ATTR);
            if (idpId != null && idp == null) {

                String idpIdName = (String) idpId.getValue();
                if (logger.isDebugEnabled())
                    logger.debug("Using IdP ID " + idpId);

                idp = ctx.getCotManager().lookupMemberById(idpIdName);
                if (idp == null) {
                    String decodedIdpId = new String(Base64.decodeBase64(idpIdName.getBytes()));
                    idp = ctx.getCotManager().lookupMemberById(decodedIdpId);
                }
            }
        }


        UserClaim rememberSelection = ctx.getUserClaim(REMEMBER_IDP_ATTR);
        if (rememberSelection != null)
            logger.warn("Using deprecated claim  [" + REMEMBER_IDP_ATTR + "]. It will be ignored!");

/*
        UserClaim rememberSelection = ctx.getUserClaim(REMEMBER_IDP_ATTR);
        if (rememberSelection != null && idp != null) {
            if (logger.isDebugEnabled())
                logger.debug("Storing selected COT member " + idp.getAlias());
            ctx.getMediationState().setLocalVariable("urn:org:atricore:idbus:capabilities:sso:select:usr:cotMember", idp);
        } else {

            if (logger.isDebugEnabled())
                logger.debug("Clearing selected COT member (if any)");

            ctx.getMediationState().removeLocalVariable("urn:org:atricore:idbus:capabilities:sso:select:usr:cotMember");
        }

 */

        return idp;

    }

    @Override
    public List<EndpointDescriptor> getUserClaimsEndpoints(EntitySelectionContext ctx, SelectorChannel channel) {

        /*
        if (ctx.getMediationState().getLocalVariable("urn:org:atricore:idbus:capabilities:sso:select:usr:cotMember") != null)
            return Collections.EMPTY_LIST;

         */

        String spName = ctx.getRequest().getIssuer();

        ServiceProvider sp = null;
        for (FederatedProvider provider : ctx.getCotManager().getCot().getProviders()) {
            if (provider.getName().equals(spName)) {
                sp = (ServiceProvider) provider;
                break;
            }
        }

        if (sp == null) {
            logger.warn("Entity Selector request issued by non-local SP " + spName);
            return Collections.EMPTY_LIST;
        }


        if (lookupPreviouslySelectedIdP(ctx, sp) != null)
            return Collections.EMPTY_LIST;

        CircleOfTrustManager cotMgr = channel.getProvider().getCotManager();

        String applianceName = channel.getProvider().getUnitContainer().getName();

        SSOEntitySelectorMediator mediator = (SSOEntitySelectorMediator) channel.getIdentityMediator();

        List<EndpointDescriptor> endpoints = new ArrayList<EndpointDescriptor>();

        // We need to build a URL like: http://<host>:<port>/IDBUS-UI/<appliance>/SSO/IDPS
        String location = mediator.getDashboardUrl() + "/IDPS";

        EndpointDescriptor ed = new EndpointDescriptorImpl(
                "SelectUserClaimsEndpoint",
                "UserClaimsRequest",
                SSOBinding.SSO_ARTIFACT.toString(),
                location,
                null);

        endpoints.add(ed);


        if (endpoints.size() > 0)
            return endpoints;

        return null;
    }

    protected CircleOfTrustMemberDescriptor lookupPreviouslySelectedIdP(EntitySelectionContext ctx, ServiceProvider sp) {

        CircleOfTrustMemberDescriptor idp = null;


        Deque<String> idps = ctx.getSelectionState().getPreviousCotMembers();
        for (String idpAlias : idps) {
            // Check default channel
            FederationChannel defaultChannel = sp.getDefaultFederationService().getChannel();
            idp = lookupAliasInChannel(idpAlias, defaultChannel);
            if (idp != null) {
                logger.trace("Found previously selected IdP in default channel [" + defaultChannel.getName() + "] " + idp.getAlias());
                return idp;
            }

            // Check override channels
            for (FederationChannel overrideChannel : sp.getDefaultFederationService().getOverrideChannels()) {
                idp = lookupAliasInChannel(idpAlias, overrideChannel);
                if (idp != null) {
                    logger.trace("Found previously selected IdP in override channel [" + overrideChannel.getName() + "] " + idp.getAlias());
                    return idp;
                }
            }
        }

        return null;

    }


}
