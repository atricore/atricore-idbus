package com.atricore.idbus.console.liveservices.liveupdate.main.profile.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.profile.DependencyNode;
import com.atricore.idbus.console.liveservices.liveupdate.main.profile.DependencyVisitor;
import com.atricore.idbus.console.liveservices.liveupdate.main.profile.DependencyWalker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DeepFirstDependencyWalker<T> implements DependencyWalker<T> {

    private static final Log logger = LogFactory.getLog(DeepFirstDependencyWalker.class);

    public T walk(DependencyNode node, DependencyVisitor<T> visitor) {
        walkNode(node, visitor);
        return visitor.getResult();
    }

    protected void walkNode(DependencyNode node, DependencyVisitor<T> visitor) {

        if (logger.isTraceEnabled())
            logger.trace("Before " + node.getFqKey());

        visitor.before(node);

        if (node.getParents() != null) {

            for (DependencyNode p : node.getParents()) {


                if (visitor.walNext(p)) {
                    if (logger.isTraceEnabled())
                        logger.trace("Walk next " + node.getFqKey() + " => " + p.getFqKey());
                    walkNode(p, visitor);
                } else {
                    break;
                }
            }
        }
        if (logger.isTraceEnabled())
            logger.trace("After " + node.getFqKey());

        visitor.after(node);
    }


}
