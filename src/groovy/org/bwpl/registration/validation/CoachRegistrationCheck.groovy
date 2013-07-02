package org.bwpl.registration.validation

import org.bwpl.registration.Registration

class CoachRegistrationCheck {

    String getError(Registration registration) {

        if (registration == null) {
            throw new IllegalArgumentException("Registration is null")
        }
        List<Registration> existingRegistrations = []
        if (registration.role == "Player") {
            existingRegistrations = Registration.findAllByAsaNumberAndRoleAndStatus(registration.asaNumber, "Coach", Status.VALID.toString())
        }
        else if (registration.role == "Coach") {
            existingRegistrations = Registration.findAllByAsaNumberAndRoleAndStatus(registration.asaNumber, "Player", Status.VALID.toString())
        }
        if (existingRegistrations.isEmpty()) {
            return ""
        }
        for (Registration existingRegistration : existingRegistrations) {
            if (isInvalid(existingRegistration, registration)) {
                return "Registered as a ${getInverseRole(registration.role)} for ${existingRegistration.team.club.name} (${existingRegistration.team.name})"
            }
        }
        return ""
    }

    private static boolean isInvalid(Registration existingRegistration, Registration registration) {

        return (registration.team.division == existingRegistration.team.division) &&
                (registration.team.id != existingRegistration.team.id)
    }

    private static String getInverseRole(String role) {

        if (role == "Coach") return "player"
        else return "coach"
    }
}
