package org.atricore.idbus.kernel.main.provisioning.domain;

import java.util.Collection;
import java.util.List;

public class ProvisioningTaskDescriptor {

    private String oid;

    private String status;

    private ProvisioningTaskType type;

    private boolean recurring;

    private OperationResult result;

    private List<OperationResult> partialResults;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public ProvisioningTaskType getType() {
        return type;
    }

    public void setType(ProvisioningTaskType type) {
        this.type = type;
    }

    public OperationResult getResult() {
        return result;
    }

    public void setResult(OperationResult result) {
        this.result = result;
    }

    public List<OperationResult> getPartialResults() {
        return partialResults;
    }

    public void setPartialResults(List<OperationResult> partialResults) {
        this.partialResults = partialResults;
    }
}
