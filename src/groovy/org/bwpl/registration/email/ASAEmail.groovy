package org.bwpl.registration.email

import groovy.text.SimpleTemplateEngine
import org.bwpl.registration.Club
import org.bwpl.registration.Registration
import org.bwpl.registration.User
import org.bwpl.registration.validation.Status

class ASAEmail {

    private static final String asaEmailAddress = "renewals@swimming.org"

    User currentUser
    Club club

    String getMailToLink() {
        return "mailto:${getTo(true)}?subject=${getSubject(true)}&body=${getBody(true, "\n", " ")}"
    }

    String getTo(boolean doEncode) {

        if (doEncode) return encode(asaEmailAddress)
        return asaEmailAddress
    }

    String getSubject(boolean doEncode) {

        String subject = "ASA Membership Check for ${club.name}"
        if (doEncode) return encode(subject)
        return subject
    }

    String getBody(boolean doEncode, String lineBreak, String space) {

        SimpleTemplateEngine templateEngine = new SimpleTemplateEngine()
        def binding = [clubName:club.name,
                       regSecName:"James Reddick",
                       regSecEmail:"reddicj@gmail.com",
                       clubSecName: currentUser.name,
                       lineBreak:lineBreak,
                       space: space]
        String body = templateEngine.createTemplate(bodyTemplate).make(binding).toString()
        if (!doEncode) {
            body = "$body$lineBreak$lineBreak${getRegistrations(lineBreak)}"
        }

        if (doEncode) return encode(body)
        return body
    }

    private String getRegistrations(String lineBreak) {

        List<Registration> invalidRegistrations = new ArrayList<Registration>(club.registrations)
        invalidRegistrations = invalidRegistrations.findAll {it.statusAsEnum == Status.INVALID}
        invalidRegistrations.sort {it.name}

        StringBuilder sb = new StringBuilder()
        invalidRegistrations.each { sb << "$it.name ($it.asaNumber)$lineBreak" }
        return sb.toString().trim()
    }

    private static String encode(String str) {
        return URLEncoder.encode(str, "UTF-8").replace("+", "%20")
    }

    private static String bodyTemplate =
        'Please provide the following membership information by return of email for the people listed below.${lineBreak}${lineBreak}' +
        '${space}${space}${space}1. Confirmation that they are currently registered with $clubName.${lineBreak}' +
        '${space}${space}${space}2. The date they were registered with $clubName.${lineBreak}' +
        '${space}${space}${space}3. Their membership category.${lineBreak}${lineBreak}' +
        'Note I have checked the ASA Online Membership Check for these people and their details are either not available or ' +
        'have not been updated on the ASA system.${lineBreak}${lineBreak}' +
        'Please copy $regSecName ($regSecEmail), the British Water Polo League Registration Secretary.${lineBreak}${lineBreak}' +
        'Regards,${lineBreak}' +
        '${clubSecName}${lineBreak}' +
        '$clubName${lineBreak}${lineBreak}' +
        '======================================================${lineBreak}' +
        'Members to check:'
}
