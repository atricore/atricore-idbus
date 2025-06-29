package org.atricore.idbus.bundles.ehcache.test;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import java.net.URL; // For demonstrating loading by URL if preferred, though create() is easier

public class EhcacheExpirationCheck {

    public static void main(String[] args) {
        CacheManager cacheManager = null; // Initialize to null
        try {
            // Option 1 (Preferred for default ehcache.xml):
            // CacheManager.create() automatically looks for ehcache.xml in the classpath root.
            System.out.println("Attempting to load ehcache.xml from classpath using CacheManager.create()");
            cacheManager = CacheManager.create();

            // Option 2 (If you want to explicitly specify the resource path or stream):
            // URL ehcacheConfigFile = EhcacheExpirationCheck.class.getClassLoader().getResource("ehcache.xml");
            // if (ehcacheConfigFile == null) {
            //     System.err.println("Error: ehcache.xml not found in classpath!");
            //     return;
            // }
            // System.out.println("Loading ehcache.xml from: " + ehcacheConfigFile.getPath());
            // cacheManager = CacheManager.create(ehcacheConfigFile);


            Cache cache = cacheManager.getCache("myTestCache"); // Get our specific cache

            if (cache == null) {
                System.err.println("Error: Cache 'myTestCache' not found. Please ensure it's defined in ehcache.xml.");
                return;
            }

            System.out.println("\n--- Starting Ehcache Expiration Test ---");
            System.out.println("Default cache (from ehcache.xml) TTI: 5s, TTL: 10s");
            System.out.println("myTestCache (our specific cache from ehcache.xml) TTI: 0s (infinite), TTL: 15s");


            // --- Test Case 1: Element using myTestCache's default (0 TTI, 15 TTL) ---
            String key1 = "elementWithCacheDefaults";
            Element element1 = new Element(key1, "value1");
            cache.put(element1);
            System.out.println("\nPutting element: '" + key1 + "' (using cache defaults: TTI=0, TTL=15)");

            try {
                System.out.println("Checking '" + key1 + "' after 5 seconds...");
                Thread.sleep(5 * 1000); // Wait 5 seconds
                Element retrieved1 = cache.get(key1); // Access it
                if (retrieved1 != null) {
                    System.out.println("Still in cache: '" + key1 + "' (Expected: Yes, TTI=0, not yet TTL)");
                } else {
                    System.out.println("NOT in cache: '" + key1 + "' (Unexpected, TTI=0)");
                }

                System.out.println("Checking '" + key1 + "' after total 16 seconds (past TTL)...");
                Thread.sleep(11 * 1000); // Wait another 11 seconds (total 16s)
                retrieved1 = cache.get(key1); // Attempt to retrieve, this will trigger eviction check
                if (retrieved1 != null) {
                    System.out.println("Still in cache: '" + key1 + "' (Unexpected, should be expired by TTL)");
                } else {
                    System.out.println("NOT in cache: '" + key1 + "' (Expected: Yes, expired by TTL)");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted: " + e.getMessage());
            }

            // --- Test Case 2: Element with custom expiration overriding cache defaults ---
            String key2 = "elementWithCustomExpiration";
            // Custom Element: timeToIdleSeconds=7 (active), timeToLiveSeconds=12 (shorter than myTestCache's 15s TTL)
            Element element2 = new Element(key2, "value2", 7, 12);
            cache.put(element2);
            System.out.println("\nPutting element: '" + key2 + "' (custom settings: TTI=7, TTL=12)");

            try {
                System.out.println("Checking '" + key2 + "' after 6 seconds (before custom TTI/TTL)...");
                Thread.sleep(6 * 1000); // Wait 6 seconds
                Element retrieved2 = cache.get(key2);
                if (retrieved2 != null) {
                    System.out.println("Still in cache: '" + key2 + "' (Expected: Yes, not yet expired)");
                } else {
                    System.out.println("NOT in cache: '" + key2 + "' (Unexpected)");
                }

                System.out.println("Checking '" + key2 + "' after total 8 seconds (past custom TTI)...");
                Thread.sleep(2 * 1000); // Wait another 2 seconds (total 8s)
                retrieved2 = cache.get(key2); // Accessing it might reset TTI or trigger check
                if (retrieved2 != null) {
                    System.out.println("Still in cache: '" + key2 + "' (Unexpected, should be expired by TTI)");
                } else {
                    System.out.println("NOT in cache: '" + key2 + "' (Expected: Yes, expired by TTI)");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted: " + e.getMessage());
            }

            // --- Test Case 3: Element with custom expiration making it eternal ---
            String key3 = "eternalElement";
            // Custom Element: eternal=true (overrides any TTI/TTL)
            Element element3 = new Element(key3, "value3", true);
            cache.put(element3);
            System.out.println("\nPutting element: '" + key3 + "' (custom settings: eternal=true)");

            try {
                System.out.println("Checking '" + key3 + "' after 20 seconds (should be eternal)...");
                Thread.sleep(20 * 1000); // Wait a long time
                Element retrieved3 = cache.get(key3);
                if (retrieved3 != null) {
                    System.out.println("Still in cache: '" + key3 + "' (Expected: Yes, eternal)");
                } else {
                    System.out.println("NOT in cache: '" + key3 + "' (Unexpected, eternal element evicted)");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted: " + e.getMessage());
            }

        } finally {
            if (cacheManager != null) {
                System.out.println("\n--- Ehcache Expiration Test Complete. Shutting down CacheManager ---");
                cacheManager.shutdown();
            }
        }
    }
}