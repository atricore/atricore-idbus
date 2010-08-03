package org.atricore.idbus.capabilities.samlr2.management;

import javax.management.openmbean.TabularData;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface CircleOfTrustMBean {

    TabularData listProviders();
    
}
