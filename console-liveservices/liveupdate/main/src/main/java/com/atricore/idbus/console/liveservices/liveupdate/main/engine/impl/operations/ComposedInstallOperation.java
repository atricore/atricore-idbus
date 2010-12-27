package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl.operations;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.UpdateContext;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallOperation;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.OperationStatus;

import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ComposedInstallOperation extends AbstractInstallOperation {

    private Set<InstallOperation> operations;

    public Set<InstallOperation> getOperations() {
        return operations;
    }

    public void setOperations(Set<InstallOperation> operations) {
        this.operations = operations;
    }

    public OperationStatus preInstall(UpdateContext ctx) throws LiveUpdateException {
        for (InstallOperation operation : operations) {
            operation.preInstall(ctx);
        }
        return OperationStatus.NEXT;
    }

    public OperationStatus postInstall(UpdateContext ctx) throws LiveUpdateException {
        for (InstallOperation operation : operations) {
            operation.postInstall(ctx);
        }
        return OperationStatus.NEXT;
    }
}
