jQuery(document).ready(function ($) {

    $("#search-result-table-users").tablesorter();

    $('#createRequest').click(function () {
        $('#networkLoginId').val('');
        $('#institutionId').val('');
        $('#roleId').multiselect('deselectAll', false);
        $('#roleId').multiselect('refresh');
        $('#userDescription').val('');
        $('#successMsg').hide();
        $('#errormsg').hide();
    });

    $('#backLink').click(function () {
        $('#successMsg').hide();
        $('#failureMsg').hide();
        $('#emailIdfailureMsg').hide();
        $('#editusers-createview').hide();
        $('#request-result-table').hide();
        $('#userRolePaginationDivId').hide();
        $('#showEntries').hide();
        $('#deleteUsers-view').hide();
        $('#numOfRecordsId').val(10);
        $('#pageNumber').val(0);
        $('#searchNetworkId').val('');
        $('#userEmailId').val('');
    });

    $('#editbackLink').click(function () {
        $('#successMsg').hide();
        $('#failureMsg').hide();
        $('#emailIdfailureMsg').hide();
        $('#editusers-createview').hide();
        $('#request-result-table').hide();
        $('#userRolePaginationDivId').hide();
        $('#showEntries').hide();
        $('#deleteUsers-view').hide();
        $('#numOfRecordsId').val(10);
        $('#pageNumber').val(0);
        $('#searchNetworkId').val('');
        $('#userEmailId').val('');
    });

    $('#deletebackLink').click(function () {
        $('#successMsg').hide();
        $('#failureMsg').hide();
        $('#emailIdfailureMsg').hide();
        $('#editusers-createview').hide();
        $('#request-result-table').hide();
        $('#userRolePaginationDivId').hide();
        $('#showEntries').hide();
        $('#deleteUsers-view').hide();
        $('#numOfRecordsId').val(10);
        $('#pageNumber').val(0);
        $('#searchNetworkId').val('');
        $('#userEmailId').val('');
    });

    /***Role Tab Form Show/Hide ***/
    $("#recaprole a.role-detailtable").click(function () {
        $("#role-searchview").hide();
        $("#role-detailtableview").show();
        $('#networkLoginIdErrMsg').hide();
        $('#institutionIdErrMsg').hide();
        $('#roleIdErrMsg').hide();
        $('#userDescriptionErrMsg').hide();
        $('#emailIdErrMsg').hide();
    });
    $("#recaprole .rolebacklink a").click(function () {
        $("#role-detailtableview").hide();
        $("#role-searchview").show();
        $('#networkLoginIdErrMsg').hide();
        $('#institutionIdErrMsg').hide();
        $('#roleIdErrMsg').hide();
        $('#userDescriptionErrMsg').hide();
        $('#emailIdErrMsg').hide();
    });
    /***Users Tab Form Show/Hide ***/
    $("#recapusers .userscreaterequestlink a").click(function () {
        $('#networkLoginId').val('');
        $('#institutionId').val('');
        $('#userDescription').val('');
        $('#roleId').multiselect('deselectAll', false);
        $('#roleId').multiselect('refresh');
        $('#emailId').val('');
        $("#users-searchview").hide();
        $("#users-createview").show();
        $('#networkLoginIdErrMsg').hide();
        $('#institutionIdErrMsg').hide();
        $('#roleIdErrMsg').hide();
        $('#userDescriptionErrMsg').hide();
        $('#emailIdErrMsg').hide();
    });
    $("#recapusers .usersbacklink a").click(function () {
        $("#users-createview").hide();
        $("#users-searchview").show();
        $('#networkLoginIdErrMsg').hide();
        $('#institutionIdErrMsg').hide();
        $('#roleIdErrMsg').hide();
        $('#userDescriptionErrMsg').hide();
    });

    resetDefaults();
    /***Request Tab Create Request Form Show/Hide ***/
    $("#userRoles .usersbacklink a").click(function () {
        $("#userRoles .users-main-section").show();
        $("#userRoles .user-create-section").hide();

    });
    $("#userRoles .user-create a").click(function () {
        $("#userRoles .users-main-section").hide();
        $("#userRoles .user-create-section").show();

    });

    $('#searchNetworkId,#userEmailId').bind('keypress', function(e) {
        if(e.keyCode==13){
            $('#searchButton').trigger('click');
            return false;
        }else {
            return true;
        }
    });

    /**Multi Select **/
    $('#roleId').multiselect();
    $('#recapusers #userstabaddrole').multiselect();
    $('#editroleId').multiselect();


    $('#backLink,#editbackLink,#deletebackLink').click(function(){
        var $form = $('#userRole-form');
        var url = $form.attr('action') + "?action=goBack";
        $.ajax({
            url: url,
            type: 'post',
            data: $form.serialize(),
            success: function (response) {
                $('#userRolesContentId').html(response);
                $('#editusers-createview').hide();
                $('#users-searchview').show();
                $('#users-createview').hide();
                $('#deleteUsers-view').hide();
            }
        });

    });

    $("a[href='https://htcrecap.atlassian.net/wiki/display/RTG/Search']").attr('href',
        'https://htcrecap.atlassian.net/wiki/pages/viewpage.action?pageId=25665733');


});
function userRolesFirstPage() {
    var $form = $('#userRole-form');
    var url = "/userRoles/first";
    $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            $('.showEntriesOption').show();
            $('#request-result-table').html(response);
            $("#search-result-table-users").tablesorter();
            $('#request-result-table').show();
            $('#userRolePaginationDivId').show();

        }
    });
}

