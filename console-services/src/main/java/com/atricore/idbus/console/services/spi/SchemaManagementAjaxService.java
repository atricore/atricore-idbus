package com.atricore.idbus.console.services.spi;

import com.atricore.idbus.console.services.spi.exceptions.SchemaManagementAjaxException;
import com.atricore.idbus.console.services.spi.request.schema.AddSchemaAttributeRequest;
import com.atricore.idbus.console.services.spi.request.schema.ListSchemaAttributesRequest;
import com.atricore.idbus.console.services.spi.request.schema.RemoveSchemaAttributeRequest;
import com.atricore.idbus.console.services.spi.request.schema.UpdateSchemaAttributeRequest;
import com.atricore.idbus.console.services.spi.response.schema.AddSchemaAttributeResponse;
import com.atricore.idbus.console.services.spi.response.schema.UpdateSchemaAttributeResponse;
import com.atricore.idbus.console.services.spi.response.schema.ListSchemaAttributesResponse;
import com.atricore.idbus.console.services.spi.response.schema.RemoveSchemaAttributeResponse;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface SchemaManagementAjaxService {

    AddSchemaAttributeResponse addSchemaAttribute(AddSchemaAttributeRequest request) throws SchemaManagementAjaxException;

    UpdateSchemaAttributeResponse updateSchemaAttribute(UpdateSchemaAttributeRequest request) throws SchemaManagementAjaxException;

    RemoveSchemaAttributeResponse removeSchemaAttribute(RemoveSchemaAttributeRequest request) throws SchemaManagementAjaxException;

    ListSchemaAttributesResponse lsitSchemaAttributes(ListSchemaAttributesRequest request) throws SchemaManagementAjaxException;

}
