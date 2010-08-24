package com.atricore.idbus.console.lifecycle.main.domain.dao;

import java.util.Collection;

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