function userRolesLastPage() {
    $('#pageNumber').val(parseInt($('#totalPageCount').val()) - 1);
    var $form = $('#userRole-form');
    var url = "/userRoles/last";
    $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            $('#request-result-table').html(response);
            $("#search-result-table-users").tablesorter();
            $('.showEntriesOption').show();
            $('#request-result-table').show();
            $('#userRolePaginationDivId').show();

        }
    });
}

function userRolesPreviousPage() {
    $('#pageNumber').val(parseInt($('#pageNumber').val()) - 1);
    var $form = $('#userRole-form');
    var url = "/userRoles/previous";
    $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            $('#request-result-table').html(response);
            $("#search-result-table-users").tablesorter();
            $('.showEntriesOption').show();
            $('#request-result-table').show();
            $('#userRolePaginationDivId').show();

        }
    });
}

function userRolesNextPage() {
    $('#pageNumber').val(parseInt($('#pageNumber').val()) + 1);
    var $form = $('#userRole-form');
    var url = "/userRoles/next";
    $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            $('#request-result-table').html(response);
            $("#search-result-table-users").tablesorter();
            $('#showEntries').show();
            $('#request-result-table').show();
            $('#userRolePaginationDivId').show();

        }
    });
}

function deleteUserRole(networkLoginId, userId) {
    var pagesize = $('#numOfRecordsId').val();
    var pageNumber = $('#pageNumber').val();
    var totalPageCount = $('#totalPageCount').val();
    var url = "/userRoles/deleteUser";
    $.ajax({
        url: url,
        type: 'get',
        data: {networkLoginId: networkLoginId, userId: userId,pagesize:pagesize,pageNumber:pageNumber,totalPageCount :totalPageCount },
        success: function (response) {
            $('#userRolesContentId').html(response);
            $('#deleteUsers-view').show();
            $('#users-searchview').hide();
            $('#users-createview').hide();
            $('#successMsg').show();
            $('#deletedRoleId').multiselect();
        }
    });
}

//Delete User Id
function deleteUser() {
    var userId = $('#editUserIdHidden').val();
    var networkLoginId = $('#deleteNetworkLoginId').val();
    var pageNumber = $('#afterDelPageNumber').val();
    var totalPageCount = $('#afterDelTotalPageCount').val();
    var pageSize = $('#afterDelPageSize').val();
    $('#userRole-form');
    var url = "/userRoles/delete";
    $.ajax({
        url: url,
        type: 'get',
        data: {userId: userId,networkLoginId:networkLoginId,pageNumber:pageNumber,totalPageCount:totalPageCount,pageSize:pageSize},
        success: function (response) {
            $('#userRolesContentId').html(response);
            $('#showEntries').show();
            $('#request-result-table').show();
            $('#userRolePaginationDivId').show();
        }
    });
}

//EditUser
function editUser(userId, networkLoginId) {
    var url = "/userRoles/editUser";
    $.ajax({
        url: url,
        type: 'get',
        data: {userId: userId, networkLoginId: networkLoginId},
        success: function (response) {
            $('#userRolesContentId').html(response);
            $('#editusers-createview').show();
            $('#users-createview').hide();
            $('#successMsg').show();
        }
    });
}

function editclear() {
    var $form = $('#userRole-form');
    var url = $form.attr('action') + "?action=editClearPage";
    $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            $('#userRolesContentId').html(response);
            $('#editusers-createview').show();
            $('#users-createview').hide();
            $('#successMsg').hide();
        }
    });
}





