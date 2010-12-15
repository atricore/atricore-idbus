package org.atricore.idbus.bundles.apache.tiles;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tiles.Definition;
import org.apache.tiles.definition.DefinitionsFactoryException;
import org.apache.tiles.definition.dao.ResolvingLocaleUrlDefinitionDAO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OsgiDefinitionDAO extends ResolvingLocaleUrlDefinitionDAO {
    
    private static final Log log = LogFactory.getLog(OsgiDefinitionDAO.class);
    
    /**
     * Loads definitions from an URL without loading from "parent" URLs.
     *
     * @param url The URL to read.
     * @return The definition map that has been read.
     */
    @Override
    protected Map<String, Definition> loadDefinitionsFromURL(URL url) {
        Map<String, Definition> defsMap = null;
        try {
            URLConnection connection = url.openConnection();
            connection.connect();
            lastModifiedDates.put(url.toExternalForm(), connection
                    .getLastModified());

            // Definition must be collected, starting from the base
            // source up to the last localized file.
            defsMap = reader.read(connection.getInputStream());
        } catch (IOException e) {
            // File not found. continue.
            if (log.isDebugEnabled()) {
                log.debug("File " + null + " not found, continue [" + e.getMessage() + "]");
            }
        } 

        return defsMap;
    }
}
