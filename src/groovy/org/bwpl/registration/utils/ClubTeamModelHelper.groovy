package org.bwpl.registration.utils

import org.bwpl.registration.Club
import org.bwpl.registration.Registration
import org.bwpl.registration.Team
import org.bwpl.registration.nav.NavItem
import org.bwpl.registration.nav.NavItems
import org.bwpl.registration.validation.RegistrationStats
import org.bwpl.registration.validation.Status

class ClubTeamModelHelper {

    private Object clubTeam
    private NavItems nav
    private SecurityUtils securityUtils
    private params

    private List<Team> teams
    private List<Registration> registrations

    static ClubTeamModelHelper getClubTeamModelHelper(Object clubTeam, NavItems nav, SecurityUtils securityUtils, def params) {

        ClubTeamModelHelper clubTeamModelHelper = new ClubTeamModelHelper()
        clubTeamModelHelper.clubTeam = clubTeam
        clubTeamModelHelper.nav = nav
        clubTeamModelHelper.securityUtils = securityUtils
        clubTeamModelHelper.params = params
        return clubTeamModelHelper
    }

    List<Team> getTeams() {

        if (teams != null) return teams
        teams = new ArrayList<Team>(getClub().teams)
        return teams
    }

    List<Registration> getRegistrations() {

        if (registrations != null) return registrations
        registrations = filterAndSortRegistrations(clubTeam.registrations, params.rfilter, params.sort)
        return registrations
    }

    Map<String, Object> getModel() {

        boolean hasAnyRegistrations = hasAnyNonDeletedRegistrations()
        boolean canUpdate = securityUtils.canUserUpdate(getClub())
        boolean isUserRegistrationSecretary = securityUtils.isCurrentUserRegistrationSecretary()
        boolean doDisplayValidateButton = canUpdate && hasAnyRegistrations && !params.rfilter

        return [user: securityUtils.currentUser,
                title: getClub().nameAndASAName,
                navItems: nav.getNavItems(),
                subNavItems: getSubNavItems(),
                club: getClub(),
                userClub: securityUtils.currentUserClub,
                teams: getTeams(),
                hasAnyRegistrations: hasAnyRegistrations,
                registrations: getRegistrations(),
                stats: new RegistrationStats(getRegistrations()),
                canUpdate: canUpdate,
                isUserRegistrationSecretary: isUserRegistrationSecretary,
                doDisplayValidateButton: doDisplayValidateButton]

    }

    private Club getClub() {

        if (clubTeam instanceof Club) return clubTeam
        else return clubTeam.club
    }

    private boolean hasAnyNonDeletedRegistrations() {

        int nonDeletedRegistrationsCount = getRegistrations().count{it.statusAsEnum != Status.DELETED}
        return nonDeletedRegistrationsCount > 0
    }

    private List<NavItem> getSubNavItems() {

        if (clubTeam instanceof Club) return nav.getClubNavItems(clubTeam, params.rfilter)
        else return nav.getTeamNavItems(clubTeam)
    }

    private static List<Registration> filterAndSortRegistrations(Collection<Registration> registrations, String registrationFilter, String sort) {

        if ("deleted".equals(registrationFilter)) {
            registrations = new ArrayList<Registration>(registrations.findAll{it.statusAsEnum == Status.DELETED})
        }
        else {
            registrations = new ArrayList<Registration>(registrations.findAll{it.statusAsEnum != Status.DELETED})
        }

        if ("firstname".equals(sort)) {
            return registrations.sort{it.name}
        }
        else if ("lastname".equals(sort)) {
            return registrations.sort{it.lastnameFirstname}
        }
        else {
            return registrations.sort{it.name}
        }
    }
}
