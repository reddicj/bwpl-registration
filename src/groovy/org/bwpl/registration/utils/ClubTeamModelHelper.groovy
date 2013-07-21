package org.bwpl.registration.utils

import org.bwpl.registration.Club
import org.bwpl.registration.Competition
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

    private Competition competition
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
        teams = getClub().getTeams(getCompetition())
        teams.sort{it.name}
        return teams
    }

    List<Registration> getRegistrations() {

        if (registrations != null) return registrations
        if (clubTeam instanceof Team) {
            return filterAndSortRegistrations(clubTeam.registrations, params.rfilter, params.sort)
        }
        else {
            return filterAndSortRegistrations(clubTeam.getRegistrations(getCompetition()), params.rfilter, params.sort)
        }
    }

    Map<String, Object> getModel() {

        boolean hasAnyNonDeletedClubRegistrations = this.hasAnyNonDeletedClubRegistrations()
        boolean canUpdate = securityUtils.canUserUpdate(getClub())
        boolean isUserRegistrationSecretary = securityUtils.isCurrentUserRegistrationSecretary()
        boolean doDisplayValidateButton = canUpdate && !getRegistrations().isEmpty() && !params.rfilter

        return [user: securityUtils.currentUser,
                title: getClub().nameAndASAName,
                navItems: nav.getNavItems(),
                subNavItems: getSubNavItems(),
                club: getClub(),
                userClub: securityUtils.currentUserClub,
                teams: getTeams(),
                hasAnyNonDeletedClubRegistrations: hasAnyNonDeletedClubRegistrations,
                registrations: getRegistrations(),
                stats: new RegistrationStats(getRegistrations()),
                canUpdate: canUpdate,
                isUserRegistrationSecretary: isUserRegistrationSecretary,
                doDisplayValidateButton: doDisplayValidateButton]

    }

    private Competition getCompetition() {

        if (competition != null) return competition
        competition = Competition.findByUrlName(params.competition)
        return competition
    }

    private Club getClub() {

        if (clubTeam instanceof Club) return clubTeam
        else return clubTeam.club
    }

    private boolean hasAnyNonDeletedClubRegistrations() {

        int nonDeletedRegistrationsCount = getClub().registrations.count{it.statusAsEnum != Status.DELETED}
        return nonDeletedRegistrationsCount > 0
    }

    private List<NavItem> getSubNavItems() {

        if (clubTeam instanceof Club) return nav.getClubNavItems(competition, clubTeam, params.rfilter)
        else return nav.getTeamNavItems(competition, clubTeam)
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
