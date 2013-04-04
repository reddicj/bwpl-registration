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

    static belongsTo = [club:Club]
    static hasMany = [registrations:Registration]

    String name
    Boolean isMale
    int division

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
        sb << "\"$division\""
        return sb.toString()
    }

    String getNameAsMungedString() {
        return name.toLowerCase().replaceAll(" ", "-")
    }
}
