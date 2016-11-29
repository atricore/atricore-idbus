/*
 * Atricore IDBus
 *
 * Copyright (c) 2016, Atricore Inc.
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
package org.atricore.idbus.connectors.jitscim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientRuntimeException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.handlers.ClientHandler;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticationFailure;
import org.atricore.idbus.capabilities.sts.main.policies.AbstractAuthenticationPolicy;
import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.authn.SSONameValuePair;
import org.atricore.idbus.kernel.main.authn.SSOUser;
import org.scribe.model.*;
import org.scribe.oauth.OAuth10aServiceImpl;
import org.wso2.charon.core.exceptions.AbstractCharonException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.schema.SCIMConstants;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Map;
import java.util.Set;

public class JITScimProvisioning extends AbstractAuthenticationPolicy {

    private static final Log logger = LogFactory.getLog(JITScimProvisioning.class);

    private String endpoint;
    private String consumerKey;
    private String consumerSecret;
    private String tokenId;
    private String tokenSecret;
    private String realm;

    @Override
    public Set<PolicyEnforcementStatement> verify(Subject subject, Object context) throws SecurityTokenAuthenticationFailure {
        String email = getProperty(subject, "email");
        String username = email;
        Integer subsidiary = Integer.valueOf(getProperty(subject, "subsidiary"));

        ExtendedUser scimUser = null;

        try {
            scimUser = getUser(username, subsidiary);
            logger.debug("Found user [" + scimUser + "]");
        } catch (NotFoundException e) {
            try {
                logger.debug("Attempting user creation [" + subject + "]");
                scimUser = createUser(subject);
            } catch (AbstractCharonException e2) {
                logger.error("error creating user", e2);
            }
        } catch (AbstractCharonException e) {
            logger.error("error getting user", e);
        }

        try {
            if (scimUser != null && !scimUser.getEmail().equals(email)) {
                scimUser = updateUserEMail(scimUser, email);
            }
        } catch (AbstractCharonException e) {
            logger.error("error updating user", e);
        }

        if (scimUser == null) {
            throw new SecurityTokenAuthenticationFailure(getName(), null, null);
        }

        return null;
    }

    protected ExtendedUser getUser(String username, Integer subsidiary) throws AbstractCharonException {
        ExtendedUser foundUser = null;

        try {
            ExtendedSCIMClient scimClient = new ExtendedSCIMClient();
            ExtendedUser scimUser = new ExtendedUser();
            scimUser.setMethod("GET");
            scimUser.setEmail(username);
            scimUser.setSubsidiary(subsidiary);

            String encodedUser = scimClient.encodeSCIMObject(scimUser, SCIMConstants.JSON);
            ScimResponseHandler responseHandler = new ScimResponseHandler();
            responseHandler.setSCIMClient(scimClient);
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.handlers(new ClientHandler[]{responseHandler});
            RestClient restClient = new RestClient(clientConfig);
            Resource userResource = oauthifyResource(restClient.resource(endpoint));

            String response = userResource.
                    contentType(SCIMConstants.APPLICATION_JSON).accept(SCIMConstants.APPLICATION_JSON).
                    post(String.class, encodedUser);
            foundUser = (ExtendedUser) scimClient.decodeSCIMResponse(response, SCIMConstants.JSON, ExtendedSCIMClient.EXTENDED_USER);
        } catch (ClientRuntimeException e) {
            if (e.getCause() instanceof AbstractCharonException) {
                AbstractCharonException ace = (AbstractCharonException) e.getCause();
                if (ace.getCode() == 404) {
                    throw new NotFoundException();
                }

            }
        }

        return foundUser;
    }


    protected ExtendedUser createUser(Subject subject) throws AbstractCharonException {
        ExtendedUser createdUser = null;

        try {
            ExtendedSCIMClient scimClient = new ExtendedSCIMClient();
            ExtendedUser scimUser = new ExtendedUser();
            scimUser.setMethod("POST");
            scimUser.setFirstName(getProperty(subject, "firstName"));
            scimUser.setLastName(getProperty(subject, "lastName"));
            scimUser.setEmail(getProperty(subject, "email"));
            scimUser.setSubsidiary(Integer.valueOf(getProperty(subject, "subsidiary")));

            String encodedUser = scimClient.encodeSCIMObject(scimUser, SCIMConstants.JSON);
            ScimResponseHandler responseHandler = new ScimResponseHandler();
            responseHandler.setSCIMClient(scimClient);
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.handlers(new ClientHandler[]{responseHandler});
            RestClient restClient = new RestClient(clientConfig);
            Resource userResource = oauthifyResource(restClient.resource(endpoint));

            String response = userResource.
                    contentType(SCIMConstants.APPLICATION_JSON).accept(SCIMConstants.APPLICATION_JSON).
                    post(String.class, encodedUser);

            createdUser = (ExtendedUser) scimClient.decodeSCIMResponse(response, SCIMConstants.JSON, ExtendedSCIMClient.EXTENDED_USER);

        } catch (ClientRuntimeException e) {
            if (e.getCause() instanceof AbstractCharonException) {
                AbstractCharonException ace = (AbstractCharonException) e.getCause();
                if (ace.getCode() == 404) {
                    throw new NotFoundException();
                }

            }
        }

        return createdUser;
    }

    protected ExtendedUser updateUserEMail(ExtendedUser user, String newEmail) throws AbstractCharonException {
        ExtendedUser updatedUser = null;

        try {
            ExtendedSCIMClient scimClient = new ExtendedSCIMClient();
            ExtendedUser scimUser = new ExtendedUser();
            scimUser.setMethod("PUT");
            scimUser.setFirstName(user.getFirstName());
            scimUser.setLastName(user.getLastName());
            scimUser.setEmail(user.getEmail());
            scimUser.setNewEmail(newEmail);
            scimUser.setSubsidiary(user.getSubsidiary());

            String encodedUser = scimClient.encodeSCIMObject(scimUser, SCIMConstants.JSON);
            ScimResponseHandler responseHandler = new ScimResponseHandler();
            responseHandler.setSCIMClient(scimClient);
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.handlers(new ClientHandler[]{responseHandler});
            RestClient restClient = new RestClient(clientConfig);
            Resource userResource = oauthifyResource(restClient.resource(endpoint));

            String response = userResource.
                    contentType(SCIMConstants.APPLICATION_JSON).accept(SCIMConstants.APPLICATION_JSON).
                    post(String.class, encodedUser);

            updatedUser = (ExtendedUser) scimClient.decodeSCIMResponse(response, SCIMConstants.JSON, ExtendedSCIMClient.EXTENDED_USER);

        } catch (ClientRuntimeException e) {
            if (e.getCause() instanceof AbstractCharonException) {
                AbstractCharonException ace = (AbstractCharonException) e.getCause();
                if (ace.getCode() == 404) {
                    throw new NotFoundException();
                }

            }
        }

        return updatedUser;
    }

    private Resource oauthifyResource(Resource resource) {
        OAuthConfig authConfig = new OAuthConfig(consumerKey, consumerSecret, null, SignatureType.Header, null, null);
        Token token = new Token(tokenId, tokenSecret);
        OAuth10aServiceImpl auth10aServiceImpl = new OAuth10aServiceImpl(new ScimApi(), authConfig);
        OAuthRequest request = new OAuthRequest(Verb.POST, endpoint);
        request.setRealm(realm);
        auth10aServiceImpl.signRequest(token, request);

        StringBuffer oauthProperties = new StringBuffer();
        for (Map.Entry<String, String> entry : request.getOauthParameters().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            oauthProperties.append(key).append("=").append("\"" + value + "\",");
        }
        oauthProperties.append("realm").append("=").append("\"" + realm + "\"");
        resource.header(SCIMConstants.AUTHORIZATION_HEADER, "OAuth " + oauthProperties.toString());

        return resource;
    }

    private String getProperty(Subject subject, String propertyName) {
        String propertyValue = null;

        for (Principal principal : subject.getPrincipals()) {
            if (principal instanceof SSOUser) {
                for (SSONameValuePair property : ((SSOUser) principal).getProperties()) {
                    if (property.getName().equals(propertyName)) {
                        propertyValue = property.getValue();
                        break;
                    }
                }
            }
        }

        return propertyValue;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }


    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }


}
