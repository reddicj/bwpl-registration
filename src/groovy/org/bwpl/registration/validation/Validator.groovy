package org.bwpl.registration.validation

import org.bwpl.registration.Registration
import org.bwpl.registration.utils.SecurityUtils

class Validator {

    SecurityUtils securityUtils
    private final ASAMembershipCheck asaMembershipCheck = new ASAMembershipCheck()
    private final DuplicatePlayerRegistrationCheck duplicateRegistrationCheck = new DuplicatePlayerRegistrationCheck()
    private final CoachRegistrationCheck coachRegistrationCheck = new CoachRegistrationCheck()

    void validate(Registration registration) {

        List<String> errorMessages = getErrorMessages(registration)

        if (errorMessages.isEmpty()) {
            registration.updateStatus(securityUtils.currentUser, Action.VALIDATED, Status.VALID, "Automatically validated.")
            registration.isInASAMemberCheck = true
        }
        else if ((errorMessages.size() == 1) && (ASAMembershipCheck.NOT_FOUND_MSG == errorMessages[0]) && registration.isManuallyValid()) {
            registration.updateStatus(securityUtils.currentUser, Action.VALIDATED, Status.VALID, registration.statusNote)
            registration.isInASAMemberCheck = false
        }
        else {
            registration.updateStatus(securityUtils.currentUser, Action.VALIDATED, Status.INVALID, errorMessages.join(" "))
            registration.isInASAMemberCheck = !errorMessages.contains(ASAMembershipCheck.NOT_FOUND_MSG)
        }
        registration.save(flush: true)
    }

    void validateAll() {

        Set<Registration> registrations = Registration.findAll {status != Status.DELETED.toString()}
        registrations.each { r ->
            validate(r)
        }
    }

    private List<String> getErrorMessages(Registration registration) {

        List<String> errors = []
        String duplicateRegistrationCheckError = duplicateRegistrationCheck.getError(registration)
        if (!duplicateRegistrationCheckError.isEmpty()) errors.add(duplicateRegistrationCheckError)
        String coachRegistrationCheckError = coachRegistrationCheck.getError(registration)
        if (!coachRegistrationCheckError.isEmpty()) errors.add(coachRegistrationCheckError)
        errors.addAll(asaMembershipCheck.getErrors(registration))
        return errors
    }
}
