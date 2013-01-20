/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.util;

import org.jboss.logging.Logger;

/**
 * A util class to get and set properties of objects via appropriately-generated
 * accessors/mutators
 *
 * @author kenmin
 */
public class ReflectionUtils {

    private static Logger LOGGER = Logger.getLogger(ReflectionUtils.class);

    /**
     * Gets the value of a field (using properly generated accessors).
     *
     * @param object The object to be introspected.
     * @param fqFieldName The fully qualified field name, dot-separated
     * @return The value of the field.
     */
    public static Object getPropertyValue(Object object, String fqFieldName) {
        Object returnValue = null;
        if (fqFieldName != null) {
            try {
                String[] fieldNames = fqFieldName.split("\\.");
                if (fieldNames.length <= 1) {
                    String filterProperty = (fieldNames.length == 1) ? fieldNames[0] : fqFieldName;
                    return object.getClass().getMethod("get" + filterProperty.substring(0, 1).toUpperCase() + filterProperty.substring(1), (Class<?>[]) null).invoke(object, (Object[]) null);
                } else if (fieldNames.length > 1) {
                    String filterProperty = fieldNames[0];
                    Object o = object.getClass().getMethod("get" + filterProperty.substring(0, 1).toUpperCase() + filterProperty.substring(1), (Class<?>[]) null).invoke(object, (Object[]) null);
                    return getPropertyValue((Object) o, fqFieldName.substring(filterProperty.length() + 1));
                }
            } catch (Exception e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(e.getMessage());
                }
            }
        }
        return returnValue;
    }

    /**
     * Sets the value of an object using Reflection using properly generated
     * mutators. Only works for first-level variables (currently).
     *
     * @param object The object to be reflected.
     * @param propName The property name to be set.
     * @param propValue The value to set the property to.
     */
    public static void setPropertyValue(Object object, String propName, Object propValue) {
        try {
            Class clazz;
            if (propValue.getClass() == java.sql.Timestamp.class) {
                clazz = java.util.Date.class;
            } else {
                clazz = propValue.getClass();
            }
            Class<?>[] paramType = {clazz};
            object.getClass().getMethod("set" + propName.substring(0, 1).toUpperCase() + propName.substring(1), (Class<?>[]) paramType).invoke(object, propValue);
        } catch (Exception e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("There was an error setting property " + propName + " to " + propValue + ". Error message: " + e.getMessage());
            }
        }
    }
}
