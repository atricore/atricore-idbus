package org.atricore.idbus.capabilities.sso.main.common;

public class RequestImpl<T> implements Request<T> {
    private String id;
    private T message;

    public RequestImpl(String id, T message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public T getMessage() {
        return message;
    }

}
