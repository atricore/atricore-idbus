package org.atricore.idbus.capabilities.sso.main;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/12/13
 */
public enum AppResource {

    SUGAR_CRM("SugarCRM", "images/sso/sugar_crm_service_provider.png"),
    GOOGLE_APPS("GoogleApps", "images/sso/google_service_provider.png"),
    SALESFORCE("SalesFokrce", "images/sso/salesforce_service_provider.png"),
    ALFRESCO("AlfrescorResource", "images/sso/alfresco_resource.png"),
    COLDFUSION("ColdfusionResource", "images/sso/alfresco_resource.png"),
    DOMINO("DominoResource", "images/sso/alfresco_resource.png"), // TODO : Is this available ?
    JOSSO_EPP("JBossEPPResource", "images/sso/jbossepp_resource.png"),
    JBOSS_PORTAL("JBossPortalResource", "images/sso/jboss_potal_resource.png"),
    JOSSO_1("JOSSO1Resource", "images/sso/josso1_resource.png"),
    JOSSO_2("JOSSO2Resource", "images/sso/josso2_resource.png"),
    LIFERAY("LiferayResource", "images/sso/liferay_resource.png"),
    MICROSTRATEGY("MicroStrategyResource", "images/sso/microstrategy_resource.png"),
    BLACKBOARD("BlackBoardResource", "images/sso/blackboard_resource.png"),
    SAS("SASResource", "images/sso/sas_resource.png"),
    SELFSERVICES("SelfServicesResource", "images/sso/selfservices_resource.png"),
    SHAREPOINT("SharepointResource", "images/sso/sharepoint_resource.png"),
    SAML2_SP("SAML2SP", "images/sso/external_saml_service_provider.png"),
    SAML2_SP_PROXY("SAMLSPProxy", "images/sso/external_saml_service_provider.png"), // TODO : Override in josso25
    UNKNOWN("UNKNONW", "images/sso/josso2_resource.png"),
    SAML2_IDP_LOCAL("SAML2IDPLocal", "images/sso/identity_provider.png"),
    SAML2_IDP_REMOTE("SAML2IDPRemote", "images/sso/external_saml_identity_provider.png"),
    GOOGLE_IDP_REMOTE("GoogleIDPRemote", "images/sso/google_external_identity_provider.png"),
    OIDC_RP("OIDCRP", "images/sso/josso1_resource.png.png"),
    APP_PAGE_LOGO("app-page-logo", "josso25/img/josso-logo-alt.svg"),
    APP_CARD_LOGO("app-card-logo", "josso25/img/josso-logo-alt.svg"),
    ;

    private String resourceType;

    private String image;

    private AppResource(String image) {
        this.image = image;
    }

    AppResource(String type, String image) {
        this.resourceType = type;
        this.image = image;
    }

    public static AppResource getForResource(String resourceType) {
        for (AppResource a : AppResource.values()) {
            if (a.resourceType.equals(resourceType))
                return a;
        }

        return UNKNOWN;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getImage() {
        return image;
    }

}
