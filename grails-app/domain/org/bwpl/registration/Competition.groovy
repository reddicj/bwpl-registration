package org.bwpl.registration

class Competition {

    static mapping = {
        cache true

    }

    static constraints = {
        name(blank: false)
        urlName(blank: false, unique: true)
    }

    static hasMany = [divisions:Division, clubs:Club]

    String name
    String urlName

    void addDivision(boolean isMale) {

        int newRank = 1
        if (!divisions.isEmpty()) {
            newRank = divisions.findAll{it.isMale == isMale}.max{it.rank}.rank + 1
        }
        Division division = new Division()
        division.rank = newRank
        division.isMale = isMale
        division.name = Division.getDefaultName(newRank, isMale)
        addToDivisions(division)
    }

    void deleteDivision(boolean isMale) {

        if (divisions.isEmpty()) return
        Division division = divisions.findAll{it.isMale == isMale}.max{it.rank}
        removeFromDivisions(division)
    }
}
