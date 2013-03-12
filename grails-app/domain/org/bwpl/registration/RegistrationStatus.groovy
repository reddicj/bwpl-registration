package org.bwpl.registration

import org.bwpl.registration.validation.Action
import org.bwpl.registration.utils.DateTimeUtils
import org.bwpl.registration.validation.Status

class RegistrationStatus {

    static mapping = {
        cache true
    }

    static constraints = {

        action(blank: false, inList: ["Added", "Deleted", "Validated"])
        status(blank: false, inList: ["New", "Invalid", "Valid", "Deleted"])
    }

    static belongsTo = [registration: Registration]

    Date date
    User user
    String action
    String status
    String notes

    String getDateAsString() {
        return DateTimeUtils.printDate(date)
    }

    Status getStatusAsEnum() {
        return Status.fromString(this.status)
    }

    void setStatusAsEnum(Status status) {
        this.status = status.toString()
    }

    Action getActionAsEnum() {
        return Action.fromString(this.action)
    }

    void setActionAsEnum(Action action) {
        this.action = action.toString()
    }
}
