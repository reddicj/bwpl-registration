package org.bwpl.registration.utils

import static org.fest.assertions.Assertions.assertThat
import org.junit.Test

class ValidationUtilsTest {

    @Test
    void testCheckValueInListIgnoreCase() {

        List<String> errors = []
        ValidationUtils.checkValueInListIgnoreCase("Role", "player", ["Player", "Coach"], errors)
        assertThat(errors.isEmpty()).isTrue()
    }

    @Test
    void testCheckValueNotInListIgnoreCase() {

        List<String> errors = []
        ValidationUtils.checkValueInListIgnoreCase("Role", "play", ["Player", "Coach"], errors)
        assertThat(errors.isEmpty()).isFalse()
    }
}
