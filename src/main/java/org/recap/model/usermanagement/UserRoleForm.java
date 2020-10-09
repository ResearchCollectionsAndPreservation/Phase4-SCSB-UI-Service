package org.recap.model.usermanagement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dharmendrag on 23/12/16.
 */
public class UserRoleForm {

    private int userId;
    private int institutionId;
    private int editUserId;
    private int[] roleId;
    private int[] editRoleId;

    private Integer pageNumber = 0;
    private Integer pageSize = 10;
    private Integer totalPageCount = 0;
    private Integer editInstitutionId;
    private Integer afterDelPageNumber=0;
    private Integer afterDelPageSize =10;
    private Integer afterDelTotalPageCount=0;

    private String searchNetworkId;
    private String networkLoginId;
    private String roleName;
    private String totalRecordsCount = "0";
    private String institutionName;
    private String message;
    private String errorMessage;
    private String errorMessageForEmail;
    private String editErromessage;
    private String sectionName;
    private String buttonName;
    private String editNetworkId;
    private String userDescriptionErrMsg;
    private String userDescription;
    private String editNetworkLoginId;
    private String editUserDescription;
    private String userEmailId;
    private String emailId;
    private String editEmailId;

    private boolean allowCreateEdit;
    private boolean isCreatedRequest;
    private boolean showPagination = false;
    private boolean showSearch = false;
    private boolean showErrorMessage = false;
    private boolean showCreateSuccess = false;
    private boolean showCreateError = false;
    private boolean showEditSuccess = false;
    private boolean showEditError = false;
    private boolean showCreateEmailError = false;
    private boolean deleteSuccessMsg = false;
    private boolean selected;
    private boolean submitted;
    private boolean showResults = false;
    private boolean deletedSuccessMsg = false;
    private boolean deleteErrorMsg= false;
    private boolean showUserSearchView = true;

    private List<Object> roles = new ArrayList<>();
    private List<Object> institutions = new ArrayList<>();
    private List<Integer> showSelectedForCreate = new ArrayList<>();
    private List<Integer> selectedForCreate = new ArrayList<>();
    private List<Integer> editSelectedForCreate = new ArrayList<>();
    private List<UserRoleForm> userRoleFormList = new ArrayList<>();

    private boolean showEditDeleteIcon=true;

    private String createdBy;
    private String lastUpdatedBy;

    /**
     * Gets show create error.
     *
     * @return the boolean
     */
    public boolean isShowCreateError() {
        return showCreateError;
    }

    /**
     * Sets show create error.
     *
     * @param showCreateError the show create error
     */
    public void setShowCreateError(boolean showCreateError) {
        this.showCreateError = showCreateError;
    }

    /**
     * Gets show create success.
     *
     * @return the boolean
     */
    public boolean isShowCreateSuccess() {
        return showCreateSuccess;
    }

    /**
     * Sets show create success.
     *
     * @param showCreateSuccess the show create success
     */
    public void setShowCreateSuccess(boolean showCreateSuccess) {
        this.showCreateSuccess = showCreateSuccess;
    }

    /**
     * Gets show error message.
     *
     * @return the boolean
     */
    public boolean isShowErrorMessage() {
        return showErrorMessage;
    }

    /**
     * Sets show error message.
     *
     * @param showErrorMessage the show error message
     */
    public void setShowErrorMessage(boolean showErrorMessage) {
        this.showErrorMessage = showErrorMessage;
    }

    /**
     * Gets created request.
     *
     * @return the boolean
     */
    public boolean isCreatedRequest() {
        return isCreatedRequest;
    }

    /**
     * Sets created request.
     *
     * @param createdRequest the created request
     */
    public void setCreatedRequest(boolean createdRequest) {
        isCreatedRequest = createdRequest;
    }

    /**
     * Gets search network id.
     *
     * @return the search network id
     */
    public String getSearchNetworkId() {
        return searchNetworkId;
    }

