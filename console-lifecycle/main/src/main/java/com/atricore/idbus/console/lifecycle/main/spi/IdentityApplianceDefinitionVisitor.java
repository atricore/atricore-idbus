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

package com.atricore.idbus.console.lifecycle.main.spi;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface IdentityApplianceDefinitionVisitor {

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(IdentityApplianceDefinition node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(IdentityApplianceDefinition node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(IdentityApplianceDefinition node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(SelfServices node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(SelfServices node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(SelfServices node, Object child, Object resultOfPreviousChild, int indexOfNextChild);



    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(InternalSaml2ServiceProvider node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(InternalSaml2ServiceProvider node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(InternalSaml2ServiceProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild);


    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(IdentityProvider node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(IdentityProvider node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(IdentityProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild);
    
    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(ExternalSaml2IdentityProvider node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(ExternalSaml2IdentityProvider node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(ExternalSaml2IdentityProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(ExternalSaml2ServiceProvider node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(ExternalSaml2ServiceProvider node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(ExternalSaml2ServiceProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(ExternalOpenIDIdentityProvider node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(ExternalOpenIDIdentityProvider node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(ExternalOpenIDIdentityProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(ExternalOAuth2IdentityProvider node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(ExternalOAuth2IdentityProvider node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(ExternalOAuth2IdentityProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(OAuth2ServiceProvider node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(OAuth2ServiceProvider node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(OAuth2ServiceProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(SalesforceServiceProvider node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(SalesforceServiceProvider node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(SalesforceServiceProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(GoogleAppsServiceProvider node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(GoogleAppsServiceProvider node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(GoogleAppsServiceProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(SugarCRMServiceProvider node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(SugarCRMServiceProvider node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(SugarCRMServiceProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild);
    
    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(IdentityProviderChannel node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(IdentityProviderChannel node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(IdentityProviderChannel node, Object child, Object resultOfPreviousChild, int indexOfNextChild);
    
    
    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(ServiceProviderChannel node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(ServiceProviderChannel node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(ServiceProviderChannel node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(IdentitySource node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(IdentitySource node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(IdentitySource node, Object child, Object resultOfPreviousChild, int indexOfNextChild);    

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(EmbeddedIdentitySource node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(EmbeddedIdentitySource node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(EmbeddedIdentitySource node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(LdapIdentitySource node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(LdapIdentitySource node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(LdapIdentitySource node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(DbIdentitySource node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(DbIdentitySource node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(DbIdentitySource node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(XmlIdentitySource node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(XmlIdentitySource node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(XmlIdentitySource node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(JOSSO1Resource node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(JOSSO1Resource node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(JOSSO1Resource node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(MicroStrategyResource node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(MicroStrategyResource node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(MicroStrategyResource node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(SelfServicesResource node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(SelfServicesResource node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(SelfServicesResource node, Object child, Object resultOfPreviousChild, int indexOfNextChild);


    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(JOSSO2Resource node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(JOSSO2Resource node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(JOSSO2Resource node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(ServiceConnection node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(ServiceConnection node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(ServiceConnection node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(JOSSOActivation node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(JOSSOActivation node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(JOSSOActivation node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(Location node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(Location node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(Location node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(FederatedConnection node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(FederatedConnection node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(FederatedConnection node, Object child, Object resultOfPreviousChild, int indexOfNextChild);
    
    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(IdentityLookup node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(IdentityLookup node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(IdentityLookup node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(DelegatedAuthentication node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(DelegatedAuthentication node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(DelegatedAuthentication node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(ExecutionEnvironment node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(ExecutionEnvironment node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(ExecutionEnvironment node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(AuthenticationService node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(AuthenticationService node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(AuthenticationService node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(Activation node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(Activation node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(Activation node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(ProviderConfig node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(ProviderConfig node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(ProviderConfig node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(Keystore node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(Keystore node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(Keystore node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

    /**
     * This method is called before walking any children of the argument
     * <code>node</code>. A node visitor instance uses this method
     * to perform any initialization tasks it needs for walking the node's
     * children.
     *
     * @param node the node to be walked
     */
    void arrive(AuthenticationMechanism node) throws Exception;

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
     * @param node the node having been walked
     * @param results the results of walking the node's children
     * @return the result of walking the node and it's children
     */
    Object[] leave(AuthenticationMechanism node, Object[] results) throws Exception;

    /**
     * This method is called before walking each child of the argument
     * <code>node</code>. The return value of this method determines if
     * the next child of the argument <code>node</code> should be walked.
     *  The argument <code>resultOfPreviousChild</code>
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
    boolean walkNextChild(AuthenticationMechanism node, Object child, Object resultOfPreviousChild, int indexOfNextChild);

}
