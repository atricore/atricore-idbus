package com.atricore.idbus.console.liveservices.liveupdate.main.profile.impl;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DeepFirstDependencyWalker {

    private DependencyVisitor visitor;

    public void walk(DependencyNode node) {

        visitor.before(node);

        if (node.getParents() != null) {
            for (DependencyNode p : node.getParents()) {
                if (visitor.walNext(p)) {
                    walk(p);
                } else {
                    break;
                }
            }
        }

        visitor.after(node);
    }


}
