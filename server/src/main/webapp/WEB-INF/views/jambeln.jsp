<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<script type="text/javascript">
var jambels;
var URL = "signalLights";

$(document).ready(function () {
    requestJambels(true);
});

function requestJambels(loadAll) {

    var getUrl = URL;
    if (loadAll) {
        getUrl += "/all";
    }
    // time parameter to avoid caching by browser
    $.getJSON(getUrl + "?time=" + new Date().toString()).done(
            function (response, responseState) {
                if (responseState === "success") {
                    $('#jambelContainer').empty();
                    $('.configureDialog').remove();

                    jambels = response.signalLights;

                    for (var i = 0; i < response.signalLights.length; i++) {

                        var signalLight = response.signalLights[i];

                        var jambelDiv = $(document.createElement('div'));
                        jambelDiv.attr('id', 'jambel_' + i);
                        jambelDiv.addClass('box');
                        jambelDiv.css('float', 'left');

                        var jambelStatusDiv = $(document.createElement('div'));
                        jambelStatusDiv.addClass('jambel_status');
                        jambelStatusDiv.attr('id', 'jambel_status' + i);

                        if (signalLight.status.green === "ON") {
                            jambelStatusDiv.addClass('jambel_status_green');
                        }
                        else if (signalLight.status.yellow === "ON") {
                            jambelStatusDiv.addClass('jambel_status_yellow');
                        }
                        else if (signalLight.status.green === "BLINK") {
                            setInterval("blinkColor(" + i + ", 'green')", 1000);
                        }
                        else if (signalLight.status.yellow === "BLINL") {
                            setInterval("blinkColor(" + i + ", 'yellow')", 1000);
                        }
                        else if (signalLight.status.red === "BLINK") {
                            setInterval("blinkColor(" + i + ", 'red')", 1000);
                        }
                        else if (signalLight.status.green === "ON") {
                            jambelStatusDiv.addClass('jambel_status_green');
                        }
                        else if (signalLight.status.red === "ON") {
                            jambelStatusDiv.addClass('jambel_status_red');
                        }

                        jambelStatusDiv.append('<h2>' + signalLight.host + ':' + signalLight.port + '</h2>');

                        var jambelJobConfigDiv = $(document.createElement('div'));
                        jambelJobConfigDiv.addClass('configuretion');
                        jambelJobConfigDiv.append('<h2>Jobs</h2>');


                        var jambelJobConfiButtonDiv = $(document.createElement('div'));
                        jambelJobConfiButtonDiv.attr('id', 'configureDialog');
                        jambelJobConfiButtonDiv.addClass('Edit_configuretion');
                        jambelJobConfiButtonDiv.append('<input id = "imageEdit1" value= "1" type = "image" width = "30" height = "30" src = "<%= pageContext.getServletContext().getContextPath()
                        %>/static/images/Edit.png" onclick="editConfiguretion(' + i + ')">');

                        var jambelJobConfigurationDiv = $(document.createElement('div'));
                        jambelJobConfigurationDiv.attr('id', 'box_configured' + i);
                        jambelJobConfigurationDiv.hide();

                        for (var j = 0; j < signalLight.jobsConfiguration.length; j++) {
                            var jobConfigurationElement = signalLight.jobsConfiguration[j];
                            var color = "#E3E3E3";
                            if (jobConfigurationElement.jobState.lastResult === "FAILURE") {
                                color = "#F49F8F";
                            }
                            else if (jobConfigurationElement.jobState.lastResult === "SUCCESS") {
                                color = "#A0E27D";
                            }

                            jambelJobConfigurationDiv.append('<table border="0"style="color:black;background-color:' + color + ';font-size:12px;word-wrap: break-word;max-width:325;width:325; margin-left:5px;margin-bottom: 2px; margin-top: 8px">'
                                    + '<tr><th style = "float: left">update Mode</th><td>' + jobConfigurationElement.jobConfiguration.updateMode + '</td></tr>'
                                    + '<tr><th style = "float: left">initialJobStatePoll</th><td>' + jobConfigurationElement.jobConfiguration.initialJobStatePoll + '</td></tr>'
                                    + '<tr><th style = "float: left">phase</th><td>' + jobConfigurationElement.jobState.phase + '</td></tr>'
                                    + '<tr><th style = "float: left">last Result</th><td>' + jobConfigurationElement.jobState.lastResult + '</td></tr>'
                                    + '<tr><th style = "float: left">pollingInterval</th><td>' + jobConfigurationElement.jobConfiguration.pollingInterval + '</td></tr>'
                                    + '<tr><th style = "float: left">jenkins Job Url</th><td>' + jobConfigurationElement.jobConfiguration.jenkinsJobUrl + '</td></tr>'
                                    + '</table>');
                        }

                        var jambelButtonConfigurationJob = $(document.createElement('div'));
                        jambelButtonConfigurationJob.attr('id', 'image_iconConfigured' + i);
                        jambelButtonConfigurationJob.addClass('image_plus');
                        jambelButtonConfigurationJob.append('<input id = "image_iconConfigured" type= "image" width="28px" height="28" src="<%= pageContext.getServletContext().getContextPath()%>/static/images/List2.png" onclick = "Configured('
                                + i + ')">');

                        var jambelConfigNewButton = $(document.createElement('div'));
                        jambelConfigNewButton.attr('id', 'newButton' + i);
                        jambelConfigNewButton.addClass('new_Button');
                        jambelConfigNewButton
                                .append('<input type = "Button" class = "new_Button" id = "new_Button" value = "New" onclick = "newText(\''
                                        + i + '\')">');

                        jambelDiv.append(jambelStatusDiv);
                        jambelDiv.append(jambelJobConfigDiv);
                        jambelJobConfigDiv.append(jambelJobConfiButtonDiv);
                        jambelJobConfigDiv.append(jambelButtonConfigurationJob);
                        jambelJobConfigDiv.append(jambelJobConfigurationDiv);
                        $('#jambelContainer').append(jambelDiv);
                    }
                    initDialogs();

                    // recursive call. fills the stack trace. there's probably some better way to do the asynchronous sleep call, but it does the trick.
                    requestJambels(false);
                }
                else if (responseState === "notmodified") {
                    requestJambels(false);
                } else {
                    console.log("error occur, pause long polling");
                    setTimeout(function () {
                                requestJambels(false)
                            }, 10000
                    );
                }

            }).error(function (e) {
                console.log("error: " + JSON.stringify(e));
                console.log("error occur, pause long polling");
                setTimeout(function () {
                            requestJambels(false)
                        }, 10000
                );
            })
            .always(function () {

            });
}

// Color Blinken ON OFF
function blinkColor(jambelId, color) {
    $('#jambel_status' + jambelId).toggleClass('jambel_status_' + color);
}

function initDialogs() {
    // Edit configuretion dialog

    $("#dialog-configuredJobs").dialog({
        DialogClass: "conDialog",
        title: "Configuration",
        autoOpen: false,
        show: {
            effect: "blind",
            duration: 250
        },
        hide: {
            effect: "blind",
            duration: 250
        },
        resizable: false,
        modal: true,
        width: 620,
        position: [ 400, 200 ],
        open: function (event, ui) {
            $(this).parent().find(".ui-dialog-titlebar-close").hide();
        },
        buttons: {
            "Apply": function () {

                var postData = {

                    jobs: [
                        {
                            "jenkinsJobUrl": $("#newConfiText").val(),
                            "updateMode": $('#deleteselect').val(),
                            "initialJobStatePoll": $('#text2').val()
                        }
                    ]
                };
                var confiObject = $.ajax({
                    url: URL + "/0",
                    data:  JSON.stringify(postData),
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    type: "POST"
                });

                $(this).dialog("close");
            },
            "Cancel": function () {
                $(this).dialog("close");
            }

        }
    });

    var newDialog = $("#newDialog").dialog({
        DialogClass: "dialog",
        autoOpen: false,
        show: {
            effect: "blind",
            duration: 250
        },
        hide: {
            effect: "blind",
            duration: 250
        },
        resizable: false,
        modal: true,
        width: 300,
        height: 350,
        open: function (event, ui) {
            $(this).parent().find(".ui-dialog-titlebar-close").hide();
        },

        buttons: {
            "Apply": function () {
                var creatNewJambel = "ip=" + $('#ip').val() + "&port=" + $('#port').val() + "&colors=" + $('#color').val();
                var xhr = $.ajax({
                    url: URL + "?" + creatNewJambel,
                    type: "PUT"
                });
                xhr.done(function () {
                    newDialog.dialog("close");
                });
                xhr.fail(function (jqXHR, textStatus, errorThrown) {
                    $("#error").append('<input  type= "image" width="20px" height="20" src="<%= pageContext.getServletContext().getContextPath()%>/static/images/error.png">' + " " + textStatus + ": " + errorThrown);
                });
            },
            "Cancel": function () {
                $(this).dialog("close");
            }
        }

    });
}
function switchColor(jambelDivId, color) {
    $('#' + jambelDivId).children('div[id^="jambelStatus"]').addClass(
                    "jambel_status_" + color);
}
// cnfiguration Jobs
function Configured(index) {
    $("#box_configured" + index).toggle("250");
}
// Dialog for the editing Configuration jobs
function editConfiguretion(jambelIndex) {


    var jambelJobConfigurationContent = $("#jobConfigurationContent");
    jambelJobConfigurationContent.empty();

    jambelJobConfigurationContent.append('<input type = "Button" class = "new_Button" id = "new_Button" value = "New" onclick = "newText(\''
            + jambelIndex + '\')">');


    for (var z = 0; z < jambels[jambelIndex].jobsConfiguration.length; z++) {
        var jobConfigurationDialogElement = jambels[jambelIndex].jobsConfiguration[z];


        jambelJobConfigurationContent
                .append('<input  id = "confi_text' + jambelIndex + '_' + z + '" size = "35" type = "text" style="background-color: #C9CAC9; font-size: 12pt"; value = "' + jobConfigurationDialogElement.jobConfiguration.jenkinsJobUrl + '">');
        jambelJobConfigurationContent
                .append('<select id = "select_text' + jambelIndex + '_' + z + '" style="background-color: #C9CAC9; font-size: 12pt;"><option>Posting</option><option>Polling</option></select>');
        jambelJobConfigurationContent
                .append('<input  id = "empty_text' + jambelIndex + '_' + z + '" type="text" size="6" style="background-color: #C9CAC9; font-size: 12pt">');
        jambelJobConfigurationContent
                .append('<input  id = "confi_checkbox' + jambelIndex + '_' + z + '" type="checkbox">');
        jambelJobConfigurationContent
                .append('<input  id = "Delete_button'
                        + jambelIndex
                        + '_'
                        + z
                        + '" class="textbutton" type="button" style="font-size: 12pt" value="Delete" onclick="deleteText(\''
                        + jambelIndex + '_' + z + '\')">');

        $("#dialog-configuredJobs").append(jambelJobConfigurationContent);
    }
    $("#dialog-configuredJobs").dialog("open");
}
// Create new Jambel
function newDialog() {
    $('#error').text("");
    $('#ip').val("");
    $('#port').val("");
    $('#color').val("");
    $("#newDialog").dialog("open");

}
/**
 * Append a new line, new option button, new checkbox, and delete button for the jobs configuration in the dialog.
 *
 * index - number of the line
 */
function newText(jambelIndex) {
    $('<input  id = "newConfiText" size = "35" style = "background-color: #C9CAC9; font-size: 12pt"/>').appendTo("#jobConfigurationContent");

    $('<select id= "deleteselect" style = "background-color:#C9CAC9; font-size: 12pt" ><option></option><option>Posting</option><option>Polling</option></select>')
            .appendTo("#jobConfigurationContent");
    $('<input  id = "text2" size = "6" style = "background-color:#C9CAC9; font-size: 12pt"/>')
            .appendTo("#jobConfigurationContent");

    $('<input type = "checkbox" id = "checkbox"/>').appendTo("#jobConfigurationContent");

    $('<input class = "deletebutton" type = "button" id = "deletebutton" value = "Delete" onclick ="deleteButtonWithText()"style = "font-size: 12pt"/>')
            .appendTo("#jobConfigurationContent");
}
function deleteButtonWithText(index) {
    $("#newConfiText").remove();
    $("#deleteselect").remove();
    $("#text2").remove();
    $("#checkbox").remove();
    $("#deletebutton").remove();
}
// Button for the deleting configuration jobs
function deleteText(index) {
    $("#Delete_button" + index).remove();
    $("#confi_checkbox" + index).remove();
    $("#empty_text" + index).remove();
    $("#select_text" + index).remove();
    $("#confi_text" + index).remove();
}
</script>


<div class="newDialog" id="newDialog" title="New Jambel">

    <c:out value="${model.name}"/>
    <div>
        <div style="float: left;">IP:</div>
        <div>
            <input id="ip" style="float: right; background-color: #C9CAC9" type="text"
                   size="19" required="true" value=''>
        </div>
    </div>

    <br> <br>

    <div>
        <div style="float: left;">Port:</div>
        <div>
            <input id="port" style="float: right; background-color: #C9CAC9" type="text"
                   size="19" required="true">
        </div>
    </div>
    <br> <br>

    <div>
        <div style="float: left;">Colors:</div>
        <div style="float: right">
            <select id="color" style="background-color: #C9CAC9; width: 197px" required="true">
                <option></option>
                <option value="RYG">RYG</option>
                <option value="GYR">GYR</option>
                <option value="YRG">YRG</option>
            </select>

            <br><br>

            <div>
                <label id="error" style="color: #FF0000"></label>
            </div>
        </div>
    </div>
</div>
<br/>

<div id="jambelContainer">
    <br/>
</div>

<div style="clear: both"></div>
<br/>

<div id="dialog-configuredJobs">
    <div id="jobConfigurationContent"></div>
</div>

