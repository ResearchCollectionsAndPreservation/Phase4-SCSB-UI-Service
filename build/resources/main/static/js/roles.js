/**
 * Created by hemalathas on 22/12/16.
 */
jQuery(document).ready(function ($) {

    $('#createRoleLink').click(function(){
        populatePermissionNames();
    });

    $('#createGoBackLink,#editGoBackLink,#deleteGoBackLink').click(function(){
        var $form = $('#roles-form');
        var url = $form.attr('action') + "?action=goBack";
        $.ajax({
            url: url,
            type: 'post',
            data: $form.serialize(),
            success: function (response) {
                $('#rolesContentId').html(response);
                $("#recaprole .create-role-section").hide();
                $("#recaprole .roles-main-section").show();
                $("#recaprole .delete-role-section").hide();
                $("#recaprole .edit-role-section").hide();
            }
        });

    });

    $("#recaprole .backtext a").click(function () {
        $("#recaprole .create-role-section").hide();
        $("#recaprole .roles-main-section").show();
        $("#recaprole .delete-role-section").hide();
        $("#recaprole .edit-role-section").hide();
        $('#errorMessageId').hide();
        $("#recaprole .errorMessage").hide();
    });

    $('#permissionNameId').multiselect();
    $('#deletePermissionNameId').multiselect();
    $('#editPermissionNameId').multiselect();

    $('#roleNameErrorMessage').hide();
    $('#roleDescriptionErrorMessage').hide();
    $('#permissionNamesErrorMessage').hide();
    $('#editRoleNameErrorMessage').hide();
    $('#editRoleDescriptionErrorMessage').hide();
    $('#editPermissionNamesErrorMessage').hide();

    $("a[href='https://htcrecap.atlassian.net/wiki/display/RTG/Search']").attr('href',
        'https://htcrecap.atlassian.net/wiki/pages/viewpage.action?pageId=25665733');

    $(".search-role-section input").keypress(function (e) {
        if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
            $("#searchRoles").click();
            return false;
        } else {
            return true;
        }
    });

});

function loadCreateRole() {
    var $form = $('#roles-form');
    var url = $form.attr('action') + "?action=loadCreateRole";
    $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            $('#rolesContentId').html(response);
            $("#recaprole .create-role-section").show();
            $("#recaprole .roles-main-section").hide();
            $("#recaprole .delete-role-section").hide();
            $("#recaprole .edit-role-section").hide();
        }
    });
}

function populatePermissionNames(){
    var $form = $('#roles-form');
    var url = $form.attr('action') + "?action=populatePermissionName";
    $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            $('#rolesContentId').html(response);
            $("#recaprole .create-role-section").show();
            $("#recaprole .roles-main-section").hide();
            $("#recaprole .delete-role-section").hide();
            $("#recaprole .edit-role-section").hide();
        }
    });

}

function createResetDefault() {
    var $form = $('#roles-form');
    var url = $form.attr('action') + "?action=clearPage";
    $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            $('#rolesContentId').html(response);
            $("#recaprole .create-role-section").show();
            $('#roleNameErrorMessage').hide();
            $('#roleDescriptionErrorMessage').hide();
            $('#permissionNamesErrorMessage').hide();
            $("#recaprole .edit-role-section").hide();
            $("#recaprole .roles-main-section").hide();
            $("#recaprole .delete-role-section").hide();

        }
    });
}

function createRole(){
    if(isValidRole()){
        loadCreateRole();
    }
}

function editRole(roleId,roleNames,roleDescription,permissionNames){
    var $form = $('#roles-form');
    $('#recaprole .roleDetails').val();
    var url = $form.attr('action') + "?action=editRole";
    $.ajax({
        url: url,
        type: 'get',
        data: {roleId:roleId,roleName:roleNames,roleDescription:roleDescription,permissionName:permissionNames},
        success: function (response) {
            $('#rolesContentId').html(response);
            $("#recaprole .edit-role-section").show();
            $("#recaprole .roles-main-section").hide();
            $("#recaprole .create-role-section").hide();
            $("#recaprole .delete-role-section").hide();
        }
    });
    
}

function saveEditedRole(){
    var editValidation = isValidEdit();
    if(editValidation){
        var roleId = $('#roleId').val();
        var permissionNames = $('#editPermissionNameId').val();
        var roleName = $('#editRoleName').val();
        var roleDescription = $('#editRoleDescription').val();
        var $form = $('#roles-form');
        var url = $form.attr('action') + "?action=saveEditedRole";
        $.ajax({
            url: url,
            type: 'get',
            data:{roleId:roleId,permissionNames:permissionNames,roleName:roleName,roleDescription:roleDescription},
            success: function (response) {
                $('#rolesContentId').html(response);
                $('#roleNameId').val('');
                $("#recaprole .edit-role-section").show();
                $("#recaprole .create-role-section").hide();
                $("#recaprole .roles-main-section").hide();
                $("#recaprole .delete-role-section").hide();
            }
        });
    }
}

