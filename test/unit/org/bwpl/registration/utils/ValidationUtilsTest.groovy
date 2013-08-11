package org.bwpl.registration.utils

import org.junit.Test

import static org.fest.assertions.Assertions.assertThat

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
    void testCheckValueContainsValidRegistrationNameCharacters() {

        List<String> errors = []
        ValidationUtils.checkValueContainsValidRegistrationNameCharacters("Firstname", "Jean-Pierre", errors)
        assertThat(errors).isEmpty()
        ValidationUtils.checkValueContainsValidRegistrationNameCharacters("Lastname", "O'Sullivan", errors)
        assertThat(errors).isEmpty()
        ValidationUtils.checkValueContainsValidRegistrationNameCharacters("Lastname", "Reddick", errors)
        assertThat(errors).isEmpty()
        ValidationUtils.checkValueContainsValidRegistrationNameCharacters("Lastname", "Gardner Bond", errors)
        assertThat(errors).isEmpty()
        ValidationUtils.checkValueContainsValidRegistrationNameCharacters("Lastname", "Gardner-Bond", errors)
        assertThat(errors).isEmpty()
    }

    @Test
    void testIsValidAsaDateOfBirth() {

        assertThat(ValidationUtils.isValidAsaDateOfBirth("5th April 1972")).isTrue()
        assertThat(ValidationUtils.isValidAsaDateOfBirth("5 April 72")).isFalse()
    }
}
