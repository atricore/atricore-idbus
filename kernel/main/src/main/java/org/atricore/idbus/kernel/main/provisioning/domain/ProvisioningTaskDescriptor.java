package org.atricore.idbus.kernel.main.provisioning.domain;

public class ProvisioningTaskDescriptor {

    private String oid;

    private String status;

    private ProvisioningTaskType type;

    private boolean recurring;

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
}
