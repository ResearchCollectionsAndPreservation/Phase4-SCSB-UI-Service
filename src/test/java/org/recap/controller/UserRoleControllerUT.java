package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.RoleEntity;
import org.recap.model.jpa.UsersEntity;
import org.recap.model.usermanagement.UserDetailsForm;
import org.recap.model.usermanagement.UserRoleForm;
import org.recap.model.usermanagement.UserRoleService;
import org.recap.repository.jpa.UserDetailsRepository;
import org.recap.security.UserManagementService;
import org.recap.util.UserAuthUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;

public class UserRoleControllerUT extends BaseTestCaseUT {

    @InjectMocks
    @Spy
    UserRoleController userRoleController;

    @Mock
    HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    UserAuthUtil userAuthUtil;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    UserDetailsRepository userDetailsRepository;

    @Mock
    UserManagementService userManagementService;

    /*@Test
    public void showUserRoles() {
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.isAuthenticated(session, ScsbConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(Boolean.TRUE);
        boolean view = userRoleController.showUserRoles(request);
        assertTrue(view);
    }

    @Test
    public void showUserRolesFalse() {
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.isAuthenticated(session, ScsbConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(Boolean.FALSE);
        boolean view = userRoleController.showUserRoles(request);
        assertFalse(view);
    }*/

    @Test
    public void searchUserRole() {
        UserRoleForm userRoleForm = new UserRoleForm();
        usersSessionAttributes();
        UserRoleForm userRole = userRoleController.searchUserRole(userRoleForm, request);
        assertNotNull(userRole);
    }

    @Test
    public void searchUserRoleException() {
        UserRoleForm userRoleForm = new UserRoleForm();
        Mockito.when(session.getAttribute(ScsbConstants.USER_ID)).thenThrow(new NullPointerException());
        UserRoleForm userRole = userRoleController.searchUserRole(userRoleForm, request);
        assertNotNull(userRole);
    }

    @Test
    public void searchUserRoleExceptionTest() {
        UserRoleForm userRoleForm = new UserRoleForm();
        usersSessionAttributes();
        Mockito.when(session.getAttribute(ScsbConstants.USER_ID)).thenThrow(new NullPointerException());
        UserRoleForm userRole = userRoleController.searchUserRole(userRoleForm, request);
        assertNotNull(userRole);
    }

