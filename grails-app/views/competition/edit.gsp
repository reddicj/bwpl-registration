<%@ page import="org.bwpl.registration.Competition" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>${title}</title>
</head>
<body>
<div>
    <g:hasErrors bean="${competition}">
        <ul class="errors" role="alert">
            <g:eachError bean="${competition}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>>
                    <g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form method="post">
        <g:hiddenField name="id" value="${competition?.id}"/>
        <fieldset class="form">
            <g:render template="form"/>
        </fieldset>
        <fieldset class="form">
            <g:actionSubmit action="updateAndReturnToList" value="Update and exit" class="buttons"/>
            <g:actionSubmit action="update" value="Update" class="buttons"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
