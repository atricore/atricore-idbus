package com.atricore.idbus.console.web.controllers;

import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.ExportIdentityApplianceProjectRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.ExportIdentityApplianceProjectResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ExportIdentityApplianceController {

    private static final Log logger = LogFactory.getLog(ExportIdentityApplianceController.class);

    @Autowired
    private IdentityApplianceManagementService idApplianceManagementService;

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void export(HttpServletResponse response,
            @RequestParam(value = "applianceId", required = true) String applianceId,
            @RequestParam(value = "name", required = true) String name) {
        
        try {
            ExportIdentityApplianceProjectRequest exportReq = new ExportIdentityApplianceProjectRequest(applianceId);
            ExportIdentityApplianceProjectResponse exportResp = idApplianceManagementService.exportIdentityApplianceProject(exportReq);
            byte[] zip = exportResp.getZip();
            if (zip != null) {
                response.setContentType("application/zip");
                response.setHeader("Content-Disposition","inline; filename=" + name + ".zip;");
                ServletOutputStream out = response.getOutputStream();
                out.write(zip);
                out.flush();
            }
        } catch (Exception e) {
            logger.error("Error exporting identity appliance", e);
        }
    }
}
