package org.bwpl.registration.validation

import org.bwpl.registration.Registration

class ValidationQueue {

    private final Validator validator
    private final List<Registration> registrations
    private final Queue<Registration> queue
    private final List<Registration> processed

    ValidationQueue(final Validator validator, final Collection<Registration> registrations) {

        this.validator = validator
        this.registrations = new ArrayList<Registration>(registrations)
        queue = new LinkedList<Registration>(registrations)
        processed = new ArrayList<Registration>()
    }

    int getTotalCount() {
        return registrations.size()
    }

    int getProcessedCount() {
        return processed.size()
    }

    int getValidCount() {
        return processed.findAll{it.statusAsEnum == Status.VALID}.size()
    }

    int getInvalidCount() {
        return processed.findAll{it.statusAsEnum == Status.INVALID}.size()
    }

    void process() {

        if (queue.isEmpty()) return
        Registration r = queue.remove()
        r = Registration.get(r.id)
        validator.validate(r)
        processed << r
    }

    boolean isEmpty() {
        return queue.isEmpty()
    }

    String toString() {
        return "Processed: $processedCount/$totalCount, Valid: $validCount, Invalid: $invalidCount"
    }
}
