package org.bwpl.registration.utils

import org.bwpl.registration.Competition
import org.bwpl.registration.Division

import static org.fest.assertions.Assertions.assertThat

import org.bwpl.registration.Club
import org.bwpl.registration.Registration
import org.bwpl.registration.Team
import org.bwpl.registration.TestUtils
import org.bwpl.registration.validation.Action
import org.bwpl.registration.validation.Status
import org.junit.Test

class EmailUtilsTest {

    @Test
    void testQueryRegistrations() {

        Competition competition = new Competition(name: "BWPL", urlName: "bwpl")
        Division division = new Division(rank: 1, name: "Mens Div 1", isMale: true)
        competition.addToDivisions(division).save()
        Club c1 = new Club(name: "Poly", asaName: "Poly")
        competition.addToClubs(c1).save()
        Team t1 = new Team(name: "Poly Men", isMale: true)
        c1.addToTeams(t1)
        division.addToTeams(t1)
        c1.save()

        Registration r1 = new Registration()
        r1.asaNumber = 283261
        r1.firstName = "James"
        r1.lastName = "Reddick"
        r1.role = "Player"
        r1.status = Status.NEW
        r1.statusNote = ""
        r1.statusDate = new Date()

        Registration r2 = new Registration()
        r2.asaNumber = 914
        r2.firstName = "Gary"
        r2.lastName = "Simons"
        r2.role = "Player"
        r2.status = Status.NEW
        r2.statusNote = ""
        r2.statusDate = new Date()

        t1.addToRegistrations(r1)
        t1.addToRegistrations(r2)
        t1.save()

        r1 = Registration.findByAsaNumber(283261)
        r1.updateStatus(TestUtils.getUser(), Action.VALIDATED, Status.VALID, "")
        r1.save()
        r2 = Registration.findByAsaNumber(914)
        r2.updateStatus(TestUtils.getUser(), Action.VALIDATED, Status.VALID, "")
        r2.updateStatus(TestUtils.getUser(), Action.VALIDATED, Status.INVALID, "")
        r2.save()

        Set<Registration> validated = Registration.findAll {
            team.club == c1 && status == Status.VALID.toString() && prevStatus != Status.VALID.toString()
        }
        Set<Registration> invalidated = Registration.findAll {
            team.club == c1 && status == Status.INVALID.toString() && prevStatus == Status.VALID.toString()
        }
        assertThat(validated.size()).isEqualTo(1)
        assertThat(validated.asList()[0].asaNumber).isEqualTo(283261)
        assertThat(invalidated.size()).isEqualTo(1)
        assertThat(invalidated.asList()[0].asaNumber).isEqualTo(914)
    }
}
