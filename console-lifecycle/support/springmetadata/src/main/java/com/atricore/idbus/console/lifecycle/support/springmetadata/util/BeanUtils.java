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

package com.atricore.idbus.console.lifecycle.support.springmetadata.util;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;

/**
 * Created by IntelliJ IDEA.
 * User: Dejan Maric
 * Date: 16.09.2009.
 */
public class BeanUtils {

    private static final Log logger = LogFactory.getLog(BeanUtils.class);

    public static Beans newBeans(String description) {
        Beans b = new Beans();
        if  (description != null) {
            Description d = new Description();
            d.getContent().add(description);
            b.setDescription(d);
        }

        return b;
    }

    public static Bean newAnonymousBean(Class clazz) {
        Bean b = new Bean();
        b.setClazz(clazz.getName());
        return b;
    }

    public static Bean newAnonymousBean(String clazz) {
        Bean b = new Bean();
        b.setClazz(clazz);
        return b;
    }

    public static Bean newBean(Beans beans, String name, Class clazz) {
        return newBean(beans, name, clazz.getName());
    }

    public static Bean newBean(Beans beans, String name, String fqcn) {

        if (logger.isTraceEnabled())
            logger.trace("newBean ["+name+"] " + fqcn);

        if (getBean(beans, name ) != null)
            throw new IllegalArgumentException("Bean name " + name + " already in use!");

        Bean b = new Bean();
        b.setName(name);
        b.setClazz(fqcn);
        beans.getImportsAndAliasAndBeen().add(b);
        return b;
    }

    public static void setBeanDescription(Bean bean, String value) {

        Description descr = new Description();
        descr.getContent().add(value);

        bean.setDescription(descr);

    }

    public static String getBeanDescription(Bean bean) {
        Description d = bean.getDescription();
        if( d == null)
            return null;

        StringBuilder sb = new StringBuilder();
        for (String c : d.getContent())
            sb.append(c);

        return sb.toString();
    }

    public static Bean getBean(Beans beans, String name) {
        for (Object o : beans.getImportsAndAliasAndBeen()) {
            if (o instanceof Bean ) {
                Bean b = (Bean) o;
                if (b.getName() != null && b.getName().equals(name))
                    return b;
            }
        }
        return null;
    }

    /**
     * Class name must be an exact match (no interfaces, etc, supported!) 
     * @param beans
     * @param clazz
     * @return
     */
    public static Collection<Bean> getBeansOfType(Beans beans, String clazz) {
        java.util.List<Bean> beansList = new ArrayList<Bean>();

        if (logger.isTraceEnabled())
            logger.trace("Looking for " + clazz + " in " + beans);

        for (Object o : beans.getImportsAndAliasAndBeen()) {

            if (o instanceof Bean) {
                Bean b = (Bean) o;

                if (logger.isTraceEnabled())
                    logger.trace("Bean: " + b.getName() + "["+b.getClazz()+"]");
                
                if (b.getClazz().equals(clazz))
                    beansList.add(b);
                
                // Look for anonymous children beans ?
            }

        }

        return beansList;

    }


    public static String getPropertyValue(Bean bean, String name){
        Property propFound = null;
        propFound = getProperty(bean, name);
        if(propFound != null && propFound.getValue() != null){
            return propFound.getValue().getContent().get(0);
        } else {
            return null;
        }
    }

    public static Bean getPropertyBean(Bean bean, String name){
        Property propFound = null;
        propFound = getProperty(bean, name);
        if(propFound != null && propFound.getBean() != null){
            return propFound.getBean();
        } else {
            return null;
        }
    }


    public static String getPropertyRef(Bean foundBean, String propertyName) {
        Property prop = getProperty(foundBean, propertyName);
        if (prop == null || prop.getRef() == null)
            return null;

        return prop.getRef().getBean();
    }



    public static void setPropertyRef(Bean bean, String propertyName, String refVal) {
        Property prop = null;
        Ref ref = new Ref();
        ref.setBean(refVal);

        prop = getProperty(bean, propertyName);

        //for now there's no difference if we're updating or creating new property.
        // we'll create property from scratch anyway. Only thing we do if it already exists is to remove old one first.
        if(prop != null){
            bean.getMetasAndConstructorArgsAndProperties().remove(prop);
        }
        prop = new Property();
        prop.setName(propertyName);
        prop.setRef(ref);
        bean.getMetasAndConstructorArgsAndProperties().add(prop);

        if (logger.isTraceEnabled())
            logger.trace("created new property [" + propertyName + "] with bean ref:" + refVal);

    }

