/*
 * Atricore IDBus
 *
 *   Copyright 2009, Atricore Inc.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.services.test;

/**
 * User: cdbirge
 * Date: Oct 5, 2009
 * Time: 12:16:50 PM
 * email: cbirge@atricore.org
 */
public class UserServiceImplTest {

    /*
    private static Log logger = LogFactory.getLog(UserServiceImplTest.class.getName() );

    private UserService _userService;
    private GroupService _groupService;

    private static String USERNAME = "USERNAME";
    private static String FIRSTNAME = "FIRSTNAME";
    private static String SURENAME = "SURENAME";
    private static String COMMONNAME = "COMMONNAME";
    private static String GIVENNAME = "GIVENNAME";
    private static String INITIALS = "INITIALS";
    private static String GENERATION_QUA = "GENERATION_QUA";
    private static String DISTING_NAME = "DISTING_NAME";
    private static String EMAIL = "EMAIL";
    private static String TELEPHONE = "TELEPHONE";
    private static String FAX = "FAX";
    private static String COUNTRY = "COUNTRY";
    private static String LOCALITY = "LOCALITY";
    private static String STATE = "STATE";
    private static String STREET = "STREET";
    private static String ORG_NAME = "ORG_NAME";
    private static String ORG_UNIT_NAME = "ORG_UNIT_NAME";
    private static String TITLE = "TITLE";
    private static String BUSINESS_CAT = "BUSINESS_CAT";
    private static String ADDRESS = "ADDRESS";
    private static String CODE = "CODE";
    private static String OFFICE_BOX = "OFFICE_BOX";
    private static String LANGUAGE = "LANGUAGE";
    private static boolean DISABLED = false;
    private static boolean EXPIRES = false;
    private static Date EXPIRATION_DATE = new Date();
    private static boolean LIMIT_LOGIN = false;
    private static int MAX_LOGIN = 8;
    private static boolean TERMINATE_SESSION = false;
    private static boolean PREV_SESSION = false;
    private static boolean ALLOW_CHANGE = false;
    private static boolean FORCE_CHANGE = false;
    private static int DAYS_BETWEEN = 8;
    private static Date PASS_EXPIRE_DATE = new Date();
    private static boolean NOTIFY_PASS_EXP = false;
    private static int DAYS_BEFORE = 8;
    private static String PASSWORD = "PASSWORD";
    private static byte[] CERTIFICATE = "CERTIFICATE".getBytes();
    private static boolean AUTO_GENERATE = false;
    private static boolean EMAIL_PASS = false;

    private static String GROUP_NAME = "GROUP_USER_NAME";
    private static String GROUP_DESC= "GROUP_USER_DESC";

    @Before
    public void setUp(){
        _userService = new UserServiceJDOImpl();
        _groupService = new GroupServiceJDOImpl();
    }

    @Test
    public void testCrudUserService() throws Exception {

        logger.debug("[Start]: testPersistUser");

        UserRequest userRequest = new UserRequest();

        userRequest.setUserName(USERNAME+"_1");
        userRequest.setFirstName(FIRSTNAME+"_1");
        userRequest.setSurename(SURENAME+"_1");
        userRequest.setCommonName(COMMONNAME+"_1");
        userRequest.setGivenName(GIVENNAME+"_1");
        userRequest.setInitials(INITIALS+"_1");
        userRequest.setGenerationQualifier(GENERATION_QUA+"_1");
        userRequest.setDistinguishedName(DISTING_NAME+"_1");
        userRequest.setEmail(EMAIL+"_1");
        userRequest.setTelephoneNumber(TELEPHONE+"_1");
        userRequest.setFacsimilTelephoneNumber(FAX+"_1");
        userRequest.setCountryName(COUNTRY+"_1");
        userRequest.setLocalityName(LOCALITY+"_1");
        userRequest.setStreetAddress(STREET+"_1");
        userRequest.setStateOrProvinceName(STATE+"_1");
        userRequest.setOrganizationName(ORG_NAME+"_1");
        userRequest.setOrganizationUnitName(ORG_UNIT_NAME+"_1");
        userRequest.setPersonalTitle(TITLE+"_1");
        userRequest.setBusinessCategory(BUSINESS_CAT+"_1");
        userRequest.setPostalAddress(ADDRESS+"_1");
        userRequest.setPostalCode(CODE+"_1");
        userRequest.setPostOfficeBox(OFFICE_BOX+"_1");
        userRequest.setLanguage(LANGUAGE+"_1");
        userRequest.setAccountDisabled(DISABLED);
        userRequest.setAccountExpires(EXPIRES);
        userRequest.setAccountExpirationDate(EXPIRATION_DATE);
        userRequest.setLimitSimultaneousLogin(LIMIT_LOGIN);
        userRequest.setMaximunLogins(MAX_LOGIN);
        userRequest.setTerminatePreviousSession(TERMINATE_SESSION);
        userRequest.setPreventNewSession(PREV_SESSION);
        userRequest.setAllowUserToChangePassword(ALLOW_CHANGE);
        userRequest.setForcePeriodicPasswordChanges(FORCE_CHANGE);
        userRequest.setDaysBetweenChanges(DAYS_BETWEEN);
        userRequest.setPasswordExpirationDate(PASS_EXPIRE_DATE);
        userRequest.setNotifyPasswordExpiration(NOTIFY_PASS_EXP);
        userRequest.setDaysBeforeExpiration(DAYS_BEFORE);
        userRequest.setUserPassword(PASSWORD+"_1");
        userRequest.setUserCertificate(CERTIFICATE);
        userRequest.setAutomaticallyGeneratePassword(AUTO_GENERATE);
        userRequest.setEmailNewPasword(EMAIL_PASS);

        //<--------- Group mappging ----->
        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setName(GROUP_NAME);
        groupRequest.setDescription(GROUP_DESC);

        _groupService.save(groupRequest);

        Collection<Group> groups = _groupService.getList();

        List<Group> groupList= new ArrayList<Group>();

        if (groups.iterator().hasNext()) {
            Group group = (Group) groups.iterator().next();
            groupList.add(group);
            userRequest.setGroups(groupList);
        }

        _userService.save(userRequest);

        UserRequest userRequest2 = new UserRequest();

        userRequest2.setUserName(USERNAME+"_2");
        userRequest2.setFirstName(FIRSTNAME+"_2");
        userRequest2.setSurename(SURENAME+"_2");
        userRequest2.setCommonName(COMMONNAME+"_2");
        userRequest2.setGivenName(GIVENNAME+"_2");
        userRequest2.setInitials(INITIALS+"_2");
        userRequest2.setGenerationQualifier(GENERATION_QUA+"_2");
        userRequest2.setDistinguishedName(DISTING_NAME+"_2");
        userRequest2.setEmail(EMAIL+"_2");
        userRequest2.setTelephoneNumber(TELEPHONE+"_2");
        userRequest2.setFacsimilTelephoneNumber(FAX+"_2");
        userRequest2.setCountryName(COUNTRY+"_2");
        userRequest2.setLocalityName(LOCALITY+"_2");
        userRequest2.setStreetAddress(STREET+"_2");
        userRequest2.setStateOrProvinceName(STATE+"_2");
        userRequest2.setOrganizationName(ORG_NAME+"_2");
        userRequest2.setOrganizationUnitName(ORG_UNIT_NAME+"_2");
        userRequest2.setPersonalTitle(TITLE+"_2");
        userRequest2.setBusinessCategory(BUSINESS_CAT+"_2");
        userRequest2.setPostalAddress(ADDRESS+"_2");
        userRequest2.setPostalCode(CODE+"_2");
        userRequest2.setPostOfficeBox(OFFICE_BOX+"_2");
        userRequest2.setLanguage(LANGUAGE+"_2");
        userRequest2.setAccountDisabled(DISABLED);
        userRequest2.setAccountExpires(EXPIRES);
        userRequest2.setAccountExpirationDate(EXPIRATION_DATE);
        userRequest2.setLimitSimultaneousLogin(LIMIT_LOGIN);
        userRequest2.setMaximunLogins(MAX_LOGIN);
        userRequest2.setTerminatePreviousSession(TERMINATE_SESSION);
        userRequest2.setPreventNewSession(PREV_SESSION);
        userRequest2.setAllowUserToChangePassword(ALLOW_CHANGE);
        userRequest2.setForcePeriodicPasswordChanges(FORCE_CHANGE);
        userRequest2.setDaysBetweenChanges(DAYS_BETWEEN);
        userRequest2.setPasswordExpirationDate(PASS_EXPIRE_DATE);
        userRequest2.setNotifyPasswordExpiration(NOTIFY_PASS_EXP);
        userRequest2.setDaysBeforeExpiration(DAYS_BEFORE);
        userRequest2.setUserPassword(PASSWORD+"_2");
        userRequest2.setUserCertificate(CERTIFICATE);
        userRequest2.setAutomaticallyGeneratePassword(AUTO_GENERATE);
        userRequest2.setEmailNewPasword(EMAIL_PASS);
        userRequest2.setGroups(null);

        _userService.save(userRequest2);

        Collection result = _userService.getList();
        assert (result.size() == 2): "Fail inserting 2 users";

        logger.debug("[Finish]: testPersistUser");

        logger.debug("[Start]: testLookupUserById");

        UserRequest searchUserRequest = new UserRequest();
        searchUserRequest.setId(1);
        UserResponse response = _userService.findById(searchUserRequest);

        checkFields(userRequest,response.getUser());

        searchUserRequest.setId(2);
        response = _userService.findById(searchUserRequest);

        checkFields(userRequest2,response.getUser());
       
        logger.debug("[Finish]: testLookupUserById");

        logger.debug("[Start]: testUpdateUser");

        UserRequest userUpdateRequest = new UserRequest();

        userUpdateRequest.setId(1);
        userUpdateRequest.setUserName(USERNAME+"_2_UPDATED");
        userUpdateRequest.setFirstName(FIRSTNAME+"_1_UPDATED");
        userUpdateRequest.setSurename(SURENAME+"_1_UPDATED");
        userUpdateRequest.setCommonName(COMMONNAME+"_1_UPDATED");
        userUpdateRequest.setGivenName(GIVENNAME+"_1_UPDATED");
        userUpdateRequest.setInitials(INITIALS+"_1_UPDATED");
        userUpdateRequest.setGenerationQualifier(GENERATION_QUA+"_1_UPDATED");
        userUpdateRequest.setDistinguishedName(DISTING_NAME+"_1_UPDATED");
        userUpdateRequest.setEmail(EMAIL+"_1_UPDATED");
        userUpdateRequest.setTelephoneNumber(TELEPHONE+"_1_UPDATED");
        userUpdateRequest.setFacsimilTelephoneNumber(FAX+"_1_UPDATED");
        userUpdateRequest.setCountryName(COUNTRY+"_1_UPDATED");
        userUpdateRequest.setLocalityName(LOCALITY+"_1_UPDATED");
        userUpdateRequest.setStreetAddress(STREET+"_1_UPDATED");
        userUpdateRequest.setStateOrProvinceName(STATE+"_1_UPDATED");
        userUpdateRequest.setOrganizationName(ORG_NAME+"_1_UPDATED");
        userUpdateRequest.setOrganizationUnitName(ORG_UNIT_NAME+"_1_UPDATED");
        userUpdateRequest.setPersonalTitle(TITLE+"_1_UPDATED");
        userUpdateRequest.setBusinessCategory(BUSINESS_CAT+"_1_UPDATED");
        userUpdateRequest.setPostalAddress(ADDRESS+"_1_UPDATED");
        userUpdateRequest.setPostalCode(CODE+"_1_UPDATED");
        userUpdateRequest.setPostOfficeBox(OFFICE_BOX+"_1_UPDATED");
        userUpdateRequest.setLanguage(LANGUAGE+"_1_UPDATED");
        userUpdateRequest.setAccountDisabled(!DISABLED);
        userUpdateRequest.setAccountExpires(!EXPIRES);
        userUpdateRequest.setAccountExpirationDate(new Date());
        userUpdateRequest.setLimitSimultaneousLogin(!LIMIT_LOGIN);
        userUpdateRequest.setMaximunLogins(MAX_LOGIN+1);
        userUpdateRequest.setTerminatePreviousSession(!TERMINATE_SESSION);
        userUpdateRequest.setPreventNewSession(!PREV_SESSION);
        userUpdateRequest.setAllowUserToChangePassword(!ALLOW_CHANGE);
        userUpdateRequest.setForcePeriodicPasswordChanges(!FORCE_CHANGE);
        userUpdateRequest.setDaysBetweenChanges(DAYS_BETWEEN+1);
        userUpdateRequest.setPasswordExpirationDate(new Date());
        userUpdateRequest.setNotifyPasswordExpiration(!NOTIFY_PASS_EXP);
        userUpdateRequest.setDaysBeforeExpiration(DAYS_BEFORE+1);
        userUpdateRequest.setUserPassword(PASSWORD+"_1_UPDATED");
        userUpdateRequest.setUserCertificate("CERTIFICATE_UPDATED".getBytes());
        userUpdateRequest.setAutomaticallyGeneratePassword(!AUTO_GENERATE);
        userUpdateRequest.setEmailNewPasword(!EMAIL_PASS);
        

        _userService.update(userUpdateRequest);

        
        searchUserRequest = new UserRequest();
        searchUserRequest.setId(1);
        response = _userService.findById(searchUserRequest);

        checkFields(userUpdateRequest, response.getUser());

        logger.debug("[Start]: testListAllUsers");

        Collection<User> listResult = _userService.getList();
        assert (listResult.size() == 2): "Fail Listing 2 users";

        for (User user: listResult){
            logger.debug("id: "+ user.getId()+" UserName: "+ user.getUserName());
        }

        logger.debug("[Finish]: testListAllUsers");

        logger.debug("[Start]: testDeleteUser");

        searchUserRequest = new UserRequest();
        searchUserRequest.setId(1);
        _userService.remove(searchUserRequest);

        try {
            _userService.findById(searchUserRequest);
            assert false: "Failed to remove User with id 1";
        } catch (UserNotFoundException e) {
            assert true;
        }

        searchUserRequest.setId(2);
        _userService.remove(searchUserRequest);

        try {
            _userService.findById(userRequest);
            assert false: "Failed to remove user with id 2";
        } catch (UserNotFoundException e) {
            assert true;
        }

        logger.debug("[Finish]: testDeleteUser");   
    }

    protected void checkFields(UserRequest req, User user){

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        
        assert (user.getUserName().equals(req.getUserName())): "Wrong Username saved";
        assert (user.getFirstName().equals(req.getFirstName())): "Wrong Firstname saved";
        assert (user.getSurename().equals(req.getSurename())): "Wrong Surename saved";
        assert (user.getCommonName().equals(req.getCommonName())): "Wrong Commonname saved";
        assert (user.getGivenName().equals(req.getGivenName())): "Wrong Givenname saved";
        assert (user.getInitials().equals(req.getInitials())): "Wrong Initials saved";
        assert (user.getGenerationQualifier().equals(req.getGenerationQualifier())): "Wrong GenerationQualifier saved";
        assert (user.getDistinguishedName().equals(req.getDistinguishedName())): "Wrong DistinguishedName saved";
        assert (user.getEmail().equals(req.getEmail())): "Wrong Email saved";
        assert (user.getTelephoneNumber().equals(req.getTelephoneNumber())): "Wrong TelephoneNumber saved";
        assert (user.getFacsimilTelephoneNumber().equals(req.getFacsimilTelephoneNumber())): "Wrong FacsimilTelephoneNumber saved";
        assert (user.getCountryName().equals(req.getCountryName())): "Wrong CountryName saved";
        assert (user.getLocalityName().equals(req.getLocalityName())): "Wrong LocalityName saved";
        assert (user.getStateOrProvinceName().equals(req.getStateOrProvinceName())): "Wrong StateOrProvinceName saved";
        assert (user.getStreetAddress().equals(req.getStreetAddress())): "Wrong StreetAddress saved";
        assert (user.getOrganizationName().equals(req.getOrganizationName())): "Wrong OrganizationName saved";
        assert (user.getOrganizationUnitName().equals(req.getOrganizationUnitName())): "Wrong OrganizationUnitName saved";
        assert (user.getPersonalTitle().equals(req.getPersonalTitle())): "Wrong PersonalTitle saved";
        assert (user.getBusinessCategory().equals(req.getBusinessCategory())): "Wrong BusinessCategory saved";
        assert (user.getPostalAddress().equals(req.getPostalAddress())): "Wrong PostalAddress saved";
        assert (user.getPostalCode().equals(req.getPostalCode())): "Wrong PostalCode saved";
        assert (user.getPostOfficeBox().equals(req.getPostOfficeBox())): "Wrong PostOfficeBox saved";
        assert (user.getLanguage().equals(req.getLanguage())): "Wrong Languaje saved";
        assert (user.getAccountDisabled() == (req.getAccountDisabled())): "Wrong AccountDisabled saved";
        assert (user.getAccountExpires() == (req.getAccountExpires())): "Wrong AccountExpires saved";
        assert (format.format(user.getAccountExpirationDate()).equals(format.format(req.getAccountExpirationDate()))): "Wrong AccountExpirationDate saved";
        assert (user.getLimitSimultaneousLogin() == (req.getLimitSimultaneousLogin())): "Wrong LimitSimultaneousLogin saved";
        assert (user.getMaximunLogins().equals(req.getMaximunLogins())): "Wrong MaximunLogins saved";
        assert (user.getTerminatePreviousSession().equals(req.getTerminatePreviousSession())): "Wrong TerminatePreviousSession saved";
        assert (user.getPreventNewSession().equals(req.getPreventNewSession())): "Wrong PreventNewSession saved";
        assert (user.getAllowUserToChangePassword().equals(req.getAllowUserToChangePassword())): "Wrong AllowUserToChangePassword saved";
        assert (user.getForcePeriodicPasswordChanges().equals(req.getForcePeriodicPasswordChanges())): "Wrong ForcePeriodicPasswordChanges saved";
        assert (user.getDaysBetweenChanges().equals(req.getDaysBetweenChanges())): "Wrong DaysBetweenChanges saved";
        assert (format.format(user.getPasswordExpirationDate()).equals(format.format(req.getPasswordExpirationDate()))): "Wrong PasswordExpirationDate saved";
        assert (user.getNotifyPasswordExpiration().equals(req.getNotifyPasswordExpiration())): "Wrong NotifyPasswordExpiration saved";
        assert (user.getDaysBeforeExpiration().equals(req.getDaysBeforeExpiration())): "Wrong DaysBeforeExpiration saved";
        assert (user.getUserPassword().equals(req.getUserPassword())): "Wrong Username saved";
        //assert (Arrays.equals(user.getUserCertificate(),req.getUserCertificate())): "Wrong UserCertificate saved";
        assert (user.getAutomaticallyGeneratePassword().equals(req.getAutomaticallyGeneratePassword())): "Wrong AutomaticallyGeneratePassword saved";
        assert (user.getEmailNewPasword().equals(req.getEmailNewPasword())): "Wrong EmailNewPasword saved";
    }

    */
}
