package org.atricore.idbus.bundles.apache.tiles;

import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.awareness.TilesApplicationContextAware;
import org.apache.tiles.context.TilesRequestContextFactory;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.definition.DefinitionsFactoryException;
import org.apache.tiles.definition.DefinitionsReader;
import org.apache.tiles.definition.Refreshable;
import org.apache.tiles.definition.dao.BaseLocaleUrlDefinitionDAO;
import org.apache.tiles.definition.digester.DigesterDefinitionsReader;
import org.apache.tiles.factory.AbstractTilesContainerFactory;
import org.apache.tiles.factory.BasicTilesContainerFactory;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.impl.mgmt.CachingTilesContainer;
import org.apache.tiles.locale.LocaleResolver;
import org.apache.tiles.preparer.PreparerFactory;
import org.apache.tiles.startup.BasicTilesInitializer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.view.tiles2.SpringLocaleResolver;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OsgiTilesInitializer extends BasicTilesInitializer {

    private boolean useMutableTilesContainer;

    private String[] definitions;

    private boolean validateDefinitions = true;

    private Class<? extends DefinitionsFactory> definitionsFactoryClass;

    private Class<? extends PreparerFactory> preparerFactoryClass;

    public boolean isUseMutableTilesContainer() {
        return useMutableTilesContainer;
    }

    public void setUseMutableTilesContainer(boolean useMutableTilesContainer) {
        this.useMutableTilesContainer = useMutableTilesContainer;
    }

    public String[] getDefinitions() {
        return definitions;
    }

    public void setDefinitions(String[] definitions) {
        this.definitions = definitions;
    }

    public boolean isValidateDefinitions() {
        return validateDefinitions;
    }

    public void setValidateDefinitions(boolean validateDefinitions) {
        this.validateDefinitions = validateDefinitions;
    }

    public Class<? extends DefinitionsFactory> getDefinitionsFactoryClass() {
        return definitionsFactoryClass;
    }

    public void setDefinitionsFactoryClass(Class<? extends DefinitionsFactory> definitionsFactoryClass) {
        this.definitionsFactoryClass = definitionsFactoryClass;
    }

    public Class<? extends PreparerFactory> getPreparerFactoryClass() {
        return preparerFactoryClass;
    }

    public void setPreparerFactoryClass(Class<? extends PreparerFactory> preparerFactoryClass) {
        this.preparerFactoryClass = preparerFactoryClass;
    }

    @Override
    protected AbstractTilesContainerFactory createContainerFactory(TilesApplicationContext context) {
        return new OsgiTilesContainerFactory();
    }

    private class OsgiTilesContainerFactory extends BasicTilesContainerFactory {

        @Override
        protected BasicTilesContainer instantiateContainer(TilesApplicationContext context) {
            return (useMutableTilesContainer ? new CachingTilesContainer() : new BasicTilesContainer());
        }

        @Override
        protected void registerRequestContextFactory(String className,
                                                     List<TilesRequestContextFactory> factories, TilesRequestContextFactory parent) {
            // Avoid Tiles 2.2 warn logging when default RequestContextFactory impl class not found
            if (ClassUtils.isPresent(className, TilesConfigurer.class.getClassLoader())) {
                super.registerRequestContextFactory(className, factories, parent);
            }
        }

        @Override
        protected List<URL> getSourceURLs(TilesApplicationContext applicationContext,
                                          TilesRequestContextFactory contextFactory) {
            if (definitions != null) {
                try {
                    List<URL> result = new LinkedList<URL>();
                    for (String definition : definitions) {
                        result.addAll(applicationContext.getResources(definition));
                    }
                    return result;
                }
                catch (IOException ex) {
                    throw new DefinitionsFactoryException("Cannot load definition URLs", ex);
                }
            } else {
                return super.getSourceURLs(applicationContext, contextFactory);
            }
        }

        @Override
        protected BaseLocaleUrlDefinitionDAO instantiateLocaleDefinitionDao(TilesApplicationContext applicationContext,
                                                                            TilesRequestContextFactory contextFactory, LocaleResolver resolver) {
            BaseLocaleUrlDefinitionDAO dao = new OsgiDefinitionDAO();
            return dao;
        }

        @Override
        protected DefinitionsReader createDefinitionsReader(TilesApplicationContext applicationContext,
                                                            TilesRequestContextFactory contextFactory) {
            DigesterDefinitionsReader reader = new DigesterDefinitionsReader();
            if (!validateDefinitions) {
                Map<String, String> map = new HashMap<String, String>();
                map.put(DigesterDefinitionsReader.PARSER_VALIDATE_PARAMETER_NAME, Boolean.FALSE.toString());
                reader.init(map);
            }
            return reader;
        }

        @Override
        protected DefinitionsFactory createDefinitionsFactory(TilesApplicationContext applicationContext,
                                                              TilesRequestContextFactory contextFactory, LocaleResolver resolver) {
            if (definitionsFactoryClass != null) {
                DefinitionsFactory factory = BeanUtils.instantiate(definitionsFactoryClass);
                if (factory instanceof TilesApplicationContextAware) {
                    ((TilesApplicationContextAware) factory).setApplicationContext(applicationContext);
                }
                BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(factory);
                if (bw.isWritableProperty("localeResolver")) {
                    bw.setPropertyValue("localeResolver", resolver);
                }
                if (bw.isWritableProperty("definitionDAO")) {
                    bw.setPropertyValue("definitionDAO",
                            createLocaleDefinitionDao(applicationContext, contextFactory, resolver));
                }
                if (factory instanceof Refreshable) {
                    ((Refreshable) factory).refresh();
                }
                return factory;
            } else {
                return super.createDefinitionsFactory(applicationContext, contextFactory, resolver);
            }
        }

        @Override
        protected PreparerFactory createPreparerFactory(TilesApplicationContext applicationContext,
                                                        TilesRequestContextFactory contextFactory) {
            if (preparerFactoryClass != null) {
                return BeanUtils.instantiate(preparerFactoryClass);
            } else {
                return super.createPreparerFactory(applicationContext, contextFactory);
            }
        }

        @Override
        protected LocaleResolver createLocaleResolver(TilesApplicationContext applicationContext,
                                                      TilesRequestContextFactory contextFactory) {
            return new SpringLocaleResolver();
        }


    }


}
