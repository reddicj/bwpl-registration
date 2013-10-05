<%@ page import="org.apache.commons.lang.StringUtils" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
</head>

<body>
<div>
    <table class="bwpl-form-table">
        <tbody>
        <tr>
            <g:form method="get" action="search">
                <td>Firstname:<br/><g:textField name="firstName" value="${firstName}"/></td>
                <td>Lastname:<br/><g:textField name="lastName" value="${lastName}"/></td>
                <td>Status changed since:<br/><g:datePicker precision="day" name="statusChangedDate" value="${statusChangedDate}"/></td>
                <td><br/><g:submitButton name="searchByName" value="Search" class="buttons"/></td>
            </g:form>
        </tr>
        </tbody>
    </table>
</div>
<hr/>
<g:if test="${registrations.isEmpty()}">
    <div>
        <p>No registrations found.</p>
    </div>
</g:if>
<g:else>
    <div>${stats.count} Registrations (${stats.countOfValid} valid, ${stats.countOfInvalid} invalid)</div>

    <div>&nbsp;</div>
    <g:render template="list" model="[registrations: registrations]"/>
</g:else>
</body>
</html>