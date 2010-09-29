package com.atricore.idbus.console.lifecycle.main.domain.dao.impl;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
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
                        "or this.state == '" + IdentityApplianceState.STARTED + "'");

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

        String text = a.getIdApplianceDefinitionBin();
        byte[] bytes = Base64.decodeBase64(text.getBytes());

        if (bytes == null || bytes.length == 0)
            return a;

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois;
        ois = new ObjectInputStream(bais);

        IdentityApplianceDefinition ad = (IdentityApplianceDefinition) ois.readObject();
        a.setIdApplianceDefinition(ad);

        return a;
    }

    public IdentityAppliance marshall(IdentityAppliance a) throws IOException {
        IdentityApplianceDefinition obj = a.getIdApplianceDefinition();
        if (obj == null)
            return a;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        byte[] bytes = baos.toByteArray();
        byte[] enc = Base64.encodeBase64(bytes);
        String ad = new String(enc);

        a.setIdApplianceDefinitionBin(ad);
        a.setIdApplianceDefinition(null);

        return a;

    }
}
