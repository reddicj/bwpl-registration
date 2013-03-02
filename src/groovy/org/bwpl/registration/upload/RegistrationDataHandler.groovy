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

        List<String> errors = getErrors(lineNumber, values)
        if (!errors.isEmpty()) {
            throw new UploadException(errors.join(", "))
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

    private List<String> getErrors(int lineNumber, String[] values) {

        if (values.length != fieldNames.size()) {

            String expected = "Expected fields: ${fieldNames.join(", ")}"
            String actual = "Actual fields: ${values.join(", ")}"
            return ["Line $lineNumber: $expected, $actual"]
        }
        else {

            List<String> errors = []

            checkForNullOrEmptyValue(lineNumber, "Club name", values[0], errors)
            checkValueIsAlphaNumericSpace(lineNumber, "Club name", values[0], errors)

            checkForNullOrEmptyValue(lineNumber, "Team name", values[1], errors)
            checkValueContainsValidNameCharacters(lineNumber, "Team name", values[1], errors)

            checkForNullOrEmptyValue(lineNumber, "Firstname", values[2], errors)
            checkValueIsAlpha(lineNumber, "Firstname", values[2], errors)

            checkForNullOrEmptyValue(lineNumber, "Lastname", values[3], errors)
            checkValueIsAlpha(lineNumber, "Lastname", values[3], errors)

            checkForNullOrEmptyValue(lineNumber, "ASA number", values[4], errors)
            checkValueIsNumeric(lineNumber, "ASA number", values[4], errors)

            checkForNullOrEmptyValue(lineNumber, "Role", values[5], errors)
            checkValueInList(lineNumber, "Role", values[5], ["Player", "Coach"], errors)

            return errors
        }
    }
}
