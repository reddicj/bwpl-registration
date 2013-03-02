<div>
    <ul class="bwpl-tabs">
        <g:each in="${tabItems}" var="tabItem">
            <g:if test="${params.rfilter == tabItem.params.rfilter}">
                <li id="active">
                    <g:link elementId="current" controller="${tabItem.controllerName}" action="${tabItem.actionName}" params="${tabItem.params}">${tabItem.displayName}</g:link>
                </li>
            </g:if>
            <g:else>
                <li>
                    <g:link controller="${tabItem.controllerName}" action="${tabItem.actionName}" params="${tabItem.params}">${tabItem.displayName}</g:link>
                </li>
            </g:else>
        </g:each>
    </ul>
</div>