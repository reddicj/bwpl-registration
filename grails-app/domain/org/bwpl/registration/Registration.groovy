package org.bwpl.registration

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.WordUtils
import org.bwpl.registration.utils.DateTimeUtils
import org.bwpl.registration.validation.Action
import org.bwpl.registration.validation.Status

class Registration {

    static final String csvFieldNames =
        "\"Firstname\",\"Lastname\",\"ASA number\",\"Role\",\"Registration date\",\"Status\",\"Notes\""

    static mapping = {
        cache true
        statusEntries cache: true
    }

    static constraints = {

        asaNumber(validator: { it > 0 })
        firstName(blank: false)
        lastName(blank: false)
        role(inList: ["Player", "Coach"])
        status(blank: false, inList: ["New", "Invalid", "Valid", "Deleted"])
    }

    static belongsTo = [team: Team]
    static hasMany = [statusEntries: RegistrationStatus]

    static List<Registration> search(String firstName, String lastName) {

        List<Registration> results = null
        if (!StringUtils.isAlpha(firstName)) return []
        if (!StringUtils.isAlpha(lastName)) return []
        if (StringUtils.isNotBlank(firstName)) {
            firstName = WordUtils.capitalize(firstName.trim())
            if (StringUtils.isNotBlank(lastName)) {
                lastName = WordUtils.capitalize(lastName.trim())
                results = Registration.findAllByFirstNameAndLastName(firstName, lastName)
            }
            else {
                results = Registration.findAllByFirstName(firstName)
            }
        }
        else if (StringUtils.isNotBlank(lastName)) {
            lastName = WordUtils.capitalize(lastName.trim())
            results = Registration.findAllByLastName(lastName)
        }
        else {
            results = []
        }
        results.sort{it.name}
        return results
    }

    Integer asaNumber
    String firstName
    String lastName
    String role
    Date registrationDate
    String status
    String statusNote

    String getName() {
        return "$firstName $lastName"
    }

    String getLastnameFirstname() {
        return "$lastName $firstName "
    }

    String getRegistrationDateAsString() {
        return DateTimeUtils.printDate(registrationDate)
    }

    Status getStatusAsEnum() {
        return Status.fromString(status)
    }

    String getStatusNote() {
        return statusNote
    }

    @Override
    String toString() {
        return name
    }

    String toCsvString() {

        StringBuilder sb = new StringBuilder()
        sb << "\"$firstName\","
        sb << "\"$lastName\","
        sb << "\"$asaNumber\","
        sb << "\"$role\","
        sb << "\"$registrationDateAsString\","
        sb << "\"${statusAsEnum.toString()}\","
        sb << "\"$statusNote\""
        return sb.toString()
    }

    List<RegistrationStatus> getStatusEntriesAsList() {

        if (this.statusEntries == null) return []
        List<RegistrationStatus> statusEntries = new ArrayList<RegistrationStatus>(this.statusEntries)
        statusEntries.sort { a, b ->
            return b.date <=> a.date
        }
        return statusEntries
    }

    RegistrationStatus getCurrentStatus() {

        if (this.statusEntries == null) return null
        List<RegistrationStatus> statusEntries = getStatusEntriesAsList()
        return statusEntries.head()
    }

    void updateStatus(User user, Action action, Status status, String notes) {

        RegistrationStatus currentEntry = currentStatus
        boolean doAddNewStatus = doAddNewStatus(currentStatus, action, status)
        if (doAddNewStatus) {

            Date dateStamp = new Date()
            if (action == Action.ADDED) registrationDate = dateStamp
            RegistrationStatus newEntry = new RegistrationStatus()
            newEntry.date = dateStamp
            newEntry.user = user
            newEntry.actionAsEnum = action
            newEntry.statusAsEnum = status
            newEntry.notes = notes
            addToStatusEntries(newEntry)
            this.status = status.toString()
            this.statusNote = notes
        }
        else {

            currentEntry.date = new Date()
            currentEntry.user = user
            currentEntry.actionAsEnum = action
            currentEntry.notes = notes
            this.statusNote = notes
        }
    }

    boolean canUpdate() {

        if (DateTimeUtils.isBeforeSeasonStart()) return true
        if (statusAsEnum == Status.NEW) return true
        return !hasBeenValidated()
    }

    boolean hasBeenValidated() {

        if (this.statusEntries == null) return false
        RegistrationStatus registrationStatus = statusEntries.find {it.statusAsEnum == Status.VALID}
        return registrationStatus != null
    }

    private static boolean doAddNewStatus(RegistrationStatus currentStatus, Action newAction, Status newStatus) {

        if (currentStatus == null) return true
        if (currentStatus.actionAsEnum == Action.ADDED) {
            if (newAction != Action.ADDED) {
                return true
            }
        }
        return currentStatus.statusAsEnum != newStatus
    }
}
