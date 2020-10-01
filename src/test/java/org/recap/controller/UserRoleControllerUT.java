package org.recap.controller;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.RoleEntity;
import org.recap.model.jpa.UsersEntity;
import org.recap.model.usermanagement.UserDetailsForm;
import org.recap.model.usermanagement.UserForm;
import org.recap.model.usermanagement.UserRoleForm;
import org.recap.model.usermanagement.UserRoleService;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.RolesDetailsRepositorty;
import org.recap.repository.jpa.UserDetailsRepository;
import org.recap.security.UserManagementService;
import org.recap.util.UserAuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

/**
 * Created by akulak on 24/1/17.
 */
public class UserRoleControllerUT extends BaseTestCase {

    private static final Logger logger = LoggerFactory.getLogger(UserRoleControllerUT.class);

    @Mock
    private UserRoleController mockedUserRoleController;

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Mock
    private UserAuthUtil userAuthUtil;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private UserManagementService userManagementService;

    @Autowired
    private RolesDetailsRepositorty rolesDetailsRepositorty;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;


    @Test
    public void showUserRoles() throws Exception{
        UserRoleForm userRoleForm = new UserRoleForm();
        userRoleForm.setSearchNetworkId("smith");
        usersSessionAttributes();
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(true);
        userDetailsForm.setLoginInstitutionId(1);
        Integer userId = 3;
        List<Object> roles = new ArrayList<>();
        roles.add(2);
        List<Object> institution = new ArrayList<>();
        institution.add("CUL");
        when(request.getSession()).thenReturn(session);
        usersSessionAttributes();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserRoleService()).thenReturn(userRoleService);
        Mockito.when(mockedUserRoleController.getUserManagementService()).thenReturn(userManagementService);
        Mockito.when(mockedUserRoleController.getUserRoleService().getRoles(getUserManagementService().getSuperAdminRoleId(), userDetailsForm.isSuperAdmin())).thenReturn(roles);
        Mockito.when(mockedUserRoleController.getUserRoleService().getInstitutions(userDetailsForm.isSuperAdmin(), userDetailsForm.getLoginInstitutionId())).thenReturn(institution);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().getUserDetails(session, RecapConstants.BARCODE_RESTRICTED_PRIVILEGE)).thenReturn(userDetailsForm);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(true);
        Mockito.when(mockedUserRoleController.showUserRoles(model, request)).thenCallRealMethod();
        String view = mockedUserRoleController.showUserRoles(model, request);
        assertNotNull(view);
        assertEquals("searchRecords",view);
    }
    @Test
    public void showUserRolesInvalidAuthentication() throws Exception{
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(false);
        Mockito.when(mockedUserRoleController.showUserRoles(model, request)).thenCallRealMethod();
        String view = mockedUserRoleController.showUserRoles(model, request);
        assertNotNull(view);
    }
    @Test
    public void searchUserRole() throws Exception{ ;
        UserRoleForm userRoleForm = new UserRoleForm();
        userRoleForm.setSearchNetworkId("smith");
        usersSessionAttributes();
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(false);
        userDetailsForm.setLoginInstitutionId(1);
        Integer userId = 3;
        List<Object> roles = new ArrayList<>();
        roles.add(2);
        List<Object> institution = new ArrayList<>();
        institution.add("CUL");
        usersSessionAttributes();
        Page<UsersEntity> usersEntityPage = getPage();
        boolean superAdmin = true;
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserRoleService()).thenReturn(userRoleService);
        Mockito.when(mockedUserRoleController.getUserManagementService()).thenReturn(userManagementService);
        Mockito.when(mockedUserRoleController.getUserRoleService().searchByNetworkId(userRoleForm, superAdmin)).thenReturn(usersEntityPage);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().getUserDetails(session, RecapConstants.BARCODE_RESTRICTED_PRIVILEGE)).thenCallRealMethod();
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(true);
        Mockito.when(mockedUserRoleController.getUserRoleService().getInstitutions(userDetailsForm.isSuperAdmin(), userDetailsForm.getLoginInstitutionId())).thenReturn(institution);
        Mockito.when(mockedUserRoleController.getUserRoleService().getRoles(getUserManagementService().getSuperAdminRoleId(), userDetailsForm.isSuperAdmin())).thenReturn(roles);
        Mockito.when(mockedUserRoleController.searchUserRole(userRoleForm, model, request)).thenCallRealMethod();
        ModelAndView modelAndView = mockedUserRoleController.searchUserRole(userRoleForm, model, request);
        assertNotNull(modelAndView);
        assertEquals("userRolesSearch :: #request-result-table",modelAndView.getViewName());
    }
    @Test
    public void searchUserRoleInvalidAthentication() throws Exception{ ;
        UserRoleForm userRoleForm = new UserRoleForm();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(false);
        Mockito.when(mockedUserRoleController.searchUserRole(userRoleForm, model, request)).thenCallRealMethod();
        ModelAndView modelAndView = mockedUserRoleController.searchUserRole(userRoleForm, model, request);
        assertNotNull(modelAndView);
        assertEquals("login",modelAndView.getViewName());
    }

    @Test
    public void checkGetterServices(){
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenCallRealMethod();
        Mockito.when(mockedUserRoleController.getUserDetailsRepository()).thenCallRealMethod();
        assertNotEquals(mockedUserRoleController.getUserAuthUtil(),userAuthUtil);
        assertNotEquals(mockedUserRoleController.getUserDetailsRepository(),userDetailsRepository);
    }

    @Test
    public void deleteUserRole() throws Exception{
        UserRoleForm userRoleForm = new UserRoleForm();
        userRoleForm.setSearchNetworkId("smith");
        usersSessionAttributes();
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(true);
        userDetailsForm.setLoginInstitutionId(1);
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setLoginId("1");
        usersEntity.setUserDescription("test");
        usersEntity.setEmailId("hemalatha.s@htcindia.com");
        usersEntity.setUserRole(Arrays.asList(new RoleEntity()));
        usersEntity.setId(1);
        Integer userId = 3;
        List<Object> roles = new ArrayList<>();
        roles.add(2);
        List<Object> institution = new ArrayList<>();
        institution.add("CUL");
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserRoleService()).thenReturn(userRoleService);
        Mockito.when(mockedUserRoleController.getUserManagementService()).thenReturn(userManagementService);
        Mockito.when(mockedUserRoleController.getUserDetailsRepository()).thenReturn(userDetailsRepository);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(true);
        Mockito.when(mockedUserRoleController.getUserDetailsRepository().findById(userId)).thenReturn(Optional.of(usersEntity));
        Mockito.when(mockedUserRoleController.getUserRoleService().getRoles(getUserManagementService().getSuperAdminRoleId(), userDetailsForm.isSuperAdmin())).thenReturn(roles);
        Mockito.when(mockedUserRoleController.getUserRoleService().getInstitutions(userDetailsForm.isSuperAdmin(), userDetailsForm.getLoginInstitutionId())).thenReturn(institution);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().getUserDetails(request.getSession(), RecapConstants.BARCODE_RESTRICTED_PRIVILEGE)).thenReturn(userDetailsForm);
        Mockito.when(mockedUserRoleController.deleteUserRole(userRoleForm.getSearchNetworkId(),3,request,10,1,2)).thenCallRealMethod();
        ModelAndView modelAndView = mockedUserRoleController.deleteUserRole(userRoleForm.getSearchNetworkId(),3,request,10,1,2);
        assertNotNull(modelAndView);
        assertEquals("userRolesSearch",modelAndView.getViewName());
    }
    @Test
    public void deleteUserRoleInvalidAuthentication() throws Exception{
        UserRoleForm userRoleForm = new UserRoleForm();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(false);
        Mockito.when(mockedUserRoleController.deleteUserRole(userRoleForm.getSearchNetworkId(),3,request,10,1,2)).thenCallRealMethod();
        ModelAndView modelAndView = mockedUserRoleController.deleteUserRole(userRoleForm.getSearchNetworkId(),3,request,10,1,2);
        assertNotNull(modelAndView);
        assertEquals("login",modelAndView.getViewName());
    }
    @Test
    public void deleteUser()throws Exception{
        UserRoleForm userRoleForm = new UserRoleForm();
        userRoleForm.setSearchNetworkId("smith");
        Page<UsersEntity> usersEntityPage = getPage();
        boolean superAdmin = true;
        usersSessionAttributes();
        List<Object> roles = new ArrayList<>();
        List<Object> institution = new ArrayList<>();
        roles.add(2);
        institution.add("CUL");
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(true);
        userDetailsForm.setLoginInstitutionId(1);
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserRoleService()).thenReturn(userRoleService);
        Mockito.when(mockedUserRoleController.getUserManagementService()).thenReturn(userManagementService);
        Mockito.when(mockedUserRoleController.getUserRoleService().getRoles(getUserManagementService().getSuperAdminRoleId(), userDetailsForm.isSuperAdmin())).thenReturn(roles);
        Mockito.when(mockedUserRoleController.getUserRoleService().getInstitutions(userDetailsForm.isSuperAdmin(), userDetailsForm.getLoginInstitutionId())).thenReturn(institution);
        Mockito.when(mockedUserRoleController.getUserDetailsRepository()).thenReturn(userDetailsRepository);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().getUserDetails(session, RecapConstants.BARCODE_RESTRICTED_PRIVILEGE)).thenCallRealMethod();
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(true);
        Mockito.when(mockedUserRoleController.getUserRoleService().searchByNetworkId(userRoleForm, superAdmin)).thenReturn(usersEntityPage);
        Mockito.when(mockedUserRoleController.deleteUser(userRoleForm,model,3,userRoleForm.getSearchNetworkId(),10,1,2,request)).thenCallRealMethod();
        ModelAndView modelAndView = mockedUserRoleController.deleteUser(userRoleForm,model,3,userRoleForm.getSearchNetworkId(),10,1,2,request);
        assertNotNull(modelAndView);
        assertEquals("userRolesSearch",modelAndView.getViewName());
    }
    @Test
    public void deleteUserInvalidAuthentication()throws Exception{
        UserRoleForm userRoleForm = new UserRoleForm();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(false);
        Mockito.when(mockedUserRoleController.deleteUser(userRoleForm,model,3,userRoleForm.getSearchNetworkId(),10,1,2,request)).thenCallRealMethod();
        ModelAndView modelAndView = mockedUserRoleController.deleteUser(userRoleForm,model,3,userRoleForm.getSearchNetworkId(),10,1,2,request);
        assertNotNull(modelAndView);
        assertEquals("login",modelAndView.getViewName());
    }

    @Test
    public void searchFirstPage()throws Exception{
        usersSessionAttributes();
        UserRoleForm userRoleForm = new UserRoleForm();
        userRoleForm.setPageSize(10);
        userRoleForm.setPageNumber(1);
        userRoleForm.setSearchNetworkId("");
        userRoleForm.setUserEmailId("");
        Page<UsersEntity> usersEntityPage = getPage();
        boolean superAdmin = true;
        List<Object> roles = new ArrayList<>();
        roles.add(2);
        List<Object> institution = new ArrayList<>();
        institution.add("CUL");
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(true);
        userDetailsForm.setLoginInstitutionId(1);
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserRoleService()).thenReturn(userRoleService);
        Mockito.when(mockedUserRoleController.getUserManagementService()).thenReturn(userManagementService);
        Mockito.when(mockedUserRoleController.getUserRoleService().getRoles(getUserManagementService().getSuperAdminRoleId(), userDetailsForm.isSuperAdmin())).thenReturn(roles);
        Mockito.when(mockedUserRoleController.getUserRoleService().getInstitutions(userDetailsForm.isSuperAdmin(), userDetailsForm.getLoginInstitutionId())).thenReturn(institution);
        Mockito.when(mockedUserRoleController.getUserDetailsRepository()).thenReturn(userDetailsRepository);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().getUserDetails(session, RecapConstants.BARCODE_RESTRICTED_PRIVILEGE)).thenReturn(userDetailsForm);
        Mockito.when(mockedUserRoleController.getUserRoleService().searchUsers(userRoleForm, superAdmin)).thenReturn(usersEntityPage);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(true);
        Mockito.when((String) session.getAttribute(RecapConstants.USER_NAME)).thenReturn("test");
        Mockito.when(mockedUserRoleController.searchFirstPage(userRoleForm, model, request)).thenCallRealMethod();
        ModelAndView modelAndView = mockedUserRoleController.searchFirstPage(userRoleForm, model, request);
        assertNotNull(modelAndView);
        assertEquals("userRolesSearch :: #request-result-table",modelAndView.getViewName());
    }
    @Test
    public void searchFirstPageInvalidAuthetication()throws Exception{
        UserRoleForm userRoleForm = new UserRoleForm();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(false);
        Mockito.when(mockedUserRoleController.searchFirstPage(userRoleForm, model, request)).thenCallRealMethod();
        ModelAndView modelAndView = mockedUserRoleController.searchFirstPage(userRoleForm, model, request);
        assertNotNull(modelAndView);
        assertEquals("login",modelAndView.getViewName());
    }
    @Test
    public void searchNextPage()throws Exception{
        usersSessionAttributes();
        UserRoleForm userRoleForm = new UserRoleForm();
        userRoleForm.setSearchNetworkId("smith");
        userRoleForm.setPageSize(10);
        userRoleForm.setPageNumber(1);
        Page<UsersEntity> usersEntityPage = getPage();
        boolean superAdmin = true;
        userRoleForm.setNetworkLoginId("test");
        userRoleForm.setEmailId("test@gmail.com");
        userRoleForm.setUserId(2);
        userRoleForm.setUserDescription("testdescription");
        List<Integer> role = new ArrayList<>();
        role.add(2);
        userRoleForm.setSelectedForCreate(role);
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(true);
        userDetailsForm.setLoginInstitutionId(1);
        List<Object> roles = new ArrayList<>();
        roles.add(2);
        List<Object> institution = new ArrayList<>();
        institution.add(1);
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserRoleService()).thenReturn(userRoleService);
        Mockito.when(mockedUserRoleController.getUserManagementService()).thenReturn(userManagementService);
        Mockito.when(mockedUserRoleController.getUserDetailsRepository()).thenReturn(userDetailsRepository);
        Mockito.when(mockedUserRoleController.getUserRoleService().getRoles(getUserManagementService().getSuperAdminRoleId(), userDetailsForm.isSuperAdmin())).thenReturn(roles);
        Mockito.when(mockedUserRoleController.getUserRoleService().getInstitutions(userDetailsForm.isSuperAdmin(), userDetailsForm.getLoginInstitutionId())).thenReturn(institution);;
        Mockito.when(mockedUserRoleController.getUserAuthUtil().getUserDetails(session, RecapConstants.BARCODE_RESTRICTED_PRIVILEGE)).thenCallRealMethod();
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(true);
        Mockito.when(mockedUserRoleController.getUserRoleService().searchByNetworkId(userRoleForm, superAdmin)).thenReturn(usersEntityPage);
        Mockito.when(mockedUserRoleController.searchNextPage(userRoleForm, model, request)).thenCallRealMethod();
        ModelAndView modelAndView = mockedUserRoleController.searchNextPage(userRoleForm, model, request);
        assertNotNull(modelAndView);
