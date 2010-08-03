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

package org.atricore.idbus.capabilities.management.support.springmetadata.test.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ComplexTestBean {

    private String id;

    private int integerProperty;

    private int[] integerArray;

    private float floatProperty;

    private float[] floatArray;

    private String stringProperty;

    private String[] stringArray;

    private boolean booleanProperty;

    private ComplexTestBean beanProperty;

    private ComplexTestBean[] beansArray;

    private List<ComplexTestBean> beansList;

    private Set<ComplexTestBean> beansSet;

    private Map<String, ComplexTestBean> beansMap;

    public ComplexTestBean() {

    }

    public ComplexTestBean(String id, int integerProperty, String stringProperty, boolean booleanProperty) {
        this.id = id;
        this.integerProperty = integerProperty;
        this.stringProperty = stringProperty;
        this.booleanProperty = booleanProperty;
    }

    public ComplexTestBean(String id, int integerProperty, int[] integerArray, String stringProperty, String[] stringArray, boolean booleanProperty, ComplexTestBean beanProperty, ComplexTestBean[] beansArray, List<ComplexTestBean> beansList, Set<ComplexTestBean> beansSet, Map<String, ComplexTestBean> beansMap) {
        this.id = id;
        this.integerProperty = integerProperty;
        this.integerArray = integerArray;
        this.stringProperty = stringProperty;
        this.stringArray = stringArray;
        this.booleanProperty = booleanProperty;
        this.beanProperty = beanProperty;
        this.beansArray = beansArray;
        this.beansList = beansList;
        this.beansSet = beansSet;
        this.beansMap = beansMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIntegerProperty() {
        return integerProperty;
    }

    public void setIntegerProperty(int integerProperty) {
        this.integerProperty = integerProperty;
    }

    public float getFloatProperty() {
        return floatProperty;
    }

    public void setFloatProperty(float floatProperty) {
        this.floatProperty = floatProperty;
    }

    public float[] getFloatArray() {
        return floatArray;
    }

    public void setFloatArray(float[] floatArray) {
        this.floatArray = floatArray;
    }

    public boolean isBooleanProperty() {
        return booleanProperty;
    }

    public void setBooleanProperty(boolean booleanProperty) {
        this.booleanProperty = booleanProperty;
    }

    public int[] getIntegerArray() {
        return integerArray;
    }

    public void setIntegerArray(int[] integerArray) {
        this.integerArray = integerArray;
    }

    public String getStringProperty() {
        return stringProperty;
    }

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public String[] getStringArray() {
        return stringArray;
    }

    public void setStringArray(String[] stringArray) {
        this.stringArray = stringArray;
    }

    public ComplexTestBean getBeanProperty() {
        return beanProperty;
    }

    public void setBeanProperty(ComplexTestBean beanProperty) {
        this.beanProperty = beanProperty;
    }

    public ComplexTestBean[] getBeansArray() {
        return beansArray;
    }

    public void setBeansArray(ComplexTestBean[] beansArray) {
        this.beansArray = beansArray;
    }

    public List<ComplexTestBean> getBeansList() {
        return beansList;
    }

    public void setBeansList(List<ComplexTestBean> beansList) {
        this.beansList = beansList;
    }

    public Set<ComplexTestBean> getBeansSet() {
        return beansSet;
    }

    public void setBeansSet(Set<ComplexTestBean> beansSet) {
        this.beansSet = beansSet;
    }

    public Map<String, ComplexTestBean> getBeansMap() {
        return beansMap;
    }

    public void setBeansMap(Map<String, ComplexTestBean> beansMap) {
        this.beansMap = beansMap;
    }
}
