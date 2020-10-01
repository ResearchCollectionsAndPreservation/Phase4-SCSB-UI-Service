package org.recap.model.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemalathas on 22/12/16.
 */
public class RolesForm {
    private String roleName;
    private String roleDescription;
    private String permissionNames;
    private boolean showResults = false;
    private boolean newRole = false;
    private String totalRecordCount="0";
    private String errorMessage;
    private Integer pageNumber = 0;
    private Integer pageSize = 10;
    private Integer totalPageCount = 0;
    private Integer afterDelPageNumber=0;
    private Integer afterDelPageSize =10;
    private Integer afterDelTotalPageCount=0;
    private String message;
    private String newRoleName;
    private String newRoleDescription;
    private String newPermissionNames;
    private String editRoleName;
    private String editRoleDescription;
    private String editPermissionNames;
    private List<String> editPermissionName = new ArrayList<>();
    private String roleNameForDelete;
    private String roleDescriptionForDelete;
    private String permissionNamesForDelete;
    private List<String> permissionNameList = new ArrayList<>();
    private List<String> selectedPermissionNames = new ArrayList<>();
    private Integer roleId;
    private List<RolesSearchResult> rolesSearchResults = new ArrayList<>();
    private boolean showIntial = true;

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
     * Gets role description.
     *
     * @return the role description
     */
    public String getRoleDescription() {
        return roleDescription;
    }

    /**
     * Sets role description.
     *
     * @param roleDescription the role description
     */
    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    /**
     * Gets permission names.
     *
     * @return the permission names
     */
    public String getPermissionNames() {
        return permissionNames;
    }

