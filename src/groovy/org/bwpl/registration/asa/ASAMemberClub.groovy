package org.bwpl.registration.asa

import org.bwpl.registration.utils.BwplDateTime

class ASAMemberClub {

    String name
    Date fromDate
    boolean isMember

    ASAMemberClub(String name, String fromDate, String membership) {

        this.name = name
        this.fromDate = BwplDateTime.fromString(fromDate, "dd-MM-yy").toJavaDate()
        this.isMember = "Non Member" != membership
    }
}
