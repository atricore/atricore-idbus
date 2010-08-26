package com.atricore.idbus.console.lifecycle.main.test;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.transform.IdentityApplianceDefinitionVisitor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Stack;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class TestIdentityApplianceDefinitionVisitor implements IdentityApplianceDefinitionVisitor {

    private static final Log logger = LogFactory.getLog(TestIdentityApplianceDefinitionVisitor.class);
    
    private Stack nodes = new Stack();


    public void arrive(IdentityApplianceDefinition node) {
        nodes.push(node);
		logger.info(getPrintableNode("Arrive", node));
        
    }

    public Object[] leave(IdentityApplianceDefinition node, Object[] results) {
        assert node == nodes.pop(); logger.info(getPrintableNode("Leave", node));
		return new Object[0];  
    }

    public boolean walkNextChild(IdentityApplianceDefinition node, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(ServiceProvider node) {
        nodes.push(node);
		logger.info(getPrintableNode("Arrive", node));
    }

    public Object[] leave(ServiceProvider node, Object[] results) {
        assert node == nodes.pop(); logger.info(getPrintableNode("Leave", node));
		return new Object[0];  
    }

    public void arrive(IdentitySource node) {
        nodes.push(node);
		logger.info(getPrintableNode("Arrive", node));
    }

    public Object[] leave(IdentitySource node, Object[] results) {
        assert node == nodes.pop(); logger.info(getPrintableNode("Leave", node));
		return new Object[0];
    }

    public boolean walkNextChild(IdentitySource node, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    public boolean walkNextChild(ServiceProvider node, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(IdentityProvider node) {
        nodes.push(node);
		logger.info(getPrintableNode("Arrive", node));
    }

    public Object[] leave(IdentityProvider node, Object[] results) {
        assert node == nodes.pop(); logger.info(getPrintableNode("Leave", node));
		return new Object[0];  
    }

    public boolean walkNextChild(IdentityProvider node, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(IdentityProviderChannel node) {
        nodes.push(node);
		logger.info(getPrintableNode("Arrive", node));
    }

    public Object[] leave(IdentityProviderChannel node, Object[] results) {
        assert node == nodes.pop(); logger.info(getPrintableNode("Leave", node));
		return new Object[0];  
    }

    public boolean walkNextChild(IdentityProviderChannel node, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(ServiceProviderChannel node) {
        nodes.push(node);
		logger.info(getPrintableNode("Arrive", node));
    }

    public Object[] leave(ServiceProviderChannel node, Object[] results) {
        assert node == nodes.pop(); logger.info(getPrintableNode("Leave", node));
		return new Object[0];  
    }

    public boolean walkNextChild(ServiceProviderChannel node, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(EmbeddedIdentitySource node) {
        nodes.push(node);
		logger.info(getPrintableNode("Arrive", node));
    }

    public Object[] leave(EmbeddedIdentitySource node, Object[] results) {
        assert node == nodes.pop(); logger.info(getPrintableNode("Leave", node));
		return new Object[0];
    }

    public boolean walkNextChild(EmbeddedIdentitySource node, Object resultOfPreviousChild, int indexOfNextChild) {
        return false;
    }

    public void arrive(LdapIdentitySource node) {
        nodes.push(node);
		logger.info(getPrintableNode("Arrive", node));
    }

    public Object[] leave(LdapIdentitySource node, Object[] results) {
        assert node == nodes.pop(); logger.info(getPrintableNode("Leave", node));
		return new Object[0];
    }

    public boolean walkNextChild(LdapIdentitySource node, Object resultOfPreviousChild, int indexOfNextChild) {
        return false;
    }

    public void arrive(DbIdentitySource node) {
        nodes.push(node);
		logger.info(getPrintableNode("Arrive", node));
    }

    public Object[] leave(DbIdentitySource node, Object[] results) {
        assert node == nodes.pop(); logger.info(getPrintableNode("Leave", node));
		return new Object[0];
    }

    public boolean walkNextChild(DbIdentitySource node, Object resultOfPreviousChild, int indexOfNextChild) {
        return false;
    }

    public void arrive(JOSSOActivation node) {
        nodes.push(node);
		logger.info(getPrintableNode("Arrive", node));
    }

    public Object[] leave(JOSSOActivation node, Object[] results) {
        assert node == nodes.pop(); logger.info(getPrintableNode("Leave", node));
		return new Object[0];
    }

    public boolean walkNextChild(JOSSOActivation node, Object resultOfPreviousChild, int indexOfNextChild) {
        return false;
    }

    public void arrive(Location node) {
        nodes.push(node);
		logger.info(getPrintableNode("Arrive", node));
    }

    public Object[] leave(Location node, Object[] results) {
        assert node == nodes.pop(); logger.info(getPrintableNode("Leave", node));
		return new Object[0];
    }

    public boolean walkNextChild(Location node, Object resultOfPreviousChild, int indexOfNextChild) {
        return false;
    }

    public void arrive(FederatedConnection node) {
        nodes.push(node);
		logger.info(getPrintableNode("Arrive", node));
    }

    public Object[] leave(FederatedConnection node, Object[] results) {
        assert node == nodes.pop(); logger.info(getPrintableNode("Leave", node));
		return new Object[0];  
    }

    public boolean walkNextChild(FederatedConnection node, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(IdentityLookup node) {
        nodes.push(node);
		logger.info(getPrintableNode("Arrive", node));
    }

    public Object[] leave(IdentityLookup node, Object[] results) {
        assert node == nodes.pop(); logger.info(getPrintableNode("Leave", node));
		return new Object[0];  
    }

    public boolean walkNextChild(IdentityLookup node, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(ExecutionEnvironment node) {
        nodes.push(node);
		logger.info(getPrintableNode("Arrive", node));
    }

    public Object[] leave(ExecutionEnvironment node, Object[] results) {
        assert node == nodes.pop(); logger.info(getPrintableNode("Leave", node));
		return new Object[0];  
    }

    public boolean walkNextChild(ExecutionEnvironment node, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(Activation node) {
        nodes.push(node);
		logger.info(getPrintableNode("Arrive", node));
    }

    public Object[] leave(Activation node, Object[] results) {
        assert node == nodes.pop(); logger.info(getPrintableNode("Leave", node));
		return new Object[0];  
    }

    public boolean walkNextChild(Activation node, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    protected String getPrintableNode(String action, Object node) {
        StringBuffer sb = new StringBuffer();
        rightPad(sb, action, 7);
        sb.append("NODE (");
        leftPad(sb, nodes.size() + "", 3);
        sb.append(")");

        // ID
        sb.append("[");
        try {
            String id = org.apache.commons.beanutils.BeanUtils.getProperty(node, "id");
            rightPad(sb, id, 6);
        } catch (Exception e) {
            rightPad(sb, "--", 6);
        }
        sb.append("] ");
        
        // Name
        sb.append("[");
        try {
            String name = org.apache.commons.beanutils.BeanUtils.getProperty(node, "name");
            rightPad(sb, name, 21);
        } catch (Exception e) {
            rightPad(sb, "--", 21);
        }
        sb.append("] ");

        // Simple Class name
        sb.append("[");
        rightPad(sb, node.getClass().getSimpleName(), 48);
        sb.append("] ");

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
