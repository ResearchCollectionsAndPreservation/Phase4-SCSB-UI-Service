/**
 * Created by akulak on 14/12/16.
 */
jQuery(document).ready(function ($) {

    var isChromium = window.chrome,
        winNav = window.navigator,
        vendorName = winNav.vendor,
        isOpera = winNav.userAgent.indexOf("OPR") > -1,
        isIEedge = winNav.userAgent.indexOf("Edge") > -1,
        isIOSChrome = winNav.userAgent.match("CriOS");

    if(isIOSChrome){
    } else if(isChromium !== null && isChromium !== undefined && vendorName === "Google Inc." && isOpera == false && isIEedge == false) {
    } else {
        $('#ReportRequestclick').css('margin-top','1px');
        $('#ReportAccessionDeaccessionclick').css('margin-top','1px');
        $('#ReportCollectionGroupDesignationclick').css('margin-top','1px');
        $('#ReportIncompleteRadio').css('margin-top','1px');
    }

    /***Report Tab RequestType Option Show/Hide ***/
    ReportRequestTabOption();
    $('#ReportRequestclick').click(function () {
        $('#RequestDateRangefrom').val('');
        $('#RequestDateRangeto').val('');
        $('#AccessionDeaccessionDateRangefrom').val('');
        $('#AccessionDeaccessionDateRangeto').val('');
        $('#showAccessionDeaccessionTable').hide();
        $('#partners-tableview').hide();
        $('#requesttype-tableview').hide();
        $('#showReportResultsText').hide();
        $('#requestFromToError').hide();
        $('#accessionFromToError').hide();
        $('#ReportShowBy').val('Partners');
        if ($(this).is(':checked')) {
            RequestOption();
        }
    });
    $('#ReportAccessionDeaccessionclick').click(function () {
        $('#RequestDateRangefrom').val('');
        $('#RequestDateRangeto').val('');
        $('#AccessionDeaccessionDateRangefrom').val('');
        $('#AccessionDeaccessionDateRangeto').val('');
        $('#showAccessionDeaccessionTable').hide();
        $('#deaccessionErrorText').hide();
        $('#requestFromToError').hide();
        $('#accessionFromToError').hide();
        if ($(this).is(':checked')) {
            AccessionDeaccessionOption();
        }
    });
    $('#ReportCollectionGroupDesignationclick').click(function () {
        $('#RequestDateRangefrom').val('');
        $('#RequestDateRangeto').val('');
        $('#AccessionDeaccessionDateRangefrom').val('');
        $('#AccessionDeaccessionDateRangeto').val('');
        $('#showAccessionDeaccessionTable').hide();
        $('#requestFromToError').hide();
        if ($(this).is(':checked')) {
            CollectionGroupDesignationOption();
        }
    });


    /** Request Type show and hide **/
    var reportShowBy = $('#ReportShowBy').val();
    if (reportShowBy == 'RequestType') {
        $('#note-partners').hide();
        $('#note-requesttype').show();
    }
    if (reportShowBy == 'Partners') {
        $('#note-partners').show();
        $('#note-requesttype').hide();
    }


    /*** Datepicker ***/
    $('#reportrequesdatepickerfrom,#reportrequesdatepickerto,#reportaccessiondeaccessionsectiondatepickerfrom,#reportaccessiondeaccessionsectiondatepickerto').datepicker({
        format: 'mm/dd/yyyy',
        autoclose: true,
        toggleActive: true,
        todayHighlight: true
    }).on('show', function(e){
        if ( e.date ) {
            $(this).data('stickyDate', e.date);
        } else if($(this).val()){
            /**auto-populate existing selection*/
            $(this).data('stickyDate', new Date($(this).val()));
            $(this).datepicker('setDate', new Date($(this).val()));
        } else {
            $(this).data('stickyDate', null);
        }
    }).on('hide', function(e){
        var stickyDate = $(this).data('stickyDate');
        if ( !e.date && stickyDate ) {
            $(this).datepicker('setDate', stickyDate);
            $(this).data('stickyDate', null);
        }
    });

    /****Report Tab RequestType Option Show/Hide*****/
    function ReportRequestTabOption() {
        $('#ReportRequestclickview').hide();
        if ($('#recapreports #ReportRequestclick').is(':checked')) {
            RequestOption();
        } else if ($('#recapreports #ReportAccessionDeaccessionclick').is(':checked')) {
            AccessionDeaccessionOption();
        } else if ($('#recapreports #ReportCollectionGroupDesignationclick').is(':checked')) {
            CollectionGroupDesignationOption();
        }
    }

    function RequestOption() {
        $('#recapreports #ReportRequestclickview').show();
        $('#recapreports #ReportAccessionDeaccessionclickview').hide();
        $('#recapreports #ReportCollectionGroupDesignationclickview').hide();
        $('#requestFromDateErrorText').hide();
        $('#requestToDateErrorText').hide();
        $('#accessionErrorText').hide();
        $('#ReportIncompleteRecordsview').hide();
        $('#requestFromToError').hide();
        $('#accessionFromToError').hide();

    }

    function AccessionDeaccessionOption() {
        $('#recapreports #ReportRequestclickview').hide();
        $('#recapreports #ReportAccessionDeaccessionclickview').show();
        $('#recapreports #ReportCollectionGroupDesignationclickview').hide();
        $('#requestFromDateErrorText').hide();
        $('#requestToDateErrorText').hide();
        $('#accessionErrorText').hide();
        $('#ReportIncompleteRecordsview').hide();
        $('#requestFromToError').hide();
        $('#accessionFromToError').hide();
    }

    function CollectionGroupDesignationOption() {
        $('#recapreports #ReportRequestclickview').hide();
        $('#recapreports #ReportAccessionDeaccessionclickview').hide();
        $('#recapreports #ReportCollectionGroupDesignationclickview').show();
        $('#requestFromDateErrorText').hide();
        $('#requestToDateErrorText').hide();
        $('#accessionErrorText').hide();
        $('#ReportIncompleteRecordsview').hide();
        $('#requestFromToError').hide();
        $('#accessionFromToError').hide();
    }


    /***Report Tab ShowBy Select Section Show/Hide ***/
    $(function () {
        $('#ReportShowBy').change(function () {
            $('#' + $(this).val()).show();
            if ($(this).find(':selected').val() === 'Partners') {
                $('#showReportResultsText').hide();
                $('#requesttype-tableview').hide();
                $('#partners-tableview').hide();
                $('#note-partners').show();
                $('#note-requesttype').hide();
                $('#showByErrorText').hide();
                $('#requestFromDateErrorText').hide();
                $('#requestToDateErrorText').hide();
                $('#requestFromToError').hide();
                $('#accessionFromToError').hide();
            } else if ($(this).find(':selected').val() === 'RequestType') {
                $('#showReportResultsText').hide();
                $('#requesttype-tableview').hide();
                $('#partners-tableview').hide();
                $('#note-partners').hide();
                $('#note-requesttype').show();
                $('#showByErrorText').hide();
                $('#requestFromDateErrorText').hide();
                $('#requestToDateErrorText').hide();
                $('#requestFromToError').hide();
                $('#accessionFromToError').hide();
            }
        });


    });


    $("#reports-form").submit(function (event) {
        event.preventDefault();
        reportRequestSubmit();

    });


    function reportRequestSubmit(){
        var submitValid = requestSubmit();
        if(submitValid == true){
            var $form = $('#reports-form');
            var url = "/reports/submit";
            $.ajax({
                url: url,
                type: 'post',
                data: $form.serialize(),
                success: function (response) {
                    $('#reportsContentId').html(response);
                }
            });
        }
        else{
            if($('#recapreports #ReportAccessionDeaccessionclick').is(':checked')){
                $('#ReportAccessionDeaccessionclickview').show();
                $('#ReportRequestclickview').hide();
                $('#showReportResultsText').hide();
                $('#ReportIncompleteRecordsview').hide();

            }
            else {
                $('#ReportRequestclickview').show();
                $('#showReportResultsText').hide();
                $('#ReportIncompleteRecordsview').hide();

            }
        }
    }


    function requestSubmit(){
        var showBy = $('#ReportShowBy').val();
        var requestFromDate = $('#RequestDateRangefrom').val();
        var requestToDate = $('#RequestDateRangeto').val();
        var requestStartDate = new Date(requestFromDate);
        var requestEndDate = new Date(requestToDate);
        var isValid = true;
        if($('#recapreports #ReportAccessionDeaccessionclick').is(':checked')){
            var fromDate = $('#AccessionDeaccessionDateRangefrom').val();
            var toDate = $('#AccessionDeaccessionDateRangeto').val();
            var accessionStartDate = new Date(fromDate);
            var accessionEndDate = new Date(toDate);

            if (isBlankValue(fromDate)) {
                $('#accessionErrorText').show();
                isValid=false;
            }
            else{
                $('#accessionErrorText').hide();
            }
            if (isBlankValue(toDate)) {
                $('#deaccessionErrorText').show();
                isValid=false;
            }
            else {
                $('#deaccessionErrorText').hide();
            }
            if(accessionStartDate > accessionEndDate){
                $('#accessionFromToError').show();
                $('#showAccessionDeaccessionTable').hide();
                isValid=false;
            }else {
                $('#accessionFromToError').hide();
            }
        }
        else{
            if (isBlankValue(requestFromDate)) {
                $('#requestFromDateErrorText').show();
                isValid=false;
            }
            else{
                $('#requestFromDateErrorText').hide();
            }
            if (isBlankValue(requestToDate)) {
                $('#requestToDateErrorText').show();
                isValid=false;
            }
            else {
                $('#requestToDateErrorText').hide();
            }
            if(requestStartDate > requestEndDate){
                $('#requestFromToError').show();
                $('#requesttype-tableview').hide();
                $('#partners-tableview').hide();
                isValid=false;
            }else {
                $('#requestFromToError').hide();
            }
        }
        return isValid;
    }

    function isBlankValue(value) {
        if (value == null || value == '') {
            return true;
        }
        return false;
    }

    $('#RequestDateRangefrom').click(function () {
        $('#requestFromDateErrorText').hide();
        $('#requestFromToError').hide();
    });

    $('#RequestDateRangeto').click(function () {
        $('#requestToDateErrorText').hide();
        $('#requestFromToError').hide();
    });

    $('#AccessionDeaccessionDateRangefrom').click(function () {
        $('#accessionErrorText').hide();
        $('#accessionFromToError').hide();
    });

    $('#AccessionDeaccessionDateRangeto').click(function () {
        $('#deaccessionErrorText').hide();
        $('#accessionFromToError').hide();
    });


    /***Reports Tab Deaccession Table Show and Reports Main Section Hide ***/
    $("#ReportAccessionDeaccessionclickview a.Deaccession-tableshow").click(function (e) {
        $(".reports-main-section").hide();
        $("#Deaccession-tableview").show();
        e.preventDefault();
    });

    $("#Deaccession-tableview .Deaccessionbacklink a").click(function (e) {
        $(".reports-main-section").show();
        $("#Deaccession-tableview").hide();
        e.preventDefault();
    });


    $('#numOfRecordsId').change(function (){
        $('#reportsPageNumber').val(0);
        var $form = $('#reports-form');
        var url = "/reports/deaccessionInformation";
        $.ajax({
            url: url,
            type: 'get',
            data :$form.serialize(),
            success: function (response) {
                $('#deaccessionInformation').html(response);
            }
        });

    });

    $("a[href='https://htcrecap.atlassian.net/wiki/display/RTG/Search']").attr('href',
        'https://htcrecap.atlassian.net/wiki/display/RTG/Reports');

});

