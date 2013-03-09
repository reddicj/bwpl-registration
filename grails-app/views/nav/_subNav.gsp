<g:if test="${!subNavItems.isEmpty()}">
    <div class="bwpl-nav">
        <ul>
            <g:each in="${subNavItems}" var="navItem">
                <li><g:link controller="${navItem.controllerName}" action="${navItem.actionName}" params="${navItem.params}" onclick="${navItem.onclick}">${navItem.displayName}</g:link></li>
            </g:each>
        </ul>
    </div>
</g:if>