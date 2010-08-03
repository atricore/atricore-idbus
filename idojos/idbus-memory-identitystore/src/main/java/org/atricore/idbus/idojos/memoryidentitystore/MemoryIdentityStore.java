/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
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
package org.atricore.idbus.idojos.memoryidentitystore;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.store.*;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;
import org.atricore.idbus.kernel.main.store.exceptions.NoSuchUserException;
import org.atricore.idbus.kernel.main.store.exceptions.NoSuchRoleException;
import org.atricore.idbus.kernel.main.authn.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.springframework.core.io.Resource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.util.*;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * @author <a href="mailto:sgonzalez@josso.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: MemoryIdentityStore.java 1210 2009-05-22 21:38:24Z ajadzinsky $
 * @org.apache.xbean.XBean element="memory-store"
 * @org.apache.xbean.XBean element="memory-store"
 * <p/>
 * Memory based implementation of an IdentityStore and CredentialStore that reads
 * data from XML files.
 */

public class MemoryIdentityStore extends AbstractStore {

    private static final Log logger = LogFactory.getLog( MemoryIdentityStore.class );

    // A map with BaseRole instances, the map key is the role name.
    private Map<String, Element> _roles;

    // A map with BaseUser instances, the key is the username.
    private Map<String, Element> _users;

    // Stores BaseRoles associated to each user.
    // The map key is the username.
    // The map value is a Set of rolenames.
    private Map<String, Set<String>> _userRoles;

    // Stores credential values (Object) associated to each user.
    // The map key is the username.
    // THe map value is another Map with credetinals (name=key/value)
    private Map<String, Element> _principalCredentials;

    private boolean _initialized;


    private Resource _credentialsFileName;
    private Resource _usersFileName;

    public MemoryIdentityStore () {
        super();

        logger.debug( "Creating new MemoryIdentityStore" );

        _users = new HashMap<String, Element>( 7 );
        _userRoles = new HashMap<String, Set<String>>( 11 );
        _roles = new HashMap<String, Element>( 11 );
        _principalCredentials = new HashMap<String, Element>( 11 );
        _initialized = false;
    }

    /**
     * Initializes the store, reads data from XML files.
     */
    public synchronized void initialize () {
        try {
            // This store can work as an identityStore and as a credentialStore, so
            // configuration parameters are optional.
            if ( _usersFileName != null )
                loadUsersData( _usersFileName );

            if ( _credentialsFileName != null )
                loadCredentialsData( _credentialsFileName );

            _initialized = true;

        } catch ( Exception e ) {
            logger.error( e, e );
            throw new RuntimeException( "Can't initialize memory store : " + e.getMessage(), e );
        }
    }

    /**
     * Loads users from file.
     * @param fName the file containing user definitions.
     */
    protected void loadUsersData ( Resource fName ) throws Exception {
        // First, users

        logger.info( "Reading users from : " + fName );

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse( fName.getInputStream() );


        // normalize text representation (what for ?!)
        doc.getDocumentElement().normalize();
        logger.debug( "Root element of the doc is " + doc.getDocumentElement().getNodeName() );

        this.loadRoles( doc );
        logger.info( "Loaded " + _roles.size() + " roles from : " + fName );

        this.loadUsers( doc );
        logger.info( "Loaded " + _users.size() + " users from : " + fName );


    }

    protected void loadRoles ( Document doc ) throws Exception {

        NodeList listOfRoles = doc.getElementsByTagName( "role" );
        int totalRoles = listOfRoles.getLength();
        logger.debug( "Total roles: " + totalRoles );

        for ( int i = 0; i < listOfRoles.getLength(); i++ ) {

            Node roleNode = listOfRoles.item( i );
            if ( roleNode.getNodeType() == Node.ELEMENT_NODE ) {

                Element domRole = (Element) roleNode;
                Element domName = (Element) domRole.getElementsByTagName( "name" ).item( 0 );


                // Store DOM Element as a role.
                logger.debug( "Storing role for name : [" + getTextContent( domName ) + "]" );
                _roles.put( getTextContent( domName ), domRole );
            }
        }

    }

