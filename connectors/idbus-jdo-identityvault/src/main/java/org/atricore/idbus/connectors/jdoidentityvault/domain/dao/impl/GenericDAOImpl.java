package org.atricore.idbus.connectors.jdoidentityvault.domain.dao.impl;

import org.atricore.idbus.connectors.jdoidentityvault.domain.dao.GenericDAO;
import org.springframework.orm.jdo.support.JdoDaoSupport;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class GenericDAOImpl<T, PK extends Serializable>
        extends JdoDaoSupport implements GenericDAO<T, PK> {

    private Class<T> persistentClass;

    public GenericDAOImpl() {
        persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public boolean exists(PK id) {
        T entity = (T) getJdoTemplate().getObjectById(this.persistentClass, id);
        return entity != null;
    }

    public T findById(PK id) {
        return (T) getJdoTemplate().getObjectById(this.persistentClass, id);
    }

    public Collection<T> findAll() {
        return getJdoTemplate().find(this.persistentClass);
    }

    public T save(T object) {
        return (T) getJdoTemplate().makePersistent(object);
    }

    public Collection<T> saveAll(Collection<T> objects) {
        return (Collection<T>) getJdoTemplate().makePersistentAll(objects);
    }

    public void delete(PK id) {
        getJdoTemplate().deletePersistent(this.findById(id));
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
