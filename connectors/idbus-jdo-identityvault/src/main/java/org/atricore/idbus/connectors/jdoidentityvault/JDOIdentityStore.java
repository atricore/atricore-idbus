package org.atricore.idbus.connectors.jdoidentityvault;

import org.atricore.idbus.kernel.main.authn.*;
import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.domain.UserAttributeValue;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.exception.UserNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.spi.IdentityPartition;
import org.atricore.idbus.kernel.main.store.AbstractStore;
import org.atricore.idbus.kernel.main.store.UserKey;
import org.atricore.idbus.kernel.main.store.exceptions.NoSuchUserException;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;
import org.atricore.idbus.kernel.main.store.identity.IdentityPartitionStore;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOIdentityStore extends AbstractStore implements IdentityPartitionStore
{

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
            // TODO : Support other type of credentials !

            Credential usrCred = cp.newCredential("username", user.getUserName());
            Credential pwdCred = cp.newCredential("password", user.getUserPassword());

            return new Credential[] {usrCred, pwdCred};
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
