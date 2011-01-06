package com.atricore.idbus.console.tooling.granite;

import org.granite.generator.as3.As3Type;
import org.granite.generator.as3.DefaultAs3TypeFactory;
import org.granite.util.ClassUtil;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ATCConsoleAs3TypeFactory extends DefaultAs3TypeFactory {

    @Override
    protected As3Type createAs3Type(Class<?> jType) {
        String name = jType.getSimpleName();
        if (name.endsWith("DTO"))
            name = name.substring(0, name.length() - 3);

        if (jType.isMemberClass())
                name = jType.getEnclosingClass().getSimpleName() + '$' + jType.getSimpleName();
        return new As3Type(ClassUtil.getPackageName(jType), name);
    }
}
