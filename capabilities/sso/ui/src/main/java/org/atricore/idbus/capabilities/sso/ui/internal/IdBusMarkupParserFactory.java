package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.wicket.markup.IMarkupParserFactory;
import org.apache.wicket.markup.MarkupParser;
import org.apache.wicket.markup.MarkupResourceStream;
import org.apache.wicket.markup.parser.XmlPullParser;
import org.apache.wicket.markup.parser.filter.EnclosureHandler;
import org.apache.wicket.markup.parser.filter.RelativePathPrefixHandler;
import org.atricore.idbus.capabilities.sso.ui.WebAppConfig;

/**
 * Work-around to make apache-wicket play nice with servlet context used in OSGi
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdBusMarkupParserFactory implements IMarkupParserFactory {

    private WebAppConfig cfg;

    public IdBusMarkupParserFactory(WebAppConfig appConfig) {
        cfg = appConfig;
    }

    public MarkupParser newMarkupParser(final MarkupResourceStream resource) {
        
        MarkupParser p = new MarkupParser(new XmlPullParser(), resource);
        p.appendMarkupFilter(new IdBusRelativePathPrefixHandler(cfg.getMountPoint()), EnclosureHandler.class);
        return p;
    }


}