function cgd() {
    var url = "/reports/collectionGroupDesignation";
    $.ajax({
        url: url,
        type: 'get',
        success: function (response) {
            $('#cgdTable').html(response);
        }
    });
}

function deaccessionPul() {
    $('#reportsPageNumber').val(0);
    $('#reportstotalPageCount').val(0);
    $('#numOfRecordsId').val('10');
    var $form = $('#reports-form');
    $('#ownInst').val('PUL');
    var url = "/reports/deaccessionInformation";
    $.ajax({
        url: url,
        type: 'get',
        data :$form.serialize(),
        success: function (response) {
            $('#deaccessionInformation').html(response);
        }
    });

}


function deaccessionCul() {
    $('#reportsPageNumber').val(0);
    $('#reportstotalPageCount').val(0);
    $('#numOfRecordsId').val('10');
    var $form = $('#reports-form');
    $('#ownInst').val('CUL');
    var url = "/reports/deaccessionInformation";
    $.ajax({
        url: url,
        type: 'get',
        data :$form.serialize(),
        success: function (response) {
            $('#deaccessionInformation').html(response);
        }
    });

}

function deaccessionNypl() {
    $('#reportsPageNumber').val(0);
    $('#reportstotalPageCount').val(0);
    $('#numOfRecordsId').val('10');
    var $form = $('#reports-form');
    $('#ownInst').val('NYPL');
    var url = "/reports/deaccessionInformation";
    $.ajax({
        url: url,
        type: 'get',
        data :$form.serialize(),
        success: function (response) {
            $('#deaccessionInformation').html(response);
        }
    });

}


