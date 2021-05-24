package org.atricore.idbus.idojos.dbidentitystore;

public class JDBCTest {

    public static void main(String[] args) throws Exception {

        try {
            if (args.length < 4) {
                System.err.println("Usage: JDBCIdentityStore driverName url username password [" + args.length + "]");
                for (int i = 0; i < args.length; i++) {
                    System.err.println(i + ":" + args[1]);
                }
                return;
            }
            JDBCIdentityStore store = new JDBCIdentityStore();
            store.setDriverName(args[0]);
            store.setConnectionURL(args[1]);
            store.setConnectionName(args[2]);
            store.setConnectionPassword(args[3]);

            store.getDBConnection();

        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (args.length >= 5 && args[4].equals("-v")) {
                e.printStackTrace();
            }

        }
    }

}
