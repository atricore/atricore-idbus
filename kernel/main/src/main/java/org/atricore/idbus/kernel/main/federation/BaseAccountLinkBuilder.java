package org.atricore.idbus.kernel.main.federation;

import javax.security.auth.Subject;
import java.util.Map;

public abstract class BaseAccountLinkBuilder<T extends BaseAccountLinkBuilder> implements AccountLinkBuilder<BaseAccountLinkBuilder> {

    protected Subject idpSubject;

    protected String localAccount;

    protected String accountFormat;

    protected boolean isDeleted = false;

    protected boolean isEnabled = true;

    protected Map<String, String> props;

    public BaseAccountLinkBuilder() {
        this.props = new java.util.HashMap();
    }

    @Override
    public T idpSubject(Subject subject) {
        idpSubject = subject;
        return (T) this;
    }

    @Override
    public T localAccount(String localAccount) {
        this.localAccount = localAccount;
        return (T) this;
    }

    @Override
    public T accountFormat(String accountFormat) {
        this.accountFormat = accountFormat;
        return (T) this;
    }

    @Override
    public T deleted(boolean deleted) {
        isDeleted = deleted;
        return (T) this;
    }

    @Override
    public T enabled(boolean enabled) {
        isEnabled = enabled;
        return (T) this;
    }

    @Override
    public T property(String property, String value) {
        this.props.put(property, value);
        return (T) this;
    }



    public T properties(Map<String, String> props) {
        this.props = props;
        return (T)  this;
    }


}
