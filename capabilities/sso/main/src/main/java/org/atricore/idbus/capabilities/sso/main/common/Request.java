package org.atricore.idbus.capabilities.sso.main.common;

public interface Request<T> {

    String getId();

    T getMessage();

}