    public static void setPropertyBean(Bean bean, String propertyName, Bean propertyValue){
        Property prop = null;

        prop = getProperty(bean, propertyName);

        //for now there's no difference if we're updating or creating new property.
        // we'll create property from scratch anyway. Only thing we do if it already exists is to remove old one first.
        if(prop != null){
            bean.getMetasAndConstructorArgsAndProperties().remove(prop);
        }
        prop = new Property();
        prop.setName(propertyName);
        prop.setBean(propertyValue);
        bean.getMetasAndConstructorArgsAndProperties().add(prop);

        if (logger.isTraceEnabled())
            logger.trace("created new property [" + propertyName + "] with bean ref:" + propertyValue);

    }

    public static void setPropertyValue(Bean bean, String propertyName, boolean propertyValue) {
        Property prop = null;
        Value valueObj = new Value();
        valueObj.getContent().add(Boolean.valueOf(propertyValue).toString());

        prop = getProperty(bean, propertyName);

        //for now there's no difference if we're updating or creating new property.
        // we'll create property from scratch anyway. Only thing we do if it already exists is to remove old one first.
        if(prop != null){
            bean.getMetasAndConstructorArgsAndProperties().remove(prop);
        }
        prop = new Property();
        prop.setName(propertyName);
        prop.setValue(valueObj);
        bean.getMetasAndConstructorArgsAndProperties().add(prop);
        if (logger.isTraceEnabled())
            logger.trace("created new property [" + propertyName + "] with value:" + propertyValue);

    }

    public static void setPropertyValue(Bean bean, String propertyName, String propertyValue){
        Property prop = null;
        Value valueObj = new Value();
        valueObj.getContent().add(propertyValue);

        prop = getProperty(bean, propertyName);

        //for now there's no difference if we're updating or creating new property.
        // we'll create property from scratch anyway. Only thing we do if it already exists is to remove old one first.
        if(prop != null){
            bean.getMetasAndConstructorArgsAndProperties().remove(prop);
        }
        prop = new Property();
        prop.setName(propertyName);
        prop.setValue(valueObj);
        bean.getMetasAndConstructorArgsAndProperties().add(prop);
        if (logger.isTraceEnabled())
            logger.trace("created new property [" + propertyName + "] with value:" + propertyValue);
    }

    public static void setConstructorArgRef(Bean bean, int index, String beanName) {

        Ref refObj = new Ref();
        refObj.setBean(beanName);

        ConstructorArg cArg = new ConstructorArg();
        cArg.setRef(refObj);
        cArg.setIndex(index + "");

        bean.getMetasAndConstructorArgsAndProperties().add(cArg);

        if (logger.isTraceEnabled())
            logger.trace("created new constructor arg referencing [" + index + "] with reference:" + beanName);

    }

    public static void setConstructorArg(Bean bean, int index, String type, String value){
        ConstructorArg cArg = new ConstructorArg();
        Value valueObj = new Value();
        valueObj.getContent().add(value);

        cArg.setType(type);
        cArg.setValue(valueObj);
        cArg.setIndex(index + "");

        bean.getMetasAndConstructorArgsAndProperties().add(cArg);

        if (logger.isTraceEnabled())
            logger.trace("created new constructor arg of type [" + type + "] with value:" + value);
    }

    public static Property getProperty(Bean bean, String name){
        Property propFound = null;
        for (Object obj : bean.getMetasAndConstructorArgsAndProperties()){
            if(obj instanceof Property){
                Property prop = (Property)obj;
                if(prop.getName() != null && prop.getName().equals(name)){
                    propFound = prop;
                    break;
                }
            }
        }
        return propFound;
    }
    
    protected static void addRefToList(com.atricore.idbus.console.lifecycle.support.springmetadata.model.List list, String refValue){
        Ref refObj = new Ref();
        refObj.setBean(refValue);
        list.getBeenAndRevesAndIdreves().add(refObj);
    }

