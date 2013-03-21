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

    Set<Registration> getRegistrations() {

        Set<Registration> registrations = new HashSet<Registration>()
        teams.each {
            registrations.addAll(it.registrations)
        }
        return registrations
    }

    Set<Registration> getRegistrations(Status status) {

        Set<Registration> registrations = registrations
        return registrations.findAll {it.statusAsEnum == status}
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
}
