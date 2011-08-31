package org.atricore.idbus.capabilities.sso.main.common;

public interface Response<T> {

    String getId();

    T getMessage();

    boolean isCommitted();

}