    protected static void addBeanToList(com.atricore.idbus.console.lifecycle.support.springmetadata.model.List list, Bean bean){
        list.getBeenAndRevesAndIdreves().add(bean);
    }

    protected static void addValueToList(com.atricore.idbus.console.lifecycle.support.springmetadata.model.List list, Value value){
        list.getBeenAndRevesAndIdreves().add(value);
    }

    protected static void addValueToSet(com.atricore.idbus.console.lifecycle.support.springmetadata.model.Set set, Value value){
        set.getBeenAndRevesAndIdreves().add(value);
    }

    protected static com.atricore.idbus.console.lifecycle.support.springmetadata.model.List getListOfValuesAndRefs(Bean bean, String name){
        Property prop = getProperty(bean, name);
        if(prop != null && prop.getList() != null){
            return prop.getList();
        }
        return null;
    }

    protected static com.atricore.idbus.console.lifecycle.support.springmetadata.model.Set getSetOfValuesAndRefs(Bean bean, String name){
        Property prop = getProperty(bean, name);
        if(prop != null && prop.getSet() != null){
            return prop.getSet();
        }
        return null;
    }

    protected static void setRefsList(Bean bean, String listName, String[] references) {
        com.atricore.idbus.console.lifecycle.support.springmetadata.model.List list = getListOfValuesAndRefs(bean, listName); //this could be null

        //in case of update, we need to remove all items from the list first
        list.getBeenAndRevesAndIdreves().clear();
        for (String ref : references){
           addRefToList(list, ref);
        }
    }

    public static void setPropertyAsValues(Bean bean, String listName, java.util.Set<String> values) {
        com.atricore.idbus.console.lifecycle.support.springmetadata.model.Set set = null;//getListOfValuesAndRefs(bean, listName);
        Property prop = getProperty(bean, listName);

        if(prop != null){
            bean.getMetasAndConstructorArgsAndProperties().remove(prop);
        }

        set = new com.atricore.idbus.console.lifecycle.support.springmetadata.model.Set();
        prop = new Property();
        prop.setName(listName);

        //in case of update, we need to remove all items from the list first
        set.getBeenAndRevesAndIdreves().clear();
        for (String strValue : values) {
            Value value = new Value();
            value.getContent().add(strValue);

           addValueToSet(set, value);
        }

        prop.setSet(set);
        bean.getMetasAndConstructorArgsAndProperties().add(prop);

    }

    public static void setPropertyAsValues(Bean bean, String listName, java.util.List<String> values) {
        com.atricore.idbus.console.lifecycle.support.springmetadata.model.List list = null;//getListOfValuesAndRefs(bean, listName);
        Property prop = getProperty(bean, listName);

        if(prop != null){
            bean.getMetasAndConstructorArgsAndProperties().remove(prop);
        }

        list = new com.atricore.idbus.console.lifecycle.support.springmetadata.model.List();
        prop = new Property();
        prop.setName(listName);

        //in case of update, we need to remove all items from the list first
        list.getBeenAndRevesAndIdreves().clear();
        for (String strValue : values) {
            Value value = new Value();
            value.getContent().add(strValue);

           addValueToList(list, value);
        }

        prop.setList(list);
        bean.getMetasAndConstructorArgsAndProperties().add(prop);

    }

    public static void setPropertyAsBeans(Bean bean, String listName, java.util.List<Bean> beansToAdd) {
        com.atricore.idbus.console.lifecycle.support.springmetadata.model.List list = null;//getListOfValuesAndRefs(bean, listName);
        Property prop = getProperty(bean, listName);

        if(prop != null){
            bean.getMetasAndConstructorArgsAndProperties().remove(prop);
        }

        list = new com.atricore.idbus.console.lifecycle.support.springmetadata.model.List();
        prop = new Property();
        prop.setName(listName);

        //in case of update, we need to remove all items from the list first
        list.getBeenAndRevesAndIdreves().clear();
        for (Bean beanToAdd : beansToAdd) {
           addBeanToList(list, beanToAdd);
        }

        prop.setList(list);
        bean.getMetasAndConstructorArgsAndProperties().add(prop);
    }

