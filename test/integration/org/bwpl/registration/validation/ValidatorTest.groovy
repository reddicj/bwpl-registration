package org.bwpl.registration.validation

import org.bwpl.registration.Club
import org.bwpl.registration.Registration
import org.bwpl.registration.Team
import org.bwpl.registration.TestUtils
import org.junit.Test

import static org.fest.assertions.Assertions.assertThat

class ValidatorTest {

    @Test
    void testIsInASAMembershipCheckUpdated() {

        Club c1 = new Club(name: "Poly", asaName: "Poly")
        Team t1 = new Team(name: "Poly Men", isMale: true)
        c1.addToTeams(t1)
        c1.save(failOnError: true)

        Registration r1 = new Registration()
        r1.asaNumber = 283261
        r1.firstName = "James"
        r1.lastName = "Reddick"
        r1.role = "Player"
        r1.status = Status.VALID
        r1.statusNote = ""
        r1.statusDate = new Date()
        r1.isInASAMemberCheck = false

        t1.addToRegistrations(r1)
        t1.save(failOnError: true)

        Validator validator = new Validator()
        validator.securityUtils = TestUtils.mockSecurityUtils
        validator.validate(r1)

        r1 = Registration.findById(r1.id)
        assertThat(r1.statusAsEnum).isEqualTo(Status.VALID)
        assertThat(r1.isInASAMemberCheck).isTrue()
    }


    @Test
    void testNotASARegistered() {

        Club c1 = new Club(name: "Poly", asaName: "Poly")
        Team t1 = new Team(name: "Poly Men", isMale: true)
        c1.addToTeams(t1)
        c1.save(failOnError: true)

        Registration r1 = new Registration()
        r1.asaNumber = 123
        r1.firstName = "James"
        r1.lastName = "Reddick"
        r1.role = "Player"
        r1.status = Status.INVALID
        r1.statusNote = ""
        r1.statusDate = new Date()

        t1.addToRegistrations(r1)
        t1.save(failOnError: true)

        Validator validator = new Validator()
        validator.securityUtils = TestUtils.mockSecurityUtils
        validator.validate(r1)

        r1 = Registration.findById(r1.id)
        assertThat(r1.statusAsEnum).isEqualTo(Status.INVALID)
        assertThat(r1.statusNote).isEqualTo("Not found in ASA membership check.")
        assertThat(r1.isInASAMemberCheck).isFalse()
    }

    @Test
    void testDuplicateRegistration() {

        Club c1 = new Club(name: "Poly", asaName: "Poly")
        Team t1 = new Team(name: "Poly Men", isMale: true)
        c1.addToTeams(t1)
        c1.save(failOnError: true)

        Club c2 = new Club(name: "Penguin", asaName: "Penguin")
        Team t2 = new Team(name: "Penguin Men", isMale: true)
        c2.addToTeams(t2)
        c2.save(failOnError: true)

        Registration r1 = new Registration()
        r1.asaNumber = 283261
        r1.firstName = "James"
        r1.lastName = "Reddick"
        r1.role = "Player"
        r1.status = Status.INVALID
        r1.statusNote = ""
        r1.statusDate = new Date()

        Registration r2 = new Registration()
        r2.asaNumber = 283261
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

        Validator validator = new Validator()
        validator.securityUtils = TestUtils.mockSecurityUtils
        validator.validate(r1)
        validator.validate(r2)

        r2 = Registration.findById(r2.id)
        assertThat(r2.statusAsEnum).isEqualTo(Status.INVALID)
        assertThat(r2.statusNote).contains("BWPL registered with Poly")
    }

    @Test
    void testCoachRegistrationCheck() {

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
            statusDate = new Date()
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
            statusDate = new Date()
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
            statusDate = new Date()
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
            statusDate = new Date()
            updateStatus(TestUtils.getUser(), Action.VALIDATED, Status.INVALID, "")
        }
        t4.addToRegistrations(r4)
        t4.save()

        Validator validator = new Validator()
        validator.securityUtils = TestUtils.mockSecurityUtils
        validator.validate(r4)
        validator.validate(r3)

        r4 = Registration.findById(r4.id)
        r3 = Registration.findById(r3.id)
        assertThat(r4.statusAsEnum).isEqualTo(Status.INVALID)
        assertThat(r4.statusNote).contains("Registered as")
        assertThat(r3.statusAsEnum).isEqualTo(Status.INVALID)
        assertThat(r3.statusNote).contains("Registered as")
    }

    @Test
    void testValidated() {

        Club c1 = new Club(name: "Poly", asaName: "Poly")
        Team t1 = new Team(name: "Poly Men", isMale: true)
        c1.addToTeams(t1)
        c1.save(failOnError: true)

        Registration r1 = new Registration()
        r1.asaNumber = 283261
        r1.firstName = "James"
        r1.lastName = "Reddick"
        r1.role = "Player"
        r1.status = Status.INVALID
        r1.statusNote = ""
        r1.statusDate = new Date()

        t1.addToRegistrations(r1)
        t1.save(failOnError: true)

        Validator validator = new Validator()
        validator.securityUtils = TestUtils.mockSecurityUtils
        validator.validate(r1)

        r1 = Registration.findById(r1.id)
        assertThat(r1.statusAsEnum).isEqualTo(Status.VALID)
        assertThat(r1.statusNote).isEqualTo("Automatically validated.")
        assertThat(r1.isInASAMemberCheck).isTrue()
    }

    @Test
    void testValidateAll() {

        Club c1 = new Club(name: "Poly", asaName: "Poly")
        Team t1 = new Team(name: "Poly Men", isMale: true)
        c1.addToTeams(t1)
        c1.save(failOnError: true)

        Registration r1 = new Registration()
        r1.asaNumber = 283261
        r1.firstName = "James"
        r1.lastName = "Reddick"
        r1.role = "Player"
        r1.status = Status.NEW
        r1.statusNote = ""
        r1.statusDate = new Date()

        Registration r2 = new Registration()
        r2.asaNumber = 9064
        r2.firstName = "Luke"
        r2.lastName = "Jones"
        r2.role = "Player"
        r2.status = Status.NEW
        r2.statusNote = ""
        r2.statusDate = new Date()

        t1.addToRegistrations(r1)
        t1.addToRegistrations(r2)
        t1.save(failOnError: true)

        Validator validator = new Validator()
        validator.securityUtils = TestUtils.mockSecurityUtils
        validator.validateAll()

        r1 = Registration.findById(r1.id)
        assertThat(r1.statusAsEnum).isEqualTo(Status.VALID)
        r2 = Registration.findById(r2.id)
        assertThat(r2.statusAsEnum).isEqualTo(Status.VALID)
    }
}
