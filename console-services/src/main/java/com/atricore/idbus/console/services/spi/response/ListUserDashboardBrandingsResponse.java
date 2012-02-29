package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.UserDashboardBrandingDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ListUserDashboardBrandingsResponse extends AbstractManagementResponse {

    private List<UserDashboardBrandingDTO> brandings;

    public List<UserDashboardBrandingDTO> getBrandings() {
        if(brandings == null){
            brandings = new ArrayList<UserDashboardBrandingDTO>();
        }
        return brandings;
    }

    public void setBrandings(List<UserDashboardBrandingDTO> brandings) {
        this.brandings = brandings;
    }
}