    public static void setPropertyAsRefs(Bean bean, String listName, java.util.List<Bean> beansToAdd){
        com.atricore.idbus.console.lifecycle.support.springmetadata.model.List list = null;
        Property prop = getProperty(bean, listName);

        if(prop != null){
            bean.getMetasAndConstructorArgsAndProperties().remove(prop);
        }
        list = new com.atricore.idbus.console.lifecycle.support.springmetadata.model.List();
        prop = new Property();
        prop.setName(listName);

        //in case of update, we need to remove all items from the list first
//        list.getBeenAndRevesAndIdreves().clear();
        for (Bean beanToAdd : beansToAdd){
            Ref ref = new Ref();
            ref.setBean(beanToAdd.getName());
           list.getBeenAndRevesAndIdreves().add(ref);
        }
        prop.setList(list);
        bean.getMetasAndConstructorArgsAndProperties().add(prop);
    }

    public static void setPropertyRefs(Bean bean, String listName, java.util.List<Ref> refsToAdd){
        com.atricore.idbus.console.lifecycle.support.springmetadata.model.List list = null;
        Property prop = getProperty(bean, listName);

        if(prop != null){
            bean.getMetasAndConstructorArgsAndProperties().remove(prop);
        }
        list = new com.atricore.idbus.console.lifecycle.support.springmetadata.model.List();
        prop = new Property();
        prop.setName(listName);

        for (Ref ref : refsToAdd){
           list.getBeenAndRevesAndIdreves().add(ref);
        }
        prop.setList(list);
        bean.getMetasAndConstructorArgsAndProperties().add(prop);
    }

    public static void setPropertyRefs(Bean bean, String setName, java.util.Set<Ref> refsToAdd){
        com.atricore.idbus.console.lifecycle.support.springmetadata.model.Set set = null;
        Property prop = getProperty(bean, setName);

        if(prop != null){
            bean.getMetasAndConstructorArgsAndProperties().remove(prop);
        }
        set = new com.atricore.idbus.console.lifecycle.support.springmetadata.model.Set();
        prop = new Property();
        prop.setName(setName);

        for (Ref ref : refsToAdd){
           set.getBeenAndRevesAndIdreves().add(ref);
        }
        prop.setSet(set);
        bean.getMetasAndConstructorArgsAndProperties().add(prop);
    }

    public static void setPropertyAsMapEntries(Bean bean, String name, java.util.List<Entry> entriesToAdd){
        com.atricore.idbus.console.lifecycle.support.springmetadata.model.Map map = null;
        Property prop = getProperty(bean, name);

        if(prop != null){
            bean.getMetasAndConstructorArgsAndProperties().remove(prop);
        }
        map = new com.atricore.idbus.console.lifecycle.support.springmetadata.model.Map();
        prop = new Property();
        prop.setName(name);

        map.getEntries().addAll(entriesToAdd);
        prop.setMap(map);
        bean.getMetasAndConstructorArgsAndProperties().add(prop);
    }

    public static void setPropertyValue(Bean bean, String name, java.util.List<Prop> propsToAdd){
        com.atricore.idbus.console.lifecycle.support.springmetadata.model.Props props = null;
        Property prop = getProperty(bean, name);

        if(prop != null){
            bean.getMetasAndConstructorArgsAndProperties().remove(prop);
        }
        props = new com.atricore.idbus.console.lifecycle.support.springmetadata.model.Props();
        prop = new Property();
        prop.setName(name);

        props.getProps().addAll(propsToAdd);
        prop.setProps(props);
        bean.getMetasAndConstructorArgsAndProperties().add(prop);
    }

    public static java.util.List<Bean> getPropertyBeans(Bean bean, String propertyName) {
        com.atricore.idbus.console.lifecycle.support.springmetadata.model.List list = getListOfValuesAndRefs(bean, propertyName);
        java.util.List<Bean> beans = null;
        if(list != null){
            beans = new ArrayList<Bean>();
            for(Object obj : list.getBeenAndRevesAndIdreves()){
                if(obj instanceof Bean){
                    beans.add((Bean)obj);
                }
            }
        }

        return beans;
    }

