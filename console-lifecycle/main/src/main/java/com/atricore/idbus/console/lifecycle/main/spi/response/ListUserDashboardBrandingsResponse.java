package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.UserDashboardBranding;

import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ListUserDashboardBrandingsResponse extends AbstractManagementResponse {

    private List<UserDashboardBranding> brandings;

    public List<UserDashboardBranding> getBrandings() {
        if (brandings == null) {
            brandings = new ArrayList<UserDashboardBranding>();
        }
        return brandings;
    }

    public void setBrandings(List<UserDashboardBranding> brandings) {
        this.brandings = brandings;
    }
}
