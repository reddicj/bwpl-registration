package org.bwpl.registration

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.WordUtils
import org.bwpl.registration.utils.DateTimeUtils
import org.bwpl.registration.validation.Action
import org.bwpl.registration.validation.Status
import org.joda.time.DateTime

class Registration {

    static final String csvFieldNames =
        "\"Firstname\",\"Lastname\",\"ASA number\",\"Role\",\"Status\",\"Notes\""

    static mapping = {
        cache true
        statusEntries cache: true
    }

    static constraints = {

        asaNumber(validator: { it > 0 })
        firstName(blank: false)
        lastName(blank: false)
        dateOfBirth(nullable: true)
        role(inList: ["Player", "Coach"])
        status(blank: false, inList: ["New", "Invalid", "Valid", "Deleted"])
        prevStatus(nullable: true)
    }

    static belongsTo = [team: Team]
    static hasMany = [statusEntries: RegistrationStatus]

    def dateTimeUtils

    Integer asaNumber
    String firstName
    String lastName
    Date dateOfBirth
    String role
    String status
    String prevStatus
    String statusNote
    Date statusDate
    boolean isInASAMemberCheck = false

    static String getRegistrationsAsCsvString() {

        List<Team> teams = Team.list()
        teams.sort{it.name}
        StringBuilder sb = new StringBuilder()
        sb << "$Club.csvFieldNames,$Team.csvFieldNames,$csvFieldNames\n"
        teams.each { team ->
            String teamRegistrationsAsCsvString = team.getRegistrationsAsCsvString()
            if (StringUtils.isNotBlank(teamRegistrationsAsCsvString)) {
                sb << teamRegistrationsAsCsvString << "\n"
            }
        }
        return sb.toString().trim()
    }

    static String getRegistrationStatusEntriesAsCsvString() {

        List<Team> teams = Team.list()
        teams.sort{it.name}
        StringBuilder sb = new StringBuilder()
        sb << "$Club.csvFieldNames,$Team.csvFieldNames,Firstname,Lastname,ASA number,Role,$RegistrationStatus.csvFieldNames\n"
        teams.each { team ->
            String teamRegistrationsAsCsvString = team.getRegistrationsAsCsvString()
            if (StringUtils.isNotBlank(teamRegistrationsAsCsvString)) {
                sb << teamRegistrationsAsCsvString << "\n"
            }
        }
        return sb.toString().trim()
    }

    static List<Registration> search(String firstName, String lastName) {

        List<Registration> results = null
        if (!StringUtils.isAlpha(firstName)) return []
        if (!StringUtils.isAlpha(lastName)) return []
        if (StringUtils.isNotBlank(firstName)) {
            firstName = WordUtils.capitalize(firstName.trim())
            if (StringUtils.isNotBlank(lastName)) {
                lastName = WordUtils.capitalize(lastName.trim())
                results = findAllByFirstNameAndLastName(firstName, lastName)
            }
            else {
                results = findAllByFirstName(firstName)
            }
        }
        else if (StringUtils.isNotBlank(lastName)) {
            lastName = WordUtils.capitalize(lastName.trim())
            results = findAllByLastName(lastName)
        }
        else {
            results = []
        }
        results.sort{it.name}
        return results
    }

    String getDateOfBirthAsString() {
        if (dateOfBirth == null) return ""
        return DateTimeUtils.printDate(dateOfBirth)
    }

    boolean isUnder18() {

        if (dateOfBirth == null) return false
        DateTime now = new DateTime()
        DateTime dob = new DateTime(dateOfBirth)
        return DateTimeUtils.isPeriodLessThan18Years(dob, now)
    }

    String getName() {
        return "$firstName $lastName"
    }

    String getLastnameFirstname() {
        return "$lastName $firstName "
    }

    Status getStatusAsEnum() {

        Status s = Status.fromString(status)
        if (Status.VALID != s) return s
        if (!isInASAMemberCheck) return s
        DateTime validationDate = new DateTime(this.statusDate)
        if (dateTimeUtils.isDuringValidationCutOff(validationDate)) return Status.INVALID
        return Status.fromString(status)
    }

    String getStatusNote() {

        Status s = Status.fromString(status)
        if (Status.VALID != s) return statusNote
        if (!isInASAMemberCheck) return statusNote
        DateTime validationDate = new DateTime(this.statusDate)
        if (dateTimeUtils.isDuringValidationCutOff(validationDate)) return DateTimeUtils.duringValidationCutOffMessage
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
        boolean doAddNewStatus = doAddNewStatus(currentStatus, status)
        if (doAddNewStatus) {

            Date dateStamp = new Date()
            RegistrationStatus newEntry = new RegistrationStatus()
            newEntry.date = dateStamp
            newEntry.user = user
            newEntry.action = action.toString()
            newEntry.status = status.toString()
            newEntry.notes = notes
            addToStatusEntries(newEntry)

            this.prevStatus = this.status
            this.status = status.toString()
            this.statusNote = notes
            this.statusDate = dateStamp
        }
        else {

            Date dateStamp = new Date()
            currentEntry.date = dateStamp
            currentEntry.user = user
            currentEntry.action = action.toString()
            currentEntry.notes = notes

            this.prevStatus = this.status
            this.statusNote = notes
            this.statusDate = dateStamp
        }
    }

    boolean canUpdate() {

        if (dateTimeUtils.isBeforeSeasonStart()) return true
        if (statusAsEnum == Status.NEW) return true
        return !hasBeenValidated()
    }

    boolean hasBeenValidated() {

        if (this.statusEntries == null) return false
        RegistrationStatus registrationStatus = statusEntries.find {it.statusAsEnum == Status.VALID}
        return registrationStatus != null
    }

    boolean isManuallyValid() {
        return (statusAsEnum == Status.VALID) && (!isInASAMemberCheck)
    }

    private static boolean doAddNewStatus(RegistrationStatus currentStatus, Status newStatus) {

        if (currentStatus == null) return true
        return currentStatus.statusAsEnum != newStatus
    }
}
