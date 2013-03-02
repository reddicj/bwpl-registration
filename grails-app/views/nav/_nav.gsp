<g:if test="${!navItems.isEmpty()}">
    <div class="bwpl-nav">
        <ul style="width: 100%">
            <g:each in="${navItems}" var="navItem">
                <li><g:link controller="${navItem.controllerName}" action="${navItem.actionName}" params="${navItem.params}">${navItem.displayName}</g:link></li>
            </g:each>
        </ul>
    </div>
</g:if>