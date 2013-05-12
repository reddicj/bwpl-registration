<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Registrations</title>
    <sec:access expression="hasRole('ROLE_REGISTRATION_SECRETARY')">
        <g:javascript>
            <g:render template="/registration/validationJavascript"/>
        </g:javascript>
        <g:javascript>
            $("button").click(function() {
                validateRegistrations();
            })
        </g:javascript>
    </sec:access>
</head>
<body>
<g:render template="/nav/registrationsTabs"/>
<g:render template="/nav/subNav"/>
<g:if test="${registrations.isEmpty()}">
    <div>
        <p>There are no registrations for this view.</p>
    </div>
</g:if>
<g:else>
    <div>&nbsp;</div>
    <g:render template="list" model="[registrations: registrations]"/>
</g:else>
</body>
</html>