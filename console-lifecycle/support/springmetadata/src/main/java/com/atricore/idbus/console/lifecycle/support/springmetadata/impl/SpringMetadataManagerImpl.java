/*
 * Atricore IDBus
 *
 *   Copyright 2009, Atricore Inc.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.lifecycle.support.springmetadata.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jdo.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.atricore.idbus.console.lifecycle.support.springmetadata.exception.BeanNotFoundException;
import com.atricore.idbus.console.lifecycle.support.springmetadata.exception.SpringMetadataManagementException;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.osgi.*;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.*;
import com.atricore.idbus.console.lifecycle.support.springmetadata.spi.SpringMetadataManager;
import com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class SpringMetadataManagerImpl implements SpringMetadataManager, DisposableBean, InitializingBean {
	
    static Log logger = LogFactory.getLog(SpringMetadataManagerImpl.class);

    private PersistenceManagerFactory pmf;

    private PersistenceManager pm;

    public void destroy() throws Exception {
        try {
            pm.close();
        } catch (Exception e) {
            logger.error("Closing Persistence Manager : " + e.getMessage(), e);
        }

    }

    public void afterPropertiesSet() throws Exception {
        pm = pmf.getPersistenceManager();
    }

    public void saveBeansDefinition(BeansDefinition beansDef) throws SpringMetadataManagementException {
        // Transaction demarcation must be performed by the invoking service

        try {
            logger.debug("Persisting BeansDefinition " + beansDef.getName());
            BeansDefinition foundDef = findBeansDefinition(beansDef.getName());
            if(foundDef != null){ //remove existing BeansDefinition
                removeBeansDefinition(beansDef.getName());
            }
            pm.makePersistent(beansDef);
        } catch (Exception e){
            throw new SpringMetadataManagementException("Failed to save beans definition", e);
        }
    }

    public void removeBeansDefinition(String name) throws SpringMetadataManagementException {
        // Transaction demarcation must be performed by the invoking service

        logger.debug("Deleting BeansDefinition " + name);

        BeansDefinition beansDefinition = findBeansDefinition(name);
        logger.debug("Finding Beans Definition " + name);
        Extent e = pm.getExtent(BeansDefinition.class, true);
        Query q = pm.newQuery(e, "name == '" + name + "'");
        Collection c = (Collection) q.execute();
        if(c != null && !c.isEmpty()){
        	beansDefinition = (BeansDefinition)c.iterator().next();
        }
        if(beansDefinition == null){
            throw new SpringMetadataManagementException("Couldn't find beans definition " + name + " to delete");
        }
        List<Object> toDelete = new ArrayList<Object>();

        // TODO : Extend this pattern to other relations in the model
        for (Object o : beansDefinition.getBeans().getImportsAndAliasAndBeen()) {
            if (o instanceof Bean) {
                Bean b = (Bean) o;
                toDelete.addAll(collectBeanChildren(b));
                b.getMetasAndConstructorArgsAndProperties().clear();
            } 
            toDelete.add(o);        
        }

        beansDefinition.getBeans().getImportsAndAliasAndBeen().clear();

        pm.deletePersistent(beansDefinition);

        for (Object o : toDelete) {
        	if(o instanceof Bean){ 
        		logger.debug("Deleting object:" + ((Bean)o).getName());
        	}
            pm.deletePersistent(o);
        }
        logger.debug("BeansDefinition has been deleted");
    }
    
    /*
     * Recursively collecting objects to delete
     */
    private List<Object> collectObjectChildren(Object o){
    	List<Object> toDelete = new ArrayList<Object>();
    	
    	if(o instanceof Bean){
    		toDelete.addAll(collectBeanChildren((Bean)o));    		
    	} else if (o instanceof Property){
    		toDelete.addAll(collectPropertyChildren((Property)o));
    	} else if (o instanceof ConstructorArg){
    		toDelete.addAll(collectConstructorArgChildren((ConstructorArg)o));
    	} else if (o instanceof com.atricore.idbus.console.lifecycle.support.springmetadata.model.List){
    		toDelete.addAll(collectListChildren((com.atricore.idbus.console.lifecycle.support.springmetadata.model.List) o));
    	} else if (o instanceof com.atricore.idbus.console.lifecycle.support.springmetadata.model.Set){
    		toDelete.addAll(collectSetChildren((Set) o));
    	} else if (o instanceof com.atricore.idbus.console.lifecycle.support.springmetadata.model.Map){
    		toDelete.addAll(collectMapChildren((Map) o));
    	} else if (o instanceof Service){
    		toDelete.addAll(collectServiceChildren((Service) o));
    	} else if (o instanceof Reference){
    		toDelete.addAll(collectReferenceChildren((Reference) o));
    	}
    	
    	return toDelete;
    } 

    private List<Object> collectReferenceChildren(Reference reference) {
    	List<Object> toDelete = new ArrayList<Object>();
    	for(Object o : reference.getInterfaces().getBeenAndRevesAndIdreves()){
    		toDelete.addAll(collectObjectChildren(o));
    		toDelete.add(o);
    	}
    	reference.getInterfaces().getBeenAndRevesAndIdreves().clear();
    	for(Tlistener l : reference.getListeners()){
    		toDelete.add(l);
    	}
    	reference.getListeners().clear();
		return toDelete;
	}

	private List<Object> collectServiceChildren(Service service) {
    	List<Object> toDelete = new ArrayList<Object>();
    	
    	for(Object o : service.getInterfaces().getBeenAndRevesAndIdreves()){
    		toDelete.addAll(collectObjectChildren(o));
    		toDelete.add(o);
    	}
    	service.getInterfaces().getBeenAndRevesAndIdreves().clear();
    	toDelete.addAll(collectObjectChildren(service.getServiceProperties()));
    	for(Object o : service.getRegistrationListeners()){
    		toDelete.add(o);
    	}
    	service.getRegistrationListeners().clear();
    	
		return toDelete;
	}

	private List<Object> collectMapChildren(MapType map) {
    	List<Object> toDelete = new ArrayList<Object>();
    	
    	for(Entry e : map.getEntries()){
    		for(Object o : e.getBeenAndRevesAndIdreves()){
    			toDelete.addAll(collectObjectChildren(o));
        		toDelete.add(o);
    		}
    		e.getBeenAndRevesAndIdreves().clear();
    		for(Object o : e.getKey().getBeenAndRevesAndIdreves()){
    			toDelete.addAll(collectObjectChildren(o));
        		toDelete.add(o);    			
    		}
    		e.getKey().getBeenAndRevesAndIdreves().clear();
    		toDelete.add(e);
    		toDelete.add(e.getKey());
    	}
    	map.getEntries().clear();
    	return toDelete;
	}

	private List<Object> collectListChildren(
			com.atricore.idbus.console.lifecycle.support.springmetadata.model.List list) {
    	List<Object> toDelete = new ArrayList<Object>();
    	
    	for(Object o : list.getBeenAndRevesAndIdreves()){
    		toDelete.addAll(collectObjectChildren(o));
    		toDelete.add(o);
    	}
    	list.getBeenAndRevesAndIdreves().clear();
    	
    	return toDelete;
	}
    
    private List<Object> collectSetChildren(Set set) {
    	List<Object> toDelete = new ArrayList<Object>();
    	
    	for(Object o : set.getBeenAndRevesAndIdreves()){
    		toDelete.addAll(collectObjectChildren(o));
    		toDelete.add(o);
    	}
    	set.getBeenAndRevesAndIdreves().clear();
    	
    	return toDelete;
	}
    
    private List<Object> collectConstructorArgChildren(ConstructorArg cArg){
    	List<Object> toDelete = new ArrayList<Object>();
    	toDelete.addAll(collectObjectChildren(cArg.getList()));
    	toDelete.addAll(collectObjectChildren(cArg.getSet()));
    	toDelete.addAll(collectObjectChildren(cArg.getMap()));
    	if(cArg.getBean() != null){
    		toDelete.addAll(collectBeanChildren(cArg.getBean()));
    	}
    	return toDelete;
    }

	private List<Object> collectPropertyChildren(Property property) {
    	List<Object> toDelete = new ArrayList<Object>();
    	
    	toDelete.addAll(collectObjectChildren(property.getList()));
    	toDelete.addAll(collectObjectChildren(property.getSet()));
    	toDelete.addAll(collectObjectChildren(property.getMap()));
    	if(property.getBean() != null){
    		toDelete.addAll(collectBeanChildren(property.getBean()));
    	}
    	
		return toDelete;
	}

	private List<Object> collectBeanChildren(Bean b) {
    	List<Object> toDelete = new ArrayList<Object>();
 
    	for (Object o : b.getMetasAndConstructorArgsAndProperties()) {
    		toDelete.addAll(collectObjectChildren(o));
            toDelete.add(o);
        }
    	b.getMetasAndConstructorArgsAndProperties().clear();
    	
		return toDelete;
	}

	public BeansDefinition findBeansDefinition(String name) throws SpringMetadataManagementException {
        // Transaction demarcation must be performed by the invoking service

        BeansDefinition beansDef = null;

        logger.debug("Finding Beans Definition " + name);
        Extent e = pm.getExtent(BeansDefinition.class, true);
        Query q = pm.newQuery(e, "name == '" + name + "'");
        Collection c = (Collection) q.execute();
        if(c != null && !c.isEmpty()){
            beansDef = (BeansDefinition)c.iterator().next();
        }
        return beansDef;
    }

    /**
     * Prevents from having inconsistent state when saving couple of dependent beans at 
     * the same time and e.g. last one is not persisted (exception occurs). 
     * This way all beans are saved in one transaction.
     */
    public void saveBeanList(List<Bean> beanList) throws SpringMetadataManagementException {

        Transaction tx=pm.currentTransaction();
        try
        {
        	logger.debug("Persisting bean");
//        	Beans beans = null;
//            Extent e = pm.getExtent(Beans.class, true);
//            Iterator iter = e.iterator();
//            while (iter.hasNext())
//            {
//                beans = (Beans)iter.next();
//                break;
//            }
            tx.begin();
//            if(beans == null){
//            	beans = new Beans();
//            }
            for (Bean bean : beanList) {
//            	beans.getAliasAndImportsAndBeen().add(bean);
				pm.makePersistent(bean);
			}
//            pm.makePersistent(beans);
            tx.commit();
        } catch (Exception e){
            logger.error("Error persisting bean list" , e);
            tx.rollback();
            throw new SpringMetadataManagementException(e);            
        }  finally {
            if (tx.isActive()) {
                tx.rollback();
            }

        }            
    }
    
	public void saveBean(Bean bean) throws SpringMetadataManagementException {

        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            logger.debug("Persisting bean");
//            Beans beans = null;
//            Extent e = pm.getExtent(Beans.class, true);
//            Iterator iter = e.iterator();
//            while (iter.hasNext())
//            {
//                beans = (Beans)iter.next();
//                break;
//            }            
//            if(beans == null){
//            	beans = new Beans();
//            }            
//            beans.getAliasAndImportsAndBeen().add(bean);
            
            pm.makePersistent(bean);
//            pm.makePersistent(beans);
            tx.commit();
        } catch (Exception e){
            logger.error("Error persisting bean" , e);
            tx.rollback();
            throw new SpringMetadataManagementException(e);            
        }  finally {
            if (tx.isActive()) {
                tx.rollback();
            }

        }
	}
	
	public void updateBeanList(List<Bean> beanList) throws SpringMetadataManagementException {

        Transaction tx=pm.currentTransaction();
        Bean bean = null;
        try {
            tx.begin();
            logger.debug("Updating bean");
            for(Bean updatedBean : beanList){
	            Query query = pm.newQuery(Bean.class, "name==:name");
	            Collection result = (Collection) query.execute(updatedBean.getName());                   
	            Iterator it = result.iterator();
	
	            if (!it.hasNext())
	                throw new BeanNotFoundException(updatedBean.getName());
	            
	            bean = (Bean)it.next();            
	    		
	    		for(Object object : updatedBean.getMetasAndConstructorArgsAndProperties()){
	    			if(object instanceof Property){
	    				Property prop = (Property)object;
	    				if(prop.getValue() != null){
	    					BeanUtils.setPropertyValue(bean, prop.getName(), prop.getValue().getContent().get(0));
	    				} else if(prop.getRef() != null){
	    					BeanUtils.setPropertyRef(bean, prop.getName(), prop.getRef().getBean());
	    				}
	    			}
	    		}
            }
            tx.commit();
        } catch (Exception e){
            logger.error("Error updating bean list" , e);
            tx.rollback();
            throw new SpringMetadataManagementException(e);
        }  finally {
            if (tx.isActive()) {
                tx.rollback();
            }

        }			
	}
	
	public void updateBean(Bean updatedBean) throws SpringMetadataManagementException {

        Transaction tx=pm.currentTransaction();
        Bean bean = null;
        try {
            tx.begin();
            logger.debug("Updating bean");
            Query query = pm.newQuery(Bean.class, "name==:name");
            Collection result = (Collection) query.execute(updatedBean.getName());                   
            Iterator it = result.iterator();

            if (!it.hasNext())
                throw new BeanNotFoundException(updatedBean.getName());
            
            bean = (Bean)it.next();            
    		
    		for(Object object : updatedBean.getMetasAndConstructorArgsAndProperties()){
    			if(object instanceof Property){
    				Property prop = (Property)object;
    				if(prop.getValue() != null){
    					BeanUtils.setPropertyValue(bean, prop.getName(), prop.getValue().getContent().get(0));
    				} else if(prop.getRef() != null){
    					BeanUtils.setPropertyRef(bean, prop.getName(), prop.getRef().getBean());
    				} else if(prop.getList() != null){
    					List<Bean> beanList = new ArrayList<Bean>();
    					List<Ref> refList = new ArrayList<Ref>();
    					for(Object tmpObj : prop.getList().getBeenAndRevesAndIdreves()){
    						if(tmpObj instanceof Bean){
    							beanList.add((Bean)tmpObj);
    						} else if(tmpObj instanceof Ref){
    							refList.add((Ref)tmpObj);
    						}
    					}
    					if(!beanList.isEmpty()){
    						BeanUtils.setPropertyAsBeans(bean, prop.getName(), beanList);
    					}
    					if(!refList.isEmpty()){
    						BeanUtils.setPropertyRefs(bean, prop.getName(), refList);
    					}
    				}
    			}
    		}            
            tx.commit();
        } catch (Exception e){
            logger.error("Error updating bean with name: " + updatedBean.getName() , e);
            tx.rollback();
            throw new SpringMetadataManagementException(e);
        }  finally {
            if (tx.isActive()) {
                tx.rollback();
            }

        }	
	}
	
	public void removeBeanList(List<String> beanNames) throws SpringMetadataManagementException {

        Transaction tx=pm.currentTransaction();
        try {
            tx.begin();
            logger.debug("Removing bean list");
            
            for (String beanName : beanNames) {
                Query query = pm.newQuery(Bean.class, "name==:name");
                Collection result = (Collection) query.execute(beanName);                  
                Iterator it = result.iterator();

                if (!it.hasNext()){
                    throw new BeanNotFoundException(beanName);
                }

                pm.deletePersistent(it.next());				
			}

            tx.commit();            
            
        } catch (Exception e){
            logger.error("Error deleting bean list" , e);
            tx.rollback();
            throw new SpringMetadataManagementException(e);
        }  finally {
            if (tx.isActive()) {
                tx.rollback();
            }

        }		
	}
	
	public void removeBean(String beanName) throws SpringMetadataManagementException {

        Transaction tx=pm.currentTransaction();
        try {
            tx.begin();
            logger.debug("Removing bean");
            Query query = pm.newQuery(Bean.class, "name==:name");
            Collection result = (Collection) query.execute(beanName);                  
            Iterator it = result.iterator();

            if (!it.hasNext())
                throw new BeanNotFoundException(beanName);

            pm.deletePersistent(it.next());

            tx.commit();
            
            
        } catch (Exception e){
            logger.error("Error deleting bean" , e);
            tx.rollback();
            throw new SpringMetadataManagementException(e);
        }  finally {
            if (tx.isActive()) {
                tx.rollback();
            }

        }		
	}
	
	public Bean findBeanByName(String beanName) throws SpringMetadataManagementException {

		Bean bean = null;
		Bean retBean = null;
		Transaction tx=pm.currentTransaction();
        try {        	
            tx.begin();
            logger.debug("Finding bean with name:" + beanName);
            
            pm.getFetchPlan().addGroup("detach_properties");
            pm.getFetchPlan().setMaxFetchDepth(3);
            Query query = pm.newQuery(Bean.class, "name==:name");
            Collection result = (Collection) query.execute(beanName);
            
            Iterator it = result.iterator();
            
            if (!it.hasNext())
                throw new BeanNotFoundException(beanName);
            
            bean = (Bean)it.next();            
            logger.debug("Found bean with name: " + bean.getName() + " using Object Manager: " + JDOHelper.getPersistenceManager(bean));
            retBean = (Bean)pm.detachCopy(bean);
            logger.debug("Detached found bean with name: " + retBean.getName());
            tx.commit();
        } catch (Exception e){
            logger.error("Error finding bean with name: " + beanName , e);
            throw new SpringMetadataManagementException(e);
        }  finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
        return retBean;
	}
	
    public void marshallBeans(String path) throws SpringMetadataManagementException {
        // Create a PersistenceManagerFactory for this datastore

        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            logger.debug("Marshalling spring configuration to spring-config.xml");
            pm.getFetchPlan().addGroup("detach_properties");
            pm.getFetchPlan().setMaxFetchDepth(3);
            Extent e = pm.getExtent(Bean.class, true);
            Iterator iter = e.iterator();
            Beans beans = new Beans();
            while (iter.hasNext())
            {
                Bean bean = (Bean)iter.next();
                bean = pm.detachCopy(bean);
                beans.getImportsAndAliasAndBeen().add(bean);
            }
            tx.commit();

//                logger.debug("# of defined beans : " + ((Beans)obj).getAliasAndImportsAndBeen().size());

                JAXBContext jaxbContext;
                javax.xml.parsers.DocumentBuilderFactory dbf =
    		        javax.xml.parsers.DocumentBuilderFactory.newInstance();
                // XML Signature needs to be namespace aware
        		dbf.setNamespaceAware(true);
                javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
                org.w3c.dom.Document doc = db.newDocument();
                jaxbContext = JAXBContext.newInstance("com.atricore.idbus.console.lifecycle.support.springmetadata.model");

                JAXBElement jaxbRequest = new JAXBElement( 
                		new QName( "http://www.springframework.org/schema/beans", "beans" ),
                		beans.getClass(), beans);
                Marshaller marshaller = jaxbContext.createMarshaller(); 
                marshaller.setProperty("jaxb.schemaLocation", "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd");
                marshaller.marshal(jaxbRequest, doc);
                
                OutputStream os;
                path = (path.endsWith("/") ? path : path + "/" );
                File springConfigFolder = new File(path);
                if(!springConfigFolder.exists()){
                	if(!springConfigFolder.mkdirs()){
                		throw new SpringMetadataManagementException("Failed to create folder(s) " + path);
                	}
                }
        		os = new FileOutputStream(path + "spring-config.xml");
        		TransformerFactory tf = TransformerFactory.newInstance();
        		Transformer trans = tf.newTransformer();
        		trans.transform(new DOMSource(doc), new StreamResult(os));	        		
//            }
            
        } catch (Exception e) {
			logger.error(e);
			throw new SpringMetadataManagementException("Failed to marshal beans", e);
		} finally
        {
            if (tx.isActive()) {
                tx.rollback();
            }

        }
    }

    public PersistenceManagerFactory getPmf() {
        return pmf;
    }

    public void setPmf(PersistenceManagerFactory pmf) {
		this.pmf = pmf;
	}	

}
