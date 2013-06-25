package org.bwpl.registration.asa

import org.bwpl.registration.utils.DateTimeUtils
import org.junit.Test

import static org.fest.assertions.Assertions.assertThat
import static org.junit.Assert.fail

class ASAMemberTest {

    @Test
    void testValidData() {

        ASAMemberData asaMemberData = new ASAMemberData(283261)
        asaMemberData.with {
            name = "James Reddick"
            dateOfBirth = "5th April 1972"
            gender = "M"
            clubs << "Poly"
        }

        assertThat(asaMemberData.dateOfBirth).isEqualTo(DateTimeUtils.parse("05-04-1972").toDate())
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
        catch (ASAMemberDataValidationException e) {
            // pass
        }
    }
}
