<div>
    <ul class="bwpl-tabs">
        <g:if test="${!divisions.isEmpty()}">
        <g:each in="${divisions}" var="division">
            <g:if test="${division.id.toString() == params.id}">
                <li id="active">
                    <g:link elementId="current" controller="division" action="show" id="${division.id}">${division.name}</g:link>
                </li>
            </g:if>
            <g:else>
                <li>
                    <g:link controller="division" action="show" id="${division.id}">${division.name}</g:link>
                </li>
            </g:else>
        </g:each>
        </g:if>
    </ul>
</div>