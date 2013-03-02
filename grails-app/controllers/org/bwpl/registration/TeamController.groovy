package org.bwpl.registration

import grails.plugins.springsecurity.Secured
import org.bwpl.registration.nav.NavItems
import org.bwpl.registration.upload.RegistrationUploader
import org.bwpl.registration.upload.UploadException
import org.bwpl.registration.utils.SecurityUtils
import org.bwpl.registration.validation.RegistrationStats
import org.bwpl.registration.validation.Status

class TeamController {

    NavItems nav
    SecurityUtils securityUtils
    RegistrationUploader registrationUploader

    @Secured(["ROLE_READ_ONLY"])
    def show = {

        Team team = Team.get(params.id)
        List<Team> teams = new ArrayList<Team>(team.club.teams)
        teams.sort{it.name}
        List<Registration> registrations = getRegistrations(team, params.rfilter)
        boolean hasAnyRegistrations = !team.club.registrations.isEmpty()
        boolean canUpdate = securityUtils.canUserUpdate(team.club)
        boolean isUserRegistrationSecretary = securityUtils.isCurrentUserRegistrationSecretary()
        boolean doDisplayValidateButton = canUpdate && !registrations.isEmpty() && !params.rfilter

        def model = [user: securityUtils.currentUser,
                     title: team.club.name,
                     navItems: nav.getNavItems(),
                     subNavItems: nav.getTeamNavItems(team),
                     club: team.club,
                     userClub: securityUtils.currentUserClub,
                     teams: teams,
                     hasAnyRegistrations: hasAnyRegistrations,
                     registrations: registrations,
                     stats: new RegistrationStats(registrations),
                     canUpdate: canUpdate,
                     isUserRegistrationSecretary: isUserRegistrationSecretary,
                     doDisplayValidateButton: doDisplayValidateButton]

        render(view: "/club/show", model: model)
    }

    // Secured by SecurityFilters.TeamControllerAccessFilter
    @Secured(["ROLE_CLUB_SECRETARY"])
    def upload = {

        Team team = Team.get(params.id)
        [user: securityUtils.currentUser,
         title: "Upload Registrations for ${team.name}",
         navItems: nav.getNavItems(),
         team: team]
    }

    // Secured by SecurityFilters.TeamControllerAccessFilter
    @Secured(["ROLE_CLUB_SECRETARY"])
    def uploadRegistrations = {

        Team team = Team.get(params.id)
        def f = request.getFile("registrations")
        try {
            registrationUploader.upload(team, f)
            flash.message = "Successfully uploaded registrations for ${team.name}"
        } catch (UploadException e) {
            flash.errors = e.message
        }
        redirect(action: "show", id: team.id.toString())
    }

    private static List<Registration> getRegistrations(Team team, String registrationFilter) {

        List<Registration> registrations =
            new ArrayList<Registration>(team.registrations.findAll{it.statusAsEnum != Status.DELETED})
        return registrations.sort{it.name}
    }
}
