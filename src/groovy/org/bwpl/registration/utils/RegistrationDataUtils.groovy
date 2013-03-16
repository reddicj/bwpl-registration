package org.bwpl.registration.utils

import org.bwpl.registration.Registration
import org.bwpl.registration.validation.Status
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.WordUtils
import org.bwpl.registration.Team
import org.bwpl.registration.Club

import static org.bwpl.registration.utils.ValidationUtils.checkValueIsAlpha

import static org.bwpl.registration.utils.ValidationUtils.checkValueInList
import static org.bwpl.registration.utils.ValidationUtils.checkForNullOrEmptyValue
import static org.bwpl.registration.utils.ValidationUtils.checkValueIsNumeric
import static org.bwpl.registration.utils.ValidationUtils.checkValueInListIgnoreCase
import static org.bwpl.registration.utils.ValidationUtils.checkValueIsAlphaNumericSpace
import static org.bwpl.registration.utils.ValidationUtils.checkValueContainsValidNameCharacters

class RegistrationDataUtils {

    static List<Registration> getRegistrations(Collection<Registration> registrations, String registrationFilter, String sort) {

        if ("deleted".equals(registrationFilter)) {
            registrations = new ArrayList<Registration>(registrations.findAll{it.statusAsEnum == Status.DELETED})
        }
        else {
            registrations = new ArrayList<Registration>(registrations.findAll{it.statusAsEnum != Status.DELETED})
        }

        if ("firstname".equals(sort)) {
            return registrations.sort{it.name}
        }
        else if ("lastname".equals(sort)) {
            return registrations.sort{it.lastnameFirstname}
        }
        else {
            return registrations.sort{it.name}
        }
    }
}
