package com.atricore.idbus.console.brandservice.main.internal.store.dao;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface GenericDAO<T, PK> {

    boolean exists(PK id);

    T findById(PK id);

    Collection<T> findAll();

    T save(T object);

    void delete(PK id);

    T detachCopy(T object, int fetchDepth);

    Collection<T> detachCopyAll(Collection<T> objects, int fetchDepth);

    void flush();
}
