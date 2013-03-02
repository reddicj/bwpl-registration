<%@ page import="org.bwpl.registration.Club" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Clubs</title>
</head>
<body>
<g:each in="${clubLists}" var="list">
    <div class="float">
        <ul class="bwpl-club-list">
            <g:each in="${list}" var="c">
                <li>
                    <g:link action="show" id="${c.id}">${c.name}</g:link>
                    <sec:ifAllGranted roles="ROLE_REGISTRATION_SECRETARY">
                        (<g:link action="edit" id="${c.id}">Edit</g:link> | <g:link action="delete" id="${c.id}" onclick="return confirm('Are you sure you want to delete?');">Delete</g:link>)
                    </sec:ifAllGranted>
                </li>
            </g:each>
        </ul>
    </div>
</g:each>
</body>
</html>
