package org.atricore.idbus.kernel.main.provisioning.domain.dao;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public interface GenericDAO<T> {

    public T createObject(T object);

    public void deleteObject(T object);

    public T findObjectById(Serializable id);

    public T updateObject(T object);

    public Collection<T> findAll();

    public void flush();

}
