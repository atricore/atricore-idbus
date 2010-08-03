package com.atricore.idbus.console.lifecycle.main.transform;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface IdResourceSerializer {

    boolean canHandle(IdProjectResource resource);

    void resolveLocation(IdResourceSerializerContext ctx, IdProjectResource resource) throws IdResourceSerializationException;

    void serialize(IdResourceSerializerContext serializer, IdProjectResource resource) throws IdResourceSerializationException;

}
