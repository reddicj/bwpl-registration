package org.bwpl.registration.utils

import static org.fest.assertions.Assertions.assertThat
import org.junit.Test

class ValidationUtilsTest {

    @Test
    void testCheckValueInListIgnoreCase() {

        List<String> errors = []
        ValidationUtils.checkValueInListIgnoreCase("Role", "player", ["Player", "Coach"], errors)
        assertThat(errors).isEmpty()
    }

    @Test
    void testCheckValueNotInListIgnoreCase() {

        List<String> errors = []
        ValidationUtils.checkValueInListIgnoreCase("Role", "play", ["Player", "Coach"], errors)
        assertThat(errors).isNotEmpty()
    }

    @Test
    void testcheckValueContainsValidLastNameCharacters() {

        List<String> errors = []
        ValidationUtils.checkValueContainsValidLastNameCharacters("Lastname", "O'Sullivan", errors)
        assertThat(errors).isEmpty()
        ValidationUtils.checkValueContainsValidLastNameCharacters("Lastname", "Reddick", errors)
        assertThat(errors).isEmpty()
        ValidationUtils.checkValueContainsValidLastNameCharacters("Lastname", "Gardner Bond", errors)
        assertThat(errors).isEmpty()
        ValidationUtils.checkValueContainsValidLastNameCharacters("Lastname", "Gardner-Bond", errors)
        assertThat(errors).isEmpty()
    }
}
