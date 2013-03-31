package org.bwpl.registration.validation

import org.bwpl.registration.Club
import org.bwpl.registration.Registration
import org.bwpl.registration.Team
import org.junit.Test

import static org.fest.assertions.Assertions.assertThat

class ASAMembershipCheckTest {

    @Test
    void testCoachRegistrationCheck() {

        Club c = new Club(name: "Poly", asaName: "Poly")
        Team t = new Team(name: "Poly Women", isMale: false)
        c.addToTeams(t)
        c.save(failOnError: true)

        Registration r = new Registration()
        r.asaNumber = 7839
        r.firstName = "Andrew"
        r.lastName = "Mcallister"
        r.role = "Coach"
        r.status = Status.INVALID
        r.statusNote = ""

        t.addToRegistrations(r)
        t.save(failOnError: true)

        ASAMembershipCheck asaMembershipCheck = new ASAMembershipCheck()
        List<String> errors = asaMembershipCheck.getErrors(r)
        assertThat(errors.isEmpty()).isTrue()
    }

    @Test
    void testNotInASAMemberCheck() {

        Club c = new Club(name: "Poly", asaName: "Poly")
        Team t = new Team(name: "Poly Men", isMale: true)
        c.addToTeams(t)
        c.save(failOnError: true)

        Registration r = new Registration()
        r.asaNumber = 123
        r.firstName = "James"
        r.lastName = "Reddick"
        r.role = "Player"
        r.status = Status.INVALID
        r.statusNote = ""

        t.addToRegistrations(r)
        t.save(failOnError: true)

        ASAMembershipCheck asaMembershipCheck = new ASAMembershipCheck()
        List<String> errors = asaMembershipCheck.getErrors(r)
        assertThat(errors.isEmpty()).isFalse()
        assertThat(errors[0]).isEqualTo("Not found in ASA membership check.")
    }

    @Test
    void testNameMismatch() {

        Club c = new Club(name: "Poly", asaName: "Poly")
        Team t = new Team(name: "Poly Men", isMale: true)
        c.addToTeams(t)
        c.save(failOnError: true)

        Registration r = new Registration()
        r.asaNumber = 283261
        r.firstName = "Gary"
        r.lastName = "Simons"
        r.role = "Player"
        r.status = Status.INVALID
        r.statusNote = ""

        t.addToRegistrations(r)
        t.save(failOnError: true)

        ASAMembershipCheck asaMembershipCheck = new ASAMembershipCheck()
        List<String> errors = asaMembershipCheck.getErrors(r)
        assertThat(errors.isEmpty()).isFalse()
        assertThat(errors[0]).isEqualTo("Name does not match, ASA name: James Reddick.")
    }

    @Test
    void testIncorrectGender() {

        Club c = new Club(name: "Poly", asaName: "Poly")
        Team t = new Team(name: "Poly Women", isMale: false)
        c.addToTeams(t)
        c.save(failOnError: true)

        Registration r = new Registration()
        r.asaNumber = 283261
        r.firstName = "James"
        r.lastName = "Reddick"
        r.role = "Player"
        r.status = Status.INVALID
        r.statusNote = ""

        t.addToRegistrations(r)
        t.save(failOnError: true)

        ASAMembershipCheck asaMembershipCheck = new ASAMembershipCheck()
        List<String> errors = asaMembershipCheck.getErrors(r)
        assertThat(errors.isEmpty()).isFalse()
        assertThat(errors[0]).isEqualTo("Player incorrect gender for team.")
    }

    @Test
    void testNotRegisteredWithClub() {

        Club c = new Club(name: "Penguin", asaName: "Penguin")
        Team t = new Team(name: "Penguin Men", isMale: true)
        c.addToTeams(t)
        c.save(failOnError: true)

        Registration r = new Registration()
        r.asaNumber = 283261
        r.firstName = "James"
        r.lastName = "Reddick"
        r.role = "Player"
        r.status = Status.INVALID
        r.statusNote = ""

        t.addToRegistrations(r)
        t.save(failOnError: true)

        ASAMembershipCheck asaMembershipCheck = new ASAMembershipCheck()
        List<String> errors = asaMembershipCheck.getErrors(r)
        assertThat(errors.isEmpty()).isFalse()
        assertThat(errors[0]).isEqualTo("Not ASA registered with Penguin.")
    }

    @Test
    void testInvalidMembershipCategory() {

        Club c = new Club(name: "Burnley", asaName: "Burnley")
        Team t = new Team(name: "Burnley Women", isMale: false)
        c.addToTeams(t)
        c.save(failOnError: true)

        Registration r = new Registration()
        r.asaNumber = 1
        r.firstName = "Margaret"
        r.lastName = "Pickles"
        r.role = "Player"
        r.status = Status.INVALID
        r.statusNote = ""

        t.addToRegistrations(r)
        t.save(failOnError: true)

        ASAMembershipCheck asaMembershipCheck = new ASAMembershipCheck()
        List<String> errors = asaMembershipCheck.getErrors(r)
        assertThat(errors.isEmpty()).isFalse()
        assertThat(errors[0]).startsWith("Invalid ASA membership category for a BWPL")
    }

    @Test
    void testChecked() {

        Club c = new Club(name: "Poly", asaName: "Poly")
        Team t = new Team(name: "Poly Men", isMale: true)
        c.addToTeams(t)
        c.save(failOnError: true)

        Registration r = new Registration()
        r.asaNumber = 283261
        r.firstName = "James"
        r.lastName = "Reddick"
        r.role = "Player"
        r.status = Status.INVALID
        r.statusNote = ""

        t.addToRegistrations(r)
        t.save(failOnError: true)

        ASAMembershipCheck asaMembershipCheck = new ASAMembershipCheck()
        List<String> errors = asaMembershipCheck.getErrors(r)
        assertThat(errors.isEmpty()).isTrue()
    }
}
