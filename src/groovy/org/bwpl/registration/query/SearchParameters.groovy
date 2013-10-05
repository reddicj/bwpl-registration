package org.bwpl.registration.query

import org.apache.commons.lang.StringUtils
import org.bwpl.registration.utils.BwplDateTime

class SearchParameters {

    String firstName
    String lastName
    Date statusChangedDate

    String getFirstName() {
        return StringUtils.trimToEmpty(firstName)
    }

    String getLastName() {
        return StringUtils.trimToEmpty(lastName)
    }

    BwplDateTime getStatusChangedDate() {
        return BwplDateTime.fromJavaDate(statusChangedDate)
    }

    boolean isNameParametersNotEmpty() {
        return StringUtils.isNotEmpty(firstName) || StringUtils.isNotEmpty(lastName)
    }
}
