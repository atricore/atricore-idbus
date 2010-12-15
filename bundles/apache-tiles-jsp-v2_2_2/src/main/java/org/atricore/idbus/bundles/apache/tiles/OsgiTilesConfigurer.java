package org.atricore.idbus.bundles.apache.tiles;

import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.preparer.PreparerFactory;
import org.apache.tiles.startup.BasicTilesInitializer;
import org.apache.tiles.startup.TilesInitializer;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OsgiTilesConfigurer extends TilesConfigurer {

    protected String[] definitions;

    protected Class<? extends DefinitionsFactory> definitionsFactoryClass;

    protected Class<? extends PreparerFactory> preparerFactoryClass;

    protected boolean validateDefinitions;

    @Override
    public void setValidateDefinitions(boolean validateDefinitions) {
        super.setValidateDefinitions(validateDefinitions);
        this.validateDefinitions = validateDefinitions;
    }

    @Override
    public void setDefinitions(String[] definitions) {
        super.setDefinitions(definitions);
        this.definitions = definitions;
    }

    @Override
    public void setDefinitionsFactoryClass(Class<? extends DefinitionsFactory> definitionsFactoryClass) {
        super.setDefinitionsFactoryClass(definitionsFactoryClass);
        this.definitionsFactoryClass = definitionsFactoryClass;
    }

    @Override
    public void setPreparerFactoryClass(Class<? extends PreparerFactory> preparerFactoryClass) {
        super.setPreparerFactoryClass(preparerFactoryClass);
        this.preparerFactoryClass = preparerFactoryClass;
    }

    /**
	 * Creates a new instance of {@link org.apache.tiles.startup.BasicTilesInitializer}.
	 * <p>Override it to use a different initializer.
	 * @see org.apache.tiles.web.startup.TilesListener#createTilesInitializer()
	 */
	protected TilesInitializer createTilesInitializer() {
		OsgiTilesInitializer i = new OsgiTilesInitializer();

        i.setDefinitions(definitions);
        i.setDefinitionsFactoryClass(definitionsFactoryClass);
        i.setPreparerFactoryClass(preparerFactoryClass);
        i.setUseMutableTilesContainer(false);
        i.setValidateDefinitions(true);
        
        return i;
	}
}
