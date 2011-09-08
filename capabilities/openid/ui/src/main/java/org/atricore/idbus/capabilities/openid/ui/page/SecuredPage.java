package org.atricore.idbus.capabilities.openid.ui.page;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.atricore.idbus.capabilities.openid.ui.BasePage;

@AuthorizeInstantiation("admin")
public class SecuredPage extends BasePage {


    public SecuredPage() {
    }
}
