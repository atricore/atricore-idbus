package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.dao.GenericDAO;
import org.springframework.orm.jdo.support.JdoDaoSupport;

import java.io.Serializable;
import java.util.Collection;

public abstract class GenericDAOImpl<T, PK extends Serializable>
        extends JdoDaoSupport implements GenericDAO<T, PK> {

    private Class<T> persistentClass;

    public GenericDAOImpl(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public boolean exists(PK id) {
        T entity = (T) getJdoTemplate().getObjectById(this.persistentClass, id);
        return entity != null;
    }

    public T getObjectById(PK id) {
        return (T) getJdoTemplate().getObjectById(this.persistentClass, id);
    }
    
    public Collection<T> findAll() {
        return getJdoTemplate().find(this.persistentClass);
    }
    
    public T save(T object) {
        return (T) getJdoTemplate().makePersistent(object);
    }
    
    public void remove(PK id) {
        getJdoTemplate().deletePersistent(this.getObjectById(id));
    }

    public void flush() {
        getJdoTemplate().flush();
    }

    public T detachCopy(T object, int fetchDepth) {
        getPersistenceManager().getFetchPlan().setMaxFetchDepth(fetchDepth);
        return (T) getJdoTemplate().detachCopy(object);
    }

    public Collection<T> detachCopyAll(Collection<T> objects, int fetchDepth) {
        getPersistenceManager().getFetchPlan().setMaxFetchDepth(fetchDepth);
        return getJdoTemplate().detachCopyAll(objects);
    }
}
