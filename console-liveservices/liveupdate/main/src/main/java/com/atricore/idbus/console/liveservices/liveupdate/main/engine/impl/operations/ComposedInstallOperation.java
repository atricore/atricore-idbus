package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl.operations;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallEvent;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.UpdateContext;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallOperation;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.OperationStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ComposedInstallOperation extends AbstractInstallOperation {

    public static Log logger = LogFactory.getLog(ComposedInstallOperation.class);

    private Set<InstallOperation> operations;

    public Set<InstallOperation> getOperations() {
        return operations;
    }

    public void setOperations(Set<InstallOperation> operations) {
        this.operations = operations;
    }

    @Override
    public void init() {
        super.init();
        for (InstallOperation op : operations) {
            op.init();
        }
    }

    @Override
    public void shutdonw() {
        super.shutdonw();
        for (InstallOperation op : operations) {
            op.shutdonw();
        }
    }

    public OperationStatus preInstall(InstallEvent event) throws LiveUpdateException {
        for (InstallOperation operation : operations) {

            if (logger.isTraceEnabled())
                logger.trace("preInstall=>" + operation.getName());
            OperationStatus sts = operation.preInstall(event);
            if (!sts.equals(OperationStatus.NEXT))
                return sts;
        }
        return OperationStatus.NEXT;
    }

    public OperationStatus postInstall(InstallEvent event) throws LiveUpdateException {
        for (InstallOperation operation : operations) {
            if (logger.isTraceEnabled())
                logger.trace("postInstall=>" + operation.getName());
            OperationStatus sts = operation.postInstall(event);
            if (!sts.equals(OperationStatus.NEXT))
                return sts;
        }
        return OperationStatus.NEXT;
    }
}
