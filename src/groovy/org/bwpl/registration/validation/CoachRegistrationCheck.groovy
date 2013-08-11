package org.bwpl.registration.validation

import org.bwpl.registration.Registration

class CoachRegistrationCheck {

    String getError(Registration registration) {

        if (registration == null) {
            throw new IllegalArgumentException("Registration is null")
        }
        List<Registration> existingRegistrations = Registration.findAllByAsaNumberAndStatus(registration.asaNumber, Status.VALID.toString())
        if (existingRegistrations.isEmpty()) {
            return ""
        }
        for (Registration existingRegistration : existingRegistrations) {
            if (isInvalid(existingRegistration, registration)) {
                return "Registered as a ${existingRegistration.role.toLowerCase()} for ${existingRegistration.team.club.name} (${existingRegistration.team.name})."
            }
        }
        return ""
    }

    private static boolean isInvalid(Registration existingRegistration, Registration registration) {

        if ((registration.role == "Coach") && (existingRegistration.role == "Coach")) return false

        return (registration.team.division == existingRegistration.team.division) &&
               (registration.team.gender == existingRegistration.team.gender) &&
               (registration.team.id != existingRegistration.team.id)
    }
}
