package org.atricore.idbus.kernel.main.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.provisioning.domain.IdentityVault;
import org.atricore.idbus.kernel.main.provisioning.domain.dao.IdentityVaultDAO;
import org.atricore.idbus.kernel.main.provisioning.domain.dao.impl.IdentityVaultDAOImpl;
import org.junit.Before;
import org.junit.Test;

import javax.jdo.PersistenceManager;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class AccountManagementTest extends AbstractDBServerTest {

    private static final Log log = LogFactory.getLog(AccountManagementTest.class);


    @Before
    public void setup() {
    }

    @Test
    public void testIdentityVaultCRUD() throws Exception {

        IdentityVaultDAO vaultDao = new IdentityVaultDAOImpl(pmf);

        String name = "v1";
        String description = "Vault One";
        String host = "localhost";
        int port = 1528;
        String usr = "user1";
        String pwd = "user1pwd";

        IdentityVault vault = new IdentityVault();
        vault.setName(name);
        vault.setDescription(description);
        vault.setHost(host);
        vault.setPort(port);
        vault.setUsername(usr);
        vault.setPassword(pwd);

        vault = vaultDao.createObject(vault);

        assert vault != null;
        assert vault.getId() > 0;
        assert vault.getName().equals(name);

        log.info("Created Vault : " + vault.getId());

        long id = vault.getId();

        vault = vaultDao.findObjectById(id);

        assert vault != null;
        assert vault.getId() == id;
        assert vault.getName().equals(name);

        log.info("Retrieved Vault : " + vault.getId() + ":" + vault.getName());

        vault.setName(name + ".1");
        vault = vaultDao.updateObject(vault);

        assert vault != null;
        assert vault.getId() == id;
        assert vault.getName().equals(name + ".1");
        
        vault = vaultDao.findObjectById(id);



        log.info("Updated Vault : " + vault.getId() + ":" + vault.getName());

        vaultDao.deleteObject(vault);

        try {
            vault = vaultDao.findObjectById(id);
            assert false : "Vaul was not deleted";
        } catch (javax.jdo.JDOObjectNotFoundException e) {
            // OK
        }

        log.info("Deleted Vault : " + vault.getId() + ":" + vault.getName());


        IdentityVault vault2 = new IdentityVault();
        vault2.setName(name + ".2");
        vault2.setDescription(description);
        vault2.setHost(host);
        vault2.setPort(port);
        vault2.setUsername(usr);
        vault2.setPassword(pwd);

        vaultDao.createObject(vault2);

        IdentityVault vault3 = new IdentityVault();
        vault3.setName(name + ".3");
        vault3.setDescription(description);
        vault3.setHost(host);
        vault3.setPort(port);
        vault3.setUsername(usr);
        vault3.setPassword(pwd);

        vaultDao.createObject(vault3);

        Collection<IdentityVault> vaults = vaultDao.findAll();

        assert vaults != null;
        assert vaults.size() == 2 : "Found " + vaults.size();

        for (IdentityVault v : vaults) {
            assert v.getId() == vault3.getId() || v.getId() == vault2.getId(); 
        }

        
    }
}
