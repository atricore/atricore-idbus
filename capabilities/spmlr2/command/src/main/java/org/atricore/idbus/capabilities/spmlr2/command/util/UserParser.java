package org.atricore.idbus.capabilities.spmlr2.command.util;

import oasis.names.tc.spml._2._0.atricore.UserType;

import java.io.InputStream;
import java.util.Set;

public interface UserParser {

    String getName();

    String getSchema();

    Set<UserType> fromStream(InputStream in) throws UserParseException;

    Set<UserType> fromStream(InputStream is, boolean unknownPropertiesAsExtendedAttributes) throws UserParseException;




}