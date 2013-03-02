package org.bwpl.registration.validation

import org.bwpl.registration.Registration

class RegistrationStats {

    private final Collection<Registration> registrations

    RegistrationStats(Collection<Registration> registrations) {
        this.registrations = registrations
    }

    int getCount() {
        return registrations.size()
    }

    int getCountOfValid() {
        return registrations.count {it.statusAsEnum == Status.VALID}
    }

    int getCountOfInvalid() {
        return registrations.count {it.statusAsEnum == Status.INVALID}
    }
}
