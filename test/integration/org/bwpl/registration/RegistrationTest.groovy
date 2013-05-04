package org.bwpl.registration

import org.bwpl.registration.validation.Status
import org.junit.Test

import static org.fest.assertions.Assertions.assertThat

class RegistrationTest {

    @Test
    void testAddTwoIdenticalRegistrations() {

        Competition competition = new Competition(name: "BWPL", urlName: "bwpl")
        Division division = new Division(rank: 1, name: "Mens Div 1", isMale: true)
        competition.addToDivisions(division).save()

        Club c = new Club(name: "Poly", asaName: "Poly")
        competition.addToClubs(c).save()
        Team t = new Team(name: "Poly1", isMale: true)
        division.addToTeams(t)
        c.addToTeams(t).save()

        Registration r1 = new Registration()
        r1.asaNumber = 123
        r1.firstName = "James"
        r1.lastName = "Reddick"
        r1.role = "Player"
        r1.status = Status.INVALID
        r1.statusNote = ""
        r1.statusDate = new Date()

        Registration r2 = new Registration()
        r2.asaNumber = 123
        r2.firstName = "James"
        r2.lastName = "Reddick"
        r2.role = "Player"
        r2.status = Status.INVALID
        r2.statusNote = ""
        r2.statusDate = new Date()

        t.addToRegistrations(r1)
        t.addToRegistrations(r2)
        t.save(failOnError: true)

        t = Team.findByName("Poly1")
        List<Registration> rs = Registration.findAllByTeamAndAsaNumber(t, 123)
        assertThat(rs.size()).isEqualTo(2)
    }
}
