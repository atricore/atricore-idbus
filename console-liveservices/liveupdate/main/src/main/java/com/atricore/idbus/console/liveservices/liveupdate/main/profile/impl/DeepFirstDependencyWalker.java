package com.atricore.idbus.console.liveservices.liveupdate.main.profile.impl;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DeepFirstDependencyWalker<T> implements DependencyWalker<T> {

    public T walk(DependencyNode node, DependencyVisitor<T> visitor) {
        walkNode(node, visitor);
        return visitor.getResult();
    }

    protected void walkNode(DependencyNode node, DependencyVisitor<T> visitor) {

        visitor.before(node);

        if (node.getParents() != null) {
            for (DependencyNode p : node.getParents()) {
                if (visitor.walNext(p)) {
                    walk(p, visitor);
                } else {
                    break;
                }
            }
        }

        visitor.after(node);
    }


}
