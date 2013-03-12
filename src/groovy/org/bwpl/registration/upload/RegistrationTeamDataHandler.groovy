package org.bwpl.registration.upload

import org.apache.commons.lang.WordUtils
import org.bwpl.registration.Registration
import org.bwpl.registration.Team
import org.bwpl.registration.User
import org.bwpl.registration.validation.Action
import org.bwpl.registration.validation.Status

import static org.bwpl.registration.utils.ValidationUtils.*

class RegistrationTeamDataHandler implements CsvHandler {

    private static final List<String> fieldNames = ["Firstname", "Lastname", "ASA number", "Role (Player/Coach)"]
    Team team
    User currentUser

    void processTokens(int lineNumber, String[] values) {

        String errors = getErrors(values)
        if (!errors.isEmpty()) {
            throw new UploadException("Error reading registration data --> Line $lineNumber: $errors")
        }

        String firstName = WordUtils.capitalize(values[0])
        String lastName = WordUtils.capitalize(values[1])
        int asaNumber = Integer.parseInt(values[2])
        String role = WordUtils.capitalize(values[3])

        Registration registration = Registration.findByTeamAndAsaNumber(team, asaNumber)
        if (registration) return
        registration = Registration.findByTeamAndFirstNameAndLastName(team, firstName, lastName)
        if (registration) return

        registration = new Registration()
        registration.asaNumber = asaNumber
        registration.firstName = firstName
        registration.lastName = lastName
        registration.role = role
        registration.updateStatus(currentUser, Action.ADDED, Status.NEW, "")
        team.addToRegistrations(registration)
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

            checkForNullOrEmptyValue("Firstname", values[0], errors)
            checkValueIsAlpha("Firstname", values[0], errors)

            checkForNullOrEmptyValue("Lastname", values[1], errors)
            checkValueIsAlpha("Lastname", values[1], errors)

            checkForNullOrEmptyValue("ASA number", values[2], errors)
            checkValueIsNumeric("ASA number", values[2], errors)

            checkForNullOrEmptyValue("Role", values[3], errors)
            checkValueInListIgnoreCase("Role", values[3], ["Player", "Coach"], errors)

            if (!errors.isEmpty()) {
                return errors.join(", ")
            }
            else {
                String firstName = WordUtils.capitalize(values[0])
                String lastName = WordUtils.capitalize(values[1])
                int asaNumber = Integer.parseInt(values[2])
                Registration registration = Registration.findByTeamAndAsaNumber(team, asaNumber)
                if (registration) return "ASA number $registration.asaNumber ($registration.name) for $registration.team.name ($registration.team.club.name) already exists."
                registration = Registration.findByTeamAndFirstNameAndLastName(team, firstName, lastName)
                if (registration) return "$registration.name for $registration.team.name ($registration.team.club.name) already exists."
            }
            return ""
        }
    }
}
