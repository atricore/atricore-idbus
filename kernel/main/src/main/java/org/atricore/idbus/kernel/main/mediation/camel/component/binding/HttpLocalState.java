package org.atricore.idbus.kernel.main.mediation.camel.component.binding;

import org.atricore.idbus.kernel.main.mediation.state.AbstractLocalState;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class HttpLocalState extends AbstractLocalState {

    private HttpSession session;

    public HttpLocalState(HttpSession session) {
        super(session.getId());
        this.session = session;
    }

    public void setValue(String key, Object value) {
        session.setAttribute(key, value);
    }

    public Object getValue(String key) {
        return session.getAttribute(key);
    }

    public void removeValue(String key) {
        session.removeAttribute(key);
    }


    public Collection<String> getKeys() {
        Enumeration attrs = session.getAttributeNames();
        List<String> lAttrs = new ArrayList<String>();

        while (attrs.hasMoreElements()) {
            String attr = (String) attrs.nextElement();
            lAttrs.add(attr);
        }

        return lAttrs;
    }

    public boolean isNew() {
        return this.session.isNew();
    }
}
