package org.bwpl.registration.validation

import org.bwpl.registration.*
import org.junit.Test

import static org.fest.assertions.Assertions.assertThat

class CoachRegistrationCheckTest {

    @Test
    void testPersonCannotBeRegisteredAsPlayerAndCoachInTheSameDivisionForDifferentTeams() {

        Registration r1
        Registration r2
        Registration r3
        Registration r4

        Competition competition = new Competition(name: "BWPL", urlName: "bwpl")
        Division division = new Division(rank: 1, name: "Mens Div 1", isMale: true)
        competition.addToDivisions(division).save()

        Club c1 = new Club(name: "Poly", asaName: "Poly")
        Team t1 = new Team(name: "Poly Men", isMale: true)
        division.addToTeams(t1)
        c1.addToTeams(t1).save()

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
        Team t2 = new Team(name: "Penguin Men", isMale: true)
        division.addToTeams(t2)
        c2.addToTeams(t2).save()

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
        Team t3 = new Team(name: "Otter Men", isMale: true)
        division.addToTeams(t3)
        c3.addToTeams(t3).save()

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
        Team t4 = new Team(name: "Bristol Men", isMale: true)
        division.addToTeams(t4)
        c4.addToTeams(t4).save()

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
        assertThat(error).isEqualTo("Registered as a coach for Poly (Poly Men)")
        error = coachRegistrationCheck.getError(r3)
        assertThat(error).isEqualTo("Registered as a player for Penguin (Penguin Men)")
    }

    @Test
    void testPersonCanBeRegisteredAsPlayerAndCoachInTheSameDivisionForSameTeam() {

        Registration r1
        Registration r2

        Competition competition = new Competition(name: "BWPL", urlName: "bwpl")
        Division division = new Division(rank: 1, name: "Mens Div 1", isMale: true)
        competition.addToDivisions(division).save()

        Club c1 = new Club(name: "Poly", asaName: "Poly")
        Team t1 = new Team(name: "Poly Men", isMale: true)
        division.addToTeams(t1)
        c1.addToTeams(t1).save()

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
}
