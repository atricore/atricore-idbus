package org.atricore.idbus.kernel.main.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.derby.drda.NetworkServerControl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
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

    protected PersistenceManager pm;

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
        if (pmf != null)
            try { pmf.close(); } catch (Exception e) { /**/ }

        derbyServer.shutdown();
    }

    @Before
    public void setup() {
        pm  = pmf.getPersistenceManager();
    }

    @After
    public void teardonw() {
        if (pm != null)
            try { pm.close(); } catch (Exception e) { /**/}
    }



}
