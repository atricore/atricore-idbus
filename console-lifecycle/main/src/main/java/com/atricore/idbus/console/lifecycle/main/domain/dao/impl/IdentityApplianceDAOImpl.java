package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceDeployment;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceState;
import com.atricore.idbus.console.lifecycle.main.domain.dao.IdentityApplianceDAO;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityApplianceDefinition;
import com.atricore.idbus.console.lifecycle.main.exception.ApplianceNotFoundException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IdentityApplianceDAOImpl extends GenericDAOImpl<IdentityAppliance, Long>
        implements IdentityApplianceDAO {

    private static final Log logger = LogFactory.getLog(IdentityApplianceDAOImpl.class);

    public IdentityApplianceDAOImpl() {
        super();
    }

    @Override
    public Collection<IdentityAppliance> findAll() {
        try {
            return unmarshallAll(super.findAll());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IdentityAppliance findById(Long id) {
        try {
            return unmarshall(super.findById(id));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IdentityAppliance save(IdentityAppliance object) {
        try {
            return unmarshall(super.save(marshall(object)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<IdentityAppliance> list(boolean deployedOnly) {
        logger.debug("Listing all identity appliances");

        if (deployedOnly) {
            try {
                PersistenceManager pm = getPersistenceManager();
                Query query = pm.newQuery("SELECT FROM com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance" +
                        //" WHERE this.idApplianceDeployment != null");
                        " WHERE this.state == '" + IdentityApplianceState.DEPLOYED + "'" +
                        " || this.state == '" + IdentityApplianceState.STARTED + "'");

                return unmarshallAll(  (Collection<IdentityAppliance>) query.execute());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            //TODO for now returning all appliances for list of projected
            return findAll();
        }
    }

    public IdentityAppliance findByName(String name) throws ApplianceNotFoundException {
        try {
            PersistenceManager pm = getPersistenceManager();
            Query query = pm.newQuery("SELECT FROM com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance" +
                    " WHERE this.name == '" + name + "'");

            Collection<IdentityAppliance> appliances = (Collection<IdentityAppliance>) query.execute();

            if (appliances == null || appliances.size() < 1)
                throw new ApplianceNotFoundException(name);

            if (appliances.size() > 1) // TODO : Improve exception!
                throw new RuntimeException("Too many appliances found for name '"+name+"'" + appliances.size());

            return unmarshall(appliances.iterator().next());

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public IdentityAppliance findByNamespace(String namespace) throws ApplianceNotFoundException {
        try {
            PersistenceManager pm = getPersistenceManager();
            Query query = pm.newQuery("SELECT FROM com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance" +
                    " WHERE this.namespace == '" + namespace + "'");

            Collection<IdentityAppliance> appliances = (Collection<IdentityAppliance>) query.execute();

            if (appliances == null || appliances.size() < 1)
                throw new ApplianceNotFoundException(namespace);

            if (appliances.size() > 1) // TODO : Improve exception!
                throw new RuntimeException("Too many appliances found for name '"+namespace+"'" + appliances.size());

            return unmarshall(appliances.iterator().next());

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean namespaceExists(long applianceId, String namespace) {
        boolean exists = true;
        PersistenceManager pm = getPersistenceManager();
        Query query = pm.newQuery("SELECT this.namespace FROM com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance" +
                " WHERE this.id != " + applianceId +
                " && this.namespace == '" + namespace + "'");
        
        Collection<String> namespaces = (Collection<String>) query.execute();

        if (namespaces == null || namespaces.size() < 1)
            exists = false;

        return exists;
    }

    @Override
    public IdentityAppliance detachCopy(IdentityAppliance object, int fetchDepth) {
        try {
            return unmarshall(super.detachCopy(object, fetchDepth));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<IdentityAppliance> detachCopyAll(Collection<IdentityAppliance> objects, int fetchDepth) {
        try {
            return unmarshallAll(super.detachCopyAll(objects, fetchDepth));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<IdentityAppliance> unmarshallAll(Collection<IdentityAppliance> all) throws IOException, ClassNotFoundException {
        List<IdentityAppliance> appliances = new ArrayList<IdentityAppliance>(all.size());
        for (IdentityAppliance a : all) {
            appliances.add(unmarshall(a));
        }
        return appliances;
    }


    public IdentityAppliance unmarshall(IdentityAppliance a) throws IOException, ClassNotFoundException {

        String defStr = a.getIdApplianceDefinitionBin();

        if (defStr != null) {
            byte[] defBytes = Base64.decodeBase64(defStr.getBytes());
            ByteArrayInputStream defBais = new ByteArrayInputStream(defBytes);
            ObjectInputStream defOis;
            defOis = new ObjectInputStream(defBais);

            IdentityApplianceDefinition definition = (IdentityApplianceDefinition) defOis.readObject();
            a.setIdApplianceDefinition(definition);
        }

        String depStr = a.getIdApplianceDeploymentBin();

        if (depStr != null) {
            byte[] depBytes = Base64.decodeBase64(depStr.getBytes());
            ByteArrayInputStream depBais = new ByteArrayInputStream(depBytes);
            ObjectInputStream depOis;
            depOis = new ObjectInputStream(depBais);

            IdentityApplianceDeployment deployment = (IdentityApplianceDeployment) depOis.readObject();
            a.setIdApplianceDeployment(deployment);
        }

        return a;
    }

    public IdentityAppliance marshall(IdentityAppliance a) throws IOException {
        IdentityApplianceDefinition definition = a.getIdApplianceDefinition();
        if (definition != null) {

            ByteArrayOutputStream defBaos = new ByteArrayOutputStream();
            ObjectOutputStream defOs = new ObjectOutputStream(defBaos);
            defOs.writeObject(definition);
            byte[] defBytes = defBaos.toByteArray();
            byte[] defEnc = Base64.encodeBase64(defBytes);
            String defStr = new String(defEnc);

            a.setIdApplianceDefinitionBin(defStr);
            a.setIdApplianceDefinition(null);
        }

        IdentityApplianceDeployment deployment = a.getIdApplianceDeployment();
        if (deployment != null) {

            ByteArrayOutputStream depBaos = new ByteArrayOutputStream();
            ObjectOutputStream depOs = new ObjectOutputStream(depBaos);
            depOs.writeObject(deployment);
            byte[] depBytes = depBaos.toByteArray();
            byte[] depEnc = Base64.encodeBase64(depBytes);
            String depStr = new String(depEnc);

            a.setIdApplianceDeploymentBin(depStr);
            a.setIdApplianceDeployment(null);
        }

        return a;
    }
}
