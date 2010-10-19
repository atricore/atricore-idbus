package org.atricore.idbus.kernel.common.boot;


/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class Bootstrap {

    public static void main(String[] args) {
        try {
            org.apache.karaf.main.Main.main(args);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(-1);
        }
    }

    public static org.apache.karaf.main.Main launch(String[] args) throws Exception {
        org.apache.karaf.main.Main main = new org.apache.karaf.main.Main(args);
        main.launch();
        return main;
    }

}
