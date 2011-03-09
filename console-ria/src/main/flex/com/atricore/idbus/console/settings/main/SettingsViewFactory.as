/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.settings.main {
import com.atricore.idbus.console.base.extensions.appsection.AppSectionView;
import com.atricore.idbus.console.base.extensions.appsection.AppSectionViewFactory;

public class SettingsViewFactory  extends AppSectionViewFactory {

    public static const VIEW_NAME:String = "SettingsView";

    public function SettingsViewFactory() {
        super(VIEW_NAME);
    }

    override public function createView():AppSectionView {
        return new SettingsView();
    }
}
}