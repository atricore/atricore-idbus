package org.atricore.idbus.capabilities.openid.ui.page;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.atricore.idbus.capabilities.openid.ui.widget.WidgetProvider;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.ops4j.pax.wicket.api.PaxWicketMountPoint;

@PaxWicketMountPoint(mountPoint = "/dashboard")
public class DashboardPage extends SinglePage {

    @PaxWicketBean(name = "widgets")
    private List<WidgetProvider> widgets;

    public DashboardPage(PageParameters parameters) {
        add(CSSPackageResource.getHeaderContribution(DashboardPage.class, "dashboard.css"));

        add(new Label("noWidgets", "So far there is no widgets to display") {
            @Override
            public boolean isVisible() {
                return widgets.size() == 0;
            }
        });

    }

    public DashboardPage() {
        this(null);
    }

}
