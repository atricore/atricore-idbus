package com.atricore.idbus.console.liveservices.liveupdate.main.profile.impl;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface DependencyVisitor<T> {

    void before(DependencyNode dep);

    void after(DependencyNode node);

    boolean walNext(DependencyNode node);

    T getResult();
}
