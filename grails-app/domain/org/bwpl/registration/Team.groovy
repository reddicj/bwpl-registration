package org.bwpl.registration

class Team {

    static final String csvFieldNames = "\"Team name\",\"Gender\",\"Division\""

    static mapping = {
        cache "nonstrict-read-write"
        registrations cache: true, lazy: false
    }

    static constraints = {
        name(blank: false)
    }

    static belongsTo = [division:Division, club:Club]
    static hasMany = [registrations:Registration]

    String name
    Boolean isMale

    static String getTeamsAsCsvString() {

        List<Team> teams = list()
        teams.sort{it.name}
        StringBuilder sb = new StringBuilder()
        sb << Club.csvFieldNames << "," << csvFieldNames << "\n"
        teams.each { team ->
            sb << team.club.toCsvString() << ","
            sb << team.toCsvString() << "\n"
        }
        return sb.toString().trim()
    }

    String getGender() {

        if (isMale) return "M"
        else return "F"
    }

    String getTeamNameGender() {

        if (isMale) return "Men"
        else return "Women"
    }

    String getNameAndGender() {
        return "$name ($gender)"
    }

    @Override
    String toString() {
        return name
    }

    String toCsvString() {

        StringBuilder sb = new StringBuilder()
        sb << "\"$name\","
        sb << "\"$gender\","
        sb << "\"$division.rank\""
        return sb.toString()
    }

    String getNameAsMungedString() {
        return name.toLowerCase().replaceAll(" ", "-")
    }

    String getRegistrationsAsCsvString(boolean doWriteHeader = false) {

        List<Registration> registrations = new ArrayList<Registration>(registrations)
        registrations.sort{it.name}
        StringBuilder sb = new StringBuilder()
        if (doWriteHeader) {
            sb << "$Club.csvFieldNames,$Team.csvFieldNames,$Registration.csvFieldNames\n"
        }
        registrations.each { registration ->
            sb << club.toCsvString() << ","
            sb << toCsvString() << ","
            sb << registration.toCsvString() << "\n"
        }
        return sb.toString().trim()
    }
}
