package org.bwpl.registration

import static org.fest.assertions.Assertions.assertThat

import org.junit.*
import org.joda.time.DateTime
import org.bwpl.registration.validation.Status

class RegistrationTest {

    @Test
    void testAddTwoIdenticalRegistrations() {

        Club c = new Club(name: "Poly", asaName: "Poly")
        Team t = new Team(name: "Poly1", isMale: true)
        c.addToTeams(t)
        c.save(failOnError: true)

        Registration r1 = new Registration()
        r1.asaNumber = 123
        r1.firstName = "James"
        r1.lastName = "Reddick"
        r1.role = "Player"
        r1.status = Status.INVALID
        r1.statusNote = ""

        Registration r2 = new Registration()
        r2.asaNumber = 123
        r2.firstName = "James"
        r2.lastName = "Reddick"
        r2.role = "Player"
        r2.status = Status.INVALID
        r2.statusNote = ""

        t.addToRegistrations(r1)
        t.addToRegistrations(r2)
        t.save(failOnError: true)

        t = Team.findByName("Poly1")
        List<Registration> rs = Registration.findAllByTeamAndAsaNumber(t, 123)
        assertThat(rs.size()).isEqualTo(2)
    }
}
