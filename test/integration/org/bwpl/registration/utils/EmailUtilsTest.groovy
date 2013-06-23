package org.bwpl.registration.utils

import org.bwpl.registration.*
import org.bwpl.registration.validation.Action
import org.bwpl.registration.validation.Status
import org.junit.Test

import static org.fest.assertions.Assertions.assertThat

class EmailUtilsTest {

    @Test
    void testGetRegistrationSecretariesEmails() {

        Role regSecRole = new Role(authority: "ROLE_REGISTRATION_SECRETARY").save()
        User sec1 = new User(username: "reddicj@gmail.com", password: "password1", enabled: true).save()
        User sec2 = new User(username: "chris@bt.com", password: "password1", enabled: true).save()
        UserRole.create(sec1, regSecRole, true)
        UserRole.create(sec2, regSecRole, true)

        Set<String> emails = EmailUtils.registrationSecretariesEmails
        assertThat(emails).contains("reddicj@gmail.com")
        assertThat(emails).contains("chris@bt.com")
    }

    @Test
    void testQueryRegistrations() {

        Club c1 = new Club(name: "Poly", asaName: "Poly")
        Team t1 = new Team(name: "Poly Men", isMale: true)
        c1.addToTeams(t1)
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
