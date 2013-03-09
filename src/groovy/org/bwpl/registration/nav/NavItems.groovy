package org.bwpl.registration.nav

import org.bwpl.registration.Club
import org.bwpl.registration.Team
import org.bwpl.registration.utils.SecurityUtils

class NavItems {

    SecurityUtils securityUtils

    List<NavItem> getNavItems() {

        List<NavItem> navItems = []
        addUserClubNavItem(navItems)
        navItems << NavItem.clubList
        // navItems << NavItem.registrationsList
        navItems << NavItem.searchRegistrations
        navItems << NavItem.admin
        if (securityUtils.isCurrentUserRegistrationSecretary()) navItems << NavItem.securityManagementConsole
        return navItems
    }

    List<NavItem> getClubNavItems(Club club, String rfilter) {

        if (rfilter) return []
        if (club.registrations.isEmpty()) return []
        return [NavItem.getExportClubRegistrations(club.id)]
    }

    List<NavItem> getTeamNavItems(Team team) {

        List<NavItem> navItems = []
        if (team == null) return
        if ((!team.registrations.isEmpty()) && (team.club.teams.size() == 1)) {
            navItems << NavItem.getExportClubRegistrations(team.club.id)
        }
        if (securityUtils.canUserUpdate(team.club)) {
            navItems << NavItem.getUploadRegistrations(team.id)
            navItems << NavItem.getAddRegistration(team.id)
            navItems << NavItem.getDeleteAllRegistrations(team.id)
        }
        return navItems
    }

    List<NavItem> getRegistrationsNavItems() {

        List<NavItem> navItems = []
        if (securityUtils.isCurrentUserRegistrationSecretary()) {
            navItems << NavItem.uploadAllRegistrations
            navItems << NavItem.exportAllRegistrations
        }
        return navItems
    }

    List<NavItem> getRegistrationsTabs() {

        List<NavItem> navItems = []
        navItems << NavItem.registrationsTabAll
        navItems << NavItem.registrationsTabInvalid
        navItems << NavItem.registrationsTabDeleted
        return navItems
    }

    private void addUserClubNavItem(List<NavItem> navItems) {

        Club userClub = securityUtils.currentUserClub
        if (userClub == null) return
        navItems << NavItem.getUserClub(userClub.id, userClub.name)
    }
}
