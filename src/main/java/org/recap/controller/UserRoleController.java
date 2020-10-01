package org.recap.controller;

import org.apache.commons.lang3.StringUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.RoleEntity;
import org.recap.model.jpa.UsersEntity;
import org.recap.model.usermanagement.UserDetailsForm;
import org.recap.model.usermanagement.UserRoleForm;
import org.recap.model.usermanagement.UserRoleService;
import org.recap.repository.jpa.UserDetailsRepository;
import org.recap.security.UserManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by dharmendrag on 23/12/16.
 */
@RestController
@RequestMapping("/userRoles")
@CrossOrigin
public class UserRoleController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(UserRoleController.class);

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserManagementService userManagementService;

    /**
     * Gets user details repository.
     *
     * @return the user details repository
     */
    public UserDetailsRepository getUserDetailsRepository() {
        return userDetailsRepository;
    }

    public UserRoleService getUserRoleService() {
        return userRoleService;
    }

    public UserManagementService getUserManagementService() {
        return userManagementService;
    }

    /**
     * Render the users UI page for the scsb application.
     *
     * @param model   the model
     * @param request the request
     * @return the string
     */
    @GetMapping("/userRoles")
    public UserRoleForm showUserRoles() {
        // HttpSession session = request.getSession(false);
        // boolean authenticated = getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL);
        // if (authenticated) {
        //logger.info(RecapConstants.USERS_TAB_CLICKED);
        UserRoleForm userRoleForm = new UserRoleForm();
        //UserDetailsForm userDetailsForm = getUserAuthUtil().getUserDetails(session, RecapConstants.BARCODE_RESTRICTED_PRIVILEGE);
        //  getAndSetRolesAndInstitutions(userRoleForm, userDetailsForm);
        userRoleForm.setAllowCreateEdit(true);
        //model.addAttribute(RecapConstants.USER_ROLE_FORM, userRoleForm);
        //model.addAttribute(RecapCommonConstants.TEMPLATE, RecapConstants.USER_ROLES_SEARCH);
        //    return RecapConstants.VIEW_SEARCH_RECORDS;
        // } else {
        //return UserManagementService.unAuthorizedUser(session,"Users",logger);
        //}
        return userRoleForm;
    }

    private void getAndSetRolesAndInstitutions(UserRoleForm userRoleForm, UserDetailsForm userDetailsForm) {
        setUserRoleForm(userRoleForm, userDetailsForm);
    }

    /**
     * Gets user search results from scsb database and display them as rows in the search user UI page.
     *
     * @param userRoleForm the user role form
     * @param model        the model
     * @param request      the request
     * @return the model and view
     */
    @PostMapping("/searchUsers")
    public UserRoleForm searchUserRole(UserRoleForm userRoleForm) {
        logger.info("Users - Search button Clicked");
       // HttpSession session = request.getSession();
        //boolean authenticated = getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL);
       /* if (authenticated) {
            logger.info("Users Tab Clicked");
            try {
                priorSearch(userRoleForm, request);
                userRoleForm.setShowPagination(true);
                model.addAttribute(RecapCommonConstants.TEMPLATE, RecapConstants.USER_ROLES_SEARCH);
            } catch (Exception e) {
                logger.error(RecapCommonConstants.LOG_ERROR, e);
            }
            return new ModelAndView(RecapConstants.VIEW_REQUEST_RESULT_TABLE, RecapConstants.USER_ROLE_FORM, userRoleForm);
        } else {
            return new ModelAndView(RecapConstants.VIEW_LOGIN);
        }*/
        return userRoleForm;
    }


    /**
     * Provide information about the user which has been selected to delete in scsb.
     *
     * @param networkLoginId the network login id
     * @param userId         the user id
     * @param request        the request
     * @param pagesize       the pagesize
     * @param pageNumber     the page number
     * @param totalPageCount the total page count
     * @return the model and view
     */
    @GetMapping("/deleteUser")
    public ModelAndView deleteUserRole(String networkLoginId, Integer userId, HttpServletRequest request, Integer pagesize, Integer pageNumber, Integer totalPageCount) {
        HttpSession session = request.getSession(false);
        boolean authenticated = getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL);
        if (authenticated) {
            UserDetailsForm userDetailsForm = getUserAuthUtil().getUserDetails(session, RecapConstants.BARCODE_RESTRICTED_PRIVILEGE);
            logger.info("User - Delete User clicked");
            Optional<UsersEntity> usersEntity = getUserDetailsRepository().findById(userId);
            UserRoleForm userRoleForm = new UserRoleForm();
            userRoleForm.setAfterDelPageSize(pagesize);
            userRoleForm.setAfterDelPageNumber(pageNumber);
            userRoleForm.setAfterDelTotalPageCount(totalPageCount);
            getAndSetRolesAndInstitutions(userRoleForm, userDetailsForm);
            userRoleForm.setEditUserId(userId);
            userRoleForm.setUserId(userId);
            if (usersEntity.isPresent()) {
                userRoleForm.setEditNetworkLoginId(usersEntity.get().getLoginId());
                userRoleForm.setEditUserDescription(usersEntity.get().getUserDescription());
                userRoleForm.setEmailId(usersEntity.get().getEmailId());
                userRoleForm.setEditEmailId(usersEntity.get().getEmailId());
                userRoleForm.setEditInstitutionId(usersEntity.get().getInstitutionId());
                setUserRoleFormRoleId(userRoleForm, usersEntity);
            }
            userRoleForm.setShowSelectedForCreate(userRoleForm.getEditSelectedForCreate());
            userRoleForm.setShowUserSearchView(false);
            return new ModelAndView(RecapConstants.USER_ROLES_SEARCH, RecapConstants.USER_ROLE_FORM, userRoleForm);
        } else {
            return new ModelAndView(RecapConstants.VIEW_LOGIN);
        }
    }


    /**
     * To delete the user permanently in scsb.
     *
     * @param userRoleForm   the user role form
     * @param model          the model
     * @param userId         the user id
     * @param networkLoginId the network login id
     * @param pageNumber     the page number
     * @param totalPageCount the total page count
     * @param pageSize       the page size
     * @param request        the request
     * @return the model and view
     */
    @GetMapping("/delete")
    public ModelAndView deleteUser(@Valid @ModelAttribute("userRoleForm") UserRoleForm userRoleForm, Model model, Integer userId, String networkLoginId, Integer pageNumber, Integer totalPageCount, Integer pageSize, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean authenticated = getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL);
        if (authenticated) {
            UsersEntity usersEntity = new UsersEntity();
            usersEntity.setId(userId);
            try {
                getUserDetailsRepository().delete(usersEntity);
                userRoleForm.setDeletedSuccessMsg(true);
                userRoleForm.setMessage(networkLoginId + RecapConstants.DELETED_SUCCESSFULLY);
                priorSearch(userRoleForm, request);
                userRoleForm.setAfterDelPageNumber(pageNumber);
                userRoleForm.setAfterDelTotalPageCount(totalPageCount);
                userRoleForm.setAfterDelPageSize(pageSize);
                userRoleForm.setShowPagination(true);
                userRoleForm.setShowResults(true);
                model.addAttribute(RecapCommonConstants.TEMPLATE, RecapConstants.USER_ROLES_SEARCH);
            } catch (Exception e) {
                logger.error(RecapCommonConstants.LOG_ERROR, e);
            }
            userRoleForm.setShowUserSearchView(true);
            return new ModelAndView(RecapConstants.USER_ROLES_SEARCH, RecapConstants.USER_ROLE_FORM, userRoleForm);
        } else {
            return new ModelAndView(RecapConstants.VIEW_LOGIN);
        }
    }

    /**
     * Gets first page user search results from scsb database and display them as rows in the search user UI page.
     *
     * @param userRoleForm the user role form
     * @param model        the model
     * @param request      the request
     * @return the model and view
     */
    @PostMapping("/first")
    public ModelAndView searchFirstPage(@ModelAttribute("userForm") UserRoleForm userRoleForm, Model model, HttpServletRequest request) {
        logger.info("Users - Search First Page button Clicked");
        HttpSession session = request.getSession(false);
        boolean authenticated = getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL);
        if (authenticated) {
            userRoleForm.resetPageNumber();
            priorSearch(userRoleForm, request);
            model.addAttribute(RecapCommonConstants.TEMPLATE, RecapConstants.USER_ROLES_SEARCH);
            return new ModelAndView(RecapConstants.VIEW_REQUEST_RESULT_TABLE, RecapConstants.USER_ROLE_FORM, userRoleForm);
        } else {
            return new ModelAndView(RecapConstants.VIEW_LOGIN);
        }
    }

    /**
     * Gets next page user search results from scsb database and display them as rows in the search user UI page.
     *
     * @param userRoleForm the user role form
     * @param model        the model
     * @param request      the request
     * @return the model and view
     */
    @PostMapping("/next")
    public ModelAndView searchNextPage(@ModelAttribute("userForm") UserRoleForm userRoleForm, Model model, HttpServletRequest request) {
        logger.info("Users - Search Next Page button Clicked");
        return getPaginationModelAndView(userRoleForm, model, request);
    }

    /**
     * Gets previous page user search results from scsb database and display them as rows in the search user UI page.
     *
     * @param userRoleForm the user role form
     * @param model        the model
     * @param request      the request
     * @return the model and view
     */
    @PostMapping("/previous")
    public ModelAndView searchPreviousPage(@ModelAttribute("userForm") UserRoleForm userRoleForm, Model model, HttpServletRequest request) {
        logger.info("Users - Search Previous Page button Clicked");
        return getPaginationModelAndView(userRoleForm, model, request);
    }

    /**
     * Gets last page user search results from scsb database and display them as rows in the search user UI page.
     *
     * @param userRoleForm the user role form
     * @param model        the model
     * @param request      the request
     * @return the model and view
     */
    @PostMapping("/last")
    public ModelAndView searchLastPage(@ModelAttribute("userForm") UserRoleForm userRoleForm, Model model, HttpServletRequest request) {
        logger.info("Users - Search Last Page button Clicked");
        return getPaginationModelAndView(userRoleForm, model, request);
    }

    /**
     * To create a new user in the scsb.
     *
     * @param userRoleForm the user role form
     * @param request      the request
     * @return the model and view
     */
    @PostMapping("/createUser")
    public ModelAndView createUserRequest(@ModelAttribute("userRoleForm") UserRoleForm userRoleForm, HttpServletRequest request) {
        logger.info("User - Create Request clicked");
        HttpSession session = request.getSession(false);
        boolean authenticated = getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL);
        if (authenticated) {
            UserDetailsForm userDetailsForm = getUserAuthUtil().getUserDetails(session, RecapConstants.BARCODE_RESTRICTED_PRIVILEGE);
            getAndSetRolesAndInstitutions(userRoleForm, userDetailsForm);
            Object userName = session.getAttribute(RecapConstants.USER_NAME);
            userRoleForm.setCreatedBy(String.valueOf(userName));
            UsersEntity usersEntity = getUserRoleService().saveNewUserToDB(userRoleForm);
            if (usersEntity != null) {
                userRoleForm.setShowCreateSuccess(true);
                userRoleForm.setAllowCreateEdit(true);
            }
            userRoleForm.setShowUserSearchView(false);
            return new ModelAndView(RecapConstants.USER_ROLES_SEARCH, RecapConstants.USER_ROLE_FORM, userRoleForm);
        } else {
            return new ModelAndView(RecapConstants.VIEW_LOGIN);
        }
    }

    /**
     * Provide information about the user which has been selected to edit in scsb.
     *
     * @param userId         the user id
     * @param networkLoginId the network login id
     * @param request        the request
     * @return the model and view
     */
    @GetMapping("/editUser")
    public ModelAndView editUser(@ModelAttribute("userId") Integer userId, @ModelAttribute("networkLoginId") String networkLoginId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean authenticated = getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL);
        if (authenticated) {
            UserDetailsForm userDetailsForm = getUserAuthUtil().getUserDetails(request.getSession(false), RecapConstants.BARCODE_RESTRICTED_PRIVILEGE);
            logger.info("User - editUser clicked");
            Optional<UsersEntity> usersEntity = getUserDetailsRepository().findById(userId);
            UserRoleForm userRoleForm = new UserRoleForm();

            if (usersEntity.isPresent()) {
                getAndSetRolesAndInstitutions(userRoleForm, userDetailsForm);
                userRoleForm.setEditNetworkLoginId(usersEntity.get().getLoginId());
                userRoleForm.setEditUserDescription(usersEntity.get().getUserDescription());
                userRoleForm.setEditUserId(userId);
                userRoleForm.setUserId(userId);
                userRoleForm.setEmailId(usersEntity.get().getEmailId());
                userRoleForm.setEditEmailId(usersEntity.get().getEmailId());
                setUserRoleFormRoleId(userRoleForm, usersEntity);
                userRoleForm.setShowSelectedForCreate(userRoleForm.getEditSelectedForCreate());
                userRoleForm.setEditInstitutionId(usersEntity.get().getInstitutionId());
                userRoleForm.setShowUserSearchView(false);
            }
            return new ModelAndView(RecapConstants.USER_ROLES_SEARCH, RecapConstants.USER_ROLE_FORM, userRoleForm);
        } else {
            return new ModelAndView(RecapConstants.VIEW_LOGIN);
        }
    }

    /**
     * To save the edited user details in scsb.
     *
     * @param userId          the user id
     * @param networkLoginId  the network login id
     * @param userDescription the user description
     * @param institutionId   the institution id
     * @param userEmailId     the user email id
     * @param request         the request
     * @return the model and view
     */
    @GetMapping("/saveEditUserDetails")
    public ModelAndView saveEditUserDetails(@ModelAttribute("userId") Integer userId, @ModelAttribute("networkLoginId") String networkLoginId,
                                            @ModelAttribute("userDescription") String userDescription,
                                            @ModelAttribute("institutionId") Integer institutionId,
                                            @ModelAttribute("userEmailId") String userEmailId,
                                            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean authenticated = getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL);
        if (authenticated) {
            UserRoleForm userRoleForm = new UserRoleForm();
            UserDetailsForm userDetailsForm = getUserAuthUtil().getUserDetails(request.getSession(false), RecapConstants.BARCODE_RESTRICTED_PRIVILEGE);
            List<Integer> roleIds = new ArrayList<>();
            String[] parameterValues = request.getParameterValues("roleIds[]");
            for (String parameterValue : parameterValues) {
                roleIds.add(Integer.valueOf(parameterValue));
            }
            userRoleForm.setMessage(networkLoginId + RecapConstants.EDITED_AND_SAVED);
            getAndSetRolesAndInstitutions(userRoleForm, userDetailsForm);
            userRoleForm.setUserId(userId);
            Object userName = session.getAttribute(RecapConstants.USER_NAME);
            userRoleForm.setLastUpdatedBy(String.valueOf(userName));
            UsersEntity usersEntity = getUserRoleService().saveEditedUserToDB(userId, networkLoginId, userDescription, institutionId, roleIds, userEmailId, userRoleForm);
            if (usersEntity != null) {
                userRoleForm.setShowEditSuccess(true);
                userRoleForm.setEditNetworkLoginId(usersEntity.getLoginId());
                userRoleForm.setEditUserDescription(usersEntity.getUserDescription());
                userRoleForm.setUserId(userRoleForm.getUserId());
                userRoleForm.setEditUserId(userRoleForm.getUserId());
                List<RoleEntity> roleEntityList = usersEntity.getUserRole();
                List<Integer> roleIdss = new ArrayList<>();
                if (roleEntityList != null) {
                    for (RoleEntity roleEntity : roleEntityList) {
                        roleIdss.add(roleEntity.getId());
                    }
                }
                userRoleForm.setEditSelectedForCreate(roleIdss);
                userRoleForm.setShowSelectedForCreate(userRoleForm.getEditSelectedForCreate());
                userRoleForm.setEditInstitutionId(usersEntity.getId());
                userRoleForm.setEditEmailId(usersEntity.getEmailId());
            } else {
                userRoleForm.setShowEditError(true);
                userRoleForm.setEditErromessage(networkLoginId + RecapConstants.ALREADY_EXISTS);
                userRoleForm.setEditNetworkLoginId(networkLoginId);
                userRoleForm.setEditUserDescription(userDescription);
                userRoleForm.setEditSelectedForCreate(roleIds);
                userRoleForm.setShowSelectedForCreate(userRoleForm.getEditSelectedForCreate());
                userRoleForm.setEditInstitutionId(institutionId);
                userRoleForm.setEditEmailId(userEmailId);
            }
            userRoleForm.setShowUserSearchView(false);
            return new ModelAndView(RecapConstants.USER_ROLES_SEARCH, RecapConstants.USER_ROLE_FORM, userRoleForm);
        } else {
            return new ModelAndView(RecapConstants.VIEW_LOGIN);
        }
    }

    /**
     * Get back to the user search UI page from edit, delete and create user UI pages.
     *
     * @param userRoleForm the user role form
     * @param request      the request
     * @return the model and view
     */
    @PostMapping("/userRoles")
    public ModelAndView goBack(@ModelAttribute("userRoleForm") UserRoleForm userRoleForm, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean authenticated = getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL);
        if (authenticated) {
            logger.info(RecapConstants.USERS_TAB_CLICKED);
            UserDetailsForm userDetailsForm = getUserAuthUtil().getUserDetails(session, RecapConstants.BARCODE_RESTRICTED_PRIVILEGE);
            setUserRoleForm(userRoleForm, userDetailsForm);
        }
        userRoleForm.setShowUserSearchView(true);
        return new ModelAndView("userRolesSearch", "userRoleForm", userRoleForm);
    }

    private void priorSearch(UserRoleForm userRoleForm, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute(RecapConstants.USER_ID);
        UserDetailsForm userDetailsForm = getUserAuthUtil().getUserDetails(session, RecapConstants.BARCODE_RESTRICTED_PRIVILEGE);
        List<Object> institutions = getUserRoleService().getInstitutions(userDetailsForm.isSuperAdmin(), userDetailsForm.getLoginInstitutionId());
        List<Object> roles = getUserRoleService().getRoles(getUserManagementService().getSuperAdminRoleId(), userDetailsForm.isSuperAdmin());
        userRoleForm.setUserId(userId);
        userRoleForm.setInstitutionId(userDetailsForm.getLoginInstitutionId());
        userRoleForm.setRoles(roles);
        userRoleForm.setInstitutions(institutions);
        userRoleForm.setAllowCreateEdit(true);
        userRoleForm.setSubmitted(true);

        searchAndSetResult(userRoleForm, userDetailsForm.isSuperAdmin(), userId, request);
    }

    private void searchAndSetResult(UserRoleForm userRoleForm, boolean superAdmin, Integer userId, HttpServletRequest request) {
        if (StringUtils.isBlank(userRoleForm.getSearchNetworkId()) && StringUtils.isBlank(userRoleForm.getUserEmailId())) {
            logger.debug("Search All Users");
            Page<UsersEntity> usersEntities = getUserRoleService().searchUsers(userRoleForm, superAdmin);
            setUserRoleFormValues(userRoleForm, usersEntities, userId, request);
        } else if (StringUtils.isNotBlank(userRoleForm.getSearchNetworkId()) && StringUtils.isBlank(userRoleForm.getUserEmailId())) {
            logger.debug("Search Users By NetworkId :" + userRoleForm.getSearchNetworkId());
            Page<UsersEntity> usersEntities = getUserRoleService().searchByNetworkId(userRoleForm, superAdmin);
            getUsersInformation(userRoleForm, superAdmin, userId, usersEntities, RecapConstants.NETWORK_LOGIN_ID_DOES_NOT_EXIST, request);
        } else if (StringUtils.isBlank(userRoleForm.getSearchNetworkId()) && StringUtils.isNotBlank(userRoleForm.getUserEmailId())) {
            logger.debug("Search Users by Email Id:" + userRoleForm.getUserEmailId());
            Page<UsersEntity> usersEntities = getUserRoleService().searchByUserEmailId(userRoleForm, superAdmin);
            getUsersInformation(userRoleForm, superAdmin, userId, usersEntities, RecapConstants.EMAILID_ID_DOES_NOT_EXIST, request);
        } else if (StringUtils.isNotBlank(userRoleForm.getSearchNetworkId()) && StringUtils.isNotBlank(userRoleForm.getUserEmailId())) {
            logger.debug("Search Users by Network Id : " + userRoleForm.getSearchNetworkId() + " and Email Id : " + userRoleForm.getUserEmailId());
            Page<UsersEntity> usersEntities = getUserRoleService().searchByNetworkIdAndUserEmailId(userRoleForm, superAdmin);
            getUsersInformation(userRoleForm, superAdmin, userId, usersEntities, RecapConstants.NETWORK_LOGIN_ID_AND_EMAILID_ID_DOES_NOT_EXIST, request);
        } else {
            userRoleForm.setShowResults(false);
        }
    }

    private void getUsersInformation(UserRoleForm userRoleForm, boolean superAdmin, Integer userId, Page<UsersEntity> usersEntities, String message, HttpServletRequest request) {
        List<UsersEntity> userEntity = usersEntities.getContent();
        if (!userEntity.isEmpty()) {
            setUserRoleFormValues(userRoleForm, usersEntities, userId, request);
        } else {
            userRoleForm.setMessage(message);
            userRoleForm.setShowErrorMessage(true);
            userRoleForm.setShowResults(false);
        }
    }

    private List<UserRoleForm> setFormValues(List<UsersEntity> usersEntities, Integer userId, HttpServletRequest request) {
        List<UserRoleForm> userRoleFormList = new ArrayList<>();
        appendValues(usersEntities, userRoleFormList, userId, request);
        return userRoleFormList;
    }

    private void appendValues(Collection<UsersEntity> usersEntities, List<UserRoleForm> userRoleFormList, Integer userId, HttpServletRequest request) {
        for (UsersEntity usersEntity : usersEntities) {
            InstitutionEntity institutionEntity = usersEntity.getInstitutionEntity();
            List<RoleEntity> userRole = usersEntity.getUserRole();
            boolean addUsers = true;
            HttpSession session = request.getSession(false);
            Object isSuperAdmin = session.getAttribute(RecapConstants.SUPER_ADMIN_USER);
            String userName = (String) session.getAttribute(RecapConstants.USER_NAME);
            if (!(boolean) isSuperAdmin) {
                for (RoleEntity superAdminCheck : userRole) {
                    if (superAdminCheck.getRoleName().equals(RecapConstants.ROLES_SUPER_ADMIN)) {
                        addUsers = false;
                    }
                }
            }
            if (addUsers) {
                UserRoleForm userRoleDetailsForm = new UserRoleForm();
                StringBuilder rolesBuffer = new StringBuilder();
                userRoleDetailsForm.setUserId(usersEntity.getId());
                userRoleDetailsForm.setInstitutionId(institutionEntity.getId());
                userRoleDetailsForm.setInstitutionName(institutionEntity.getInstitutionName());
                userRoleDetailsForm.setNetworkLoginId(usersEntity.getLoginId());
                userRoleDetailsForm.setUserDescription(usersEntity.getUserDescription());
                for (RoleEntity roleEntity : usersEntity.getUserRole()) {
                    rolesBuffer.append(roleEntity.getRoleName() + ",");
                }
                userRoleDetailsForm.setRoleName(roles(rolesBuffer.toString(), ","));
                userRoleDetailsForm.setShowEditDeleteIcon(userRoleDetailsForm.getUserId() != userId);
                userRoleFormList.add(userRoleDetailsForm);//Added all user's details
            }
        }
    }

    private ModelAndView getPaginationModelAndView(@ModelAttribute("userForm") UserRoleForm userRoleForm, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean authenticated = getUserAuthUtil().isAuthenticated(session, RecapConstants.SCSB_SHIRO_USER_ROLE_URL);
        if (authenticated) {
            priorSearch(userRoleForm, request);
            model.addAttribute(RecapCommonConstants.TEMPLATE, RecapConstants.USER_ROLES_SEARCH);
            return new ModelAndView(RecapConstants.VIEW_REQUEST_RESULT_TABLE, RecapConstants.USER_ROLE_FORM, userRoleForm);
        } else {
            return new ModelAndView(RecapConstants.VIEW_LOGIN);
        }
    }

    private String roles(String rolesBuffer, String seperator) {
        if (rolesBuffer != null && rolesBuffer.endsWith(seperator)) {
            return rolesBuffer.substring(0, rolesBuffer.length() - 1);
        }
        return null;
    }

    private void setUserRoleForm(UserRoleForm userRoleForm, UserDetailsForm userDetailsForm) {
        List<Object> roles = getUserRoleService().getRoles(getUserManagementService().getSuperAdminRoleId(), userDetailsForm.isSuperAdmin());
        List<Object> institutions = getUserRoleService().getInstitutions(userDetailsForm.isSuperAdmin(), userDetailsForm.getLoginInstitutionId());
        userRoleForm.setRoles(roles);
        userRoleForm.setInstitutions(institutions);
    }

    private void setUserRoleFormValues(UserRoleForm userRoleForm, Page<UsersEntity> usersEntities, Integer userId, HttpServletRequest request) {
        userRoleForm.setUserRoleFormList(setFormValues(usersEntities.getContent(), userId, request));
        userRoleForm.setShowResults(true);
        userRoleForm.setTotalRecordsCount(String.valueOf(usersEntities.getTotalElements()));
        userRoleForm.setTotalPageCount(usersEntities.getTotalPages());
    }

    private void setUserRoleFormRoleId(UserRoleForm userRoleForm, Optional<UsersEntity> usersEntity) {
        if (usersEntity.isPresent()) {
            List<RoleEntity> roleEntityList = usersEntity.get().getUserRole();
            List<Integer> roleIds = new ArrayList<>();
            if (roleEntityList != null) {
                for (RoleEntity roleEntity : roleEntityList) {
                    roleIds.add(roleEntity.getId());
                }
            }
            userRoleForm.setEditSelectedForCreate(roleIds);
        }
    }
}
