package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.wicket.markup.MarkupFactory;
import org.apache.wicket.markup.MarkupParser;
import org.apache.wicket.markup.MarkupResourceStream;
import org.apache.wicket.markup.parser.filter.EnclosureHandler;
import org.atricore.idbus.capabilities.sso.ui.WebAppConfig;

/**
 * Work-around to make apache-wicket play nice with servlet context used in OSGi
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdBusMarkupParserFactory extends MarkupFactory {

    private WebAppConfig cfg;

    public IdBusMarkupParserFactory(WebAppConfig appConfig) {
        cfg = appConfig;
    }

    @Override
    public MarkupParser newMarkupParser(final MarkupResourceStream resource) {
        MarkupParser p = super.newMarkupParser(resource);
        p.getMarkupFilters().add(new IdBusRelativePathPrefixHandler(cfg.getMountPoint()), EnclosureHandler.class);
        return p;
    }


}
