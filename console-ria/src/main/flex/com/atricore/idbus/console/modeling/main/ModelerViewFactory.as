/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.modeling.main {
import com.atricore.idbus.console.main.AppSectionView;
import com.atricore.idbus.console.main.AppSectionViewFactory;
import com.atricore.idbus.console.modeling.main.ModelerView;

public class ModelerViewFactory extends AppSectionViewFactory {

    public static const VIEW_NAME:String = "ModelerView";

    public function ModelerViewFactory() {
        super(VIEW_NAME);
    }

    override public function createView():AppSectionView {
        var view:ModelerView  = new ModelerView();
        // TODO : Use resource bundle, place property in parent !
        view.name = "Identity Appliance Modeler";
        return view;
    }
}
}
