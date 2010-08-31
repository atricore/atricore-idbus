/*
 * Copyright (c) 2009., Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.lifecycle.main.transform;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.TransformException;
import com.atricore.idbus.console.lifecycle.support.springmetadata.impl.SpringMetadataManagerImpl;
import com.atricore.idbus.console.lifecycle.support.springmetadata.spi.SpringMetadataManager;

import java.util.*;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class TransformerVisitor implements IdentityApplianceDefinitionVisitor {

    private static Log logger = LogFactory.getLog(TransformerVisitor.class);
    
    private static final String FEDERATED_CONN_ROLE_ATTR = "federatedConnectionRole";

    private SpringMetadataManager springMgr = new SpringMetadataManagerImpl();

    private java.util.List<Transformer> transformers = new ArrayList<Transformer>();
    
    private static ThreadLocal<IdApplianceTransformationContext> contextHolder = new ThreadLocal<IdApplianceTransformationContext>();

    public List<Transformer> getTransformers() {
        return transformers;
    }

    public void setTransformers(List<Transformer> transformers) {
        this.transformers = transformers;
    }

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
     public void arrive(IdentityApplianceDefinition node) {
        arrive(contextHolder.get(), node);
     }

    /**
     * This method is called after walking the children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to compute the result of walking the argument <code>node</code>
     * and it's children. This result is returned by the tree walker's
     * <code>walk</code> method. The argument <code>results</code> holds
     * the results of walking the children of the argument <code>node</code>.
     * Usually, the result of the argument <code>node</code> is computed in
     * consideration of the results of its' children.
     *
     * @param node    the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    public Object[] leave(IdentityApplianceDefinition node, Object[] results) {
        return leave(contextHolder.get(), node, results);
    }

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     * In case of returning <code>false</code>, none of the remaining
     * children are walked. Instead, the node's leave method is called
     * immediately. The argument <code>resultOfPreviousChild</code>
     * holds the result of walking the previous child of the argument
     * <code>node</code>. Usually, it is used to determine the return value
     * of this method. The argument <code>indexOfNextChild</code>
     * determines the index of the next child to be walked. This index
     * determines the position in the children array of the argument <code>node</code>.
     * Note: The index of the first child is 0.
     *
     * @param node                  the parent node of the children currently walked
     * @param resultOfPreviousChild the result of walking the node's previous child
     * @param indexOfNextChild      the index of the next child to be walked
     * @return <code>false</code>, if no more childs should be walked, else <code>true</code>
     */
    public boolean walkNextChild(IdentityApplianceDefinition node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    public void arrive(ServiceProvider node) {
        arrive(contextHolder.get(), node);
    }

    /**
     * This method is called after walking the children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to compute the result of walking the argument <code>node</code>
     * and it's children. This result is returned by the tree walker's
     * <code>walk</code> method. The argument <code>results</code> holds
     * the results of walking the children of the argument <code>node</code>.
     * Usually, the result of the argument <code>node</code> is computed in
     * consideration of the results of its' children.
     *
     * @param node    the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    public Object[] leave(ServiceProvider node, Object[] results) {
        return leave(contextHolder.get(), node, results);
    }

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     * In case of returning <code>false</code>, none of the remaining
     * children are walked. Instead, the node's leave method is called
     * immediately. The argument <code>resultOfPreviousChild</code>
     * holds the result of walking the previous child of the argument
     * <code>node</code>. Usually, it is used to determine the return value
     * of this method. The argument <code>indexOfNextChild</code>
     * determines the index of the next child to be walked. This index
     * determines the position in the children array of the argument <code>node</code>.
     * Note: The index of the first child is 0.
     *
     * @param node                  the parent node of the children currently walked
     * @param resultOfPreviousChild the result of walking the node's previous child
     * @param indexOfNextChild      the index of the next child to be walked
     * @return <code>false</code>, if no more childs should be walked, else <code>true</code>
     */
    public boolean walkNextChild(ServiceProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }


    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    public void arrive(BindingProvider node) {
        arrive(contextHolder.get(), node);
    }

    /**
     * This method is called after walking the children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to compute the result of walking the argument <code>node</code>
     * and it's children. This result is returned by the tree walker's
     * <code>walk</code> method. The argument <code>results</code> holds
     * the results of walking the children of the argument <code>node</code>.
     * Usually, the result of the argument <code>node</code> is computed in
     * consideration of the results of its' children.
     *
     * @param node    the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    public Object[] leave(BindingProvider node, Object[] results) {
        return leave(contextHolder.get(), node, results);
    }

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     * In case of returning <code>false</code>, none of the remaining
     * children are walked. Instead, the node's leave method is called
     * immediately. The argument <code>resultOfPreviousChild</code>
     * holds the result of walking the previous child of the argument
     * <code>node</code>. Usually, it is used to determine the return value
     * of this method. The argument <code>indexOfNextChild</code>
     * determines the index of the next child to be walked. This index
     * determines the position in the children array of the argument <code>node</code>.
     * Note: The index of the first child is 0.
     *
     * @param node                  the parent node of the children currently walked
     * @param resultOfPreviousChild the result of walking the node's previous child
     * @param indexOfNextChild      the index of the next child to be walked
     * @return <code>false</code>, if no more childs should be walked, else <code>true</code>
     */
    public boolean walkNextChild(BindingProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    public void arrive(IdentityProvider node) {
        arrive(contextHolder.get(), node);
    }

    /**
     * This method is called after walking the children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to compute the result of walking the argument <code>node</code>
     * and it's children. This result is returned by the tree walker's
     * <code>walk</code> method. The argument <code>results</code> holds
     * the results of walking the children of the argument <code>node</code>.
     * Usually, the result of the argument <code>node</code> is computed in
     * consideration of the results of its' children.
     *
     * @param node    the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    public Object[] leave(IdentityProvider node, Object[] results) {
        return leave(contextHolder.get(), node, results);
    }

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     * In case of returning <code>false</code>, none of the remaining
     * children are walked. Instead, the node's leave method is called
     * immediately. The argument <code>resultOfPreviousChild</code>
     * holds the result of walking the previous child of the argument
     * <code>node</code>. Usually, it is used to determine the return value
     * of this method. The argument <code>indexOfNextChild</code>
     * determines the index of the next child to be walked. This index
     * determines the position in the children array of the argument <code>node</code>.
     * Note: The index of the first child is 0.
     *
     * @param node                  the parent node of the children currently walked
     * @param resultOfPreviousChild the result of walking the node's previous child
     * @param indexOfNextChild      the index of the next child to be walked
     * @return <code>false</code>, if no more childs should be walked, else <code>true</code>
     */
    public boolean walkNextChild(IdentityProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    public void arrive(IdentityProviderChannel node) {
        arrive(contextHolder.get(), node);
    }

    /**
     * This method is called after walking the children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to compute the result of walking the argument <code>node</code>
     * and it's children. This result is returned by the tree walker's
     * <code>walk</code> method. The argument <code>results</code> holds
     * the results of walking the children of the argument <code>node</code>.
     * Usually, the result of the argument <code>node</code> is computed in
     * consideration of the results of its' children.
     *
     * @param node    the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    public Object[] leave(IdentityProviderChannel node, Object[] results) {
        return leave(contextHolder.get(), node, results);
    }

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     * In case of returning <code>false</code>, none of the remaining
     * children are walked. Instead, the node's leave method is called
     * immediately. The argument <code>resultOfPreviousChild</code>
     * holds the result of walking the previous child of the argument
     * <code>node</code>. Usually, it is used to determine the return value
     * of this method. The argument <code>indexOfNextChild</code>
     * determines the index of the next child to be walked. This index
     * determines the position in the children array of the argument <code>node</code>.
     * Note: The index of the first child is 0.
     *
     * @param node                  the parent node of the children currently walked
     * @param resultOfPreviousChild the result of walking the node's previous child
     * @param indexOfNextChild      the index of the next child to be walked
     * @return <code>false</code>, if no more childs should be walked, else <code>true</code>
     */
    public boolean walkNextChild(IdentityProviderChannel node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    public void arrive(ServiceProviderChannel node) {
        arrive(contextHolder.get(), node);
    }

    /**
     * This method is called after walking the children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to compute the result of walking the argument <code>node</code>
     * and it's children. This result is returned by the tree walker's
     * <code>walk</code> method. The argument <code>results</code> holds
     * the results of walking the children of the argument <code>node</code>.
     * Usually, the result of the argument <code>node</code> is computed in
     * consideration of the results of its' children.
     *
     * @param node    the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    public Object[] leave(ServiceProviderChannel node, Object[] results) {
        return leave(contextHolder.get(), node, results);
    }

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     * In case of returning <code>false</code>, none of the remaining
     * children are walked. Instead, the node's leave method is called
     * immediately. The argument <code>resultOfPreviousChild</code>
     * holds the result of walking the previous child of the argument
     * <code>node</code>. Usually, it is used to determine the return value
     * of this method. The argument <code>indexOfNextChild</code>
     * determines the index of the next child to be walked. This index
     * determines the position in the children array of the argument <code>node</code>.
     * Note: The index of the first child is 0.
     *
     * @param node                  the parent node of the children currently walked
     * @param resultOfPreviousChild the result of walking the node's previous child
     * @param indexOfNextChild      the index of the next child to be walked
     * @return <code>false</code>, if no more childs should be walked, else <code>true</code>
     */
    public boolean walkNextChild(ServiceProviderChannel node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    public void arrive(IdentitySource node) {
        arrive(contextHolder.get(), node);
    }

    /**
     * This method is called after walking the children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to compute the result of walking the argument <code>node</code>
     * and it's children. This result is returned by the tree walker's
     * <code>walk</code> method. The argument <code>results</code> holds
     * the results of walking the children of the argument <code>node</code>.
     * Usually, the result of the argument <code>node</code> is computed in
     * consideration of the results of its' children.
     *
     * @param node    the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    public Object[] leave(IdentitySource node, Object[] results) {
        return leave(contextHolder.get(), node, results);
    }

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     * In case of returning <code>false</code>, none of the remaining
     * children are walked. Instead, the node's leave method is called
     * immediately. The argument <code>resultOfPreviousChild</code>
     * holds the result of walking the previous child of the argument
     * <code>node</code>. Usually, it is used to determine the return value
     * of this method. The argument <code>indexOfNextChild</code>
     * determines the index of the next child to be walked. This index
     * determines the position in the children array of the argument <code>node</code>.
     * Note: The index of the first child is 0.
     *
     * @param node                  the parent node of the children currently walked
     * @param resultOfPreviousChild the result of walking the node's previous child
     * @param indexOfNextChild      the index of the next child to be walked
     * @return <code>false</code>, if no more childs should be walked, else <code>true</code>
     */
    public boolean walkNextChild(IdentitySource node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    public void arrive(IdentityLookup node) {
        arrive(contextHolder.get(), node);
    }

    /**
     * This method is called after walking the children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to compute the result of walking the argument <code>node</code>
     * and it's children. This result is returned by the tree walker's
     * <code>walk</code> method. The argument <code>results</code> holds
     * the results of walking the children of the argument <code>node</code>.
     * Usually, the result of the argument <code>node</code> is computed in
     * consideration of the results of its' children.
     *
     * @param node    the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    public Object[] leave(IdentityLookup node, Object[] results) {
        return leave(contextHolder.get(), node, results);
    }

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     * In case of returning <code>false</code>, none of the remaining
     * children are walked. Instead, the node's leave method is called
     * immediately. The argument <code>resultOfPreviousChild</code>
     * holds the result of walking the previous child of the argument
     * <code>node</code>. Usually, it is used to determine the return value
     * of this method. The argument <code>indexOfNextChild</code>
     * determines the index of the next child to be walked. This index
     * determines the position in the children array of the argument <code>node</code>.
     * Note: The index of the first child is 0.
     *
     * @param node                  the parent node of the children currently walked
     * @param resultOfPreviousChild the result of walking the node's previous child
     * @param indexOfNextChild      the index of the next child to be walked
     * @return <code>false</code>, if no more childs should be walked, else <code>true</code>
     */
    public boolean walkNextChild(IdentityLookup node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    public void arrive(Activation node) {
        arrive(contextHolder.get(), node);
    }

    /**
     * This method is called after walking the children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to compute the result of walking the argument <code>node</code>
     * and it's children. This result is returned by the tree walker's
     * <code>walk</code> method. The argument <code>results</code> holds
     * the results of walking the children of the argument <code>node</code>.
     * Usually, the result of the argument <code>node</code> is computed in
     * consideration of the results of its' children.
     *
     * @param node    the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    public Object[] leave(Activation node, Object[] results) {
        return leave(contextHolder.get(), node, results);
    }

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     * In case of returning <code>false</code>, none of the remaining
     * children are walked. Instead, the node's leave method is called
     * immediately. The argument <code>resultOfPreviousChild</code>
     * holds the result of walking the previous child of the argument
     * <code>node</code>. Usually, it is used to determine the return value
     * of this method. The argument <code>indexOfNextChild</code>
     * determines the index of the next child to be walked. This index
     * determines the position in the children array of the argument <code>node</code>.
     * Note: The index of the first child is 0.
     *
     * @param node                  the parent node of the children currently walked
     * @param resultOfPreviousChild the result of walking the node's previous child
     * @param indexOfNextChild      the index of the next child to be walked
     * @return <code>false</code>, if no more childs should be walked, else <code>true</code>
     */
    public boolean walkNextChild(Activation node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    public void arrive(FederatedConnection node) {

        // See also ReflexiveIdentityApplianceDefinitionWalker, it also contains specific logic to handle connections

        IdApplianceTransformationContext ctx = contextHolder.get();

        Object parentNode = ctx.peek();
        if (node.getRoleA() == parentNode) {
            ctx.put(FEDERATED_CONN_ROLE_ATTR, "roleA");
        } else if (node.getRoleB() == parentNode) {
            ctx.put(FEDERATED_CONN_ROLE_ATTR, "roleB");
        } else {
            throw new RuntimeException("Parent node " + parentNode + " does not match connection provider A nor B");
        }
        arrive(contextHolder.get(), node);
    }

    /**
     * This method is called after walking the children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to compute the result of walking the argument <code>node</code>
     * and it's children. This result is returned by the tree walker's
     * <code>walk</code> method. The argument <code>results</code> holds
     * the results of walking the children of the argument <code>node</code>.
     * Usually, the result of the argument <code>node</code> is computed in
     * consideration of the results of its' children.
     *
     * @param node    the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    public Object[] leave(FederatedConnection node, Object[] results) {
        IdApplianceTransformationContext ctx = contextHolder.get();
        Object[] result = leave(contextHolder.get(), node, results);
        ctx.put(FEDERATED_CONN_ROLE_ATTR, null);

        return result;
    }

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     * In case of returning <code>false</code>, none of the remaining
     * children are walked. Instead, the node's leave method is called
     * immediately. The argument <code>resultOfPreviousChild</code>
     * holds the result of walking the previous child of the argument
     * <code>node</code>. Usually, it is used to determine the return value
     * of this method. The argument <code>indexOfNextChild</code>
     * determines the index of the next child to be walked. This index
     * determines the position in the children array of the argument <code>node</code>.
     * Note: The index of the first child is 0.
     *
     * @param node                  the parent node of the children currently walked
     * @param resultOfPreviousChild the result of walking the node's previous child
     * @param indexOfNextChild      the index of the next child to be walked
     * @return <code>false</code>, if no more childs should be walked, else <code>true</code>
     */
    public boolean walkNextChild(FederatedConnection node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {

        IdApplianceTransformationContext ctx = contextHolder.get();
        String role = (String) ctx.get(FEDERATED_CONN_ROLE_ATTR);

        // Do not treat providers as 'children' of this node.
        if (child instanceof Provider)
            return false;

        if (child instanceof Channel) {
            if (role.equalsIgnoreCase("roleA")) {
                return child == node.getChannelA();
            } else if (role.equalsIgnoreCase("roleB")) {
                return child == node.getChannelB();
            } else {
                throw new RuntimeException("No role information found in context for FederatedConnection " + node.getName());
            }
        }

        // We should walk all other children
        return true;
    }

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    public void arrive(ExecutionEnvironment node) {
        arrive(contextHolder.get(), node);
    }

    /**
     * This method is called after walking the children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to compute the result of walking the argument <code>node</code>
     * and it's children. This result is returned by the tree walker's
     * <code>walk</code> method. The argument <code>results</code> holds
     * the results of walking the children of the argument <code>node</code>.
     * Usually, the result of the argument <code>node</code> is computed in
     * consideration of the results of its' children.
     *
     * @param node    the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    public Object[] leave(ExecutionEnvironment node, Object[] results) {
        return leave(contextHolder.get(), node, results);
    }

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     * In case of returning <code>false</code>, none of the remaining
     * children are walked. Instead, the node's leave method is called
     * immediately. The argument <code>resultOfPreviousChild</code>
     * holds the result of walking the previous child of the argument
     * <code>node</code>. Usually, it is used to determine the return value
     * of this method. The argument <code>indexOfNextChild</code>
     * determines the index of the next child to be walked. This index
     * determines the position in the children array of the argument <code>node</code>.
     * Note: The index of the first child is 0.
     *
     * @param node                  the parent node of the children currently walked
     * @param resultOfPreviousChild the result of walking the node's previous child
     * @param indexOfNextChild      the index of the next child to be walked
     * @return <code>false</code>, if no more childs should be walked, else <code>true</code>
     */
    public boolean walkNextChild(ExecutionEnvironment node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    public void arrive(EmbeddedIdentitySource node) {
        arrive(contextHolder.get(), node);
    }

    /**
     * This method is called after walking the children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to compute the result of walking the argument <code>node</code>
     * and it's children. This result is returned by the tree walker's
     * <code>walk</code> method. The argument <code>results</code> holds
     * the results of walking the children of the argument <code>node</code>.
     * Usually, the result of the argument <code>node</code> is computed in
     * consideration of the results of its' children.
     *
     * @param node    the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    public Object[] leave(EmbeddedIdentitySource node, Object[] results) {
        return leave(contextHolder.get(), node, results);
    }

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     * In case of returning <code>false</code>, none of the remaining
     * children are walked. Instead, the node's leave method is called
     * immediately. The argument <code>resultOfPreviousChild</code>
     * holds the result of walking the previous child of the argument
     * <code>node</code>. Usually, it is used to determine the return value
     * of this method. The argument <code>indexOfNextChild</code>
     * determines the index of the next child to be walked. This index
     * determines the position in the children array of the argument <code>node</code>.
     * Note: The index of the first child is 0.
     *
     * @param node                  the parent node of the children currently walked
     * @param resultOfPreviousChild the result of walking the node's previous child
     * @param indexOfNextChild      the index of the next child to be walked
     * @return <code>false</code>, if no more childs should be walked, else <code>true</code>
     */
    public boolean walkNextChild(EmbeddedIdentitySource node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    public void arrive(LdapIdentitySource node) {
        arrive(contextHolder.get(), node);
    }

    /**
     * This method is called after walking the children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to compute the result of walking the argument <code>node</code>
     * and it's children. This result is returned by the tree walker's
     * <code>walk</code> method. The argument <code>results</code> holds
     * the results of walking the children of the argument <code>node</code>.
     * Usually, the result of the argument <code>node</code> is computed in
     * consideration of the results of its' children.
     *
     * @param node    the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    public Object[] leave(LdapIdentitySource node, Object[] results) {
        return leave(contextHolder.get(), node, results);
    }

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     * In case of returning <code>false</code>, none of the remaining
     * children are walked. Instead, the node's leave method is called
     * immediately. The argument <code>resultOfPreviousChild</code>
     * holds the result of walking the previous child of the argument
     * <code>node</code>. Usually, it is used to determine the return value
     * of this method. The argument <code>indexOfNextChild</code>
     * determines the index of the next child to be walked. This index
     * determines the position in the children array of the argument <code>node</code>.
     * Note: The index of the first child is 0.
     *
     * @param node                  the parent node of the children currently walked
     * @param resultOfPreviousChild the result of walking the node's previous child
     * @param indexOfNextChild      the index of the next child to be walked
     * @return <code>false</code>, if no more childs should be walked, else <code>true</code>
     */
    public boolean walkNextChild(LdapIdentitySource node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    public void arrive(DbIdentitySource node) {
        arrive(contextHolder.get(), node);
    }

    /**
     * This method is called after walking the children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to compute the result of walking the argument <code>node</code>
     * and it's children. This result is returned by the tree walker's
     * <code>walk</code> method. The argument <code>results</code> holds
     * the results of walking the children of the argument <code>node</code>.
     * Usually, the result of the argument <code>node</code> is computed in
     * consideration of the results of its' children.
     *
     * @param node    the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    public Object[] leave(DbIdentitySource node, Object[] results) {
        return leave(contextHolder.get(), node, results);
    }

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     * In case of returning <code>false</code>, none of the remaining
     * children are walked. Instead, the node's leave method is called
     * immediately. The argument <code>resultOfPreviousChild</code>
     * holds the result of walking the previous child of the argument
     * <code>node</code>. Usually, it is used to determine the return value
     * of this method. The argument <code>indexOfNextChild</code>
     * determines the index of the next child to be walked. This index
     * determines the position in the children array of the argument <code>node</code>.
     * Note: The index of the first child is 0.
     *
     * @param node                  the parent node of the children currently walked
     * @param resultOfPreviousChild the result of walking the node's previous child
     * @param indexOfNextChild      the index of the next child to be walked
     * @return <code>false</code>, if no more childs should be walked, else <code>true</code>
     */
    public boolean walkNextChild(DbIdentitySource node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    public void arrive(Location node) {
        arrive(contextHolder.get(), node);
    }

    /**
     * This method is called after walking the children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to compute the result of walking the argument <code>node</code>
     * and it's children. This result is returned by the tree walker's
     * <code>walk</code> method. The argument <code>results</code> holds
     * the results of walking the children of the argument <code>node</code>.
     * Usually, the result of the argument <code>node</code> is computed in
     * consideration of the results of its' children.
     *
     * @param node    the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    public Object[] leave(Location node, Object[] results) {
        return leave(contextHolder.get(), node, results);
    }

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     * In case of returning <code>false</code>, none of the remaining
     * children are walked. Instead, the node's leave method is called
     * immediately. The argument <code>resultOfPreviousChild</code>
     * holds the result of walking the previous child of the argument
     * <code>node</code>. Usually, it is used to determine the return value
     * of this method. The argument <code>indexOfNextChild</code>
     * determines the index of the next child to be walked. This index
     * determines the position in the children array of the argument <code>node</code>.
     * Note: The index of the first child is 0.
     *
     * @param node                  the parent node of the children currently walked
     * @param resultOfPreviousChild the result of walking the node's previous child
     * @param indexOfNextChild      the index of the next child to be walked
     * @return <code>false</code>, if no more childs should be walked, else <code>true</code>
     */
    public boolean walkNextChild(Location node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    public void arrive(JOSSOActivation node) {
        arrive(contextHolder.get(), node);
    }

    /**
     * This method is called after walking the children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to compute the result of walking the argument <code>node</code>
     * and it's children. This result is returned by the tree walker's
     * <code>walk</code> method. The argument <code>results</code> holds
     * the results of walking the children of the argument <code>node</code>.
     * Usually, the result of the argument <code>node</code> is computed in
     * consideration of the results of its' children.
     *
     * @param node    the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    public Object[] leave(JOSSOActivation node, Object[] results) {
        return leave(contextHolder.get(), node, results);
    }

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     * In case of returning <code>false</code>, none of the remaining
     * children are walked. Instead, the node's leave method is called
     * immediately. The argument <code>resultOfPreviousChild</code>
     * holds the result of walking the previous child of the argument
     * <code>node</code>. Usually, it is used to determine the return value
     * of this method. The argument <code>indexOfNextChild</code>
     * determines the index of the next child to be walked. This index
     * determines the position in the children array of the argument <code>node</code>.
     * Note: The index of the first child is 0.
     *
     * @param node                  the parent node of the children currently walked
     * @param resultOfPreviousChild the result of walking the node's previous child
     * @param indexOfNextChild      the index of the next child to be walked
     * @return <code>false</code>, if no more childs should be walked, else <code>true</code>
     */
    public boolean walkNextChild(JOSSOActivation node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    // ---------------------------------------------------< Utils >

    public void setContext(IdApplianceTransformationContext ctx) {
        contextHolder.set(ctx);
    }

    protected void arrive(IdApplianceTransformationContext ctx, Object node) {
        TransformEvent event = new TransformEventImpl(ctx, node, null);
        ctx.push(node);

        for (Transformer transformer : transformers) {

            if (transformer.accept(event)) {
                try {
                    if (logger.isTraceEnabled())
                        logger.trace("before        -> " + transformer.getClass().getSimpleName());
                    transformer.before(event);
                } catch (TransformException e) {
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            } else {
                if (logger.isTraceEnabled())
                    logger.trace("skip before   -> " + transformer.getClass().getSimpleName());
            }
        }
    }

    protected Object[] leave(IdApplianceTransformationContext ctx, Object node, Object[] results) {
        
        List<Object> newResults = new ArrayList<Object>();
        
        TransformEvent event = new TransformEventImpl(ctx, node, results);
        for (Transformer transformer : transformers) {
            if (transformer.accept(event)) {
                try {
                    if (logger.isTraceEnabled())
                        logger.trace("after         -> " + transformer.getClass().getSimpleName());
                    newResults.add(transformer.after(event));
                } catch (TransformException e) {
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            } else {
                if (logger.isTraceEnabled())
                    logger.trace("skip after    -> " + transformer.getClass().getSimpleName());
            }
        }

        ctx.pop();
        return newResults.toArray(new Object[newResults.size()]);
    }
}
