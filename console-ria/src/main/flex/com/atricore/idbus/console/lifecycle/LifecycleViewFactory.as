/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.lifecycle {
import com.atricore.idbus.console.main.AppSectionView;
import com.atricore.idbus.console.main.AppSectionViewFactory;

public class LifecycleViewFactory extends AppSectionViewFactory {

    public static const VIEW_NAME:String = "LifecycleView";

    public function LifecycleViewFactory() {
        super(VIEW_NAME);
    }

    override public function createView():AppSectionView {
        var view:LifecycleView = new LifecycleView();
        // TODO : Use resource bundle, place property in parent !
        view.name = "Identity Appliance Lifecycle Management";
        return view;
    }
}
}
