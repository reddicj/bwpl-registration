package org.bwpl.registration.validation

import org.bwpl.registration.Registration

class DuplicatePlayerRegistrationCheck {

    String getError(Registration registration) {

        if (registration == null) {
            throw new IllegalArgumentException("Registration is null")
        }
        if (registration.role != "Player") {
            return ""
        }
        List<Registration> existingRegistrations = Registration.findAllByAsaNumberAndRoleAndStatus(registration.asaNumber, "Player", Status.VALID.toString())
        if (existingRegistrations.isEmpty()) {
            return ""
        }
        return "BWPL registered with ${existingRegistrations[0].team.club.name} (${existingRegistrations[0].team.name})."
    }
}