    protected void loadUsers ( Document doc ) throws Exception {

        NodeList listOfUsers = doc.getElementsByTagName( "user" );
        int totalUsers = listOfUsers.getLength();
        logger.debug( "Total users: " + totalUsers );

        for ( int i = 0; i < listOfUsers.getLength(); i++ ) {
            Node userNode = listOfUsers.item( i );
            if ( userNode.getNodeType() == Node.ELEMENT_NODE ) {
                Element userElement = (Element) userNode;
                Node domName = userElement.getElementsByTagName( "name" ).item( 0 );

                logger.debug( "Storing user for name : " + getTextContent( domName ) );
                // Store DOM Element as a user.
                _users.put( getTextContent( domName ), userElement );

            }
        }

    }

    /**
     * Loads credentials from file.
     * @param fName the file containing user definitions.
     */
    protected void loadCredentialsData ( Resource fName ) throws Exception {
        logger.info( "Reading credentials from : " + fName );

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse( fName.getInputStream() );

        // normalize text representation (what for ?!)
        doc.getDocumentElement().normalize();
        loadCredentials( doc );

        logger.info( "Loaded " + _principalCredentials.size() + " credential sets from : " + fName );

    }

    protected void loadCredentials ( Document doc ) throws Exception {

        NodeList credentialSetLst = doc.getElementsByTagName( "credential-set" );
        int totalCredentials = credentialSetLst.getLength();
        logger.debug( "Total credential sets: " + totalCredentials );

        for ( int i = 0; i < credentialSetLst.getLength(); i++ ) {

            // Each credential set has a key and a list of credentials.
            Element domCredentialSet = (Element) credentialSetLst.item( i );
            Node domKey = domCredentialSet.getElementsByTagName( "key" ).item( 0 );
            if ( domKey.getNodeType() != Node.ELEMENT_NODE ||
                    !domKey.getNodeName().equals( "key" ) )
                throw new SSOIdentityException( "Credential set definitions need a 'key' element [" + domKey.getNodeName() + "]" );

            String key = getTextContent( domKey );
            logger.info( "Storing credentials for key : " + key );
            _principalCredentials.put( key, domCredentialSet );
        }

    }

    protected Collection<BaseUser> listUsers () throws SSOIdentityException {
        if ( !_initialized )
            initialize();

        Collection<Element> domUsers = _users.values();
        List<BaseUser> ssoUsers = new ArrayList<BaseUser>( domUsers.size() );

        for ( Element domUser : domUsers ) {
            ssoUsers.add( toBaseUser( domUser ) );
        }

        return ssoUsers;
    }


    // ------------------------------------------------------------------------------
    // IdentityStore
    // ------------------------------------------------------------------------------

    // BaseUser related methods.

    public synchronized BaseUser loadUser ( UserKey key ) throws NoSuchUserException, SSOIdentityException {
        if ( !_initialized )
            initialize();

        if ( !( key instanceof SimpleUserKey) ) {
            throw new SSOIdentityException( "Unsupported key type : " + key.getClass().getName() );
        }

        Element domUser = _users.get( ( (SimpleUserKey) key ).getId() );
        if ( domUser == null )
            throw new NoSuchUserException( key );

        BaseUser user = toBaseUser( domUser );
        if ( logger.isDebugEnabled() )
            logger.debug( "[load(" + key + ")] : ok" );

        return user;
    }

    /**
     * @param key
     *
     * @throws SSOIdentityException
     */
    public synchronized BaseRole[] findRolesByUserKey ( UserKey key )
            throws SSOIdentityException {

        // TODO : This should be added to the store lifecycle
        if ( !_initialized )
            initialize();

        List<BaseRole> roles = new ArrayList<BaseRole>();

        SimpleUserKey simpleKey = (SimpleUserKey) key;
        Set<String> roleNames = _userRoles.get( simpleKey.getId() );
        if ( roleNames != null ) {
            Iterator it = roleNames.iterator();
            while ( it.hasNext() ) {
                String roleName = (String) it.next();
                BaseRole role = findRoleByName( roleName );
                if ( role == null )
                    throw new SSOIdentityException( "Role '" + roleName + "' declared for user '" + key + "' not defined" );

                roles.add( role );
            }
        }

        return roles.toArray( new BaseRole[roles.size()] );
    }

