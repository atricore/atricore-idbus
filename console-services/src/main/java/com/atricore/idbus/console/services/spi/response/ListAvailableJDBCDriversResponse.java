package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.JDBCDriverDescriptorDTO;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ListAvailableJDBCDriversResponse {

    private List<JDBCDriverDescriptorDTO> drivers;

    public List<JDBCDriverDescriptorDTO> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<JDBCDriverDescriptorDTO> drivers) {
        this.drivers = drivers;
    }
}
