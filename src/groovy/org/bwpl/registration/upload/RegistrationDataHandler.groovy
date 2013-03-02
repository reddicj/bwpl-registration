package org.bwpl.registration.upload

import org.apache.commons.lang.WordUtils
import org.bwpl.registration.Registration
import org.bwpl.registration.Team
import org.bwpl.registration.User
import org.bwpl.registration.validation.Action
import org.bwpl.registration.validation.Status

import static org.bwpl.registration.utils.ValidationUtils.*

class RegistrationDataHandler implements CsvHandler {

    private static final List<String> fieldNames = ["Club name", "Team name", "Firstname", "Lastname", "ASA number", "Role (Player/Coach)"]
    User currentUser

    void processTokens(int lineNumber, String[] values) {

        String errors = getErrors(values)
        if (!errors.isEmpty()) {
            throw new UploadException("Error reading registration data --> Line $lineNumber: $errors")
        }

        String clubName = values[0]
        String teamName = values[1]
        String firstName = WordUtils.capitalize(values[2])
        String lastName = WordUtils.capitalize(values[3])
        int asaNumber = Integer.parseInt(values[4])
        String role = WordUtils.capitalize(values[5])


        Team team = Team.findByName(teamName)
        if (team == null) {
            throw new UploadException("Error uploading registration data for team: $teamName")
        }

        Registration registration = Registration.findByTeamAndAsaNumber(team, asaNumber)
        if (!registration) {

            registration = new Registration()
            registration.asaNumber = asaNumber
            registration.firstName = firstName
            registration.lastName = lastName
            registration.role = role
            registration.updateStatus(currentUser, Action.ADDED, Status.INVALID, "")
            team.addToRegistrations(registration)
        }
        team.save()
    }

    private String getErrors(String[] values) {

        if (values.length != fieldNames.size()) {

            String expected = "Expected fields: ${fieldNames.join(", ")}"
            String actual = "Actual fields: ${values.join(", ")}"
            return "$expected, $actual"
        }
        else {

            List<String> errors = []

            checkForNullOrEmptyValue("Club name", values[0], errors)
            checkValueIsAlphaNumericSpace("Club name", values[0], errors)

            checkForNullOrEmptyValue("Team name", values[1], errors)
            checkValueContainsValidNameCharacters("Team name", values[1], errors)

            checkForNullOrEmptyValue("Firstname", values[2], errors)
            checkValueIsAlpha("Firstname", values[2], errors)

            checkForNullOrEmptyValue("Lastname", values[3], errors)
            checkValueIsAlpha("Lastname", values[3], errors)

            checkForNullOrEmptyValue("ASA number", values[4], errors)
            checkValueIsNumeric("ASA number", values[4], errors)

            checkForNullOrEmptyValue("Role", values[5], errors)
            checkValueInList("Role", values[5], ["Player", "Coach"], errors)

            return errors.join(", ")
        }
    }
}