    // ------------------------------------------------------------------------------
    // CredentialStore
    // ------------------------------------------------------------------------------
    /**
     * Gets configured credentials for this principal.
     * @param key used to retrieve this credentials.
     *
     * @throws SSOIdentityException
     */
    public Credential[] loadCredentials ( CredentialKey key, CredentialProvider cp ) throws SSOIdentityException {

        // TODO : This should be added to the store lifecycle
        if ( !_initialized )
            initialize();

        if ( !( key instanceof SimpleUserKey ) ) {
            throw new SSOIdentityException( "Unsupported key type : " + key.getClass().getName() );
        }

        SimpleUserKey simpleKey = (SimpleUserKey) key;

        Element credentialElement = _principalCredentials.get( simpleKey.getId() );
        if (credentialElement == null) {
            return new Credential[0];
        }

        Credential[] creds = toCredentials( credentialElement, cp );

        logger.debug( "Found " + creds.length + " credentials!" );

        if ( logger.isDebugEnabled() ) {
            for ( int i = 0; i < creds.length; i++ ) {
                Credential cred = creds[ i ];
                logger.debug( "Credential[" + i + "]=" + creds[ i ] );
            }
        }
        return creds;
    }

    // ------------------------------------------------------------------------------
    // utils ....
    // ------------------------------------------------------------------------------

    /**
     * Transforms a DOM Node to a Credential instance
     * @return
     */
    protected Credential[] toCredentials ( Element domCredentialSet, CredentialProvider cp ) throws SSOIdentityException {

        NodeList domCredentials = domCredentialSet.getElementsByTagName( "credential" );

        List<Credential> creds = new ArrayList<Credential>();

        // Each child must be a credential element
        for ( int i = 0; i < domCredentials.getLength(); i++ ) {

            Element domCredential = (Element) domCredentials.item( i );
            if ( domCredential.getNodeType() != Node.ELEMENT_NODE ||
                    !domCredential.getNodeName().equals( "credential" ) )
                continue;

            Node domName = domCredential.getElementsByTagName( "name" ).item( 0 );
            if ( domName.getNodeType() != Node.ELEMENT_NODE ||
                    !domName.getNodeName().equals( "name" ) )
                throw new SSOIdentityException( "Credential definitions need a 'name' and 'value' element" );

            Node domValue = domCredential.getElementsByTagName( "value" ).item( 0 );
            if ( domValue.getNodeType() != Node.ELEMENT_NODE ||
                    !domValue.getNodeName().equals( "value" ) )
                throw new SSOIdentityException( "Credential definitions need a 'name' and 'value' element" );

            String name = getTextContent( domName );
            String value = getTextContent( domValue );

            if ( logger.isDebugEnabled() )
                logger.debug( "Creating credential [" + name + "/" + value + "] " );

            Credential c = cp.newCredential( name, value );
            if ( c != null )
                creds.add( c );
        }

        return creds.toArray( new Credential[creds.size()] );

    }

    protected BaseRole toBaseRole ( Element domRole ) throws SSOIdentityException {

        Node domName = ( (Element) domRole ).getElementsByTagName( "name" ).item( 0 );
        if ( domName.getNodeType() != Node.ELEMENT_NODE ||
                !domName.getNodeName().equals( "name" ) )
            throw new SSOIdentityException( "Role definitions need a 'name' element" );

        return new BaseRoleImpl( getTextContent( domName ) );

    }

