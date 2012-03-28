package com.atricore.idbus.console.brandservice.main.domain;

import java.io.Serializable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class BrandingResource implements Serializable {

    private static final long serialVersionUID = 871536646583264968L;
    
    private String id;
    
    private String path;

    private byte[] content;

    private boolean shared;
    
    private BrandingResourceType type;
    
    private String condition;
    
    private String skin;
    
    private String locale;
    
    private String priority;
}
