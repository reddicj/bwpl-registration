<%@ page import="org.bwpl.registration.Registration" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
     <title>${title}</title>
</head>
<body>
<div>
    <g:hasErrors bean="${r}">
        <ul class="errors" role="alert">
            <g:eachError bean="${r}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                        error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form method="post">
        <g:hiddenField name="teamId" value="${team.id}"/>
        <fieldset class="form">
            <g:if test="${actionName == "edit"}">
                <g:hiddenField name="id" value="${r?.id}"/>
                <g:render template="editForm"/>
                <g:actionSubmit action="update" value="Update" class="buttons"/>
            </g:if>
            <g:else>
                <g:render template="createForm"/>
                <g:actionSubmit action="add" value="Update" class="buttons"/>
            </g:else>
        </fieldset>
    </g:form>
</div>
</body>
</html>
