package org.atricore.idbus.kernel.main.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class AbstractDBServerTest {

    private static final Log logger = LogFactory.getLog(AbstractDBServerTest.class);

    protected static PersistenceManagerFactory pmf;

    protected static NetworkServerControl derbyServer;

    @BeforeClass
    public static void setupClass() throws Exception {

        InetAddress address = InetAddress.getByName("localhost");

        // TODO : Extract to spring app. context
        System.setProperty("derby.system.home", "./target/derby/");
        derbyServer = new NetworkServerControl(address,
                1537,
                "atricore",
                "ádmin");

        derbyServer.start(new PrintWriter(System.out));

        pmf = JDOHelper.getPersistenceManagerFactory("datanucleus-tests.properties");

    }

    @AfterClass
    public static void tearDownClass() throws Exception{
        derbyServer.shutdown();
    }

    protected void assertEquivalent(BeansDefinition b1, BeansDefinition b2) {
        assert b1.getName().equals(b2.getName()) : "BeanDefinitions name do not match [" + b1.getName() + "/" + b2.getName() + "]";

        // TODO : Other attribues ...
        assertEquivalent(b1.getBeans(), b2.getBeans());

    }

    protected void assertEquivalent(Beans b1, Beans b2) {
        assert b1.getImportsAndAliasAndBeen().size() == b2.getImportsAndAliasAndBeen().size();

        // TODO : Add more conditions?

        // Will JDO respect the original order in the list ?
    }


    protected void assertEquivalent(Bean b1, Bean b2) {
        assert b1.getName().equals(b2.getName());
        assert b1.getClazz().equals(b2.getClazz());
        // TODO : more
    }


    protected static BeansDefinition importBeans(String beansDefinitionResource) throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(beansDefinitionResource);
        assert is != null  : "Beans resource not found " + beansDefinitionResource;

        Beans beans = BeanUtils.unmarshal(is);
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            BeansDefinition def = new BeansDefinition();
            def.setName(beansDefinitionResource);
            def.setBeans(beans);

            pm.makePersistent(def);

            tx.commit();

            return def;
        } finally {
            if (tx.isActive()) {
                logger.warn("Transaction rollback!");
                tx.rollback();
            }
        }

    }


}
