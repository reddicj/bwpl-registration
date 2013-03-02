<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>${club.name}</title>
    <g:if test="${canUpdate}">
        <g:javascript src="registration-validation.js"/>
        <g:if test="${controllerName == "club"}">
            <g:javascript>
                $("#validate-button").click(function() {
                    validateClub(${params.id})
                })
            </g:javascript>
        </g:if>
        <g:else>
            <g:javascript>
                $("#validate-button").click(function() {
                    validateTeam(${params.id});
                })
            </g:javascript>
        </g:else>
    </g:if>
</head>
<body>
<g:render template="/nav/clubTabs"/>
<g:render template="/nav/subNav"/>
<g:if test="${doDisplayValidateButton}">
    <table>
        <tr>
            <td id="validationStatus">${stats.count} Registrations (${stats.countOfValid} valid, ${stats.countOfInvalid} invalid)</td>
            <td>&nbsp;</td>
            <td><button id="validate-button" class="buttons">Validate</button></td>
        </tr>
    </table>
</g:if>
<g:if test="${registrations.isEmpty()}">
    <p>There are no registrations for this view.</p>
</g:if>
<g:else>
    <div>&nbsp;</div>
    <g:render template="/registration/list" model="[registrations: registrations, canUpdate: canUpdate]"/>
</g:else>
</body>
</html>
