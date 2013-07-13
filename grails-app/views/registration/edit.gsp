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
    <g:form method="post" action="$actionName" params="[competition: params.competition]">
        <g:hiddenField name="teamId" value="${team.id}"/>
        <fieldset class="form">
            <g:if test="${actionName == "edit"}">
                <g:hiddenField name="id" value="${r?.id}"/>
                <g:render template="editForm"/>
            </g:if>
            <g:else>
                <g:render template="createForm"/>
            </g:else>
            <g:actionSubmit value="Update" class="buttons"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