    @Test
    public void exportUsers(){
        UserRoleForm userRoleForm = new UserRoleForm();
        usersSessionAttributes();
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(userAuthUtil.isAuthenticated(session, ScsbConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(Boolean.TRUE);
        UserRoleForm userRole = userRoleController.exportUsers(userRoleForm, request);
        assertNotNull(userRole);
    }
    @Test
    public void exportUsersException(){
        UserRoleForm userRoleForm = new UserRoleForm();
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(userAuthUtil.isAuthenticated(session, ScsbConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(Boolean.FALSE);
        UserRoleForm userRole = userRoleController.exportUsers(userRoleForm, request);
        assertNotNull(userRole);
    }

    @Test
    public void exportUsersExceptionTest(){
        UserRoleForm userRoleForm = new UserRoleForm();
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(userAuthUtil.isAuthenticated(session, ScsbConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(Boolean.TRUE);
        UserRoleForm userRole = userRoleController.exportUsers(userRoleForm, request);
        assertNotNull(userRole);
    }

    @Test
    public void deleteUser() {
        UserRoleForm userRoleForm = new UserRoleForm();
        usersSessionAttributes();
        UserRoleForm userRoleForm1 = userRoleController.deleteUser(3, userRoleForm.getSearchNetworkId(), 10, 1, 2, request);
        assertNotNull(userRoleForm1);
    }
    @Test
    public void deleteUserExceptionTest() {
        UserRoleForm userRoleForm = new UserRoleForm();
        Mockito.doThrow(new NullPointerException()).when(userDetailsRepository).delete(any());
        UserRoleForm userRoleForm1 = userRoleController.deleteUser(3, userRoleForm.getSearchNetworkId(), 10, 1, 2, request);
        assertNotNull(userRoleForm1);
    }
    @Test
    public void deleteUserException() {
        UserRoleForm userRoleForm = new UserRoleForm();
        usersSessionAttributes();
        Mockito.doThrow(new NullPointerException()).when(userDetailsRepository).delete(any());
        UserRoleForm userRoleForm1 = userRoleController.deleteUser(3, userRoleForm.getSearchNetworkId(), 10, 1, 2, request);
        assertNotNull(userRoleForm1);
    }

    @Test
    public void searchFirstPage() {
        UserRoleForm userRoleForm = new UserRoleForm();
        usersSessionAttributes();
        UserRoleForm userRoleForm1 = userRoleController.searchFirstPage(userRoleForm, request);
        assertNotNull(userRoleForm1);
    }

    @Test
    public void searchFirstPageTest() {
        UserRoleForm userRoleForm = new UserRoleForm();
        UserRoleForm userRoleForm1 = userRoleController.searchFirstPage(userRoleForm, request);
        assertNotNull(userRoleForm1);
    }

    @Test
    public void searchNextPage() {
        UserRoleForm userRoleForm = new UserRoleForm();
        usersSessionAttributes();
        UserRoleForm userRoleForm1 = userRoleController.searchNextPage(userRoleForm, request);
        assertNotNull(userRoleForm1);
    }

    @Test
    public void searchPreviousPage() {
        UserRoleForm userRoleForm = new UserRoleForm();
        usersSessionAttributes();
        UserRoleForm userRoleForm1 = userRoleController.searchPreviousPage(userRoleForm, request);
        assertNotNull(userRoleForm1);
    }

    @Test
    public void searchLastPage() {
        UserRoleForm userRoleForm = new UserRoleForm();
        usersSessionAttributes();
        UserRoleForm userRoleForm1 = userRoleController.searchLastPage(userRoleForm, request);
        assertNotNull(userRoleForm1);
    }

    @Test
    public void createUserRequest() {
        UserRoleForm userRoleForm = new UserRoleForm();
        usersSessionAttributes();
        Mockito.when(userRoleService.saveNewUserToDB(any())).thenReturn(getUsersEntity().get(0));
        UserRoleForm userRoleForm1 = userRoleController.createUserRequest(userRoleForm, request);
        assertNotNull(userRoleForm1);
    }

    @Test
    public void createUserRequestTest() {
        UserRoleForm userRoleForm = new UserRoleForm();
        Mockito.when(userRoleService.saveNewUserToDB(any())).thenReturn(getUsersEntity().get(0));
        UserRoleForm userRoleForm1 = userRoleController.createUserRequest(userRoleForm, request);
        assertNotNull(userRoleForm1);
    }

    @Test
    public void userEntityTest() {
        UserRoleForm userRoleForm = new UserRoleForm();
        usersSessionAttributes();
        Mockito.when(userRoleService.saveNewUserToDB(any())).thenReturn(null);
        UserRoleForm userRoleForm1 = userRoleController.createUserRequest(userRoleForm, request);
        assertNotNull(userRoleForm1);
    }

    @Test
    public void editUser() {
        usersSessionAttributes();
        Mockito.when(userDetailsRepository.findById(any())).thenReturn(Optional.of(getUsersEntity().get(0)));
        UserRoleForm userRoleForm1 = userRoleController.editUser(1, "smith", request);
        assertNotNull(userRoleForm1);
    }

    @Test
    public void editUserTest() {
        Mockito.when(userDetailsRepository.findById(any())).thenReturn(Optional.of(getUsersEntity().get(0)));
        UserRoleForm userRoleForm1 = userRoleController.editUser(1, "smith", request);
        assertNotNull(userRoleForm1);
    }

    @Test
    public void editUserGetUE() {
        usersSessionAttributes();
        Mockito.when(userDetailsRepository.findById(any())).thenReturn(Optional.empty());
        UserRoleForm userRoleForm1 = userRoleController.editUser(1, "smith", request);
        assertNotNull(userRoleForm1);
    }

    @Test
    public void saveEditUserDetails() {
        Integer userId = 3;
        String networkLoginId = "test";
        String userDescription = "test description";
        Integer institutionId = 1;
        List<Integer> role = new ArrayList<>();
        role.add(2);
        List<Integer> roleIds = role;
        String userEmailId = "test@mail.com";
        usersSessionAttributes();
        Mockito.when(userRoleService.saveEditedUserToDB(any(), any(), any(), any(), any(), any(), any())).thenReturn(getUsersEntity().get(0));
        UserRoleForm userRoleForm1 = userRoleController.saveEditUserDetails(userId, networkLoginId, userDescription, institutionId, userEmailId, roleIds, request);
        assertNotNull(userRoleForm1);
    }

    @Test
    public void saveEditUserDetailsWithoutDbTest() {
        Integer userId = 3;
        String networkLoginId = "test";
        String userDescription = "test description";
        Integer institutionId = 1;
        String userEmailId = "test@mail.com";
        List<Integer> role = new ArrayList<>();
        role.add(2);
        List<Integer> roleIds = role;
        UserRoleForm userRoleForm1 = userRoleController.saveEditUserDetails(userId, networkLoginId, userDescription, institutionId, userEmailId, roleIds, request);
        assertNotNull(userRoleForm1);
    }


    @Test
    public void saveEditUserDetailsWithoutDb() {
        Integer userId = 3;
        String networkLoginId = "test";
        String userDescription = "test description";
        Integer institutionId = 1;
        String userEmailId = "test@mail.com";
        List<Integer> role = new ArrayList<>();
        role.add(2);
        List<Integer> roleIds = role;
        usersSessionAttributes();
        UserRoleForm userRoleForm1 = userRoleController.saveEditUserDetails(userId, networkLoginId, userDescription, institutionId, userEmailId, roleIds, request);
        assertNotNull(userRoleForm1);
    }

    @Test
    public void userRoles() {
        usersSessionAttributes();
        UserRoleForm userRoleForm1 = userRoleController.userRoles(request);
        assertNotNull(userRoleForm1);
    }

    @Test
    public void userRolesTest() {
        UserRoleForm userRoleForm1 = userRoleController.userRoles(request);
        assertNotNull(userRoleForm1);
    }


    private void usersSessionAttributes() {
        Page<UsersEntity> usersEntityPage = new PageImpl<>(getUsersEntity());
        List<Object> roles = new ArrayList<>();
        roles.add(2);
        List<Object> institution = new ArrayList<>();
        institution.add("CUL");
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(true);
        userDetailsForm.setLoginInstitutionId(1);
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(session.getAttribute(ScsbConstants.USER_ID)).thenReturn(3);
        Mockito.when(session.getAttribute(ScsbConstants.SUPER_ADMIN_USER)).thenReturn(true);
        Mockito.when(userAuthUtil.isAuthenticated(session, ScsbConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(Boolean.TRUE);
        Mockito.when(userAuthUtil.getUserDetails(session, ScsbConstants.BARCODE_RESTRICTED_PRIVILEGE)).thenReturn(userDetailsForm);
        Mockito.when(userRoleService.getInstitutions(userDetailsForm.isSuperAdmin(), userDetailsForm.getLoginInstitutionId())).thenReturn(institution);
        Mockito.when(userManagementService.getSuperAdminRoleId()).thenReturn(1);
        Mockito.when(userRoleService.searchUsers(any(), anyBoolean())).thenReturn(usersEntityPage);
        Mockito.when(userRoleService.searchByNetworkId(any(), anyBoolean())).thenReturn(usersEntityPage);
        Mockito.when(userRoleService.searchByUserEmailId(any(), anyBoolean())).thenReturn(usersEntityPage);
        Mockito.when(userRoleService.searchByNetworkIdAndUserEmailId(any(), anyBoolean())).thenReturn(usersEntityPage);
        Mockito.when(userRoleService.getRoles(1, userDetailsForm.isSuperAdmin())).thenReturn(roles);
    }

    @Test
    public void searchAndSetResultWithSearchNetworkId(){
        usersSessionAttributes();
        UserRoleForm userRoleForm = new UserRoleForm();
        userRoleForm.setSearchNetworkId("test");
        userRoleForm.setUserEmailId("");
        Integer userId = 1;
        boolean superAdmin = true;
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(session.getAttribute(ScsbConstants.SUPER_ADMIN_USER)).thenReturn(true);
        ReflectionTestUtils.invokeMethod(userRoleController,"searchAndSetResult",
                request,userRoleForm,superAdmin,userId);
    }
    @Test
    public void searchAndSetResultWithUserEmailId(){
        usersSessionAttributes();
        UserRoleForm userRoleForm = new UserRoleForm();
        userRoleForm.setSearchNetworkId("");
        userRoleForm.setUserEmailId("test@gmail.com");
        Integer userId = 1;
        boolean superAdmin = true;
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(session.getAttribute(ScsbConstants.SUPER_ADMIN_USER)).thenReturn(true);
        ReflectionTestUtils.invokeMethod(userRoleController,"searchAndSetResult",
                request,userRoleForm,superAdmin,userId);
    }
    @Test
    public void searchAndSetResultWithSearchNetworkIdAndUserEmailId(){
        usersSessionAttributes();
        UserRoleForm userRoleForm = new UserRoleForm();
        userRoleForm.setSearchNetworkId("test");
        userRoleForm.setUserEmailId("test@gmail.com");
        Integer userId = 1;
        boolean superAdmin = true;
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(session.getAttribute(ScsbConstants.SUPER_ADMIN_USER)).thenReturn(true);
        ReflectionTestUtils.invokeMethod(userRoleController,"searchAndSetResult",
                request,userRoleForm,superAdmin,userId);
    }
    @Test
    public void getUsersInformation(){
        UserRoleForm userRoleForm = new UserRoleForm();
        Integer userId =1;
        Page<UsersEntity> usersEntityPage = new PageImpl<>(getUsersEntity());
        String message = "test";
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(session.getAttribute(ScsbConstants.SUPER_ADMIN_USER)).thenReturn(true);
        ReflectionTestUtils.invokeMethod(userRoleController,"getUsersInformation",
                request,userRoleForm,userId,usersEntityPage,message);
    }
    @Test
    public void getUsersInformationWithoutUserEntites(){
        UserRoleForm userRoleForm = new UserRoleForm();
        Integer userId =1;
        Page<UsersEntity> usersEntityPage = new PageImpl<UsersEntity>(new ArrayList<>());
        String message = "test";

        ReflectionTestUtils.invokeMethod(userRoleController,"getUsersInformation",
                request,userRoleForm,userId,usersEntityPage,message);
    }

    private UserDetailsForm getUserDetailsForm() {
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(true);
        userDetailsForm.setLoginInstitutionId(1);
        return userDetailsForm;
    }

    private List<UsersEntity> getUsersEntity() {
        UsersEntity usersEntity = new UsersEntity();
        List<UsersEntity> usersEntityList = new ArrayList<>();
        usersEntity.setId(2);
        usersEntity.setLoginId("1");
        usersEntity.setUserRole(Arrays.asList(new RoleEntity()));
        usersEntityList.add(usersEntity);
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionName("PUL");
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setId(1);
        usersEntity.setInstitutionEntity(institutionEntity);
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleName("Super Admin");
        usersEntity.setUserRole(Arrays.asList(roleEntity));
        return usersEntityList;
    }

}
