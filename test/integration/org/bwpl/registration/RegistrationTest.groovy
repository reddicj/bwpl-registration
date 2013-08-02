package org.bwpl.registration

import org.bwpl.registration.utils.DateTimeUtils
import org.bwpl.registration.validation.Action
import org.bwpl.registration.validation.Status
import org.joda.time.DateTime
import org.junit.Test

import static org.fest.assertions.Assertions.assertThat
import static org.joda.time.DateTimeConstants.SUNDAY

class RegistrationTest {

    @Test
    void testAddTwoIdenticalRegistrations() {

        Club c = new Club(name: "Poly", asaName: "Poly")
        Team t = new Team(name: "Poly1", isMale: true)
        c.addToTeams(t)
        c.save()

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
    void testStatusDuringValidationCutoffIsInvalid() {

        Club c = new Club(name: "Poly", asaName: "Poly")
        Team t = new Team(name: "Poly1", isMale: true)
        c.addToTeams(t)
        c.save()
        Registration r = getRegistration(Status.VALID, "01-08-2013 23:00")
        r.addToStatusEntries(getStatus("01-08-2013 14:00", Action.ADDED, Status.NEW))
        r.addToStatusEntries(getStatus("01-08-2013 23:00", Action.VALIDATED, Status.VALID))
        t.addToRegistrations(r)
        t.save()

        r = Registration.findByAsaNumber(123)
        r.dateTimeUtils = new TestDateTimeUtils("02-08-2013 23:59")
        assertThat(r.statusAsEnum).isEqualTo(Status.INVALID)
    }

    @Test
    void testStatusDuringValidationCutoffIsValid() {

        Club c = new Club(name: "Poly", asaName: "Poly")
        Team t = new Team(name: "Poly1", isMale: true)
        c.addToTeams(t)
        c.save()
        Registration r = getRegistration(Status.VALID, "01-08-2013 23:00")
        r.addToStatusEntries(getStatus("31-07-2013 14:00", Action.ADDED, Status.NEW))
        r.addToStatusEntries(getStatus("01-08-2013 23:00", Action.VALIDATED, Status.VALID))
        t.addToRegistrations(r)
        t.save()

        r = Registration.findByAsaNumber(123)
        r.dateTimeUtils = new TestDateTimeUtils("02-08-2013 23:59")
        assertThat(r.statusAsEnum).isEqualTo(Status.VALID)
    }

    @Test
    void testStatusDuringValidationCutoffIsValidOnSunEve() {

        Club c = new Club(name: "Poly", asaName: "Poly")
        Team t = new Team(name: "Poly1", isMale: true)
        c.addToTeams(t)
        c.save()
        Registration r = getRegistration(Status.VALID, "01-08-2013 23:00")
        r.addToStatusEntries(getStatus("01-08-2013 14:00", Action.ADDED, Status.NEW))
        r.addToStatusEntries(getStatus("01-08-2013 23:00", Action.VALIDATED, Status.VALID))
        t.addToRegistrations(r)
        t.save()

        r = Registration.findByAsaNumber(123)
        r.dateTimeUtils = new TestDateTimeUtils("04-08-2013 20:30")
        assertThat(r.statusAsEnum).isEqualTo(Status.VALID)
    }

    private static Registration getRegistration(Status status, String statusDateTime) {

        Registration r = new Registration()
        r.asaNumber = 123
        r.firstName = "James"
        r.lastName = "Reddick"
        r.role = "Player"
        r.status = status.toString()
        r.statusNote = ""
        r.statusDate = DateTimeUtils.parse(statusDateTime, "dd-MM-yyyy HH:mm").toDate()
        r.isInASAMemberCheck = true
        return r
    }

    private static RegistrationStatus getStatus(String dateTime, Action action, Status status) {

        RegistrationStatus s = new RegistrationStatus()
        s.date = DateTimeUtils.parse(dateTime, "dd-MM-yyyy HH:mm").toDate()
        s.user = TestUtils.user
        s.action = action.toString()
        s.status = status.toString()
        s.notes = ""
        return s
    }

    private static class TestDateTimeUtils extends DateTimeUtils {

        private DateTime date

        TestDateTimeUtils(String date) {
            this.date = parse(date, "dd-MM-yyyy HH:mm")
        }

        @Override
        DateTime getSeasonStartDate() {
            return parse("01-07-2013")
        }

        @Override
        DateTime getWedMidnight() {
            return parse("31-07-2013 23:59", "dd-MM-yyyy HH:mm")
        }

        @Override
        boolean isSunEve() {
            return (date.dayOfWeek == SUNDAY) && (date.hourOfDay >= 20)
        }
    }
}
