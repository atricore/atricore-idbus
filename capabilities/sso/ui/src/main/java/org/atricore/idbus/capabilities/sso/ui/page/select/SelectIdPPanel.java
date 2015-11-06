package org.atricore.idbus.capabilities.sso.ui.page.select;

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
import org.atricore.idbus.capabilities.sso.ui.model.IdPModel;

import java.util.*;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 6/17/13
 */
public class SelectIdPPanel extends Panel {

    private static final Log logger = LogFactory.getLog(SelectIdPPanel.class);

    private SelectIdPMediator mediator;

    public SelectIdPPanel(String id, SelectIdPMediator m) {
        super(id);
        this.mediator = m;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Build a table with all the available IdPs
        List<IColumn<IdPModel, String>> columns = new ArrayList<IColumn<IdPModel, String>>();

        // First column contains the IdP logo
        columns.add(new PropertyColumn<IdPModel, String>(new Model<String>(getString("idpTypeColumn", null, " ")), "displayName") {

            @Override
            public String getCssClass() {
                return "gt-avatar";
            }

            @Override
            public void populateItem(Item<ICellPopulator<IdPModel>> cellItem, String componentId,
                                     IModel<IdPModel> model) {

                cellItem.add(new IdPLogoPanel(componentId, model, mediator));
            }
        }
        );

        // Second column contains IdP description and sign-in link
        columns.add(new PropertyColumn<IdPModel, String>(new Model<String>(getString("idpDetailsColumn", null, " ")), "displayName") {

            @Override
            public void populateItem(Item<ICellPopulator<IdPModel>> cellItem, String componentId,
                                     IModel<IdPModel> model) {

                cellItem.add(new IdPDetailsPanel(componentId, model, mediator));
            }

        });


        // Third column contains IdP additional information (i.e. ID and Type)
        columns.add(new PropertyColumn<IdPModel, String>(new Model<String>(getString("idpInformationColumn", null, " ")), "displayName") {
            @Override
            public void populateItem(Item<ICellPopulator<IdPModel>> cellItem, String componentId,
                                     IModel<IdPModel> model) {

                cellItem.add(new IdPInformationPanel(componentId, model, mediator));
            }

        });

        DataTable dataTable = new DefaultDataTable<IdPModel, String>("ssoIdPs", columns, new IdPDataProvider(mediator.getIdpModels()), 8);

        //dataTable.addBottomToolbar(new ExportToolbar(dataTable).addDataExporter(new CSVDataExporter()));
        add(dataTable);

    }

    public class IdPDataProvider extends SortableDataProvider<IdPModel, String> {

        private List<IdPModel> idps;

        public IdPDataProvider(List<IdPModel> idps) {
            this.idps = new ArrayList<IdPModel>();
            this.idps.addAll(idps);
            Collections.sort(this.idps, new IdPComparator());
        }

        public final Iterator<IdPModel> iterator(long first, long count) {
            return idps.iterator();
        }

        public final long size() {
            return idps.size();
        }

        public IModel<IdPModel> model(IdPModel idp) {
            return new CompoundPropertyModel<IdPModel>(idp);
        }

        protected class IdPComparator implements Comparator<IdPModel> {
            @Override
            public int compare(IdPModel idp1, IdPModel idp2) {
                return idp1.getDescription().compareTo(idp2.getDescription());
            }
        }
    }
}
