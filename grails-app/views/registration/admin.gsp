<%@ page import="org.bwpl.registration.Club" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Admin</title>
    <sec:access expression="hasRole('ROLE_REGISTRATION_SECRETARY')">
        <g:javascript>
            <g:render template="/registration/validationJavascript"/>
        </g:javascript>
        <g:javascript>
            $("button").click(function() {
                validateRegistrations();
            })
        </g:javascript>
    </sec:access>
</head>
<body>
<sec:ifAllGranted roles="ROLE_REGISTRATION_SECRETARY">
    <h2>Upload Data</h2>
    <ul class="bwpl-club-list">
        <li><g:link controller="club" action="create">Add Club</g:link></li>
        <li><g:link controller="club" action="upload">Upload Club data</g:link></li>
        <li><g:link action="upload">Upload All Registrations</g:link></li>
        <li><g:link controller="admin" action="upload">Upload User data</g:link></li>
    </ul>
    <div>&nbsp;</div>
    <hr/>
    <h2>Validate All Registrations</h2>
    <table>
        <tr>
            <td id="validationStatus">${stats.count} Registrations (${stats.countOfValid} valid, ${stats.countOfInvalid} invalid)</td>
            <td>&nbsp;</td>
            <td><button id="validate-button" class="buttons">Validate</button></td>
        </tr>
    </table>
    <div>&nbsp;</div>
    <hr/>
</sec:ifAllGranted>
<h2>Export Data</h2>
<ul class="bwpl-club-list">
    <li><g:link action="export">Export All Registrations</g:link></li>
</ul>
<hr/>
<p>Version: <g:meta name="app.version"/> / Built: <g:meta name="app.build.date"/></p>
</body>
</html>
