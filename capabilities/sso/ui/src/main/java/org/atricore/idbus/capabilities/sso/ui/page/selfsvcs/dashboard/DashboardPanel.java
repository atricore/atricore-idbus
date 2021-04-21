package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.dashboard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.atricore.idbus.capabilities.sso.ui.model.PartnerAppModel;
import org.atricore.idbus.kernel.main.provisioning.domain.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/12/13
 */
public class DashboardPanel extends Panel {

    private static final Log logger = LogFactory.getLog(DashboardPanel.class);

    private User user ;

    private List<PartnerAppModel> apps;

    public DashboardPanel(String id, User user, List<PartnerAppModel> apps) {
        super(id);
        this.user = user;
        this.apps = apps;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Build dashbord table
        List<IColumn<PartnerAppModel, String>> columns = new ArrayList<IColumn<PartnerAppModel, String>>();

        // Application ICON
        //<td class="gt-avatar"><img src="images/gt/avatar.gif" alt="avatar" width="53" height="53"/></td>

        columns.add(new PropertyColumn<PartnerAppModel, String>(new Model<String>("Type"), "displayName") {

            @Override
            public void populateItem(Item<ICellPopulator<PartnerAppModel>> cellItem, String componentId,
                                     IModel<PartnerAppModel> model) {

                cellItem.add(new AppLogoPanel(componentId, model));
            }
        }
        );

        columns.add(new PropertyColumn<PartnerAppModel, String>(new Model<String>("Details"), "displayName") {
            @Override
            public void populateItem(Item<ICellPopulator<PartnerAppModel>> cellItem, String componentId,
                                     IModel<PartnerAppModel> model) {

                cellItem.add(new AppDetailsPanel(componentId, model));
            }

        });

        /*
        columns.add(new PropertyColumn<PartnerAppModel, String>(new Model<String>(" "), "displayName") {
            @Override
            public void populateItem(Item<ICellPopulator<PartnerAppModel>> cellItem, String componentId,
                                     IModel<PartnerAppModel> model) {

                cellItem.add(new SpDetailsPanel(componentId, model));
            }

        });
        */

        DataTable dataTable = new DefaultDataTable<PartnerAppModel, String>("ssoApps", columns, new PartnerAppDataProvider(apps), 8);

        //dataTable.addBottomToolbar(new ExportToolbar(dataTable).addDataExporter(new CSVDataExporter()));
        add(dataTable);
    }

    public class PartnerAppDataProvider extends SortableDataProvider<PartnerAppModel, String> {

        private List<PartnerAppModel> apps;

        public PartnerAppDataProvider(List<PartnerAppModel> apps) {
            setSort("name", SortOrder.ASCENDING);
            this.apps = apps;
        }

        public final Iterator<PartnerAppModel> iterator(long first, long count) {
            return apps.iterator();
        }

        public final long size() {
            return apps.size();
        }

        public IModel<PartnerAppModel> model(PartnerAppModel partnerAppModel) {
            return new CompoundPropertyModel<PartnerAppModel>(partnerAppModel);
        }
    }

}
