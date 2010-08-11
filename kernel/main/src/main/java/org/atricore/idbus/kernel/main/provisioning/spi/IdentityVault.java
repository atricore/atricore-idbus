package org.atricore.idbus.kernel.main.provisioning.spi;

import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public interface IdentityVault {

    String getName();

    String getDescription();

    Set<IdentityPartition> getIdentityPartitions();

}
