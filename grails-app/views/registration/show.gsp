<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
</head>
<body>
<g:if test="${!registration.canUpdate()}">
    <p style="color: red;">Cannot edit or delete this registration as it is or has been validated.</p>
</g:if>
<g:render template="list" model="[registrations: [registration]]"/>
<h2>Status history</h2>
<div id="registrations">
    <table class="bwpl-table">
        <thead>
        <tr>
        <th>Date</th>
        <th>User</th>
        <th>Action</th>
        <th>Status</th>
        <th>Notes</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${registration.statusEntriesAsList}" var="statusEntry">
            <tr class="${statusEntry.statusAsEnum.toString().toLowerCase()}">
                <td>${statusEntry.dateAsString}</td>
                <td>${statusEntry.user.name}</td>
                <td>${statusEntry.actionAsEnum.toString()}</td>
                <td>${statusEntry.statusAsEnum.toString()}</td>
                <td>${statusEntry.notes}</td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
</body>
</html>