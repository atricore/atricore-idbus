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

package com.atricore.idbus.console.services.test;

import com.atricore.idbus.console.lifecycle.support.springmetadata.exception.SpringMetadataManagementException;
import com.atricore.idbus.console.lifecycle.support.springmetadata.impl.SpringMetadataManagerImpl;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Bean;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Property;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Ref;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.Value;
import com.atricore.idbus.console.lifecycle.support.springmetadata.spi.SpringMetadataManager;
import com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils;
import org.junit.Before;
import org.junit.Test;

public class SpringMetadataManagementTest {
	
	private BeanUtils beanUtils;
	private SpringMetadataManager smm;

    @Before
	public void setUp() throws SpringMetadataManagementException{

		smm = new SpringMetadataManagerImpl();
		Bean bean = new Bean();
		bean.setId("id1");
		bean.setName("name1");
		
		Property prop1 = new Property();
		prop1.setName("prop1-value");
		Value val1 = new Value();
		val1.getContent().add("val1");
		prop1.setValue(val1);
		
		bean.getMetasAndConstructorArgsAndProperties().add(prop1);
		
		Property prop2 = new Property();
		prop2.setName("prop2-ref");
		Ref ref2 = new Ref();
		ref2.setBean("some_new_bean");
		prop2.setRef(ref2);
		
		bean.getMetasAndConstructorArgsAndProperties().add(prop2);
		
		smm.saveBean(bean);		
	}

	@Test
	public void testUpdate() throws SpringMetadataManagementException{
		Bean updatedBean = new Bean();
		updatedBean.setId("id1");
		updatedBean.setName("name1");
		
		Property prop1 = new Property();
		prop1.setName("prop1-value");
		Value val1 = new Value();
		val1.getContent().add("val1_u");
		prop1.setValue(val1);
		
		updatedBean.getMetasAndConstructorArgsAndProperties().add(prop1);
		
		Property prop2 = new Property();
		prop2.setName("prop2-ref");
		Ref ref2 = new Ref();
		ref2.setBean("some_new_bean_u");
		prop2.setRef(ref2);
		
		updatedBean.getMetasAndConstructorArgsAndProperties().add(prop2);		
		smm.updateBean(updatedBean);
		
		Bean foundBean = smm.findBeanByName("name1");
		assert foundBean != null : "Bean's name not updated";
		assert foundBean.getName().equals("name1") : "Name not correct";
		assert beanUtils.getPropertyValue(foundBean, "prop1-value") != null
			&& beanUtils.getPropertyValue(foundBean, "prop1-value").equals("val1_u") : "property value not updated correctly";
		assert beanUtils.getPropertyRef(foundBean, "prop2-ref") != null
			&& beanUtils.getPropertyRef(foundBean, "prop2-ref").equals("some_new_bean_u") : "property ref not updated correctly";
		
	}
}
