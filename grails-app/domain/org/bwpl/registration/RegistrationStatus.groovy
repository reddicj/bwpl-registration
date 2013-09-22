package org.bwpl.registration

import org.bwpl.registration.utils.BwplDateTime
import org.bwpl.registration.validation.Action
import org.bwpl.registration.validation.Status

class RegistrationStatus {

    static mapping = {
        cache true
    }

    static constraints = {

        action(blank: false, inList: ["Added", "Deleted", "Undeleted", "Validated"])
        status(blank: false, inList: ["New", "Invalid", "Valid", "Deleted"])
    }

    static belongsTo = [registration: Registration]

    Date date
    User user
    String action
    String status
    String notes

    String getDateAsString() {
        return BwplDateTime.fromJavaDate(date).toDateTimeString()
    }

    Status getStatusAsEnum() {

        Status s = Status.fromString(status)
        if (s != Status.VALID) return s
        if (registration.doInvalidateStatusDuringValidationCutoff()) return Status.INVALID
        return s
    }

    String getStatusNotes() {

        Status s = Status.fromString(status)
        if (s != Status.VALID) return notes
        if (registration.doInvalidateStatusDuringValidationCutoff()) return Registration.duringValidationCutOffMessage
        return notes
    }

    Action getActionAsEnum() {
        return Action.fromString(this.action)
    }
}
