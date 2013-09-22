package org.bwpl.registration.query

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.WordUtils
import org.bwpl.registration.Registration
import org.bwpl.registration.utils.BwplDateTime

class RegistrationSearch {

    List<Registration> search(SearchParameters parameters) {

        if (parameters.isNameParametersNotEmpty()) {
            return byName(parameters.firstName, parameters.lastName)
        }
        else {
            return whereStatusChangedAfter(parameters.statusChangedDate)
        }
    }

    List<Registration> byName(String firstName, String lastName) {

        List<Registration> results = null
        if (!StringUtils.isAlpha(firstName)) return []
        if (!StringUtils.isAlpha(lastName)) return []
        if (StringUtils.isNotBlank(firstName)) {
            firstName = WordUtils.capitalize(firstName.trim())
            if (StringUtils.isNotBlank(lastName)) {
                lastName = WordUtils.capitalize(lastName.trim())
                results = Registration.findAllByFirstNameAndLastName(firstName, lastName)
            }
            else {
                results = Registration.findAllByFirstName(firstName)
            }
        }
        else if (StringUtils.isNotBlank(lastName)) {
            lastName = WordUtils.capitalize(lastName.trim())
            results = Registration.findAllByLastName(lastName)
        }
        else {
            results = []
        }
        results.sort{it.name}
        return results
    }

    List<Registration> whereStatusChangedAfter(BwplDateTime date) {

        List<Registration> results = new ArrayList<Registration>(Registration.findAllByStatusDateGreaterThan(date.toJavaDate()))
        return results.sort{it.name}
    }
}
