package org.bwpl.registration

import org.apache.commons.lang.StringUtils
import org.bwpl.registration.asa.ASAMemberDataRetrieval
import org.bwpl.registration.utils.EmailUtils
import org.bwpl.registration.validation.Validator

class ValidationJob {

    private static final int TWO_HOURS_IN_MILLI_SECONDS = 2 * 60 * 60 * 1000

    static triggers = {
        cron name: 'weeklyMondayTrigger', cronExpression: "0 0 2 ? * 2"
        // cron name: 'testNightlyTrigger', cronExpression: "0 0 2 * * ?"
        // simple name: 'testTrigger', startDelay: 120000, repeatInterval: 120000
    }

    Validator validator
    EmailUtils emailUtils

    def execute() {

        ASAMemberDataRetrieval asaMemberDataRetrieval = new ASAMemberDataRetrieval()
        for (int i = 1; i < 5; i++) {

            String asaMembershipCheckServiceError = asaMemberDataRetrieval.getServiceError()
            if (StringUtils.isBlank(asaMembershipCheckServiceError)) {

                String errors = validator.validateAll()
                emailUtils.emailError("Errors with weekly validation", errors)
                emailUtils.emailInvalidatedRegistrations()
                emailUtils.emailValidatedRegistrations()
                break
            }
            if (i > 3) {

                emailUtils.emailError("Error with weekly validation", "ASA Membership Check service error:\n$asaMembershipCheckServiceError")
                break
            }
            Thread.sleep(TWO_HOURS_IN_MILLI_SECONDS)
        }
    }
}
