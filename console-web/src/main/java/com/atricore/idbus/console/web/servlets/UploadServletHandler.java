package com.atricore.idbus.console.web.servlets;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.management.main.domain.metadata.Resource;
import org.atricore.idbus.capabilities.management.main.spi.IdentityApplianceManagementService;
import org.atricore.idbus.capabilities.management.main.spi.request.AddResourceRequest;
import org.atricore.idbus.capabilities.management.main.spi.response.AddResourceResponse;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @version $Id$
 */
public class UploadServletHandler implements HttpRequestHandler {

    private static final Log logger = LogFactory.getLog(UploadServletHandler.class);
    private IdentityApplianceManagementService idApplianceManagementService;

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getMethod().equals("POST")) {
            // Create a factory for disk-based file items
            DiskFileItemFactory factory = new DiskFileItemFactory();

            // Set factory constraints
            factory.setSizeThreshold(0);
            //factory.setRepository(yourTempDirectory);

            ServletFileUpload upload = new ServletFileUpload(factory);

            try {
                Long resourceId = null;
                String resourceName = null;
                String resourceDisplayName = null;

                // Parse the request
                List<FileItem> items = upload.parseRequest(request);
                for (FileItem item : items) {
                    if (item.getFieldName().equalsIgnoreCase("resourceName")) {
                        resourceName = item.getString();
                    } else if (item.getFieldName().equalsIgnoreCase("resourceDisplayName")) {
                        resourceDisplayName = item.getString();
                    }
                    if (!item.isFormField()) {
                        byte[] value = item.get();
                        Resource resource = new Resource();
                        resource.setName(resourceName);
                        resource.setDisplayName(resourceDisplayName);
                        resource.setValue(value);
                        AddResourceRequest addResourceRequest = new AddResourceRequest();
                        addResourceRequest.setResource(resource);
                        AddResourceResponse addResourceResponse = idApplianceManagementService.addResource(addResourceRequest);
                        if (addResourceResponse.getResource() != null) {
                            resource = addResourceResponse.getResource();
                            resourceId = resource.getId();
                        }
                    }
                }

                if (resourceId != null) {
                    logger.debug("Resource saved with id: " + resourceId);
                    response.getWriter().write(resourceId.toString());
                }
            } catch (Exception e) {
                logger.error(e, e);
                //response.getWriter().write(e.getMessage());
            }
        }
    }

    public void setIdApplianceManagementService(IdentityApplianceManagementService idApplianceManagementService) {
        this.idApplianceManagementService = idApplianceManagementService;
    }
}