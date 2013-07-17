<div>
    <ul class="bwpl-tabs">
        <g:if test="${(teams.size() > 1) && hasAnyNonDeletedClubRegistrations}">
            <g:if test="${(controllerName == "club") && (!params.rfilter)}">
                <li id="active">
                    <g:link elementId="current" controller="club" action="show" id="${club.id}">All</g:link>
                </li>
            </g:if>
            <g:else>
                <li>
                    <g:link controller="club" action="show" id="${club.id}">All</g:link>
                </li>
            </g:else>
        </g:if>
        <g:each in="${teams}" var="t">
            <g:if test="${(controllerName == "team") && (t.id.toString() == params.id)}">
                <li id="active">
                    <g:link elementId="current" controller="team" action="show" id="${t.id}">${t.name} (${t.gender})</g:link>
                </li>
            </g:if>
            <g:else>
                <li>
                    <g:link controller="team" action="show" id="${t.id}">${t.name} (${t.gender})</g:link>
                </li>
            </g:else>
        </g:each>
        <g:if test="${(controllerName == "club") && ("deleted" == params.rfilter)}">
            <li id="active">
                <g:link elementId="current" controller="club" action="show" id="${club.id}" params="[rfilter: 'deleted']">Deleted</g:link>
            </li>
        </g:if>
        <g:else>
            <li>
                <g:link controller="club" action="show" id="${club.id}" params="[rfilter: 'deleted']">Deleted</g:link>
            </li>
        </g:else>
    </ul>
</div>