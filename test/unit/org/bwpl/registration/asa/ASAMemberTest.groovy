package org.bwpl.registration.asa

import org.junit.Test

import static org.fest.assertions.Assertions.assertThat
import static org.junit.Assert.fail

class ASAMemberTest {

    @Test
    void testValidData() {

        ASAMemberData asaMemberData = new ASAMemberData(283261)
        asaMemberData.with {
            name = "James Reddick"
            gender = "M"
            clubs << "Poly"
        }

        assertThat(asaMemberData.isMale).isTrue()
        assertThat(asaMemberData.asaNumber).isEqualTo(283261)
        assertThat(asaMemberData.clubs[0]).isEqualTo("Poly")
    }

    @Test
    void testInvalidGender() {

        try {
            ASAMemberData asaMemberData = new ASAMemberData(283261)
            asaMemberData.with {
                name = "James Reddick"
                gender = "Blah"
                clubs << "Poly"
            }
            fail()
        }
        catch (ASAMemberDataRetrievalException e) {
            // pass
        }
    }
}
