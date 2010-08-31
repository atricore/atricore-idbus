package org.atricore.idbus.connectors.jdoidentityvault;

import org.atricore.idbus.connectors.jdoidentityvault.domain.JDOUser;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOGroupDAO;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.JDOUserDAO;
import org.atricore.idbus.kernel.main.authn.scheme.UsernameCredential;
import org.atricore.idbus.kernel.main.provisioning.domain.Group;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.authn.*;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;
import org.atricore.idbus.kernel.main.store.AbstractStore;
import org.atricore.idbus.kernel.main.store.UserKey;
import org.atricore.idbus.kernel.main.store.exceptions.NoSuchUserException;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOIdentityStore extends AbstractStore
{

    private JDOIdentityPartition idPartition;


    public JDOIdentityPartition getIdPartition() {
        return idPartition;
    }

    public void setIdPartition(JDOIdentityPartition idPartition) {
        this.idPartition = idPartition;
    }

    public Credential[] loadCredentials(CredentialKey key, CredentialProvider cp) throws SSOIdentityException {

        try {

            User user = idPartition.findUserByUserName(key.toString());
            // TODO : Support other type of credentials ?

            Credential usrCred = cp.newCredential("username", user.getUserName());
            Credential pwdCred = cp.newCredential("password", user.getUserPassword());

            return new Credential[] {usrCred, pwdCred};
        } catch (ProvisioningException e) {
            throw new SSOIdentityException(e);
        }
    }

    public BaseUser loadUser(UserKey key) throws NoSuchUserException, SSOIdentityException {

        try {
            User jdoUser = idPartition.findUserByUserName(key.toString());
            return toSSOUser(jdoUser);
        } catch (ProvisioningException e) {
            throw new SSOIdentityException(e);
        }
    }

    public BaseRole[] findRolesByUserKey(UserKey key) throws SSOIdentityException {

        try {
            Collection<Group> groups = idPartition.findGroupsByUsernName(key.toString());
            return toSSORole(groups);
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
        // Todo add JDO User properties as SSOUser properties ?
        return ssoUser;

    }

    protected BaseRole[] toSSORole(Collection<Group> groups) {

        if (groups == null)
            return new BaseRole[0];
        
        BaseRole[] ssoRoles = new BaseRole[groups.size()];
        for (Group group : groups) {
            BaseRole ssoRole = new BaseRoleImpl();
            ssoRole.setName(group.getName());
        }

        return ssoRoles;

    }
}
