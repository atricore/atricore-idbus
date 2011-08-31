package org.atricore.idbus.capabilities.sso.main.common;

public class ResponseImpl<T> implements Response<T> {
    private String id;
    private T message;
    private boolean isCommitted;

    public ResponseImpl(String id, T message, boolean isCommitted) {
        this.id = id;
        this.message = message;
        this.isCommitted = isCommitted;
    }

    public String getId() {
        return id;
    }

    public T getMessage() {
        return message;
    }

    public boolean isCommitted() {
        return isCommitted;
    }
}
