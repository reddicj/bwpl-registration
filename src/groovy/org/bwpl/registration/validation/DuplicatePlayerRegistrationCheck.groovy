package org.bwpl.registration.validation

import org.bwpl.registration.Registration

class DuplicatePlayerRegistrationCheck {

    String getError(Registration registration) {

        if (registration == null) throw new IllegalArgumentException("Registration is null")
        if (registration.role != "Player") return ""
        List<Registration> existingRegistrations = Registration.findAllByAsaNumberAndRoleAndStatus(registration.asaNumber, "Player", Status.VALID.toString())
        Registration duplicateRegistration = existingRegistrations.find { it.team.id != registration.team.id }
        if (duplicateRegistration == null) return ""
        return "BWPL registered with ${duplicateRegistration.team.club.name} (${duplicateRegistration.team.name})."
    }
}
