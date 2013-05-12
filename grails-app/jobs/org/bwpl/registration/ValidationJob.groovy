package org.bwpl.registration

import org.bwpl.registration.utils.EmailUtils
import org.bwpl.registration.validation.Validator

class ValidationJob {

    static triggers = {
        // cron name: 'weeklySundayTrigger', cronExpression: "0 0 2 * * 1"
        cron name: 'testNightlyTrigger', cronExpression: "0 0 2 * * ?"
        // simple name: 'testTrigger', startDelay: 120000, repeatInterval: 120000
    }

    Validator validator
    EmailUtils emailUtils

    def execute() {

        validator.validateAll()
        emailUtils.emailInvalidatedRegistrations()
        emailUtils.emailValidatedRegistrations()
    }
}
