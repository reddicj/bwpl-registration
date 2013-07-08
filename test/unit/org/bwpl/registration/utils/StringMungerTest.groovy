package org.bwpl.registration.utils

import static org.fest.assertions.Assertions.assertThat
import org.junit.Test

class StringMungerTest {

    @Test
    void testMunge() {

        String name = "Kim O`keefe"
        assertThat(StringMunger.munge(name)).isEqualTo("Kim Okeefe")
    }
}
