package org.atricore.idbus.capabilities.sso.ui.widget;

import org.apache.wicket.markup.html.panel.Panel;

/**
 * Widget extension point which may be used in many places. Services registered
 * in OSGi should provide information where it should be put using "intention"
 * property.
 */
public interface WidgetProvider {

    Panel getWidgetPanel(String id);

}
