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

        List<NavItem> navItems = []
        if ("deleted" == rfilter) {
            navItems << NavItem.getDeleteAllDeletedRegistrationsPermanent(club.id)
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
            navItems << NavItem.getDeleteAllRegistrations(team.id)
        }
        if ((!team.registrations.isEmpty()) && (team.club.teams.size() == 1)) {
            navItems << NavItem.getExportClubRegistrations(team.club.id)
            navItems << NavItem.getASAEmail(team.club.id)
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
