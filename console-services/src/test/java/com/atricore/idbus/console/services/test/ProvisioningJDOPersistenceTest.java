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
 * Date: Oct 1, 2009
 * Time: 4:22:39 PM
 * email: cbirge@atricore.org
 */
public class ProvisioningJDOPersistenceTest {
    /*

    private static Log logger = LogFactory.getLog(ProvisioningJDOPersistenceTest.class.getName() );

    private GroupService _groupService;

    private static String GROUP_NAME = "GROUP_NAME";
    private static String GROUP_DESC = "GROUP_DESC";

    @Before
    public void setUp(){
        _groupService = new GroupServiceJDOImpl();
    }

    //<------------- Group Test Cases ----------------->

    @Test
    public void testCrudGroupService() throws Exception {

        logger.debug("[Start]: testPersistGroup");

        GroupRequest groupRequest = new GroupRequest();

        groupRequest.setName(GROUP_NAME+"_1");
        groupRequest.setDescription(GROUP_DESC+"_1");

        _groupService.save(groupRequest);

        groupRequest.setName(GROUP_NAME+"_2");
        groupRequest.setDescription(GROUP_DESC+"_2");

        _groupService.save(groupRequest);

        Collection<Group> result = _groupService.getList();
        for (Group group: result) {
            logger.debug("Group Persisted: "+group.getId()+" - "+group.getName());
        }

        logger.debug("[Finish]: testPersistGroup");

        logger.debug("[Start]: testLookupGroupById");

        groupRequest = new GroupRequest();
        groupRequest.setId(11);
        GroupResponse response = _groupService.findById(groupRequest);

        assert (response.getGroup().getId() == 11): "Wrong Id in lookpup by id (11) : "+response.getGroup().getId();
        assert (response.getGroup().getName().equals(GROUP_NAME+"_1")): "Wrong Name in lookpup by id (11): "+response.getGroup().getName();
        assert (response.getGroup().getDescription().equals(GROUP_DESC+"_1")): "Wrong Description in lookpup by id (11): "+response.getGroup().getDescription();

        groupRequest.setId(12);
        response = _groupService.findById(groupRequest);

        assert (response.getGroup().getId() == 12): "Wrong Id in lookpup by id (12): "+response.getGroup().getId();
        assert (response.getGroup().getName().equals(GROUP_NAME+"_2")): "Wrong Name in lookpup by id (12): "+response.getGroup().getName();
        assert (response.getGroup().getDescription().equals(GROUP_DESC+"_2")): "Wrong Description in lookpup by id (12): "+response.getGroup().getDescription();

        logger.debug("[Finish]: testLookupGroupById");

        logger.debug("[Start]: testUpdateGroup");

        groupRequest = new GroupRequest();

        groupRequest.setId(11);
        groupRequest.setName(GROUP_NAME+"_1_UPDATED");
        groupRequest.setDescription(GROUP_DESC+"_1_UPDATED");

        _groupService.update(groupRequest);

        groupRequest = new GroupRequest();
        groupRequest.setId(11);
        response = _groupService.findById(groupRequest);

        assert (response.getGroup().getId() == 11): "Wrong Update in id (11) : "+response.getGroup().getId();
        assert (response.getGroup().getName().equals(GROUP_NAME+"_1_UPDATED")): "Wrong Name updated in id (11): "+response.getGroup().getName();
        assert (response.getGroup().getDescription().equals(GROUP_DESC+"_1_UPDATED")): "Wrong Description updated in id (11): "+response.getGroup().getDescription();

        logger.debug("[Finish]: testUpdateGroup");

        logger.debug("[Start]: testListAllGroup");

        result = _groupService.getList();
        for (Group group: result){
            logger.debug("Group Id: "+ group.getId()+ " - Name: "+ group.getName() + " - Description: "+group.getDescription());
        }

        logger.debug("[Finish]: testListAllGroup");

        logger.debug("[Start]: testDeleteGroup");

        groupRequest = new GroupRequest();
        groupRequest.setId(11);
        _groupService.delete(groupRequest);

        try {
            _groupService.findById(groupRequest);
            assert false: "Failed to delete Group with id 11";
        } catch (GroupNotFoundException e) {
            assert true;
        }

        groupRequest.setId(12);
        _groupService.delete(groupRequest);

        try {
            _groupService.findById(groupRequest);
            assert false: "Failed to delete Group with id 12";
        } catch (GroupNotFoundException e) {
            assert true;
        }

        logger.debug("[Finish]: testDeleteGroup");
    }

    */

}