function first() {
    var $form = $('#reports-form');
    var url = "/reports/first";
    $.ajax({
        url: url,
        type: 'POST',
        data: $form.serialize(),
        success: function (response) {
            if($('#ReportIncompleteRadio').is(':checked')){
                $('#IncompleteReporttableview').html(response);
            }
            else{
                $('#deaccessionInformation').html(response);
            }
        }
    });
}

function previous() {
    var $form = $('#reports-form');
    var url = "/reports/previous";
    $.ajax({
        url: url,
        type: 'POST',
        data: $form.serialize(),
        success: function (response) {
            if($('#ReportIncompleteRadio').is(':checked')){
                $('#IncompleteReporttableview').html(response);
            }
            else{
                $('#deaccessionInformation').html(response);
            }
        }
    });
}

function next() {
    var $form = $('#reports-form');
    var url = "/reports/next";
    $.ajax({
        url: url,
        type: 'Post',
        data: $form.serialize(),
        success: function (response) {
            if($('#ReportIncompleteRadio').is(':checked')){
                $('#IncompleteReporttableview').html(response);
            }
            else{
                $('#deaccessionInformation').html(response);
            }
        }
    });
}

function last() {
    var $form = $('#reports-form');
    var url = "/reports/last";
    $.ajax({
        url: url,
        type: 'POST',
        data: $form.serialize(),
        success: function (response) {
            if($('#ReportIncompleteRadio').is(':checked')){
                $('#IncompleteReporttableview').html(response);
            }
            else{
                $('#deaccessionInformation').html(response);
            }
        }
    });
}