//        assertEquals("login",modelAndView.getViewName());
    }


    @Test
    public void searchPreviousPage()throws Exception{
        usersSessionAttributes();
        UserRoleForm userRoleForm = new UserRoleForm();
        userRoleForm.setSearchNetworkId("smith");
        userRoleForm.setPageSize(10);
        userRoleForm.setPageNumber(1);
        Page<UsersEntity> usersEntityPage = getPage();
        boolean superAdmin = true;
        userRoleForm.setNetworkLoginId("test");
        userRoleForm.setEmailId("test@gmail.com");
        userRoleForm.setUserId(2);
        userRoleForm.setUserDescription("testdescription");
        List<Integer> role = new ArrayList<>();
        role.add(2);
        userRoleForm.setSelectedForCreate(role);
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(true);
        userDetailsForm.setLoginInstitutionId(1);
        List<Object> roles = new ArrayList<>();
        roles.add(2);
        List<Object> institution = new ArrayList<>();
        institution.add(1);
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserRoleService()).thenReturn(userRoleService);
        Mockito.when(mockedUserRoleController.getUserManagementService()).thenReturn(userManagementService);
        Mockito.when(mockedUserRoleController.getUserDetailsRepository()).thenReturn(userDetailsRepository);
        Mockito.when(mockedUserRoleController.getUserRoleService().getRoles(getUserManagementService().getSuperAdminRoleId(), userDetailsForm.isSuperAdmin())).thenReturn(roles);
        Mockito.when(mockedUserRoleController.getUserRoleService().getInstitutions(userDetailsForm.isSuperAdmin(), userDetailsForm.getLoginInstitutionId())).thenReturn(institution);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().getUserDetails(session, RecapConstants.BARCODE_RESTRICTED_PRIVILEGE)).thenCallRealMethod();
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(true);
        Mockito.when(mockedUserRoleController.getUserRoleService().searchByNetworkId(userRoleForm, superAdmin)).thenReturn(usersEntityPage);
        Mockito.when(mockedUserRoleController.searchPreviousPage(userRoleForm, model, request)).thenCallRealMethod();
        ModelAndView modelAndView = mockedUserRoleController.searchPreviousPage(userRoleForm,model, request);
        assertNotNull(modelAndView);
