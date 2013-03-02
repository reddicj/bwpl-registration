package org.bwpl.registration.validation

import org.bwpl.registration.Registration
import org.apache.commons.lang.StringUtils
import org.bwpl.registration.utils.SecurityUtils

class Validator {

    SecurityUtils securityUtils
    private final ASAMembershipCheck asaMembershipCheck = new ASAMembershipCheck()
    private final DuplicatePlayerRegistrationCheck duplicateRegistrationCheck = new DuplicatePlayerRegistrationCheck()
    private final CoachRegistrationCheck coachRegistrationCheck = new CoachRegistrationCheck()

    void validate(Registration registration) {

        String errorMessage = getErrorMessage(registration)
        if (StringUtils.isBlank(errorMessage)) {
            registration.updateStatus(securityUtils.currentUser, Action.VALIDATED, Status.VALID, "")
        }
        else {
            registration.updateStatus(securityUtils.currentUser, Action.VALIDATED, Status.INVALID, errorMessage)
        }
        registration.save()
    }

    String getErrorMessage(Registration registration) {

        List<String> errors = []
        errors.addAll(asaMembershipCheck.getErrors(registration))
        errors.add(duplicateRegistrationCheck.getError(registration))
        errors.addAll(coachRegistrationCheck.getErrors(registration))
        return errors.join(" ").trim()
    }
}
