package org.bwpl.registration.validation

import org.junit.Test

import static org.fest.assertions.Assertions.assertThat

class StatusTest {

    @Test
    void testFromString() {

        Status s = Status.fromString("invalid")
        assertThat(s).isEqualTo(Status.INVALID)
    }

    @Test
    void testFromStringForInvalidString() {

        Status s = Status.fromString("invalidValue")
        assertThat(s).isEqualTo(Status.INVALID)
    }
}
