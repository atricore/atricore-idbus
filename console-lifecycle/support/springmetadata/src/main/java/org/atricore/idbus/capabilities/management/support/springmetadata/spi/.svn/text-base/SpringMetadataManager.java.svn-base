/*
 * Copyright (c) 2009., Atricore Inc.
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

package org.atricore.idbus.capabilities.management.support.springmetadata.spi;

import java.util.List;

import javax.jdo.PersistenceManagerFactory;

import org.atricore.idbus.capabilities.management.support.springmetadata.exception.SpringMetadataManagementException;
import org.atricore.idbus.capabilities.management.support.springmetadata.model.Bean;
import org.atricore.idbus.capabilities.management.support.springmetadata.model.BeansDefinition;

public interface SpringMetadataManager {

    void saveBeansDefinition(BeansDefinition beansDef) throws SpringMetadataManagementException;

    void removeBeansDefinition(String name) throws SpringMetadataManagementException;

    BeansDefinition findBeansDefinition(String name) throws SpringMetadataManagementException;

    @Deprecated
    void saveBeanList(List<Bean> beanList) throws SpringMetadataManagementException;

    @Deprecated
    void saveBean(Bean bean) throws SpringMetadataManagementException;

    @Deprecated
    void updateBeanList(List<Bean> beanList) throws SpringMetadataManagementException;

    @Deprecated
    void updateBean(Bean updatedBean) throws SpringMetadataManagementException;

    @Deprecated
    void removeBeanList(List<String> beanNames) throws SpringMetadataManagementException;

    @Deprecated
    void removeBean(String beanName) throws SpringMetadataManagementException;

    @Deprecated
    Bean findBeanByName(String beanName) throws SpringMetadataManagementException;

    @Deprecated
    void marshallBeans(String path) throws SpringMetadataManagementException;

    @Deprecated
    void setPmf(PersistenceManagerFactory pmf);
}
