package org.bwpl.registration.validation

import org.bwpl.registration.Club
import org.bwpl.registration.Registration
import org.bwpl.registration.Team
import org.bwpl.registration.TestUtils
import org.junit.Test

import static org.fest.assertions.Assertions.assertThat

class CoachRegistrationCheckTest {

    @Test
    void testPersonCannotBeRegisteredAsPlayerAndCoachInTheSameDivisionForDifferentTeams() {

        Registration r1
        Registration r2
        Registration r3
        Registration r4

        Club c1 = new Club(name: "Poly", asaName: "Poly")
        Team t1 = new Team(name: "Poly Men", isMale: true, division: 1)
        c1.addToTeams(t1)
        c1.save(failOnError: true)
        r1 = new Registration()
        r1.with {
            asaNumber = 123
            firstName = "James"
            lastName = "Reddick"
            role = "Coach"
            status = Status.INVALID
            statusNote = ""
            updateStatus(TestUtils.getUser(), Action.VALIDATED, Status.VALID, "")
        }
        t1.addToRegistrations(r1)
        t1.save()

        Club c2 = new Club(name: "Penguin", asaName: "Penguin")
        Team t2 = new Team(name: "Penguin Men", isMale: true, division: 1)
        c2.addToTeams(t2)
        c2.save(failOnError: true)
        r2 = new Registration()
        r2.with {
            asaNumber = 123
            firstName = "James"
            lastName = "Reddick"
            role = "Player"
            status = Status.INVALID
            statusNote = ""
            updateStatus(TestUtils.getUser(), Action.VALIDATED, Status.VALID, "")
        }
        t2.addToRegistrations(r2)
        t2.save()

        Club c3 = new Club(name: "Otter", asaName: "Otter")
        Team t3 = new Team(name: "Otter Men", isMale: true, division: 1)
        c3.addToTeams(t3)
        c3.save(failOnError: true)
        r3 = new Registration()
        r3.with {
            asaNumber = 123
            firstName = "James"
            lastName = "Reddick"
            role = "Coach"
            status = Status.INVALID
            statusNote = ""
            updateStatus(TestUtils.getUser(), Action.VALIDATED, Status.INVALID, "")
        }
        t3.addToRegistrations(r3)
        t3.save()

        Club c4 = new Club(name: "Bristol", asaName: "Bristol")
        Team t4 = new Team(name: "Bristol Men", isMale: true, division: 1)
        c4.addToTeams(t4)
        c4.save(failOnError: true)
        r4 = new Registration()
        r4.with {
            asaNumber = 123
            firstName = "James"
            lastName = "Reddick"
            role = "Player"
            status = Status.INVALID
            statusNote = ""
            updateStatus(TestUtils.getUser(), Action.VALIDATED, Status.INVALID, "")
        }
        t4.addToRegistrations(r4)
        t4.save()

        CoachRegistrationCheck coachRegistrationCheck = new CoachRegistrationCheck()
        String error = coachRegistrationCheck.getError(r4)
        assertThat(error).isNotEmpty()
        error = coachRegistrationCheck.getError(r3)
        assertThat(error).isNotEmpty()
    }

    @Test
    void testPersonCanBeRegisteredAsPlayerAndCoachInTheSameDivisionForSameTeam() {

        Registration r1
        Registration r2

        Club c1 = new Club(name: "Poly", asaName: "Poly")
        Team t1 = new Team(name: "Poly Men", isMale: true, division: 1)
        c1.addToTeams(t1)
        c1.save(failOnError: true)
        r1 = new Registration()
        r1.with {
            asaNumber = 123
            firstName = "James"
            lastName = "Reddick"
            role = "Coach"
            status = Status.INVALID
            statusNote = ""
            updateStatus(TestUtils.getUser(), Action.VALIDATED, Status.VALID, "")
        }
        t1.addToRegistrations(r1)
        t1.save()

        r2 = new Registration()
        r2.with {
            asaNumber = 123
            firstName = "James"
            lastName = "Reddick"
            role = "Player"
            status = Status.INVALID
            statusNote = ""
            updateStatus(TestUtils.getUser(), Action.VALIDATED, Status.VALID, "")
        }
        t1.addToRegistrations(r2)
        t1.save()

        CoachRegistrationCheck coachRegistrationCheck = new CoachRegistrationCheck()
        String error = coachRegistrationCheck.getError(r2)
        assertThat(error).isEmpty()
        error = coachRegistrationCheck.getError(r1)
        assertThat(error).isEmpty()
    }

    @Test
    void testAddSameCoachTwoTeamsSameClub() {

        Registration r1
        Registration r2

        Club c1 = new Club(name: "Poly", asaName: "Poly")
        Team t1 = new Team(name: "Poly 1", isMale: true, division: 1)
        Team t2 = new Team(name: "Poly 2", isMale: true, division: 2)
        c1.addToTeams(t1)
        c1.addToTeams(t2)
        c1.save()

        r1 = new Registration()
        r1.with {
            asaNumber = 123
            firstName = "James"
            lastName = "Reddick"
            role = "Coach"
            status = Status.INVALID
            statusNote = ""
            updateStatus(TestUtils.getUser(), Action.ADDED, Status.NEW, "")
        }
        t1.addToRegistrations(r1)
        t1.save()

        r2 = new Registration()
        r2.with {
            asaNumber = 123
            firstName = "James"
            lastName = "Reddick"
            role = "Coach"
            status = Status.INVALID
            statusNote = ""
            updateStatus(TestUtils.getUser(), Action.ADDED, Status.NEW, "")
        }
        t2.addToRegistrations(r2)
        t2.save()

        CoachRegistrationCheck coachRegistrationCheck = new CoachRegistrationCheck()
        String error = coachRegistrationCheck.getError(r2)
        assertThat(error).isEmpty()
        error = coachRegistrationCheck.getError(r1)
        assertThat(error).isEmpty()
    }
}
