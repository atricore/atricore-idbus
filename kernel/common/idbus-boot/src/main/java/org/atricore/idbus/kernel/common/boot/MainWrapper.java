package org.atricore.idbus.kernel.common.boot;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class MainWrapper {

    public static void main(final String[] args) {
        try {

            // Register shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    System.out.println("in : run () : shutdownHook");
                    try {
                        org.apache.karaf.main.Stop.main(args);
                        // Give the framework time to stop
                        sleep(1000);
                    } catch (Exception e) {
                        System.err.print(e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

            // Start Karaf
            org.apache.karaf.main.Main.main(args);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(-1);
        }
    }



}
