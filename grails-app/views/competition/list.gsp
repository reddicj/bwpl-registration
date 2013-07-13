<%@ page import="org.bwpl.registration.Competition" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
</head>
<body>
<ul class="bwpl-club-list">
<g:each in="${competitions}" var="competition">
    <li>
        <g:link action="show" id="${competition.id}" params="[competition: competition.urlName]">${competition.name}</g:link>
        <sec:ifAllGranted roles="ROLE_REGISTRATION_SECRETARY">
            (<g:link action="edit" id="${competition.id}">Edit</g:link> | <g:link action="delete" id="${competition.id}" onclick="return confirm('Are you sure you want to delete?');">Delete</g:link>)
        </sec:ifAllGranted>
    </li>
</g:each>
</ul>
</body>
</html>