package org.bwpl.registration

import org.bwpl.registration.utils.DateTimeUtils
import org.bwpl.registration.validation.Action
import org.bwpl.registration.validation.Status
import org.joda.time.DateTime

class RegistrationStatus {

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

        if (registration.doInvalidateStatusDuringValidationCutoff()) return Status.INVALID
        else return Status.fromString(status)
    }

    String getStatusNotes() {

        if (registration.doInvalidateStatusDuringValidationCutoff()) return DateTimeUtils.duringValidationCutOffMessage
        else return notes
    }

    Action getActionAsEnum() {
        return Action.fromString(this.action)
    }
}
