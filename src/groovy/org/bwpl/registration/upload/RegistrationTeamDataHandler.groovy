package org.bwpl.registration.upload

import org.bwpl.registration.Registration
import org.bwpl.registration.Team
import org.bwpl.registration.User
import org.bwpl.registration.data.RegistrationData
import org.bwpl.registration.validation.Action
import org.bwpl.registration.validation.Status

class RegistrationTeamDataHandler implements CsvHandler {

    private static final List<String> fieldNames = ["Firstname", "Lastname", "ASA number", "Role (Player/Coach)"]
    Team team
    User currentUser

    void processTokens(int lineNumber, String[] values) {

        if (values.length != fieldNames.size()) {

            String expected = "Expected fields: ${fieldNames.join(", ")}"
            String actual = "Actual fields: ${values.join(", ")}"
            throw new UploadException("Line $lineNumber: $expected, $actual")
        }

        RegistrationData registrationData = RegistrationData.fromArray(values)
        String errors = registrationData.getErrors(team)
        if (!errors.isEmpty()) throw new UploadException("Line $lineNumber: $errors")

        Registration registration = Registration.findByTeamAndAsaNumber(team, registrationData.asaNumber)
        if (registration) return
        registration = Registration.findByTeamAndFirstNameAndLastName(team, registrationData.firstName, registrationData.lastName)
        if (registration) return

        registration = new Registration()
        registration.asaNumber = registrationData.asaNumber
        registration.firstName = registrationData.firstName
        registration.lastName = registrationData.lastName
        registration.role = registrationData.role
        registration.updateStatus(currentUser, Action.ADDED, Status.NEW, "")
        team.addToRegistrations(registration)
        team.save()
    }
}
