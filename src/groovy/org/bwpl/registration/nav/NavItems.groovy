package org.bwpl.registration.nav

import org.bwpl.registration.Club
import org.bwpl.registration.Team
import org.bwpl.registration.utils.SecurityUtils
import org.bwpl.registration.Registration
import org.bwpl.registration.validation.Status

class NavItems {

    SecurityUtils securityUtils

    List<NavItem> getNavItems() {

        List<NavItem> navItems = []
        addUserClubNavItem(navItems)
        navItems << NavItem.clubList
        navItems << NavItem.searchRegistrations
        navItems << NavItem.admin
        if (securityUtils.isCurrentUserRegistrationSecretary()) navItems << NavItem.securityManagementConsole
        return navItems
    }

    List<NavItem> getClubNavItems(Club club, String rfilter) {

        List<NavItem> navItems = []
        if ("deleted" == rfilter) {
            Registration r = club.registrations.find { it.statusAsEnum == Status.DELETED }
            if (r != null) navItems << NavItem.getDeleteAllDeletedRegistrations(club.id)
            return navItems
        }
        if (club.registrations.isEmpty()) return navItems
        navItems << NavItem.getExportClubRegistrations(club.id)
        if (securityUtils.canUserUpdate(club)) navItems << NavItem.getASAEmail(club.id)
        return navItems
    }

    List<NavItem> getTeamNavItems(Team team) {

        List<NavItem> navItems = []
        if (team == null) return
        if (securityUtils.canUserUpdate(team.club)) {
            navItems << NavItem.getUploadRegistrations(team.id)
            navItems << NavItem.getAddRegistration(team.id)
            if (!team.registrations.isEmpty()) navItems << NavItem.getDeleteAllRegistrations(team.id)
        }
        if ((!team.registrations.isEmpty()) && (team.club.teams.size() == 1)) {
            navItems << NavItem.getExportClubRegistrations(team.club.id)
            navItems << NavItem.getASAEmail(team.club.id)
        }
        return navItems
    }

    private void addUserClubNavItem(List<NavItem> navItems) {

        Club userClub = securityUtils.currentUserClub
        if (userClub == null) return
        navItems << NavItem.getUserClub(userClub.id, userClub.name)
    }
}
