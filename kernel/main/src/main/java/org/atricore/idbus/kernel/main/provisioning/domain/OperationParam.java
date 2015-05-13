package org.atricore.idbus.kernel.main.provisioning.domain;

/**
 * Created by sgonzalez on 5/12/15.
 */
public class OperationParam {

    private String name;

    private String value;

    public OperationParam() {

    }

    public OperationParam(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
