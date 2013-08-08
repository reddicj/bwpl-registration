package org.bwpl.registration.asa

import org.bwpl.registration.utils.BwplDateTime
import org.junit.Test

import static org.fest.assertions.Assertions.assertThat
import static org.junit.Assert.fail

class ASADataRetrievalTest {

    @Test
    void testGetServiceAvailabilityError() {

        ASAMemberDataRetrieval asaDataRetrieval = new ASAMemberDataRetrieval()
        String errorMsg = asaDataRetrieval.getServiceError()
        assertThat(errorMsg).isEmpty()
    }

    @Test
    void testGetDataForJamesReddick() {

        ASAMemberDataRetrieval asaDataRetrieval = new ASAMemberDataRetrieval()
        ASAMemberData asaData = asaDataRetrieval.get(283261)
        assertThat(asaData.name).startsWith("James").endsWith("Reddick")
        assertThat(asaData.dateOfBirth).isEqualTo(BwplDateTime.fromString("05-04-1972").toJavaDate())
        assertThat(asaData.asaNumber).isEqualTo(283261)
        assertThat(asaData.isMale).isTrue()
        assertThat(asaData.membershipCategory).isEqualTo("ASA Cat 2")
        assertThat(asaData.clubs.find {it.name.startsWith("Polytechnic")}).isNotNull()
    }

    @Test
    void testGetDataForNonExistentMember() {

        ASAMemberDataRetrieval asaDataRetrieval = new ASAMemberDataRetrieval()
        try {
            asaDataRetrieval.get(123456789)
            fail()
        }
        catch (ASAMemberDataNotFoundException e) {
            assertThat(e.message).startsWith("Error getting data for ASA number: 123456789")
        }
    }

    @Test
    void testInvalidHtml() {

        ASAMemberDataRetrieval asaDataRetrieval = new ASAMemberDataRetrieval("https://swimmingresults.org/biogs/")
        try {
            asaDataRetrieval.get(7839)
            fail()
        }
        catch (ASAMemberDataNotFoundException e) {
            assertThat(e.message).contains("Error reading html")
        }
    }
}
