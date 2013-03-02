package org.bwpl.registration.validation

import org.bwpl.registration.asa.ASAMemberDataRetrieval
import org.bwpl.registration.asa.ASAMemberData
import org.bwpl.registration.asa.ASAMemberDataRetrievalException
import org.bwpl.registration.Registration
import org.bwpl.registration.Team
import org.bwpl.registration.Club

class ASAMembershipCheck {

    private final ASAMemberDataRetrieval asaMemberDataRetrieval = new ASAMemberDataRetrieval()

    List<String> getErrors(Registration registration) {

        List<String> errors = []
        if (registration == null) {
            throw new IllegalAccessException("Registration is null")
        }

        try {
            ASAMemberData asaMember = asaMemberDataRetrieval.get(registration.asaNumber)
            Team team = registration.team
            Club registrationClub = team.club
            if (!asaMember.isNameMatch(registration.firstName, registration.lastName)) {
                errors << "Name does not match, ASA name: $asaMember.name."
            }
            if (team.isMale != asaMember.isMale) {
                errors << "Incorrect gender for team."
            }
            if (!asaMember.isMemberOfClub(registrationClub.asaName)) {
                errors << "Not registered with $registrationClub.name."
                return errors
            }
            if (!asaMember.isValidMembershipCategory()) {
                errors << "Invalid membership category: $asaMember.membershipCategory"
            }
            return errors
        }
        catch (ASAMemberDataRetrievalException e) {
            errors << "Not found in ASA membership check."
            return errors
        }
    }
}
