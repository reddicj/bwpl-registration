package org.bwpl.registration

import org.bwpl.registration.utils.SecurityUtils

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when
import org.bwpl.registration.upload.RegistrationUploader

class TestUtils {

    static User getUser() {

        User clubSecUser = User.findByUsername("clubsec@gmail.com")
        if (clubSecUser != null) return clubSecUser
        Role clubSecRole = new Role(authority: "ROLE_CLUB_SECRETARY").save(flush: true)
        clubSecUser = new User(username: "clubsec@gmail.com", enabled: true, password: "password").save(flush: true)
        UserRole.create(clubSecUser, clubSecRole, true)
        return clubSecUser
    }

    static SecurityUtils getMockSecurityUtils() {

        SecurityUtils securityUtils = mock(SecurityUtils)
        when(securityUtils.currentUser).thenReturn(getUser())
        RegistrationUploader teamUploader = new RegistrationUploader()
        teamUploader.securityUtils = securityUtils
        return securityUtils
    }
}
