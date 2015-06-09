package org.atricore.idbus.capabilities.sso.main.emitter.plans.actions.attributes;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class AttributeProfileRegistry {

    private Map<String, SamlR2AttributeProfileMapper> mappers = new HashMap<String, SamlR2AttributeProfileMapper>();

    public void init() {

    }

    public SamlR2AttributeProfileMapper resolveMapper(String name) {
        return mappers.get(name);
    }

    public void register(SamlR2AttributeProfileMapper mapper) {
        if (mappers.get(mapper.getName()) != null)
            throw new RuntimeException("Mapper already registered for name " + mapper.getName());

        mappers.put(mapper.getName(), mapper);
    }

    public void unregister(SamlR2AttributeProfileMapper mapper) {
        mappers.remove(mapper.getName());
    }

    public Collection<SamlR2AttributeProfileMapper> getMappers() {
        return mappers.values();
    }

    public void setBuiltInMappers(List<SamlR2AttributeProfileMapper> builtInStrategies) {
        for (SamlR2AttributeProfileMapper mapper : builtInStrategies) {
            register(mapper);
        }
    }
}