function editUserDetails() {
    var isValidEdit = editUserDetailsValidation();
        if(isValidEdit && validateEditEmailAddress()) {
            var userId = $('#editUserId').val();
            var networkLoginId = $('#editnetworkLoginId').val();
            var institutionId = $('#editinstitutionId').val();
            var roleIds = $('#editroleId').val();
            var userDescription = $('#edituserDescription').val();
            var userEmailId = $('#editEmailId').val();
            var url = "/userRoles/saveEditUserDetails";
            $.ajax({
                url: url,
                type: 'get',
                data: {
                    userId: userId,
                    networkLoginId: networkLoginId,
                    roleIds: roleIds,
                    userDescription: userDescription,
                    institutionId: institutionId,
                    userEmailId: userEmailId
                },
                success: function (response) {
                    $('#userRolesContentId').html(response);
                    $('#editusers-createview').show();
                    $('#users-searchview').hide();
                    $('#users-createview').hide();
                    $('#successMsg').show();
                }
            });
        }
}


function editUserDetailsValidation(){
    var isValid = true;
    var institutionId = $('#editinstitutionId').val();
    var roleId = $('#editroleId').val();
    var userDescription = $('#edituserDescription').val();
    var editDescLength = userDescription.length;
    if(editDescLength == 45){
        $('#edituserDescriptionErrMsg').hide();
    }
    if (isBlankValue(institutionId)) {
        $('#editinstitutionIdErrMsg').show();
        isValid = false;
    } else {
        $('#editinstitutionIdErrMsg').hide();
    }
    if (isBlankValue(roleId)) {
        $('#editroleIdErrMsg').show();
        isValid = false;
    } else {
        $('#editroleIdErrMsg').hide();
    }
    if (isBlankValue(userDescription)) {
        $('#edituserDescriptionErrMsg').show();
        isValid = false;
    } else {
        $('#edituserDescriptionErrMsg').hide();
    }
    return isValid;
}


function searchUserRoles() {
    var $form = $('#userRole-form');
    var url = "/userRoles/searchUsers";
    $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            $('#request-result-table').html(response);
            $("#search-result-table-users").tablesorter();
            $('#showEntries').show();
            $('#request-result-table').show();
            $('#userRolePaginationDivId').show();
        }
    });
}


function showEntriesChange() {
    $('#pageNumber').val(0);
    var $form = $('#userRole-form');
    var url = "/userRoles/searchUsers";
    $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            $('#request-result-table').html(response);
            $("#search-result-table-users").tablesorter();
            $('#request-result-table').show();
            $('#userRolePaginationDivId').show();

        }
    });
}

function submitForm() {
    var formValidation = validateForm();
    var emailAddress = validateEmailAddress();
    if (formValidation && (emailAddress)) {
        var $form = $('#userRole-form');
        var url = "/userRoles/createUser";
        $.ajax({
            url: url,
            type: 'post',
            data: $form.serialize(),
            success: function (response) {
                $('#userRolesContentId').html(response);
                $('#users-searchview').hide();
                $('#users-createview').show();
                $('#successMsg').show();
                $('#networkLoginIdErrMsg').hide();
                $('#institutionIdErrMsg').hide();
                $('#roleIdErrMsg').hide();
                $('#userDescriptionErrMsg').hide();
                $('#networkLoginId').val('');
                $('#institutionId').val('');
                $('#roleId').multiselect('deselectAll', false);
                $('#roleId').multiselect('refresh');
                $('#userDescription').val('');
                $('#emailId').val('');
            }
        });
        $('#networkLoginIdErrMsg').hide();
        $('#institutionIdErrMsg').hide();
        $('#roleIdErrMsg').hide();
        $('#userDescriptionErrMsg').hide();
    }


}
function resetDefaults() {
    $('#networkLoginIdErrMsg').hide();
    $('#institutionIdErrMsg').hide();
    $('#roleIdErrMsg').hide();
    $('#userDescriptionErrMsg').hide();
}

function validateForm() {
    var isValid = true;
    var newNetworkLoginId = $('#networkLoginId').val();
    var institutionId = $('#institutionId').val();
    var roleId = $('#roleId').val();
    var userDescription = $('#userDescription').val();
    var length = userDescription.length;
    if(length == 45){
        $('#userDescriptionErrMsg').hide();
    }
    if (isBlankValue(newNetworkLoginId)) {
        $('#networkLoginIdErrMsg').show();
        isValid = false;
    } else {
        $('#networkLoginIdErrMsg').hide();
    }
    if (isBlankValue(institutionId)) {
        $('#institutionIdErrMsg').show();
        isValid = false;
    } else {
        $('#institutionIdErrMsg').hide();
    }
    if (isBlankValue(roleId)) {
        $('#roleIdErrMsg').show();
        isValid = false;
    } else {
        $('#roleIdErrMsg').hide();
    }
    if (isBlankValue(userDescription)) {
        $('#userDescriptionErrMsg').show();
        isValid = false;
    } else {
        $('#userDescriptionErrMsg').hide();
    }
    return isValid;
}


