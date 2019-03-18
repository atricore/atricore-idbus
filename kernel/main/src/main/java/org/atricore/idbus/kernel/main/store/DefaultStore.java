package org.atricore.idbus.kernel.main.store;

import org.atricore.idbus.kernel.main.authn.*;
import org.atricore.idbus.kernel.main.authn.scheme.UsernamePasswordCredentialProvider;
import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.domain.UserAttributeValue;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.exception.UserNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.spi.IdentityPartition;
import org.atricore.idbus.kernel.main.store.exceptions.NoSuchUserException;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;

import java.util.Collection;

/**
 * Default Identity Store
 */
public class DefaultStore extends AbstractStore {

    private IdentityPartition partition;

    public IdentityPartition getPartition() {
        return partition;
    }

    public void setPartition(IdentityPartition partition) {
        this.partition = partition;
    }

    public Credential[] loadCredentials(CredentialKey key, CredentialProvider cp) throws SSOIdentityException {

        try {

            User user = partition.findUserByUserName(key.toString());

            if (user.getUserPassword() == null)
                return new Credential[0];

            Credential usrCred = cp.newCredential(UsernamePasswordCredentialProvider.USERNAME_CREDENTIAL_NAME, user.getUserName());
            Credential pwdCred = cp.newCredential(UsernamePasswordCredentialProvider.PASSWORD_CREDENTIAL_NAME, user.getUserPassword());
            Credential idCred = cp.newCredential(UsernamePasswordCredentialProvider.USERID_CREDENTIAL_NAME, key.toString());
            Credential saltCred = null;
            if (user.getSalt() != null)
                saltCred = cp.newCredential(UsernamePasswordCredentialProvider.SALT_CREDENTIAL_NAME, user.getSalt());

            return saltCred == null ? new Credential[]{usrCred, idCred, pwdCred} : new Credential[]{usrCred, idCred, pwdCred, saltCred};

        } catch (UserNotFoundException e) {
            return new Credential[0];
        } catch (ProvisioningException e) {
            throw new SSOIdentityException(e);
        }
    }

    public BaseUser loadUser(UserKey key) throws NoSuchUserException, SSOIdentityException {

        try {
            User jdoUser = partition.findUserByUserName(key.toString());
            return toSSOUser(jdoUser);
        } catch (UserNotFoundException e) {
            throw new NoSuchUserException(key.toString());
        } catch (ProvisioningException e) {
            throw new SSOIdentityException(e);
        }
    }

    public BaseRole[] findRolesByUserKey(UserKey key) throws SSOIdentityException {

        try {
            Collection<Group> groups = partition.findGroupsByUserName(key.toString());
            return toSSORoles(groups);
        } catch (UserNotFoundException e) {
            throw new NoSuchUserException(key.toString());
        } catch (ProvisioningException e) {
            throw new SSOIdentityException(e);
        }

    }

    public boolean userExists(UserKey key) throws SSOIdentityException {
        try {
            partition.findUserByUserName(key.toString());
            return true;
        } catch (ProvisioningException e) {
            // Ingore this
            return false;
        }
    }

    protected BaseUser toSSOUser(User jdoUser) {
        BaseUser ssoUser = new BaseUserImpl();
        ssoUser.setName(jdoUser.getUserName());

        // Email
        if (jdoUser.getEmail() != null) {
            SSONameValuePair email = new SSONameValuePair("email", jdoUser.getEmail());
            ssoUser.addProperty(email);
        }

        // First Name
        if (jdoUser.getFirstName() != null) {
            SSONameValuePair firstName = new SSONameValuePair("firstName", jdoUser.getFirstName());
            ssoUser.addProperty(firstName);
        }

        // Last Name
        if (jdoUser.getSurename() != null) {
            SSONameValuePair lastName = new SSONameValuePair("lastName", jdoUser.getSurename());
            ssoUser.addProperty(lastName);
        }

        // Common Name
        if (jdoUser.getCommonName() != null) {
            SSONameValuePair commonName = new SSONameValuePair("commonName", jdoUser.getCommonName());
            ssoUser.addProperty(commonName);
        }

        // Country Name
        if (jdoUser.getCountryName() != null) {
            SSONameValuePair countryName = new SSONameValuePair("countryName", jdoUser.getCountryName());
            ssoUser.addProperty(countryName);
        }

        // Language
        if (jdoUser.getLanguage() != null) {
            SSONameValuePair language = new SSONameValuePair("language", jdoUser.getLanguage());
            ssoUser.addProperty(language);
        }

        // Telephone Number
        if (jdoUser.getTelephoneNumber() != null) {
            SSONameValuePair telephoneNumber = new SSONameValuePair("telephoneNumber", jdoUser.getTelephoneNumber());
            ssoUser.addProperty(telephoneNumber);
        }

        // Street Address
        if (jdoUser.getStreetAddress() != null) {
            SSONameValuePair streetAddress = new SSONameValuePair("streetAddress", jdoUser.getStreetAddress());
            ssoUser.addProperty(streetAddress);
        }

        // Postal Address
        if (jdoUser.getPostalAddress() != null) {
            SSONameValuePair postalAddress = new SSONameValuePair("postalAddress", jdoUser.getPostalAddress());
            ssoUser.addProperty(postalAddress);
        }

        // -----------------------------------------------------------
        // Security properties
        // -----------------------------------------------------------

        if (jdoUser.getAccountDisabled() != null) {
            SSONameValuePair p = new SSONameValuePair("accountDisabled", jdoUser.getAccountDisabled().toString());
            ssoUser.addProperty(p);
        }

        if (jdoUser.getAccountExpires() != null) {
            SSONameValuePair p = new SSONameValuePair("accountExpires", jdoUser.getAccountExpires().toString());
            ssoUser.addProperty(p);
        }

        if (jdoUser.getAccountExpirationDate() != null) {
            SSONameValuePair p = new SSONameValuePair("accountExpirationDate", jdoUser.getAccountExpirationDate().toString());
            ssoUser.addProperty(p);
        }

        if (jdoUser.getLastAuthentication() != null) {
            SSONameValuePair p = new SSONameValuePair("lastAuthentication", jdoUser.getLastAuthentication().toString());
            ssoUser.addProperty(p);
        }

        if (jdoUser.getPasswordExpirationDate() != null) {
            SSONameValuePair p = new SSONameValuePair("passwordExpirationDate", jdoUser.getPasswordExpirationDate().toString());
            ssoUser.addProperty(p);
        }

        if (jdoUser.getPreventNewSession() != null) {
            SSONameValuePair p = new SSONameValuePair("preventNewSession", jdoUser.getPreventNewSession().toString());
            ssoUser.addProperty(p);
        }


        // -----------------------------------------------------------
        // Custom attributes
        // -----------------------------------------------------------
        if (jdoUser.getAttrs() != null) {
            for (UserAttributeValue userAttribute : jdoUser.getAttrs()) {
                SSONameValuePair attr = new SSONameValuePair(userAttribute.getName(), userAttribute.getValue());
                ssoUser.addProperty(attr);
            }
        }

        return ssoUser;

    }

    protected BaseRole[] toSSORoles(Collection<Group> groups) {

        if (groups == null)
            return new BaseRole[0];

        BaseRole[] ssoRoles = new BaseRole[groups.size()];
        int i = 0;
        for (Group group : groups) {
            BaseRole ssoRole = new BaseRoleImpl();
            ssoRole.setName(group.getName());
            ssoRoles[i] = ssoRole;
            i++;
        }

        return ssoRoles;

    }
}