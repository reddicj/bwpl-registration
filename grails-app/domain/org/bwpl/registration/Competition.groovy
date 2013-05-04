package org.bwpl.registration

class Competition {

    static mapping = {
        cache true

    }

    static constraints = {
        name(blank: false)
        urlName(blank: false)
    }

    static hasMany = [divisions:Division, clubs:Club]

    String name
    String urlName
}
