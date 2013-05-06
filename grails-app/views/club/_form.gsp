<%@ page import="org.bwpl.registration.Club" %>
<div class="fieldcontain">
    <table class="bwpl-form-table">
        <thead>
        <tr>
            <th>Name</th><th>ASA Name</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td><g:textField name="club-name" required="true" value="${club?.name}"/></td>
            <td><g:textField name="club-asaName" required="true" value="${club?.asaName}"/></td>
        </tr>
        </tbody>
    </table>
</div>
<hr/>
<div class="fieldcontain">
    <h1>Secretary</h1>
    <table class="bwpl-form-table">
        <thead>
        <tr>
            <th>Firstname</th><th>Lastname</th><th>Email</th><th>Delete?</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${club?.secretaries}" var="secretary">
            <tr>
                <td><g:textField name="secretary-firstname-$secretary.id" value="${secretary.firstname}"/></td>
                <td><g:textField name="secretary-lastname-$secretary.id" value="${secretary.lastname}"/></td>
                <td><g:textField name="secretary-email-$secretary.id" value="${secretary.username}"/></td>
                <td><g:checkBox name="secretary-delete-$secretary.id"/></td>
            </tr>
        </g:each>
        <tr>
            <td><g:textField name="secretary-firstname-new" value=""/></td>
            <td><g:textField name="secretary-lastname-new" value=""/></td>
            <td><g:textField name="secretary-email-new" value=""/></td>
        </tr>
        </tbody>
    </table>
</div>
<hr/>
<div class="fieldcontain">
    <h1>Teams</h1>
    <table class="bwpl-form-table">
        <thead>
        <tr>
            <th>Name</th><th>Gender</th><th>Division</th><th>Delete?</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${club?.teams}" var="team">
            <tr>
                <td><g:textField name="team-name-$team.id" required="true" value="${team.name}"/></td>
                <td><g:select name="team-gender-$team.id" from="${["M", "F"]}" value="${team.gender}"/></td>
                <td><g:select name="team-division-$team.id" from="${0..5}" value="${team.division}"/></td>
                <td><g:checkBox name="team-delete-$team.id"/></td>
            </tr>
        </g:each>
        <tr>
            <td><g:textField name="team-name-new" value=""/></td>
            <td><g:select name="team-gender-new" from="${["M", "F"]}" value="M"/></td>
            <td><g:select name="team-division-new" from="${0..5}" value="1"/></td>
        </tr>
        </tbody>
    </table>
</div>
