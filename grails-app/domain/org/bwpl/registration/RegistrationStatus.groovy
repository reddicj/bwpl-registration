package org.bwpl.registration

import org.bwpl.registration.validation.Action
import org.bwpl.registration.utils.DateTimeUtils
import org.bwpl.registration.validation.Status
import org.joda.time.DateTime

class RegistrationStatus {

    static final String csvFieldNames =
        "\"Date\",\"User\",\"Action\",\"Status\",\"Notes\""

    static mapping = {
        cache true
    }

    static constraints = {

        action(blank: false, inList: ["Added", "Deleted", "Undeleted", "Validated"])
        status(blank: false, inList: ["New", "Invalid", "Valid", "Deleted"])
    }

    static belongsTo = [registration: Registration]

    def dateTimeUtils

    Date date
    User user
    String action
    String status
    String notes

    String getDateAsString() {
        return DateTimeUtils.printDateTime(date)
    }

    Status getStatusAsEnum() {

        Status s = Status.fromString(status)
        if (Status.VALID != s) return s
        if (!registration.isInASAMemberCheck) return s
        DateTime validationDate = new DateTime(date)
        if (dateTimeUtils.isDuringValidationCutOff(validationDate)) return Status.INVALID
        return s
    }

    String getStatusNotes() {

        Status s = Status.fromString(status)
        if (Status.VALID != s) return notes
        if (!registration.isInASAMemberCheck) return notes
        DateTime validationDate = new DateTime(date)
        if (dateTimeUtils.isDuringValidationCutOff(validationDate)) return DateTimeUtils.duringValidationCutOffMessage
        return notes
    }

    Action getActionAsEnum() {
        return Action.fromString(this.action)
    }

    String toCsvString() {

        StringBuilder sb = new StringBuilder()
        sb << "\"${DateTimeUtils.printDate(date)}\","
        sb << "\"${user.username}\","
        sb << "\"$action\","
        sb << "\"$status\","
        sb << "\"$notes\""
        return sb.toString()
    }
}
