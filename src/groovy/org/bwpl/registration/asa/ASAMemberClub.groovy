package org.bwpl.registration.asa

import org.bwpl.registration.utils.DateTimeUtils

class ASAMemberClub {

    String name
    Date fromDate

    ASAMemberClub(String name, String fromDate) {

        this.name = name
        this.fromDate = DateTimeUtils.parse(fromDate, "dd-MM-yy").toDate()
    }
}