function deleteFromDb(){
    var pageNumber = $('#afterDelPageNumber').val();
    var totalPageCount = $('#afterDelTotalPageCount').val();
    var pageSize = $('#afterDelPageSize').val();
    var $form = $('#roles-form');
    var url = $form.attr('action') + "?action=delete";
    $.ajax({
        url: url,
        type: 'get',
        data: $form.serialize(),
        success: function (response) {
            $('#rolesContentId').html(response);
            $("#recaprole .edit-role-section").hide();
            $("#recaprole .create-role-section").hide();
            $("#recaprole .roles-main-section").show();
            $("#recaprole .search-results-container").show();
            $("#recaprole .delete-role-section").hide();
        }
    });
}

function deleteRole(roleId,roleNames,roleDescription,permissionNames) {
    var pageSize = $('#numOfRecordsId').val();
    var pageNumber = $('#pageNumber').val();
    var totalPageCount = $('#totalPageCount').val();
    var $form = $('#roles-form');
    $('#recaprole .roleDetails').val();
    var url = $form.attr('action') + "?action=deleteRole";
    $.ajax({
        url: url,
        type: 'get',
        data: {roleId:roleId,roleName:roleNames,roleDescription:roleDescription,permissionName:permissionNames,
               pageSize:pageSize,pageNumber:pageNumber,totalPageCount:totalPageCount},
        success: function (response) {
            $('#rolesContentId').html(response);
            $("#recaprole .delete-role-section").show();
            $("#recaprole .create-role-section").hide();
            $("#recaprole .roles-main-section").hide();
            $("#recaprole .edit-role-section").hide();
        }
    });
    
}

function isValidRole() {
    var isValid = true;
    var roleName = $('#roleNameId').val();
    var roleDescription = $('#roleDescriptionId').val();
    var permissionName = $('#permissionNameId').val();
    var roleDescLength = roleDescription.length;
    if(roleDescLength == 45){
        $('#roleDescriptionErrorMessage').hide();
    }
    if (isBlankValue(roleName)) {
        $('#roleNameErrorMessage').show();
        isValid = false;
    } else {
        $('#roleNameErrorMessage').hide();
    }
    if (isBlankValue(roleDescription)) {
        $('#roleDescriptionErrorMessage').show();
        isValid = false;
    } else {
        $('#roleDescriptionErrorMessage').hide();
    }
    if (isBlankValue(permissionName)) {
        $('#permissionNamesErrorMessage').show();
        isValid = false;
    } else {
        $('#permissionNamesErrorMessage').hide();
    }
    return isValid;
}

function isValidEdit(){
    var isValid = true;
    var roleName = $('#editRoleName').val();
    var roleDescription = $('#editRoleDescription').val();
    var permissionName = $('#editPermissionNameId').val();
    var roleDescLength = roleDescription.length;
    if(roleDescLength == 45){
        $('#editRoleDescriptionErrorMessage').hide();
    }
    if (isBlankValue(roleName)) {
        $('#editRoleNameErrorMessage').show();
        isValid = false;
    } else {
        $('#editRoleNameErrorMessage').hide();
    }
    if (isBlankValue(roleDescription)) {
        $('#editRoleDescriptionErrorMessage').show();
        isValid = false;
    } else {
        $('#editRoleDescriptionErrorMessage').hide();
    }
    if (isBlankValue(permissionName)) {
        $('#editPermissionNamesErrorMessage').show();
        isValid = false;
    } else {
        $('#editPermissionNamesErrorMessage').hide();
    }
    return isValid;

}


function isBlankValue(value) {
    if (value == null || value == '') {
        return true;
    }
    return false;
}


function toggleRoleNameValidation() {
    var roleNameId = $('#roleNameId').val();
    if (isBlankValue(roleNameId) && !isBlankValue(roleNameId)) {
        $('#roleNameErrorMessage').show();
    } else {
        $('#roleNameErrorMessage').hide();
    }
}

function toggleRoleDescriptionValidation(val) {
    var roleDescriptionId = $('#roleDescriptionId').val();
    var roleDescLength = roleDescriptionId.length;
    var len = val.value.length;
    if (len > 45) {
        val.value = val.value.substring(0, 45);
    } else {
        $('#roleDescCharactersRemaining').text(45 - len);
    }
    if(roleDescLength == 45){
        $('#roleDescriptionErrorMessage').hide();
    }
    if (isBlankValue(roleDescriptionId) && !isBlankValue(roleDescriptionId)) {
        $('#roleDescriptionErrorMessage').show();
    } else {
        $('#roleDescriptionErrorMessage').hide();
    }
}

function togglePermissionValidation() {
    var permissionNameId = $('#permissionNameId').val();
    if (isBlankValue(permissionNameId)) {
        $('#permissionNamesErrorMessage').show();
    } else {
        $('#permissionNamesErrorMessage').hide();
    }
}

function editDescLengthValidation(val){
    $("#charactersRemainingInRoleDesc").show();
    var roleDescription = $('#editRoleDescription').val();
    var roleDescLength = roleDescription.length;
    var len = val.value.length;
    if (len > 45) {
        val.value = val.value.substring(0, 45);
    } else {
        $('#editRoleDescCharactersRemaining').text(45 - len);
    }
    if(roleDescLength == 45){
        $('#editRoleDescriptionErrorMessage').hide();
    }
}