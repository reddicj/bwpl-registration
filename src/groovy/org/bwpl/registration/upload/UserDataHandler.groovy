package org.bwpl.registration.upload

import org.apache.commons.lang.RandomStringUtils
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.WordUtils
import org.bwpl.registration.Club
import org.bwpl.registration.Role
import org.bwpl.registration.User
import org.bwpl.registration.UserRole

import static org.bwpl.registration.utils.ValidationUtils.*

class UserDataHandler implements CsvHandler {

    private static final List<String> fieldNames = ["Firstname", "Lastname", "Email", "Role", "Club"]

    void processTokens(int lineNumber, String[] values) {

        String errors = getErrors(values)
        if (!errors.isEmpty()) {
            throw new UploadException("Line $lineNumber: $errors")
        }

        String firstname = WordUtils.capitalize(StringUtils.trimToEmpty(values[0]), [' ', '-'] as char[])
        String lastname = WordUtils.capitalize(StringUtils.trimToEmpty(values[1]), [' ', '-'] as char[])
        String email = StringUtils.trimToEmpty(values[2])
        String role = getRole(values[3])

        User user = User.findByUsername(email)
        if (user != null) return

        user = new User()
        user.firstname = firstname
        user.lastname = lastname
        user.username = email
        user.enabled = true
        user.password = RandomStringUtils.random(10, true, true)
        user.save()

        if ("CLUB_SECRETARY".equals(role)) {

            if (values.length != 5) {
                throw new UploadException("Error reading user data --> ${getFieldCountError(values)}")
            }

            Role clubSecRole = Role.findByAuthority("ROLE_CLUB_SECRETARY")
            Role readOnlyRole = Role.findByAuthority("ROLE_READ_ONLY")
            UserRole.create(user, clubSecRole, true)
            UserRole.create(user, readOnlyRole, true)

            String clubName = values[4]
            Club club = Club.findByName(clubName)
            if ((club != null) && (!club.hasSecretary(email))) {
                club.addToSecretaries(user)
                club.save()
            }
        }
        else if ("READ_ONLY".equals(role)) {

            Role readOnlyRole = Role.findByAuthority("ROLE_READ_ONLY")
            UserRole.create(user, readOnlyRole, true)
        }
    }

    private String getErrors(String[] values) {

        if ((values.length < 4) || (values.length > 5)) {
            return getFieldCountError(values)
        }
        else {

            List<String> errors = []

            checkForNullOrEmptyValue("Firstname", values[0], errors)
            checkValueIsAlpha("Firstname", values[0], errors)

            checkForNullOrEmptyValue("Lastname", values[1], errors)
            checkValueIsAlpha("Lastname", values[1], errors)

            checkForNullOrEmptyValue("Email", values[2], errors)
            checkValueIsValidEmail("Email", values[2], errors)

            checkForNullOrEmptyValue("Role", values[3], errors)
            checkValueInList("Role", values[3], ["CLUB_SECRETARY", "READ_ONLY"], errors)

            if (values.length == 5) checkValueIsAlphaNumericSpace("Club", values[4], errors)

            return errors.join(", ")
        }
    }

    private static String getFieldCountError(String[] values) {

        String expected = "Expected fields: ${fieldNames.join(", ")}"
        String actual = "Actual fields: ${values.join(", ")}"
        return "$expected, $actual"
    }

    private static String getRole(String data) {

        String s = StringUtils.trimToEmpty(data)
        s = StringUtils.upperCase(s)
        return StringUtils.replace(s, " ", "_")
    }
}
