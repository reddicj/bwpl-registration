package org.bwpl.registration

import org.apache.commons.lang.StringUtils
import org.bwpl.registration.validation.Status

class Club {

    static final String csvFieldNames = '"Club name"'

    static mapping = {
        cache true
        teams cache: "nonstrict-read-write", cascade: "all-delete-orphan"
    }

    static constraints = {
        name(blank: false)
        asaName(blank: false)
    }

    static hasMany = [teams:Team, secretaries:User]

    String name
    String asaName

    List<Team> getTeams(Competition competition) {

        List<Team> result = new ArrayList<Team>(teams.findAll {it.division.competition.id == competition.id})
        result.sort{it.name}
        return result
    }

    boolean hasTeam(String name) {

        for (Team team : teams) {
            if (StringUtils.equals(name, team.name)) {
                return true
            }
        }
        return false
    }

    boolean hasSecretary(String username) {

        for (User secretary : secretaries) {
            if (StringUtils.equals(username, secretary.username)) {
                return true
            }
        }
        return false
    }

    Set<Registration> getRegistrations(Competition competition) {

        def query = Registration.where { team.club == this }
        query = query.where { team.division.competition == competition }
        return new HashSet<Registration>(query.list())
    }

    Set<Registration> getRegistrations(Competition competition, Status theStatus) {

        def query = Registration.where { team.club == this }
        query = query.where { team.division.competition == competition }
        query = query.where { status == theStatus.toString() }
        return new HashSet<Registration>(query.list())
    }

    @Override
    String toString() {
        return name
    }

    String toCsvString() {
        return "\"$name\""
    }

    String getNameAsMungedString() {
        return name.toLowerCase().replaceAll(" ", "-")
    }

    String getNameAndASAName() {

        if (name == asaName) return name
        else return "$name ($asaName)"
    }

    String getRegistrationsAsCsvString(Competition competition, boolean doWriteHeader = false) {

        List<Team> teams = getTeams(competition)
        StringBuilder sb = new StringBuilder()
        if (doWriteHeader) {
            sb << "$Club.csvFieldNames,$Team.csvFieldNames,$Registration.csvFieldNames\n"
        }
        teams.each { team ->
            String teamRegistrationsAsCsvString = team.getRegistrationsAsCsvString()
            if (StringUtils.isNotBlank(teamRegistrationsAsCsvString)) {
                sb << teamRegistrationsAsCsvString << "\n"
            }
        }
        return sb.toString().trim()
    }
}
