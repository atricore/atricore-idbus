package com.atricore.idbus.console.lifecycle.main.impl;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceDefinitionVisitor;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class AbstractApplianceDefinitionVisitor implements IdentityApplianceDefinitionVisitor {
    
    public void arrive(IdentityApplianceDefinition node) throws Exception {
        
    }

    public Object[] leave(IdentityApplianceDefinition node, Object[] results) throws Exception {
        return new Object[0];  
    }

    public boolean walkNextChild(IdentityApplianceDefinition node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(ServiceProvider node) throws Exception {
        
    }

    public Object[] leave(ServiceProvider node, Object[] results) throws Exception {
        return new Object[0];  
    }

    public boolean walkNextChild(ServiceProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(IdentityProvider node) throws Exception {
        
    }

    public Object[] leave(IdentityProvider node, Object[] results) throws Exception {
        return new Object[0];  
    }

    public boolean walkNextChild(IdentityProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(ExternalServiceProvider node) throws Exception {

    }

    public Object[] leave(ExternalServiceProvider node, Object[] results) throws Exception {
        return new Object[0];
    }

    public boolean walkNextChild(ExternalServiceProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    public void arrive(ExternalIdentityProvider node) throws Exception {

    }

    public Object[] leave(ExternalIdentityProvider node, Object[] results) throws Exception {
        return new Object[0];
    }

    public boolean walkNextChild(ExternalIdentityProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    public void arrive(SalesforceServiceProvider node) throws Exception {

    }

    public Object[] leave(SalesforceServiceProvider node, Object[] results) throws Exception {
        return new Object[0];
    }

    public boolean walkNextChild(SalesforceServiceProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    public void arrive(GoogleAppsServiceProvider node) throws Exception {

    }

    public Object[] leave(GoogleAppsServiceProvider node, Object[] results) throws Exception {
        return new Object[0];
    }

    public boolean walkNextChild(GoogleAppsServiceProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    public void arrive(SugarCRMServiceProvider node) throws Exception {

    }

    public Object[] leave(SugarCRMServiceProvider node, Object[] results) throws Exception {
        return new Object[0];
    }

    public boolean walkNextChild(SugarCRMServiceProvider node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    public void arrive(IdentityProviderChannel node) throws Exception {
        
    }

    public Object[] leave(IdentityProviderChannel node, Object[] results) throws Exception {
        return new Object[0];  
    }

    public boolean walkNextChild(IdentityProviderChannel node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(ServiceProviderChannel node) throws Exception {
        
    }

    public Object[] leave(ServiceProviderChannel node, Object[] results) throws Exception {
        return new Object[0];  
    }

    public boolean walkNextChild(ServiceProviderChannel node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(IdentitySource node) throws Exception {
        
    }

    public Object[] leave(IdentitySource node, Object[] results) throws Exception {
        return new Object[0];  
    }

    public boolean walkNextChild(IdentitySource node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(EmbeddedIdentitySource node) throws Exception {
        
    }

    public Object[] leave(EmbeddedIdentitySource node, Object[] results) throws Exception {
        return new Object[0];  
    }

    public boolean walkNextChild(EmbeddedIdentitySource node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(LdapIdentitySource node) throws Exception {
        
    }

    public Object[] leave(LdapIdentitySource node, Object[] results) throws Exception {
        return new Object[0];  
    }

    public boolean walkNextChild(LdapIdentitySource node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(DbIdentitySource node) throws Exception {
        
    }

    public Object[] leave(DbIdentitySource node, Object[] results) throws Exception {
        return new Object[0];  
    }

    public boolean walkNextChild(DbIdentitySource node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(XmlIdentitySource node) throws Exception {

    }

    public Object[] leave(XmlIdentitySource node, Object[] results) throws Exception {
        return new Object[0];
    }

    public boolean walkNextChild(XmlIdentitySource node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }


    public void arrive(JOSSOActivation node) throws Exception {
        
    }

    public Object[] leave(JOSSOActivation node, Object[] results) throws Exception {
        return new Object[0];  
    }

    public boolean walkNextChild(JOSSOActivation node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(Location node) throws Exception {
        
    }

    public Object[] leave(Location node, Object[] results) throws Exception {
        return new Object[0];  
    }

    public boolean walkNextChild(Location node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(FederatedConnection node) throws Exception {
        
    }

    public Object[] leave(FederatedConnection node, Object[] results) throws Exception {
        return new Object[0];  
    }

    public boolean walkNextChild(FederatedConnection node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(IdentityLookup node) throws Exception {
        
    }

    public Object[] leave(IdentityLookup node, Object[] results) throws Exception {
        return new Object[0];  
    }

    public boolean walkNextChild(IdentityLookup node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(DelegatedAuthentication node) throws Exception {

    }

    public Object[] leave(DelegatedAuthentication node, Object[] results) throws Exception {
        return new Object[0];
    }

    public boolean walkNextChild(DelegatedAuthentication node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    public void arrive(ExecutionEnvironment node) throws Exception {
        
    }

    public Object[] leave(ExecutionEnvironment node, Object[] results) throws Exception {
        return new Object[0];  
    }

    public boolean walkNextChild(ExecutionEnvironment node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(AuthenticationService node) throws Exception {

    }

    public Object[] leave(AuthenticationService node, Object[] results) throws Exception {
        return new Object[0];
    }

    public boolean walkNextChild(AuthenticationService node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    public void arrive(Activation node) throws Exception {
        
    }

    public Object[] leave(Activation node, Object[] results) throws Exception {
        return new Object[0];  
    }

    public boolean walkNextChild(Activation node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;  
    }

    public void arrive(ProviderConfig node) throws Exception {

    }

    public Object[] leave(ProviderConfig node, Object[] results) throws Exception {
        return new Object[0];
    }

    public boolean walkNextChild(ProviderConfig node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    public void arrive(Keystore node) throws Exception {

    }

    public Object[] leave(Keystore node, Object[] results) throws Exception {
        return new Object[0];
    }

    public boolean walkNextChild(Keystore node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

    public void arrive(AuthenticationMechanism node) throws Exception {

    }

    public Object[] leave(AuthenticationMechanism node, Object[] results) throws Exception {
        return new Object[0];
    }

    public boolean walkNextChild(AuthenticationMechanism node, Object child, Object resultOfPreviousChild, int indexOfNextChild) {
        return true;
    }

}
