/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.account.main {
import com.atricore.idbus.console.base.extensions.appsection.AppSectionView;
import com.atricore.idbus.console.base.extensions.appsection.AppSectionViewFactory;

public class AccountManagementViewFactory extends AppSectionViewFactory {

    public static const VIEW_NAME:String = "AccountManagementView";

    public function AccountManagementViewFactory() {
        super(VIEW_NAME );
    }


    override public function createView():AppSectionView {
        return new AccountManagementView();
    }
}
}
