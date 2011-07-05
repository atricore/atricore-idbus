package com.atricore.idbus.console.help.main {
import com.atricore.idbus.console.base.extensions.appsection.AppSectionView;
import com.atricore.idbus.console.base.extensions.appsection.AppSectionViewFactory;

public class HelpViewFactory extends AppSectionViewFactory {

    public static const VIEW_NAME:String = "HelpView";

    public function HelpViewFactory() {
        super(VIEW_NAME);
    }

    override public function createView():AppSectionView {
        return new HelpView();
    }
}
}