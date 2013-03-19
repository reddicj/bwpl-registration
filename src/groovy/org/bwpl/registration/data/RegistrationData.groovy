package org.bwpl.registration.data

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.WordUtils
import org.bwpl.registration.Registration
import org.bwpl.registration.Team

import static org.bwpl.registration.utils.ValidationUtils.*

class RegistrationData {

    String firstName
    String lastName
    String asaNumberAsString
    String role

    static RegistrationData fromCsvList(List<String> values) {

        String[] asArray = values.toArray(new String[values.size()])
        return fromArray(asArray)
    }

    static RegistrationData fromArray(String[] values) {

        RegistrationData registrationData = new RegistrationData()
        registrationData.with {
            firstName = values[0]
            lastName = values[1]
            asaNumberAsString = values[2]
            role = values[3]
        }
        return registrationData
    }

    static RegistrationData fromFormParams(Team team, def params) {

        RegistrationData registrationData = new RegistrationData()
        registrationData.with {
            firstName = params["firstName"]
            lastName = params["lastName"]
            asaNumberAsString = params["asaNumber"]
            role = params["role"]
        }
        return registrationData
    }

    String getFirstName() {
        return WordUtils.capitalize(StringUtils.trimToEmpty(firstName))
    }

    String getLastName() {
        return WordUtils.capitalize(StringUtils.trimToEmpty(lastName))
    }

    String getRole() {
        return WordUtils.capitalize(StringUtils.trimToEmpty(role))
    }

    int getAsaNumber() {
        return Integer.parseInt(asaNumberAsString)
    }

    String getErrors(Team team) {

        String errors = getFieldValueErrors()
        if (!errors.isEmpty()) return errors
        return getTeamRegistrationExistsError(team)
    }

    String getFieldValueErrors() {

        List<String> errors = []

        checkForNullOrEmptyValue("Firstname", firstName, errors)
        checkValueIsAlpha("Firstname", firstName, errors)

        checkForNullOrEmptyValue("Lastname", lastName, errors)
        checkValueContainsValidNameCharacters("Lastname", lastName, errors)

        checkForNullOrEmptyValue("Role", role, errors)
        checkValueInList("Role", role, ["Player", "Coach"], errors)

        checkForNullOrEmptyValue("ASA Number", asaNumberAsString, errors)
        checkValueIsNumeric("ASA number", asaNumberAsString, errors)

        return errors.join(", ")
    }

    private String getTeamRegistrationExistsError(Team team) {

        Registration registration = Registration.findByTeamAndAsaNumber(team, asaNumber)
        if (registration) return "ASA number $registration.asaNumber ($registration.name) for $registration.team.name ($registration.team.club.name) already exists."
        registration = Registration.findByTeamAndFirstNameAndLastName(team, firstName, lastName)
        if (registration) return "$registration.name for $registration.team.name ($registration.team.club.name) already exists."
        return ""
    }
}