function incompleteRecords(){
    $('#ReportIncompleteRecordsview').show();
    $('#recapreports #ReportRequestclickview').hide();
    $('#recapreports #ReportAccessionDeaccessionclickview').hide();
    $('#recapreports #ReportCollectionGroupDesignationclickview').hide();
    $('#IncompleteReporttableview').hide();
    $('#incompleteShowRecords').val(10);
    var url = "/reports/getInstitutions";
    $.ajax({
        url: url,
        type: 'get',
        success: function (response) {
            $('#incompleteShowBy').html(response);
        }
    });
}

function submitIncompleterequest(){
    var $form = $('#reports-form');
    $('#incompleteShowRecords').val(10);
    var url = "/reports/incompleteRecords";
    $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            $('#IncompleteReporttableview').html(response);
            $('#IncompleteReporttableview').show();

        }
    });
}

function exportRecords(){
    var $form = $('#reports-form');
    var url = "/reports/export";
    $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response,status, xhr) {
            var filename = "";
            var disposition = xhr.getResponseHeader('Content-Disposition');
            if (disposition && disposition.indexOf('attachment') !== -1) {
                var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                var matches = filenameRegex.exec(disposition);
                if (matches != null && matches[1]) filename = matches[1].replace(/['"]/g, '');
            }

            var type = xhr.getResponseHeader('Content-Type');
            var blob = new Blob([response], { type: type });

            if (typeof window.navigator.msSaveBlob !== 'undefined') {
                // IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created. These URLs will no longer resolve as the data backing the URL has been freed."
                window.navigator.msSaveBlob(blob, filename);
            } else {
                var URL = window.URL || window.webkitURL;
                var downloadUrl = URL.createObjectURL(blob);

                if (filename) {
                    // use HTML5 a[download] attribute to specify filename
                    var a = document.createElement("a");
                    // safari doesn't support this yet
                    if (typeof a.download === 'undefined') {
                        window.location = downloadUrl;
                    } else {
                        a.href = downloadUrl;
                        a.download = filename;
                        document.body.appendChild(a);
                        a.click();
                    }
                } else {
                    window.location = downloadUrl;
                }

                setTimeout(function () { URL.revokeObjectURL(downloadUrl); }, 100); // cleanup
            }
        }
    });
}


