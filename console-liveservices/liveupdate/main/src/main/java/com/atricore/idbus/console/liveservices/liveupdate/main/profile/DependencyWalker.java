package com.atricore.idbus.console.liveservices.liveupdate.main.profile;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface DependencyWalker<T> {

    T walk(DependencyNode node, DependencyVisitor<T> v);

}
