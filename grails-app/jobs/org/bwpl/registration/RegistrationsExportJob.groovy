package org.bwpl.registration

import org.bwpl.registration.utils.EmailUtils

class RegistrationsExportJob {

    static triggers = {
        cron name: 'weeklySaturdayTrigger', cronExpression: "0 0 2 ? * 7"
    }

    EmailUtils emailUtils

    def execute() {
        emailUtils.emailRegistrationsExport()
    }
}
