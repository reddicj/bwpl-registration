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
            if (registration.team.division == existingRegistration.team.division) {
                return "Registered as a ${getInverseRole(registration.role)} for ${existingRegistration.team.club.name} (${existingRegistration.team.name})"
            }
        }
        return ""
    }

    private static String getInverseRole(String role) {

        if (role == "Coach") return "player"
        else return "coach"
    }
}
