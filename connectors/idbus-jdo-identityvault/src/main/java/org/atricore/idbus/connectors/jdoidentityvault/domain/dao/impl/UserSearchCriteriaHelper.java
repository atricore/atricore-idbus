package org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.atricore.idbus.kernel.main.provisioning.domain.UserSearchCriteria;

import java.util.HashMap;
import java.util.Map;

public class UserSearchCriteriaHelper {

    private UserSearchCriteria searchCriteria;

    private StringBuilder searchCriteriaQuery;

    private Map<String, Object> params = new HashMap<String, Object>();

    public UserSearchCriteriaHelper(UserSearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public void createFilterData() {
        searchCriteriaQuery = new StringBuilder();
        params = new HashMap<String, Object>();

        if (searchCriteria != null) {
            if (StringUtils.isNotBlank(searchCriteria.getUsername())) {
                if (searchCriteria.isExactMatch()) {
                    searchCriteriaQuery.append("userName == :userName");
                    params.put("userName", searchCriteria.getUsername());
                } else {
                    searchCriteriaQuery.append("userName.matches(:userName)");
                    params.put("userName", ".*" + searchCriteria.getUsername() + ".*");
                }
            }
            if (StringUtils.isNotBlank(searchCriteria.getFirstName())) {
                if (params.size() > 0)
                    searchCriteriaQuery.append(" && ");
                if (searchCriteria.isExactMatch()) {
                    searchCriteriaQuery.append("firstName == :firstName");
                    params.put("firstName", searchCriteria.getFirstName());
                } else {
                    searchCriteriaQuery.append("firstName.toLowerCase().matches(:firstName)");
                    params.put("firstName", ".*" + searchCriteria.getFirstName().toLowerCase() + ".*");
                }
            }
            if (StringUtils.isNotBlank(searchCriteria.getLastName())) {
                if (params.size() > 0)
                    searchCriteriaQuery.append(" && ");
                if (searchCriteria.isExactMatch()) {
                    searchCriteriaQuery.append("surename == :lastName");
                    params.put("lastName", searchCriteria.getLastName());
                } else {
                    searchCriteriaQuery.append("surename.toLowerCase().matches(:lastName)");
                    params.put("lastName", ".*" + searchCriteria.getLastName().toLowerCase() + ".*");
                }
            }
            if (StringUtils.isNotBlank(searchCriteria.getEmail())) {
                if (params.size() > 0)
                    searchCriteriaQuery.append(" && ");
                if (searchCriteria.isExactMatch()) {
                    searchCriteriaQuery.append("email == :email");
                    params.put("email", searchCriteria.getEmail());
                } else {
                    searchCriteriaQuery.append("email.toLowerCase().matches(:email)");
                    params.put("email", ".*" + searchCriteria.getEmail().toLowerCase() + ".*");
                }
            }
        }
    }

    public String getSearchCriteriaQuery() {
        return searchCriteriaQuery.toString();
    }

    public Map<String, Object> getParams() {
        return params;
    }
}
