package org.atricore.idbus.kernel.main.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.IdentityPartitionDAO;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.IdentityVaultDAO;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl.IdentityPartitionDAOImpl;
import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl.IdentityVaultDAOImpl;
import org.atricore.idbus.kernel.main.provisioning.spi.IdentityPartition;
import org.atricore.idbus.kernel.main.provisioning.spi.IdentityVault;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AccountManagementTest extends AbstractDBServerTest {

    private static final Log log = LogFactory.getLog(AccountManagementTest.class);

    @Before
    public void setup() {
        super.setup();
        IdentityVaultDAO vaultDao = new IdentityVaultDAOImpl(pm);
        IdentityPartitionDAO partitionDao = new IdentityPartitionDAOImpl(pm);

        for (IdentityVault vault : vaultDao.findAll()) {
            vaultDao.deleteObject(vault);
        }

        for (IdentityPartition partition : partitionDao.findAll()) {
            partitionDao.deleteObject(partition);
        }


    }

    @Test
    public void testIdentityVaultCRUD() throws Exception {

        IdentityVaultDAO vaultDao = new IdentityVaultDAOImpl(pm);
        IdentityPartitionDAO partitionDao = new IdentityPartitionDAOImpl(pm);

        String p1Name = "p1";
        String p1Description = "Partition One";

        IdentityPartition partition = new IdentityPartition();
        partition.setName(p1Name);
        partition.setDescription(p1Description);
        List<IdentityPartition> parts = new ArrayList<IdentityPartition>();
        parts.add(partition);

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
        vault.setPartitions(parts);

        vault = vaultDao.createObject(vault);

        assert vault != null;
        assert vault.getId() > 0;
        assert vault.getName().equals(name);
        assert vault.getPartitions() != null;
        assert vault.getPartitions().size() == 1;

        for (IdentityPartition p : vault.getPartitions()) {
            assert p.getVault() != null;
            assert p.getVault().getId() == vault.getId();
        }

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
    
    @Test
    public void testIdentityPartitionCRUD() throws Exception {

        IdentityVaultDAO vaultDao = new IdentityVaultDAOImpl(pm);

        IdentityPartitionDAO partitionDao = new IdentityPartitionDAOImpl(pm);

        String v2Name = "v2";
        String v2Description = "Vault Two";
        String v2Host = "localhost-2";
        int v2Port = 2222;
        String v2Usr = "user2";
        String v2Pwd = "user2pwd";

        IdentityVault vault2 = new IdentityVault();
        vault2.setName(v2Name + ".2");
        vault2.setDescription(v2Description);
        vault2.setHost(v2Host);
        vault2.setPort(v2Port);
        vault2.setUsername(v2Usr);
        vault2.setPassword(v2Pwd);

        vaultDao.createObject(vault2);

        String v3Name = "v3";
        String v3Description = "Vault Three";
        String v3Host = "localhost-3";
        int v3Port = 3333;
        String v3Usr = "user3";
        String v3Pwd = "user3pwd";


        IdentityVault vault3 = new IdentityVault();
        vault3.setName(v3Name);
        vault3.setDescription(v3Description);
        vault3.setHost(v3Host);
        vault3.setPort(v3Port);
        vault3.setUsername(v3Usr);
        vault3.setPassword(v3Pwd);

        vaultDao.createObject(vault3);

        String name = "v1";
        String description = "Partition One";

        IdentityPartition partition = new IdentityPartition();
        partition.setName(name);
        partition.setDescription(description);
        partition.setVault(vault2);
        vault2.getPartitions().add(partition);

        partition = partitionDao.createObject(partition);

        assert partition != null;
        assert partition.getId() > 0;
        assert partition.getName().equals(name);
        assert partition.getVault() != null;
        assert partition.getVault().getId() == vault2.getId();

        log.info("Created Partition : " + partition.getId());

        long id = partition.getId();

        partition = partitionDao.findObjectById(id);

        assert partition != null;
        assert partition.getId() == id;
        assert partition.getName().equals(name);
        assert partition.getVault() != null;
        assert partition.getVault().getId() == vault2.getId();

        log.info("Retrieved Partition : " + partition.getId() + ":" + partition.getName());

        partition.setName(name + ".1");
        partition.setVault(vault3);
        partition = partitionDao.updateObject(partition);

        assert partition != null;
        assert partition.getId() == id;
        assert partition.getName().equals(name + ".1");
        assert partition.getVault() != null;
        assert partition.getVault().getId() == vault3.getId();

        partition = partitionDao.findObjectById(id);

        log.info("Updated Partition : " + partition.getId() + ":" + partition.getName());

        partitionDao.deleteObject(partition);

        try {
            partition = partitionDao.findObjectById(id);
            assert false : "Vaul was not deleted";
        } catch (javax.jdo.JDOObjectNotFoundException e) {
            // OK
        }

        log.info("Deleted Partition : " + partition.getId() + ":" + partition.getName());

        IdentityPartition partition2 = new IdentityPartition();
        partition2.setName(name + ".2");
        partition2.setDescription(description);

        partitionDao.createObject(partition2);

        IdentityPartition partition3 = new IdentityPartition();
        partition3.setName(name + ".3");
        partition3.setDescription(description);

        partitionDao.createObject(partition3);

        Collection<IdentityPartition> partitions = partitionDao.findAll();

        assert partitions != null;
        assert partitions.size() == 2 : "Found " + partitions.size();

        for (IdentityPartition v : partitions) {
            assert v.getId() == partition3.getId() || v.getId() == partition2.getId(); 
        }

        
    }
    
}
