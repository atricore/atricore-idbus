package com.atricore.idbus.console.brandservice.main.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class BrandingUtil {
    
    /**
     * Get a bundles list with the name or symbolic name matching the pattern.
     *
     * @param name the bundle name or symbolic name pattern to match.
     * @return the bundles list.
     */
    public static List<Bundle> getBundleByName(BundleContext bundleContext, String name) {
        return getBundleByNameAndVersion(bundleContext, name, null);
    }

    public static List<Bundle> getBundleByHeader(BundleContext bundleContext, String hName, String hValue) {
        Bundle[] bundles = bundleContext.getBundles();
        ArrayList<Bundle> result = new ArrayList<Bundle>();
        for (int i = 0; i < bundles.length; i++) {
            String v = (String) bundles[i].getHeaders().get(hName);
            if (v != null && v.equals(hValue)) {
                result.add(bundles[i]);
            }
        }

        // Sort bundles by ID
        Comparator<Bundle> c = new Comparator<Bundle>() {
            public int compare(Bundle o1, Bundle o2) {
                return (int) (o1.getBundleId() - o2.getBundleId());
            }
        };

        Collections.sort(result, c);

        return result;
    }

    /**
     * Get a bundles list with the name or symbolic name matching the name pattern and version matching the version pattern.
     *
     * @param name    the bundle name or symbolic name regex to match.
     * @param version the bundle version regex to match.
     * @return the bundles list.
     */
    public static List<Bundle> getBundleByNameAndVersion(BundleContext bundleContext, String name, String version) {
        Bundle[] bundles = bundleContext.getBundles();

        ArrayList<Bundle> result = new ArrayList<Bundle>();

        Pattern namePattern = Pattern.compile(name);

        for (int i = 0; i < bundles.length; i++) {

            String bundleSymbolicName = bundles[i].getSymbolicName();
            // skip bundles without Bundle-SymbolicName header
            if (bundleSymbolicName == null) {
                continue;
            }
            
            Matcher symbolicNameMatcher = namePattern.matcher(bundleSymbolicName);
            
            Matcher nameMatcher = null;
            String bundleName = (String) bundles[i].getHeaders().get(Constants.BUNDLE_NAME);
            if (bundleName != null) {
                nameMatcher = namePattern.matcher(bundleName);
            }

            if (version != null) {
                String bundleVersion = (String) bundles[i].getHeaders().get(Constants.BUNDLE_VERSION);
                if (bundleVersion != null) {
                    boolean nameMatch = (nameMatcher != null && nameMatcher.find()) || symbolicNameMatcher.find();
                    if (nameMatch) {
                        Pattern versionPattern = Pattern.compile(version);
                        Matcher versionMatcher = versionPattern.matcher(bundleVersion);                    
                        if (versionMatcher.find()) {
                            result.add(bundles[i]);
                        }
                    }
                }
            } else {
                boolean nameMatch = (nameMatcher != null && nameMatcher.find()) || symbolicNameMatcher.find();
                if (nameMatch) {
                    result.add(bundles[i]);
                }
            }
        }
        return result;
    }
    
}
