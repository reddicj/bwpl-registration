<g:if test="${(navItems != null) && !navItems.isEmpty()}">
    <div class="bwpl-nav">
        <ul style="width: 100%">
            <g:each in="${navItems}" var="navItem">
                <li><g:link controller="${navItem.controllerName}" action="${navItem.actionName}" params="${navItem.params}">${navItem.displayName}</g:link></li>
            </g:each>
            <li><a href="https://docs.google.com/document/d/1Z--xRiC0WYV7L6FiNs92OoHiRUTwH-TYSP2HY8pcWww/edit?usp=sharing" target="_blank">User Guide</a></li>
        </ul>
    </div>
</g:if>