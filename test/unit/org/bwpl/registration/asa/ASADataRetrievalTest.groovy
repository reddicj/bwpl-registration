package org.bwpl.registration.asa

import org.junit.Test

import static org.fest.assertions.Assertions.assertThat
import static org.junit.Assert.fail
import org.bwpl.registration.utils.DateTimeUtils

class ASADataRetrievalTest {

    @Test
    void testGetDataForAndyMcAllister() {

        ASAMemberDataRetrieval asaDataRetrieval = new ASAMemberDataRetrieval()
        ASAMemberData asaData = asaDataRetrieval.get(7839)
        assertThat(asaData.name).startsWith("Andrew").endsWith("McAllister")
        assertThat(asaData.asaNumber).isEqualTo(7839)
        assertThat(asaData.isMale).isTrue()
        assertThat(asaData.membershipCategory).isEqualTo("ASA Cat 2")
        assertThat(asaData.clubs.size()).isEqualTo(2)
        assertThat(asaData.clubs[0].name).startsWith("Beckenham")
        assertThat(asaData.clubs[0].fromDate).isEqualTo(DateTimeUtils.parse("14-07-2012").toDate())
        assertThat(asaData.clubs[1].name).startsWith("Invicta")
        assertThat(asaData.clubs[1].fromDate).isEqualTo(DateTimeUtils.parse("01-09-2011").toDate())
    }

    @Test
    void testGetDataForJamesReddick() {

        ASAMemberDataRetrieval asaDataRetrieval = new ASAMemberDataRetrieval()
        ASAMemberData asaData = asaDataRetrieval.get(283261)
        assertThat(asaData.name).startsWith("James").endsWith("Reddick")
        assertThat(asaData.asaNumber).isEqualTo(283261)
        assertThat(asaData.isMale).isTrue()
        assertThat(asaData.membershipCategory).isEqualTo("ASA Cat 2")
        assertThat(asaData.clubs.size()).isEqualTo(2)
        assertThat(asaData.clubs[1].name).startsWith("Polytechnic")
        assertThat(asaData.clubs[1].fromDate).isEqualTo(DateTimeUtils.parse("03-04-2005").toDate())
    }

    @Test
    void testGetDataForNonExistentMember() {

        ASAMemberDataRetrieval asaDataRetrieval = new ASAMemberDataRetrieval()
        try {
            asaDataRetrieval.get(123456789)
            fail()
        }
        catch (ASAMemberDataRetrievalException e) {
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
        catch (ASAMemberDataRetrievalException e) {
            assertThat(e.message).contains("Error reading html")
        }
    }
}
