package org.bwpl.registration.data

import org.apache.commons.lang.ArrayUtils
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.WordUtils
import org.bwpl.registration.Registration
import org.bwpl.registration.Team
import org.springframework.util.CollectionUtils

import static org.bwpl.registration.utils.ValidationUtils.*

class RegistrationData {

    String firstName
    String lastName
    String asaNumberAsString
    String role

    static RegistrationData fromCsvList(List<String> values) {

        if (CollectionUtils.isEmpty(values)) {
            throw new IllegalArgumentException("Registration data values are null or empty")
        }
        String[] asArray = values.toArray(new String[values.size()])
        return fromArray(asArray)
    }

    static RegistrationData fromArray(String[] values) {

        if (ArrayUtils.isEmpty(values)) {
            throw new IllegalArgumentException("Registration data values are null or empty")
        }
        if (values.length != 4) {
            throw new IllegalArgumentException("Registration data values: [${values.join(", ")}]. Expecting: [firstname, lastname, asa number, role]")
        }

        RegistrationData registrationData = new RegistrationData()
        registrationData.with {
            setFirstName(values[0])
            setLastName(values[1])
            setAsaNumberAsString(values[2])
            setRole(values[3])
        }
        return registrationData
    }

    static RegistrationData fromFormParams(def params) {

        RegistrationData registrationData = new RegistrationData()
        registrationData.with {
            setFirstName(params["firstName"])
            setLastName(params["lastName"])
            setAsaNumberAsString(params["asaNumber"])
            setRole(params["role"])
        }
        return registrationData
    }

    private void setFirstName(String firstName) {
        this.firstName = WordUtils.capitalizeFully(StringUtils.trimToEmpty(firstName), [' ', '-'] as char[])
    }

    private void setLastName(String lastName) {
        this.lastName = WordUtils.capitalizeFully(StringUtils.trimToEmpty(lastName), [' ', '-', '\''] as char[])
    }

    private void setRole(String role) {
        this.role = WordUtils.capitalizeFully(StringUtils.trimToEmpty(role))
    }

    private void setAsaNumberAsString(String asaNumberAsString) {
        this.asaNumberAsString = StringUtils.trimToEmpty(asaNumberAsString)
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
        checkValueContainsValidRegistrationNameCharacters("Firstname", firstName, errors)

        checkForNullOrEmptyValue("Lastname", lastName, errors)
        checkValueContainsValidRegistrationNameCharacters("Lastname", lastName, errors)

        checkForNullOrEmptyValue("Role", role, errors)
        checkValueInListIgnoreCase("Role", role, ["Player", "Coach"], errors)

        checkForNullOrEmptyValue("ASA Number", asaNumberAsString, errors)
        checkValueIsNumeric("ASA number", asaNumberAsString, errors)
        checkNumberIsInRange("ASA number", asaNumberAsString, errors, 1, 99999999)

        return errors.join(", ")
    }

    private String getTeamRegistrationExistsError(Team team) {

        Registration registration = Registration.findByTeamAndAsaNumberAndRole(team, asaNumber, role)
        if (registration) {
            return "${this.toString()} is already registered as a $role for $team.name ($team.club.name)."
        }
        return ""
    }

    String toString() {
        return "$firstName $lastName ($asaNumberAsString)"
    }
}
