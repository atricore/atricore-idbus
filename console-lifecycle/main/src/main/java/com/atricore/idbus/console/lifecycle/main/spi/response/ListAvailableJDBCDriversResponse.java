package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.JDBCDriverDescriptor;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ListAvailableJDBCDriversResponse extends AbstractManagementResponse {

    private List<JDBCDriverDescriptor> drivers;

    public List<JDBCDriverDescriptor> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<JDBCDriverDescriptor> drivers) {
        this.drivers = drivers;
    }
}
