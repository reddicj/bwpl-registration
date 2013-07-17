package org.bwpl.registration.nav

import org.bwpl.registration.Club
import org.bwpl.registration.Registration
import org.bwpl.registration.Team
import org.bwpl.registration.utils.SecurityUtils
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
            if (doDisplayDeleteAllDeleted(club)) {
                navItems << NavItem.getUndeleteAllDeletedRegistrations(club.id)
                navItems << NavItem.getDeleteAllDeletedRegistrations(club.id)
            }
            return navItems
        }
        if (!hasAnyNonDeletedRegistrations(club.registrations)) return navItems
        navItems << NavItem.getExportClubRegistrations(club.id)
        if (doDisplayASAEmail(club)) navItems << NavItem.getASAEmail(club.id)
        return navItems
    }

    List<NavItem> getTeamNavItems(Team team) {

        List<NavItem> navItems = []
        if (team == null) return
        if (securityUtils.canUserUpdate(team.club)) {
            navItems << NavItem.getUploadRegistrations(team.id)
            navItems << NavItem.getAddRegistration(team.id)
            if (hasAnyNonDeletedRegistrations(team.registrations)) navItems << NavItem.getDeleteAllRegistrations(team.id)
        }
        if ((hasAnyNonDeletedRegistrations(team.registrations)) && (team.club.teams.size() == 1)) {
            navItems << NavItem.getExportClubRegistrations(team.club.id)
            if (doDisplayASAEmail(team.club)) navItems << NavItem.getASAEmail(team.club.id)
        }
        if ((hasAnyNonDeletedRegistrations(team.registrations)) && (team.club.teams.size() > 1)) {
            navItems << NavItem.getExportTeamRegistrations(team.id)
        }
        return navItems
    }

    private static boolean hasAnyNonDeletedRegistrations(Collection<Registration> registrations) {

        if (registrations == null) return false
        int countOfNonDeleted = registrations.count {it.statusAsEnum != Status.DELETED}
        return countOfNonDeleted > 0
    }

    private boolean doDisplayASAEmail(Club club) {

        if (!securityUtils.canUserUpdate(club)) return false
        return !club.getRegistrations(Status.INVALID).isEmpty()
    }

    private boolean doDisplayDeleteAllDeleted(Club club) {

        if (!securityUtils.canUserUpdate(club)) return false
        return !club.getRegistrations(Status.DELETED).isEmpty()
    }

    private void addUserClubNavItem(List<NavItem> navItems) {

        Club userClub = securityUtils.currentUserClub
        if (userClub == null) return
        navItems << NavItem.getUserClub(userClub.id, userClub.name)
    }
}
