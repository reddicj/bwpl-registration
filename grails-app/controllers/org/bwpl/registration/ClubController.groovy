package org.bwpl.registration

import grails.plugins.springsecurity.Secured
import org.apache.commons.lang.RandomStringUtils
import org.apache.commons.lang.StringUtils
import org.bwpl.registration.email.ASAEmail
import org.bwpl.registration.nav.NavItems
import org.bwpl.registration.upload.TeamUploader
import org.bwpl.registration.upload.UploadException
import org.bwpl.registration.utils.BwplDateTime
import org.bwpl.registration.utils.ClubTeamModelHelper
import org.bwpl.registration.utils.SecurityUtils
import org.bwpl.registration.validation.Action
import org.bwpl.registration.validation.Status

class ClubController {

    NavItems nav
    SecurityUtils securityUtils

    def list = {

        List<Club> clubs = Club.list()
        clubs.sort{it.name}
        int maxListSize = getMaxListSize(clubs.size(), 3)
        List<List<Club>> clubLists = clubs.collate(maxListSize)

        [user: securityUtils.currentUser,
         navItems: nav.getNavItems(),
         clubLists: clubLists,
         userClub: securityUtils.currentUserClub]
    }

    @Secured(["ROLE_READ_ONLY"])
    def show = {

        Club club = Club.get(params.id)
        ClubTeamModelHelper clubModelHelper = ClubTeamModelHelper.getClubTeamModelHelper(club, nav, securityUtils, params)

        if ("deleted" == params.rfilter) {
            return clubModelHelper.model
        }
        else if ((clubModelHelper.teams.size() > 1) && (!clubModelHelper.registrations.isEmpty())) {
            return clubModelHelper.model
        }
        else {
            redirect([controller: "team", action: "show", id: clubModelHelper.teams[0].id, params: [competition: params.competition]])
        }
    }

    @Secured(["ROLE_READ_ONLY"])
    def export = {

        Competition competition = Competition.findByUrlName(params.competition)
        Club club = Club.get(params.id)
        String dateTimeStamp = BwplDateTime.now.toFileNameDateTimeString()
        String fileName = "bwpl-registrations-${club.nameAsMungedString}-${dateTimeStamp}.csv"
        response.setHeader("Content-disposition", "attachment; filename=$fileName")
        response.contentType = "text/csv"
        response.outputStream << club.getRegistrationsAsCsvString(competition, true) << "\n"
        response.flushBuffer()
    }

