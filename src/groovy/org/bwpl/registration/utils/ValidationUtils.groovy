package org.bwpl.registration.utils

import org.apache.commons.lang.StringUtils
import org.apache.commons.validator.EmailValidator
import org.springframework.web.multipart.MultipartFile

class ValidationUtils {

    static void checkIsValidCsvFile(MultipartFile file, List<String> errors) {

        if (file == null) {
            errors << "No file selected or file is empty."
            return
        }

        if (file.isEmpty()) {
            errors << "No file selected or file is empty."
        }
    }

    static void checkForNullOrEmptyValue(String name, String value, List<String> errors) {

        if (StringUtils.isBlank(value)) {
            errors << "$name is null or empty"
        }
    }

    static void checkValueInList(String name, String value, List<String> allowedValues, List<String> errors) {

        if (!allowedValues.contains(value)) {
            errors << "$name must be one of the following values: ${allowedValues.join(", ")}"
        }
    }

    static void checkValueInListIgnoreCase(String name, String value, List<String> allowedValues, List<String> errors) {

        for (String allowedValue : allowedValues) {
            if (StringUtils.equalsIgnoreCase(allowedValue, value)) {
                return
            }
        }
        errors << "$name must be one of the following values: ${allowedValues.join(", ")}"
    }

    static void checkValueIsNumeric(String name, String value, List<String> errors) {

        if (!StringUtils.isNumeric(value)) {
            errors << "$name is not a valid number: $value"
        }
    }

    static void checkNumberIsInRange(String name, String value, List<String> errors, int lowerBound, int upperBound) {

        if (!StringUtils.isNumeric(value)) return
        final String errorMessage = "$name must be a number between $lowerBound to $upperBound"
        try {
            int valueAsInt = Integer.parseInt(value)
            if ((valueAsInt < lowerBound) || (valueAsInt > upperBound)) {
                errors << errorMessage
            }
        }
        catch (NumberFormatException e) {
            errors << errorMessage
        }
    }

    static void checkValueIsAlphaNumericSpace(String name, String value, List<String> errors) {

        if (!StringUtils.isAlphanumericSpace(value)) {
            errors << "$name contains non alphanumeric, space characters: $value"
        }
    }

    static void checkValueContainsValidClubTeamNameCharacters(String name, String value, List<String> errors) {

        boolean isValid = value =~ "^[A-Za-z0-9\\- ]+\$"
        if (!isValid) {
            errors << "$name contains invalid name characters: $value"
        }
    }

    static void checkValueContainsValidRegistrationNameCharacters(String name, String value, List<String> errors) {

        boolean isValid = value =~ "^[A-Za-z]+[ \\-']{0,1}[A-Za-z]+\$"
        if (!isValid) {
            errors << "$name contains invalid firstname characters: $value"
        }
    }

    static void checkValueIsAlpha(String name, String value, List<String> errors) {

        if (!StringUtils.isAlpha(value)) {
            errors << "$name contains non alphabetical characters: $value"
        }
    }

    static void checkValueIsValidEmail(String name, String value, List<String> errors) {

        if (!EmailValidator.instance.isValid(value)) {
            errors << "$name is not a valid email address format: $value"
        }
    }

    static boolean isValidGenderData(String gender) {

        return StringUtils.startsWithIgnoreCase(gender, "M") ||
               StringUtils.startsWithIgnoreCase(gender, "F")
    }

    static boolean isValidAsaDateOfBirth(String dob) {
        return dob =~ /^([0-9]{1,2})[a-z]{2} ([A-Za-z]+) ([0-9]{4})$/
    }
}
