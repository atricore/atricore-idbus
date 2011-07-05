package com.atricore.idbus.console.activation.main.spi.request;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ConfigureAgentResource  {

    private String resource;

    private String name;

    public ConfigureAgentResource() {

    }

    public ConfigureAgentResource(String name, String configResourceContent) {
        this.name = name;
        this.resource = configResourceContent;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
