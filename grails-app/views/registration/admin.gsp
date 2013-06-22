<%@ page import="org.bwpl.registration.Club" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Admin</title>
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
</sec:ifAllGranted>
<h2>Export Data</h2>
<ul class="bwpl-club-list">
    <li><g:link action="export">Export All Registrations</g:link></li>
    <sec:ifAllGranted roles="ROLE_REGISTRATION_SECRETARY">
        <li><g:link controller="admin" action="export">Export BWPL Data</g:link></li>
    </sec:ifAllGranted>
</ul>
<hr/>
<p>Version: <g:meta name="app.version"/> / Built: <g:meta name="app.build.date"/></p>
</body>
</html>
