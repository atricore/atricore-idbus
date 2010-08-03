/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.bundles.apache.tiles;

import org.apache.tiles.definition.*;
import org.apache.tiles.definition.digester.DigesterDefinitionsReader;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.Definition;
import org.apache.tiles.TilesException;
import org.apache.tiles.util.ClassUtil;
import org.apache.tiles.locale.LocaleResolver;
import org.apache.tiles.locale.impl.DefaultLocaleResolver;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class OsgiDefinitionsFactory implements DefinitionsFactory, ReloadableDefinitionsFactory {

    /**
     * LOG instance for all UrlDefinitionsFactory instances.
     */
    private static final Log logger = LogFactory.getLog(UrlDefinitionsFactory.class);

    /**
     * Contains the URL objects identifying where configuration data is found.
     */
    protected List<Object> sources;

    /**
     * Reader used to get definitions from the sources.
     */
    protected DefinitionsReader reader;

    /**
     * Contains the dates that the URL sources were last modified.
     */
    protected Map<String, Long> lastModifiedDates;

    /**
     * Contains a list of locales that have been processed.
     */
    private List<Locale> processedLocales;


    /**
     * The definitions holder object.
     */
    private Definitions definitions;

    /**
     * The locale resolver object.
     */
    private LocaleResolver localeResolver;

    /**
     * Creates a new instance of UrlDefinitionsFactory.
     */
    public OsgiDefinitionsFactory() {

        if (logger.isDebugEnabled())
           logger.debug("Creating Initializing factory");

        sources = new ArrayList<Object>();
        lastModifiedDates = new HashMap<String, Long>();
        processedLocales = new ArrayList<Locale>();
    }

    /**
     * Initializes the DefinitionsFactory and its subcomponents.
     * <p/>
     * Implementations may support configuration properties to be passed in via
     * the params Map.
     *
     * @param params The Map of configuration properties.
     * @throws org.apache.tiles.TilesException if an initialization error occurs.
     */
    public void init(Map<String, String> params) throws TilesException {

        if (logger.isDebugEnabled())
           logger.debug("Initializing factory");

        String readerClassName =
            params.get(DefinitionsFactory.READER_IMPL_PROPERTY);

        if (readerClassName != null) {
            reader = (DefinitionsReader) ClassUtil.instantiate(readerClassName);
        } else {
            reader = new DigesterDefinitionsReader();
        }
        reader.init(params);

        String resolverClassName = params
                .get(DefinitionsFactory.LOCALE_RESOLVER_IMPL_PROPERTY);
        if (resolverClassName != null) {
            localeResolver = (LocaleResolver) ClassUtil.instantiate(resolverClassName);
        } else {
            localeResolver = new DefaultLocaleResolver();
        }
        localeResolver.init(params);
        definitions = readDefinitions();
    }

    /**
     * Returns the definitions holder object.
     *
     * @return The definitions holder.
     * @throws DefinitionsFactoryException If something goes wrong during
     * reading definitions.
     */
    protected Definitions getDefinitions()
        throws DefinitionsFactoryException {
        return definitions;
    }


    /**
     * Returns a Definition object that matches the given name and
     * Tiles context.
     *
     * @param name         The name of the Definition to return.
     * @param tilesContext The Tiles context to use to resolve the definition.
     * @return the Definition matching the given name or null if none
     *         is found.
     * @throws DefinitionsFactoryException if an error occurs reading definitions.
     */
    public Definition getDefinition(String name,
                                             TilesRequestContext tilesContext)
        throws DefinitionsFactoryException {

        if (logger.isDebugEnabled())
            logger.debug("Looking for definitioon " + name + ", tiles context:" + tilesContext);

        Definitions definitions = getDefinitions();
        Locale locale = null;

        if (tilesContext != null) {
            locale = localeResolver.resolveLocale(tilesContext);

            if (logger.isDebugEnabled())
                logger.debug("Looking for definitioon " + name +
                        ", locale:" + locale +
                ", isContextProcessed(tilesContext):" + isContextProcessed(tilesContext));

            if (!isContextProcessed(tilesContext)) {
                synchronized (definitions) {
                    addDefinitions(definitions, tilesContext);
                }
            }
        }

        return definitions.getDefinition(name, locale);
    }

    /**
     * Adds a source where Definition objects are stored.
     * <p/>
     * Implementations should publish what type of source object they expect.
     * The source should contain enough information to resolve a configuration
     * source containing definitions.  The source should be a "base" source for
     * configurations.  Internationalization and Localization properties will be
     * applied by implementations to discriminate the correct data sources based
     * on locale.
     *
     * @param source The configuration source for definitions.
     * @throws DefinitionsFactoryException if an invalid source is passed in or
     *                                     an error occurs resolving the source to an actual data store.
     */
    public void addSource(Object source) throws DefinitionsFactoryException {

        if (logger.isDebugEnabled())
            logger.debug("Adding source " + source);

        if (source == null) {
            throw new DefinitionsFactoryException(
                "Source object must not be null");
        }

        if (!(source instanceof URL)) {
            throw new DefinitionsFactoryException(
                "Source object must be an URL");
        }

        sources.add(source);
    }

    /**
     * Appends locale-specific {@link Definition} objects to an existing
     * {@link Definitions} set by reading locale-specific versions of
     * the applied sources.
     *
     * @param definitions  The Definitions object to append to.
     * @param tilesContext The requested locale.
     * @throws DefinitionsFactoryException if an error occurs reading definitions.
     */
    protected void addDefinitions(Definitions definitions,
                                  TilesRequestContext tilesContext)
        throws DefinitionsFactoryException {

        Locale locale = localeResolver.resolveLocale(tilesContext);

        if (logger.isDebugEnabled())
            logger.debug("Adding definitios for locale " + locale);

        if (isContextProcessed(tilesContext)) {
            if (logger.isDebugEnabled())
                logger.debug("isContextProcessed(tilesContext):true, returning");

            return;
        }

        if (locale == null) {
            if (logger.isDebugEnabled())
                logger.debug("locale == null, returning");
            return;
        }

        processedLocales.add(locale);
        List<String> postfixes = calculatePostfixes(locale);

        if (logger.isDebugEnabled())
            logger.debug("Processing postfixes:" + (postfixes != null ? postfixes.size() : "null"));

        Map<String, Definition> localeDefsMap = new HashMap<String, Definition>();
        for (Object postfix : postfixes) {

            if (logger.isDebugEnabled())
                logger.debug("Processing postfix [" + postfix + "] for sources:" + (sources != null ? sources.size() : "null"));

            // For each postfix, all the sources must be loaded.
            for (Object source : sources) {
                
                URL url = (URL) source;
                String path = url.toExternalForm();
                String newPath = concatPostfix(path, (String) postfix);

                if (logger.isDebugEnabled())
                    logger.debug("Adding source definition : " + newPath);

                try {
                    URL newUrl = new URL(newPath);
                    URLConnection connection = newUrl.openConnection();
                    connection.connect();
                    if (logger.isDebugEnabled())
                        logger.debug("Loding definition from URL:" + newUrl.toExternalForm());

                    lastModifiedDates.put(newUrl.toExternalForm(),
                        connection.getLastModified());

                    // Definition must be collected, starting from the base
                    // source up to the last localized file.
                    Map<String, Definition> defsMap = reader
                            .read(connection.getInputStream());
                    if (defsMap != null) {
                        localeDefsMap.putAll(defsMap);
                    }
                } catch (FileNotFoundException e) {
                    // File not found. continue.
                    if (logger.isDebugEnabled()) {
                        logger.debug("File " + newPath + " not found, continue");
                    }
                } catch (IOException e) {
                    // Assume I/O Exception is a Not Found error ?
                    /*
                    throw new DefinitionsFactoryException(
                        "I/O error processing configuration.", e);
                        */
                    if (logger.isDebugEnabled())
                        logger.debug("I/O error processing configuration " + newPath + ":" + e.getMessage(), e);
                }
            }
        }

        // At the end of definitions loading, they can be assigned to
        // Definitions implementation, to allow inheritance resolution.
        definitions.addDefinitions(localeDefsMap, localeResolver
                .resolveLocale(tilesContext));
    }

    /**
     * Creates and returns a {@link Definitions} set by reading
     * configuration data from the applied sources.
     *
     * @return The definitions holder object, filled with base definitions.
     * @throws DefinitionsFactoryException if an error occurs reading the
     * sources.
     */
    public Definitions readDefinitions()
        throws DefinitionsFactoryException {
        Definitions definitions = createDefinitions();
        try {
            for (Object source1 : sources) {
                URL source = (URL) source1;
                URLConnection connection = source.openConnection();
                connection.connect();
                lastModifiedDates.put(source.toExternalForm(),
                    connection.getLastModified());
                Map<String, Definition> defsMap = reader
                        .read(connection.getInputStream());
                definitions.addDefinitions(defsMap);
            }
        } catch (IOException e) {
            throw new DefinitionsFactoryException("I/O error accessing source.", e);
        }
        return definitions;
    }

    /**
     * Indicates whether a given context has been processed or not.
     * <p/>
     * This method can be used to avoid unnecessary synchronization of the
     * DefinitionsFactory in multi-threaded situations.  Check the return of
     * isContextProcessed before synchronizing the object and reading
     * locale-specific definitions.
     *
     * @param tilesContext The Tiles context to check.
     * @return true if the given context has been processed and false otherwise.
     */
    protected boolean isContextProcessed(TilesRequestContext tilesContext) {
        return processedLocales.contains(localeResolver
                .resolveLocale(tilesContext));
    }

    /**
     * Creates a new instance of <code>Definitions</code>. Override this method
     * to provide your custom instance of Definitions.
     *
     * @return A new instance of <code>Definitions</code>.
     */
    protected Definitions createDefinitions() {
        return new DefinitionsImpl();
    }

    /**
     * Concat postfix to the name. Take care of existing filename extension.
     * Transform the given name "name.ext" to have "name" + "postfix" + "ext".
     * If there is no ext, return "name" + "postfix".
     *
     * @param name    Filename.
     * @param postfix Postfix to add.
     * @return Concatenated filename.
     */
    protected String concatPostfix(String name, String postfix) {
        if (postfix == null) {
            return name;
        }

        // Search file name extension.
        // take care of Unix files starting with .
        int dotIndex = name.lastIndexOf(".");
        int lastNameStart = name.lastIndexOf(java.io.File.pathSeparator);
        if (dotIndex < 1 || dotIndex < lastNameStart) {
            return name + postfix;
        }

        String ext = name.substring(dotIndex);
        name = name.substring(0, dotIndex);
        return name + postfix + ext;
    }

    /**
     * Calculate the postfixes along the search path from the base bundle to the
     * bundle specified by baseName and locale.
     * Method copied from java.util.ResourceBundle
     *
     * @param locale the locale
     * @return a list of
     */
    protected static List<String> calculatePostfixes(Locale locale) {
        final List<String> result = new ArrayList<String>();
        final String language = locale.getLanguage();
        final int languageLength = language.length();
        final String country = locale.getCountry();
        final int countryLength = country.length();
        final String variant = locale.getVariant();
        final int variantLength = variant.length();

        // The default configuration file must be loaded to allow correct
        // definition inheritance.
        result.add("");
        if (languageLength + countryLength + variantLength == 0) {
            //The locale is "", "", "".
            return result;
        }

        final StringBuffer temp = new StringBuffer();
        temp.append('_');
        temp.append(language);

        if (languageLength > 0) {
            result.add(temp.toString());
        }

        if (countryLength + variantLength == 0) {
            return result;
        }

        temp.append('_');
        temp.append(country);

        if (countryLength > 0) {
            result.add(temp.toString());
        }

        if (variantLength == 0) {
            return result;
        } else {
            temp.append('_');
            temp.append(variant);
            result.add(temp.toString());
            return result;
        }
    }


    /** {@inheritDoc} */
    public void refresh() throws DefinitionsFactoryException {
        logger.debug("Updating Tiles definitions. . .");
        synchronized (definitions) {
            Definitions newDefs = readDefinitions();
            definitions.reset();
            definitions.addDefinitions(newDefs.getBaseDefinitions());
        }
    }


    /**
     * Indicates whether the DefinitionsFactory is out of date and needs to be
     * reloaded.
     *
     * @return If the factory needs refresh.
     */
    public boolean refreshRequired() {
        boolean status = false;

        Set<String> urls = lastModifiedDates.keySet();

        try {
            for (String urlPath : urls) {
                Long lastModifiedDate = lastModifiedDates.get(urlPath);
                URL url = new URL(urlPath);
                URLConnection connection = url.openConnection();
                connection.connect();
                long newModDate = connection.getLastModified();
                if (newModDate != lastModifiedDate) {
                    status = true;
                    break;
                }
            }
        } catch (Exception e) {
            logger.warn("Exception while monitoring update times.", e);
            return true;
        }
        return status;
    }
}
