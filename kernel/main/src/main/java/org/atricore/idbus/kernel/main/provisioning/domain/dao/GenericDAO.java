package org.atricore.idbus.kernel.main.provisioning.domain.dao;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public interface GenericDAO<T> {

    T createObject(T object);

    void deleteObject(T object);

    T findObjectById(Serializable id);

    T updateObject(T object);

    Collection<T> findAll();

    void flush();

}