function incompletePageSizeRequest(){
    var $form = $('#reports-form');
    var url = "/reports/incompleteReportPageSizeChange";
    $.ajax({
        url: url,
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            $('#IncompleteReporttableview').html(response);
            $('#IncompleteReporttableview').show();

        }
    });
}


function ToggleAccessionDateFrom(){
    var dateFromLength = $('#AccessionDeaccessionDateRangefrom').val().length;
    var dateFrom = $('#AccessionDeaccessionDateRangefrom').val();
    var includes = dateFrom.includes('/');
    if(dateFromLength == 8 && !includes){
        var date = splitMMddYYYY(dateFrom);
        $('#AccessionDeaccessionDateRangefrom').val(date.getMonth()+"/"+date.getDate()+"/"+date.getFullYear());
    }
    if(dateFromLength == 10 && includes){
        var valid = isValidDate(dateFrom);
        if(valid){
            var split = dateFrom.split('/');
            var dd = parseInt(split[0]);
            var mm  = parseInt(split[1]);
            var yyyy = parseInt(split[2]);
            $('#AccessionDeaccessionDateRangefrom').val(dd+"/"+mm+"/"+yyyy);
        }
    }
}

function ToggleAccessionDateTo(){
    var dateToLength = $('#AccessionDeaccessionDateRangeto').val().length;
    var dateTo = $('#AccessionDeaccessionDateRangeto').val();
    var includes = dateTo.includes('/');
    if(dateToLength == 8 && !includes){
        var date = splitMMddYYYY(dateTo);
        $('#AccessionDeaccessionDateRangeto').val(date.getMonth()+"/"+date.getDate()+"/"+date.getFullYear());
    }
    if(dateToLength == 10 && includes){
        var valid = isValidDate(dateTo);
        if(valid){
            var split = dateTo.split('/');
            var dd = parseInt(split[0]);
            var mm  = parseInt(split[1]);
            var yyyy = parseInt(split[2]);
            $('#AccessionDeaccessionDateRangeto').val(dd+"/"+mm+"/"+yyyy);
        }
    }
}
function ToggleRequestDateFrom(){
    var dateFromLength = $('#RequestDateRangefrom').val().length;
    var dateFrom = $('#RequestDateRangefrom').val();
    var includes = dateFrom.includes('/');
    if(dateFromLength == 8 && !includes){
        var date = splitMMddYYYY(dateFrom);
        $('#RequestDateRangefrom').val(date.getMonth()+"/"+date.getDate()+"/"+date.getFullYear());
    }
    else if(dateFromLength == 10 && includes){
        var valid = isValidDate(dateFrom);
        if(valid){
            var split = dateFrom.split('/');
            var dd = parseInt(split[0]);
            var mm  = parseInt(split[1]);
            var yyyy = parseInt(split[2]);
            $('#RequestDateRangefrom').val(dd+"/"+mm+"/"+yyyy);
        }
    }
}
function ToggleRequestDateTo(){
    var dateToLength = $('#RequestDateRangeto').val().length;
    var dateTo = $('#RequestDateRangeto').val();
    var includes = dateTo.includes('/');
    if(dateToLength == 8 && !includes){
        var date = splitMMddYYYY(dateTo);
        $('#RequestDateRangeto').val(date.getMonth()+"/"+date.getDate()+"/"+date.getFullYear());
    }
    if(dateToLength == 10 && includes){
        var valid = isValidDate(dateTo);
        if(valid){
            var split = dateTo.split('/');
            var dd = parseInt(split[0]);
            var mm  = parseInt(split[1]);
            var yyyy = parseInt(split[2]);
            $('#RequestDateRangeto').val(dd+"/"+mm+"/"+yyyy);
        }
    }
}

function splitMMddYYYY(dateFrom) {
    var day = dateFrom.substring(2, 4);
    var year = dateFrom.substring(4, 8);
    var month = dateFrom.slice(0, 2);
    var date = new Date(year, month, day);
    return date;
}

function isValidDate(dateFrom) {
    var formats = 'MM/DD/YYYY';
    var valid = moment(dateFrom, formats).isValid();
    return valid;
}
