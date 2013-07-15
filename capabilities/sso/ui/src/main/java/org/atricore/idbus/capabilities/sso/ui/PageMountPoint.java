package org.atricore.idbus.capabilities.sso.ui;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 7/12/13
 */
public class PageMountPoint implements java.io.Serializable {

    private String path;

    private Class clazz;

    public PageMountPoint(String path, Class clazz) {
        this.path = path;
        this.clazz = clazz;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Class getPageClass() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
}
