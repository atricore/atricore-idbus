package org.atricore.idbus.capabilities.spmlr2.main.binding.services;

import oasis.names.tc.spml._2._0.*;
import oasis.names.tc.spml._2._0.async.CancelRequestType;
import oasis.names.tc.spml._2._0.async.CancelResponseType;
import oasis.names.tc.spml._2._0.async.StatusRequestType;
import oasis.names.tc.spml._2._0.async.StatusResponseType;
import oasis.names.tc.spml._2._0.atricore.ReplacePasswordRequestType;
import oasis.names.tc.spml._2._0.batch.BatchRequestType;
import oasis.names.tc.spml._2._0.batch.BatchResponseType;
import oasis.names.tc.spml._2._0.bulk.BulkModifyRequestType;
import oasis.names.tc.spml._2._0.password.*;
import oasis.names.tc.spml._2._0.search.CloseIteratorRequestType;
import oasis.names.tc.spml._2._0.search.SearchRequestType;
import oasis.names.tc.spml._2._0.search.SearchResponseType;
import oasis.names.tc.spml._2._0.suspend.ActiveRequestType;
import oasis.names.tc.spml._2._0.suspend.ActiveResponseType;
import oasis.names.tc.spml._2._0.suspend.ResumeRequestType;
import oasis.names.tc.spml._2._0.suspend.SuspendRequestType;
import oasis.names.tc.spml._2._0.updates.IterateRequestType;
import oasis.names.tc.spml._2._0.updates.UpdatesRequestType;
import oasis.names.tc.spml._2._0.updates.UpdatesResponseType;
import oasis.names.tc.spml._2._0.wsdl.SPMLRequestPortType;

import javax.jws.WebParam;
import java.util.logging.Logger;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
@javax.jws.WebService(
                      serviceName = "SPMLService",
                      portName = "soap",
                      targetNamespace = "urn:oasis:names:tc:SPML:2:0:wsdl",
                      endpointInterface = "oasis.names.tc.spml._2._0.wsdl.SPMLRequestPortType")
public class SpmlR2ServiceImpl implements SPMLRequestPortType {

    private static final Logger LOG = Logger.getLogger(SpmlR2ServiceImpl.class.getName());

    public ActiveResponseType spmlActiveRequest(@WebParam(partName = "body", name = "activeRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0:suspend") ActiveRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public UpdatesResponseType spmlUpdatesIterateRequest(@WebParam(partName = "body", name = "iterateRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0:updates") IterateRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public ResponseType spmlReplacePasswordRequest(@WebParam(partName = "body", name = "replacePasswordRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0:atricore") ReplacePasswordRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public ResponseType spmlSetPasswordRequest(@WebParam(partName = "body", name = "setPasswordRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0:password") SetPasswordRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public ResetPasswordResponseType spmlResetPasswordRequest(@WebParam(partName = "body", name = "resetPasswordRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0:password") ResetPasswordRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public BatchResponseType spmlBatchRequest(@WebParam(partName = "body", name = "batchRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0:batch") BatchRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public CancelResponseType spmlCancelRequest(@WebParam(partName = "body", name = "cancelRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0:async") CancelRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public ResponseType spmlExpirePasswordRequest(@WebParam(partName = "body", name = "expirePasswordRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0:password") ExpirePasswordRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public AddResponseType spmlAddRequest(@WebParam(partName = "body", name = "addRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0") AddRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public SearchResponseType spmlSearchRequest(@WebParam(partName = "body", name = "searchRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0:search") SearchRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public ResponseType spmlSuspendRequest(@WebParam(partName = "body", name = "suspendRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0:suspend") SuspendRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public ResponseType spmlDeleteRequest(@WebParam(partName = "body", name = "deleteRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0") DeleteRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public SearchResponseType spmlSearchIterateRequest(@WebParam(partName = "body", name = "iterateRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0:search") oasis.names.tc.spml._2._0.search.IterateRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public ResponseType spmlSearchCloseIteratorRequest(@WebParam(partName = "body", name = "closeIteratorRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0:search") CloseIteratorRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public ListTargetsResponseType spmlListTargetsRequest(@WebParam(partName = "body", name = "listTargetsRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0") ListTargetsRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public LookupResponseType spmlLookupRequest(@WebParam(partName = "body", name = "lookupRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0") LookupRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public UpdatesResponseType spmlUpdatesRequest(@WebParam(partName = "body", name = "updatesRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0:updates") UpdatesRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public ResponseType spmlBulkModifyRequest(@WebParam(partName = "body", name = "bulkModifyRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0:bulk") BulkModifyRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public StatusResponseType spmlStatusRequest(@WebParam(partName = "body", name = "statusRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0:async") StatusRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public ResponseType spmlUpdatesCloseIteratorRequest(@WebParam(partName = "body", name = "closeIteratorRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0:updates") oasis.names.tc.spml._2._0.updates.CloseIteratorRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public ResponseType spmlResumeRequest(@WebParam(partName = "body", name = "resumeRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0:suspend") ResumeRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public ValidatePasswordResponseType spmlValidatePasswordRequest(@WebParam(partName = "body", name = "validatePasswordRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0:password") ValidatePasswordRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }

    public ModifyResponseType spmlModifyRequest(@WebParam(partName = "body", name = "modifyRequest", targetNamespace = "urn:oasis:names:tc:SPML:2:0") ModifyRequestType body) {
        throw new UnsupportedOperationException("This service is not meant to be invoked");
    }
}
