<%@ page import="org.bwpl.registration.Competition" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
</head>
<body>
<g:render template="/nav/divisionTabs"/>
<table class="bwpl-table">
    <thead>
    <tr>
        <th>Team</th>
        <th>Club</th>
        <g:if test="${canUpdate}">
            <th>Actions</th>
        </g:if>
    </tr>
    </thead>
    <tbody>
    <g:each in="${teams}" var="team">
        <tr>
            <td><g:link controller="team" action="show" id="${team.id}" params="[competition: division.competition.urlName]">${team.name}</g:link></td>
            <td><g:link controller="club" action="show" id="${team.club.id}" params="[competition: division.competition.urlName]">${team.club.name}</g:link></td>
            <g:if test="${canUpdate}">
                <td>
                    <g:link controller="team" action="edit" id="${team.id}" params="[competition: division.competition.urlName]">Edit</g:link> |
                    <g:link controller="team" action="delete" id="${team.id}" params="[competition: division.competition.urlName]" onclick="return confirm('Are you sure you want to delete team: ${team.name}?');">Delete</g:link>
                </td>
            </g:if>
        </tr>
    </g:each>
    </tbody>
</table>
</body>
</html>