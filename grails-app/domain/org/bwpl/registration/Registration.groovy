package org.bwpl.registration

import org.apache.commons.lang.StringUtils
import org.bwpl.registration.utils.BwplDateTime
import org.bwpl.registration.validation.Action
import org.bwpl.registration.validation.Status

class Registration {

    static final String duringValidationCutOffMessage =
        "Invalid until Sunday 8pm. The Registration was added after the Wednesday midnight deadline."

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

    def grailsApplication

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

    String getDateOfBirthAsString() {

        if (dateOfBirth == null) return ""
        return BwplDateTime.fromJavaDate(dateOfBirth).toDateString()
    }

    boolean isUnder18() {

        if (dateOfBirth == null) return false
        return BwplDateTime.fromJavaDate(dateOfBirth).isDiffLessThan18Years(BwplDateTime.now)
    }

    String getName() {
        return "$firstName $lastName"
    }

    String getLastnameFirstname() {
        return "$lastName $firstName "
    }

    Status getStatusAsEnum() {

        if (doInvalidateStatusDuringValidationCutoff()) return Status.INVALID
        else return Status.fromString(status)
    }

    String getStatusNote() {

        if (doInvalidateStatusDuringValidationCutoff()) return duringValidationCutOffMessage
        else return statusNote
    }

    boolean doInvalidateStatusDuringValidationCutoff() {

        BwplDateTime seasonStartDate = BwplDateTime.fromString(grailsApplication.config.bwpl.registration.season.start.date)
        return doInvalidateStatusDuringValidationCutoff(seasonStartDate, BwplDateTime.now)
    }

    protected boolean doInvalidateStatusDuringValidationCutoff(BwplDateTime seasonStartDate, BwplDateTime currentDate) {

        Status s = Status.fromString(status)
        if (Status.VALID != s) return false
        if (!isInASAMemberCheck) return false
        BwplDateTime theDateAdded = BwplDateTime.fromJavaDate(dateAdded)
        return currentDate.isDuringValidationCutOff(seasonStartDate, theDateAdded)
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
        sb << "\"${getStatusNote()}\""
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

    Date getDateAdded() {

        List<RegistrationStatus> statusList = getStatusEntriesAsList()
        for (RegistrationStatus status : statusList) {
            if (status.status == Status.NEW.toString()) {
                return status.date
            }
        }
        return statusDate
    }

    void updateStatus(User user, Action action, Status status, String notes) {

        RegistrationStatus currentEntry = currentStatus
        boolean doAddNewStatus = doAddNewStatus(status, currentStatus)
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

            // We update the status entry date to record when a registration was last validated.
            Date dateStamp = new Date()
            currentEntry.date = dateStamp
            currentEntry.user = user
            currentEntry.action = action.toString()
            currentEntry.notes = notes

            this.prevStatus = this.status
            this.statusNote = notes
            // Note this.statusDate is only updated if a new status entry is added.
            // This allows us to query for registrations that have had recent status changes.
        }
    }

    boolean canUpdate() {

        BwplDateTime seasonStartDate = BwplDateTime.fromString(grailsApplication.config.bwpl.registration.season.start.date)
        if (BwplDateTime.now.isBefore(seasonStartDate)) return true
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

    private boolean doAddNewStatus(Status newStatus, RegistrationStatus currentStatus) {

        if (currentStatus == null) return true
        if (StringUtils.isEmpty(status)) return true
        return Status.fromString(status) != newStatus
    }
}
