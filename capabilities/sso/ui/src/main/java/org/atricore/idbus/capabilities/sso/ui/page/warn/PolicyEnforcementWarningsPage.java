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
package org.atricore.idbus.capabilities.sso.ui.page.warn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.atricore.idbus.capabilities.sso.main.binding.SsoHttpArtifactBinding;
import org.atricore.idbus.capabilities.sso.ui.WebBranding;
import org.atricore.idbus.capabilities.sso.ui.internal.BaseWebApplication;
import org.atricore.idbus.capabilities.sso.ui.internal.SSOWebSession;
import org.atricore.idbus.capabilities.sso.ui.page.BasePage;
import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Artifact;
import org.atricore.idbus.kernel.main.mediation.ArtifactImpl;
import org.atricore.idbus.kernel.main.mediation.MessageQueueManager;
import org.atricore.idbus.kernel.main.mediation.policy.PolicyEnforcementRequest;
import org.atricore.idbus.kernel.main.mediation.policy.PolicyEnforcementResponse;
import org.atricore.idbus.kernel.main.mediation.policy.PolicyEnforcementResponseImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Page for displaying policy enforcement warnings.
 */
public class PolicyEnforcementWarningsPage extends BasePage {

    private static final Log logger = LogFactory.getLog(PolicyEnforcementWarningsPage.class);

    private String artifactId;

    public PolicyEnforcementWarningsPage() throws Exception {
        this(null);
    }

    public PolicyEnforcementWarningsPage(PageParameters parameters) throws Exception {
        super(parameters);
        if (parameters != null)
            artifactId = parameters.get(SsoHttpArtifactBinding.SSO_ARTIFACT_ID).toString();
    }

    @Override
    protected void onInitialize()  {
        super.onInitialize();

        PolicyEnforcementRequest policyEnforcementRequest = null;

        if (artifactId != null) {

            if (logger.isDebugEnabled())
                logger.debug("Artifact ID = " + artifactId);

            // Lookup for PolicyEnforcementRequest!
            try {
                policyEnforcementRequest = (PolicyEnforcementRequest) artifactQueueManager.pullMessage(new ArtifactImpl(artifactId));
            } catch (Exception e) {
                logger.error("Cannot resolve artifact id ["+artifactId+"] : " + e.getMessage(), e);
            }

            if (policyEnforcementRequest != null) {

                ((SSOWebSession) getSession()).setPolicyEnforcementRequest(policyEnforcementRequest);

                if (logger.isDebugEnabled())
                    logger.debug("Received Policy Enforcement request " + policyEnforcementRequest.getId() +
                            " reply to" + policyEnforcementRequest.getReplyTo());

            } else {
                logger.debug("No Policy Enforcement request received, try stored value");
                policyEnforcementRequest = ((SSOWebSession) getSession()).getPolicyEnforcementRequest();
            }
        } else {
            policyEnforcementRequest = ((SSOWebSession)getSession()).getPolicyEnforcementRequest();
        }

        if (logger.isDebugEnabled())
            logger.debug("policyEnforcementRequest = " + policyEnforcementRequest);

        if (policyEnforcementRequest == null) {
            // No way to process this page, fall-back
            WebBranding branding = ((BaseWebApplication) getApplication()).getBranding();
            if (branding.getFallbackUrl() != null) {
                // Redirect to fall-back (session expired !)
                throw new RestartResponseAtInterceptPageException(resolvePage("ERROR/SESSION"));

            }
            // Redirect to Session Expired Page
            throw new RestartResponseAtInterceptPageException(resolvePage("ERROR/SESSION"));
        }

        List<WarningData> warnings = new ArrayList<WarningData>();
        for (PolicyEnforcementStatement stmt : policyEnforcementRequest.getStatements()) {
            warnings.add(new WarningData(stmt));
            if (stmt.getValues() != null && stmt.getValues().size() > 1)
                logger.warn("PolicyEnforcementStatement has more than one value!");
        }

        add(new PropertyListView<WarningData>("warnings", warnings) {
            @Override
            public void populateItem(final ListItem<WarningData> listItem) {
                WarningData warningData = listItem.getModelObject();
                if (warningData.getMsgParam() != null) {
                    listItem.add(new Label("warnMsg",
                            getString(listItem.getModelObject().getMsgKey(), new Model<WarningData>(warningData),
                                    "Unknown Policy Enforcement warning")));
                } else {
                    listItem.add(new Label("warnMsg",
                            getString(listItem.getModelObject().getMsgKey(), null, "Unknown Policy Enforcement warning")));
                }
            }
        }).setVersioned(false);

        ContinueForm form = new ContinueForm("continueForm");
        form.setOutputMarkupId(true);
        add(form);
    }

    public final class ContinueForm extends StatelessForm<Void> {

        public ContinueForm(String id) {
            super(id);
        }

        @Override
        public final void onSubmit() {
            try {
                SSOWebSession session = (SSOWebSession) getSession();
                PolicyEnforcementRequest request = session.getPolicyEnforcementRequest();

                EndpointDescriptor ed = request.getReplyTo();

                String location = ed.getResponseLocation() != null ? ed.getResponseLocation() : ed.getLocation();

                PolicyEnforcementResponse response = new PolicyEnforcementResponseImpl();

                Artifact a = artifactQueueManager.pushMessage(response);
                location += "?SSOArt=" + a.getContent();

                if (logger.isDebugEnabled())
                    logger.debug("Returning policy enforcement response to " + location);

                session.setPolicyEnforcementRequest(null);

                getRequestCycle().scheduleRequestHandlerAfterCurrent(new RedirectRequestHandler(location));
            } catch (Exception e) {
                logger.error("Fatal error: " + e.getMessage(), e);
            }
        }
    }

    public MessageQueueManager getArtifactQueueManager() {
        return artifactQueueManager;
    }

    public void setArtifactQueueManager(MessageQueueManager artifactQueueManager) {
        this.artifactQueueManager = artifactQueueManager;
    }
}
