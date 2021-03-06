<%@ page import="org.bwpl.registration.validation.Status" %>
<%@ page import="org.bwpl.registration.utils.BwplDateTime" %>
<div class="fieldcontain">
    <table class="bwpl-form-table">
        <tbody>
        <tr>
            <g:if test="${r?.canUpdate()}">
                <td>Firstname:</td><td><g:textField name="firstName" required="true" value="${r?.firstName}"/></td>
                <td>Lastname:</td><td><g:textField name="lastName" required="true"  value="${r?.lastName}"/></td>
            </g:if>
            <g:else>
                <td>Firstname:</td><td><input type="text" name="firstName" value="${r?.firstName}" readonly/></td>
                <td>Lastname:</td><td><input type="text" name="lastName" value="${r?.lastName}" readonly/></td>
            </g:else>
        </tr>
        <tr>
            <g:if test="${r?.canUpdate()}">
                <td>Role:</td><td><g:select name="role" from="${["Player", "Coach"]}" value="${r?.role}"/></td>
                <td>ASA Number:</td><td><g:textField name="asaNumber" required="true" value="${r?.asaNumber}"/></td>
            </g:if>
            <g:else>
                <td>Role:</td><td><input type="text" name="role" value="${r?.role}" readonly/></td>
                <td>ASA Number:</td><td><input type="text" name="asaNumber" value="${r?.asaNumber}" readonly/></td>
            </g:else>
        </tr>
        </tbody>
    </table>
    <sec:ifAllGranted roles="ROLE_REGISTRATION_SECRETARY">
        <hr/>
        <h1>Update Registration Status</h1>
        <table class="bwpl-form-table">
            <tbody>
                <tr>
                    <td>Status:</td><td><g:select name="status" from="${["New", "Valid", "Invalid", "Deleted"]}" value="${r?.statusAsEnum.toString()}"/></td>
                </tr>
                <tr>
                    <td>Details:</td><td colspan="3"><g:textArea name="statusNotes" rows="3" cols="70"/></td>
                </tr>
                <tr>
                    <td>E.g.</td><td>ASA confirmation email received ${BwplDateTime.now.toDateString()}.</td>
                </tr>
            </tbody>
        </table>
    </sec:ifAllGranted>
</div>