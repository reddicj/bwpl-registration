package org.bwpl.registration.validation

import org.bwpl.registration.Club
import org.bwpl.registration.Competition
import org.bwpl.registration.Division
import org.bwpl.registration.Registration
import org.bwpl.registration.Team
import org.bwpl.registration.TestUtils
import org.junit.Test

import static org.fest.assertions.Assertions.assertThat

class DuplicatePlayerRegistrationCheckTest {

    @Test
    void testNoDuplicate() {

        Competition competition = new Competition(name: "BWPL", urlName: "bwpl")
        Division division = new Division(rank: 1, name: "Mens Div 1", isMale: true)
        competition.addToDivisions(division).save()

        Club c1 = new Club(name: "Poly", asaName: "Poly")
        Team t1 = new Team(name: "Poly Men", isMale: true)
        division.addToTeams(t1)
        c1.addToTeams(t1).save()

        Club c2 = new Club(name: "Penguin", asaName: "Penguin")
        Team t2 = new Team(name: "Penguin Men", isMale: true)
        division.addToTeams(t2)
        c2.addToTeams(t2).save()

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

        t1.addToRegistrations(r1)
        t1.save(failOnError: true)

        t2.addToRegistrations(r2)
        t2.save(failOnError: true)

        DuplicatePlayerRegistrationCheck duplicateRegistrationCheck = new DuplicatePlayerRegistrationCheck()
        String error = duplicateRegistrationCheck.getError(r1)
        assertThat(error).isEmpty()
    }

    @Test
    void testDuplicate() {

        Competition competition = new Competition(name: "BWPL", urlName: "bwpl")
        Division division = new Division(rank: 1, name: "Mens Div 1", isMale: true)
        competition.addToDivisions(division).save()

        Club c1 = new Club(name: "Poly", asaName: "Poly")
        Team t1 = new Team(name: "Poly Men", isMale: true)
        division.addToTeams(t1)
        c1.addToTeams(t1).save()

        Club c2 = new Club(name: "Penguin", asaName: "Penguin")
        Team t2 = new Team(name: "Penguin Men", isMale: true)
        division.addToTeams(t2)
        c2.addToTeams(t2).save()

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
        r2.updateStatus(TestUtils.getUser(), Action.VALIDATED, Status.VALID, "")

        t1.addToRegistrations(r1)
        t1.save(failOnError: true)

        t2.addToRegistrations(r2)
        t2.save(failOnError: true)

        DuplicatePlayerRegistrationCheck duplicateRegistrationCheck = new DuplicatePlayerRegistrationCheck()
        String error = duplicateRegistrationCheck.getError(r1)
        assertThat(error).isEqualTo("BWPL registered with Penguin (Penguin Men).")
    }

    @Test
    void testNoDuplicateWhenCoach1() {

        Competition competition = new Competition(name: "BWPL", urlName: "bwpl")
        Division division = new Division(rank: 1, name: "Mens Div 1", isMale: true)
        competition.addToDivisions(division).save()

        Club c1 = new Club(name: "Poly", asaName: "Poly")
        Team t1 = new Team(name: "Poly Men", isMale: true)
        division.addToTeams(t1)
        c1.addToTeams(t1).save()

        Club c2 = new Club(name: "Penguin", asaName: "Penguin")
        Team t2 = new Team(name: "Penguin Men", isMale: true)
        division.addToTeams(t2)
        c2.addToTeams(t2).save()

        Registration r1 = new Registration()
        r1.asaNumber = 123
        r1.firstName = "James"
        r1.lastName = "Reddick"
        r1.role = "Coach"
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
        r2.updateStatus(TestUtils.getUser(), Action.VALIDATED, Status.VALID, "")

        t1.addToRegistrations(r1)
        t1.save(failOnError: true)

        t2.addToRegistrations(r2)
        t2.save(failOnError: true)

        DuplicatePlayerRegistrationCheck duplicateRegistrationCheck = new DuplicatePlayerRegistrationCheck()
        String error = duplicateRegistrationCheck.getError(r1)
        assertThat(error).isEmpty()
    }

    @Test
    void testNoDuplicateWhenCoach2() {

        Competition competition = new Competition(name: "BWPL", urlName: "bwpl")
        Division division = new Division(rank: 1, name: "Mens Div 1", isMale: true)
        competition.addToDivisions(division).save()

        Club c1 = new Club(name: "Poly", asaName: "Poly")
        Team t1 = new Team(name: "Poly Men", isMale: true)
        division.addToTeams(t1)
        c1.addToTeams(t1).save()

        Club c2 = new Club(name: "Penguin", asaName: "Penguin")
        Team t2 = new Team(name: "Penguin Men", isMale: true)
        division.addToTeams(t2)
        c2.addToTeams(t2).save()

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
        r2.role = "Coach"
        r2.status = Status.INVALID
        r2.statusNote = ""
        r2.statusDate = new Date()
        r2.updateStatus(TestUtils.getUser(), Action.VALIDATED, Status.VALID, "")

        t1.addToRegistrations(r1)
        t1.save(failOnError: true)

        t2.addToRegistrations(r2)
        t2.save(failOnError: true)

        DuplicatePlayerRegistrationCheck duplicateRegistrationCheck = new DuplicatePlayerRegistrationCheck()
        String error = duplicateRegistrationCheck.getError(r1)
        assertThat(error).isEmpty()
    }
}
