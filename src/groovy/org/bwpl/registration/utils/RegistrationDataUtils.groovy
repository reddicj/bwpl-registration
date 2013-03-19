package org.bwpl.registration.utils

import org.bwpl.registration.Registration
import org.bwpl.registration.validation.Status

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
