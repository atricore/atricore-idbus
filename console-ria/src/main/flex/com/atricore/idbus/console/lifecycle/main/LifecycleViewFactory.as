/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.lifecycle.main {
import com.atricore.idbus.console.base.extensions.appsection.AppSectionView;
import com.atricore.idbus.console.base.extensions.appsection.AppSectionViewFactory;

public class LifecycleViewFactory extends AppSectionViewFactory {

    public static const VIEW_NAME:String = "LifecycleView";

    public function LifecycleViewFactory() {
        super(VIEW_NAME);
    }

    override public function createView():AppSectionView {
        return new LifecycleView();
    }
}
}
