package org.atricore.idbus.kernel.main.authn.util;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.provisioning.domain.UserAttributeValue;

import java.lang.reflect.InvocationTargetException;

public class UserUtil {

    private static final Log logger = LogFactory.getLog(UserUtil.class);

    /**
     * Get user attribute
     * @param user
     * @param name
     * @return
     */
    public static UserAttributeValue getExtendedAttribute(User user, String name) {
        // Try from extended attributes
        if (user.getAttrs() == null)
            return null;

        for (UserAttributeValue attr : user.getAttrs()) {
            if (attr.getName().equals(name))
                return attr;
        }

        return null;
    }

    /**
     * Get user attribute
     * @param user
     * @param name
     * @return
     */
    public static String getExtendedAttributeValue(User user, String name) {
        // Try from extended attributes
        if (user.getAttrs() == null)
            return null;

        for (UserAttributeValue attr : user.getAttrs()) {
            if (attr.getName().equals(name))
                return attr.getValue();
        }

        return null;

    }

    /**
     * Get a User property.  This may be a built-in property (i.e. userName) or a value for an extended attribute
     *
     * @param user
     * @param propertyName
     * @return
     */
    public static String getProperty(User user, String propertyName) {

        // Try reflection:

        try {

            String propertyValue = new BeanUtilsBean().getProperty(user, propertyName);

            return propertyValue;
        } catch (NoSuchMethodException e) {
            logger.trace(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }

        return getExtendedAttributeValue(user, propertyName);

    }
}
