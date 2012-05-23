package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import com.atricore.idbus.console.lifecycle.main.transform.annotations.IgnoreChildren;
import com.atricore.idbus.console.lifecycle.main.transform.annotations.ReEntrant;

import java.io.Serializable;

@ReEntrant
public abstract class ServiceResource implements Serializable {

    private static final long serialVersionUID = 2524797144868455174L;

    private long id;

    private String name;

    private String description;

    private ServiceConnection serviceConnection;

    private Activation activation;

    private double x;
    private double y;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @IgnoreChildren
    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    public void setServiceConnection(ServiceConnection serviceConnection) {
        this.serviceConnection = serviceConnection;
    }

    @IgnoreChildren
    public Activation getActivation() {
        return activation;
    }

    public void setActivation(Activation activation) {
        this.activation = activation;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceResource)) return false;

        ServiceResource resource = (ServiceResource) o;

        if(id == 0) return false;

        if (id != resource.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
