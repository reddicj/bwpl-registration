package org.bwpl.registration.data

import junit.framework.Assert
import org.junit.Test

import static org.fest.assertions.Assertions.assertThat

class RegistrationDataTest {

    @Test
    void testFromCsvListWhenListIsEmpty() {

        List<String> csv = []
        try {
            RegistrationData.fromCsvList(csv)
            Assert.fail("Expected error")
        }
        catch (IllegalArgumentException e) {
            assertThat(e.message).isEqualTo("Registration data values are null or empty")
        }
    }

    @Test
    void testFromCsvListWhenListHasMissingValues() {

        List<String> csv = ["James", "Reddick"]
        try {
            RegistrationData.fromCsvList(csv)
            Assert.fail("Expected error")
        }
        catch (IllegalArgumentException e) {
            assertThat(e.message).contains("Registration data values: [").contains("Expecting: [firstname, lastname, asa number, role]")
        }
    }

    @Test
    void testFromCsvList1() {

        List<String> csv = ["james peter", "  Peter Reddick ", " 123   ", " player   "]
        RegistrationData registrationData = RegistrationData.fromCsvList(csv)
        String errors = registrationData.getFieldValueErrors()
        assertThat(errors).isEmpty()
        assertThat(registrationData.firstName).isEqualTo("James Peter")
        assertThat(registrationData.lastName).isEqualTo("Peter Reddick")
        assertThat(registrationData.asaNumber).isEqualTo(123)
        assertThat(registrationData.role).isEqualTo("Player")
    }

    @Test
    void testFromCsvList2() {

        List<String> csv = ["James Peter", "  peter-reddick ", " 123   ", " player "]
        RegistrationData registrationData = RegistrationData.fromCsvList(csv)
        String errors = registrationData.getFieldValueErrors()
        assertThat(errors).isEmpty()
        assertThat(registrationData.firstName).isEqualTo("James Peter")
        assertThat(registrationData.lastName).isEqualTo("Peter-Reddick")
        assertThat(registrationData.asaNumber).isEqualTo(123)
        assertThat(registrationData.role).isEqualTo("Player")
    }
}
