<div class="fieldcontain">
    <table class="bwpl-form-table">
        <tbody>
        <tr>
            <td>Firstname:</td><td><g:textField name="firstName" required="true" value=""/></td>
            <td>Lastname:</td><td><g:textField name="lastName" required="true"  value=""/></td>
        </tr>
        <tr>
            <td>Role:</td><td><g:select name="role" from="${["Player", "Coach"]}" value="Player"/></td>
            <td>ASA Number:</td><td><g:textField name="asaNumber" required="true" value=""/></td>
        </tr>
        </tbody>
    </table>
</div>