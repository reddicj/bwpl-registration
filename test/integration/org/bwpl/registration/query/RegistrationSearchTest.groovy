package org.bwpl.registration.query

import org.bwpl.registration.*
import org.bwpl.registration.utils.BwplDateTime
import org.bwpl.registration.validation.Status
import org.junit.Test

import static org.fest.assertions.Assertions.assertThat

class RegistrationSearchTest {

    @Test
    void testWhereStatusChangedAfter() {

        addData()
        RegistrationSearch registrationSearch = new RegistrationSearch()
        BwplDateTime date = BwplDateTime.fromString("02-08-2013")
        List<Registration> results = registrationSearch.whereStatusChangedAfter(date)
        assertThat(results.size()).isEqualTo(2)
        assertThat(results[0].firstName).isEqualTo("FirstName2")
        assertThat(results[1].firstName).isEqualTo("FirstName3")
    }

    private static void addData() {

        Competition competition = new Competition(name: "BWPL", urlName: "bwpl")
        Division division = new Division(rank: 1, name: "Mens Div 1", isMale: true)
        competition.addToDivisions(division).save()

        Club c = new Club(name: "Poly", asaName: "Poly")
        Team t = new Team(name: "Poly1", isMale: true)
        division.addToTeams(t)
        c.addToTeams(t)
        c.save()

        Registration r1 = getRegistration(1, "FirstName1", "LastName1", Status.NEW, "01-08-2013 20:00")
        Registration r2 = getRegistration(2, "FirstName2", "LastName2", Status.NEW, "01-09-2013 20:00")
        Registration r3 = getRegistration(3, "FirstName3", "LastName3", Status.NEW, "01-10-2013 20:00")

        t.addToRegistrations(r1)
        t.addToRegistrations(r2)
        t.addToRegistrations(r3)
        t.save()
    }

    private static Registration getRegistration(int asaNumber, String firstName, String lastName, Status status, String statusDateTime) {

        Registration r = new Registration()
        r.asaNumber = asaNumber
        r.firstName = firstName
        r.lastName = lastName
        r.role = "Player"
        r.status = status.toString()
        r.statusNote = ""
        r.statusDate = BwplDateTime.fromString(statusDateTime, "dd-MM-yyyy HH:mm").toJavaDate()
        r.isInASAMemberCheck = true
        return r
    }
}
