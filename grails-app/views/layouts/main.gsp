<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title>
            <g:if test="${title}">
                ${title}
            </g:if>
            <g:else>
                <g:layoutTitle default="BWPL Registration"/>
            </g:else>
        </title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'mobile.css')}" type="text/css">
        <link rel="shortcut icon" href="${resource(dir: "images", file: "favicon.ico")}" type="image/x-icon"/>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js"></script>
        <g:layoutHead/>
		<r:layoutResources />
	</head>
	<body>
    <table class="bwpl-header">
        <tr>
            <td>
                <g:img dir="images" file="bwpl-icon-small.png" width="25" height="28"/>
            </td>
            <td><h1>BWPL Registration</h1></td>
            <td >
                <sec:ifNotLoggedIn>
                    Not logged in (<g:link controller="login" action="auth">Login</g:link>)
                </sec:ifNotLoggedIn>
                <sec:ifLoggedIn>
                    <g:if test="${user != null}">
                        Logged in as ${user.name} (<g:link controller='logout'>Logout</g:link>)
                    </g:if>
                    <g:else>
                        Logged in as <sec:loggedInUserInfo field="username"/> (<g:link controller='logout'>Logout</g:link>)
                    </g:else>
                </sec:ifLoggedIn>
            </td>
        </tr>
    </table>
    <g:render template="/nav/nav"/>
    <g:if test="${title}"><h2>${title}</h2></g:if>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:elseif test="${flash.errors}">
        <div class="errors">${flash.errors}</div>
    </g:elseif>
    <g:layoutBody/>
    <div class="footer" role="contentinfo"></div>
    <div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
    <g:javascript library="application"/>
    <r:layoutResources />
	</body>
</html>
