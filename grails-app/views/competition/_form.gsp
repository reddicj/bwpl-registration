<%@ page import="org.bwpl.registration.Competition" %>
<div class="fieldcontain">
    <table class="bwpl-form-table">
        <thead>
        <tr>
            <th>Name</th><th>URL name</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td><g:textField name="competition-name" required="true" value="${competition?.name}" size="40"/></td>
            <td><g:textField name="competition-urlName" required="true" value="${competition?.urlName}"/></td>
        </tr>
        </tbody>
    </table>
</div>
<br/>
<div class="fieldcontain">
    <table class="bwpl-form-table" border="1">
        <tr>
            <td>
                <h1>Mens Divisions</h1>
                <table class="bwpl-form-table">
                    <thead>
                    <tr>
                        <th>Rank</th><th>Name</th>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${mensDivisions}" var="division">
                        <tr>
                            <td>${division.rank}</td>
                            <td><g:textField name="division-name-$division.id" value="${division.name}"/></td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </td>
            <td>
                <h1>Womens Divisions</h1>
                <table class="bwpl-form-table">
                    <thead>
                    <tr>
                        <th>Rank</th><th>Name</th>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${womensDivisions}" var="division">
                        <tr>
                            <td>${division.rank}</td>
                            <td><g:textField name="division-name-$division.id" value="${division.name}"/></td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </td>
        </tr>
        <tr>
            <td>
                <g:actionSubmit action="addMensDivision" value="Add" class="buttons"/>
                <g:if test="${!mensDivisions.isEmpty()}">
                    <g:actionSubmit action="deleteMensDivision" value="Delete" class="buttons"/>
                </g:if>
            </td>
            <td>
                <g:actionSubmit action="addWomensDivision" value="Add" class="buttons"/>
                <g:if test="${!womensDivisions.isEmpty()}">
                    <g:actionSubmit action="deleteWomensDivision" value="Delete" class="buttons"/>
                </g:if>
            </td>
        </tr>
    </table>
</div>