package org.bwpl.registration.utils

import org.apache.commons.lang.StringUtils

import org.springframework.web.multipart.MultipartFile
import java.util.regex.Pattern
import org.apache.commons.validator.EmailValidator

class ValidationUtils {

    static void checkIsValidCsvFile(MultipartFile file, List<String> errors) {

        if (file == null) {
            errors << "No file selected or file is empty."
            return
        }

        if (file.isEmpty()) {
            errors << "No file selected or file is empty."
            return
        }
    }

    static void checkForNullOrEmptyValue(int lineNumber, String name, String value, List<String> errors) {

        if (StringUtils.isBlank(value)) {
            errors << "Line $lineNumber: $name is null or empty"
        }
    }

    static void checkValueInList(int lineNumber, String name, String value, List<String> allowedValues, List<String> errors) {

        if (!allowedValues.contains(value)) {
            errors << "Line $lineNumber: $name must be one of the following values: ${allowedValues.join(", ")}"
        }
    }

    static void checkValueIsNumeric(int lineNumber, String name, String value, List<String> errors) {

        if (!StringUtils.isNumeric(value)) {
            errors << "Line $lineNumber: $name is not a number: $value"
        }
    }

    static void checkValueIsAlphaNumericSpace(int lineNumber, String name, String value, List<String> errors) {

        if (!StringUtils.isAlphanumericSpace(value)) {
            errors << "Line $lineNumber: $name contains non alphanumeric, space characters: $value"
        }
    }

    static void checkValueContainsValidNameCharacters(int lineNumber, String name, String value, List<String> errors) {

        String v = StringUtils.remove(value, '-')
        if (!StringUtils.isAlphanumericSpace(v)) {
            errors << "Line $lineNumber: $name contains non valid name characters: $value"
        }
    }

    static void checkValueIsAlpha(int lineNumber, String name, String value, List<String> errors) {

        if (!StringUtils.isAlpha(value)) {
            errors << "Line $lineNumber: $name contains non alphabetical characters: $value"
        }
    }

    static void checkValueIsValidEmail(int lineNumber, String name, String value, List<String> errors) {

        if (!EmailValidator.instance.isValid(value)) {
            errors << "Line $lineNumber: $name is not a valid email address format: $value"
        }
    }

    static boolean isValidGenderData(String gender) {

        return StringUtils.startsWithIgnoreCase(gender, "M") ||
               StringUtils.startsWithIgnoreCase(gender, "F")
    }
}
