package org.atricore.idbus.kernel.main.provisioning.spi;

import org.atricore.idbus.kernel.main.provisioning.domain.ProvisioningTaskDescriptor;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;

import java.util.Collection;

/**
 * Workflow/Tasks management
 */
public interface ProvisioningEngine {

    Collection<ProvisioningTaskDescriptor> getTasks() throws ProvisioningException;

    ProvisioningTaskDescriptor lookupTask(String taskOid) throws ProvisioningException;

    void createTask(ProvisioningTaskDescriptor taskDescriptor) throws ProvisioningException;

    void deleteTask(String taskOid) throws ProvisioningException;

    void resumeTask(String taskOid) throws ProvisioningException;

    void suspendTask(String taskOid) throws ProvisioningException;

    void executeTask(String taskOid) throws ProvisioningException;

    void stopAllTasks() throws ProvisioningException;

    void startAllTasks() throws ProvisioningException;

}