    // Secured by SecurityFilters.ClubControllerAccessFilter
    @Secured(["ROLE_CLUB_SECRETARY"])
    def asaemail = {

        Club club = Club.get(params.id)
        Competition competition = Competition.findByUrlName(params.competition)
        ASAEmail asaEmail = new ASAEmail()
        asaEmail.currentUser = securityUtils.currentUser
        asaEmail.club = club
        asaEmail.competition = competition
        [navItems: nav.getNavItems(), email: asaEmail]
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def upload = {
        [user: securityUtils.currentUser,
         title: "Upload Club data",
         navItems: nav.getNavItems()]
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def uploadClubData = {

        try {
            Competition competition = Competition.findByUrlName(params.competition)
            def f = request.getFile("clubs")
            new TeamUploader().upload(competition, f)
            flash.message = "Successfully uploaded team data"
        }
        catch (UploadException e) {
            flash.errors = e.message
        }
        redirect(controller: "registration", action: "admin", params: [competition: params.competition])
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def create = {
        render(view: "edit", model: [user: securityUtils.currentUser,
                                     title: "Add Club",
                                     navItems: nav.getNavItems()])
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def edit = {

        Club club = Club.get(params.id)
        [user: securityUtils.currentUser,
         title: "Edit Club",
         navItems: nav.getNavItems(),
         club: club]
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def updateAndReturnToList = {

        updateData(params)
        redirect(action: "list")
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def update = {

        Club club = updateData(params)
        flash.message = "${club.name} updated"
        redirect(action: "edit", params: [id: club.id])
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def delete = {

        Club c = Club.get(params.id)
        c.delete()
        redirect(action: "list")
    }

    // Secured by SecurityFilters.ClubControllerAccessFilter
    @Secured(["ROLE_CLUB_SECRETARY"])
    def deleteDeletedRegistrations = {

        Club club = Club.get(params.id)
        Competition competition = Competition.findByUrlName(params.competition)
        int countOfDeleted = 0
        club.getRegistrations(competition).each { r ->
            if (r.statusAsEnum == Status.DELETED) {
                r.team.removeFromRegistrations(r)
                r.delete()
                countOfDeleted++
            }
        }
        flash.message = "$countOfDeleted registrations for $club.name removed permanently."
        redirect(action: "show", id: club.id, params: [rfilter: "deleted"])
    }

    // Secured by SecurityFilters.ClubControllerAccessFilter
    @Secured(["ROLE_CLUB_SECRETARY"])
    def undeleteDeletedRegistrations = {

        Club club = Club.get(params.id)
        Competition competition = Competition.findByUrlName(params.competition)
        int countOfUndeleted = 0
        int countOfNotUndeleted = 0
        club.getRegistrations(competition).each { r ->
            if (r.statusAsEnum == Status.DELETED) {
                if (r.canUpdate()) {
                    r.updateStatus(securityUtils.currentUser, Action.UNDELETED, Status.NEW, "")
                    r.save()
                    countOfUndeleted++
                }
                else countOfNotUndeleted++
            }
        }

        StringBuilder sb = new StringBuilder()
        sb << "$countOfUndeleted registrations for $club.name undeleted. "
        if (countOfNotUndeleted > 0) {
            sb << "$countOfNotUndeleted registrations for $club.name not undeleted. "
            sb << "You cannot undelete registrations that have already been validated."
        }
        flash.message = sb.toString().trim()
        redirect(action: "show", id: club.id, params: [rfilter: "deleted"])
    }

    private static Club updateData(def params) {

        Club club = null
        List<Team> teams = null
        List<User> secretaries = null

        if (StringUtils.isEmpty(params.id)) {
            club = new Club()
            teams = []
            secretaries = []
        }
        else {
            club = Club.get(params.id)
            teams = new ArrayList<Team>(club.teams)
            secretaries = new ArrayList<User>(club.secretaries)
        }

        club.name = params["club-name"]
        club.asaName = params["club-asaName"]

        secretaries.each { sec ->
            if (params["secretary-delete-$sec.id"]) {
                club.removeFromSecretaries(sec)
            }
            else {
                sec.with {
                    firstname = params["secretary-firstname-$sec.id"]
                    lastname = params["secretary-lastname-$sec.id"]
                    username = params["secretary-email-$sec.id"]
                }
            }
        }
        if (StringUtils.isNotBlank(params["secretary-email-new"])) {
            User sec = new User()
            sec.with {
                firstname = params["secretary-firstname-new"]
                lastname = params["secretary-lastname-new"]
                username = params["secretary-email-new"]
                password = RandomStringUtils.random(10, true, true)
                accountLocked = false
                passwordExpired = false
                enabled = true
            }
            sec.save()
            Role clubSecRole = Role.findByAuthority("ROLE_CLUB_SECRETARY")
            Role readOnlyRole = Role.findByAuthority("ROLE_READ_ONLY")
            UserRole.create(sec, clubSecRole)
            UserRole.create(sec, readOnlyRole)
            club.addToSecretaries(sec)
        }

        teams.each { t ->
            if (params["team-delete-$t.id"]) {
                club.removeFromTeams(t)
            }
            else {
                t.name = params["team-name-$t.id"]
                t.isMale = "M".equals(params["team-gender-$t.id"])
                //TODO t.division = Integer.parseInt(params["team-division-$t.id"])
            }
        }
        if (StringUtils.isNotBlank(params["team-name-new"])) {
            Team newTeam = new Team()
            newTeam.name = params["team-name-new"]
            newTeam.isMale = "M".equals(params["team-gender-new"])
            //TODO newTeam.division = Integer.parseInt(params["team-division-new"])
            club.addToTeams(newTeam)
        }

        club.save()
        return club
    }

    private static int getMaxListSize(int countOfClubs, int countOfLists) {

        int maxListSize = countOfClubs.intdiv(countOfLists)
        int remainder = countOfClubs.mod(countOfLists)
        if (remainder > 0) return maxListSize + 1
        return maxListSize
    }
}
