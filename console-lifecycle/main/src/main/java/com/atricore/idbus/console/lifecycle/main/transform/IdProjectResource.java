package com.atricore.idbus.console.lifecycle.main.transform;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdProjectResource<T> {

    private String id;

    private String name;

    private String nameSpace;

    private String type;

    private String classifier;

    private Scope scope;

    private T value;

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

    @Override
    public String toString() {
        return "{" + id + "}:" + nameSpace + "/" + name + "/" + type + "/" + classifier + " value=["+value+"]";
    }

    public enum Scope {
        PROJECT,
        RESOURCE,
        SOURCE;
    }
    
}
