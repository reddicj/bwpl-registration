package org.bwpl.registration.utils

import org.bwpl.registration.Club
import org.bwpl.registration.Team
import org.bwpl.registration.Registration
import org.apache.commons.lang.StringUtils

class CsvWriter {

    static String getCsvFieldNames() {
        return "$Club.csvFieldNames,$Team.csvFieldNames,$Registration.csvFieldNames"
    }

    static String getAllRegistrationsAsCsvString() {

        List<Club> clubs = Club.list()
        clubs.sort{it.name}
        StringBuilder sb = new StringBuilder()
        clubs.each { club ->
            String clubRegistrationsAsCsvString = getClubRegistrationsAsCsvString(club)
            if (StringUtils.isNotBlank(clubRegistrationsAsCsvString)) {
                sb << clubRegistrationsAsCsvString << "\n"
            }
        }
        return sb.toString().trim()
    }

    static String getClubRegistrationsAsCsvString(Club club) {

        StringBuilder sb = new StringBuilder()
        List<Team> teams = new ArrayList<Team>(club.teams)
        teams.sort{it.name}
        teams.each { team ->
            String teamRegistrationsAsCsvString = getTeamRegistrationsAsCsvString(team)
            if (StringUtils.isNotBlank(teamRegistrationsAsCsvString)) {
                sb << teamRegistrationsAsCsvString << "\n"
            }
        }
        return sb.toString().trim()
    }

    static String getTeamRegistrationsAsCsvString(Team team) {

        StringBuilder sb = new StringBuilder()
        List<Registration> registrations = new ArrayList<Registration>(team.registrations)
        registrations.sort{it.name}
        registrations.each { registration ->

            sb << team.club.toCsvString() << ","
            sb << team.toCsvString() << ","
            sb << registration.toCsvString() << "\n"
        }
        return sb.toString().trim()
    }
}
