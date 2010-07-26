package com.atricore.idbus.console.main {
public final class CommandNames {
    public static const STARTUP_CMD:String = "startupCommand";
    public static const DELETE_USER_CMD:String = "deleteUserCommand";
    public static const SETUP_SERVER_CMD:String = "setupServerCommand";
    public static const REGISTER_CMD:String = "registerCommand";
    public static const CREATE_SIMPLE_SSO_IDENTITY_APPLIANCE_CMD:String = "createSimpleSSOIdentityApplianceCommand";
    public static const CREATE_IDENTITY_APPLIANCE_CMD:String = "createIdentityApplianceCommand";
    public static const IDENTITY_APPLIANCE_REMOVE_CMD:String = "identityApplianceRemoveCommand";
    public static const IDENTITY_PROVIDER_REMOVE_CMD:String = "identityProviderRemoveCommand";
    public static const SERVICE_PROVIDER_REMOVE_CMD:String = "serviceProviderRemoveCommand";
    public static const IDP_CHANNEL_REMOVE_CMD:String = "idpChannelRemoveCommand";
    public static const SP_CHANNEL_REMOVE_CMD:String = "spChannelRemoveCommand";
    public static const DB_IDENTITY_VAULT_REMOVE_CMD:String = "dbIdentityVaultRemoveCommand";
    public static const LOOKUP_IDENTITY_APPLIANCE_BY_ID_CMD:String = "dbIdentityVaultRemoveCommand";
    public static const IDENTITY_APPLIANCE_LIST_LOAD_CMD:String = "identityApplianceListLoadCommand";
    public static const BUILD_IDENTITY_APPLIANCE_CMD:String = "buildIdentityApplianceCmd";
    public static const DEPLOY_IDENTITY_APPLIANCE_CMD:String = "deployIdentityApplianceCommand";
    public static const EDIT_IDENTITY_APPLIANCE_CMD:String = "editIdentityApplianceCommand";
    public static const UPLOAD_CMD:String = "uploadCommand";


    public final function CommandNames() {
        throw new Error("This class is only constants container. It can't be instantiated.");
    }
}
}
