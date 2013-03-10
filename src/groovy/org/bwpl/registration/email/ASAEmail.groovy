package org.bwpl.registration.email

import org.bwpl.registration.Club
import org.bwpl.registration.User
import org.bwpl.registration.Registration
import groovy.text.SimpleTemplateEngine

class ASAEmail {

    private static final String asaEmailAddress = "renewals@swimming.org"

    User currentUser
    Club club
    List<Registration> invalidRegistrations
    String lineBreak = "\n"

    String getMailToLink() {

        lineBreak = "\n"
        return "mailto:${getTo(true)}?subject=${getSubject(true)}&body=${getBody(true, "\n")}"
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

    String getBody(boolean doEncode, String lineBreak) {

        this.lineBreak = lineBreak
        SimpleTemplateEngine templateEngine = new SimpleTemplateEngine()
        def binding = [clubName:club.name,
                       regSecName:User.registrationSecretary.name,
                       regSecEmail:User.registrationSecretary.email,
                       lineBreak:lineBreak]
        String body = templateEngine.createTemplate(bodyTemplate).make(binding).toString()
        body = "$body$lineBreak$lineBreak$registrations"

        if (doEncode) return encode(body)
        return body
    }

    private String getRegistrations() {

        StringBuilder sb = new StringBuilder()
        invalidRegistrations.each { sb << "$it.name ($it.asaNumber)$lineBreak" }
        return sb.toString().trim()
    }

    private static String encode(String str) {
        return URLEncoder.encode(str, "UTF-8")
    }

    private static String bodyTemplate =
        'Please provide the following membership details by return of email for the people listed below.${lineBreak}${lineBreak}' +
        '* Date registered with $clubName${lineBreak}' +
        '* Membership Category${lineBreak}${lineBreak}' +
        'Note I have checked the ASA Online Membership Check for these people and their details are either not available or ' +
        'have not been updated on the ASA system. Please copy $regSecName ($regSecEmail), ' +
        'the British Water Polo League Registration Secretary.'
}
