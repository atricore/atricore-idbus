//import { TokenRenewMode } from "@axa-fr/react-oidc";

export const IAMTFconfiguration = {
    client_id: "my-client",
    redirect_uri: window.location.origin + "/authentication/callback",
    silent_redirect_uri:
      window.location.origin + "/authentication/silent-callback",
    scope: "openid profile email api offline_access", // offline_access scope allow your client to retrieve the refresh_token
    authority: "http://localhost:8081/IDBUS/MYIAM-01/MY-APP-OP/OIDC/MD",
    service_worker_relative_url: "/OidcServiceWorker.js",
    service_worker_only: false,
  };