package com.atricore.idbus.console.lifecycle.main.transform;

import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdProjectResource<T> {

    private String id;

    private String name;

    private String nameSpace;

    private String type;

    private String subtype;

    private String classifier;

    private Scope scope;

    private T value;

    private String extension;

    private Map<String, Object> params;


    public IdProjectResource(String id, String name, String type, T value) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.value = value;
        this.scope = Scope.RESOURCE;
    }


    public IdProjectResource(String id, String nameSpace, String name, String type, T value) {
        this(id, name, type, value);
        this.nameSpace = nameSpace;
    }

    public IdProjectResource(String id, String nameSpace, String name, String type, String subtype, T value) {
        this(id, nameSpace, name, type, value);
        this.subtype = subtype;
    }


    public String getId() {
        return id;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }


    public T getValue() {
        return value;
    }

    public String getClassifier() {
        return classifier;
    }

    public Scope getScope() {
        return scope;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "{" + id + "}:" + nameSpace + "/" + name + "/" + type + "/" + subtype + "/" + classifier + " value=["+value+"]";
    }

    public enum Scope {
        PROJECT,
        RESOURCE,
        SOURCE;
    }
    
}
