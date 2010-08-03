/*
 * Copyright (c) 2010., Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.capabilities.management.support.springmetadata.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.management.support.springmetadata.JDOXmlApplicationContext;
import org.atricore.idbus.capabilities.management.support.springmetadata.test.util.AbstractDBServerTest;
import org.atricore.idbus.capabilities.management.support.springmetadata.test.util.ComplexTestBean;
import org.atricore.idbus.capabilities.management.support.springmetadata.test.util.HelloWorldTestBean;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import java.util.*;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class JDOApplicationContextTest extends AbstractDBServerTest {

    private static final Log logger = LogFactory.getLog(JDOApplicationContextTest.class);

    private static final String BEANS_DEFINITION_JDO_1 = "org/atricore/idbus/capabilities/management/support/springmetadata/test/test-jdo-appctx-1.xml";

    private static final String[] BEANS_DEFINITION_JDO = new String [] {
            "org/atricore/idbus/capabilities/management/support/springmetadata/test/test-jdo-appctx-2.xml"};

    private static final String MSG_VALUE="Hello, World!";

    @BeforeClass
    public static void setupDB() throws Exception {

        importBeans(BEANS_DEFINITION_JDO_1);

        for (String s : BEANS_DEFINITION_JDO) {
            importBeans(s);
        }


    }

    @Test
    public void testWithoutTransaction() {
        PersistenceManager pm = pmf.getPersistenceManager();
        ApplicationContext appCtx = new JDOXmlApplicationContext(pmf, BEANS_DEFINITION_JDO_1);

        HelloWorldTestBean hwBean = (HelloWorldTestBean) appCtx.getBean("helloWorldName-1");
        assert hwBean.getMessage().equals(MSG_VALUE) :
                "Invalid mess value received/expected ["+hwBean.getMessage()+"/"+MSG_VALUE+"]";
        
    }

    @Test
    public void testWithTransaction() {

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();
            ApplicationContext appCtx = new JDOXmlApplicationContext(pmf, BEANS_DEFINITION_JDO_1);

            HelloWorldTestBean hwBean = (HelloWorldTestBean) appCtx.getBean("helloWorldName-1");
            assert hwBean.getMessage().equals(MSG_VALUE) :
                    "Invalid message value received/expected ["+hwBean.getMessage()+"/"+MSG_VALUE+"]";

            assert tx.isActive() : "Transaction is no longer active!";
            tx.commit();
        } finally {
            assert !tx.isActive() : "Transaction is still active!";

        }
    }

    @Test
    public void testComplexDefinitions() throws Exception {
        for (String s : BEANS_DEFINITION_JDO) {
            logger.info("Testing definition " + s);
            testComplexDefinition(s);
        }
    }

    protected void testComplexDefinition(String beansDefinitionResource) throws Exception {

        PersistenceManager pm = pmf.getPersistenceManager();

        // Create an application context using spring classpath xml application
        ApplicationContext cpAppCtx = new ClassPathXmlApplicationContext(beansDefinitionResource);
        logger.info("ClasspathXmlApplicationContext:" + cpAppCtx.getDisplayName());

        // Create an application context using atricore jdo xml application context
        ApplicationContext jdoAppCtx = new JDOXmlApplicationContext(pmf, beansDefinitionResource);
        logger.info("JDOXmlApplicationContext:" + jdoAppCtx.getDisplayName());


        // Make sure that beans defined in cpAppCtx are all present in jdoAppCtx, with the same properties!
        assert cpAppCtx.getBeanDefinitionCount() == jdoAppCtx.getBeanDefinitionCount() :
                "Beand definition count don't match CP vs JDO [" + cpAppCtx.getBeanDefinitionCount() + "/" + jdoAppCtx.getBeanDefinitionCount() + "]";

        String [] cpBeanNames = cpAppCtx.getBeanDefinitionNames();
        for (String cpBeanName : cpBeanNames) {
            ComplexTestBean cpBean = (ComplexTestBean) cpAppCtx.getBean(cpBeanName);
            ComplexTestBean jdoBean = (ComplexTestBean) jdoAppCtx.getBean(cpBeanName);

            assertEquivalent(cpBean, jdoBean);
        }
    }

    protected void assertEquivalent(ComplexTestBean bean1, ComplexTestBean bean2) {

        logger.debug("Comparing " + bean1 + " with " + bean2);

        if (bean1 == null && bean2 == null)
            return;

        assert bean1 != null && bean2 != null : "One of the beans is null [" + bean1 + "] vs [" + bean2 + "]";

        logger.debug("Comparing bean " + bean1.getId() + " with " + bean2.getId());

        // IDs property
        if (bean1.getId() != null && bean2.getId() != null) {
            assert bean1.getId().equals(bean2.getId()) :
                 "Id doesn't match ["+bean1.getId()+"] vs ["+bean2.getId()+"]";
        } else {
            assert bean1.getId() == null && bean2.getId() == null :
                    "Id doesn't match ["+bean1.getId()+"] vs ["+bean2.getId()+"]";
        }

        // Primitive properties :
        assert bean1.getIntegerProperty() == bean2.getIntegerProperty() : 
                "IntegerProperty doesn't match ["+bean1.getIntegerProperty()+"] vs ["+bean2.getIntegerProperty()+"]";

        assert bean1.getFloatProperty() == bean2.getFloatProperty() :
                "FloatProperty doesn't match ["+bean1.getFloatProperty()+"] vs ["+bean2.getFloatProperty()+"]";

        if (bean1.getStringProperty() != null && bean2.getStringProperty() != null) {
            assert bean1.getStringProperty().equals(bean2.getStringProperty()) :
                 "StringProperty doesn't match ["+bean1.getStringProperty()+"] vs ["+bean2.getStringProperty()+"]";
        } else {
            assert bean1.getStringProperty() == null && bean2.getStringProperty() == null :
                    "StringProperty doesn't match ["+bean1.getStringProperty()+"] vs ["+bean2.getStringProperty()+"]"; 
        }
        
        assert bean1.isBooleanProperty() == bean2.isBooleanProperty() :
                "BooleanProperty doesn't match ["+bean1.isBooleanProperty()+"] vs ["+bean2.isBooleanProperty()+"]";


        if (bean1.getIntegerArray() != null) {
            
            if (logger.isTraceEnabled()) {
                String values = java.util.Arrays.toString(bean1.getIntegerArray());
                logger.trace("IntegerArray ("+bean1.getIntegerArray().length+") " + values);
            }

            assert bean2.getIntegerArray() != null : "IntegerArray doesn't match size [" + bean1.getIntegerArray().length + "] vs null";
            assert bean1.getIntegerArray().length == bean2.getIntegerArray().length :
                    "IntegerArray doesn't match size [" + bean1.getIntegerArray().length + "] vs ["+bean2.getIntegerArray().length+"]";

            for (int i = 0; i < bean1.getIntegerArray().length; i++) {
                assert bean1.getIntegerArray()[i] == bean2.getIntegerArray()[i] :
                        "IntegerArray doesn't match idx ("+i+") ["+bean1.getIntegerArray()[i]+"] vs ["+bean2.getIntegerArray()[i]+"]";

            }
        }
        
        if (bean1.getFloatArray() != null) {
            
            if (logger.isTraceEnabled()) {
                String values = java.util.Arrays.toString(bean1.getFloatArray());
                logger.trace("FloatArray ("+bean1.getFloatArray().length+") " + values);
            }

            assert bean2.getFloatArray() != null : "FloatArray doesn't match size [" + bean1.getFloatArray().length + "] vs null";
            assert bean1.getFloatArray().length == bean2.getFloatArray().length :
                    "FloatArray doesn't match size [" + bean1.getFloatArray().length + "] vs ["+bean2.getFloatArray().length+"]";

            for (int i = 0; i < bean1.getFloatArray().length; i++) {
                assert bean1.getFloatArray()[i] == bean2.getFloatArray()[i] :
                        "FloatArray doesn't match idx ("+i+") ["+bean1.getFloatArray()[i]+"] vs ["+bean2.getFloatArray()[i]+"]";

            }
        }
        
        
        if (bean1.getStringArray() != null) {
            
            if (logger.isTraceEnabled()) {
                String values = java.util.Arrays.toString(bean1.getStringArray());
                logger.trace("StringArray ("+bean1.getStringArray().length+") " + values);
            }

            assert bean2.getStringArray() != null : "StringArray doesn't match size [" + bean1.getStringArray().length + "] vs null";
            assert bean1.getStringArray().length == bean2.getStringArray().length :
                    "StringArray doesn't match size [" + bean1.getStringArray().length + "] vs ["+bean2.getStringArray().length+"]";

            for (int i = 0; i < bean1.getStringArray().length; i++) {
                // TODO : Support null?
                assert bean1.getStringArray()[i].equals(bean2.getStringArray()[i]) :
                        "StringArray doesn't match idx ("+i+") ["+bean1.getStringArray()[i]+"] vs ["+bean2.getStringArray()[i]+"]";

            }
        }
        
        // Complex types
        
        assertEquivalent(bean1.getBeanProperty(), bean2.getBeanProperty());
        
        if (bean1.getBeansList() != null) {
            
            if (logger.isTraceEnabled()) {
                logger.trace("BeansList ("+bean1.getBeansList().size()+") ");
            }
            assert bean2.getBeansList() != null : "BeansList doesn't match size [" + bean1.getBeansList().size() + "] vs null";
            assert bean1.getBeansList().size() == bean2.getBeansList().size() :
                    "BeansList doesn't match size [" + bean1.getBeansList().size()+ "] vs ["+bean2.getBeansList().size() + "]";

            // Need to be carefull with order ?!
            for (int i = 0; i < bean1.getBeansList().size(); i++) {
                ComplexTestBean complexTestBean1 = bean1.getBeansList().get(i);
                ComplexTestBean complexTestBean2 = bean2.getBeansList().get(i);

                assertEquivalent(complexTestBean1, complexTestBean2);

            }
            
        }
        
        if (bean1.getBeansSet() != null) {
            
            if (logger.isTraceEnabled()) {
                logger.trace("BeansSet ("+bean1.getBeansSet().size()+") ");
            }
            assert bean2.getBeansSet() != null : "BeansSet doesn't match size [" + bean1.getBeansSet().size() + "] vs null";
            assert bean1.getBeansSet().size() == bean2.getBeansSet().size() :
                    "BeansSet doesn't match size [" + bean1.getBeansSet().size()+ "] vs ["+bean2.getBeansSet().size() + "]";

            // Need to be carefull with order ?!

            ComplexTestBean[] complexTestBeans2 = bean2.getBeansSet().toArray(new ComplexTestBean[bean2.getBeansSet().size()]);
            Map<String, ComplexTestBean> beans2  = new HashMap<String, ComplexTestBean>();
            for (ComplexTestBean complexTestBean2 : complexTestBeans2) {
                beans2.put(complexTestBean2.getId(), complexTestBean2);
            }

            for (ComplexTestBean complexTestBean1 : bean1.getBeansSet()) {
                ComplexTestBean complexTestBean2 = beans2.get(complexTestBean1.getId());
                assert complexTestBean2 != null : "BeansSet doesn't match bean id " + complexTestBean1.getId() + ", not found";
                assertEquivalent(complexTestBean1, complexTestBean2);
            }

        }
        
        
        if (bean1.getBeansArray() != null) {
            
            if (logger.isTraceEnabled()) {
                logger.trace("BeansArray ("+bean1.getBeansArray().length+") ");
            }
            assert bean2.getBeansArray() != null : "BeansArray doesn't match size [" + bean1.getBeansArray().length + "] vs null";
            assert bean1.getBeansArray().length == bean2.getBeansArray().length :
                    "BeansArray doesn't match size [" + bean1.getBeansArray().length+ "] vs ["+bean2.getBeansArray().length + "]";

            // Need to be carefull with order ?!
            for (int i = 0; i < bean1.getBeansArray().length; i++) {
                ComplexTestBean complexTestBean1 = bean1.getBeansArray()[i];
                ComplexTestBean complexTestBean2 = bean2.getBeansArray()[i];

                assertEquivalent(complexTestBean1, complexTestBean2);

            }
            
        }
        
        if (bean1.getBeansMap() != null) {
            
            assert bean2.getBeansMap() != null : "BeansMap doesn't match size [" + bean1.getBeansMap().size()+ "] vs null";
            assert bean1.getBeansMap().size()== bean2.getBeansMap().size():
                    "BeansMap doesn't match size [" + bean1.getBeansMap().size()+ "] vs ["+bean2.getBeansMap().size()+ "]";
            
            Map<String, ComplexTestBean> map1 = bean1.getBeansMap();
            Map<String, ComplexTestBean> map2 = bean2.getBeansMap();

            for (String key : map1.keySet()) {
                assert map2.containsKey(key) : "BeansMap doesn't match, key not found " + key;
            }

            for (String key : map1.keySet()) {
                assertEquivalent(map1.get(key), map2.get(key));
            }

        }
        

        logger.info("Comparing bean " + bean1.getId() + " with " + bean2.getId() + " ... OK!");

    }

}