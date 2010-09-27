package com.atricore.idbus.console.lifecycle.main.test;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceDeployment;
import com.atricore.idbus.console.lifecycle.main.domain.dao.IdentityApplianceDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.AddIdentityApplianceRequest;
import com.atricore.idbus.console.lifecycle.main.spi.request.DisposeIdentityApplianceRequest;
import com.atricore.idbus.console.lifecycle.main.spi.request.RemoveIdentityApplianceRequest;
import com.atricore.idbus.console.lifecycle.main.spi.request.UpdateIdentityApplianceRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.AddIdentityApplianceResponse;
import com.atricore.idbus.console.lifecycle.main.spi.response.DisposeIdentityApplianceResponse;
import com.atricore.idbus.console.lifecycle.main.spi.response.RemoveIdentityApplianceResponse;
import com.atricore.idbus.console.lifecycle.main.spi.response.UpdateIdentityApplianceResponse;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import static com.atricore.idbus.console.lifecycle.main.test.util.ApplianceAssert.*;


/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class TransactionalPersistenceTest {

    private IdentityApplianceDAO dao;

    private IdentityApplianceManagementService svc;

    public IdentityApplianceManagementService getSvc() {
        return svc;
    }

    public void setSvc(IdentityApplianceManagementService svc) {
        this.svc = svc;
    }

    public IdentityApplianceDAO getDao() {
        return dao;
    }

    public void setDao(IdentityApplianceDAO dao) {
        this.dao = dao;
    }

    public void testRemoveFC() {

    }

    public static void setupTestSuite() {

    }

    public static void tearDownTestSuite() {
    }


    public void setupTest() {

    }

    public void tearDownTest() {

    }

    @Transactional
    public void testAddAppliance() throws Exception{

        IdentityAppliance ida1 = newApplianceInstance("ida1", "ida1-add");

        AddIdentityApplianceRequest req = new AddIdentityApplianceRequest();
        req.setIdentityAppliance(ida1);

        AddIdentityApplianceResponse res =  svc.addIdentityAppliance(req);
        IdentityAppliance ida1Test = res.getAppliance();

        ida1 = newApplianceInstance("ida1", "ida1-add");

        // Let's check that we get what we sent !
        assertAppliancesAreEqual(ida1, ida1Test, true);
    }

    @Transactional
    public void testUpdaetAppliance() throws Exception {

        IdentityAppliance ida1 = newApplianceInstance("ida1", "ida1-update");

        // Change the name!
        // ida1-getIdApplianceDefinition().setName("ida11");

        AddIdentityApplianceRequest addReq = new AddIdentityApplianceRequest();
        addReq.setIdentityAppliance(ida1);

        AddIdentityApplianceResponse addRes =  svc.addIdentityAppliance(addReq);
        IdentityAppliance ida1Test = addRes.getAppliance();

        ida1 = newApplianceInstance("ida1", "ida1-update");

        UpdateIdentityApplianceRequest updReq = new UpdateIdentityApplianceRequest();
        updReq.setAppliance(ida1Test);

        UpdateIdentityApplianceResponse updRes = svc.updateIdentityAppliance(updReq);
        ida1Test = updRes.getAppliance();

        // Let's check that we get what we sent !
        assertAppliancesAreEqual(ida1, ida1Test, true);

    }

    @Transactional
    public void testDeleteAppliance() throws Exception{

        IdentityAppliance ida1 = newApplianceInstance("ida1", "ida1-delete");

        AddIdentityApplianceRequest req = new AddIdentityApplianceRequest();
        req.setIdentityAppliance(ida1);

        AddIdentityApplianceResponse res =  svc.addIdentityAppliance(req);
        IdentityAppliance ida1Test = res.getAppliance();

        ida1 = newApplianceInstance("ida1", "ida1-delete");

        // Let's check that we get what we sent !
        assertAppliancesAreEqual(ida1, ida1Test, true);

        DisposeIdentityApplianceRequest disposeReq = new DisposeIdentityApplianceRequest();
        disposeReq.setId(ida1Test.getId() + "");
        DisposeIdentityApplianceResponse disposeRes = svc.disposeIdentityAppliance(disposeReq);

        RemoveIdentityApplianceRequest removeReq = new RemoveIdentityApplianceRequest();
        removeReq.setApplianceId(ida1Test.getId() + "");

        RemoveIdentityApplianceResponse removeRes = svc.removeIdentityAppliance(removeReq);

    }

    // ------------------------------< Fine Grained Persistence tests >

    // 1. Remove SP
    @Transactional
    public void testRemoveSP() throws Exception {

        // New persited appliance
        IdentityAppliance ida = createAppliance(newApplianceInstance("ida1", "ida1-removeSP"));
        IdentityApplianceDefinition idad = ida.getIdApplianceDefinition();

        // Remove SP2
        removeProvider(ida, "sp1");
        IdentityAppliance idaTest = updateAppliance(ida);

        ida = newApplianceInstance("ida1", "ida1-removeSP");
        removeProvider(ida, "sp1");
        assertAppliancesAreEqual(ida, idaTest, true);

    }

    // ------------------------------< test utils >

    protected Provider findProvider(IdentityAppliance ida, String name) {
        for (Provider p : ida.getIdApplianceDefinition().getProviders()) {
            if (p.getName().equals(name))
                return p;
        }
        return null;
    }

    /**
     * Removing a provider implies some work on provider connections!
     * @param ida
     * @param name
     */
    protected void removeProvider(IdentityAppliance ida, String name) {

        IdentityApplianceDefinition idad = ida.getIdApplianceDefinition();
        FederatedProvider fp = (FederatedProvider) findProvider(ida, name);
        assert fp != null : "No provider found with name " + name;

        // Exec envs
        if (fp instanceof ServiceProvider) {
            ServiceProvider sp = (ServiceProvider) fp;

            if (sp.getActivation() != null) {
                ExecutionEnvironment execEnv1 = sp.getActivation().getExecutionEnv();
                execEnv1.getActivations().remove(sp.getActivation());
                sp.getActivation().setExecutionEnv(null);
                sp.setActivation(null);
            }
        }

        // Identity Looup
        if (fp.getIdentityLookup() != null) {
            // Nothing to do here
        }

        // Federated connections
        for (FederatedConnection fcA : fp.getFederatedConnectionsA()) {
            FederatedProvider p = fcA.getRoleB();
            int size = p.getFederatedConnectionsB().size();
            p.getFederatedConnectionsB().remove(fcA);
            assert size == p.getFederatedConnectionsB().size() + 1;
            fcA.setRoleB(null);
        }

        for (FederatedConnection fcB : fp.getFederatedConnectionsB()) {
            FederatedProvider p = fcB.getRoleA();
            int size = p.getFederatedConnectionsA().size();
            p.getFederatedConnectionsA().remove(fcB);
            assert size == p.getFederatedConnectionsA().size() + 1;
            fcB.setRoleA(null);
        }


        idad.getProviders().remove(fp);

    }

    protected IdentityAppliance updateAppliance(IdentityAppliance a) throws IdentityServerException {
        UpdateIdentityApplianceRequest updReq = new UpdateIdentityApplianceRequest();
        updReq.setAppliance(a);

        UpdateIdentityApplianceResponse updRes = svc.updateIdentityAppliance(updReq);
        return updRes.getAppliance();
    }

    protected IdentityAppliance createAppliance(IdentityAppliance a) throws IdentityServerException {
        AddIdentityApplianceRequest req = new AddIdentityApplianceRequest();
        req.setIdentityAppliance(a);

        AddIdentityApplianceResponse res =  svc.addIdentityAppliance(req);
        IdentityAppliance ida1Test = res.getAppliance();

        return ida1Test;
    }

    protected IdentityAppliance newApplianceInstance(String name, String newName) {
        IdentityAppliance a = newApplianceInstance(name);
        a.getIdApplianceDefinition().setName(newName);
        return a;
    }


    protected IdentityAppliance newApplianceInstance(String name) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/com/atricore/idbus/console/lifecycle/main/test/appliance-model-beans.xml");
        return (IdentityAppliance) ctx.getBean(name);
    }

}
