package org.bwpl.registration

import org.bwpl.registration.utils.BwplDateTime
import org.bwpl.registration.validation.Action
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

    @Test
    void testStatusIsInvalidForRegistrationAddedAfterWedDeadline() {

        Competition competition = new Competition(name: "BWPL", urlName: "bwpl")
        Division division = new Division(rank: 1, name: "Mens Div 1", isMale: true)
        competition.addToDivisions(division).save()

        Club c = new Club(name: "Poly", asaName: "Poly")
        Team t = new Team(name: "Poly1", isMale: true)
        division.addToTeams(t)
        c.addToTeams(t)
        c.save()
        Registration r = getRegistration(Status.VALID, "01-08-2013 23:00")
        r.addToStatusEntries(getStatus("01-08-2013 14:00", Action.ADDED, Status.NEW))
        r.addToStatusEntries(getStatus("01-08-2013 23:00", Action.VALIDATED, Status.VALID))
        t.addToRegistrations(r)
        t.save()

        r = Registration.findByAsaNumber(123)
        BwplDateTime currentDate = BwplDateTime.fromString("02-08-2013 23:59", "dd-MM-yyyy HH:mm")
        assertThat(r.doInvalidateAddedAfterWedDeadline(seasonStartDate, currentDate)).isTrue()
        assertThat(r.doInvalidateValidatedAfterFriDeadline(seasonStartDate, currentDate)).isFalse()
    }

    @Test
    void testStatusIsInvalidForRegistrationValidatedAfterFriDeadline() {

        Competition competition = new Competition(name: "BWPL", urlName: "bwpl")
        Division division = new Division(rank: 1, name: "Mens Div 1", isMale: true)
        competition.addToDivisions(division).save()

        Club c = new Club(name: "Poly", asaName: "Poly")
        Team t = new Team(name: "Poly1", isMale: true)
        division.addToTeams(t)
        c.addToTeams(t)
        c.save()

        String dateTimeValidated = "03-08-2013 10:00"
        Registration r = getRegistration(Status.VALID, dateTimeValidated)
        r.addToStatusEntries(getStatus("31-07-2013 14:00", Action.ADDED, Status.NEW))
        r.addToStatusEntries(getStatus(dateTimeValidated, Action.VALIDATED, Status.VALID))
        t.addToRegistrations(r)
        t.save()

        r = Registration.findByAsaNumber(123)
        BwplDateTime currentDate = BwplDateTime.fromString("03-08-2013 12:00", "dd-MM-yyyy HH:mm")
        assertThat(r.doInvalidateAddedAfterWedDeadline(seasonStartDate, currentDate)).isFalse()
        assertThat(r.doInvalidateValidatedAfterFriDeadline(seasonStartDate, currentDate)).isTrue()
    }

    @Test
    void testStatusIsValidForRegistrationAddedAndValidatedBeforeDeadlines() {

        Club c = new Club(name: "Poly", asaName: "Poly")
        Team t = new Team(name: "Poly1", isMale: true)
        c.addToTeams(t)
        c.save()

        String dateTimeValidated = "01-08-2013 23:00"
        Registration r = getRegistration(Status.VALID, dateTimeValidated)
        r.addToStatusEntries(getStatus("31-07-2013 14:00", Action.ADDED, Status.NEW))
        r.addToStatusEntries(getStatus(dateTimeValidated, Action.VALIDATED, Status.VALID))
        t.addToRegistrations(r)
        t.save()

        r = Registration.findByAsaNumber(123)
        BwplDateTime currentDate = BwplDateTime.fromString("02-08-2013 23:59", "dd-MM-yyyy HH:mm")
        assertThat(r.doInvalidateAddedAfterWedDeadline(seasonStartDate, currentDate)).isFalse()
        assertThat(r.doInvalidateValidatedAfterFriDeadline(seasonStartDate, currentDate)).isFalse()
    }

    @Test
    void testStatusIsValidForRegistrationAddedAfterWedDeadlineOnSunEve() {

        Competition competition = new Competition(name: "BWPL", urlName: "bwpl")
        Division division = new Division(rank: 1, name: "Mens Div 1", isMale: true)
        competition.addToDivisions(division).save()

        Club c = new Club(name: "Poly", asaName: "Poly")
        Team t = new Team(name: "Poly1", isMale: true)
        division.addToTeams(t)
        c.addToTeams(t)
        c.save()

        String dateTimeValidated = "01-08-2013 23:00"
        Registration r = getRegistration(Status.VALID, dateTimeValidated)
        r.addToStatusEntries(getStatus("01-08-2013 14:00", Action.ADDED, Status.NEW))
        r.addToStatusEntries(getStatus(dateTimeValidated, Action.VALIDATED, Status.VALID))
        t.addToRegistrations(r)
        t.save()

        r = Registration.findByAsaNumber(123)
        BwplDateTime currentDate = BwplDateTime.fromString("04-08-2013 20:30", "dd-MM-yyyy HH:mm")
        assertThat(r.doInvalidateAddedAfterWedDeadline(seasonStartDate, currentDate)).isFalse()
        assertThat(r.doInvalidateValidatedAfterFriDeadline(seasonStartDate, currentDate)).isFalse()
    }

    @Test
    void testStatusIsValidForRegistrationValidatedAfterFriDeadlineOnSunEve() {

        Club c = new Club(name: "Poly", asaName: "Poly")
        Team t = new Team(name: "Poly1", isMale: true)
        c.addToTeams(t)
        c.save()

        String dateTimeValidated = "03-08-2013 10:00"
        Registration r = getRegistration(Status.VALID, dateTimeValidated)
        r.addToStatusEntries(getStatus("31-07-2013 14:00", Action.ADDED, Status.NEW))
        r.addToStatusEntries(getStatus(dateTimeValidated, Action.VALIDATED, Status.VALID))
        t.addToRegistrations(r)
        t.save()

        r = Registration.findByAsaNumber(123)
        BwplDateTime currentDate = BwplDateTime.fromString("04-08-2013 20:30", "dd-MM-yyyy HH:mm")
        assertThat(r.doInvalidateAddedAfterWedDeadline(seasonStartDate, currentDate)).isFalse()
        assertThat(r.doInvalidateValidatedAfterFriDeadline(seasonStartDate, currentDate)).isFalse()
    }

    private static Registration getRegistration(Status status, String statusDateTime) {

        Registration r = new Registration()
        r.asaNumber = 123
        r.firstName = "James"
        r.lastName = "Reddick"
        r.role = "Player"
        r.status = status.toString()
        r.statusNote = ""
        r.statusDate = BwplDateTime.fromString(statusDateTime, "dd-MM-yyyy HH:mm").toJavaDate()
        r.isInASAMemberCheck = true
        return r
    }

    private static RegistrationStatus getStatus(String dateTime, Action action, Status status) {

        RegistrationStatus s = new RegistrationStatus()
        s.date = BwplDateTime.fromString(dateTime, "dd-MM-yyyy HH:mm").toJavaDate()
        s.user = TestUtils.user
        s.action = action.toString()
        s.status = status.toString()
        s.notes = ""
        return s
    }

    private static BwplDateTime getSeasonStartDate() {
        return BwplDateTime.fromString("01-06-2013")
    }
}
