package org.bwpl.registration.validation

import org.bwpl.registration.Registration
import org.bwpl.registration.asa.ASAMemberDataRetrieval
import org.bwpl.registration.asa.ASAMemberDataRetrievalException
import org.bwpl.registration.asa.ASAMemberDataValidationException

class ValidationQueue {

    private Validator validator
    private List<Registration> registrations
    private Queue<Registration> queue
    private List<Registration> processed
    String asaMemberCheckError = ""

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
        if (!isASAMemberCheckOk()) {
            queue = new LinkedList<Registration>()
            return
        }

        Registration r = queue.remove()
        r = Registration.get(r.id)

        try {
            validator.validate(r)
        }
        catch (ASAMemberDataRetrievalException e) {
            asaMemberCheckError = e.message
            queue = new LinkedList<Registration>()
            return
        }
        catch (ASAMemberDataValidationException e) {
            asaMemberCheckError = e.message
            queue = new LinkedList<Registration>()
            return
        }
        processed << r
    }

    private boolean isASAMemberCheckOk() {

        ASAMemberDataRetrieval asaMemberDataRetrieval = new ASAMemberDataRetrieval()
        asaMemberCheckError = asaMemberDataRetrieval.getServiceError()
        return asaMemberCheckError.isEmpty()
    }

    boolean isEmpty() {
        return queue.isEmpty()
    }

    String toString() {

        String s = "Processed: $processedCount/$totalCount, Valid: $validCount, Invalid: $invalidCount"
        if (asaMemberCheckError.isEmpty()) {
            return s
        }
        else {
            return "$s. $asaMemberCheckError"
        }
    }
}