//        assertEquals("login",modelAndView.getViewName());
    }

    @Test
    public void searchLastPage()throws Exception{
        usersSessionAttributes();
        UserRoleForm userRoleForm = new UserRoleForm();
        userRoleForm.setSearchNetworkId("smith");
        userRoleForm.setPageSize(10);
        userRoleForm.setPageNumber(1);
        Page<UsersEntity> usersEntityPage = getPage();
        boolean superAdmin = true;
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserDetailsRepository()).thenReturn(userDetailsRepository);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().getUserDetails(session, RecapConstants.BARCODE_RESTRICTED_PRIVILEGE)).thenCallRealMethod();
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(false);
        Mockito.when(userRoleService.searchUsers(userRoleForm, superAdmin)).thenReturn(usersEntityPage);
        Mockito.when(mockedUserRoleController.searchLastPage(userRoleForm, model, request)).thenCallRealMethod();
        ModelAndView modelAndView = mockedUserRoleController.searchLastPage(userRoleForm, model, request);
        assertNotNull(modelAndView);
        assertEquals("login",modelAndView.getViewName());
    }

    @Test
    public void createUserRequest()throws Exception{
        when(request.getSession()).thenReturn(session);
        usersSessionAttributes();
        UserRoleForm userRoleForm = new UserRoleForm();
        userRoleForm.setNetworkLoginId("test");
        userRoleForm.setEmailId("test@gmail.com");
        userRoleForm.setUserId(2);
        userRoleForm.setUserDescription("testdescription");
        List<Integer> role = new ArrayList<>();
        role.add(2);
        userRoleForm.setSelectedForCreate(role);
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(true);
        userDetailsForm.setLoginInstitutionId(1);
        List<Object> roles = new ArrayList<>();
        List<Object> institution = new ArrayList<>();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserRoleService()).thenReturn(userRoleService);
        Mockito.when(mockedUserRoleController.getUserManagementService()).thenReturn(userManagementService);
        Mockito.when(mockedUserRoleController.getUserRoleService().getRoles(getUserManagementService().getSuperAdminRoleId(), userDetailsForm.isSuperAdmin())).thenReturn(roles);
        Mockito.when(mockedUserRoleController.getUserRoleService().getInstitutions(userDetailsForm.isSuperAdmin(), userDetailsForm.getLoginInstitutionId())).thenReturn(institution);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().getUserDetails(session, RecapConstants.BARCODE_RESTRICTED_PRIVILEGE)).thenReturn(userDetailsForm);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(true);
        Mockito.when(userRoleService.saveNewUserToDB(userRoleForm)).thenReturn(new UsersEntity());
        Mockito.when(mockedUserRoleController.createUserRequest(userRoleForm,request)).thenCallRealMethod();
        ModelAndView modelAndView = mockedUserRoleController.createUserRequest(userRoleForm,request);
        assertNotNull(modelAndView);
        assertEquals("userRolesSearch",modelAndView.getViewName());
    }
    @Test
    public void createUserRequestInvalidAuthentication()throws Exception{
        UserRoleForm userRoleForm = new UserRoleForm();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(false);
        Mockito.when(mockedUserRoleController.createUserRequest(userRoleForm,request)).thenCallRealMethod();
        ModelAndView modelAndView = mockedUserRoleController.createUserRequest(userRoleForm,request);
        assertNotNull(modelAndView);
        assertEquals("login",modelAndView.getViewName());
    }
    @Test
    public void editUser()throws Exception{
        usersSessionAttributes();
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(true);
        userDetailsForm.setRecapUser(true);
        userDetailsForm.setRecapPermissionAllowed(true);
        userDetailsForm.setLoginInstitutionId(1);
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setLoginId("1");
        usersEntity.setUserDescription("test");
        usersEntity.setEmailId("hemalatha.s@htcindia.com");
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(1);
        usersEntity.setUserRole(Arrays.asList(roleEntity));
        usersEntity.setId(1);
        ModelAndView modelAndView1 = new ModelAndView();
        modelAndView1.setViewName("userRolesSearch");
        List<Object> roles = new ArrayList<>();
        roles.add(2);
        List<Object> institution = new ArrayList<>();
        institution.add("CUL");
        Integer userId = 3;
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserRoleService()).thenReturn(userRoleService);
        Mockito.when(mockedUserRoleController.getUserManagementService()).thenReturn(userManagementService);
        Mockito.when(mockedUserRoleController.getUserDetailsRepository()).thenReturn(userDetailsRepository);
        Mockito.when(mockedUserRoleController.getUserRoleService().getRoles(getUserManagementService().getSuperAdminRoleId(),userDetailsForm.isSuperAdmin())).thenReturn(roles);
        Mockito.when(mockedUserRoleController.getUserRoleService().getInstitutions(userDetailsForm.isSuperAdmin(), userDetailsForm.getLoginInstitutionId())).thenReturn(institution);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().getUserDetails(session, RecapConstants.BARCODE_RESTRICTED_PRIVILEGE)).thenReturn(userDetailsForm);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(true);
        Mockito.when(mockedUserRoleController.getUserDetailsRepository().findById(userId)).thenReturn(Optional.of(usersEntity));
        Mockito.when(mockedUserRoleController.editUser(userId, "smith", request)).thenCallRealMethod();
        ModelAndView modelAndView = mockedUserRoleController.editUser(userId, "smith", request);
        assertNotNull(modelAndView);
        assertEquals("userRolesSearch",modelAndView.getViewName());
    }
    @Test
    public void editUserInvalidAuthentication()throws Exception{
        usersSessionAttributes();
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        Integer userId = 3;
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(false);
        Mockito.when(mockedUserRoleController.editUser(userId, "smith", request)).thenCallRealMethod();
        ModelAndView modelAndView = mockedUserRoleController.editUser(userId, "smith", request);
        assertNotNull(modelAndView);
        assertEquals("login",modelAndView.getViewName());
    }
    @Test
    public void saveEditUserDetails()throws Exception{
        UsersEntity usersEntity = getUserEntity();
        UserRoleForm userRoleForm = new UserRoleForm();
        userRoleForm.setNetworkLoginId("test");
        userRoleForm.setEmailId("test@gmail.com");
        userRoleForm.setUserId(2);
        userRoleForm.setUserDescription("test Description");
        List<Integer> role = new ArrayList<>();
        role.add(2);
        userRoleForm.setSelectedForCreate(role);
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(true);
        userDetailsForm.setLoginInstitutionId(1);
        List<Object> roles = new ArrayList<>();
        roles.add(2);
        List<Object> institution = new ArrayList<>();
        institution.add("CUL");
        Integer userId = 3;
        String networkLoginId = "test";
        String userDescription = "test description";
        Integer institutionId = 1;
        List<Integer> roleIds = role;
        String userEmailId = "test@mail.com";
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(session.getAttribute(RecapConstants.USER_NAME)).thenReturn("username");
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserRoleService()).thenReturn(userRoleService);
        Mockito.when(mockedUserRoleController.getUserManagementService()).thenReturn(userManagementService);
        Mockito.when(mockedUserRoleController.getUserRoleService().getRoles(getUserManagementService().getSuperAdminRoleId(),userDetailsForm.isSuperAdmin())).thenReturn(new ArrayList<>());
        Mockito.when(mockedUserRoleController.getUserRoleService().getInstitutions(userDetailsForm.isSuperAdmin(), userDetailsForm.getLoginInstitutionId())).thenReturn(institution);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().getUserDetails(session, RecapConstants.BARCODE_RESTRICTED_PRIVILEGE)).thenReturn(userDetailsForm);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(true);
        String[] roleId = {"2","3"};
        when(request.getParameterValues("roleIds[]")).thenReturn(roleId);
        ModelAndView modelAndView1 = new ModelAndView();
        modelAndView1.setViewName("userRolesSearch");
        Mockito.when(mockedUserRoleController.getUserRoleService().saveEditedUserToDB(userId, networkLoginId, userDescription, institutionId, roleIds, userEmailId,userRoleForm)).thenReturn(usersEntity);
        Mockito.when(mockedUserRoleController.saveEditUserDetails(userId, networkLoginId, userDescription, institutionId, userEmailId, request)).thenCallRealMethod();
        ModelAndView modelAndView = mockedUserRoleController.saveEditUserDetails(userId, networkLoginId, userDescription, institutionId, userEmailId, request);
        assertNotNull(modelAndView);
        assertEquals("userRolesSearch",modelAndView.getViewName());

    }
    @Test
    public void saveEditUserDetailsInvalidAuthentication()throws Exception{
        Integer userId = 3;
        String networkLoginId = "test";
        String userDescription = "test description";
        Integer institutionId = 1;
        String userEmailId = "test@mail.com";
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(false);
        Mockito.when(mockedUserRoleController.saveEditUserDetails(userId, networkLoginId, userDescription, institutionId, userEmailId, request)).thenCallRealMethod();
        ModelAndView modelAndView = mockedUserRoleController.saveEditUserDetails(userId, networkLoginId, userDescription, institutionId, userEmailId, request);
        assertNotNull(modelAndView);
        assertEquals("login",modelAndView.getViewName());

    }

    @Test
    public void testGoBackFuctionality() throws Exception {
        when(request.getSession()).thenReturn(session);
        usersSessionAttributes();
        List<Object> roles = new ArrayList<>();
        roles.add(2);
        List<Object> institution = new ArrayList<>();
        institution.add("CUL");
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(true);
        userDetailsForm.setLoginInstitutionId(1);
        UserRoleForm userRoleForm = new UserRoleForm();
        userRoleForm.setNetworkLoginId("test");
        userRoleForm.setEmailId("test@gmail.com");
        userRoleForm.setUserId(2);
        userRoleForm.setUserDescription("testdescription");
        List<Integer> role = new ArrayList<>();
        role.add(2);
        userRoleForm.setSelectedForCreate(role);
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(mockedUserRoleController.getUserAuthUtil()).thenReturn(userAuthUtil);
        Mockito.when(mockedUserRoleController.getUserRoleService()).thenReturn(userRoleService);
        Mockito.when(mockedUserRoleController.getUserManagementService()).thenReturn(userManagementService);
        Mockito.when(mockedUserRoleController.getUserRoleService().getRoles(getUserManagementService().getSuperAdminRoleId(), userDetailsForm.isSuperAdmin())).thenReturn(roles);
        Mockito.when(mockedUserRoleController.getUserRoleService().getInstitutions(userDetailsForm.isSuperAdmin(), userDetailsForm.getLoginInstitutionId())).thenReturn(institution);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().getUserDetails(session, RecapConstants.BARCODE_RESTRICTED_PRIVILEGE)).thenReturn(userDetailsForm);
        Mockito.when(mockedUserRoleController.getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL)).thenReturn(true);
        Mockito.when(mockedUserRoleController.goBack(userRoleForm,request)).thenCallRealMethod();
        ModelAndView modelAndView = mockedUserRoleController.goBack(userRoleForm,request);
        assertNotNull(modelAndView);
        assertEquals(modelAndView.getViewName(),"userRolesSearch");
    }
    public RoleEntity saveRole(){
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleName("test role");
        roleEntity.setRoleDescription("test role");
        roleEntity.setCreatedBy("test");
        roleEntity.setCreatedDate(new Date());
        roleEntity.setLastUpdatedDate(new Date());
        roleEntity.setLastUpdatedBy("test");
        RoleEntity savedRoleEntity = rolesDetailsRepositorty.save(roleEntity);
        return savedRoleEntity;

    }

    private void usersSessionAttributes() throws Exception {
        when(request.getSession()).thenReturn(session);
        UserForm userForm = new UserForm();
        userForm.setUsername("kholi");
        userForm.setInstitution("3");
        userForm.setPassword("12345");
        UsernamePasswordToken token=new UsernamePasswordToken(userForm.getUsername()+ RecapConstants.TOKEN_SPLITER +userForm.getInstitution(),userForm.getPassword(),true);
        userAuthUtil.doAuthentication(token);
        when(session.getAttribute(RecapConstants.USER_TOKEN)).thenReturn(token);
        when(session.getAttribute(RecapConstants.USER_ID)).thenReturn(3);
        when(session.getAttribute(RecapConstants.SUPER_ADMIN_USER)).thenReturn(true);
        when(session.getAttribute(RecapConstants.RECAP_USER)).thenReturn(false);
        when(session.getAttribute(RecapConstants.BARCODE_RESTRICTED_PRIVILEGE)).thenReturn(false);
        when(session.getAttribute(RecapConstants.USER_INSTITUTION)).thenReturn(1);
        when(session.getAttribute(RecapConstants.REQUEST_ALL_PRIVILEGE)).thenReturn(false);
        userAuthUtil.getUserDetails(session,RecapConstants.BARCODE_RESTRICTED_PRIVILEGE);
    }

    public List<UsersEntity> getUsersEntity(){
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
        roleEntity.setRoleName("1");
        usersEntity.setUserRole(Arrays.asList(roleEntity));
        return usersEntityList;
    }
    private UsersEntity getUserEntity(){
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setLoginId("1");
        usersEntity.setUserDescription("test");
        usersEntity.setEmailId("hemalatha.s@htcindia.com");
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(1);
        usersEntity.setUserRole(Arrays.asList(roleEntity));
        usersEntity.setId(1);
        return usersEntity;
    }

    public Page getPage(){
       Page<UsersEntity> page = new Page<UsersEntity>() {
            @Override
            public int getTotalPages() {
                return 0;
            }

            @Override
            public long getTotalElements() {
                return 2;
            }

           @Override
           public <U> Page<U> map(Function<? super UsersEntity, ? extends U> converter) {
               return null;
           }

           /*@Override
            public <S> Page<S> map(Converter<? super UsersEntity, ? extends S> converter) {
                return null;
            }*/

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List<UsersEntity> getContent() {

                return getUsersEntity();
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<UsersEntity> iterator() {
                return null;
            }
        };
        return page;
    }
    public UserManagementService getUserManagementService() {
        return userManagementService;
    }
}