    /**
     * Sets search network id.
     *
     * @param searchNetworkId the search network id
     */
    public void setSearchNetworkId(String searchNetworkId) {
        this.searchNetworkId = searchNetworkId;
    }

    /**
     * Gets edit network id.
     *
     * @return the edit network id
     */
    public String getEditNetworkId() {
        return editNetworkId;
    }

    /**
     * Sets edit network id.
     *
     * @param editNetworkId the edit network id
     */
    public void setEditNetworkId(String editNetworkId) {
        this.editNetworkId = editNetworkId;
    }

    /**
     * Get edit role id int [ ].
     *
     * @return the int [ ]
     */
    public int[] getEditRoleId() {
        return editRoleId;
    }

    /**
     * Sets edit role id.
     *
     * @param editRoleId the edit role id
     */
    public void setEditRoleId(int[] editRoleId) {
        this.editRoleId = editRoleId;
    }

    /**
     * Gets roles.
     *
     * @return the roles
     */
    public List<Object> getRoles() {
        return roles;
    }

    /**
     * Sets roles.
     *
     * @param roles the roles
     */
    public void setRoles(List<Object> roles) {
        this.roles = roles;
    }

    /**
     * Gets institutions.
     *
     * @return the institutions
     */
    public List<Object> getInstitutions() {
        return institutions;
    }

    /**
     * Sets institutions.
     *
     * @param institutions the institutions
     */
    public void setInstitutions(List<Object> institutions) {
        this.institutions = institutions;
    }

    /**
     * Gets section name.
     *
     * @return the section name
     */
    public String getSectionName() {
        return sectionName;
    }

    /**
     * Sets section name.
     *
     * @param sectionName the section name
     */
    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    /**
     * Gets button name.
     *
     * @return the button name
     */
    public String getButtonName() {
        return buttonName;
    }

    /**
     * Sets button name.
     *
     * @param buttonName the button name
     */
    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    /**
     * Gets allow create edit.
     *
     * @return the boolean
     */
    public boolean isAllowCreateEdit() {
        return allowCreateEdit;
    }

    /**
     * Sets allow create edit.
     *
     * @param allowCreateEdit the allow create edit
     */
    public void setAllowCreateEdit(boolean allowCreateEdit) {
        this.allowCreateEdit = allowCreateEdit;
    }

    /**
     * Gets selected.
     *
     * @return the boolean
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets selected.
     *
     * @param selected the selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Gets submitted.
     *
     * @return the boolean
     */
    public boolean isSubmitted() {
        return submitted;
    }

    /**
     * Sets submitted.
     *
     * @param submitted the submitted
     */
    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    /**
     * Gets show results.
     *
     * @return the boolean
     */
    public boolean isShowResults() {
        return showResults;
    }

