package org.bwpl.registration.upload

import org.bwpl.registration.Registration
import org.bwpl.registration.Team
import org.bwpl.registration.User
import org.bwpl.registration.data.RegistrationData
import org.bwpl.registration.validation.Action
import org.bwpl.registration.validation.Status

class RegistrationDataHandler implements CsvHandler {

    private static final List<String> fieldNames = ["Club name", "Team name", "Firstname", "Lastname", "ASA number", "Role (Player/Coach)"]
    User currentUser

    void processTokens(int lineNumber, String[] values) {

        if (values.length != fieldNames.size()) {

            String expected = "Expected fields: ${fieldNames.join(", ")}"
            String actual = "Actual fields: ${values.join(", ")}"
            throw new UploadException("Line $lineNumber: $expected, $actual")
        }

        String clubName = values[0]
        String teamName = values[1]
        Team team = Team.findByName(teamName)
        if (team == null) {
            throw new UploadException("Error uploading registration data for team: $teamName")
        }

        List<String> registrationValues = values[2 .. (values.length - 1)]
        RegistrationData registrationData = RegistrationData.fromCsvList(registrationValues)
        String errors = registrationData.getErrors(team)
        if (!errors.isEmpty()) throw new UploadException("Line $lineNumber: $errors")

        Registration registration = Registration.findByTeamAndAsaNumber(team, registrationData.asaNumber)
        if (!registration) {

            registration = new Registration()
            registration.asaNumber = registrationData.asaNumber
            registration.firstName = registrationData.firstName
            registration.lastName = registrationData.lastName
            registration.role = registrationData.role
            registration.updateStatus(currentUser, Action.ADDED, Status.INVALID, "")
            team.addToRegistrations(registration)
        }
        team.save()
    }
}