    /**
     * Sets permission names.
     *
     * @param permissionNames the permission names
     */
    public void setPermissionNames(String permissionNames) {
        this.permissionNames = permissionNames;
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
     * Gets total record count.
     *
     * @return the total record count
     */
    public String getTotalRecordCount() {
        return totalRecordCount;
    }

    /**
     * Sets total record count.
     *
     * @param totalRecordCount the total record count
     */
    public void setTotalRecordCount(String totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
    }

    /**
     * Gets roles search results.
     *
     * @return the roles search results
     */
    public List<RolesSearchResult> getRolesSearchResults() {
        return rolesSearchResults;
    }

    /**
     * Sets roles search results.
     *
     * @param rolesSearchResults the roles search results
     */
    public void setRolesSearchResults(List<RolesSearchResult> rolesSearchResults) {
        this.rolesSearchResults = rolesSearchResults;
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
     * Sets error message.
     *
     * @param errorMessage the error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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
     * Gets new role.
     *
     * @return the boolean
     */
    public boolean isNewRole() {
        return newRole;
    }

    /**
     * Sets new role.
     *
     * @param newRole the new role
     */
    public void setNewRole(boolean newRole) {
        this.newRole = newRole;
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
     * Gets new role name.
     *
     * @return the new role name
     */
    public String getNewRoleName() {
        return newRoleName;
    }

    /**
     * Sets new role name.
     *
     * @param newRoleName the new role name
     */
    public void setNewRoleName(String newRoleName) {
        this.newRoleName = newRoleName;
    }

    /**
     * Gets new role description.
     *
     * @return the new role description
     */
    public String getNewRoleDescription() {
        return newRoleDescription;
    }

    /**
     * Sets new role description.
     *
     * @param newRoleDescription the new role description
     */
    public void setNewRoleDescription(String newRoleDescription) {
        this.newRoleDescription = newRoleDescription;
    }

    /**
     * Gets new permission names.
     *
     * @return the new permission names
     */
    public String getNewPermissionNames() {
        return newPermissionNames;
    }

    /**
     * Sets new permission names.
     *
     * @param newPermissionNames the new permission names
     */
    public void setNewPermissionNames(String newPermissionNames) {
        this.newPermissionNames = newPermissionNames;
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
     * Gets permission name list.
     *
     * @return the permission name list
     */
    public List<String> getPermissionNameList() {
        return permissionNameList;
    }

    /**
     * Sets permission name list.
     *
     * @param permissionNameList the permission name list
     */
    public void setPermissionNameList(List<String> permissionNameList) {
        this.permissionNameList = permissionNameList;
    }

    /**
     * Gets selected permission names.
     *
     * @return the selected permission names
     */
    public List<String> getSelectedPermissionNames() {
        return selectedPermissionNames;
    }

    /**
     * Sets selected permission names.
     *
     * @param selectedPermissionNames the selected permission names
     */
    public void setSelectedPermissionNames(List<String> selectedPermissionNames) {
        this.selectedPermissionNames = selectedPermissionNames;
    }

    /**
     * Gets edit role name.
     *
     * @return the edit role name
     */
    public String getEditRoleName() {
        return editRoleName;
    }

    /**
     * Sets edit role name.
     *
     * @param editRoleName the edit role name
     */
    public void setEditRoleName(String editRoleName) {
        this.editRoleName = editRoleName;
    }

    /**
     * Gets edit role description.
     *
     * @return the edit role description
     */
    public String getEditRoleDescription() {
        return editRoleDescription;
    }

    /**
     * Sets edit role description.
     *
     * @param editRoleDescription the edit role description
     */
    public void setEditRoleDescription(String editRoleDescription) {
        this.editRoleDescription = editRoleDescription;
    }

    /**
     * Gets edit permission names.
     *
     * @return the edit permission names
     */
    public String getEditPermissionNames() {
        return editPermissionNames;
    }

    /**
     * Sets edit permission names.
     *
     * @param editPermissionNames the edit permission names
     */
    public void setEditPermissionNames(String editPermissionNames) {
        this.editPermissionNames = editPermissionNames;
    }

    /**
     * Gets role id.
     *
     * @return the role id
     */
    public Integer getRoleId() {
        return roleId;
    }

    /**
     * Sets role id.
     *
     * @param roleId the role id
     */
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    /**
     * Gets role name for delete.
     *
     * @return the role name for delete
     */
    public String getRoleNameForDelete() {
        return roleNameForDelete;
    }

    /**
     * Sets role name for delete.
     *
     * @param roleNameForDelete the role name for delete
     */
    public void setRoleNameForDelete(String roleNameForDelete) {
        this.roleNameForDelete = roleNameForDelete;
    }

    /**
     * Gets role description for delete.
     *
     * @return the role description for delete
     */
    public String getRoleDescriptionForDelete() {
        return roleDescriptionForDelete;
    }

    /**
     * Sets role description for delete.
     *
     * @param roleDescriptionForDelete the role description for delete
     */
    public void setRoleDescriptionForDelete(String roleDescriptionForDelete) {
        this.roleDescriptionForDelete = roleDescriptionForDelete;
    }

    /**
     * Gets permission names for delete.
     *
     * @return the permission names for delete
     */
    public String getPermissionNamesForDelete() {
        return permissionNamesForDelete;
    }

    /**
     * Sets permission names for delete.
     *
     * @param permissionNamesForDelete the permission names for delete
     */
    public void setPermissionNamesForDelete(String permissionNamesForDelete) {
        this.permissionNamesForDelete = permissionNamesForDelete;
    }

    /**
     * Gets edit permission name.
     *
     * @return the edit permission name
     */
    public List<String> getEditPermissionName() {
        return editPermissionName;
    }

    /**
     * Sets edit permission name.
     *
     * @param editPermissionName the edit permission name
     */
    public void setEditPermissionName(List<String> editPermissionName) {
        this.editPermissionName = editPermissionName;
    }

    /**
     * Reset.
     */
    public void reset(){
        this.errorMessage=null;
        this.message=null;
        this.totalRecordCount=String.valueOf(0);
        this.rolesSearchResults=new ArrayList<>();
        this.totalPageCount=0;

    }

    /**
     * Reset page number.
     */
    public void resetPageNumber() {
        this.pageNumber = 0;
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
     * Gets show intial.
     *
     * @return the boolean
     */
    public boolean isShowIntial() {
        return showIntial;
    }

    /**
     * Sets show intial.
     *
     * @param showIntial the show intial
     */
    public void setShowIntial(boolean showIntial) {
        this.showIntial = showIntial;
    }

}