    protected BaseUser toBaseUser ( Element domUser ) throws SSOIdentityException {

        // Build the user instance
        Node domUsername = ( (Element) domUser ).getElementsByTagName( "name" ).item( 0 );
        if ( domUsername.getNodeType() != Node.ELEMENT_NODE ||
                !domUsername.getNodeName().equals( "name" ) ) {
            throw new SSOIdentityException( "User definitions need a 'name'" );
        }

        String username = getTextContent( domUsername );
        BaseUser user = new BaseUserImpl();
        UserKey key = new SimpleUserKey( username );
        user.setName( username );

        // Add user properties

        NodeList propertiesLst = ( (Element) domUser ).getElementsByTagName( "property" );
        for ( int i = 0; i < propertiesLst.getLength(); i++ ) {
            Element domProperty = (Element) propertiesLst.item( i );

            Node domName = ( (Element) domProperty ).getElementsByTagName( "name" ).item( 0 );
            if ( domName.getNodeType() != Node.ELEMENT_NODE ||
                    !domName.getNodeName().equals( "name" ) )
                throw new SSOIdentityException( "Property definitions need a 'name' and 'value' element" );

            Node domValue = ( (Element) domProperty ).getElementsByTagName( "value" ).item( 0 );
            if ( domValue.getNodeType() != Node.ELEMENT_NODE ||
                    !domValue.getNodeName().equals( "value" ) )
                throw new SSOIdentityException( "Property definitions need a 'name' and 'value' element" );

            String name = getTextContent( domName );
            String value = getTextContent( domValue );

            user.addProperty( new SSONameValuePair( name, value ) );

        }


        // Add user roles !?

        NodeList rolesLst = ( (Element) domUser ).getElementsByTagName( "roles" );
        if ( rolesLst.getLength() > 1 )
            throw new SSOIdentityException( "Only one 'roles' element can be defined for a user" );

        if ( rolesLst.getLength() > 0 ) {
            Set<String> roles = new HashSet<String>();

            Node domRoles = rolesLst.item( 0 );
            String stRoles = getTextContent( domRoles );

            StringTokenizer st = new StringTokenizer( stRoles != null ? stRoles : "", "," );
            while ( st.hasMoreTokens() ) {
                String roleName = st.nextToken().trim();
                BaseRole role = findRoleByName( roleName );
                roles.add( roleName );
                logger.debug( "User is in role : " + role );
            }

            _userRoles.put( username, roles );


        }


        return user;

    }

    protected CredentialKey createCredentialKey ( String name ) {
        // TODO : Use proper key adapter.
        return new SimpleUserKey( name );
    }

    public synchronized Set<String> getRoleKeys ()
            throws SSOIdentityException {
        return _roles.keySet();
    }

    public synchronized BaseRole loadRole ( RoleKey roleKey )
            throws NoSuchRoleException, SSOIdentityException {
        BaseRole role = (BaseRole) _roles.get( roleKey );
        if ( role == null )
            throw new NoSuchRoleException( roleKey );

        return role;
    }

    public synchronized BaseRole findRoleByName ( String name ) throws SSOIdentityException {
        Element domRole = _roles.get( name );
        if ( domRole == null )
            throw new SSOIdentityException( "No such role : " + name );

        return toBaseRole( domRole );
    }


    // -----------------------------------------------------------------------------------------
    // Private Utils.
    // -----------------------------------------------------------------------------------------


    protected UserKey createUserKey ( BaseUser user ) {
        // TODO : Use proper key adapter ...
        return new SimpleUserKey( user.getName() );
    }

    protected BaseRole createRole ( String name ) {
        BaseRole role = new BaseRoleImpl();
        role.setName( name );
        return role;
    }

    protected RoleKey createRoleKey ( BaseRole role ) {
        // TODO : Use proper key adapter ...
        return new SimpleRoleKey( role.getName() );
    }

    // ---------------------------------------------------------------------
    // Configuration properties
    // ---------------------------------------------------------------------

    public void setCredentialsFileName ( Resource credentialsFileName ) {
        logger.debug( "Setting crednetials file name to : " + credentialsFileName );
        _credentialsFileName = credentialsFileName;
    }

    public Resource getCredentialsFileName () {
        return _credentialsFileName;
    }

    public void setUsersFileName ( Resource usersFileName ) {
        logger.debug( "Setting users file name to : " + usersFileName );
        _usersFileName = usersFileName;
    }

    public Resource getUsersFileName() {
        return _usersFileName;
    }


    // Some utils ...

    protected String getTextContent ( Node node ) {
        try {
            // Only supported in earlier versions of DOM
            Method getTextContent = node.getClass().getMethod( "getTextContent" );
            return (String) getTextContent.invoke( node );

        } catch ( NoSuchMethodException e ) {
            logger.debug( "Using old DOM Java Api to get Node text content" );
        } catch ( InvocationTargetException e ) {
            logger.warn( e.getMessage(), e );
        } catch ( IllegalAccessException e ) {
            logger.warn( e.getMessage(), e );
        }

        // Old DOM API usage to get node's text content
        NodeList children = node.getChildNodes();
        for ( int i = 0; i < children.getLength(); i++ ) {
            Node child = children.item( i );
            if ( child.getNodeType() == Node.TEXT_NODE ) {
                return child.getNodeValue();
            }
        }
        return null;
    }

}
