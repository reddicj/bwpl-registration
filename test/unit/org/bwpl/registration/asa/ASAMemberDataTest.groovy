package org.bwpl.registration.asa

import static org.fest.assertions.Assertions.assertThat

import org.bwpl.registration.utils.DateTimeUtils
import org.junit.Test
import static org.junit.Assert.fail

class ASAMemberDataTest {

    @Test
    void testIsNameMatch() {

        ASAMemberData asaMemberData = new ASAMemberData(923329)
        asaMemberData.name = "Kim O`keefe"
        assertThat(asaMemberData.isNameMatch("Kim", "O'Keefe")).isTrue()
    }

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
        assertThat(asaMemberData.isNameMatch("James", "Reddick")).isTrue()
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
