package com.atricore.idbus.console.lifecycle.main.test.util;

import com.atricore.idbus.console.lifecycle.main.transform.IdApplianceTransformationContext;
import com.atricore.idbus.console.lifecycle.main.transform.TransformerVisitor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class TestIdApplianceLoggerVisitor extends TransformerVisitor {

    private static final Log logger = LogFactory.getLog(TestIdApplianceLoggerVisitor.class);

    private int depth = 0;

    @Override
    protected void arrive(IdApplianceTransformationContext ctx, Object node) {
        depth ++;
        super.arrive(ctx, node);
        logger.info(getPrintableNode(ctx, "IN", node));
    }

    @Override
    protected Object[] leave(IdApplianceTransformationContext ctx, Object node, Object[] results) {

        super.leave(ctx, node, results);
        logger.info(getPrintableNode(ctx, "OUT", node));
        depth --;
        return null;
    }

    protected String getPrintableNode(IdApplianceTransformationContext ctx, String action, Object node) {

        StringBuffer sb = new StringBuffer();

        rightPad(sb, action, 3);

        // Name
        try {
            String name = org.apache.commons.beanutils.BeanUtils.getProperty(node, "name");
            sb.append(" [");
            rightPad(sb, name, 28);
            sb.append("]");
        } catch (Exception e) {
            sb.append(" [");
            rightPad(sb, node.getClass().getSimpleName(), 28);
            sb.append("]");
        }

        sb.append(" ");
        // Simple Class name
        for ( int i = 0 ; i < depth ; i++) {
            sb.append("++");
        }
        
        return sb.toString();
    }

    protected void rightPad(StringBuffer sb, String text, int length) {
        sb.append(text);
        for (int i = text.length() ; i < length ; i ++) {
            sb.append(" ");
        }
    }

    protected void leftPad(StringBuffer sb, String text, int length) {

        for (int i = text.length() ; i < length ; i ++) {
            sb.append(" ");
        }

        sb.append(text);
    }

    

}
