package org.bwpl.registration.validation

import org.bwpl.registration.Club
import org.bwpl.registration.Competition
import org.bwpl.registration.Division
import org.bwpl.registration.Registration
import org.bwpl.registration.Team
import org.junit.Test

import static org.fest.assertions.Assertions.assertThat

class ASAMembershipCheckTest {

    @Test
    void testCoachRegistrationCheck() {

        Registration r = new Registration()
        r.asaNumber = 7839
        r.firstName = "Andrew"
        r.lastName = "Mcallister"
        r.role = "Coach"
        r.status = Status.INVALID
        r.statusNote = ""

        Team t = getTeam("Poly", "Poly Women", false)
        t.addToRegistrations(r)
        t.save(failOnError: true)

        ASAMembershipCheck asaMembershipCheck = new ASAMembershipCheck()
        List<String> errors = asaMembershipCheck.getErrors(r)
        assertThat(errors.isEmpty()).isTrue()
    }

    @Test
    void testNotInASAMemberCheck() {

        Registration r = new Registration()
        r.asaNumber = 123
        r.firstName = "James"
        r.lastName = "Reddick"
        r.role = "Player"
        r.status = Status.INVALID
        r.statusNote = ""

        Team t = getTeam("Poly", "Poly Men", true)
        t.addToRegistrations(r)
        t.save(failOnError: true)

        ASAMembershipCheck asaMembershipCheck = new ASAMembershipCheck()
        List<String> errors = asaMembershipCheck.getErrors(r)
        assertThat(errors.isEmpty()).isFalse()
        assertThat(errors[0]).isEqualTo("Not found in ASA membership check.")
    }

    @Test
    void testNameMismatch() {

        Registration r = new Registration()
        r.asaNumber = 283261
        r.firstName = "Gary"
        r.lastName = "Simons"
        r.role = "Player"
        r.status = Status.INVALID
        r.statusNote = ""

        Team t = getTeam("Poly", "Poly Men", true)
        t.addToRegistrations(r)
        t.save(failOnError: true)

        ASAMembershipCheck asaMembershipCheck = new ASAMembershipCheck()
        List<String> errors = asaMembershipCheck.getErrors(r)
        assertThat(errors.isEmpty()).isFalse()
        assertThat(errors[0]).isEqualTo("Name does not match, ASA name: James Reddick.")
    }

    @Test
    void testIncorrectGender() {

        Registration r = new Registration()
        r.asaNumber = 283261
        r.firstName = "James"
        r.lastName = "Reddick"
        r.role = "Player"
        r.status = Status.INVALID
        r.statusNote = ""

        Team t = getTeam("Poly", "Poly Women", false)
        t.addToRegistrations(r)
        t.save(failOnError: true)

        ASAMembershipCheck asaMembershipCheck = new ASAMembershipCheck()
        List<String> errors = asaMembershipCheck.getErrors(r)
        assertThat(errors.isEmpty()).isFalse()
        assertThat(errors[0]).isEqualTo("Player incorrect gender for team.")
    }

    @Test
    void testNotRegisteredWithClub() {

        Registration r = new Registration()
        r.asaNumber = 283261
        r.firstName = "James"
        r.lastName = "Reddick"
        r.role = "Player"
        r.status = Status.INVALID
        r.statusNote = ""

        Team t = getTeam("Penguin", "Penguin Men", true)
        t.addToRegistrations(r)
        t.save(failOnError: true)

        ASAMembershipCheck asaMembershipCheck = new ASAMembershipCheck()
        List<String> errors = asaMembershipCheck.getErrors(r)
        assertThat(errors.isEmpty()).isFalse()
        assertThat(errors[0]).isEqualTo("Not ASA registered with Penguin.")
    }

    @Test
    void testInvalidMembershipCategory() {

        Registration r = new Registration()
        r.asaNumber = 1
        r.firstName = "Margaret"
        r.lastName = "Pickles"
        r.role = "Player"
        r.status = Status.INVALID
        r.statusNote = ""

        Team t = getTeam("Burnley", "Burnley Women", false)
        t.addToRegistrations(r)
        t.save(failOnError: true)

        ASAMembershipCheck asaMembershipCheck = new ASAMembershipCheck()
        List<String> errors = asaMembershipCheck.getErrors(r)
        assertThat(errors.isEmpty()).isFalse()
        assertThat(errors[0]).startsWith("Invalid ASA membership category for a BWPL")
    }

    @Test
    void testChecked() {

        Registration r = new Registration()
        r.asaNumber = 283261
        r.firstName = "James"
        r.lastName = "Reddick"
        r.role = "Player"
        r.status = Status.INVALID
        r.statusNote = ""

        Team t = getTeam("Poly", "Poly Men", true)
        t.addToRegistrations(r)
        t.save(failOnError: true)

        ASAMembershipCheck asaMembershipCheck = new ASAMembershipCheck()
        List<String> errors = asaMembershipCheck.getErrors(r)
        assertThat(errors.isEmpty()).isTrue()
    }

    @Test
    void testFerMasriNameMatchCheck() {

        Registration r = new Registration()
        r.asaNumber = 441577
        r.firstName = "Fernanda"
        r.lastName = "Masri"
        r.role = "Player"
        r.status = Status.INVALID
        r.statusNote = ""

        Team t = getTeam("Poly", "Poly Women", false)
        t.addToRegistrations(r)
        t.save(failOnError: true)

        ASAMembershipCheck asaMembershipCheck = new ASAMembershipCheck()
        List<String> errors = asaMembershipCheck.getErrors(r)
        assertThat(errors.isEmpty()).isTrue()
    }

    private static Team getTeam(String clubName, String teamName, boolean isMale) {

        Competition competition = new Competition(name: "BWPL", urlName: "bwpl")
        Division division = new Division(rank: 1, name: "Mens Div 1", isMale: true)
        competition.addToDivisions(division).save()
        Club c = new Club(name: clubName, asaName: clubName)
        competition.addToClubs(c).save()
        Team t = new Team(name: teamName, isMale: isMale)
        division.addToTeams(t)
        c.addToTeams(t).save()
        return t
    }
}
