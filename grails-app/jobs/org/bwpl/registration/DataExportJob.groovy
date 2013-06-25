package org.bwpl.registration

import org.bwpl.registration.utils.EmailUtils

class DataExportJob {

    static triggers = {
        cron name: 'nightlyTrigger', cronExpression: "0 0 4 ? * *"
    }

    EmailUtils emailUtils

    def execute() {
        println "bollocks"
        emailUtils.emailDataExport()
    }
}