    public static java.util.List<Ref> getPropertyRefs(Bean bean, String propertyName) {
        com.atricore.idbus.console.lifecycle.support.springmetadata.model.List list = getListOfValuesAndRefs(bean, propertyName);
        java.util.List<Ref> refs = null;
        if(list != null){
            refs = new ArrayList<Ref>();
            for(Object obj : list.getBeenAndRevesAndIdreves()){
                if(obj instanceof Ref){
                    refs.add((Ref)obj);
                }
            }
        }
        return refs;
    }

    public static void addPropertyBeansAsRefs(Bean bean, String listName, Bean beanToAdd) {
        java.util.List<Ref> refsToAdd = new ArrayList<Ref>();
        com.atricore.idbus.console.lifecycle.support.springmetadata.model.List refList = getListOfValuesAndRefs(bean, listName);
        if(refList == null){
            refList = new com.atricore.idbus.console.lifecycle.support.springmetadata.model.List();
        } else {
            for (Object tmpObj : refList.getBeenAndRevesAndIdreves()) {
                if(tmpObj instanceof Ref){
                    refsToAdd.add((Ref)tmpObj);
                }
            }
        }
        Ref ref = new Ref();
        ref.setBean(beanToAdd.getName());
        refsToAdd.add(ref);
        setPropertyRefs(bean, listName, refsToAdd);
    }

    public static void addPropertyBeansAsRefsToSet(Bean bean, String setName, Bean beanToAdd) {
        java.util.Set<Ref> refsToAdd = new LinkedHashSet<Ref>();
        com.atricore.idbus.console.lifecycle.support.springmetadata.model.Set refSet = getSetOfValuesAndRefs(bean, setName);
        if(refSet == null){
            refSet = new com.atricore.idbus.console.lifecycle.support.springmetadata.model.Set();
        } else {
            for (Object tmpObj : refSet.getBeenAndRevesAndIdreves()) {
                if(tmpObj instanceof Ref){
                    refsToAdd.add((Ref)tmpObj);
                }
            }
        }
        Ref ref = new Ref();
        ref.setBean(beanToAdd.getName());
        refsToAdd.add(ref);
        setPropertyRefs(bean, setName, refsToAdd);
    }

    public static void addPropertyRefsToSet(Bean bean, String setName, Ref refToAdd) {
        java.util.Set<Ref> refsToAdd = new LinkedHashSet<Ref>();
        com.atricore.idbus.console.lifecycle.support.springmetadata.model.Set refSet = getSetOfValuesAndRefs(bean, setName);
        if(refSet == null){
            refSet = new com.atricore.idbus.console.lifecycle.support.springmetadata.model.Set();
        } else {
            for (Object tmpObj : refSet.getBeenAndRevesAndIdreves()) {
                if(tmpObj instanceof Ref){
                    refsToAdd.add((Ref)tmpObj);
                }
            }
        }
        Ref ref = refToAdd;
        refsToAdd.add(ref);
        setPropertyRefs(bean, setName, refsToAdd);

    }

    public static void addPropertyRefsToSet(Bean bean, String setName, String refToAdd) {
        java.util.Set<Ref> refsToAdd = new LinkedHashSet<Ref>();
        com.atricore.idbus.console.lifecycle.support.springmetadata.model.Set refSet = getSetOfValuesAndRefs(bean, setName);
        if(refSet == null){
            refSet = new com.atricore.idbus.console.lifecycle.support.springmetadata.model.Set();
        } else {
            for (Object tmpObj : refSet.getBeenAndRevesAndIdreves()) {
                if(tmpObj instanceof Ref){
                    refsToAdd.add((Ref)tmpObj);
                }
            }
        }
        Ref ref = new Ref();
        ref.setBean(refToAdd);
        refsToAdd.add(ref);
        setPropertyRefs(bean, setName, refsToAdd);
    }


    public static void addPropertyBean(Bean bean, String listName, Bean beanToAdd) {
        java.util.List<Bean> beansToAdd = new ArrayList<Bean>();
        com.atricore.idbus.console.lifecycle.support.springmetadata.model.List beanList = getListOfValuesAndRefs(bean, listName);
        if(beanList == null){
            beanList = new com.atricore.idbus.console.lifecycle.support.springmetadata.model.List();
        } else {
            for (Object tmpObj : beanList.getBeenAndRevesAndIdreves()) {
                if(tmpObj instanceof Bean){
                    beansToAdd.add((Bean)tmpObj);
                }
            }
        }
        beansToAdd.add(beanToAdd);
        setPropertyAsBeans(bean, listName, beansToAdd);
    }

