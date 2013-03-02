package org.bwpl.registration.utils

import org.bwpl.registration.Club
import org.bwpl.registration.Team
import org.bwpl.registration.Registration

class CsvWriter {

    static String getCsvFieldNames() {
        return "$Club.csvFieldNames,$Team.csvFieldNames,$Registration.csvFieldNames"
    }

    static String getRegistrationsAsCsvString(Club club) {

        StringBuilder sb = new StringBuilder()
        List<Team> teams = new ArrayList<Team>(club.teams)
        teams.sort{it.name}
        teams.each { team ->

            List<Registration> registrations = new ArrayList<Registration>(team.registrations)
            registrations.sort{it.name}
            registrations.each { registration ->

                sb << club.toCsvString() << ","
                sb << team.toCsvString() << ","
                sb << registration.toCsvString() << "\n"
            }
        }
        return sb.toString().trim()
    }
}
