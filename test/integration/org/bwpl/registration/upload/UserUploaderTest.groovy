package org.bwpl.registration.upload

import static org.fest.assertions.Assertions.assertThat
import org.junit.Test
import org.springframework.web.multipart.MultipartFile

import static org.mockito.Mockito.*
import org.bwpl.registration.User
import org.junit.Before
import org.bwpl.registration.Role

class UserUploaderTest {

    @Before
    void setUp() {

        Role role = Role.findByAuthority("ROLE_REGISTRATION_SECRETARY")
        if (role == null) {
            new Role(authority: "ROLE_REGISTRATION_SECRETARY").save(flush: true)
        }
        role = Role.findByAuthority("ROLE_CLUB_SECRETARY")
        if (role == null) {
            new Role(authority: "ROLE_CLUB_SECRETARY").save(flush: true)
        }
        role = Role.findByAuthority("ROLE_READ_ONLY")
        if (role == null) {
            new Role(authority: "ROLE_READ_ONLY").save(flush: true)
        }
    }

    @Test
    void testUploadUserData() {

        MultipartFile f = mock(MultipartFile.class)
        when(f.isEmpty()).thenReturn(false)
        when(f.contentType).thenReturn("text/csv")
        when(f.originalFilename).thenReturn("fileName")
        when(f.bytes).thenReturn(userData.getBytes("UTF-8"))

        UserUploader userUploader = new UserUploader()
        userUploader.upload(f)

        assertThat(User.count).isEqualTo(2)

        User user = User.findByUsername("reddicj@gmail.com")
        assertThat(user.firstname).isEqualTo("James")
        assertThat(user.lastname).isEqualTo("Reddick")
        assertThat(user.hasRole("ROLE_READ_ONLY")).isTrue()
        assertThat(user.hasRole("ROLE_CLUB_SECRETARY")).isTrue()
        assertThat(user.hasRole("ROLE_REGISTRATION_SECRETARY")).isFalse()

        user = User.findByUsername("chris.ducker@bwpl.org")
        assertThat(user.firstname).isEqualTo("Chris")
        assertThat(user.lastname).isEqualTo("Ducker")
        assertThat(user.hasRole("ROLE_READ_ONLY")).isTrue()
        assertThat(user.hasRole("ROLE_CLUB_SECRETARY")).isFalse()
        assertThat(user.hasRole("ROLE_REGISTRATION_SECRETARY")).isFalse()
    }

    private static final String userData =
    "James,Reddick,reddicj@gmail.com,CLUB_SECRETARY,Polytechnic\n" +
    "Chris,Ducker,chris.ducker@bwpl.org,READ_ONLY"
}