    public static void addEntryToMap(Bean bean, String name, Entry entryToAdd) {
        java.util.List<Entry> entriesToAdd = new ArrayList<Entry>();
        com.atricore.idbus.console.lifecycle.support.springmetadata.model.Map map = null;
        Property prop = getProperty(bean, name);
        
        if (prop != null) {
            entriesToAdd.addAll(prop.getMap().getEntries());
            bean.getMetasAndConstructorArgsAndProperties().remove(prop);
        }
        map = new com.atricore.idbus.console.lifecycle.support.springmetadata.model.Map();
        prop = new Property();
        prop.setName(name);

        entriesToAdd.add(entryToAdd);
        map.getEntries().addAll(entriesToAdd);
        prop.setMap(map);
        bean.getMetasAndConstructorArgsAndProperties().add(prop);
    }

    public static void removePropertyBeanRef(Bean bean, String listName, String beanToRemove){
        java.util.List<Ref> refsToAdd = new ArrayList<Ref>();
        com.atricore.idbus.console.lifecycle.support.springmetadata.model.List refList = getListOfValuesAndRefs(bean, listName);
        if(refList == null){
            refList = new com.atricore.idbus.console.lifecycle.support.springmetadata.model.List();
        } else {
            for (Ref ref : refsToAdd) {
                //skip adding beanToRemove to list, that way it will be removed
                if(!ref.getBean().equals(beanToRemove)){
                    refsToAdd.add(ref);
                }
            }
        }
        setPropertyRefs(bean, listName, refsToAdd);
    }

    public static java.util.List<Bean> findBeansMissingInList(java.util.List<Bean> listToSearch,
            java.util.List<Bean> beansToLookFor) {
        java.util.List<Bean> missingItems = new ArrayList<Bean>();

        for(Bean searchedItem : beansToLookFor){
            boolean found = false;
            for(Bean itemToCompare : listToSearch){
                //if(searchedItem.getName().equals(itemToCompare.getName())){
                if(getPropertyValue(searchedItem, "binding").equals(getPropertyValue(itemToCompare, "binding"))
                        && getPropertyValue(searchedItem, "type").equals(getPropertyValue(itemToCompare, "type"))){
                    found = true;
                    break;
                }
            }
            if(!found){
                missingItems.add(searchedItem);
            }
        }
        ;
        return missingItems;
    }

    public static String marshal(Beans beans, ClassLoader cl) throws JAXBException, XMLStreamException {

        JAXBContext ctx = JAXBContext.newInstance("com.atricore.idbus.console.lifecycle.support.springmetadata.model:" +
                "com.atricore.idbus.console.lifecycle.support.springmetadata.model.osgi:" +
                "com.atricore.idbus.console.lifecycle.support.springmetadata.model.tool", cl);
        Marshaller m = ctx.createMarshaller();

        //m.marshal(beans, new FileOutputStream(outputFile, false));


        StringWriter writer = new StringWriter();
        m.marshal(beans, new XmlApplicationContextEnhancer(writer));
        return writer.toString();

    }

    public static String marshal(Beans beans) throws JAXBException, XMLStreamException {
        return marshal(beans, Thread.currentThread().getContextClassLoader());
    }

    public static Beans unmarshal(InputStream is) throws JAXBException {
        return unmarshal(is, Thread.currentThread().getContextClassLoader());
    }

    public static Beans unmarshal(InputStream is, ClassLoader cl) throws JAXBException {

        JAXBContext ctx = JAXBContext.newInstance("com.atricore.idbus.console.lifecycle.support.springmetadata.model:" +
                "com.atricore.idbus.console.lifecycle.support.springmetadata.model.osgi:" +
                "com.atricore.idbus.console.lifecycle.support.springmetadata.model.tool", cl);

        Unmarshaller um  = ctx.createUnmarshaller();

        return (Beans) um.unmarshal(is);
    }

}

