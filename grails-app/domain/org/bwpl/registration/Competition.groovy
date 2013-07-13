package org.bwpl.registration

class Competition {

    static mapping = {
        cache true
    }

    static constraints = {
        name(blank: false)
        urlName(blank: false, unique: true)
    }

    static hasMany = [divisions:Division]

    String name
    String urlName

    List<Division> getDivisions(boolean isMale) {

        List<Division> divs = new ArrayList<Division>(divisions)
        divs = divs.findAll{it.isMale == isMale}
        divs.sort{it.rank}
        return divs
    }

    void addDivision(boolean isMale) {

        int newRank = getNewRank(isMale)
        Division division = new Division()
        division.rank = newRank
        division.isMale = isMale
        division.name = Division.getDefaultName(newRank, isMale)
        addToDivisions(division)
    }

    void deleteDivision(boolean isMale) {

        if (divisions == null) return
        Division division = divisions.findAll{it.isMale == isMale}.max{it.rank}
        if (division == null) return
        removeFromDivisions(division)
    }

    private int getNewRank(boolean isMale) {

        if (divisions == null) return 1
        Division division = divisions.findAll{it.isMale == isMale}.max{it.rank}
        if (division == null) return 1
        return division.rank + 1
    }
}
