package org.bwpl.registration

import org.bwpl.registration.utils.SecurityUtils

class SecurityFilters {

    SecurityUtils securityUtils

    def filters = {

        ClubControllerAccessFilter(controller: "club", action: "(asaemail|deleteDeletedRegistrations)") {

            before = {

                Club club = getClubForClubContext(params)

                if (!securityUtils.canUserUpdate(club)) {
                    redirect(controller: "login", action: "auth")
                    return false
                }
            }
        }

        TeamControllerAccessFilter(controller: "team", action: "(upload|uploadRegistrations|deleteRegistrations)") {

            before = {

                Team team = getTeamForTeamContext(params)

                if (!securityUtils.canUserUpdate(team.club)) {
                    redirect(controller: "login", action: "auth")
                    return false
                }
            }
        }

        RegistrationAccessFilter(controller: "registration", action: "*", actionExclude: "(admin|list|search|show|export)") {

            before = {

                Club club = getClubForRegistrationContext(params)

                if (!securityUtils.canUserUpdate(club)) {
                    redirect(controller: "login", action: "auth")
                    return false
                }
            }
        }

        SecurityManagementConsole(controller: "(user|role|registrationCode|securityInfo)", action: "*") {

            before = {

                if (!securityUtils.isCurrentUserRegistrationSecretary()) {
                    redirect(controller: "login", action: "auth")
                    return false
                }
            }
        }
    }

    private static Club getClubForClubContext(def params) {

        Club club = null
        if (params.id) {
            club = Club.get(params.id)
        }
        if (club == null) {
            throw new IllegalArgumentException("Error club is null")
        }
        return club
    }

    private static Team getTeamForTeamContext(def params) {

        Team team = null
        if (params.id) {
            team = Team.get(params.id)
        }
        if (team == null) {
            throw new IllegalStateException("Error team is null")
        }
        return team
    }

    private static Club getClubForRegistrationContext(def params) {

        Club club = null
        if (params.clubId) {
            club = Club.get(params.clubId)
        }
        else if (params.teamId) {
            club = Team.get(params.teamId).club
        }
        else if (params.id) {
            club = Registration.get(params.id).team.club
        }
        return club
    }
}