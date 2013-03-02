<%@ page import="org.apache.commons.lang.StringUtils" %>
<div class="fieldcontain">
    <table class="bwpl-form-table">
        <tbody>
        <tr>
            <td>Firstname:</td><td><g:textField name="firstName" required="true" value="${params["firstName"]}"/></td>
            <td>Lastname:</td><td><g:textField name="lastName" required="true"  value="${params["lastName"]}"/></td>
        </tr>
        <tr>
            <g:if test="${StringUtils.isBlank(params["role"])}">
                <td>Role:</td><td><g:select name="role" from="${["Player", "Coach"]}" value="Player"/></td>
            </g:if>
            <g:else>
                <td>Role:</td><td><g:select name="role" from="${["Player", "Coach"]}" value="${params["role"]}"/></td>
            </g:else>
            <td>ASA Number:</td><td><g:textField name="asaNumber" required="true" value="${params["asaNumber"]}"/></td>
        </tr>
        </tbody>
    </table>
</div>