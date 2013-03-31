package org.bwpl.registration.asa

import org.bwpl.registration.utils.DateTimeUtils

class ASAMemberClub {

    String name
    Date fromDate
    boolean isMember

    ASAMemberClub(String name, String fromDate, String membership) {

        this.name = name
        this.fromDate = DateTimeUtils.parse(fromDate, "dd-MM-yy").toDate()
        this.isMember = "Non Member" != membership
    }
}
