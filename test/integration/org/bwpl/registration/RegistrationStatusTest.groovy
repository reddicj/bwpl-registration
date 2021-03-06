package org.bwpl.registration

import org.bwpl.registration.utils.BwplDateTime
import org.bwpl.registration.validation.Action
import org.bwpl.registration.validation.Status
import org.junit.Test

import static org.fest.assertions.Assertions.assertThat

class RegistrationStatusTest {

    @Test
    void test() {

        Competition competition = new Competition(name: "BWPL", urlName: "bwpl")
        Division division = new Division(rank: 1, name: "Mens Div 1", isMale: true)
        competition.addToDivisions(division).save()

        Club c = new Club(name: "Poly", asaName: "Poly")
        Team t = new Team(name: "Poly1", isMale: true)
        division.addToTeams(t)
        c.addToTeams(t).save()

        Registration r = new Registration()
        r.asaNumber = 123
        r.firstName = "James"
        r.lastName = "Reddick"
        r.role = "Player"
        r.updateStatus(TestUtils.getUser(), Action.ADDED, Status.NEW, "")

        t.addToRegistrations(r)
        t.save()

        r = Registration.findByAsaNumber(123)
        List<RegistrationStatus> statusEntriesList = r.statusEntriesAsList
        assertThat(r.statusAsEnum).isEqualTo(Status.NEW)
        assertThat(r.statusEntries.size()).isEqualTo(1)
        assertThat(r.currentStatus.actionAsEnum).isEqualTo(Action.ADDED)
        assertThat(r.currentStatus.statusAsEnum).isEqualTo(Status.NEW)
        assertThat(r.currentStatus.dateAsString).startsWith(BwplDateTime.now.toDateString())
        assertThat(r.hasBeenValidated()).isFalse()

        r.updateStatus(TestUtils.getUser(), Action.VALIDATED, Status.INVALID, "")
        r.save()
        r = Registration.findByAsaNumber(123)
        statusEntriesList = r.statusEntriesAsList
        assertThat(r.statusAsEnum).isEqualTo(Status.INVALID)
        assertThat(r.statusEntries.size()).isEqualTo(2)
        assertThat(r.currentStatus.actionAsEnum).isEqualTo(Action.VALIDATED)
        assertThat(r.currentStatus.statusAsEnum).isEqualTo(Status.INVALID)
        assertThat(r.currentStatus.dateAsString).startsWith(BwplDateTime.now.toDateString())
        assertThat(r.hasBeenValidated()).isFalse()

        r.updateStatus(TestUtils.getUser(), Action.VALIDATED, Status.INVALID, "")
        r.save()
        r = Registration.findByAsaNumber(123)
        statusEntriesList = r.statusEntriesAsList
        assertThat(r.statusAsEnum).isEqualTo(Status.INVALID)
        assertThat(r.statusEntries.size()).isEqualTo(2)
        assertThat(r.currentStatus.actionAsEnum).isEqualTo(Action.VALIDATED)
        assertThat(r.currentStatus.statusAsEnum).isEqualTo(Status.INVALID)
        assertThat(r.currentStatus.dateAsString).startsWith(BwplDateTime.now.toDateString())
        assertThat(r.hasBeenValidated()).isFalse()

        r.updateStatus(TestUtils.getUser(), Action.VALIDATED, Status.VALID, "")
        r.save()
        r = Registration.findByAsaNumber(123)
        statusEntriesList = r.statusEntriesAsList
        assertThat(r.statusAsEnum).isEqualTo(Status.VALID)
        assertThat(r.statusEntries.size()).isEqualTo(3)
        assertThat(r.currentStatus.actionAsEnum).isEqualTo(Action.VALIDATED)
        assertThat(r.currentStatus.statusAsEnum).isEqualTo(Status.VALID)
        assertThat(r.currentStatus.dateAsString).startsWith(BwplDateTime.now.toDateString())
        assertThat(r.hasBeenValidated()).isTrue()

        r.updateStatus(TestUtils.getUser(), Action.DELETED, Status.DELETED, "")
        r.save()
        r = Registration.findByAsaNumber(123)
        statusEntriesList = r.statusEntriesAsList
        assertThat(r.statusAsEnum).isEqualTo(Status.DELETED)
        assertThat(r.statusEntries.size()).isEqualTo(4)
        assertThat(r.currentStatus.actionAsEnum).isEqualTo(Action.DELETED)
        assertThat(r.currentStatus.statusAsEnum).isEqualTo(Status.DELETED)
        assertThat(r.currentStatus.dateAsString).startsWith(BwplDateTime.now.toDateString())
        assertThat(r.hasBeenValidated()).isTrue()
    }
}
