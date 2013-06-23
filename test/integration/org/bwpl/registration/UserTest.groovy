package org.bwpl.registration

import org.junit.Test

import static org.fest.assertions.Assertions.assertThat

class UserTest {

    @Test
    void test() {

        /*
        Club club = new Club(name: "Poly", asaName: "Poly").save(flush: true)
        Role clubSecRole = new Role(authority: "ROLE_CLUB_SECRETARY").save(flush: true)
        User clubSecUser = new User(username: "clubsec", enabled: true, password: "password").save(flush: true)

        UserRole.create(clubSecUser, clubSecRole, true)

        club.secretary = clubSecUser
        club.save(flush: true)

        assertThat(User.count).isEqualTo(1)
        clubSecUser = User.findByUsername("clubsec")
        assertThat(clubSecUser.username).isEqualTo("clubsec")
        assertThat(clubSecUser.club.name).isEqualTo("Poly")
        club = Club.findByName("Poly")
        assertThat(club.secretary.username).isEqualTo("clubsec")
        */
    }

    @Test
    void testGetRoles() {

        User user = new User(firstname: "James", lastname: "Reddick", username: "user@test.com", password: "password", enabled: true).save()
        Role role1 = new Role(authority: "role1").save()
        Role role2 = new Role(authority: "role2").save()
        Role role3 = new Role(authority: "role3").save()
        UserRole.create(user, role1, true)
        UserRole.create(user, role2, true)
        UserRole.create(user, role3, true)

        List<Role> roles = UserRole.getUserRoles(user)
        List<String> authorities = roles.collect{it.authority}
        assertThat(authorities).hasSize(3)
        assertThat(authorities).contains("role1")
        assertThat(authorities).contains("role2")
        assertThat(authorities).contains("role3")

        user = User.findByUsername("user@test.com")
        String csvString = user.toCsvString()
        String expected = "\"James\",\"Reddick\",\"user@test.com\",\"${authorities.join("|")}\",\"\""
        assertThat(csvString).isEqualTo(expected)
    }
}
