package com.atricore.idbus.console.lifecycle.main.transform;

import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdProjectSource extends IdProjectResource<String> {

    public IdProjectSource(String id, String name, String type, String value) {
        super(id, name, type, value);
        this.setScope(Scope.SOURCE);
    }


    public IdProjectSource(String id, String nameSpace, String name, String type, String value) {
        super(id, nameSpace, name, type, value);
        this.setScope(Scope.SOURCE);
    }

}