function isBlankValue(value) {
    if (value == null || value == '') {
        return true;
    }
    return false;
}

function validateEmailAddress() {
    var isValidEmailAddress = $('#emailId').is(':valid');
    var blankEmailAddress = $('#emailId').val();
    var isValid;
    if (blankEmailAddress == null || blankEmailAddress == '' || isValidEmailAddress) {
        $('#emailIdErrMsg').hide();
        isValid = true;
    } else {
        $('#emailIdErrMsg').show();
        isValid = false;
    }
    return isValid;
}


function validateEditEmailAddress() {
    var isValidEmailAddress = $('#editEmailId').is(':valid');
    var blankEmailAddress = $('#editEmailId').val();
    var isValid;
    if (blankEmailAddress == null || blankEmailAddress == '' || isValidEmailAddress) {
        $('#editEmailIdErrMsg').hide();
        isValid = true;
    } else {
        $('#editEmailIdErrMsg').show();
        isValid = false;
    }
    return isValid;
}

function toggleUserNetworkLoginIdValidation() {
    var networkLoginId = $('#networkLoginId').val();
    if (isBlankValue(networkLoginId) && !isBlankValue(networkLoginId)) {
        $('#networkLoginIdErrMsg').show();
    } else {
        $('#networkLoginIdErrMsg').hide();
    }
}

function toggleUserDescriptionValidation(val) {
    var userDescription = $('#userDescription').val();
    var length = userDescription.length;
    var len = val.value.length;
    if (len > 45) {
        val.value = val.value.substring(0, 45);
    } else {
        $('#remainingCharacters').text(45 - len);
    }
    if(length == 45){
        $('#userDescriptionErrMsg').hide();
    }
    if (isBlankValue(userDescription) && !isBlankValue(userDescription)) {
        $('#userDescriptionErrMsg').show();
    } else {
        $('#userDescriptionErrMsg').hide();
    }
}

function toggleUserInstitutionValidation() {
    var institutionId = $('#institutionId').val();
    if (isBlankValue(institutionId)) {
        $('#institutionIdErrMsg').show();
    } else {
        $('#institutionIdErrMsg').hide();
    }
}

function toggleUserRolesValidation() {
    var roleId = $('#roleId').val();
    if (isBlankValue(roleId)) {
        $('#roleIdErrMsg').show();
    } else {
        $('#roleIdErrMsg').hide();
    }
}

function toggleUserEmailValidation() {
    var isValidEmailAddress = $('#emailId').is(':valid');
    if (isBlankValue(isValidEmailAddress)) {
        $('#emailIdErrMsg').show();
    } else {
        $('#emailIdErrMsg').hide();
    }
}

function toggleEditInstitutionIdValidation() {
    var editinstitutionId = $('#editinstitutionId').val();
    if (isBlankValue(editinstitutionId)) {
        $('#editinstitutionIdErrMsg').show();
    } else {
        $('#editinstitutionIdErrMsg').hide();
    }
}

function toggleEditRoleValidation() {
    var editroleId = $('#editroleId').val();
    if (isBlankValue(editroleId)) {
        $('#editroleIdErrMsg').show();
    } else {
        $('#editroleIdErrMsg').hide();
    }
}

function toggleEditUserDescriptionValidation(val) {
    $("#editTextCharactersRemaining").show();
    var edituserDescription = $('#edituserDescription').val();
    var editDescLength = edituserDescription.length;
    var len = val.value.length;
    if (len > 45) {
        val.value = val.value.substring(0, 45);
    } else {
        $('#editCharactersRemaining').text(45 - len);
    }
    if(editDescLength == 45){
        $('#edituserDescriptionErrMsg').hide();
    }
    if (isBlankValue(edituserDescription)) {
        $('#edituserDescriptionErrMsg').show();
    } else {
        $('#edituserDescriptionErrMsg').hide();
    }
}

function toggleEditUserEmailValidation() {
    var editEmailId = $('#editEmailId').is(':valid');
    if (isBlankValue(editEmailId)) {
        $('#editEmailIdErrMsg').show();
    } else {
        $('#editEmailIdErrMsg').hide();
    }
}

function sortHeader() {
    $("tr.childRow").removeClass('in');
    $('[name=showItemsInput]').val("false");
    $('[name=showItemsIcon]').removeClass("fa-minus-circle");
    $('[name=showItemsIcon]').addClass("fa-plus-circle");
}