    /**
     * Sets show results.
     *
     * @param showResults the show results
     */
    public void setShowResults(boolean showResults) {
        this.showResults = showResults;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets institution name.
     *
     * @return the institution name
     */
    public String getInstitutionName() {
        return institutionName;
    }

    /**
     * Sets institution name.
     *
     * @param institutionName the institution name
     */
    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    /**
     * Gets user role form list.
     *
     * @return the user role form list
     */
    public List<UserRoleForm> getUserRoleFormList() {
        return userRoleFormList;
    }

    /**
     * Sets user role form list.
     *
     * @param userRoleFormList the user role form list
     */
    public void setUserRoleFormList(List<UserRoleForm> userRoleFormList) {
        this.userRoleFormList = userRoleFormList;
    }

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets user id.
     *
     * @param userId the user id
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets edit user id.
     *
     * @return the edit user id
     */
    public int getEditUserId() {
        return editUserId;
    }

    /**
     * Sets edit user id.
     *
     * @param editUserId the edit user id
     */
    public void setEditUserId(int editUserId) {
        this.editUserId = editUserId;
    }

    /**
     * Gets network login id.
     *
     * @return the network login id
     */
    public String getNetworkLoginId() {
        return networkLoginId;
    }

    /**
     * Sets network login id.
     *
     * @param networkLoginId the network login id
     */
    public void setNetworkLoginId(String networkLoginId) {
        this.networkLoginId = networkLoginId;
    }

    /**
     * Gets institution id.
     *
     * @return the institution id
     */
    public int getInstitutionId() {
        return institutionId;
    }

    /**
     * Sets institution id.
     *
     * @param institutionId the institution id
     */
    public void setInstitutionId(int institutionId) {
        this.institutionId = institutionId;
    }

    /**
     * Get role id int [ ].
     *
     * @return the int [ ]
     */
    public int[] getRoleId() {
        return roleId;
    }

    /**
     * Sets role id.
     *
     * @param roleId the role id
     */
    public void setRoleId(int[] roleId) {
        this.roleId = roleId;
    }

    /**
     * Gets role name.
     *
     * @return the role name
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * Sets role name.
     *
     * @param roleName the role name
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * Gets total records count.
     *
     * @return the total records count
     */
    public String getTotalRecordsCount() {
        return totalRecordsCount;
    }

    /**
     * Sets total records count.
     *
     * @param totalRecordsCount the total records count
     */
    public void setTotalRecordsCount(String totalRecordsCount) {
        this.totalRecordsCount = totalRecordsCount;
    }

    /**
     * Gets page number.
     *
     * @return the page number
     */
    public Integer getPageNumber() {
        return pageNumber;
    }

    /**
     * Sets page number.
     *
     * @param pageNumber the page number
     */
    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Gets page size.
     *
     * @return the page size
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * Sets page size.
     *
     * @param pageSize the page size
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets total page count.
     *
     * @return the total page count
     */
    public Integer getTotalPageCount() {
        return totalPageCount;
    }

    /**
     * Sets total page count.
     *
     * @param totalPageCount the total page count
     */
    public void setTotalPageCount(Integer totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    /**
     * Reset page number.
     */
    public void resetPageNumber() {
        this.pageNumber = 0;
    }

    /**
     * Sets error message.
     *
     * @param errorMessage the error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Gets error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets user description.
     *
     * @return the user description
     */
    public String getUserDescription() {
        return userDescription;
    }

    /**
     * Sets user description.
     *
     * @param userDescription the user description
     */
    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    /**
     * Gets user description err msg.
     *
     * @return the user description err msg
     */
    public String getUserDescriptionErrMsg() {
        return userDescriptionErrMsg;
    }

    /**
     * Sets user description err msg.
     *
     * @param userDescriptionErrMsg the user description err msg
     */
    public void setUserDescriptionErrMsg(String userDescriptionErrMsg) {
        this.userDescriptionErrMsg = userDescriptionErrMsg;
    }

    /**
     * Is show pagination boolean.
     *
     * @return the boolean
     */
    public boolean isShowPagination() {
        return showPagination;
    }

    /**
     * Sets show pagination.
     *
     * @param showPagination the show pagination
     */
    public void setShowPagination(boolean showPagination) {
        this.showPagination = showPagination;
    }

    /**
     * Is show search boolean.
     *
     * @return the boolean
     */
    public boolean isShowSearch() {
        return showSearch;
    }

    /**
     * Sets show search.
     *
     * @param showSearch the show search
     */
    public void setShowSearch(boolean showSearch) {
        this.showSearch = showSearch;
    }

    /**
     * Gets show selected for create.
     *
     * @return the show selected for create
     */
    public List<Integer> getShowSelectedForCreate() {
        return showSelectedForCreate;
    }

    /**
     * Sets show selected for create.
     *
     * @param showSelectedForCreate the show selected for create
     */
    public void setShowSelectedForCreate(List<Integer> showSelectedForCreate) {
        this.showSelectedForCreate = showSelectedForCreate;
    }

    /**
     * Gets selected for create.
     *
     * @return the selected for create
     */
    public List<Integer> getSelectedForCreate() {
        return selectedForCreate;
    }

    /**
     * Sets selected for create.
     *
     * @param selectedForCreate the selected for create
     */
    public void setSelectedForCreate(List<Integer> selectedForCreate) {
        this.selectedForCreate = selectedForCreate;
    }

    /**
     * Is show edit success boolean.
     *
     * @return the boolean
     */
    public boolean isShowEditSuccess() {
        return showEditSuccess;
    }

    /**
     * Sets show edit success.
     *
     * @param showEditSuccess the show edit success
     */
    public void setShowEditSuccess(boolean showEditSuccess) {
        this.showEditSuccess = showEditSuccess;
    }

    /**
     * Is show edit error boolean.
     *
     * @return the boolean
     */
    public boolean isShowEditError() {
        return showEditError;
    }

    /**
     * Sets show edit error.
     *
     * @param showEditError the show edit error
     */
    public void setShowEditError(boolean showEditError) {
        this.showEditError = showEditError;
    }

    /**
     * Gets edit network login id.
     *
     * @return the edit network login id
     */
    public String getEditNetworkLoginId() {
        return editNetworkLoginId;
    }

    /**
     * Sets edit network login id.
     *
     * @param editNetworkLoginId the edit network login id
     */
    public void setEditNetworkLoginId(String editNetworkLoginId) {
        this.editNetworkLoginId = editNetworkLoginId;
    }

    /**
     * Sets edit institution id.
     *
     * @param editInstitutionId the edit institution id
     */
    public void setEditInstitutionId(Integer editInstitutionId) {
        this.editInstitutionId = editInstitutionId;
    }

    /**
     * Gets edit selected for create.
     *
     * @return the edit selected for create
     */
    public List<Integer> getEditSelectedForCreate() {
        return editSelectedForCreate;
    }

    /**
     * Sets edit selected for create.
     *
     * @param editSelectedForCreate the edit selected for create
     */
    public void setEditSelectedForCreate(List<Integer> editSelectedForCreate) {
        this.editSelectedForCreate = editSelectedForCreate;
    }

    /**
     * Gets edit user description.
     *
     * @return the edit user description
     */
    public String getEditUserDescription() {
        return editUserDescription;
    }

    /**
     * Sets edit user description.
     *
     * @param editUserDescription the edit user description
     */
    public void setEditUserDescription(String editUserDescription) {
        this.editUserDescription = editUserDescription;
    }

    /**
     * Gets edit institution id.
     *
     * @return the edit institution id
     */
    public Integer getEditInstitutionId() {
        return editInstitutionId;
    }

    /**
     * Gets user email id.
     *
     * @return the user email id
     */
    public String getUserEmailId() {
        return userEmailId;
    }

    /**
     * Sets user email id.
     *
     * @param userEmailId the user email id
     */
    public void setUserEmailId(String userEmailId) {
        this.userEmailId = userEmailId;
    }

    /**
     * Gets email id.
     *
     * @return the email id
     */
    public String getEmailId() {
        return emailId;
    }

    /**
     * Sets email id.
     *
     * @param emailId the email id
     */
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    /**
     * Gets edit email id.
     *
     * @return the edit email id
     */
    public String getEditEmailId() {
        return editEmailId;
    }

    /**
     * Sets edit email id.
     *
     * @param editEmailId the edit email id
     */
    public void setEditEmailId(String editEmailId) {
        this.editEmailId = editEmailId;
    }

    /**
     * Is delete success msg boolean.
     *
     * @return the boolean
     */
    public boolean isDeleteSuccessMsg() {
        return deleteSuccessMsg;
    }

    /**
     * Sets delete success msg.
     *
     * @param deleteSuccessMsg the delete success msg
     */
    public void setDeleteSuccessMsg(boolean deleteSuccessMsg) {
        this.deleteSuccessMsg = deleteSuccessMsg;
    }

    /**
     * Is deleted success msg boolean.
     *
     * @return the boolean
     */
    public boolean isDeletedSuccessMsg() {
        return deletedSuccessMsg;
    }

    /**
     * Sets deleted success msg.
     *
     * @param deletedSuccessMsg the deleted success msg
     */
    public void setDeletedSuccessMsg(boolean deletedSuccessMsg) {
        this.deletedSuccessMsg = deletedSuccessMsg;
    }

    /**
     * Is delete error msg boolean.
     *
     * @return the boolean
     */
    public boolean isDeleteErrorMsg() {
        return deleteErrorMsg;
    }

    /**
     * Sets delete error msg.
     *
     * @param deleteErrorMsg the delete error msg
     */
    public void setDeleteErrorMsg(boolean deleteErrorMsg) {
        this.deleteErrorMsg = deleteErrorMsg;
    }

    /**
     * Gets after del page number.
     *
     * @return the after del page number
     */
    public Integer getAfterDelPageNumber() {
        return afterDelPageNumber;
    }

    /**
     * Sets after del page number.
     *
     * @param afterDelPageNumber the after del page number
     */
    public void setAfterDelPageNumber(Integer afterDelPageNumber) {
        this.afterDelPageNumber = afterDelPageNumber;
    }

    /**
     * Gets after del page size.
     *
     * @return the after del page size
     */
    public Integer getAfterDelPageSize() {
        return afterDelPageSize;
    }

    /**
     * Sets after del page size.
     *
     * @param afterDelPageSize the after del page size
     */
    public void setAfterDelPageSize(Integer afterDelPageSize) {
        this.afterDelPageSize = afterDelPageSize;
    }

    /**
     * Gets after del total page count.
     *
     * @return the after del total page count
     */
    public Integer getAfterDelTotalPageCount() {
        return afterDelTotalPageCount;
    }

    /**
     * Sets after del total page count.
     *
     * @param afterDelTotalPageCount the after del total page count
     */
    public void setAfterDelTotalPageCount(Integer afterDelTotalPageCount) {
        this.afterDelTotalPageCount = afterDelTotalPageCount;
    }

    /**
     * Gets edit erromessage.
     *
     * @return the edit erromessage
     */
    public String getEditErromessage() {
        return editErromessage;
    }

    /**
     * Sets edit erromessage.
     *
     * @param editErromessage the edit erromessage
     */
    public void setEditErromessage(String editErromessage) {
        this.editErromessage = editErromessage;
    }

    /**
     * Gets error message for email.
     *
     * @return the error message for email
     */
    public String getErrorMessageForEmail() {
        return errorMessageForEmail;
    }

    /**
     * Sets error message for email.
     *
     * @param errorMessageForEmail the error message for email
     */
    public void setErrorMessageForEmail(String errorMessageForEmail) {
        this.errorMessageForEmail = errorMessageForEmail;
    }

    /**
     * Is show create email error boolean.
     *
     * @return the boolean
     */
    public boolean isShowCreateEmailError() {
        return showCreateEmailError;
    }

    /**
     * Sets show create email error.
     *
     * @param showCreateEmailError the show create email error
     */
    public void setShowCreateEmailError(boolean showCreateEmailError) {
        this.showCreateEmailError = showCreateEmailError;
    }

    /**
     * Gets created by.
     *
     * @return the created by
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets created by.
     *
     * @param createdBy the created by
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets last updated by.
     *
     * @return the last updated by
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Sets last updated by.
     *
     * @param lastUpdatedBy the last updated by
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * Is show user search view boolean.
     *
     * @return the boolean
     */
    public boolean isShowUserSearchView() {
        return showUserSearchView;
    }

    /**
     * Sets show user search view.
     *
     * @param showUserSearchView the show user search view
     */
    public void setShowUserSearchView(boolean showUserSearchView) {
        this.showUserSearchView = showUserSearchView;
    }

    /**
     * Is show edit delete icon boolean.
     *
     * @return the boolean
     */
    public boolean isShowEditDeleteIcon() {
        return showEditDeleteIcon;
    }

    /**
     * Sets show edit delete icon.
     *
     * @param showEditDeleteIcon the show edit delete icon
     */
    public void setShowEditDeleteIcon(boolean showEditDeleteIcon) {
        this.showEditDeleteIcon = showEditDeleteIcon;
    }
}