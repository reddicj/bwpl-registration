package org.bwpl.registration.utils

import org.bwpl.registration.Club
import org.bwpl.registration.User

class SecurityUtils {

    def springSecurityService

    User getCurrentUser() {

        User user = springSecurityService.currentUser
        if (user == null) return User.sys
        return user
    }

    boolean isCurrentUserRegistrationSecretary() {

        User currentUser = (User) springSecurityService.currentUser
        if (currentUser == null) return false
        return currentUser.hasRole("ROLE_REGISTRATION_SECRETARY")
    }

    boolean canUserUpdate(Club club) {

        if (club == null) throw new IllegalArgumentException("The club is null")
        User currentUser = (User) springSecurityService.currentUser
        if (currentUser == null) throw new IllegalStateException("Current user on session is null")
        if (currentUser.hasRole("ROLE_REGISTRATION_SECRETARY")) return true
        return club.hasSecretary(currentUser.username)
    }

    Club getCurrentUserClub() {

        User currentUser = (User) springSecurityService.currentUser
        if (currentUser == null) return null
        return currentUser.club
    }
}
