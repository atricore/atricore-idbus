package org.atricore.idbus.kernel.main.session;

import javax.security.auth.Subject;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by sgonzalez.
 */
public class SSOSessionContext implements Serializable {

    private Subject subject;

    private Properties props = new Properties();

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setProperty(String key, String value) {
        this.props.setProperty(key, value);
    }

    public String getProperty(String key) {
        return props.getProperty(key);
    }

    public Enumeration getProperties() {
        return props.propertyNames();
    }
}
