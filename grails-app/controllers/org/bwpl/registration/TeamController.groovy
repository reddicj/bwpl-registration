package org.bwpl.registration

import grails.plugins.springsecurity.Secured
import org.bwpl.registration.nav.NavItems
import org.bwpl.registration.upload.RegistrationUploader
import org.bwpl.registration.upload.UploadException
import org.bwpl.registration.utils.BwplDateTime
import org.bwpl.registration.utils.ClubTeamModelHelper
import org.bwpl.registration.utils.SecurityUtils
import org.bwpl.registration.validation.Action
import org.bwpl.registration.validation.Status

class TeamController {

    NavItems nav
    SecurityUtils securityUtils
    RegistrationUploader registrationUploader

    @Secured(["ROLE_READ_ONLY"])
    def show = {

        Team team = Team.get(params.id)
        ClubTeamModelHelper teamModelHelper = ClubTeamModelHelper.getClubTeamModelHelper(team, nav, securityUtils, params)
        render(view: "/club/show", model: teamModelHelper.model)
    }

    @Secured(["ROLE_READ_ONLY"])
    def export = {

        Team team = Team.get(params.id)
        String dateTimeStamp = BwplDateTime.now.toFileNameDateTimeString()
        String fileName = "bwpl-registrations-${team.nameAsMungedString}-${dateTimeStamp}.csv"
        response.setHeader("Content-disposition", "attachment; filename=$fileName")
        response.contentType = "text/csv"
        response.outputStream << team.getRegistrationsAsCsvString(true) << "\n"
        response.flushBuffer()
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

    // Secured by SecurityFilters.TeamControllerAccessFilter
    @Secured(["ROLE_CLUB_SECRETARY"])
    def deleteRegistrations = {

        Team team = Team.get(params.id)
        int countOfDeleted = 0
        int countOfNotDeleted = 0
        team.registrations.each { r ->

            if (r.canUpdate()) {
                r.updateStatus(securityUtils.currentUser, Action.DELETED, Status.DELETED, "")
                r.save()
                countOfDeleted++
            }
            else countOfNotDeleted++
        }

        StringBuilder sb = new StringBuilder()
        sb << "$countOfDeleted registrations for $team.name ($team.club.name) deleted. "
        if (countOfNotDeleted > 0) {
            sb << "$countOfNotDeleted registrations for $team.name ($team.club.name) not deleted. "
            sb << "You cannot delete registrations that have already been validated."
        }
        flash.message = sb.toString().trim()
        redirect(action: "show", id: team.id)
    }
}
