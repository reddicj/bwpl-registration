package org.bwpl.registration

class Division {

    static mapping = {
        cache true
    }

    static constraints = {
        rank(min: 1, max: 20)
        name(blank: false)
    }

    static belongsTo = [competition:Competition]
    static hasMany = [teams:Team]

    int rank
    String name
    boolean isMale

    static String getDefaultName(int rank, boolean isMale) {

        if (isMale) return "Mens Div $rank"
        else return "Womens Div $rank"
    }

    boolean equals(o) {

        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Division division = (Division) o

        if (isMale != division.isMale) return false
        if (rank != division.rank) return false

        return true
    }

    int hashCode() {
        return rank
    }
}
