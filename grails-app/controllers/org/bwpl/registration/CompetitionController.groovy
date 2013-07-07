package org.bwpl.registration

import grails.plugins.springsecurity.Secured
import org.apache.commons.lang.StringUtils
import org.bwpl.registration.utils.SecurityUtils

class CompetitionController {

    static defaultAction = "list"

    SecurityUtils securityUtils

    def list = {

        List<Competition> competitions = Competition.list()
        competitions.sort{it.name}
        [title: "Competitions", competitions:competitions]
    }

    def show = {

        Competition competition = Competition.get(params.id)
        if (competition.divisions.isEmpty()) {
            flash.errors = "There are no divisions or teams for the competition: $competition.name"
            redirect(action: "list")
        }
        else {
            List<Division> divisions = competition.getDivisions(true)
            if (divisions.isEmpty()) divisions = competition.getDivisions(false)
            redirect(controller: "division", action: "show", id: divisions[0].id)
        }
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def create = {

        render(view: "edit", model: [user: securityUtils.currentUser, title: "Add Competition"])
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def edit = {

        Competition competition = Competition.get(params.id)
        List<Division> mensDivisions = new ArrayList<Division>(competition.divisions.findAll{it.isMale})
        mensDivisions.sort{it.rank}
        List<Division> womensDivisions = new ArrayList<Division>(competition.divisions.findAll{!it.isMale})
        womensDivisions.sort{it.rank}

        [user: securityUtils.currentUser,
         title: "Edit Competition",
         competition: competition,
         mensDivisions: mensDivisions,
         womensDivisions: womensDivisions]
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def delete = {

        Competition competition = Competition.get(params.id)
        competition.delete()
        redirect(action: "list")
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def updateAndReturnToList = {

        updateCompetitionData(params)
        redirect(action: "list")
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def update = {

        Competition competition = updateCompetitionData(params)
        flash.message = "${competition.name} updated"
        redirect(action: "edit", id: competition.id)
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def addMensDivision = {

        Competition competition = addNewDivision(params, true)
        flash.message = "New mens division added"
        redirect(action: "edit", id: competition.id)
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def addWomensDivision = {

        Competition competition = addNewDivision(params, false)
        flash.message = "New womens division added"
        redirect(action: "edit", id: competition.id)
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def deleteMensDivision = {

        if (StringUtils.isEmpty(params.id)) {
            redirect(action: "create")
            return
        }
        Competition competition = Competition.get(params.id)
        if (competition.divisions.isEmpty()) {
            redirect(action: "edit", id: competition.id)
            return
        }
        Division division = competition.divisions.findAll{it.isMale}.max{it.rank}
        if (division.hasTeams()) {
            flash.errors = "Error cannot delete division with team data. First delete all team data from division."
            redirect(action: "edit", id: competition.id)
            return
        }
        competition.removeFromDivisions(division)
        division.delete()
        flash.message = "Mens division deleted"
        redirect(action: "edit", id: competition.id)
    }

    @Secured(["ROLE_REGISTRATION_SECRETARY"])
    def deleteWomensDivision = {

        if (StringUtils.isEmpty(params.id)) {
            redirect(action: "create")
            return
        }
        Competition competition = Competition.get(params.id)
        if (competition.divisions.isEmpty()) {
            redirect(action: "edit", id: competition.id)
            return
        }
        Division division = competition.divisions.findAll{!it.isMale}.max{it.rank}
        if (division.hasTeams()) {
            flash.errors = "Error cannot delete division with team data. First delete all team data from division."
            redirect(action: "edit", id: competition.id)
            return
        }
        competition.removeFromDivisions(division)
        division.delete()
        flash.message = "Womens division deleted"
        redirect(action: "edit", id: competition.id)
    }

    private static Competition addNewDivision(def params, boolean isMale) {

        Competition competition = null
        if (StringUtils.isEmpty(params.id)) {
            competition = updateCompetitionData(params)
        }
        else {
            competition = Competition.get(params.id)
        }
        competition.addDivision(isMale)
        competition.save()
        return competition
    }

    private static Competition updateCompetitionData(def params) {

        Competition competition = null
        if (StringUtils.isEmpty(params.id)) {
            competition = new Competition()
        }
        else {
            competition = Competition.get(params.id)
        }
        competition.name = params["competition-name"]
        competition.urlName = params["competition-urlName"]
        competition.divisions.each { division ->
            division.with {
                name = params["division-name-$division.id"]
            }
        }
        competition.save()
        return competition
    }
}
