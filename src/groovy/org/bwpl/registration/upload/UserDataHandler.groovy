package org.bwpl.registration.upload

import static org.bwpl.registration.utils.ValidationUtils.*
import org.bwpl.registration.User
import org.bwpl.registration.Role
import org.bwpl.registration.UserRole
import org.bwpl.registration.Club

class UserDataHandler implements CsvHandler {

    private static final List<String> fieldNames = ["Firstname", "Lastname", "Email", "Role", "Club"]

    void processTokens(int lineNumber, String[] values) {

        String errors = getErrors(lineNumber, values)
        if (!errors.isEmpty()) {
            throw new UploadException("Error reading user data --> $errors")
        }

        String firstname = values[0]
        String lastname = values[1]
        String email = values[2]
        String role = values[3]

        User user = User.findByUsername(email)
        if (user != null) return

        user = new User()
        user.firstname = firstname
        user.lastname = lastname
        user.username = email
        user.enabled = true
        user.password = "password"
        user.save()

        if ("CLUB_SECRETARY".equals(role)) {

            if (values.length != 5) {
                throw new UploadException("Error reading user data --> ${getFieldCountError(lineNumber, values)}")
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

    private String getErrors(int lineNumber, String[] values) {

        if ((values.length < 4) || (values.length > 5)) {
            return getFieldCountError(lineNumber, values)
        }
        else {

            List<String> errors = []

            checkForNullOrEmptyValue(lineNumber, "Firstname", values[0], errors)
            checkValueIsAlpha(lineNumber, "Firstname", values[0], errors)

            checkForNullOrEmptyValue(lineNumber, "Lastname", values[1], errors)
            checkValueIsAlpha(lineNumber, "Lastname", values[1], errors)

            checkForNullOrEmptyValue(lineNumber, "Email", values[2], errors)
            checkValueIsValidEmail(lineNumber, "Email", values[2], errors)

            checkForNullOrEmptyValue(lineNumber, "Role", values[3], errors)
            checkValueInList(lineNumber, "Role", values[3], ["CLUB_SECRETARY", "READ_ONLY"], errors)

            if (values.length == 5) checkValueIsAlpha(lineNumber, "Club", values[4], errors)

            return errors.join(", ")
        }
    }

    private String getFieldCountError(int lineNumber, String[] values) {

        String expected = "Expected fields: ${fieldNames.join(", ")}"
        String actual = "Actual fields: ${values.join(", ")}"
        return "Line $lineNumber: $expected, $actual"
    }
}
