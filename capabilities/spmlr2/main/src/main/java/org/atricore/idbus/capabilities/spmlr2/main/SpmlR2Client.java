package org.atricore.idbus.capabilities.spmlr2.main;

import oasis.names.tc.spml._2._0.wsdl.SPMLRequestPortType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface SpmlR2Client extends SPMLRequestPortType {

    String getPSProviderName();

    boolean hasTarget(String psTargetName);


}
