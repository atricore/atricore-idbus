package org.atricore.idbus.connectors.jdoidentityvault;

import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.authn.*;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.provisioning.exception.UserNotFoundException;
import org.atricore.idbus.kernel.main.provisioning.spi.IdentityPartition;
import org.atricore.idbus.kernel.main.store.AbstractStore;
import org.atricore.idbus.kernel.main.store.UserKey;
import org.atricore.idbus.kernel.main.store.exceptions.NoSuchUserException;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;
import org.springframework.beans.BeanUtils;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOIdentityStore extends AbstractStore
{

    private IdentityPartition idPartition;


    public IdentityPartition getIdPartition() {
        return idPartition;
    }

    public void setIdPartition(IdentityPartition idPartition) {
        this.idPartition = idPartition;
    }

    public Credential[] loadCredentials(CredentialKey key, CredentialProvider cp) throws SSOIdentityException {

        try {

            User user = idPartition.findUserByUserName(key.toString());
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
            User jdoUser = idPartition.findUserByUserName(key.toString());
            return toSSOUser(jdoUser);
        } catch (UserNotFoundException e) {
            throw new NoSuchUserException(key.toString());
        } catch (ProvisioningException e) {
            throw new SSOIdentityException(e);
        }
    }

    public BaseRole[] findRolesByUserKey(UserKey key) throws SSOIdentityException {

        try {
            Collection<Group> groups = idPartition.findGroupsByUsernName(key.toString());
            return toSSORoles(groups);
        } catch (UserNotFoundException e) {
            throw new NoSuchUserException(key.toString());
        } catch (ProvisioningException e) {
            throw new SSOIdentityException(e);
        }

    }

    public boolean userExists(UserKey key) throws SSOIdentityException {
        try {
            idPartition.findUserByUserName(key.toString());
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

        // TODO : Use configuraiton/reflexion to add more properties.

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
