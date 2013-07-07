<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
</head>
<body>
<div>
    <g:hasErrors bean="${team}">
        <ul class="errors" role="alert">
            <g:eachError bean="${team}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                        error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form method="post">
        <g:hiddenField name="division" value="${division?.id}"/>
        <g:hiddenField name="id" value="${team?.id}"/>
        <fieldset class="form">
            <div class="fieldcontain">
                <table class="bwpl-form-table">
                    <thead>
                    <tr>
                        <th>Name</th><th>Gender</th><th>Division</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td><g:textField name="name" required="true" value="${team?.name}"/></td>
                        <td><g:select name="gender" from="${["M", "F"]}" value="${team?.gender}"/></td>
                        <td><g:select name="rank" from="${0..5}" value="${team?.division.rank}"/></td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </fieldset>
        <fieldset class="form">
            <g:actionSubmit action="updateAndReturnToList" value="Update and exit" class="buttons"/>
            <g:actionSubmit action="update" value="Update" class="buttons"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
