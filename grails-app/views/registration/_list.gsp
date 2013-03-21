<%@ page import="org.bwpl.registration.validation.Status" %>
<%@ page import="org.bwpl.registration.asa.ASAMemberDataRetrieval" %>
<div id="registrations">
    <table class="bwpl-table">
        <thead>
        <tr>
            <g:if test="${controllerName in ["club", "team"]}">
                <th><g:link controller="${controllerName}" action="show" id="${params.id}" params="${[sort: 'firstname', rfilter: params.rfilter]}">Firstname</g:link></th>
                <th><g:link controller="${controllerName}" action="show" id="${params.id}" params="${[sort: 'lastname', rfilter: params.rfilter]}">Lastname</g:link></th>
            </g:if>
            <g:else>
                <th>Firstname</th>
                <th>Lastname</th>
            </g:else>
            <th>ASA No</th>
            <th>Role</th>
            <th>Registration Date</th>
            <g:if test="${controllerName == "registration"}">
                <th>Club</th>
            </g:if>
            <g:if test="${controllerName in ["club", "registration"]}">
                <th>Team</th>
            </g:if>
            <th>Status</th>
            <g:if test="${canUpdate}">
                <th>Actions</th>
            </g:if>
        </tr>
        </thead>
        <tbody>
        <g:each in="${registrations}" var="r">
            <tr class="${r.statusAsEnum.toString().toLowerCase()}">
                <td><g:link controller="registration" action="show" id="${r.id}">${r.firstName}</g:link></td>
                <td><g:link controller="registration" action="show" id="${r.id}">${r.lastName}</g:link></td>
                <td><a href="${ASAMemberDataRetrieval.ASA_MEMBERSHIP_CHECK_URL}?${ASAMemberDataRetrieval.ASA_NUMBER_PARAMETER_NAME}=${r.asaNumber}">${r.asaNumber}</a></td>
                <td>${r.role}</td>
                <td>${r.registrationDateAsString}</td>
                <g:if test="${controllerName == "registration"}">
                    <td><g:link controller="club" action="show" id="${r.team.club.id}">${r.team.club.name}</g:link></td>
                </g:if>
                <g:if test="${controllerName in ["club", "registration"]}">
                    <td><g:link controller="team" action="show" id="${r.team.id}">${r.team.name} (${r.team.gender})</g:link></td>
                </g:if>
                <td>${r.statusAsEnum.toString()}</td>
                <g:if test="${canUpdate}">
                    <td>
                        <g:if test="${(r.statusAsEnum == Status.DELETED) && (isUserRegistrationSecretary || r.canUpdate())}">
                            <g:link controller="registration" action="undelete" id="${r.id}" params="[targetUri: (request.forwardURI - request.contextPath)]" onclick="return confirm('Are you sure you want to undelete?');">Undelete</g:link> |
                            <g:link controller="registration" action="deleteDeleted" id="${r.id}" onclick="return confirm('Are you sure you want to remove permanently?');">Remove</g:link>
                        </g:if>
                        <g:elseif test="${(r.statusAsEnum != Status.DELETED) && (isUserRegistrationSecretary || r.canUpdate())}">
                            <g:link controller="registration" action="edit" id="${r.id}">Edit</g:link> |
                            <g:link controller="registration" action="delete" id="${r.id}" params="[targetUri: (request.forwardURI - request.contextPath)]" onclick="return confirm('Are you sure you want to delete?');">Delete</g:link> |
                            <g:link controller="registration" action="validate" id="${r.id}" params="[targetUri: (request.forwardURI - request.contextPath)]">Validate</g:link>
                        </g:elseif>
                    </td>
                </g:if>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>